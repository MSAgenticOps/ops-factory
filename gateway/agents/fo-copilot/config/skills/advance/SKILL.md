---
name: advance
description: "Advance / correct a ticket (advance). Use when a ticket needs to move to its next state, or fields need correcting (change category, change priority, etc.). Read context, pick the target state, validate preconditions, write the reason, execute per authorization. 推进 / 订正工单。"
---

# Advance

Move a ticket to its next state, or correct its fields. Works on generic ticket semantics.

## Workflow

1. **Read context**: `ticket.get` for current state, owner, priority, SLA. Only when you are actually going to act on this ticket, use `ticket.get_timeline` to deep-read the full history (routine scans do not read the timeline).
2. **Read memory**: check for any agreement on this ticket (e.g. "don't chase until the customer confirms Friday") or an existing escalation record, to avoid breaking an agreement or nagging twice.
3. **Decide the action**: pick the target state (`ticket.list_transitions` to list available transitions) or the fields to correct (category, priority, etc.).
4. **Validate preconditions**: are the transition's preconditions met, does it need confirmation (see the `Transition`'s `needsConfirm`)? If not, fill the gap or explain first.
5. **Write the reason**: before / after executing, use `ticket.comment` to spell out why the change is made.
6. **Execute**: `ticket.transition` to advance state, `ticket.update` to change fields — **per the authorization below**.

## Action authorization

State transition is a high-risk write operation; corrections like changing priority or category are treated as high-risk if they change the authorization tier:

- **P1 / P2 tickets: propose and await confirmation** — give the recommended target state / field change and reason in the report, mark "awaiting your confirmation", and do not execute directly.
- **P3 / P4 tickets: may execute directly**, with a mandatory `ticket.comment` carrying the reason after executing.
- When priority cannot be determined, treat as P2.

## Memory rules

- **Scope is always global** (`is_global=true`).
- **May write**: an agreement or blocker exposed while advancing that affects future watch and that the ticketing system cannot look up (e.g. "this ticket can only advance after approval passes"). Anchor with a ticket number / timestamp.
- **Do not write**: the ticket's current state, fields, SLA, timeline — query live.
- Delete when the blocker clears or the agreement lapses; before writing, search for similar entries to dedupe / update in place.

## Report format

- State which ticket, what state it advanced to / you want to advance it to, or what fields were corrected, and the basis.
- For P1/P2, make clear it is a "recommendation awaiting confirmation"; at the end, state whether a human needs to act.
- Keep facts and inferences apart; do not fabricate state or preconditions.
