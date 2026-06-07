"""Tool surface for the FO Copilot `ticket` MCP adapter (8 tools).

Design rationale (see README.md and the architecture doc, §4.2):
- **Writes are atomic and orthogonal** — create / update / comment / transition /
  update_assignment are discrete actions the skills compose freely.
- **Reads are task-aligned, not raw CRUD** — `get_todo` (scan my plate),
  `get_state_context` (everything needed to act on one ticket, with a
  `format: concise|detailed` knob), `get_candidates` (the roster). This keeps a
  watch pass cheap and a single-ticket decision a single round-trip.
- Responses carry high-signal, human-readable fields (no opaque ids/UUIDs) and a
  one-line `summary`; errors carry an actionable `hint`.

This module has no dependency on the `mcp` package, so its logic is unit-testable
in isolation (see test_tools.py). server.py wires it to stdio.
"""

from __future__ import annotations

import json
from datetime import date
from typing import Any

from store import COPILOT_OWNER, TicketStore, available_transitions

# Optional business idempotency key on every write. goosed handles
# request/session/schedule-level execution correlation but not "is this the same
# business action on the same ticket"; the mock accepts and ignores it, the
# contract keeps the extension point (see §4.2).
_IDEMPOTENCY = {
    "operationId": {"type": "string", "description": "Optional business idempotency key for this write; safe to omit."},
    "followupId": {"type": "string", "description": "Optional follow-up correlation id; safe to omit."},
}

_FORMAT = {
    "type": "string",
    "enum": ["concise", "detailed"],
    "description": "concise = summary view (default); detailed = full context incl. timeline, comments, and available transitions.",
}

_store = TicketStore()


class ToolError(Exception):
    def __init__(self, code: str, message: str, hint: str = "") -> None:
        super().__init__(message)
        self.code = code
        self.message = message
        self.hint = hint


# --- response shaping -----------------------------------------------------

def _ok(summary: str, data: Any) -> str:
    return json.dumps({"ok": True, "summary": summary, "data": data}, ensure_ascii=False, indent=2)


def _err(code: str, message: str, hint: str = "") -> str:
    return json.dumps({"ok": False, "error": {"code": code, "message": message, "hint": hint}}, ensure_ascii=False, indent=2)


def _ticket_not_found(ticket_id: str) -> None:
    raise ToolError("TICKET_NOT_FOUND", f"No ticket {ticket_id}.", "Call get_todo to list current ticket ids.")


def _summary_view(t: dict[str, Any]) -> dict[str, Any]:
    """High-signal concise view of a ticket — enough to triage without a deep read."""
    one_line = (t.get("description") or "").splitlines()
    return {
        "id": t["id"],
        "type": t["type"],
        "title": t["title"],
        "status": t["status"],
        "priority": t["priority"],
        "owner": t.get("owner"),
        "category": t.get("category"),
        "system": t.get("system"),
        "slaResolveDueAt": (t.get("sla") or {}).get("resolveDueAt"),
        "slaResponseDueAt": (t.get("sla") or {}).get("responseDueAt"),
        "updatedAt": t.get("updatedAt"),
        "oneLine": one_line[0] if one_line else "",
    }


def _detailed_view(t: dict[str, Any], recent_comments: int = 10) -> dict[str, Any]:
    view = _summary_view(t)
    comments = [e for e in t.get("timeline", []) if e.get("type") == "comment"]
    view.update({
        "description": t.get("description", ""),
        "contact": t.get("contact"),
        "createdAt": t.get("createdAt"),
        "sla": t.get("sla", {}),
        "availableTransitions": available_transitions(t["status"]),
        "recentComments": comments[-recent_comments:],
        "timeline": t.get("timeline", []),
    })
    return view


# --- tool handlers --------------------------------------------------------

def _t_get_todo(args: dict[str, Any]) -> str:
    todos = _store.list_todo()
    return _ok(
        f"{len(todos)} ticket(s) awaiting my follow-up.",
        {"tickets": [_summary_view(t) for t in todos]},
    )


def _t_get_state_context(args: dict[str, Any]) -> str:
    ticket_id = args.get("ticketId")
    if not ticket_id:
        raise ToolError("MISSING_ARG", "ticketId is required.", "Pass the ticket id, e.g. INC-1042.")
    t = _store.get(ticket_id)
    if t is None:
        _ticket_not_found(ticket_id)
    fmt = args.get("format", "concise")
    view = _detailed_view(t) if fmt == "detailed" else _summary_view(t)
    return _ok(f"{t['id']} [{t['status']}/{t['priority']}] {t['title']}", view)


def _t_get_daily_brief(args: dict[str, Any]) -> str:
    target = None
    raw_date = args.get("date")
    if raw_date:
        try:
            target = date.fromisoformat(raw_date)
        except ValueError:
            raise ToolError("INVALID_ARG", "date must be an ISO date (YYYY-MM-DD).",
                            "Omit date for yesterday, or pass e.g. 2026-06-06.")
    brief = _store.daily_brief(target)
    fb = f" (no activity on {brief['fallbackFrom']}; rolled back)" if brief.get("fallbackFrom") else ""
    summary = (
        f"Daily brief {brief['date']}{fb}: {brief['opened']} opened, {brief['resolved']} resolved, "
        f"{len(brief['awaitingDecision'])} awaiting your decision, {len(brief['atRisk'])} at SLA risk."
    )
    return _ok(summary, brief)


def _t_get_candidates(args: dict[str, Any]) -> str:
    cands = _store.candidates()
    ticket_id = args.get("ticketId")
    note = "Full team roster with skills and current load."
    if ticket_id:
        t = _store.get(ticket_id)
        if t is None:
            _ticket_not_found(ticket_id)
        wanted = {x for x in (t.get("category"), t.get("system")) if x}
        scored = []
        for c in cands:
            matched = sorted(wanted.intersection(set(c.get("skills", [])) | set(c.get("responsibilities", []))))
            scored.append({**c, "matchedSkills": matched})
        # surface the best matches first; load/shift are left for the skill to weigh
        cands = sorted(scored, key=lambda c: (-len(c["matchedSkills"]), c.get("openTickets", 0)))
        note = f"Roster ranked by skill match for {t['id']} (category/system: {', '.join(sorted(wanted)) or 'n/a'})."
    return _ok(note, {"candidates": cands})


def _t_create(args: dict[str, Any]) -> str:
    if not args.get("title"):
        raise ToolError("MISSING_ARG", "title is required.", "Provide a short ticket title.")
    ticket = _store.create(args)
    return _ok(f"Created {ticket['id']}: {ticket['title']}", _summary_view(ticket))


def _t_update(args: dict[str, Any]) -> str:
    ticket_id = args.get("ticketId")
    if not ticket_id:
        raise ToolError("MISSING_ARG", "ticketId is required.", "Pass the ticket id.")
    fields = args.get("fields")
    if fields is None or fields == {}:
        raise ToolError("MISSING_ARG", "fields is required.", "Pass a fields object, e.g. {\"priority\": \"P2\"}.")
    if not isinstance(fields, dict):
        # Validate the type at the tool boundary so a malformed argument becomes
        # a clear business error instead of an internal AttributeError later
        # (e.g. fields="oops" reaching fields.items()).
        raise ToolError("INVALID_ARG", "fields must be an object (map of fields to change).",
                        "Pass a fields object, e.g. {\"priority\": \"P2\"}.")
    t = _store.update_fields(ticket_id, fields)
    if t is None:
        _ticket_not_found(ticket_id)
    return _ok(f"Updated {ticket_id}.", _summary_view(t))


def _t_comment(args: dict[str, Any]) -> str:
    ticket_id = args.get("ticketId")
    body = args.get("body")
    if not ticket_id or not body:
        raise ToolError("MISSING_ARG", "ticketId and body are required.", "Pass the ticket id and comment text.")
    t = _store.add_comment(ticket_id, body, args.get("visibility", "internal"))
    if t is None:
        _ticket_not_found(ticket_id)
    return _ok(f"Comment added to {ticket_id}.", {"id": ticket_id})


def _t_transition(args: dict[str, Any]) -> str:
    ticket_id = args.get("ticketId")
    target = args.get("transitionId") or args.get("to")
    if not ticket_id or not target:
        raise ToolError("MISSING_ARG", "ticketId and transitionId (or to) are required.",
                        "Read get_state_context(detailed) for availableTransitions, then pass a transition id.")
    result = _store.transition(ticket_id, target, args.get("reason"))
    if result["ok"]:
        return _ok(f"{ticket_id}: {result['from']} -> {result['to']}.", _summary_view(result["ticket"]))
    if result["code"] == "TICKET_NOT_FOUND":
        _ticket_not_found(ticket_id)
    ids = ", ".join(f"{tr['id']}->{tr['to']}" for tr in result["available"]) or "none (terminal state)"
    raise ToolError(
        "ILLEGAL_TRANSITION",
        f"'{target}' is not reachable from '{result['from']}'.",
        f"Available from {result['from']}: {ids}.",
    )


def _t_update_assignment(args: dict[str, Any]) -> str:
    ticket_id = args.get("ticketId")
    owner = args.get("owner")
    if not ticket_id or not owner:
        raise ToolError("MISSING_ARG", "ticketId and owner are required.",
                        "Pass the ticket id and the new owner id (call get_candidates for valid owners).")
    t = _store.reassign(ticket_id, owner, args.get("reason"))
    if t is None:
        _ticket_not_found(ticket_id)
    handed_off = " (ball handed off)" if owner != COPILOT_OWNER else ""
    return _ok(f"{ticket_id} reassigned to {owner}{handed_off}.", _summary_view(t))


_HANDLERS = {
    "get_todo": _t_get_todo,
    "get_daily_brief": _t_get_daily_brief,
    "get_state_context": _t_get_state_context,
    "get_candidates": _t_get_candidates,
    "create": _t_create,
    "update": _t_update,
    "comment": _t_comment,
    "transition": _t_transition,
    "update_assignment": _t_update_assignment,
}

# --- tool schemas (advertised over MCP) -----------------------------------

TOOLS: list[dict[str, Any]] = [
    {
        "name": "get_todo",
        "description": "Proactive-watch entry point: list the tickets awaiting my (copilot's) follow-up, as concise summaries. Start a watch pass here; most tickets can be triaged from this view without a deep read.",
        "inputSchema": {"type": "object", "properties": {}, "additionalProperties": False},
    },
    {
        "name": "get_daily_brief",
        "description": "Previous-day handling summary for the FO lead's morning brief: counts of tickets opened / resolved / handled, a priority breakdown, the currently-open important (P1/P2) backlog, the items awaiting the FO lead's decision (with the specific question for each), and what is at SLA risk. Defaults to yesterday and rolls back to the most recent active day if yesterday was quiet (never empty). Use for the scheduled daily-brief task or when asked 'how did yesterday go'.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "date": {"type": "string", "description": "Optional ISO date (YYYY-MM-DD) to report on; defaults to yesterday."},
            },
            "additionalProperties": False,
        },
    },
    {
        "name": "get_state_context",
        "description": "Read everything needed to act on one ticket. concise = summary view; detailed = full fields + timeline + recent comments + available transitions (with preconditions and needsConfirm). Use detailed only when actually advancing or closing a ticket.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "ticketId": {"type": "string", "description": "Ticket id, e.g. INC-1042."},
                "format": _FORMAT,
            },
            "required": ["ticketId"],
        },
    },
    {
        "name": "get_candidates",
        "description": "List the team roster with skills, on-shift status, current load (open ticket count), and responsibility boundaries. Pass a ticketId to rank candidates by skill match for that ticket. Use when choosing or changing an owner.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "ticketId": {"type": "string", "description": "Optional ticket id to rank candidates against."},
            },
        },
    },
    {
        "name": "create",
        "description": "Create a ticket. Provide at least a title; type/priority/category/system/contact/owner/description are optional.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "title": {"type": "string"},
                "description": {"type": "string"},
                "type": {"type": "string", "enum": ["Incident", "Problem", "Change", "ServiceRequest"]},
                "priority": {"type": "string", "enum": ["P1", "P2", "P3", "P4"]},
                "category": {"type": "string"},
                "system": {"type": "string"},
                "contact": {"type": "string"},
                "owner": {"type": "string"},
                **_IDEMPOTENCY,
            },
            "required": ["title"],
        },
    },
    {
        "name": "update",
        "description": "Correct ticket fields (priority, category, system, title, description, contact, sla). Does not change state — use transition for that.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "ticketId": {"type": "string"},
                "fields": {"type": "object", "description": "Map of fields to change, e.g. {\"priority\": \"P2\", \"category\": \"network\"}."},
                **_IDEMPOTENCY,
            },
            "required": ["ticketId", "fields"],
        },
    },
    {
        "name": "comment",
        "description": "Add a comment to a ticket's timeline. Use this to record the reason for any action you take (audit trail).",
        "inputSchema": {
            "type": "object",
            "properties": {
                "ticketId": {"type": "string"},
                "body": {"type": "string"},
                "visibility": {"type": "string", "enum": ["internal", "public"], "description": "Defaults to internal."},
                **_IDEMPOTENCY,
            },
            "required": ["ticketId", "body"],
        },
    },
    {
        "name": "transition",
        "description": "Move a ticket to a new state. Pass a transitionId (preferred) or a target state via `to`; read get_state_context(detailed).availableTransitions first. Validates legality only — authorization (e.g. P1/P2 needs confirmation) is the caller's responsibility.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "ticketId": {"type": "string"},
                "transitionId": {"type": "string", "description": "Transition id from availableTransitions, e.g. resolve."},
                "to": {"type": "string", "description": "Alternative to transitionId: the target state."},
                "reason": {"type": "string", "description": "Why this transition is being made (recorded on the timeline)."},
                **_IDEMPOTENCY,
            },
            "required": ["ticketId"],
        },
    },
    {
        "name": "update_assignment",
        "description": "Reassign a ticket to a new owner. Reassigning away from copilot hands the follow-up ball off (the ticket leaves get_todo). Call get_candidates to pick a valid owner.",
        "inputSchema": {
            "type": "object",
            "properties": {
                "ticketId": {"type": "string"},
                "owner": {"type": "string", "description": "New owner id, e.g. li.wei."},
                "reason": {"type": "string", "description": "Why it is (re)assigned (recorded on the timeline)."},
                **_IDEMPOTENCY,
            },
            "required": ["ticketId", "owner"],
        },
    },
]


async def dispatch(name: str, args: dict[str, Any]) -> str:
    handler = _HANDLERS.get(name)
    if handler is None:
        return _err("UNKNOWN_TOOL", f"No such tool: {name}.", f"Known tools: {', '.join(_HANDLERS)}.")
    try:
        return handler(args)
    except ToolError as exc:
        return _err(exc.code, exc.message, exc.hint)
    except Exception:
        # Convergence point: a non-ToolError is an internal fault. Never surface
        # its raw text across the tool boundary (G.ERR.08); return a generic,
        # actionable business error instead.
        return _err(
            "TOOL_EXECUTION_FAILED",
            "The tool failed to execute.",
            "Check the arguments and retry; if it persists, inspect the server log.",
        )
