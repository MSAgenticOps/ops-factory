#!/usr/bin/env python3
"""Core logic for the `delegation` A2A MCP extension.

The pure helpers (identity resolution, session-id reading, header building, SSE-frame classification) import only the
standard library so they are unit-testable without `mcp`/`httpx` installed. The async `delegate` driver imports
`httpx` / `httpx_sse` lazily.

Flow: the model calls `call_agent(target, message)` -> this relays to the gateway A2A endpoint over SSE, turns each
`a2a_progress` frame into an MCP logging notification (visible as the live status line), and returns the terminal
`a2a_result` as the tool result for the initiating agent to synthesize.
"""
from __future__ import annotations

import json
import os
import sys
import time
from pathlib import Path
from typing import Any, Optional

# Injected into every goosed process env by the gateway (InstanceManager.buildEnvironment); inherited by this stdio
# child. Defaults are only for local/test fallback.
GATEWAY_URL = os.environ.get("GATEWAY_URL", "http://127.0.0.1:8080").rstrip("/")
# The gateway auth secret (gateway.secret-key) is what AuthWebFilter validates on every request — it is the same
# credential the sop-executor extension uses (GATEWAY_SECRET_KEY), and the one callbacks to the gateway must present.
# GATEWAY_API_PASSWORD is a *downstream* credential (default empty, e.g. QOS_PASSWORD), kept only as a legacy fallback.
SECRET_KEY = os.environ.get("GATEWAY_SECRET_KEY") or os.environ.get("GATEWAY_API_PASSWORD", "")

# goose injects the current session id into every tool call's MCP _meta under this key (session_context.rs).
SESSION_ID_META_KEY = "agent-session-id"

PROGRESS_TYPE = "a2a_progress"
RESULT_TYPE = "a2a_result"


def log(event: str, **fields: Any) -> None:
    """Best-effort structured stderr log line (goose drains and captures stderr)."""
    try:
        payload = {"ts": round(time.time(), 3), "mcp": "delegation", "event": event, **fields}
        print(json.dumps(payload, ensure_ascii=False), file=sys.stderr, flush=True)
    except Exception:
        pass


def resolve_identity(cwd: str) -> tuple[Optional[str], Optional[str]]:
    """Derives ``(user_id, agent_id)`` from a goose runtime CWD of the form
    ``.../gateway/users/{userId}/agents/{agentId}/...``. Returns ``(None, None)`` if the path does not match.

    Anchors on the ``gateway/users`` pair (not a bare ``users`` segment) and takes the last such anchor, so an
    unrelated ``users`` dir earlier in an absolute deploy path (e.g. ``/home/users/svc/...``) cannot be mistaken for
    the runtime owner."""
    user_id: Optional[str] = None
    agent_id: Optional[str] = None
    parts = Path(cwd).parts
    for i in range(len(parts) - 1):
        if parts[i] == "gateway" and parts[i + 1] == "users":
            user_id = parts[i + 2] if i + 2 < len(parts) else None
            agent_id = parts[i + 4] if (i + 4 < len(parts) and parts[i + 3] == "agents") else None
    return user_id, agent_id


def read_session_id(meta: Any) -> Optional[str]:
    """Reads goose's injected session id from the MCP request ``_meta`` (key ``agent-session-id``). Handles a plain
    dict, a pydantic model with ``model_extra``, or direct attribute access."""
    if meta is None:
        return None
    if isinstance(meta, dict):
        value = meta.get(SESSION_ID_META_KEY)
        return str(value) if value else None
    extra = getattr(meta, "model_extra", None)
    if isinstance(extra, dict):
        value = extra.get(SESSION_ID_META_KEY)
        if value:
            return str(value)
    value = getattr(meta, SESSION_ID_META_KEY, None)
    return str(value) if value else None


def build_headers(secret: str, user_id: Optional[str], agent_id: Optional[str],
                  session_id: Optional[str]) -> dict:
    """Builds the gateway request headers. depth is NOT sent — the gateway derives it from the side-record."""
    headers = {"x-secret-key": secret or ""}
    if user_id:
        headers["x-user-id"] = user_id
    if agent_id:
        headers["x-a2a-origin"] = agent_id
    if session_id:
        headers["x-a2a-origin-session"] = session_id
    return headers


def is_terminal(frame: Any) -> bool:
    """Whether an SSE frame is the terminal ``a2a_result``."""
    return isinstance(frame, dict) and frame.get("type") == RESULT_TYPE


def progress_payload(frame: dict) -> dict:
    """Maps an ``a2a_progress`` frame to the MCP logging-notification data payload the frontend binds to a card."""
    return {
        "type": PROGRESS_TYPE,
        "target_agent": frame.get("target_agent"),
        "sub_session_id": frame.get("sub_session_id"),
        "kind": frame.get("kind"),
        "label": frame.get("label"),
        "step": frame.get("step"),
    }


def result_text(frame: dict) -> str:
    """Extracts the tool-result string from the terminal frame (annotating error / timeout for the model)."""
    status = frame.get("status")
    result = frame.get("result") or ""
    if status == "error":
        return f"[delegated agent error] {frame.get('error') or 'unknown error'}"
    if status == "timeout":
        return "[delegated agent timed out]" + (f" Partial result: {result}" if result else "")
    return result


def error_detail(body: str) -> Optional[str]:
    """Extracts a human message from a gateway JSON error body (Spring/ResponseStatusException shape), or None."""
    try:
        data = json.loads(body)
    except (json.JSONDecodeError, ValueError):
        return None
    if isinstance(data, dict):
        for key in ("message", "error", "detail"):
            value = data.get(key)
            if isinstance(value, str) and value.strip():
                return value.strip()
    return None


def _meta_from_ctx(ctx: Any) -> Any:
    try:
        return ctx.request_context.meta
    except Exception:
        return None


async def _send_progress(ctx: Any, payload: dict) -> None:
    try:
        await ctx.request_context.session.send_log_message(level="info", data=payload, logger=PROGRESS_TYPE)
    except Exception as exc:  # noqa: BLE001 - progress is best-effort, never fail the run on a dropped notification
        log("progress_send_failed", error=str(exc))


async def delegate(ctx: Any, target: str, message: str) -> str:
    """Drives one delegated run: opens the gateway A2A SSE stream, relays progress as MCP notifications, and returns
    the terminal result. On cancellation the ``async with`` context managers close the SSE connection, which the
    gateway observes as a caller disconnect and uses to cancel the target."""
    user_id, agent_id = resolve_identity(os.getcwd())
    session_id = read_session_id(_meta_from_ctx(ctx))
    if not user_id:
        log("identity_unresolved", cwd=os.getcwd())
        return "[delegation error] could not resolve caller identity from runtime directory"

    headers = build_headers(SECRET_KEY, user_id, agent_id, session_id)
    url = f"{GATEWAY_URL}/api/gateway/agents/{target}/a2a"
    log("delegate_start", target=target, user_id=user_id, agent_id=agent_id, session_id=session_id)

    import httpx
    from httpx_sse import aconnect_sse

    # Loopback gateway uses a self-signed cert when TLS is on; skip verification (equivalent to Node's
    # NODE_TLS_REJECT_UNAUTHORIZED=0 that the gateway sets for JS extensions).
    verify = not GATEWAY_URL.startswith("https")
    timeout = httpx.Timeout(None, connect=10.0)
    result = ""
    try:
        async with httpx.AsyncClient(verify=verify, timeout=timeout) as client:
            async with aconnect_sse(client, "POST", url, json={"message": message}, headers=headers) as source:
                # Non-2xx (e.g. 409 nesting guard, 400 validation) are JSON, not SSE — surface the real message
                # instead of letting aiter_sse raise an opaque content-type error.
                if source.response.status_code >= 400:
                    body = (await source.response.aread()).decode("utf-8", "replace").strip()
                    status = source.response.status_code
                    log("delegate_http_error", target=target, status=status, body=body[:200])
                    return f"[delegation error] gateway returned {status}: {error_detail(body) or body[:200]}"
                async for sse in source.aiter_sse():
                    if not sse.data:
                        continue
                    try:
                        frame = json.loads(sse.data)
                    except json.JSONDecodeError:
                        continue
                    if is_terminal(frame):
                        result = result_text(frame)
                        break
                    if isinstance(frame, dict):
                        await _send_progress(ctx, progress_payload(frame))
    except Exception as exc:  # noqa: BLE001 - surface failures as a tool error so the model can react
        log("delegate_failed", target=target, error=str(exc))
        return f"[delegation error] {exc}"

    log("delegate_done", target=target, result_len=len(result))
    return result
