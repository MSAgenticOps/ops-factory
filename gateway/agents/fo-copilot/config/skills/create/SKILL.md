---
name: create
description: "Open / create a ticket. Use when an intake intent is recognized in conversation — reporting an issue, raising a request, asking to log something. Decide whether a ticket is really warranted, extract key fields, fill gaps, then create the ticket. 开单 / 创建工单。"
---

# Create Ticket (Intake)

Turn a conversation or request into a clear, directly actionable ticket. Works on generic ticket semantics, not bound to any specific ticketing system.

## Workflow

1. **Decide whether a ticket is really warranted**: pure advisory questions you can answer on the spot do not need a ticket; only "work that must be tracked to closure" does. When unsure, confirm with one line.
2. **Determine type and priority**: 
   - Identify ticket type: Incident / Problem / Change / Service Request
   - Extract **impact** (影响范围) and **urgency** (紧急程度) from the user request
   - Determine **priority** (P1–P4) based on impact and urgency matrix (decision matrix TBD — for now, lean conservative and state your reasoning when uncertain)
   - Priority drives downstream authorization: P1/P2 require confirmation, P3/P4 can proceed directly

3. **Extract required fields for Incident tickets**:
   For **Incident** type tickets, extract these **required** parameters from the user request:
   - **title**: 工单标题
   - **desc**: 详细描述
   - **date**: 发生日期
   - **category**: 分类（调用 `ticket.queryCategory` 获取可选值）
   - **subcategory**: 子分类（调用 `ticket.querySubCategory` 基于选中的category获取可选值，category与subcategory是一对多关系）
   - **type**: 工单类型（调用 `ticket.queryIncidentType` 获取可选值）
   - **impact**: 影响范围（调用 `ticket.queryImpact` 获取可选值）
   - **urgency**: 紧急程度（调用 `ticket.queryUrgency` 获取可选值）
   - **impactCi**: 受影响的配置项（调用 `ticket.queryImpactCi` 获取可选值，支持多选，用逗号分隔）
   - **ticketSource**: 工单来源（调用 `ticket.queryTicketSource` 获取可选值，支持多选，用逗号分隔）

   When user information is insufficient:
   - Call the respective query APIs to present valid options to the user
   - Ask **at most 1–3 questions at a time** to fill gaps
   - Do not interrogate item by item; prioritize the most critical missing fields
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
