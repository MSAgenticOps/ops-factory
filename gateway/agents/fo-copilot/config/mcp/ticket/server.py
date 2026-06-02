"""stdio entry point for the FO Copilot `ticket` MCP adapter.

Thin plumbing only: it advertises the tools from tools.py and routes calls to
dispatch(). All ticket logic lives in tools.py / store.py (which carry no `mcp`
dependency and are unit-tested directly).
"""

import asyncio
import json
import os
import sys
from datetime import datetime, timezone
from pathlib import Path

# Make tools.py / store.py importable when launched via a path such as
#   python config/mcp/ticket/server.py
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from mcp.server import Server
from mcp.server.stdio import stdio_server
from mcp import types

from tools import TOOLS, dispatch
from store import resolve_data_path

# TICKET_MCP_LOG is external (env) input; confine it to an allowed data area
# before creating files (G.FIO.02). Absent override → default under this dir.
_DEFAULT_LOG = Path(__file__).resolve().parent / ".data" / "server.log"
_LOG_ENV = os.environ.get("TICKET_MCP_LOG")
_LOG_PATH = resolve_data_path(_LOG_ENV, default=_DEFAULT_LOG) if _LOG_ENV else _DEFAULT_LOG


def _log(event: str, detail: dict | None = None) -> None:
    """Append a structured line to a log file. Never writes to stdout (reserved
    for the JSON-RPC stream) and swallows its own errors."""
    try:
        _LOG_PATH.parent.mkdir(parents=True, exist_ok=True, mode=0o700)
        line = json.dumps(
            {"time": datetime.now(timezone.utc).isoformat(), "event": event, **(detail or {})},
            ensure_ascii=False,
        )
        # Owner-only perms on first create (G.FIO.01); append thereafter.
        fd = os.open(_LOG_PATH, os.O_WRONLY | os.O_CREAT | os.O_APPEND, 0o600)
        with os.fdopen(fd, "a", encoding="utf-8") as fh:
            fh.write(line + "\n")
    except OSError as exc:
        # Logging is best-effort and must never break the JSON-RPC stream on
        # stdout; surface the failure on stderr (captured by goosed) rather than
        # swallowing it silently.
        sys.stderr.write(f"[ticket-mcp] log write failed: {exc}\n")


app = Server("ticket")


@app.list_tools()
async def handle_list_tools() -> list[types.Tool]:
    _log("list_tools", {"count": len(TOOLS)})
    return [
        types.Tool(name=t["name"], description=t["description"], inputSchema=t["inputSchema"])
        for t in TOOLS
    ]


@app.call_tool()
async def handle_call_tool(name: str, arguments: dict | None) -> list[types.TextContent]:
    args = arguments or {}
    _log("call_tool", {"tool": name, "args": list(args)})
    try:
        result = await dispatch(name, args)
    except Exception as exc:  # last-resort guard; dispatch already shapes known errors
        # Keep the internal detail in the server-side log only; never return the
        # raw exception text across the tool boundary (G.ERR.08).
        _log("call_tool_failed", {"tool": name, "error": repr(exc)})
        result = json.dumps(
            {"ok": False, "error": {
                "code": "TOOL_EXECUTION_FAILED",
                "message": "Tool execution failed unexpectedly.",
                "hint": "Check the server log for details.",
            }},
            ensure_ascii=False,
        )
    return [types.TextContent(type="text", text=result)]


async def main() -> None:
    async with stdio_server() as (read_stream, write_stream):
        _log("server_started", {"pid": os.getpid(), "log": str(_LOG_PATH)})
        await app.run(read_stream, write_stream, app.create_initialization_options())


if __name__ == "__main__":
    asyncio.run(main())
