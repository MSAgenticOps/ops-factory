# API Boundaries

## Service Entry Points
Browser traffic may access multiple first-class services. Preserve stable service ownership and avoid ad hoc cross-service coupling:
- `gateway` owns authentication via `x-secret-key`, user scoping via `x-user-id`, agent routing, sessions, file APIs, and monitoring
- `knowledge-service` owns knowledge ingestion, retrieval, chunk/document management, and related maintenance APIs
- `business-intelligence` owns analytics, report generation, report history, and business insight APIs

## Compatibility Rules
- Do not change route prefixes, response shapes, or auth header names casually.
- Changes to SSE or streaming event payloads require explicit review and coordinated frontend/test updates.
- If an endpoint contract changes, update frontend consumers, SDK types, and integration tests in the same change.
- New browser-facing service integrations must be wired through explicit web-app runtime config keys rather than hardcoded origins.

## File and Config APIs
- File access must continue to flow through gateway services/controllers rather than direct filesystem exposure from the UI.
- Agent config CRUD should remain centralized in gateway services and controller routes.

## Review Triggers
Request explicit cross-team review when a change affects:
- API path structure
- auth semantics
- session lifecycle
- SSE message format
- compatibility with existing test fixtures or SDK consumers
