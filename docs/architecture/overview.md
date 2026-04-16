# Architecture Overview

## Purpose
`ops-factory` 是一个多服务协同的 Agent 平台。整体架构遵循几个稳定边界：

- `web-app` 负责统一前端体验与业务页面编排
- `gateway` 是浏览器、SDK 与渠道接入的主入口，负责鉴权、路由、会话编排与运行时管理
- `gateway` 按 `(agentId, userId)` 维度托管实际 Agent Runtime
- `knowledge-service`、`business-intelligence`、`control-center` 等服务提供独立领域能力
- `langfuse`、`onlyoffice`、`prometheus-exporter` 等保持为可选集成，不应成为最小可运行链路的硬依赖

## System Diagram

```text
+--------------------------------------------------------------------------------------------------+
|                                          Ops Factory                                             |
+--------------------------------------------------------------------------------------------------+
| Access Layer                                                                                     |
|--------------------------------------------------------------------------------------------------|
| Browser (web-app)                 | TypeScript SDK             | External Channels              |
| chat / files / history / settings | programmatic gateway access| WhatsApp / WeChat / media     |
+-----------------------------------+----------------------------+--------------------------------+
                  |                                   |                              |
                  | HTTP / SSE                        | HTTP                         | bridge events / media
                  +-----------------------------------+------------------------------+
                                                      |
                                                      v
+--------------------------------------------------------------------------------------------------+
| gateway                                                                                          |
|--------------------------------------------------------------------------------------------------|
| auth / routing / session orchestration / file APIs / config CRUD / SSE relay / channel adapters  |
| process management / runtime lifecycle / service integration                                      |
+-----------------------------+---------------------------+----------------------+----------------------+
                              |                           |                      |                      |
                              | spawn / route             | service calls        | service control      | platform integration
                              v                           v                      v                      v
          +--------------------------------+  +---------------------------+  +----------------------+  +----------------------+
          | Agent Runtime Pool             |  | Domain Services           |  | control-center       |  | Platform Services    |
          |--------------------------------|  |---------------------------|  |----------------------|  |----------------------|
          | isolated by (agentId, userId)  |  | knowledge-service         |  | health / logs /      |  | langfuse             |
          | goosed process per runtime     |  | business-intelligence     |  | config / actions     |  | onlyoffice           |
          | runtime data under             |  +---------------------------+  +----------------------+  | prometheus-exporter  |
          | gateway/users/<user>/agents/*  |                                                           | observability /      |
          +----------------+---------------+                                                           | preview / metrics    |
                           |                                                                           +----------------------+
                           |
                           | shared config / prompts / skills / memory
                           v
          +---------------------------------------------------+
          | gateway/agents/<agent-id>/config + skills         |
          +---------------------------------------------------+
```

## Key Architecture Information

### Core Services
- `web-app/`: React + Vite 前端。负责平台壳层、导航、Chat、文件、历史、Agent 配置、Control Center 入口，以及各业务模块页面。
- `gateway/`: Java 21 + Spring Boot 后端主入口。负责认证、路由、会话、文件访问、配置管理、SSE/流式转发、渠道桥接，以及 Agent Runtime 生命周期管理。
- `gateway/agents/*`: Agent 级别的共享配置、提示词、skills、provider 定义与 Goose 全局配置根目录。
- `knowledge-service/`: 知识入库、切块、索引与检索服务，提供知识相关能力。
- `business-intelligence/`: 业务报表与分析服务，输出面向业务场景的数据分析能力。
- `control-center/`: 平台控制面服务，负责健康状态、日志、配置查看与服务控制动作。
- `typescript-sdk/`: 网关访问的类型化 SDK。
- `test/`: 跨服务集成测试与 E2E 测试。

### Primary Request Flow
1. 浏览器、SDK、渠道事件统一进入 `gateway`。
2. `gateway` 完成鉴权、用户隔离、Agent 路由、会话与文件语义处理。
3. 若请求需要 Agent 执行，`gateway` 将请求路由到对应 `(agentId, userId)` 的运行时实例；实例不存在则按需拉起。
4. Agent Runtime 在执行过程中可按各自配置使用领域能力，例如知识检索、业务分析或渠道工具。
5. 流式响应、文件结果、会话状态再由 `gateway` 返回给前端、SDK 或渠道。

### Runtime And Isolation Model
- `(agentId, userId)` 是运行时隔离边界。
- 每个 Runtime 拥有独立进程、端口、工作目录、上传目录和生命周期。
- 共享 Agent 配置位于 `gateway/agents/<agentId>/config`。
- 用户态运行时数据位于 `gateway/users/<userId>/agents/<agentId>/`。
- 常驻实例只是不参与空闲回收，不改变健康检查、重启恢复和本地绑定规则。

### Frontend And Module Boundary
- `web-app/src/app/platform/*` 承载壳层、providers、navigation、chat、preview、renderers、panels、shared UI 等平台能力。
- `web-app/src/app/modules/*` 承载业务模块页面、组件、hooks 与模块样式。
- 新增前端能力优先复用现有 route shell、section card、toolbar/form block、result card、right panel 等既有模式。

### Integration And Optional Services
- 渠道接入通过 `gateway/channels/*` 与 `gateway/tools/*` 完成，不应让前端直接承担渠道协议细节。
- `knowledge-service`、`business-intelligence`、`control-center` 是独立服务边界，应保持各自配置、日志和部署职责清晰。
- `External Channels` 是平台外部接入面的一部分，但不属于浏览器/SDK client。
- `langfuse`、`onlyoffice`、`prometheus-exporter` 也是方案中的显式服务，只是保持为可选增强能力，不应破坏仅运行核心链路时的开发体验。

### Configuration Rules
- 大多数服务的运行时配置文件为 `config.yaml`。
- `web-app` 的运行时配置使用 `config.json`。
- 默认遵循“服务配置为主，环境变量覆盖”的规则。
- 新增配置项时，必须同步更新对应的 `config.yaml.example` 或 `config.json` 示例，以及相关架构/开发文档。
- `control-center/config.yaml` 通过 `control-center.services[]` 声明可管理服务，并可为每个服务定义 `config-path` 与 `log-path`。

## Non-Negotiable Boundaries
- 浏览器与 SDK 集成必须以 `gateway` 作为稳定入口，不要随意改动认证头、路由前缀或 SSE 事件结构。
- 前端不要绕过既定服务边界直接耦合底层运行时、进程管理或本地文件系统语义。
- Agent 特有行为应沉淀在 `gateway/agents/<agent-id>/config` 及相关运行时配置中，不要散落到无关服务。
- 进程拉起、回收、健康检查、重启恢复、端口分配等逻辑属于 `gateway` 的 process-management 责任域。
- 可选服务必须保持可选；本地开发至少应支持核心链路独立运行。

## Related Documents
- API 边界: [api-boundaries.md](./api-boundaries.md)
- 运行时与进程管理: [process-management.md](./process-management.md)
- 渠道桥接设计: [channel-module.md](./channel-module.md)
- UI 与前端模式: [../development/ui-guidelines.md](../development/ui-guidelines.md)
- 日志规范: [../development/logging-guidelines.md](../development/logging-guidelines.md)
