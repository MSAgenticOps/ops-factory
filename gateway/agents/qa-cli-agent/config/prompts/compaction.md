## Task Context
- An LLM context limit or auto-compaction threshold was reached during a QA CLI Agent session.
- The active model context is 128K tokens, but the compacted summary must intentionally target a much smaller budget.
- Produce a continuation summary that preserves what the next agent needs and removes large raw evidence.

## Compression Budget
- Target summary size: 20K-25K tokens.
- Hard upper bound: 32K tokens.
- Prefer underestimating the needed size. The next request will add the system prompt, active tool schemas, the current user message, and new tool results.
- Do not copy long tool outputs, long file excerpts, or repeated search results. Keep exact file paths, line ranges, identifiers, and conclusions instead.

## Conversation History
{{ messages }}

Wrap reasoning in `<analysis>` tags:
- Review the conversation chronologically.
- Track the current user goal, constraints, and unresolved requests.
- Preserve confirmed facts, file paths, line numbers, table names, field names, API names, error messages, commands, and configuration values.
- Preserve decisions already made and user feedback that changed direction.
- Preserve active work at the moment compaction was triggered.
- Remove verbose raw tool output unless a short excerpt is essential.

### Include the Following Sections
1. **User Intent** - current and prior goals that still matter.
2. **Technical Concepts** - relevant tools, config, context management, and retrieval behavior.
3. **Files + Evidence** - files viewed or changed, important paths, exact line ranges when available, and short evidence snippets only when needed.
4. **Errors + Fixes** - failures, root causes, and applied or proposed fixes.
5. **Decisions** - accepted design choices and rejected alternatives.
6. **Pending Tasks** - unresolved work or verification still required.
7. **Current Work** - active task state at compaction time, including filenames and next edit/test target.
8. **Next Step** - only the direct next step needed to continue the latest user instruction.

Keep this summary dense and operational. It is for the next agent turn, not for the user.
