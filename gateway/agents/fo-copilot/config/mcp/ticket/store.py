"""File-backed mock ticket store for the FO Copilot `ticket` MCP adapter.

This is a deliberately small stand-in for a real ticketing system: it holds a
handful of seed tickets and a team roster in one JSON file, and implements just
enough of a state machine for the watch / advance / close workflows to really
pull tickets, change fields, comment, transition state, and reassign — so the
end-to-end loop can be demonstrated and tested without an external system.

The real adapter swaps in behind the exact same tool surface (see tools.py);
nothing here leaks into the tool contract beyond the data shapes documented in
docs/architecture/fo-copilot-proactive-assistant.md (§4.2).
"""

from __future__ import annotations

import json
import os
import sys
import tempfile
from datetime import date, datetime, timedelta, timezone
from pathlib import Path
from typing import Any

# --- identity -------------------------------------------------------------
# Who "I" am from the ticketing system's point of view. `get_todo` returns the
# tickets whose follow-up ball is in this owner's court.
COPILOT_OWNER = "fo-copilot"

# --- state machine --------------------------------------------------------
# Generic across ticket types (Incident / Problem / Change / ServiceRequest).
# `preconditions` are advisory data handed to the agent for judgment; the store
# only enforces *structural* legality (does this transition exist from the
# current state). `needsConfirm` is likewise data — the skill, not the adapter,
# decides whether to pause for a human (see §4.1 authorization).
TRANSITIONS: dict[str, list[dict[str, Any]]] = {
    "new": [
        {"id": "start_triage", "to": "triage", "preconditions": [], "needsConfirm": False},
        {"id": "cancel", "to": "cancelled", "preconditions": ["Confirm the request is invalid or withdrawn"], "needsConfirm": True},
    ],
    "triage": [
        {"id": "begin_work", "to": "in_progress", "preconditions": ["An owner is assigned"], "needsConfirm": False},
        {"id": "cancel", "to": "cancelled", "preconditions": ["Confirm the request is invalid or withdrawn"], "needsConfirm": True},
    ],
    "in_progress": [
        {"id": "await_external", "to": "pending", "preconditions": ["State what you are waiting on"], "needsConfirm": False},
        {"id": "resolve", "to": "resolved", "preconditions": ["A fix or workaround is in place"], "needsConfirm": True},
    ],
    "pending": [
        {"id": "resume", "to": "in_progress", "preconditions": [], "needsConfirm": False},
    ],
    "resolved": [
        {"id": "close", "to": "closed", "preconditions": ["A resolution summary has been recorded"], "needsConfirm": True},
        {"id": "reopen", "to": "in_progress", "preconditions": ["Resolution rejected or recurrence observed"], "needsConfirm": False},
    ],
    "closed": [],
    "cancelled": [],
}

ALL_STATES = list(TRANSITIONS.keys())


def available_transitions(status: str) -> list[dict[str, Any]]:
    """Transitions legally reachable from `status` (data for the agent to judge)."""
    return [dict(t) for t in TRANSITIONS.get(status, [])]


def _now() -> datetime:
    return datetime.now(timezone.utc)


def _iso(dt: datetime) -> str:
    return dt.replace(microsecond=0).isoformat().replace("+00:00", "Z")


def _build_seed() -> dict[str, Any]:
    """Seed a small, varied board. SLA fields are absolute timestamps relative
    to seed time so a watch pass sees a realistic mix of overdue / near / ample,
    and judges 'stuck or not' itself against current_date_time (§4.2)."""
    now = _now()

    def evt(minutes_ago: int, author: str, kind: str, summary: str, visibility: str = "internal") -> dict[str, Any]:
        return {
            "time": _iso(now - timedelta(minutes=minutes_ago)),
            "author": author,
            "type": kind,
            "summary": summary,
            "visibility": visibility,
        }

    # Yesterday-anchored timestamp (always lands on the previous calendar day in UTC,
    # regardless of the current hour) so the daily-brief always has a populated window.
    def y_at(hour: int, minute: int = 0) -> str:
        return _iso((now - timedelta(days=1)).replace(hour=hour, minute=minute, second=0, microsecond=0))

    def y_evt(hour: int, author: str, kind: str, summary: str, visibility: str = "internal") -> dict[str, Any]:
        return {"time": y_at(hour), "author": author, "type": kind, "summary": summary, "visibility": visibility}

    tickets = [
        {
            "id": "INC-1042",
            "type": "Incident",
            "title": "Checkout service 5xx spike for EU customers",
            "description": "Error rate on the checkout API jumped to ~12% for the EU region after the 14:00 deploy.",
            "status": "in_progress",
            "priority": "P1",
            "owner": COPILOT_OWNER,
            "category": "payments",
            "system": "checkout-api",
            "contact": "ops-oncall",
            "todo": True,
            "createdAt": _iso(now - timedelta(hours=3)),
            "updatedAt": _iso(now - timedelta(hours=2, minutes=10)),
            "sla": {
                "responseDueAt": _iso(now - timedelta(hours=2, minutes=30)),
                "resolveDueAt": _iso(now - timedelta(minutes=20)),  # overdue
            },
            "timeline": [
                evt(180, "monitoring", "created", "Auto-raised from error-rate alert."),
                evt(170, COPILOT_OWNER, "comment", "Correlated with deploy build #4821; paged the on-call."),
                evt(130, "li.wei", "comment", "Rolling back build #4821 to mitigate.", "internal"),
            ],
        },
        {
            "id": "REQ-2087",
            "type": "ServiceRequest",
            "title": "Provision read replica for analytics DB",
            "description": "Analytics team needs a read replica to offload reporting queries.",
            "status": "triage",
            "priority": "P3",
            "owner": COPILOT_OWNER,
            "category": "database",
            "system": "analytics-db",
            "contact": "data-platform",
            "todo": True,
            "createdAt": _iso(now - timedelta(days=1, hours=2)),
            "updatedAt": _iso(now - timedelta(hours=20)),
            "sla": {
                "responseDueAt": _iso(now + timedelta(hours=4)),
                "resolveDueAt": _iso(now + timedelta(days=2)),  # ample
            },
            "timeline": [
                evt(1560, "wang.fang", "created", "Submitted via the service catalog."),
            ],
        },
        {
            "id": "CHG-3310",
            "type": "Change",
            "title": "Upgrade ingress controller to 1.9",
            "description": "Planned upgrade of the ingress controller during the next maintenance window.",
            "status": "pending",
            "priority": "P2",
            "owner": COPILOT_OWNER,
            "category": "platform",
            "system": "ingress",
            "contact": "platform-team",
            "todo": True,
            # Explicitly flagged as needing the FO lead's call (the daily-brief also
            # *derives* awaiting-decision items from overdue P1/P2 — see daily_brief()).
            "awaitingDecision": True,
            "decisionNeeded": "CAB approval is stalled; decide whether to push the ingress 1.9 upgrade to next week's window or expedite an emergency change.",
            "createdAt": _iso(now - timedelta(days=2)),
            "updatedAt": _iso(now - timedelta(hours=6)),
            "sla": {
                "responseDueAt": _iso(now - timedelta(days=1)),
                "resolveDueAt": _iso(now + timedelta(hours=8)),  # near
            },
            "timeline": [
                evt(2880, "zhang.lei", "created", "Change request raised."),
                evt(400, COPILOT_OWNER, "comment", "Waiting on change-advisory-board approval before scheduling."),
                evt(360, COPILOT_OWNER, "transition", "in_progress -> pending: awaiting CAB approval."),
            ],
        },
        {
            "id": "INC-1009",
            "type": "Incident",
            "title": "Nightly backup job failed once",
            "description": "Backup job for the billing DB failed on a transient lock; retried green.",
            "status": "resolved",
            "priority": "P4",
            "owner": COPILOT_OWNER,
            "category": "database",
            "system": "billing-db",
            "contact": "dba",
            "todo": True,
            "createdAt": _iso(now - timedelta(days=1)),
            "updatedAt": _iso(now - timedelta(hours=12)),
            "sla": {
                "responseDueAt": _iso(now - timedelta(hours=20)),
                "resolveDueAt": _iso(now - timedelta(hours=14)),
            },
            "timeline": [
                evt(1440, "monitoring", "created", "Backup job exit code 1."),
                evt(800, COPILOT_OWNER, "comment", "Retry succeeded; root cause was a transient row lock during VACUUM."),
                evt(720, COPILOT_OWNER, "transition", "in_progress -> resolved: retry green, no data loss."),
            ],
        },
        {
            "id": "INC-0998",
            "type": "Incident",
            "title": "VPN latency reported by remote staff",
            "description": "A few remote staff report intermittent VPN latency in the afternoon.",
            "status": "in_progress",
            "priority": "P3",
            "owner": "chen.hao",
            "category": "network",
            "system": "vpn",
            "contact": "it-helpdesk",
            "todo": False,  # ball is with chen.hao, not copilot
            "createdAt": _iso(now - timedelta(days=1, hours=4)),
            "updatedAt": _iso(now - timedelta(hours=3)),
            "sla": {
                "responseDueAt": _iso(now - timedelta(days=1)),
                "resolveDueAt": _iso(now + timedelta(days=1)),
            },
            "timeline": [
                evt(1680, "it-helpdesk", "created", "Multiple latency reports."),
                evt(180, "chen.hao", "comment", "Investigating the afternoon traffic peak on gateway-2."),
            ],
        },
        # --- previous-day activity (drives get_daily_brief) -------------------
        # Two opened-and-resolved-yesterday plus one opened-yesterday-still-open
        # carry-over, so the brief always reports a real prior-day window.
        {
            "id": "INC-1051",
            "type": "Incident",
            "title": "Payment gateway timeouts during EU checkout",
            "description": "Gateway timeouts caused intermittent checkout failures for EU customers around midday.",
            "status": "resolved",
            "priority": "P1",
            "owner": COPILOT_OWNER,
            "category": "payments",
            "system": "checkout-api",
            "contact": "ops-oncall",
            "todo": False,
            "createdAt": y_at(8, 30),
            "updatedAt": y_at(11, 10),
            "sla": {"responseDueAt": y_at(9, 0), "resolveDueAt": y_at(12, 0)},
            "timeline": [
                y_evt(8, "monitoring", "created", "Auto-raised from gateway timeout alert."),
                y_evt(9, COPILOT_OWNER, "comment", "Failed over to the secondary payment route; errors dropping."),
                y_evt(11, COPILOT_OWNER, "transition", "in_progress -> resolved: failover held, error rate back to baseline."),
            ],
        },
        {
            "id": "INC-1047",
            "type": "Incident",
            "title": "Auth service elevated 401 errors",
            "description": "A bad cache config caused a burst of spurious 401s on the auth service.",
            "status": "resolved",
            "priority": "P2",
            "owner": COPILOT_OWNER,
            "category": "identity",
            "system": "auth-service",
            "contact": "security-oncall",
            "todo": False,
            "createdAt": y_at(13, 15),
            "updatedAt": y_at(15, 40),
            "sla": {"responseDueAt": y_at(14, 0), "resolveDueAt": y_at(17, 0)},
            "timeline": [
                y_evt(13, "monitoring", "created", "401 rate above threshold."),
                y_evt(14, COPILOT_OWNER, "comment", "Reverted the auth cache TTL change from the morning deploy."),
                y_evt(15, COPILOT_OWNER, "transition", "in_progress -> resolved: 401 rate normal after revert."),
            ],
        },
        {
            "id": "REQ-2090",
            "type": "ServiceRequest",
            "title": "Grant analytics team read access to staging",
            "description": "Analytics team requested read-only access to the staging dataset for a one-off audit.",
            "status": "in_progress",
            "priority": "P3",
            "owner": "wang.fang",
            "category": "database",
            "system": "analytics-db",
            "contact": "data-platform",
            "todo": False,  # opened yesterday, still being worked by wang.fang — a carry-over
            "createdAt": y_at(16, 45),
            "updatedAt": y_at(17, 30),
            "sla": {"responseDueAt": y_at(18, 0), "resolveDueAt": _iso(now + timedelta(hours=6))},
            "timeline": [
                y_evt(16, "data-platform", "created", "Submitted via the service catalog."),
                y_evt(17, "wang.fang", "comment", "Preparing a scoped read role; will grant after review."),
            ],
        },
    ]

    candidates = [
        {"id": "li.wei", "name": "Li Wei", "skills": ["payments", "checkout-api", "java"], "onShift": True, "openTickets": 2, "responsibilities": ["payments", "checkout"]},
        {"id": "wang.fang", "name": "Wang Fang", "skills": ["database", "analytics-db", "sql"], "onShift": True, "openTickets": 1, "responsibilities": ["database"]},
        {"id": "zhang.lei", "name": "Zhang Lei", "skills": ["platform", "ingress", "kubernetes"], "onShift": False, "openTickets": 4, "responsibilities": ["platform"]},
        {"id": "chen.hao", "name": "Chen Hao", "skills": ["network", "vpn", "firewall"], "onShift": True, "openTickets": 3, "responsibilities": ["network"]},
        {"id": "sun.li", "name": "Sun Li", "skills": ["database", "billing-db", "backup"], "onShift": True, "openTickets": 0, "responsibilities": ["database", "billing"]},
    ]

    return {"tickets": tickets, "candidates": candidates, "seq": 9000}


# --- safe data-path resolution -------------------------------------------
# The store/log file paths may come from the environment (TICKET_MOCK_STORE /
# TICKET_MCP_LOG). os.environ is external input, so any path built from it must
# be normalized and confined to a known data area before it is used to create
# files (G.FIO.02 path manipulation), and the files are created with
# owner-only permissions (G.FIO.01).
_MODULE_DIR = Path(__file__).resolve().parent
_DEFAULT_STORE = _MODULE_DIR / ".data" / "store.json"


def _allowed_bases() -> list[Path]:
    """Directories a data file is allowed to live under: the adapter's own dir,
    the per-user runtime CWD (= GOOSE_PATH_ROOT), and the system temp dir."""
    bases: list[Path] = []
    for b in (_MODULE_DIR, Path.cwd(), Path(tempfile.gettempdir())):
        rb = b.resolve()
        if rb not in bases:
            bases.append(rb)
    return bases


def resolve_data_path(raw: str, *, default: Path) -> Path:
    """Normalize an externally-supplied path and confine it to an allowed base.

    A relative path resolves against the CWD (the per-user runtime dir). The
    result is realpath-normalized; if it escapes every allowed base (e.g. via
    ``..`` or an absolute path into a sensitive location) the built-in default
    is used instead and the rejection is noted on stderr."""
    candidate = Path(raw).expanduser()
    if not candidate.is_absolute():
        candidate = Path.cwd() / candidate
    candidate = candidate.resolve()
    if any(candidate == base or candidate.is_relative_to(base) for base in _allowed_bases()):
        return candidate
    sys.stderr.write(
        f"[ticket-mcp] refusing out-of-bounds store/log path {candidate}; using default {default}\n"
    )
    return default.resolve()


class TicketStore:
    """Whole-file load/save JSON store. Simple and synchronous — fine for a mock."""

    def __init__(self, path: str | None = None) -> None:
        raw = path or os.environ.get("TICKET_MOCK_STORE")
        # raw may carry external (env) input; confine it. Absent override → the
        # built-in default under the adapter's own data dir.
        self.path = resolve_data_path(raw, default=_DEFAULT_STORE) if raw else _DEFAULT_STORE
        self._ensure_seeded()

    def _ensure_seeded(self) -> None:
        if self.path.exists():
            return
        self.path.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
        self._save(_build_seed())

    def _load(self) -> dict[str, Any]:
        with self.path.open("r", encoding="utf-8") as fh:
            return json.load(fh)

    def _save(self, data: dict[str, Any]) -> None:
        tmp = self.path.with_suffix(self.path.suffix + ".tmp")
        # Create the temp file with owner-only permissions (G.FIO.01) before
        # writing, then atomically swap it into place.
        fd = os.open(tmp, os.O_WRONLY | os.O_CREAT | os.O_TRUNC, 0o600)
        with os.fdopen(fd, "w", encoding="utf-8") as fh:
            json.dump(data, fh, ensure_ascii=False, indent=2)
        tmp.replace(self.path)

    # --- reads -----------------------------------------------------------
    def list_todo(self) -> list[dict[str, Any]]:
        return [t for t in self._load()["tickets"] if t.get("todo")]

    def get(self, ticket_id: str) -> dict[str, Any] | None:
        for t in self._load()["tickets"]:
            if t["id"] == ticket_id:
                return t
        return None

    def candidates(self) -> list[dict[str, Any]]:
        return self._load()["candidates"]

    # States that still represent live work (not resolved/closed/cancelled) — i.e.
    # tickets that can still breach their resolve SLA or await a decision.
    _OPEN_STATES = {"new", "triage", "in_progress", "pending"}

    @staticmethod
    def _date_of(iso: str) -> date | None:
        if not iso:
            return None
        try:
            return datetime.fromisoformat(iso.replace("Z", "+00:00")).date()
        except ValueError:
            return None

    @classmethod
    def _resolved_on(cls, t: dict[str, Any], d: date) -> bool:
        """True if the ticket was moved to resolved/closed on date `d` (per timeline)."""
        for e in t.get("timeline", []):
            if e.get("type") != "transition":
                continue
            summary = e.get("summary", "")
            if ("-> resolved" in summary or "-> closed" in summary) and cls._date_of(e.get("time", "")) == d:
                return True
        return False

    @classmethod
    def _handled_on(cls, t: dict[str, Any], d: date) -> bool:
        """True if the copilot took at least one action on the ticket on date `d`."""
        return any(
            e.get("author") == COPILOT_OWNER and cls._date_of(e.get("time", "")) == d
            for e in t.get("timeline", [])
        )

    def _latest_active_date(self, tickets: list[dict[str, Any]]) -> date | None:
        """Most recent calendar day on which any ticket was opened or resolved."""
        days: set[date] = set()
        for t in tickets:
            opened = self._date_of(t.get("createdAt", ""))
            if opened is not None:
                days.add(opened)
            for e in t.get("timeline", []):
                if e.get("type") == "transition":
                    summary = e.get("summary", "")
                    if "-> resolved" in summary or "-> closed" in summary:
                        d = self._date_of(e.get("time", ""))
                        if d is not None:
                            days.add(d)
        return max(days) if days else None

    def _brief_for(self, tickets: list[dict[str, Any]], d: date) -> dict[str, Any]:
        opened = [t for t in tickets if self._date_of(t.get("createdAt", "")) == d]
        resolved = [t for t in tickets if self._resolved_on(t, d)]
        handled = [t for t in tickets if self._handled_on(t, d)]
        by_priority = {p: sum(1 for t in opened if t.get("priority") == p) for p in ("P1", "P2", "P3", "P4")}
        return {
            "date": d.isoformat(),
            "opened": len(opened),
            "resolved": len(resolved),
            "handled": len(handled),
            "byPriority": by_priority,
            "openedTickets": [self._brief_line(t) for t in opened],
            "resolvedTickets": [self._brief_line(t) for t in resolved],
        }

    @staticmethod
    def _brief_line(t: dict[str, Any]) -> dict[str, Any]:
        return {"id": t["id"], "title": t["title"], "priority": t.get("priority"), "status": t.get("status")}

    def daily_brief(self, target_date: date | None = None) -> dict[str, Any]:
        """Mock previous-day handling summary for the FO lead's morning brief.

        Defaults to yesterday; if that day saw no opened/resolved activity, rolls
        back to the most recent active day so the brief is never empty (`fallbackFrom`
        records the originally requested date). Counts are date-scoped, but the
        backlog views (`importantOpen`, `awaitingDecision`, `atRisk`) reflect *current*
        state — that is what the FO lead needs to decide on today, regardless of when
        each ticket was opened."""
        data = self._load()
        tickets = data["tickets"]
        if target_date is None:
            target_date = (_now() - timedelta(days=1)).date()

        brief = self._brief_for(tickets, target_date)
        if brief["opened"] == 0 and brief["resolved"] == 0:
            latest = self._latest_active_date(tickets)
            if latest is not None and latest != target_date:
                brief = self._brief_for(tickets, latest)
                brief["fallbackFrom"] = target_date.isoformat()

        now = _now()
        important_open = {
            "P1": sum(1 for t in tickets if t["status"] in self._OPEN_STATES and t.get("priority") == "P1"),
            "P2": sum(1 for t in tickets if t["status"] in self._OPEN_STATES and t.get("priority") == "P2"),
        }

        awaiting: list[dict[str, Any]] = []
        at_risk: list[dict[str, Any]] = []
        for t in tickets:
            status = t.get("status")
            priority = t.get("priority")
            resolve_due = (t.get("sla") or {}).get("resolveDueAt")
            overdue = bool(resolve_due) and (self._date_of(resolve_due) is not None) and resolve_due < _iso(now)
            open_work = status in self._OPEN_STATES

            # Explicit human flag awaits a decision regardless of state; the derived
            # rule only fires on still-open work (a resolved ticket past its SLA is
            # done, not a pending decision).
            explicit = t.get("awaitingDecision") is True
            derived = open_work and priority in ("P1", "P2") and overdue
            if explicit or derived:
                awaiting.append({
                    "id": t["id"], "title": t["title"], "priority": priority, "status": status,
                    "question": t.get("decisionNeeded")
                    or f"{priority} is past its resolve SLA and still {status} — approve escalation / extra resources?",
                })

            if status in self._OPEN_STATES and priority in ("P1", "P2") and overdue:
                at_risk.append({
                    "id": t["id"], "title": t["title"], "priority": priority,
                    "status": status, "resolveDueAt": resolve_due,
                })

        brief["importantOpen"] = important_open
        brief["awaitingDecision"] = awaiting
        brief["atRisk"] = at_risk
        return brief

    # --- writes ----------------------------------------------------------
    def create(self, fields: dict[str, Any]) -> dict[str, Any]:
        data = self._load()
        data["seq"] += 1
        prefix = {
            "Incident": "INC", "Problem": "PRB", "Change": "CHG", "ServiceRequest": "REQ",
        }.get(fields.get("type", "Incident"), "TIC")
        now = _iso(_now())
        ticket = {
            "id": f"{prefix}-{data['seq']}",
            "type": fields.get("type", "Incident"),
            "title": fields["title"],
            "description": fields.get("description", ""),
            "status": "new",
            "priority": fields.get("priority", "P3"),
            "owner": fields.get("owner", COPILOT_OWNER),
            "category": fields.get("category"),
            "system": fields.get("system"),
            "contact": fields.get("contact"),
            "todo": fields.get("owner", COPILOT_OWNER) == COPILOT_OWNER,
            "createdAt": now,
            "updatedAt": now,
            "sla": fields.get("sla", {}),
            "timeline": [{"time": now, "author": COPILOT_OWNER, "type": "created", "summary": "Ticket created.", "visibility": "internal"}],
        }
        data["tickets"].append(ticket)
        self._save(data)
        return ticket

    def _mutate(self, ticket_id: str, fn) -> dict[str, Any] | None:
        data = self._load()
        for t in data["tickets"]:
            if t["id"] == ticket_id:
                fn(t)
                t["updatedAt"] = _iso(_now())
                self._save(data)
                return t
        return None

    _EDITABLE_FIELDS = {"title", "description", "priority", "category", "system", "contact", "sla"}

    def update_fields(self, ticket_id: str, fields: dict[str, Any]) -> dict[str, Any] | None:
        applied: dict[str, Any] = {}

        def fn(t: dict[str, Any]) -> None:
            for k, v in fields.items():
                if k in self._EDITABLE_FIELDS:
                    t[k] = v
                    applied[k] = v
            t["timeline"].append({
                "time": _iso(_now()), "author": COPILOT_OWNER, "type": "update",
                "summary": "Updated fields: " + ", ".join(sorted(applied)) if applied else "No editable fields changed.",
                "visibility": "internal",
            })

        ticket = self._mutate(ticket_id, fn)
        return ticket

    def add_comment(self, ticket_id: str, body: str, visibility: str) -> dict[str, Any] | None:
        def fn(t: dict[str, Any]) -> None:
            t["timeline"].append({
                "time": _iso(_now()), "author": COPILOT_OWNER, "type": "comment",
                "summary": body, "visibility": visibility,
            })

        return self._mutate(ticket_id, fn)

    def transition(self, ticket_id: str, target: str, reason: str | None) -> dict[str, Any]:
        """Apply a state transition. Returns {ok, ...}; never raises for known
        business errors so the tool layer can hand the agent an actionable hint."""
        ticket = self.get(ticket_id)
        if ticket is None:
            return {"ok": False, "code": "TICKET_NOT_FOUND"}
        legal = available_transitions(ticket["status"])
        match = next((tr for tr in legal if tr["id"] == target or tr["to"] == target), None)
        if match is None:
            return {
                "ok": False,
                "code": "ILLEGAL_TRANSITION",
                "from": ticket["status"],
                "available": legal,
            }
        from_status = ticket["status"]

        def fn(t: dict[str, Any]) -> None:
            t["status"] = match["to"]
            note = f"{from_status} -> {match['to']}" + (f": {reason}" if reason else ".")
            t["timeline"].append({
                "time": _iso(_now()), "author": COPILOT_OWNER, "type": "transition",
                "summary": note, "visibility": "internal",
            })

        updated = self._mutate(ticket_id, fn)
        return {"ok": True, "from": from_status, "to": match["to"], "ticket": updated}

    def reassign(self, ticket_id: str, owner: str, reason: str | None) -> dict[str, Any] | None:
        def fn(t: dict[str, Any]) -> None:
            prev = t.get("owner")
            t["owner"] = owner
            t["todo"] = owner == COPILOT_OWNER
            note = f"Owner {prev} -> {owner}" + (f": {reason}" if reason else ".")
            t["timeline"].append({
                "time": _iso(_now()), "author": COPILOT_OWNER, "type": "assignment",
                "summary": note, "visibility": "internal",
            })

        return self._mutate(ticket_id, fn)
