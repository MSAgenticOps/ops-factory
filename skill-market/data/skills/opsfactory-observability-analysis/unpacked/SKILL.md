---
name: "OpsFactory Observability Analysis"
description: "Analyze OpsFactory latency, errors, traces, observation breakdown, cost, and realtime performance using Control Center observability tools."
---

# OpsFactory Observability Analysis

## When to Use

Use this skill when the user asks about OpsFactory traces, latency, P95, TTFT, token throughput, errors, cost, Langfuse data, or recent performance changes.

## Workflow

1. Choose the requested time range; default to 24 hours if unspecified.
2. Call `control_center__get_observability_data` with the selected `hours`.
3. Call `control_center__get_realtime_metrics` for current runtime metrics that do not depend on Langfuse.
4. If observability data is unavailable, state that clearly and continue with realtime metrics only.
5. Compare errors, latency, TTFT, tokens/sec, trace volume, and cost signals from the returned data.

## Output

Use sections: Time Range, Performance Summary, Error Signals, Cost Signals, Suspected Hotspots, Recommendations.

## Rules

- Do not invent missing Langfuse data.
- Do not label a trend without returned historical data to support it.
- Include the queried time range in the answer.
- Use the same language as the user.
