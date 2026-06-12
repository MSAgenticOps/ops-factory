---
name: monitor
description: "Proactive watch (watch). One round of ticket patrol fired by a scheduled task: pull the tickets 'waiting on me', judge each and try to pass the ball on, maintain memory, and produce one report. 主动看护。Use when the scheduled proactive ticket-watch task fires."
---

# Watch

This is the driving loop of proactive watch. Each run is fired by a scheduled task and is **one independent round**: observe this round's tickets that are "waiting on me", judge each and try to pass the ball on, produce a report, end. It does not touch the timeline or deep-read itself — it routes to the action skills (advance / assign / escalate / close).

## The "whose court is the ball in" lens

Every ticket has a "next-action owner". Watching = pushing out the tickets whose ball is **not in the other party's court but stuck with me (copilot)**; the goal of handling is to make this ticket's owner no longer be copilot. Whether action is needed is judged per "Team Policy · SLA care ruler" (stuck / near-deadline / should escalate).

## Workflow

1. **Get the list**: `ticket.get_todo` for the tickets "waiting on me". Its selection rule is defined on the ticketing-system side; you do not care about the rule, only about working the list down item by item.
2. **Handle each ticket**:
   - Loop through each ticket from the list in step 1.
   - Call `ticket.get_state_context` to query the ticket's detailed information and decide the next action.
   - **Read memory**: is there an agreement on this ticket (e.g. "wait for the customer Friday"), has it already been escalated → avoid breaking an agreement or nagging twice.
   - **Decide and execute per authorization**:
     - If transition is needed, call `ticket.transition` to execute the transition.
     - If reassignment is needed, call `ticket.update_assignment` to reassign to another person.
     - The ultimate goal is to pass the ticket to someone else (no longer copilot).
3. **Maintain memory**: write the new agreements that surfaced this round, clean up lapsed entries.
4. **Produce a report**: focus on the 1–3 most important items; if nothing is wrong, a single line "all normal, no action needed".

## Stateless principle (important)

- Each round is independent. **Do not write "which tickets I scanned last round" into memory**; do not maintain cross-round scan state.
- A ticket whose ball cannot be passed this round (e.g. agreement "wait for customer Friday") will still appear in the to-do next round with unchanged state — this is expected. Tolerate it through **report concision**: for "still waiting" items, a single terse line or fold into a digest — do not repeatedly spam the same ticket. When this round's to-do is done, the round ends.
- Deep-reading (`ticket.get_timeline`, full context) happens only when advance / close is actually going to act on a ticket, not in the routine scan.

## Action authorization

High-risk write operations triggered during the patrol (state transition / reassignment / close) follow the uniform authorization: **P1 / P2 propose and await confirmation; P3 / P4 execute directly** with a `ticket.comment` leaving the reason. When priority cannot be determined, treat as P2.

## Memory rules

- **Scope is always global** (`is_global=true`), otherwise it will not carry across runs.
- **May write**: human-given agreements, items escalated but not closed out, temporary collaboration preferences — things that will change future watch behavior and that the ticketing system cannot look up. Anchor with a ticket number / timestamp.
- **When to delete**: delete immediately when the agreed time has passed, the ticket is closed, the escalation got a decision, or the preference was overridden.
- **Never write**: ticket state, handler, SLA, timeline — query live. Also do not write "which tickets I scanned this round".
- Before writing, search for similar entries to dedupe / update in place. Note: memory newly written this round only takes effect next run; to see it this round, re-read with the memory tool.

## Report format

- Carry clear ticket context (number + one line) so the reader need not guess which ticket.
- Focus on the 1–3 most important items; compress "still waiting / no change" into one line or a digest.
- At the end, state whether a human needs to act (especially P1/P2 recommendations awaiting confirmation).
- The ticketing system's own reminders notify whoever should be notified; your report **goes only to the FO lead**, with what they need to know or decide.
