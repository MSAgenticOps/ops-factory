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
from datetime import datetime, timedelta, timezone
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
    ]

    candidates = [
        {"id": "li.wei", "name": "Li Wei", "skills": ["payments", "checkout-api", "java"], "onShift": True, "openTickets": 2, "responsibilities": ["payments", "checkout"]},
        {"id": "wang.fang", "name": "Wang Fang", "skills": ["database", "analytics-db", "sql"], "onShift": True, "openTickets": 1, "responsibilities": ["database"]},
        {"id": "zhang.lei", "name": "Zhang Lei", "skills": ["platform", "ingress", "kubernetes"], "onShift": False, "openTickets": 4, "responsibilities": ["platform"]},
        {"id": "chen.hao", "name": "Chen Hao", "skills": ["network", "vpn", "firewall"], "onShift": True, "openTickets": 3, "responsibilities": ["network"]},
        {"id": "sun.li", "name": "Sun Li", "skills": ["database", "billing-db", "backup"], "onShift": True, "openTickets": 0, "responsibilities": ["database", "billing"]},
    ]

    return {"tickets": tickets, "candidates": candidates, "seq": 9000}


class TicketStore:
    """Whole-file load/save JSON store. Simple and synchronous — fine for a mock."""

    def __init__(self, path: str | None = None) -> None:
        raw = path or os.environ.get("TICKET_MOCK_STORE") or str(
            Path(__file__).resolve().parent / ".data" / "store.json"
        )
        self.path = Path(raw).expanduser()
        if not self.path.is_absolute():
            # relative paths resolve against CWD (= GOOSE_PATH_ROOT per-user dir)
            self.path = Path.cwd() / self.path
        self._ensure_seeded()

    def _ensure_seeded(self) -> None:
        if self.path.exists():
            return
        self.path.parent.mkdir(parents=True, exist_ok=True)
        self._save(_build_seed())

    def _load(self) -> dict[str, Any]:
        with self.path.open("r", encoding="utf-8") as fh:
            return json.load(fh)

    def _save(self, data: dict[str, Any]) -> None:
        tmp = self.path.with_suffix(self.path.suffix + ".tmp")
        with tmp.open("w", encoding="utf-8") as fh:
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
