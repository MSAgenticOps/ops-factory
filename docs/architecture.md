# Ops-Factory 技术方案

## 1. 系统概述

Ops-Factory 是一个多 AI Agent 编排平台，基于 [Goose](https://github.com/block/goose) 开源 AI Agent 框架构建。平台通过统一的 API Gateway 管理多个 Goosed 实例，提供 Web 界面和 SDK 进行交互，支持多 Agent 并行运行、会话管理、文件产出浏览等能力。

---

## 2. 系统架构总览

```
┌─────────────────────────────────────────────────────────┐
│                      用户浏览器                          │
└────────────────────────┬────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│              Web App (React + Vite)                      │
│              端口: 5173                                  │
│  页面: 聊天 / 历史 / 文件 / Agent列表 / Agent配置        │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP (x-secret-key 认证)
                         ▼
┌─────────────────────────────────────────────────────────┐
│                  Gateway (Node.js)                       │
│                  端口: 3000                              │
│                                                         │
│  ┌──────────┐ ┌──────────┐ ┌───────────┐ ┌───────────┐ │
│  │ 反向代理  │ │ 进程管理  │ │ 文件服务   │ │ 会话聚合  │ │
│  └──────────┘ └──────────┘ └───────────┘ └───────────┘ │
└───┬──────────────┬──────────────┬──────────────┬────────┘
    │              │              │              │
    ▼              ▼              ▼              ▼
┌────────┐   ┌────────┐   ┌────────┐   ┌────────────────┐
│Goosed  │   │Goosed  │   │Goosed  │   │  OnlyOffice    │
│Agent-1 │   │Agent-2 │   │Agent-3 │   │  (Docker)      │
│:3001   │   │:3002   │   │:3003   │   │  :8080         │
└────────┘   └────────┘   └────────┘   └────────────────┘
```

---

## 3. 组件详细说明

### 3.1 Gateway（API 网关 & 进程管理器）

**职责：** 系统的核心枢纽，承担 API 网关、进程管理、文件服务、会话聚合四重职能。

**技术栈：** Node.js + TypeScript，使用 `http-proxy` 做反向代理。

**核心功能：**

| 功能模块 | 说明 |
|---------|------|
| 反向代理 | 将 `/agents/{agentId}/*` 的请求路由到对应 Goosed 实例端口 |
| 进程管理 | 通过 `child_process.spawn()` 启动/停止各 Goosed 子进程，传递环境变量 |
| 健康检查 | 轮询各 Goosed 实例的 `/status` 端口，确认启动就绪 |
| 文件服务 | 提供 Agent 产出文件的 HTTP 访问，支持路径安全校验（防目录遍历） |
| 会话聚合 | 跨多个 Agent 汇总所有 session 列表，统一查询入口 |
| 认证鉴权 | 校验 `x-secret-key` 请求头 |
| SSE 透传 | 将 Goosed 的 SSE 流式响应透传给前端，超时时间 5 分钟 |

**主要 API 路由：**

| 方法 | 路径 | 用途 |
|------|------|------|
| GET | `/status` | 网关健康检查 |
| GET | `/agents` | 获取所有 Agent 列表及运行状态 |
| GET/PUT | `/agents/:id/config` | 读取/更新 Agent 配置 |
| GET | `/agents/:id/files` | 获取 Agent 产出文件列表 |
| GET | `/agents/:id/files/*` | 下载/预览具体文件 |
| GET | `/sessions` | 聚合查询所有 Agent 的 session |
| POST/GET/DELETE | `/agents/:id/mcp` | MCP 扩展热加载管理 |
| ALL | `/agents/:id/*` | 反向代理至 Goosed 实例 |

---

### 3.2 Web App（前端应用）

**职责：** 为用户提供可视化的 Agent 交互界面。

**技术栈：** React 18 + Vite + TypeScript + React Router

**页面结构：**

| 路由 | 页面 | 功能 |
|------|------|------|
| `/` | 首页 | 着陆页 |
| `/chat` | 聊天 | 与 Agent 进行流式对话，支持 Markdown/代码块渲染 |
| `/history` | 历史 | 查看和恢复过往 session |
| `/files` | 文件 | 浏览 Agent 产出的文件，支持预览 |
| `/agents` | Agent 列表 | 查看各 Agent 运行状态 |
| `/agents/:id/configure` | Agent 配置 | 修改 Agent 运行参数 |

**关键模块：**

- **GoosedContext** — 管理 Gateway 连接和客户端实例池
- **useChat Hook** — 处理 SSE 流式响应、消息缓冲和渲染
- **PreviewContext** — 文件预览浮层管理
- **FilePreview** — 支持 HTML、Markdown、PDF 以及 Office 文档（通过 OnlyOffice）预览

**配置：**

通过环境变量连接 Gateway：

```
VITE_GATEWAY_URL=http://127.0.0.1:3000
VITE_GATEWAY_SECRET_KEY=test
```

---

### 3.3 Goosed（AI Agent 运行时）

**职责：** 单个 AI Agent 的运行时进程，负责 LLM 调用、工具执行、会话管理。

**技术栈：** Rust (async/tokio)，由上游 Goose 开源项目提供二进制。

**核心能力：**

| 能力 | 说明 |
|------|------|
| LLM 对话 | 支持 OpenAI / Anthropic / DeepSeek / Ollama 等多种 Provider |
| 工具调用 | 通过 MCP (Model Context Protocol) 调用外部工具 |
| 会话持久化 | SQLite 存储对话历史、token 用量、扩展状态 |
| 上下文压缩 | token 超过阈值时自动压缩历史消息 |
| 权限控制 | 工具调用分为 Safe / Standard / Dangerous 三级 |
| 扩展管理 | 加载/卸载内置和外部 MCP 扩展 |

**通信协议：**

- **对外（被 Gateway 调用）：** HTTP REST + SSE 流式
- **对内（调用扩展）：** JSON-RPC over stdio（子进程）或 HTTP Streaming

**主要端点（被 Gateway 代理）：**

| 方法 | 路径 | 用途 |
|------|------|------|
| POST | `/agent/start` | 创建新 session |
| POST | `/agent/resume` | 恢复已有 session |
| POST | `/reply` | 发送消息并获取 SSE 流式响应 |
| GET | `/agent/tools` | 列出可用工具 |
| POST | `/agent/call_tool` | 执行指定工具 |
| POST | `/agent/stop` | 停止当前 session |

**SSE 事件类型：**

| 事件 | 说明 |
|------|------|
| `Message` | Agent 回复内容（文本、工具调用、工具结果） |
| `Finish` | 回复结束，包含 token 统计 |
| `Error` | 错误信息 |
| `Notification` | MCP 扩展通知 |
| `Ping` | 心跳保活 |

---

### 3.4 File Server（文件服务）

**职责：** 提供 Agent 产出文件的 HTTP 访问能力，内嵌于 Gateway 中。

**文件存储结构：**

```
agents/{agent-id}/artifacts/
├── report.md
├── analysis.xlsx
├── output/
│   ├── chart.png
│   └── data.csv
└── ...
```

**特性：**

- 递归扫描 Agent 的 `artifacts/` 目录
- 按修改时间倒序排列
- 自动跳过 `node_modules`、`.git`、`__pycache__` 等目录
- 基于文件扩展名自动设置 MIME 类型
- 路径遍历防护（canonical path 校验）
- 支持查询参数 `?key=` 认证（便于 OnlyOffice 回调拉取文件）

---

### 3.5 OnlyOffice（Office 文档预览服务）

**职责：** 提供 DOCX / XLSX / PPTX 等 Office 文档的在线预览与编辑能力。

**技术栈：** Docker 容器化部署，端口 8080。

**集成方式：**

```
浏览器 → OnlyOffice Editor (iframe)
              │
              ▼ (通过 fileBaseUrl 回调拉取文件)
          Gateway File Server
```

- Web App 通过 iframe 嵌入 OnlyOffice 编辑器
- OnlyOffice 通过 `fileBaseUrl`（指向 Gateway）拉取原始文件
- 本地开发时 JWT 验证关闭，允许私有 IP 访问

---

### 3.6 TypeScript SDK

**职责：** 提供编程接入 Gateway 的 TypeScript 客户端，Web App 内部使用。

**核心接口：**

| 方法 | 功能 |
|------|------|
| `status()` | 检查 Agent 健康状态 |
| `startSession(workingDir)` | 创建新会话 |
| `resumeSession(sessionId)` | 恢复会话 |
| `listSessions()` | 列出所有会话 |
| `sendMessage(sessionId, text)` | 发送消息，返回 SSE AsyncGenerator |
| `chat(sessionId, text)` | 简化版对话（等待完整回复） |
| `getTools(sessionId)` | 获取可用工具列表 |
| `callTool(sessionId, name, args)` | 调用工具 |

**错误类型：**

- `GoosedAuthError` (401) — 认证失败
- `GoosedNotFoundError` (404) — 资源不存在
- `GoosedAgentNotInitializedError` (424) — Agent 未初始化
- `GoosedConnectionError` — 连接失败
- `GoosedServerError` (500+) — 服务端错误

---

## 4. 运行态数据流

### 4.1 用户对话流（核心数据流）

```
用户输入消息
    │
    ▼
Web App (useChat Hook)
    │ POST /agents/{agentId}/reply
    │ Headers: x-secret-key, Content-Type
    │ Body: { session_id, message: [{ role: "user", content: [...] }] }
    │
    ▼
Gateway (反向代理)
    │ 转发请求到 Goosed 实例端口
    │ 设置 SSE 超时: 5 分钟
    │
    ▼
Goosed 实例
    │ 1. 加载 session 上下文（SQLite）
    │ 2. 拼接系统提示词 + 历史消息 + 用户输入
    │ 3. 调用 LLM Provider (OpenAI / DeepSeek / ...)
    │ 4. 若 LLM 请求工具调用 → 执行 MCP 工具 → 结果回传 LLM
    │ 5. 持久化对话记录
    │
    ▼ SSE Stream
Gateway (透传)
    │
    ▼ SSE Stream
Web App
    │ 逐事件解析并渲染：
    │ - Message → 显示文本/代码/工具调用
    │ - Finish  → 结束加载状态
    │ - Error   → 显示错误
    ▼
用户看到流式回复
```

### 4.2 文件预览流

```
用户点击文件
    │
    ▼
Web App (FilePreview 组件)
    │ 判断文件类型
    │
    ├─ HTML/MD/PDF/图片 → GET /agents/{agentId}/files/{path}
    │                        → Gateway File Server 直接返回
    │
    └─ Office 文档 → 加载 OnlyOffice iframe
                       │
                       ▼
                   OnlyOffice Editor
                       │ 通过 fileBaseUrl 回调
                       ▼
                   Gateway File Server
                       │ 返回文件内容
                       ▼
                   OnlyOffice 渲染文档
```

### 4.3 Agent 生命周期

```
系统启动 (scripts/startup.sh)
    │
    ├─ 1. 启动 OnlyOffice Docker 容器
    ├─ 2. 等待 OnlyOffice 就绪
    ├─ 3. 启动 Gateway
    │      │
    │      ├─ 读取 agents.yaml 配置
    │      ├─ 为每个 Agent spawn goosed 子进程
    │      │   环境变量: GOOSE_PORT, GOOSE_HOST,
    │      │             GOOSE_SERVER__SECRET_KEY,
    │      │             GOOSE_PATH_ROOT
    │      ├─ 轮询 /status 等待各 Agent 就绪
    │      └─ 开始接受外部请求
    │
    └─ 4. 启动 Web App (Vite dev server)

系统关闭 (scripts/shutdown.sh)
    │
    ├─ Kill 端口 3000-3005 上的进程
    ├─ Kill 端口 5173 上的进程
    └─ 停止 OnlyOffice 容器
```

---

## 5. 配置体系

### 5.1 Gateway 配置

**文件：** `gateway/config/agents.yaml`

```yaml
officePreview:
  enabled: true
  onlyofficeUrl: "http://localhost:8080"
  fileBaseUrl: "http://host.docker.internal:3000"

agents:
  - id: universal-agent
    name: "Universal Agent"
    port: 3001

  - id: kb-agent
    name: "KB Agent"
    port: 3002

  - id: report-agent
    name: "Report Agent"
    port: 3003

  - id: contract-agent
    name: "Contract Agent"
    port: 3004
```

**环境变量：**

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `GATEWAY_PORT` | 3000 | Gateway 监听端口 |
| `GATEWAY_SECRET_KEY` | test | API 认证密钥 |

### 5.2 Agent 配置

**目录结构（每个 Agent）：**

```
agents/{agent-id}/
├── AGENTS.md              # Agent 角色描述 / 系统提示词
├── config/
│   ├── config.yaml        # Goose 运行配置
│   ├── secrets.yaml       # 密钥凭证（Provider API Key 等）
│   └── skills/            # 自定义技能目录
│       └── {skill-name}/
│           ├── SKILL.md   # 技能描述（YAML frontmatter）
│           └── skill.py   # 技能实现
├── artifacts/             # Agent 产出文件目录
├── data/                  # 持久化数据
└── state/                 # Session 状态
```

**Agent config.yaml 示例：**

```yaml
GOOSE_PROVIDER: openai
GOOSE_MODEL: deepseek-chat

extensions:
  skills:               # 加载自定义技能
    enabled: true
  todo:                 # 内置任务追踪
    enabled: true
  computercontroller:   # 系统交互工具
    enabled: true
  autovisualiser:       # 数据可视化
    enabled: true
  developer:            # 软件开发工具
    enabled: false
  code_execution:       # 沙箱化 JS 执行
    enabled: false
  memory:               # 用户偏好学习
    enabled: false
```

### 5.3 OnlyOffice 配置

**文件：** `gateway/config/onlyoffice.local.json`

- 关闭 JWT 认证（本地开发用）
- 允许私有 IP 访问（容器访问宿主机）

---

## 6. 工作流总结

### 6.1 开发/部署工作流

```
1. 编辑 gateway/config/agents.yaml → 定义 Agent 列表和端口
2. 为每个 Agent 配置 agents/{id}/config/ → 设置 Provider / Model / Extensions
3. 编写 agents/{id}/AGENTS.md → 定义 Agent 角色和提示词
4. 运行 scripts/startup.sh → 一键启动全部服务
5. 访问 http://localhost:5173 → 使用 Web UI
```

### 6.2 新增 Agent 工作流

```
1. 在 agents/ 下创建新目录，编写 AGENTS.md 和 config/
2. 在 gateway/config/agents.yaml 中注册（分配唯一 ID 和端口）
3. 重启 Gateway → 自动 spawn 新的 Goosed 实例
```

### 6.3 会话管理工作流

```
创建会话 → 多轮对话（含工具调用） → 查看产出文件 → 结束/恢复会话
    │                                                    │
    └── session 持久化在 Goosed 的 SQLite 中 ─────────────┘
```

---

## 7. 技术栈一览

| 层级 | 技术 |
|------|------|
| 前端 | React 18, Vite, TypeScript, React Router |
| 网关 | Node.js, http-proxy, YAML |
| SDK | TypeScript, Fetch API, SSE |
| Agent 运行时 | Rust (Goosed), async/tokio |
| 存储 | SQLite (会话), 文件系统 (产出物) |
| 文档预览 | OnlyOffice (Docker) |
| 部署 | Bash 脚本, Docker |
