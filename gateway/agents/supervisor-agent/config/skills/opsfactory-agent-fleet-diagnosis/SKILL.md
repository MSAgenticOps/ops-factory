---
name: "OpsFactory Agent Fleet Diagnosis"
description: "Diagnose OpsFactory agent configuration and runtime fleet issues such as missing skills, wrong providers, idle agents, and unhealthy instance counts."
---

# OpsFactory Agent Fleet Diagnosis

## When to Use

Use this skill when the user asks about OpsFactory agents, agent status, provider/model configuration, installed skills, running instances, or why an agent is not responding.

## Workflow

1. Call `control_center__get_agents_status` to inspect configured agents, provider, model, skills, and running instance counts.
2. Call `control_center__get_platform_status` to relate agent state to gateway/runtime health.
3. Call `control_center__get_realtime_metrics` to check per-agent traffic, errors, latency, TTFT, and active sessions.
4. If a specific agent is affected, compare its config and runtime metrics against the rest of the fleet.
5. If evidence points to a service problem, use `control_center__get_service_status` and logs for the relevant service.

## Findings

Look for missing or unexpected skills, provider/model mismatch, zero instances for expected agents, high per-agent latency or TTFT, repeated errors, and gateway/runtime issues affecting multiple agents.

## Output

Summarize Fleet State, Affected Agents, Evidence, Recommendations, and Follow-up Checks.

## Rules

- Base findings only on tool data.
- Do not assume an agent is broken only because it has no active traffic.
- Use the same language as the user.
