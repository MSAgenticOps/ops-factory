---
name: "OpsFactory Service Recovery"
description: "Investigate and recover managed OpsFactory services through Control Center status, logs, config, events, and service action tools."
---

# OpsFactory Service Recovery

## When to Use

Use this skill when the user asks why a managed service is down, unhealthy, unreachable, or asks to start, stop, or restart a service.

Known service ids include `gateway`, `knowledge-service`, and `business-intelligence`.

## Workflow

1. Identify the target service id. If unclear, ask one concise question.
2. Call `control_center__get_service_status` for the target service.
3. Call `control_center__list_events` to check recent service actions and health transitions.
4. If the service is unhealthy or the reason is unclear, call `control_center__read_service_logs` with enough recent lines to identify errors.
5. If configuration may be involved, call `control_center__read_service_config`.
6. For explicit action requests, call `control_center__start_service`, `control_center__stop_service`, or `control_center__restart_service`, then verify with `control_center__get_service_status`.

## Output

Report Current State, Evidence, Likely Cause, Action Taken or Recommended Action, and Verification.

## Rules

- Do not perform service actions unless the user requested that action or clearly approved it.
- Quote only short log snippets needed as evidence.
- If a tool fails, inspect `${GOOSE_PATH_ROOT}/logs/mcp/control_center.log`.
- Use the same language as the user.
