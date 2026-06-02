---
name: escalate
description: "Escalate (escalate). Use when a situation needs the FO lead's awareness or decision — high-priority near / past SLA, a conflict, unclear ownership, a third party unresponsive for long, a ticket stuck. Organize clear context, produce a report, record one escalation memory to avoid repeats. 升级。"
---

# Escalate

Organize a situation that needs the FO lead's awareness or decision into a clear report and send it up. The escalation target is uniformly the FO lead — who then coordinates upward or laterally; you do not directly alarm higher levels or other teams.

## When to escalate

Per "Team Policy · escalation matrix", escalate on any of: P1/P2 near or past SLA; a conflict or unclear ownership; a third party / external dependency unresponsive for a long time; a ticket repeatedly bounced and stuck; needing the FO lead's decision or authorization to proceed.

## Workflow

1. **Organize context**: `ticket.get` (and `ticket.get_timeline` if needed) for enough background. Make clear: which ticket, where it is stuck, why it needs the FO lead, what the options are.
2. **Check memory first**: has this already been escalated? Do not re-bother for an item escalated-but-not-closed-out, unless the situation materially changed (e.g. from near-deadline to past SLA).
3. **Produce a report**: organize clear context and **state at the end exactly what the FO lead needs to do** (pick an option / authorize an action / just be aware).
4. **Record one escalation memory**: to avoid escalating the same thing again next round.

## Action authorization

Escalation itself is "informing / consulting", not a write operation, and may be produced at any priority. But **high-risk actions recommended within** the escalation still follow authorization: present P1/P2 ones as "recommendations awaiting confirmation"; do not throw a P3/P4 action you could have done directly at a human as an escalation.

## Memory rules

- **Scope is always global** (`is_global=true`).
- **Must write**: an "escalated" record — which ticket, what was escalated, when, what decision is awaited. This is escalate's core memory use, specifically to **avoid duplicate nagging**.
- **When to delete**: delete immediately when the FO lead has given a decision, or the ticket is closed out.
- **Do not write**: queryable facts like ticket state / SLA / handler.
- Before writing, search for an existing escalation record of the same item; if present, update in place rather than adding a new one. Memory written this round only takes effect next run.

## Report format

- Carry clear ticket context; state the situation, why it is escalated, and what the options are.
- End with one line on "what you need to do".
- Do not fabricate impact / priority; keep facts and inferences apart.
