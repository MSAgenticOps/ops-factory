#!/usr/bin/env python3
"""`delegation` MCP extension (stdio FastMCP).

Exposes a single tool, ``call_agent``, that delegates a task to another predefined agent via the gateway A2A endpoint
and returns its result. This is a thin relay — all orchestration (spawn, sub-session, timeout, cancel, nesting guard)
lives in the gateway. Enabled in every registered agent so any agent can be @-mentioned.
"""
from __future__ import annotations

from mcp.server.fastmcp import Context, FastMCP

from core import delegate, log

mcp = FastMCP(
    "delegation",
    instructions=(
        "Delegate a task to another predefined agent and return its result. Use call_agent when the user asks to "
        "hand work to a specific @agent; relay the user's verbatim request as the message."
    ),
)


@mcp.tool(
    name="call_agent",
    description=(
        "Delegate a task to another predefined agent and return its result. The target runs as a one-off sub-run; "
        "you will receive its final result to synthesize into your reply. Pass the target agent id as `target` and "
        "the verbatim task (any context the target needs) as `message`."
    ),
)
async def call_agent(target: str, message: str, ctx: Context) -> str:
    """Delegate ``message`` to agent ``target`` and return its final result."""
    return await delegate(ctx, target, message)


if __name__ == "__main__":
    log("server_started", transport="stdio")
    mcp.run(transport="stdio")
