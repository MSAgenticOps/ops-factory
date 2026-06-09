---
name: assign
description: "Assign / reassign (assign). Use when a new ticket needs an owner, the current handler is unavailable (on leave / off duty), or a handover is needed. Pick a suitable handler from skills, schedule, load, and responsibility boundaries. 派单 / 改派。"
---

# Assign

Pick a suitable handler for a ticket and complete (or recommend) the (re)assignment. Works on generic ticket semantics.

## Workflow

1. **Read context**: `ticket.get_state_context` for ticket details, focusing on the routing context (`routingContext`: candidates, skills, schedule, load, responsibility boundaries). When routing data is absent, state the default reason — do not invent candidates.
2. **Pick a handler**: 
   - Call `ticket.get_candidates` to query current members' **skills** (技能), **on-duty status** (值班情况), and **load level** (负载情况)
   - Combine with ticket details retrieved in step 1 (type, priority, category, affected system, etc.)
   - Select the most suitable candidate based on:
     - **Skill match**: Candidate's expertise aligns with the ticket's category/affected system
     - **On-duty status**: Prefer candidates currently on shift
     - **Load level**: Consider current workload to avoid overburdening
     - **Responsibility boundaries**: Respect team/domain ownership rules
   - An unavailable handler (on leave / off duty) is a common trigger for reassignment
3. **Execute or propose**: reassign with `ticket.update_assignment`.
4. **Comment to explain**: after reassigning, use `ticket.comment` to spell out "who it goes to, why, and when feedback is expected".

## Action authorization

Reassignment is a high-risk write operation:

- **P1 / P2 tickets: propose and await confirmation** — give the recommended handler and reason in the report, mark it "awaiting your confirmation", and do not call `ticket.update_assignment` directly.
- **P3 / P4 tickets: may reassign directly**, with a `ticket.comment` leaving the reason.
- When priority cannot be determined, treat as P2 (propose first).

## Memory rules

- **Scope is always global** (`is_global=true`).
- **May write**: temporary information that affects assignment and that the ticketing system cannot look up (e.g. "Zhang is on leave this week, route related tickets to Li"). Anchor with a timestamp.
- **Do not write**: schedule, load, skills, current handler — these **facts are queryable in the routing context**; always query live to avoid a second source of truth.
- Delete when the agreement lapses (e.g. leave ends); before writing, search for similar entries to dedupe / update in place.

## Report format

- State which ticket, who it is (recommended to be) assigned to, and the basis (skills / schedule / load / responsibility).
- For P1/P2, make clear this is a "recommendation awaiting confirmation"; at the end, state whether a human needs to act.
- Do not fabricate candidates or their schedule / load.
