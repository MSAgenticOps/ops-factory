# Change Scope Rules

## Keep Diffs Intentional
- Do not mix feature work with unrelated refactors.
- Avoid broad renames or directory reshuffles unless that is the actual task.
- Prefer the smallest change that preserves architecture boundaries and testability.

## Cross-Team Coordination
- If a change touches `gateway`, `web-app`, and shared contracts together, describe the dependency chain clearly in the PR.
- If you add a config field, update the matching config example file, startup assumptions, and docs in the same change.
- Config example files must mirror the structure the code actually binds to. For Spring `config.yaml` files, keep keys aligned with the real `@ConfigurationProperties` prefix and field names, and do not introduce extra nesting in the example unless the service reads that nested structure.
- Local runtime config files such as `config.yaml` are environment-specific and should stay out of version control; shared defaults and key descriptions belong in the matching `*.example` file and docs.
- If a change breaks compatibility for other teams, write that explicitly instead of hiding it in implementation details.

## AI And Human Contributors
- Treat `AGENTS.md` as the short mandatory rule set.
- Treat this document set as persistent collaboration memory; do not rely on session-only instructions.
- When a new repository convention becomes important, write it down here before expecting others to infer it from code.
