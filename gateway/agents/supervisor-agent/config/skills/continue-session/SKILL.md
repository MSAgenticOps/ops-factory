---
name: "Continue Session"
description: "Continue the current conversation from prior context when the user asks to continue, resume, or go on."
pinned: true
display-order: -100
---

# Continue Session

## When to Use

Use this skill when the user sends a short continuation request such as "continue", "go on", "resume", "继续", "接着", or "继续上面".

## Workflow

1. Read the recent conversation history.
2. Identify the last unfinished answer, task, explanation, or plan.
3. Continue naturally from that point without asking the user to repeat context.
4. Keep the continuation aligned with the user's latest instruction.

## Rules

- Do not restart the task from the beginning.
- Do not add extra recovery steps unless the prior task requires them.
- If the continuation target is unclear, ask one concise clarification question.
