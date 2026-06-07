---
name: daily-brief
description: "Daily brief (daily-brief). The FO lead's morning operations brief: pull the previous day's ticket-handling summary and render it as a tight ops-director report — what came in, what got closed, what's still important, and above all what needs the FO lead's decision today. 每日运维简报。Use when the scheduled daily-brief task fires, or when asked how the previous day went."
---

# Daily Brief

Produce the FO lead's morning operations brief in one pass. This is a **read-only reporting** skill — it never changes ticket state and routes to no action skill. One call to `ticket.get_daily_brief`, then write a crisp report a busy ops director can read in fifteen seconds and know exactly what, if anything, needs their call today.

## Workflow

1. **Pull the brief**: `ticket.get_daily_brief` (defaults to yesterday; it rolls back to the most recent active day on its own if yesterday was quiet, and tells you via `fallbackFrom`). The payload carries: `opened` / `resolved` / `handled` counts, a `byPriority` breakdown of what was opened, the currently-open important backlog (`importantOpen` P1/P2), the items `awaitingDecision` (each with the specific `question` for you), and what is `atRisk` against SLA.
2. **Render the report** using the template below. Numbers first, then the short list of things that actually need attention, then the decisions you owe an answer to.
3. **Do not deep-read or act**: this is a digest, not a watch round. If something needs action, name it under "Needs your decision" — don't transition, reassign, or close anything here.

## Report template (ops-director style)

Keep it to the shape below. Lead with the headline, never bury the decisions.

```
📋 FO Daily Brief — <date>
(If the brief rolled back from an empty day, add: "yesterday was quiet; showing <date>.")

Yesterday at a glance
• Opened <opened> · Resolved <resolved> · Worked <handled>
• New by priority: P1 <n> · P2 <n> · P3 <n> · P4 <n>
• Important still open: <importantOpen.P1> P1, <importantOpen.P2> P2

⚠️ Needs your decision (<count>)
For each awaitingDecision item:
• <id> [<priority>] <title>
  → <question>     (the concrete call you have to make)
(If none: "Nothing blocked on you. ✅")

🔥 At SLA risk (<count>)
For each atRisk item:
• <id> [<priority>] <title> — resolve due <resolveDueAt>
(If none: omit this section.)

Bottom line: <one sentence — either "X decisions waiting on you, Y at risk" or "all green, nothing needs you">.
```

## Authoring rules

- **Decisions first.** The single job of this brief is to surface what needs the FO lead. If `awaitingDecision` is non-empty, those items lead; everything else is context. Always restate each item's `question` verbatim-in-spirit — the lead should not have to open the ticket to know what you're asking.
- **Honest, not noisy.** If the day was quiet, say so in one line. Don't pad. Don't invent risk, impact, or root cause that isn't in the data.
- **No raw JSON, no internal ids beyond ticket numbers.** Numbers and ticket numbers only; no opaque keys, no contact details, no secrets.
- **Counts come from the tool, not from you.** Don't recompute or estimate — report what `get_daily_brief` returns. If a count looks surprising, report it as-is; investigating is a watch-round job, not a brief.

## Delivery & memory

- This skill is wired to a scheduled task that delivers to the FO lead's bound IM channel. Write the report as the message body; the platform handles delivery.
- **Memory**: nothing to write. The brief is a stateless digest derived live each morning — do not record "what I reported today" or any per-run scan state (same stateless principle as watch).
