---
name: "OpsFactory Platform Diagnosis"
description: "Diagnose overall OpsFactory platform health using Control Center runtime, agent, observability, realtime metric, and service status tools."
---

# OpsFactory Platform Diagnosis

## When to Use

Use this skill when the user asks for an OpsFactory platform health check, daily diagnosis, control center report, or overall system status.

## Workflow

1. Call `control_center__get_platform_status` for gateway health, uptime, running instances, and Langfuse status.
2. Call `control_center__get_agents_status` for configured agents, provider/model settings, skills, and running instance counts.
3. Call `control_center__get_realtime_metrics` for active sessions, requests, errors, latency, TTFT, tokens/sec, and per-agent runtime metrics.
4. Call `control_center__list_services` for managed service health.
5. If observability is relevant, call `control_center__get_observability_data` with `hours=24`; if unavailable, state that limitation.

## Findings

Classify findings as:

- CRITICAL: service down, gateway unreachable, repeated errors, failed control-center actions.
- WARNING: high latency, high TTFT, nonzero error trend, missing Langfuse, unexpected zero running instances.
- INFO: healthy status, normal but notable configuration or usage data.

## Output

Use Markdown with: Summary, Findings, Recommendations, and Key Metrics. Include only metrics returned by tools.

## Rules

- Do not fabricate metrics or baselines beyond the returned data.
- Use the same language as the user.
- If a Control Center tool fails, inspect `${GOOSE_PATH_ROOT}/logs/mcp/control_center.log` before retrying.
