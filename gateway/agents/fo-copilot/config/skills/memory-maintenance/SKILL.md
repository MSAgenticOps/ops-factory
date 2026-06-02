---
name: memory-maintenance
description: "Memory maintenance (memory-maintenance). Fired by a daily scheduled task to health-check and trim the dynamic memory: merge duplicates, delete lapsed entries, condense overlong entries. 记忆维护。Use when the daily memory-maintenance task fires."
---

# Memory Maintenance

Fired by an independent daily scheduled task (agreed id `fo-copilot-memory-maintenance`, daily at 12:00), dedicated to health-checking and length-controlling the dynamic memory. When goose loads memory it reads everything unconditionally and concatenates it into the prompt with no length limit, so length must be backstopped by this task.

## Workflow

1. **Review all memory**: the full memory comes in with the long context, so you can see every entry right there in context; judge each one "keep / edit / delete". To confirm whether an entry is still in effect, re-read the latest on-disk content with the memory tool.
2. **Delete the lapsed**: judged against current ticket reality — agreements on already-closed tickets, expired maintenance windows / do-not-disturb windows, escalation records that already got a decision — delete them.
3. **Merge duplicates**: combine multiple fragments about the same ticket / same matter into one, anchored with a ticket number / timestamp.
4. **Condense the overlong**: compress verbose entries into a one-line point, keeping the ticket number and timestamp.
5. **Report changes**: list "what changed and why" so the FO lead can review.

## Work within your means / preservation discretion

- **Only handle the memory files; do not go scan all tickets.** When you need to verify whether an entry is lapsed, **point-query** the corresponding ticket with the ticket MCP (`ticket.get`) — **never a full-table scan**.
- **Do not touch the human-preset team policy**: things like the SLA ruler and ops preferences live in the prompt / AGENTS.md, not in dynamic memory — this task does not touch them. Only clean up the dynamic entries auto-accumulated during runs.
- **When in doubt, lean toward keeping and flagging, do not arbitrarily delete** what looks human-written.

## Action authorization

This task only reads/writes **memory**, never touches ticket state. Point-querying a ticket (`ticket.get`) is read-only and may be done directly. Do not perform any ticket write operation (transition / reassignment / close) in this task.

## Memory rules

- **Scope is always global** (`is_global=true`) — both reading and writing target global memory, otherwise what is maintained and what the agent actually loads will diverge.
- Deletes / merges land on disk immediately. Note that this round's changes are re-injected into the prompt only next run.
- Maintenance does not add "business" memory; it only tidies existing entries.

## Report format

- Summarize this maintenance: how many entries deleted (and why), which were merged, which were condensed, and roughly the current memory size.
- This task **does not push to any IM channel**; its report is the transcript of this run; language defaults to Chinese.
- Distinguish "confirmed lapsed, deleted" from "in doubt, kept for review".
