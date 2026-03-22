# UI Guidelines

## Preserve Existing Interaction Model
- Keep the current route-driven shell and sidebar-based navigation.
- Preserve the right-panel pattern for preview and market-style auxiliary flows.
- Reuse existing contexts, hooks, and shared components before adding parallel state flows.

## UI Change Rules
- New pages should fit the current information architecture: `src/pages` for route pages, `src/components` for reusable UI, `src/hooks` for shared logic.
- Keep i18n support in mind when introducing user-facing text.
- Errors should use the established error-handling and toast patterns rather than bespoke banners per page.
- Responsive behavior is required for any new top-level page or major workflow.

## Review Triggers
Request design or frontend review when a change affects:
- navigation structure
- right-panel behavior
- chat/file/monitoring core workflows
- shared visual patterns used across multiple pages
