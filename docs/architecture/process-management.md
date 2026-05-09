# 进程管理

## 运行时模型
`gateway` 按用户和 Agent 管理 `goosed` 运行时进程。`(agentId, userId)` 是隔离边界：每个运行时都有独立端口、工作目录、上传目录和进程生命周期。

Gateway 运行时配置由 Spring Boot 直接从 `gateway/config.yaml` 加载。标准框架日志使用 `logging.level.*`；gateway 自身的日志开关使用 `gateway.logging.*`。

## 必要属性
- 按需懒加载运行时；除非预热路径或常驻实例配置明确要求，不要提前创建用户运行时。
- 自动回收空闲实例，而不是让所有运行时常驻。
- Agent 配置保持共享，但运行时数据和上传文件必须按用户隔离。
- 运行时服务只绑定本地地址；外部访问必须继续经过 gateway。

## 常驻实例
- 常驻实例在 `gateway/config.yaml` 中以明确的 `(userId, agentId)` 目标配置。
- 常驻实例在 gateway 启动时拉起，并且只豁免空闲回收。
- 健康检查、超时回收和崩溃恢复仍然适用于常驻实例。

## 运行时目录约定
Gateway 在 `gateway/users/<userId>/agents/<agentId>/` 下准备运行时目录。共享 Agent 配置通过链接接入，可变运行时状态保留在用户本地目录。新功能应遵守这个拆分，不要直接写入共享 Agent 配置树。

Files 页面从 `gateway.files.scan-roots` 扫描用户可见文件。默认根目录是运行时目录及其 `output/` 子目录，二者都不递归扫描。每个根目录都有稳定的 `id`，因此文件预览、下载、编辑和删除动作可以区分不同根目录下的同名文件。

拉起 `goosed` 时，gateway 会把 `XDG_CONFIG_HOME` 注入为共享 Agent 配置目录，使 Goose 内置配置消费者解析到 `gateway/agents/<agentId>/config/goose/*`，而不是宿主机用户主目录。通过该配置根目录存储的 Goose memory 是 agent-scoped，位于 `gateway/agents/<agentId>/config/goose/memory/`；即使 Goose API 把这种存储模式命名为 `global`，它在 ops-factory 中也不是平台全局共享。

## 健康检查与恢复
- 健康检查、空闲清理和重启逻辑属于 gateway 的进程管理类，不应放在前端代码或临时脚本中。
- Watchdog、重启退避或实例数量限制的改动会影响所有 Agent，必须有谨慎的回归覆盖。
- 保留进程输出 draining 等防御性运行时行为；历史问题表明，在工具调用密集时，缺少输出 draining 会让本来健康的运行时冻结。

## 日志约定
- `gateway/logs/gateway.log` 是 gateway 应用的唯一主日志文件。
- `gateway/logs/gateway-stdout-stderr.log` 可用于后台启动时捕获 stdout/stderr，但它只是辅助诊断文件，不是主要业务日志。
- Gateway 在 HTTP 响应中返回 `X-Request-Id`，并为每个请求记录统一访问日志。
- 运维排障时，应在请求日志、controller/service 日志和进程管理日志之间关联 `requestId`、`userId`、`agentId`、`sessionId`、`port`、`pid`。
- 敏感的上游响应体和 SSE 预览必须保留在显式 `gateway.logging.*` 开关之后，默认不记录。

## 需要评审的变更
以下变更需要显式评审：
- 运行时目录布局
- 拉起、回收、重启语义
- 端口分配行为
- 健康检查就绪规则
- `goosed` 环境变量注入
