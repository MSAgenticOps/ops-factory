# Feishu Doc MCP for kb-agent

This directory documents the Feishu MCP wiring used by kb-agent.

## Runtime registration

The extension is registered in:

- `agents/kb-agent/config/config.yaml`

Extension name: `feishu-doc`

## Knowledge base

- Name: `AgenticOps`
- `space_id`: `7599469732730850247`

## Tools

| Tool | Usage |
|------|-------|
| `wiki_v1_node_search` | 搜索知识库文档，必须传 `space_id: "7599469732730850247"`，静态 token 模式不要传 `useUAT` |
| `docs_v1_content_get` | 获取文档 Markdown 内容，传 `doc_token`（来自搜索结果的 `obj_token`），静态 token 模式不要传 `useUAT` |

## Authentication

Uses static **user_access_token** via `USER_ACCESS_TOKEN` environment variable.

Required OAuth scopes:
- `wiki:wiki:readonly` (or `wiki:wiki`)
- `search:docs:read`
- `docs:document.content:read`
- `drive:drive.search:readonly`

### Prerequisites

Required secrets in `agents/kb-agent/config/secrets.yaml`:

- `APP_ID`
- `APP_SECRET`
- `USER_ACCESS_TOKEN` (manual refresh every ~2 hours)

## Usage policy

- Intended for RAG only (search + read).
- Data boundary: only the `AgenticOps` knowledge base.
- Cite source title and URL in final answers.
- Prefer summary + quote snippets, avoid full raw dump.
- Disallowed: create, update, delete, share docs; cross-tenant operations.

## Local verification

1. Update `USER_ACCESS_TOKEN` in `agents/kb-agent/config/secrets.yaml`.
2. Start stack:
   - `OFFICE_PREVIEW_ENABLED=false ./scripts/startup.sh`
3. Check mcp list:
   - `curl -H 'x-secret-key: test' http://127.0.0.1:3000/agents/kb-agent/mcp`
4. In kb-agent chat, ask:
   - `reportAgent 包含哪些报表？`
