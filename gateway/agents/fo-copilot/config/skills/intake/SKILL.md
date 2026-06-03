---
name: intake
description: "Open / create a ticket (intake). Use when an intake intent is recognized in conversation — reporting an issue, raising a request, asking to log something. Decide whether a ticket is really warranted, extract key fields, fill gaps, then create the ticket. 开单 / 创建工单。"
---

# Intake

Turn a conversation or request into a clear, directly actionable ticket. Works on generic ticket semantics, not bound to any specific ticketing system.

## Workflow

1. **Decide whether a ticket is really warranted**: pure advisory questions you can answer on the spot do not need a ticket; only "work that must be tracked to closure" does. When unsure, confirm with one line.
2. **Determine type and priority**: Incident / Problem / Change / Service Request; estimate priority (P1–P4) per "Team Policy · SLA care ruler". Priority drives downstream authorization; when you cannot estimate it, lean conservative and say so.
3. **Extract key fields**: title, description, impact scope, urgency, affected system, contact / source.
4. **Fill gaps**: when key fields are missing, ask first — **at most 1–3** of the most important questions at a time, do not interrogate item by item.
5. **Create**: create with `ticket.create`. Use `ticket.update` / `ticket.comment` to add fields or write back a source summary.
6. **Write back a source summary**: after creating, record a source note with `ticket.comment` (where this request came from, who raised it, the original points) so a later handler can reconstruct the background.

## Action authorization

- Creating a ticket and commenting are low-risk and may be done directly.
- If intake also involves a high-risk write operation (e.g. immediately assigning to someone, directly advancing state): for **P1 / P2 tickets propose and await confirmation**; **P3 / P4 may be executed directly** with a `ticket.comment` leaving the reason.

## Memory rules

- **Scope is always global** (`is_global=true`), otherwise it will not carry across runs.
- **May write**: temporary agreements given at intake time that ticket fields cannot hold and that will affect future watch (e.g. "advance this ticket only after the customer confirms budget"). Anchor each with a ticket number / timestamp.
- **Do not write**: the ticket's own fields (title, state, handler, priority) — they live in the ticket, query live; do not duplicate them in memory.
- Before writing, search for similar entries to dedupe / update in place. Delete when the agreement lapses (e.g. the customer has confirmed).

## Report format

- State which ticket was opened (number + one-line title), type, priority, and whether key fields are complete.
- If you asked questions, list the 1–3 fields still missing.
- At the end, state whether a human needs to act (e.g. FO lead to confirm assignment / priority).
- Do not fabricate impact, system names, or contacts.
