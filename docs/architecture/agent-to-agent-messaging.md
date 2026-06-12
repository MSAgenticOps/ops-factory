# Agent 间消息（@mention）与 A2A 调用架构

> 状态：实现完成（已对齐全部讨论结论 + 代码核验）。
> 策略定位：**delegation 仅 fo-copilot 启用** —— fo-copilot 是「数字人」级编排者，是唯一能发起 @ 调用的 agent；其它 agent 是「工具」级，可作为被调目标，但不发起调用（产品约定，非技术强制）。
> 范围：在 `gateway` 层新增「一个 Agent 在对话中 @ 另一个 Agent，由后者执行并把结果回交前者综合」的能力。
> 不可动摇的前提：**goose / goosed 零改动**，A2A 全部以 goosed 的外部 MCP 调用 + 网关编排实现。

## 1. 目标

让用户在 **fo-copilot**（数字人级编排者）的对话框输入「`@agentB 执行 XXX`」时，fo-copilot 能把任务委派给一个**已预定义的工具型 AgentB**；AgentB 启动一次独立运行，过程进度实时、轻量地回流到 fo-copilot 的会话，运行结束后把结果交还 fo-copilot，由它综合成最终答复。

> 约定：只有 fo-copilot 发起调用；任何已注册 agent 都可作为被调目标。为通用起见，下文仍以「AgentA」泛指发起方（即 fo-copilot）、「AgentB」泛指被调目标。

提供：

- 在 AgentA 会话内，以 `@mention` 触发对另一个已注册 Agent 的调用
- AgentB 运行过程的**实时、低打扰**进度呈现（不污染 AgentA 的模型上下文）
- 运行结果回交 AgentA 综合
- 多 @ 的串行支持（一期），真并行（二期）

不提供（一期）：

- AgentB 再向下 @ 第三个 Agent（**禁止嵌套**）
- 跨用户调用（A2A 始终在同一 `userId` 身份下）
- 真正的并发 fan-out（放到二期）

## 2. 设计原则与约束

1. **goose 零改动**：goosed 是外部二进制，只能遵循其既有协议。所有新增逻辑落在 `gateway`（Java）、一个 stdio MCP 扩展（Python）、`web-app`（前端）三处。
2. **网关是唯一枢纽**：所有 Agent 运行时由网关按 `(agentId, userId)` 托管。A2A 的实例拉起、子会话、超时、取消、防环全部在网关收口。
3. **复用现有范式（仅骨架）**：A2A 的 MCP 扩展沿用 `control_center` 的 **stdio FastMCP 骨架** 与「config.yaml 声明 + `.python-deps` 供货 + 读 env/secret」的约定。**但要说清边界**：`control_center` 是同步 `urllib` 请求/响应、调的是另一个服务，**没有 SSE / 没有 `ctx` 通知 / 没有取消**。本扩展需要的「消费网关 SSE、`ctx.send_log_message` 回流、响应取消」三件能力在本仓库**零先例**，是净新增的 async 组件（详见 §5.1）。最接近「回调网关、带密码、自签 TLS」的现成参考是 `qos-agent/system-health-analysis`（但它也仍是同步、无 SSE）。
4. **展示通道与上下文通道分离**：AgentB 的过程通过 MCP **通知**回流（仅用于前端展示），**不进** AgentA 的对话消息、不进其 LLM 上下文；只有最终结果作为工具返回值进入 AgentA 的上下文。

## 3. 核心概念与语义

**身份 vs 角色**（关键区分）：

| | AgentB 的身份 | AgentB 在一次 @ 调用中的角色 |
| --- | --- | --- |
| 是什么 | 在 `config.yaml` 注册、与 AgentA 平级的一等 Agent | 一次性的子运行（临时工 / subagent） |
| 载体 | 自己的 `gateway/agents/<id>/`（AGENTS.md + config + 自有 provider/工具） | 在 B 实例上新建的**一次性子会话** |
| 生命周期 | 长期存在、可复用、也可被用户直接对话 | 即用即弃 |

- **信封 = 用户原话**：AgentA 委派给 AgentB 的任务内容，就是用户那条 @ 消息的原文（去除 @ 标记），**不自动拼接 AgentA 的历史上下文**（避免长对话噪音）。AgentB 的 persona / 工具来自它自己的定义。需要更多上下文时由用户在 @ 消息里自行补充。
- **结果回交（两条路径，见 §5.3）**：
  - **模型路径**（模型自行调用 `call_agent`，覆盖自然语言/未被前端拦截的提及）：AgentB 的结果作为工具返回值交给 AgentA 的模型综合，并随该轮工具调用**持久化在 AgentA 的 goosed 会话**里（用户与 AgentA 一个声音对话）。
  - **确定性 `@<已知 id>` 路径**（前端直驱）：前端合成 `call_agent` 卡片并**逐字**展示 AgentB 的结果（不经 AgentA 模型综合）；该轮为前端态（`agentVisible:false`），**不写入 AgentA 的 goosed 会话**——刷新后顶层会话不含这轮。
  - 无论哪条路径，AgentB 的子运行都在 B 侧持久化并登记侧记录，**权威留痕在历史「Agent 调用」页**（§5.4）。AgentB 的过程在折叠的轻量状态行里实时可见。
- **禁止嵌套（机制见 §5.2，已按代码核验修正）**：当 B 以子运行身份执行时，不得再发起 A2A。goose 进程内的 `SessionType::SubAgent` 检查跨 goosed 进程不生效，因此**在网关强制**。注意：扩展**无法自报 depth**（B 实例按 `(agentId,userId)` 复用、跨会话共享一个 goosed 进程，env 在 spawn 时固定、无 per-session 维度）。因此 depth **不由扩展携带，而由网关从子会话侧记录派生**：扩展诚实上报「自己当前的 session id」，网关查「该 session 本身是否已是登记在册的 A2A 子会话」来判定是否嵌套，并沿父链做环检测。B 作为顶层 Agent 被用户直接对话时不受此限。

## 4. 整体架构与调用链路

```text
+-----------+    @agentB 执行XXX      +-------------------+
| web-app   | ----------------------> | gateway           |
| (AgentA   | <---------------------- | (AgentA 实例代理) |
|  会话UI)  |   SSE: a2a_progress /    +---------+---------+
+-----------+   tool result                     |
                                                 | (代理 reply / events)
                                                 v
                                     +-----------------------+
                                     | goosed (AgentA)       |  ← 零改动
                                     |  调用 MCP 工具         |
                                     |  call_agent(agentB,…) |
                                     +-----------+-----------+
                                                 | stdio (MCP)
                                                 v
                                     +-----------------------+
                                     | delegation MCP 扩展    |  ← 新增, 净新增 async
                                     | (stdio FastMCP, py)    |     thin relay
                                     +-----------+-----------+
                                                 | HTTP: fetch(${GATEWAY_URL}/.../a2a)
                                                 v   (SSE 回流)
                                     +-----------------------+
                                     | gateway A2A 端点       |  ← 新增, 编排收口
                                     |  getOrSpawn(B,user)    |
                                     |  新建 B 子会话          |
                                     |  驱动 reply + 消费events|
                                     |  精简/超时/取消/防环    |
                                     +-----------+-----------+
                                                 | 代理 goosed 协议
                                                 v
                                     +-----------------------+
                                     | goosed (AgentB)       |  ← 零改动, 真实实例
                                     +-----------------------+
```

**一次同步调用的时序（模型路径——模型自行调用 `call_agent`；`@<已知 agentId>` 的提及由前端直驱、绕过本序列，见 §5.3）：**

1. 用户在 AgentA 输入 `@agentB 执行 XXX`。前端 @ 自动补全（数据源 `GET /api/gateway/agents`）产出**规范化的 `agentId`**；消息作为普通用户消息发给 AgentA（系统提示规则把规范化后的 `@<id>` 映射到 `call_agent`，详见 §5.3）。
2. AgentA 的 goosed 调用 MCP 工具 `call_agent(target="agentB", message="执行 XXX")`。
3. `delegation` 扩展（stdio）收到调用，`fetch(${GATEWAY_URL}/api/gateway/agents/agentB/a2a, …)`，请求头携带：`x-secret-key`（网关鉴权密钥 `GATEWAY_SECRET_KEY` = `gateway.secret-key`，与前端/`sop-executor` 同源；**不是** `GATEWAY_API_PASSWORD`，后者是下游凭据、默认空）、`x-user-id` 与 `x-a2a-origin`（均**从扩展自身 CWD `gateway/users/{userId}/agents/{agentId}/` 解析**）、`x-a2a-origin-session`（**从本次工具调用的 MCP `_meta.agent-session-id` 读取**，即 AgentA 当前会话 id，见 §5.1 与附录）。**depth 不由扩展携带**。
4. 网关 A2A 端点：先做防嵌套判定（查 `x-a2a-origin-session` 是否已是 A2A 子会话，见 §5.2）→ `getOrSpawn(agentB, 同一 userId)` → 在 B 实例上新建一个**子会话** → 登记子会话侧记录 → `POST /sessions/{sub}/reply`（user_message = 原话）→ 订阅 `GET /sessions/{sub}/events`。
5. 网关消费 B 的 SSE，**精简后**以 SSE 形式回流给 `delegation` 扩展；扩展把每条精简事件转成 MCP **通知**（`ctx.session.send_log_message`，payload 见 §6）经 stdio 发给 AgentA 的 goosed。
6. goosed 在工具执行期间实时转发这些通知 → 网关代理 AgentA 的 SSE → 前端按通知帧的 `request_id`（= 本次 `call_agent` 工具调用 id）绑定到 `call_agent` 卡片、就地更新状态行。
7. B 运行结束，网关把最终结果作为 A2A 端点的终止帧返回 → `delegation` 扩展作为 `call_agent` 的工具返回值返回 → AgentA 的模型综合 → 回复用户。

## 5. 组件设计

### 5.1 `delegation` MCP 扩展（stdio FastMCP）

> 命名（遵循 MCP 最佳实践）：扩展名 `delegation`（小写、描述能力、稳定、避免用 `a2a` 这种缩写做模型可见名），单一动作工具 `call_agent`（模型可见为 `delegation__call_agent`）；不复用 goose 内部既有的 `delegate` 名以免混淆。`a2a` 仅用于网关 HTTP 端点。

在 **fo-copilot** 的 `config/config.yaml` 的 `extensions` 下声明。**仅 fo-copilot 启用**——它是唯一能发起 @ 调用的 agent；其它 agent 不发起调用，但都可作为被 @ 的目标（被调目标不需要本扩展，网关直接驱动其子会话）：

```yaml
extensions:
  delegation:
    enabled: true
    type: stdio
    name: delegation
    # 用自带 .venv 的 python（仿 ticket 扩展），不要用 `cmd: python3`：
    # goosed 以最小 PATH 启动，其 python3 可能是过老的系统 python（实测 /usr/bin/python3=3.9，低于 mcp 要求的 3.10），
    # 且与构建依赖时的 python ABI 不一致（pydantic_core 的原生 .so 版本不匹配）。.venv 自带匹配的 python + 依赖，免疫 PATH 漂移。
    cmd: config/mcp/delegation/.venv/bin/python
    args:
      - config/mcp/delegation/server.py
    # 建议把 A2A 总超时设大，作为 goose 扩展超时之下的钝兜底（见 §8）
    timeout: 1200
```

> `GATEWAY_URL` / `GATEWAY_SECRET_KEY` 由 `InstanceManager.buildEnvironment()` 自动注入 goosed 进程环境，stdio 子进程继承，无需在 `envs:` / `env_keys:` 里显式声明。`GATEWAY_SECRET_KEY` 取自 `properties.getSecretKey()`（= `gateway.secret-key`），即 `AuthWebFilter` 校验的同一密钥。

**代码归属**：delegation 是 fo-copilot 独有能力，server.py 放在 fo-copilot 自己的 `gateway/agents/fo-copilot/config/mcp/delegation/`（与其它 agent 的 MCP 目录一致，无共享/symlink 机制）。依赖用 **`.venv`** 供货（仿 `ticket`/`local-tiny-tools`），在 `gateway/scripts/ctl.sh` 新增一个 build wrapper（用 `build_python_mcp` 而非 `check_python_mcp`，指向 fo-copilot 的 delegation 目录），由它创建 `.venv` 并 `pip install -r requirements.txt`；`requirements.txt` 含 `mcp` + `httpx` + `httpx-sse`。**关键：必须用 .venv，不能用 `.python-deps` + `cmd: python3`** —— goosed 以最小 PATH 启动，`python3` 可能是过老的系统 python（mcp 需 ≥3.10）且与构建依赖的 python ABI 不一致，会以 `ModuleNotFoundError: No module named 'pydantic_core._pydantic_core'` 加载失败。

**工具定义 `call_agent`：**

```jsonc
{
  "name": "call_agent",
  "description": "Delegate a task to another predefined agent and return its result. The target runs as a one-off sub-run; you will receive its final result to synthesize.",
  "inputSchema": {
    "type": "object",
    "properties": {
      "target":  { "type": "string", "description": "Target agent id, e.g. 'agentB'." },
      "message": { "type": "string", "description": "Verbatim task for the target. Include any context the target needs." }
    },
    "required": ["target", "message"]
  }
}
```

> 工具实现签名需带 `ctx: Context`，以便从 `ctx.request_context.meta` 读取 goose 注入的 `agent-session-id`（见下「身份与会话上下文」）。

**身份与会话上下文（已核验 goose 源码）：**

- `x-user-id` / `x-a2a-origin`（发起 agentId）：从扩展 CWD `gateway/users/{userId}/agents/{agentId}/` 解析即可，无需 goose 配合。
- `x-a2a-origin-session`（AgentA 当前会话 id）：goose 把当前 session id 注入**每次工具调用**的 MCP `_meta`，键名 `agent-session-id`（`goose/crates/goose/src/session_context.rs:3`；注入逻辑 `mcp_client.rs:740-772`）。Python SDK 的 `RequestParams.Meta` 是 `extra="allow"`，故工具体内可经 `ctx.request_context.meta`（取 `model_extra["agent-session-id"]`，键含连字符）读到。这是 depth 守卫与父会话归因的基础。

**行为（thin relay，net-new async）：**

- 工具体为 **async**，用 `httpx.AsyncClient` + `httpx_sse.aconnect_sse` 调 `fetch(${GATEWAY_URL}/api/gateway/agents/{target}/a2a)`，携带上面的请求头。TLS：server SSL 开启时 `GATEWAY_URL` 为 `https://127.0.0.1`（自签），需 `verify=False` / 自建 SSLContext（`NODE_TLS_REJECT_UNAUTHORIZED=0` 只对 Node 有效，对 Python 无效；参考 `qos-agent` 的做法）。
- 以 SSE 消费网关回流：每条 `a2a_progress` 事件 → `ctx.session.send_log_message(...)`（FastMCP 的 logging 通知，payload 见 §6）；终止帧 → 作为工具结果 `return`。
- **取消（已核验，是协程被打断而非回调）**：goose 在工具超时/用户取消时向该 MCP server 发 `notifications/cancelled`（见 §8）；MCP Python SDK 的 `_receive_loop` 收到后按 `requestId` 调 `responder.cancel()` → `anyio.CancelScope.cancel()`，**正在跑的 `call_agent` 协程会被抛 `CancelledError`**。扩展必须在 `except/finally` 里收尾：关闭 httpx SSE 连接、调网关 `POST /sessions/{sub}/cancel`。否则 B 在网关侧会继续空跑。

> 说明：扩展只是 stdio↔SSE 的薄中继；所有编排逻辑在网关，便于复用实例管理与可观测。但「消费 SSE / `ctx` 通知 / 取消兜底」三件是仓库净新增，工时按净新增 async 组件估，勿按「拷贝 control_center」估。

### 5.2 网关 A2A 端点（编排收口）

新增（建议）：`POST /api/gateway/agents/{targetAgentId}/a2a`，**返回 SSE**。该路由不被现有 `AuthWebFilter`/`UserContextFilter` 特判，因此天然要求合法 `x-secret-key` + 非空 `x-user-id`；自定义头 `x-a2a-*` 透传给控制器自取。

职责：

1. **鉴权与身份**：校验 `x-secret-key`；从 `x-user-id` 取原始用户；A2A **始终以同一 userId** 运行 B（不跨用户）。
2. **防嵌套 / 防环（网关派生 depth）**：读 `x-a2a-origin-session`，查子会话侧记录——若该 session 本身已是登记在册的 A2A 子会话（即调用方已是某次 @ 的子运行）→ 视为嵌套，直接拒绝（409/403）。否则放行。环检测：沿 `parentSessionId` / `originAgentId` 链，若目标已在当前调用链上 → 拒绝。**depth 不读自请求头**。
3. **实例与子会话**：`instanceManager.getOrSpawn(targetAgentId, userId).block()`；复用现有建会话流程（`POST /agent/start` → `POST /agent/resume`，参考 `SessionController.startSession`）在 B 实例上**新建一个子会话**，登记网关侧记录（`origin=a2a`、`parentSessionId`、`originAgentId`、`callerUserId`，见 §5.4；goose 侧该会话仍是 `user` 类型，无法在 goose 打标），随后 `POST /sessions/{sub}/reply`（user_message = `message`）。
4. **消费与精简**：订阅 `GET /sessions/{sub}/events`（服务端 in-process 消费，照 `SessionBridgeService` 的范式），按 §6 规则**过滤 thinking、精简**为 `a2a_progress` 事件 SSE 回流；终止时回流最终结果帧。
5. **超时治理**（见 §8）：维护「空闲超时」「总时长上限」，到点优雅返回「超时/部分结果」终止帧。期间 A2A 流量需刷新 B 实例的 `last-activity`，避免子运行跑一半被 `IdleReaper` 误杀。
6. **取消传播**：收到上游取消 → `POST /sessions/{sub}/cancel` 取消 B。

> 端点钉死在 **event-bus 家族**：`POST /sessions/{id}/reply`（立即返回 `request_id`、真正执行经事件流产出）+ `GET /sessions/{id}/events`（SSE，支持 `Last-Event-ID`）+ `POST /sessions/{id}/cancel`。注意 goosed 另有 legacy `POST /reply`（其本身即 SSE、不返 `request_id`），**不要用它**。B 的运行时长走 SSE 长预算（`sse.max-duration-sec`），不受 120s 的 reply 提交超时约束。网关现有 `ReplyController` 已用 event-bus 家族，可直接复用。

### 5.3 `@mention` 触发与解析（确定性解析 + 提示兜底）

- **数据源**：前端 `@` 自动补全直接用现成的 `GET /api/gateway/agents`（返回 id/name/status）；前端已有 `useGoosed().agents` 在内存，无需新增请求。补全 UI 仿现有 `/` 技能选择器（`ChatInput.tsx` 的 `findSlashSkillToken` 一套）。**仅在与 fo-copilot 对话时出现**：`ChatPage` 按 `activeAgentId === 'fo-copilot'` 决定是否把 agents 列表传给 `ChatInput`（传空数组则选择器不弹出），与「只有 fo-copilot 能发起调用」的约定一致。
- **触发方式（实现现状：确定性前端直驱为主、提示兜底）**：
  - **确定性路径（主，`@<已知 agentId>`）**：前端 @ 补全产出**规范化的精确 `agentId`**（来自 agents 列表，杜绝拼错/编造）。当发送的消息含 `@<已知 agentId>` 时，**前端直接驱动网关 A2A 调用**（`client.delegateAgent` → `POST …/a2a`），合成 `call_agent` 卡片、就地渲染进度与 B 的结果——**完全绕过 AgentA 的 goosed 与模型**，因此「弱模型漏发/编造委托」在这条路径上被彻底消除。代价：B 的结果**逐字展示、不经 AgentA 综合**，且该轮**不写入 AgentA 的 goosed 会话**（`agentVisible:false`；权威留痕见「Agent 调用」历史 §5.4）。
  - **提示兜底（模型路径）**：覆盖自然语言/未命中前端拦截的提及。各 Agent 的 AGENTS.md 加入规则：「要把活交给某 agent 时调用 `call_agent`，`target=<精确 id>`（不确定先 `list_available_agents`），`message=` 去除 @ 标记后的原话」。这条路径**经 AgentA 模型发起与综合、并持久化在 goosed 会话**；但 goosed 的 reply 无 `tool_choice`、无法强制调用，可靠性靠提示（弱模型可能漏发或选错 id，故仅作兜底）。
- **信封**：`message` 为用户原话去除 @ 标记后的逐字内容（不注入 AgentA 历史）。

### 5.4 A2A 子会话的历史归类（新增第三类「Agent 调用」）

B 的运行天然是一次 goosed 会话，本就被持久化。但需要把它与「user」「scheduled」区分开，新增第三类。

**约束（已核验）**：goose 冻结，且 goosed 建会话 API 把类型硬编码为 `User`（`crates/goose-server/src/routes/agent.rs:241`，请求体 `StartAgentRequest` 无 `session_type` 字段）；其枚举虽含 `sub_agent`/`gateway`（`crates/goose/src/session/session_manager.rs:41-50`），但**经 HTTP API 不可设置**。因此 A2A 子会话在 goose 侧仍是 `user` 类型，**第三类必须在网关侧派生**（零 goose 改动）。

**硬限制（必须知道）**：网关 `GET /sessions` 的聚合**只遍历当前在跑的实例**（`SessionController.listAllSessions` 只取 `Status.RUNNING` 实例再去 goosed 拉），B 实例一旦被 `IdleReaper` 回收，其 A2A 子会话在历史里**根本不出现**。因此「Agent 调用」要离线可列，**必须持久化侧记录**（决策已定）。

**做法**：

- **侧记录 = 一个 JSON 文件**，照现成 `ChannelBindingService` + `ChannelRuntimeStorageService` 范式，存在 `gateway/users/{userId}/a2a/sessions.json`，每条形如：
  ```jsonc
  { "subSessionId": "20260605_143022", "parentSessionId": "20260605_142800",
    "originAgentId": "agentA", "targetAgentId": "agentB", "callerUserId": "...",
    "createdAt": "...", "status": "completed", "title": "执行 XXX" }
  ```
  网关创建 B 的 A2A 子会话时 append 一条。注意：该类文件存储是 read-modify-write 无锁、**非跨进程并发安全**（与现有 channel 存储同款），A2A 低频可接受。
- **归类**：`GET /sessions` 列表时把命中侧记录的会话注入 `origin=a2a`（在 `extractSessionsArray` 注入），`SessionController.matchesType`（约 `SessionController.java:278-288`，当前只分 `user`/`scheduled`）新增分支 `agent_call`：命中者归此类，并**从默认 `user` 列表排除**，避免每次 @ 都污染主历史。写侧记录时 invalidate `SessionCacheService(userId)`。
- **离线可列 vs 离线看详情**：列表由侧记录支撑、B 离线也能列出；点开**完整 transcript** 时若 B 已回收，需按需 `getOrSpawn` 把 B 拉起来读（复用现有读取路径），「可列」不自动等于「离线渲染全文」。
- **前端**：历史新增「Agent 调用」筛选/页签（仿 `HistoryPage` 的 `seg-filter` 三段控件加第四段），条目展示「由 agentA 发起 → 调用 agentB」，并支持深链到该子会话详情（与 §7 的「展开详情」同一入口）。

## 6. 进度回流协议 `a2a_progress`

**本质：MCP 之上的一层应用约定，不是协议扩展。** 它就是标准 MCP `notifications/message`（logging 通知，扩展侧用 `ctx.session.send_log_message` 发出），在 `data` 里放约定字段；goose/MCP 当作不透明 JSON 原样转发。约定只存在于「网关/扩展（发）↔ 前端（认）」之间。

**payload（建议，放在 logging 通知的数据里）：**

```jsonc
{
  "type": "a2a_progress",        // 前端识别用的判别字段
  "target_agent": "agentB",       // 来源 Agent（前端染色用）
  "sub_session_id": "20260605_3", // B 的子会话（可用于"展开详情"深链）
  "kind": "tool_call" | "text" | "milestone" | "status",
  "label": "读取服务日志",         // 网关把原始工具名映射为友好动作名（前端只渲染 label）
  "step": 3                        // 累计步数（网关计数）
}
```

约定要点（已对齐 goosed 线缆格式）：

- **thinking 完全忽略**：网关侧过滤 `Thinking` / `RedactedThinking`，不回流。
- **线上事件 type 是 `"Notification"`（不是 `McpNotification`）**：`McpNotification` 是 goose 内部 Rust 枚举名；序列化到 AgentA 的 `/events` SSE 后帧为 `{"type":"Notification", "request_id":…, "chat_request_id":…, "message":{…}}`。
- **绑定用 `request_id`（已核验、免费）**：该帧的 `request_id` **就是本次 `call_agent` 工具调用的 id**（`dispatch_tool_call` 把传入的 `request.id` 原样回传，`agent.rs:868/900`；通知在 `agent.rs:1918` 用同一 id yield）。前端工具卡片正按该 id 键控，故 `notification.request_id === toolRequest.id` 直接对得上。另有 `chat_request_id`（本轮 reply 的 UUID）用于 turn 级路由，两者都在帧里，别混。
- **友好名在网关做**：前端不认 `control_center__read_service_logs` 这类原始名；网关给出 `label`，前端只渲染 `label`，保持前端"哑"。
- **粒度可调，零 goose 风险**：一期可"全透传（除 thinking）"，过吵则在网关收紧为「仅工具标题 + 里程碑」。该旋钮完全在网关/前端。
- **并发归因是 best-effort**：goose 注释明确 MCP 通知非 request-scoped（按当前正在排空的工具流打标）。一期串行无碍；二期真并行、同一扩展并发多工具时需重新设计归因。

## 7. 前端呈现

**现状（须知）**：前端目前对 `Notification` 这个 SSE 事件是 **no-op**（`useChat.ts` 的 switch 直接 break），`systemNotification` 是声明了从未渲染的死类型，`ToolCallDisplay` 没有状态行槽位，也没有 `request_id → 工具卡片` 的索引（卡片按 message content 的 `toolRequest.id` 渲染、按 `toolResponse.id` 配对）。**因此 §7 的实时进度行是 100% 净新增**，不是"复用现有渲染"。好消息：绑定可行（见 §6，`request_id` 已对得上）。

**目标形态**：`call_agent` 在结构上**与普通 MCP 工具调用一致**——完成后，一个 agent-call 与一个普通 tool-call 在前端看起来一样。差异只发生在**执行过程中**。

**执行中（C 档 last-wins 可变状态）**：在 `call_agent` 工具卡片标题下挂**一行**就地更新的活动行，按 `request_id` 绑定：

```text
⟳ agentB · 步 3 · 读取服务日志 · 0:42
  └ spinner  └计数  └当前动作(label)  └前端本地 elapsed 计时器
```

- **当前动作** = B 的「最新一次工具调用」对应的 `label`（last-wins，新通知就地覆盖）。
- **步数** = 网关累计的 `step`。
- **elapsed 计时器** = 纯前端本地计时，不依赖通知频率；即便某步很久没有新通知，计时器仍走，消除"静止=是否还活着"的歧义（也与 §8 的"goose 不靠心跳续命"解耦）。
- **染色** = 按 `target_agent` 给卡片/状态行一个颜色或 `agentB:` 前缀标识来源。
- **完成时**：状态行折叠为 `✓ agentB · 共 5 步 · 1:12`（或直接移除），最终结果落到正常 result 槽——此刻与普通已完成工具调用结构一致。
- **（可选）展开详情**：用 `sub_session_id` 深链到 B 的子会话完整记录，无需在 A 内重复渲染（B 离线时按需拉起，见 §5.4）。

**前端净改动**：① `useChat` 把 `Notification` 从 no-op 改为捕获进 state、新建 `request_id → 卡片` 查找、按 `request_id` 绑定 `a2a_progress`；② `Message`/`ToolCallDisplay` 加 last-wins 状态行（含本地计时器）+ 按 `target_agent` 染色；③ `@` 补全（仿 `/` 技能选择器）；④ 历史「Agent 调用」页签。其余复用现有 MCP 工具渲染。

## 8. 超时、取消与容错

**关键事实（已核验 goose 源码）：goose 的 MCP 每次调用超时是「整段调用总时长」的 wall-clock 死线，不是消息间隔超时，且通知不会重置它。**

- `await_response`（`crates/goose/src/agents/mcp_client.rs:537-558`）是一个 `tokio::select!`：在「最终响应 / 一次性 `sleep(timeout)` / 取消」三者间竞速；通知走另一条独立通道（`notification_subscribers`），**碰不到这根计时器**。
- 默认超时 `DEFAULT_EXTENSION_TIMEOUT = 300` 秒（`crates/goose/src/config/extensions.rs:11`），可按扩展 `timeout` 或 `GOOSE_DEFAULT_EXTENSION_TIMEOUT` 覆盖（`extension_manager.rs:63`）。
- 结论：**心跳续命在 goose 这层不成立**。长任务必须把扩展 `timeout` 设大；真正的"空闲续命"语义需在网关自己实现。

**三层超时（叠加）：**

| 层 | 实现位置 | 建议值 | 作用 |
| --- | --- | --- | --- |
| 空闲超时（B 无进度即中止） | **网关**（记 B 的 last-activity） | 无进度 3 分钟 | 真正想要的"有进度就活着" |
| 总时长上限 | **网关** | 15 分钟 | 防 B 一直磨蹭 |
| goose 扩展超时（钝兜底） | **扩展 `timeout` 配置** | 1200 秒（> 上面两者） | 正常永不触发，仅兜网关挂掉的极端情况 |

网关应在到达自身空闲/总时长阈值时**优雅返回**「超时/部分结果」终止帧，而不是等 goose 的钝死线抛 `ServiceError::Timeout`。同时 A2A 流量需刷新 B 实例 `last-activity`，避免子运行被 `IdleReaper` 误杀。

**取消传播（已核验，协程被打断）**：用户取消 AgentA 当前轮 → goose 向 `delegation` 扩展发 `notifications/cancelled`（`mcp_client.rs` 的超时分支与取消分支**两处都发**，约 line 550 / 554）→ MCP Python SDK `_receive_loop` 收到后 `responder.cancel()` → `anyio.CancelScope.cancel()`（`mcp/shared/session.py:403-406`、`138-145`）→ 正在跑的 `call_agent` 协程抛 `CancelledError` → 扩展在 `except/finally` 关闭 SSE 并请求网关 `POST /sessions/{sub}/cancel` 取消 B。**这是协程被打断、不是回调，扩展须自行兜底清理。**

**错误传播（白拿）**：`call_agent` 报错/连接断 → goose 自动转为工具失败，AgentA 的模型可感知并处理（告知用户/重试），无需自建 agent 循环韧性。

**防嵌套 / 防环**：见 §5.2（网关从子会话侧记录派生 depth + 拒绝已是子会话的调用方 + 沿父链做环检测）。

## 9. 并发与分期

**为什么并行需要 async**：AgentA 的 goosed 在同一轮内对扩展工具是**顺序执行**的——一轮里两个同步 `call_agent` 会 B 跑完再跑 C（串行）。真并行（同时 @B @C）需要后台/async 模式，这部分复杂度（任务生命周期、结果检索、聚合、部分失败）集中且易出 bug。

- **一期（简单、稳）**：同步 `call_agent` + 流式 `a2a_progress`。
  - 单目标 @ 体验完整；多 @ 退化为**串行**（按提及顺序 B 后 C）。
  - 注意：一期阻塞式已经提供**实时进度**（靠通知），用户不会盯着几分钟黑箱。**async 不是为了"看得见进度"，只为"真并行"。**
- **二期（真并行）**：引入后台/async 调用 + 结果聚合 + 多卡片并排呈现。届时"串/并、长/短由模型自行决定"白送（模型自选是否 async、发几个）。

## 10. 安全与隔离

- **同用户身份**：A2A 始终以发起者的 `userId` 运行 B；不跨用户（避免越权）。`userId` 由扩展从自身 CWD 解析，网关从 `x-user-id` 取。
- **回环鉴权**：扩展→网关复用现有 `GATEWAY_URL` / `GATEWAY_SECRET_KEY`（自动注入的网关鉴权密钥）/ `x-secret-key`，并透传 `x-user-id`。
- **实例预算**：A2A 在同 userId 下拉起 B 会消耗 `maxInstancesPerUser` 预算；重度 @ 需确认上限够用、且不会挤掉用户自己的 agent 实例。
- **编排者唯一（约定，非技术强制）**：只有 fo-copilot（数字人级）配置 delegation、能发起 `call_agent`；其它 agent（工具级）不发起调用，但可作为被 @ 的目标。靠「只给 fo-copilot 配 delegation 扩展 + 前端仅在 fo-copilot 对话显示 @ 选择器」落地，而非权限校验。成环/越权仍由 **防嵌套/防环守卫**（fo-copilot→fo-copilot 自调用被 depth 守卫拦住）与 **同 userId** 兜底。
- **隔离不变**：B 仍在自己的 `(agentId, userId)` 实例与目录内运行，复用网关既有隔离。

## 11. 一期改动清单（均不涉及 goose）

**gateway-service（Java）**

- 新增 `A2AController`：`POST /api/gateway/agents/{targetAgentId}/a2a`（SSE）。
- 新增 A2A 编排服务：防嵌套判定 → `getOrSpawn` → 建子会话（start+resume）→ 驱动 `reply` → in-process 消费 `events`（照 `SessionBridgeService`）→ 精简/超时/取消/刷新 last-activity。
- 新增 A2A 子会话侧记录存储（JSON，`users/{userId}/a2a/sessions.json`，仿 `ChannelBindingService`）：写记录 + invalidate `SessionCacheService`。
- `SessionController`：`extractSessionsArray` 注入 `origin`、`matchesType` 新增 `agent_call` 分支并从默认 `user` 列表排除（§5.4）；详情读取支持对离线 B 按需 `getOrSpawn`。
- 复用 `InstanceManager` / `GoosedProxy` / 鉴权过滤器。

**agent 配置（仅 fo-copilot）**

- `gateway/agents/fo-copilot/config/mcp/delegation/server.py`（stdio FastMCP、async、`call_agent` 工具 + `ctx: Context`）；`requirements.txt` 含 `mcp` + `httpx` + `httpx-sse`。
- `gateway/scripts/ctl.sh` 新增 delegation 的 build wrapper（`build_python_mcp` 创建自带 `.venv`，指向 fo-copilot 的 delegation 目录）。
- 在 fo-copilot 的 `config/config.yaml` 的 `extensions` 下声明 `delegation`（`envs:`、较大 `timeout`）。
- 在 fo-copilot 的 AGENTS.md 加入 `@<id> → call_agent` 的调用约定（提示兜底）。

**web-app（前端）**

- `useChat`：`Notification` 从 no-op 改为捕获、按 `request_id` 绑定 `a2a_progress`。
- `Message`/`ToolCallDisplay`：last-wins 状态行（含本地 elapsed 计时器）、按 `target_agent` 染色。
- `@` 自动补全：接 `useGoosed().agents`（`GET /api/gateway/agents`），仿 `/` 技能选择器；产出规范化 `agentId`。**仅在 fo-copilot 对话出现**（`ChatPage` 按 `activeAgentId === 'fo-copilot'` 传入 agents；非 fo-copilot 传空数组，选择器不弹出）。
- 历史新增「Agent 调用」筛选/页签（§5.4）。
- i18n：`en.json` / `zh.json` 同步新增进度行、@ 补全、历史页签等 copy。

## 12. 已确认的决策

1. **编排者唯一（约定）**：仅 fo-copilot（数字人级）配 delegation、能发起 `call_agent`；工具级 agent 不发起、但可作为目标。非技术强制——靠「只给 fo-copilot 配扩展 + 前端 @ 选择器仅在 fo-copilot 出现」落地，叠加防嵌套/防环 + 同 userId 兜底（§10）。
2. **子会话归类**：B 的子会话天然由 goosed 持久化；历史中**新增第三类「Agent 调用」**，在**网关侧派生**（goose 冻结、其类型经 API 不可设置，§5.4）。**持久化侧记录（JSON），离线可列**；点开完整详情对离线 B 按需 `getOrSpawn`。
3. **命名**：HTTP 端点 `…/a2a`；MCP 扩展 `delegation`、工具 `call_agent`（模型可见 `delegation__call_agent`）。
4. **depth 守卫**：扩展不自报 depth；从 MCP `_meta.agent-session-id` 读自身会话 id 上报，**网关从子会话侧记录派生 depth**（调用方 session 已是 A2A 子会话即拒），沿父链做环检测（§5.2）。
5. **触发方式**：`@<已知 agentId>` 走**确定性前端直驱**（前端直接调用网关 A2A、绕过 AgentA 模型；结果逐字展示、不持久化到 AgentA 的 goosed 会话）；自然语言/未命中拦截走**模型路径 + 系统提示兜底**（经 AgentA 模型综合并持久化）。权威子运行留痕统一在「Agent 调用」历史（§5.3、§5.4）。
6. **进度粒度**：一期默认「全透传（除 thinking）」，过吵再在网关收紧为「仅工具标题 + 里程碑」。
7. **扩展归属**：delegation 归 fo-copilot 自有（`fo-copilot/config/mcp/delegation/`，无共享/symlink）；只改 fo-copilot 的 `config.yaml` + AGENTS.md。前端 `@` 选择器只在与 fo-copilot 对话时出现（`ChatPage` 按 `activeAgentId === 'fo-copilot'` 传入 agents 列表）。

## 附录：关键 goose / MCP-SDK 机制与代码引用（仅供设计依据，不改动；均已核验）

- **会话 id 注入工具调用 `_meta`（depth/归因的基础）**：goose 把当前 session id 注入每次工具调用的 `_meta`，键 `agent-session-id`（`crates/goose/src/session_context.rs:3`；注入 `mcp_client.rs:740-772`）。Python SDK `RequestParams.Meta` 为 `extra="allow"`（`mcp/types.py`），`ctx.request_context.meta` 暴露，扩展可读。
- **工具调用中实时转发通知，且通知带 `request_id`（= 工具调用 id）**：`crates/goose/src/agents/agent.rs:1882-1929`——工具调用被建模成「通知 + 最终结果」交织的流，`ToolStreamItem::Message` 即时 `yield AgentEvent::McpNotification((request_id, …))`；`request_id` 即 `dispatch_tool_call` 原样回传的 `request.id`（`agent.rs:862-900`）。
- **线上事件序列化**：序列化到 `/events` SSE 后事件 type 为 `"Notification"`，帧含 `request_id`（工具调用 id）+ `chat_request_id`（本轮 reply UUID）+ `message`（rmcp `ServerNotification`）。
- **会话类型经 HTTP API 不可设非 User**：`crates/goose-server/src/routes/agent.rs:241` 硬编码 `SessionType::User`；枚举 `crates/goose/src/session/session_manager.rs:41-50` 含 `sub_agent`/`gateway` 但 API 不暴露。
- **MCP 每次调用超时（总时长、不可被通知重置）**：`crates/goose/src/agents/mcp_client.rs:537-558`；默认 `DEFAULT_EXTENSION_TIMEOUT=300`（`crates/goose/src/config/extensions.rs:11`）；可按扩展 `timeout` 或 `GOOSE_DEFAULT_EXTENSION_TIMEOUT` 覆盖（`extension_manager.rs:63`）。
- **取消传播（goose 端发送）**：`mcp_client.rs` 超时分支与取消分支（约 550 / 554）均向 MCP server 发 `notifications/cancelled`（`CancelledNotificationParam{ requestId, reason }`）。
- **取消处理（MCP Python SDK 端，已核验仓库 vendored SDK）**：`_receive_loop` 收 `CancelledNotification` → 按 `requestId` 调 `responder.cancel()` → `anyio.CancelScope.cancel()`（`mcp/shared/session.py:397-406`、`138-145`），async 工具协程因此被取消，须自行兜底清理。
- **goosed event-bus API（A2A 走这套）**：`POST /agent/start`（建会话，恒 `session_type:"user"`）→ `POST /agent/resume`（load_model_and_extensions）→ `POST /sessions/{id}/reply`（即返 `request_id`）→ `GET /sessions/{id}/events`（SSE，`Last-Event-ID` 可续）→ `POST /sessions/{id}/cancel`。
- **既有 stdio MCP 范式（参考）**：骨架对标 `gateway/agents/supervisor-agent/config/mcp/control-center/`（但其为同步 `urllib`、调另一服务、无 SSE/ctx/cancel）；「回调网关 + 密码 + 自签 TLS」参考 `gateway/agents/qos-agent/config/mcp/system-health-analysis/`（亦同步、无 SSE）。服务端消费 goosed 会话 SSE 的现成范式：`gateway/.../service/channel/SessionBridgeService.java`。
- **既有同类原语（goose 内置，参考不直接使用）**：`orchestrator`（进程内对等会话）与 `summon`/`delegate`（进程内子代理 + async + 通知回流）。本方案是其**跨 goosed 进程、经网关编排**的对应实现。
