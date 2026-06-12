---
name: close
description: "Close a ticket (close). Use when a ticket meets the closure conditions. Read full context, summarize problem/impact/root-cause/handling/verification/prevention, report and ask the FO lead to confirm, then write the summary back and transition to closed. 关单。"
---

# Close

Produce a closing summary for a ticket that meets the closure conditions, and (where authorization allows) transition it to closed.

## Workflow

1. **Read full context**: `ticket.get_state_context` to query the ticket's detailed information and complete history — this is one of the few scenarios that needs a timeline deep-read.
2. **Validate closure conditions**: is the problem truly resolved / the request truly fulfilled / the verification truly passed? If not, do not close; state what is still missing.
3. **Write the closing summary**: cover **problem, impact, root cause, handling, verification, prevention**. **Strictly keep facts / inferences / unverified assumptions apart** — write what was verified as fact, mark what was not checked as an assumption, do not present guesses as conclusions.
4. **Handle per authorization** (see below): write the summary back (`ticket.comment`) and transition to closed with `ticket.transition`.
5. **Postmortem reminder**: if it is a P1/P2 incident, remind to drive a postmortem (timeline → root cause → impact → improvement actions → owner); an unclosed postmortem follow-up may be recorded as a dynamic-memory entry.

## Action authorization

Closing is a high-risk write operation:

- **P1 / P2 tickets: propose and await confirmation** — first send the closing summary as a report and ask the FO lead to confirm; only **after confirmation** write the summary back and transition to closed. Do not act first and ask later.
- **P3 / P4 tickets: may close directly** when closure conditions are met, writing the summary back and leaving a `ticket.comment` with the reason.
- When priority cannot be determined, treat as P2 (ask for confirmation first).

## Memory rules

- **Scope is always global** (`is_global=true`).
- **Closing is mostly "delete"**: clean up the now-lapsed dynamic memory related to this ticket — agreements on it, its "escalated" record, etc. Once a ticket is closed, these should disappear.
- **May write**: only record one entry if a cross-ticket persistent follow-up remains (e.g. a postmortem action not yet closed out), anchored with a timestamp.
- **Do not write**: the closing summary itself — it is written back into the ticket comment, not into memory (avoid a second source of truth).
- Note: memory deleted / written this round is re-injected into the prompt only next run.

## Report format

- Carry clear ticket context; give the structured closing summary (problem / impact / root cause / handling / verification / prevention).
- Keep facts / inferences / unverified assumptions apart.
- For P1/P2, end with a clear "I'll close it after you confirm"; for P3/P4, state it has been closed.
- Do not fabricate root cause or verification results.
