#!/usr/bin/env python3
"""`delegation` MCP extension (stdio FastMCP).

Exposes two tools: ``call_agent`` delegates a task to another predefined agent via the gateway A2A endpoint and
returns its result; ``list_available_agents`` returns the delegatable agent roster (exact ids + names) so the model
targets the right id instead of guessing. Both are thin relays — all orchestration (spawn, sub-session, timeout,
cancel, nesting guard) lives in the gateway. Enabled in every registered agent so any agent can be @-mentioned.
"""
from __future__ import annotations

from mcp.server.fastmcp import Context, FastMCP

from core import delegate, list_agents, log

mcp = FastMCP(
    "delegation",
    instructions=(
        "Delegate a task to another predefined agent and return its result. Use call_agent when the user asks to "
        "hand work to a specific agent; relay the user's verbatim request as the message. If you are unsure of the "
        "target's exact id, call list_available_agents first and use the exact id rather than guessing."
    ),
)


@mcp.tool(
    name="call_agent",
    description=(
        "Delegate a task to another predefined agent and return its result. The target runs as a one-off sub-run; "
        "you will receive its final result to synthesize into your reply. Pass the target agent id as `target` and "
        "the verbatim task (any context the target needs) as `message`. If unsure of the exact id, call "
        "list_available_agents first."
    ),
)
async def call_agent(target: str, message: str, ctx: Context) -> str:
    """Delegate ``message`` to agent ``target`` and return its final result."""
    return await delegate(ctx, target, message)


@mcp.tool(
    name="list_available_agents",
    description=(
        "List the agents you can delegate to, with their exact ids and names. Call this before call_agent whenever "
        "you are unsure of the target's exact id — for example when the user names an agent loosely like "
        "'supervisor agent' — then pass the exact id as call_agent's `target` rather than guessing."
    ),
)
async def list_available_agents() -> str:
    """Return the delegatable agent roster (exact ids + names) so the model can pick a valid `target`."""
    return await list_agents()


if __name__ == "__main__":
    log("server_started", transport="stdio")
    mcp.run(transport="stdio")
