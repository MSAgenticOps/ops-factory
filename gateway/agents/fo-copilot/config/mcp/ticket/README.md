# ticket MCP adapter (FO Copilot)

The `ticket` MCP gives FO Copilot the hands to operate a ticketing system: pull
the tickets awaiting its follow-up, read one ticket's full decision context,
pick an owner, and write (create / update / comment / transition / reassign).

It is the **atomic operation layer** under the FO Copilot skills. Skills compose
these tools into workflows (watch / intake / assign / advance / escalate /
close); the tools themselves carry no business judgment (no P1/P2 authorization
gate — that lives in the skill prompts). See
[`docs/architecture/fo-copilot-proactive-assistant.md`](../../../../../../docs/architecture/fo-copilot-proactive-assistant.md)
§4.2.

## Tools (8)

| Tool | Kind | Purpose |
| --- | --- | --- |
| `get_todo` | read · scan | Tickets awaiting my follow-up, as concise summaries (watch entry point). |
| `get_state_context` | read · deep | One ticket's full decision context; `format: concise \| detailed`. |
| `get_candidates` | read · routing | Team roster (skills / on-shift / load / responsibilities); rank by `ticketId`. |
| `create` | write | Create a ticket. |
| `update` | write | Correct fields (priority, category, …). |
| `comment` | write | Add a timeline comment (audit trail). |
| `transition` | write | Move state; validates legality, returns an actionable hint on illegal moves. |
| `update_assignment` | write | Reassign owner; away from copilot = hands the ball off. |

Reads are **task-aligned** (consolidated, with a concise/detailed knob) rather
than raw per-field CRUD; writes are **atomic and orthogonal**. Design rationale:
<https://www.anthropic.com/engineering/writing-tools-for-agents>.

All write tools accept optional `operationId` / `followupId` idempotency keys
(accepted and ignored by this mock — the contract keeps the extension point).

## Mock backend

This first implementation is a **file-backed mock**, not a real ticketing
integration: a small seeded board plus a generic state machine, enough to
demonstrate and test the end-to-end watch loop (really pull tickets, really
change state). The real adapter swaps in behind the identical tool surface.

- State machine: `new → triage → in_progress → {pending, resolved} → {closed, reopen}` (+ `cancel`). See `TRANSITIONS` in `store.py`.
- Store file: `TICKET_MOCK_STORE` env (relative paths resolve against the process CWD = the per-user `GOOSE_PATH_ROOT`); defaults to `./.data/store.json` next to the server. Seeded on first run if absent. The `.data/` dir is gitignored.
- Server log: `TICKET_MCP_LOG` env; defaults to `./.data/server.log`.

## Files

- `server.py` — stdio plumbing (depends on `mcp`).
- `tools.py` — tool schemas + dispatch + response shaping (no `mcp` dependency).
- `store.py` — mock store + state machine + seed.
- `test_tools.py` — logic tests (`python -m unittest test_tools`, no `mcp` needed).
- `requirements.txt` — `mcp`.

## Local setup

```bash
cd gateway/agents/fo-copilot/config/mcp/ticket
python3 -m venv .venv
.venv/bin/pip install -r requirements.txt
```

The `.venv` is gitignored and built at deploy time, matching the other agents'
MCP servers. It is referenced from `config.yaml` as
`config/mcp/ticket/.venv/bin/python`.
