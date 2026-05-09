# Service Test Layout

This directory follows the deployable service boundaries documented under `docs/deployment`.

| Deployment service | Test location | Notes |
| --- | --- | --- |
| Gateway | `gateway/` | Gateway-owned APIs, config, sessions, files, scheduler, Agent configuration, channel runtime layout, and Gateway-hosted business APIs such as remote diagnosis. |
| goosed | `goosed/` | Agent runtime lifecycle, model health, resident process behavior, and end-to-end Agent conversations started through Gateway. |
| Knowledge Service | `knowledge-service/` | Knowledge ingestion, retrieval, and live knowledge API checks. |
| Prometheus Exporter | `prometheus-exporter/` | Exporter service integration checks. |

Services that keep their tests in their own package directories stay there:

| Deployment service | Test location |
| --- | --- |
| Business Intelligence | `business-intelligence/**/src/test` or package-local tests |
| Skill Market | `skill-market/**` |
| Control Center | `control-center/**` |
| TypeScript SDK | `typescript-sdk/tests` |

Shared test helpers stay in `test/platform/shared` because they are infrastructure used by several service suites, not a deployable service.
