# Gateway 超长方法收敛改造计划

## Summary
- 目标：基于已识别的 `gateway` 超长方法清单，完成最后一轮收敛式重构，使所有目标方法方法体 `< 50` 行，且最大嵌套深度 `<= 5`。
- 当前状态：主源码 `src/main/java` 的 32 个目标方法已完成结构性拆分并在最近一次扫描中清零；测试源码 `src/test/java` 仍剩 4 个超长方法待压缩。
- 实施策略：主源码不再继续扩写功能，只做结果复核；剩余工作聚焦测试夹具提取、断言助手复用、真实代理链路保持不变，以及最终的扫描/诊断/测试验证。
- 成功标准：`/tmp/scan_gateway_long_methods.py` 重新扫描后 `main` 与 `test` 均为空；相关测试通过；近期修改文件无新增阻断性诊断。

## Current State Analysis

### 原始目标方法清单

#### 主源码 `src/main/java` 目标 32 个
| 方法 | 类 | 文件 | 原始行数 | 当前状态 |
| --- | --- | --- | ---: | --- |
| `getTopologyForBusinessService()` | `BusinessServiceService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/BusinessServiceService.java` | 100 | 已拆分，待最终回归验证 |
| `migrateFromBusinessField()` | `BusinessServiceService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/BusinessServiceService.java` | 78 | 已拆分，待最终回归验证 |
| `doCollect()` | `MetricsCollector` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/monitoring/MetricsCollector.java` | 99 | 已拆分，待最终回归验证 |
| `doSpawn()` | `InstanceManager` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/process/InstanceManager.java` | 97 | 已拆分，待最终回归验证 |
| `buildEnvironment()` | `InstanceManager` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/process/InstanceManager.java` | 70 | 已拆分，待最终回归验证 |
| `execute()` | `RemoteExecutionService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/RemoteExecutionService.java` | 87 | 已拆分，待最终回归验证 |
| `doBuildOverview()` | `LangfuseService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/LangfuseService.java` | 85 | 已拆分，待最终回归验证 |
| `metrics()` | `InternalRuntimeSourceController` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/InternalRuntimeSourceController.java` | 76 | 已拆分，待最终回归验证 |
| `getClusterNeighbors()` | `ClusterRelationService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/ClusterRelationService.java` | 72 | 已拆分，待最终回归验证 |
| `proxySessionEventsToEmitter()` | `GoosedProxy` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/proxy/GoosedProxy.java` | 67 | 已拆分，待最终回归验证 |
| `getNeighbors()` | `HostRelationService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostRelationService.java` | 64 | 已拆分，待最终回归验证 |
| `install()` | `AgentSkillInstallService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/AgentSkillInstallService.java` | 62 | 已拆分，待最终回归验证 |
| `getTree()` | `HostGroupService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostGroupService.java` | 62 | 已拆分，待最终回归验证 |
| `enrichWithBusinessServices()` | `HostRelationController` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/HostRelationController.java` | 61 | 已拆分，待最终回归验证 |
| `buildGraphNodes()` | `ClusterRelationService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/ClusterRelationService.java` | 60 | 已拆分，待最终回归验证 |
| `testConnection()` | `HostService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostService.java` | 59 | 已拆分，待最终回归验证 |
| `extractFinalAssistantText()` | `SessionBridgeService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/channel/SessionBridgeService.java` | 59 | 已拆分，待最终回归验证 |
| `createHost()` | `HostService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostService.java` | 58 | 已拆分，待最终回归验证 |
| `collect()` | `SessionTraceService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/SessionTraceService.java` | 58 | 已拆分，待最终回归验证 |
| `sendSseFrame()` | `GoosedProxy` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/proxy/GoosedProxy.java` | 57 | 已拆分，待最终回归验证 |
| `createRelation()` | `ClusterRelationService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/ClusterRelationService.java` | 57 | 已拆分，待最终回归验证 |
| `readSessions()` | `UsageSnapshotService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/finops/UsageSnapshotService.java` | 57 | 已拆分，待最终回归验证 |
| `logResumeConversationDigest()` | `ReplyController` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/ReplyController.java` | 56 | 已拆分，待最终回归验证 |
| `createAgent()` | `AgentConfigService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/AgentConfigService.java` | 56 | 已拆分，待最终回归验证 |
| `updateHost()` | `HostService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostService.java` | 55 | 已拆分，待最终回归验证 |
| `checkInstanceHealth()` | `InstanceWatchdog` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/process/InstanceWatchdog.java` | 54 | 已拆分，待最终回归验证 |
| `corsFilter()` | `ServletWebConfig` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/config/ServletWebConfig.java` | 52 | 已拆分，待最终回归验证 |
| `process()` | `FileAttachmentHook` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/hook/FileAttachmentHook.java` | 52 | 已拆分，待最终回归验证 |
| `loadResidentInstances()` | `AgentConfigService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/AgentConfigService.java` | 52 | 已拆分，待最终回归验证 |
| `diffFiles()` | `FileService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/FileService.java` | 52 | 已拆分，待最终回归验证 |
| `startSession()` | `SessionController` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/SessionController.java` | 51 | 已拆分，待最终回归验证 |
| `splitShellPipe()` | `CommandWhitelistService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/CommandWhitelistService.java` | 51 | 已拆分，待最终回归验证 |

#### 测试源码 `src/test/java` 剩余 4 个
| 方法 | 类 | 文件 | 行号 | 当前行数 | 当前状态 |
| --- | --- | --- | --- | ---: | --- |
| `sessionEvents_drainedActiveReqEmitsOutputFilesAfterEvent()` | `ReplyControllerRealProxyTest` | `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/controller/ReplyControllerRealProxyTest.java` | `191-271` | 81 | 待处理 |
| `sessionReply_realGoosed400ReturnsGatewayErrorEnvelope()` | `ReplyControllerRealProxyTest` | `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/controller/ReplyControllerRealProxyTest.java` | `64-127` | 64 | 待处理 |
| `metrics_admin_returnsMetricsData()` | `RuntimeSourceEndpointE2ETest` | `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/e2e/RuntimeSourceEndpointE2ETest.java` | `148-203` | 56 | 待处理 |
| `sessionEvents_realGoosed404ReturnsGatewayErrorEnvelope()` | `ReplyControllerRealProxyTest` | `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/controller/ReplyControllerRealProxyTest.java` | `133-183` | 51 | 待处理 |

### 当前问题画像
- 主源码长方法已经从扫描结果中消失，说明结构性拆分方向正确，后续重点转为验证行为兼容与控制新增诊断。
- 剩余 4 个超长方法全部位于测试，超长原因都不是业务复杂度，而是“服务端桩搭建 + 公共 mock + 请求执行 + 多段断言”堆叠在同一方法中。
- `ReplyControllerRealProxyTest` 的三个目标测试共享高度相似的构建路径：启动本地 `HttpServer`、创建 `ManagedInstance`、配置 `InstanceManager/HookPipeline/AgentConfigService/FileService`、再组装 `ReplyController + MockMvc`。
- `RuntimeSourceEndpointE2ETest#metrics_admin_returnsMetricsData()` 的长度主要来自一组 `MetricsSnapshot` 字段初始化和一长串 JSONPath 断言，天然适合抽成 fixture/helper。

## Proposed Changes

### 1. 主源码收尾策略

#### 范围
- 仅对已改动主源码做结果复核，不再做新一轮大面积结构调整。

#### 具体文件与动作
- `BusinessServiceService.java`
  - 复核 `getTopologyForBusinessService()`、`migrateFromBusinessField()` 的编排方法是否仍保持“加载 -> 扩展/聚合 -> 组装 -> 返回”的单一路径。
  - 重点检查新增 helper 是否造成重复遍历或丢失业务边连线顺序。
- `MetricsCollector.java`
  - 复核 `doCollect()` 新增聚合对象与统计口径，确认 `avg/p95/tps` 计算仍与旧逻辑一致。
- `InstanceManager.java`
  - 复核 `doSpawn()`、`buildEnvironment()` 的运行时变量写入顺序、ready 检查、drain 线程启动时机。
- `RemoteExecutionService.java`
  - 复核 `execute()` 的 SSH 会话关闭路径与错误返回结构。
- `GoosedProxy.java`
  - 复核 `proxySessionEventsToEmitter()` 与 `sendSseFrame()` 的 SSE 生命周期、字段转发顺序、错误完成行为。
- 其余已处理文件
  - 统一通过扫描、诊断、测试确认，不做额外实现选择。

#### 目标
- 保持这些文件当前拆分结果不回退到超长方法。
- 用测试和扫描证明“已完成”而不是继续局部美化。

### 2. `ReplyControllerRealProxyTest` 定向改造

#### 文件
- `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/controller/ReplyControllerRealProxyTest.java`

#### 拆分思路
- 提取共享测试夹具，避免每个测试重复创建同一套 Reactor Netty server、Mock 依赖和 `MockMvc`。
- 让每个测试只保留 3 段主流程：
  - 准备场景
  - 执行请求
  - 断言结果

#### 计划新增的私有 helper
- `startProxyTestServer(...)`
  - 负责按传入路由场景启动本地 `HttpServer` 并返回 `DisposableServer`。
- `runningInstanceFor(DisposableServer server)`
  - 统一构建 `ManagedInstance`，消除重复的 `agentId/userId/port/pid/secret` 初始化。
- `stubCommonReplyFlow(...)`
  - 统一 stub `instanceManager.getOrSpawn(...)`、`hookPipeline.executeRequest(...)`、`agentConfigService.getUserAgentDir(...)` 等公共依赖。
- `buildReplyController(...)`
  - 负责构造 `ReplyController` 与 `GoosedProxy`。
- `buildMockMvcWithAlice(...)`
  - 统一封装带 `alice` 用户上下文的 `MockMvc` 创建逻辑，必要时支持 `defaultRequest` 与 `Filter` 两种模式。
- `performReply(...)`
  - 统一发送 `/reply` 请求，屏蔽重复 JSON body 字符串。
- `performEvents(...)`
  - 统一发送 `/events` SSE 请求。
- `assertGatewayErrorEnvelope(...)`
  - 统一断言错误包裹中的关键文本，减少链式 `containsString(...)` 占行。
- `assertContainsOutputFilesEvent(...)`
  - 统一断言 `ActiveRequests`、`OutputFiles`、`request_id` 三个关键片段。

#### 三个目标方法的具体改法
- `sessionReply_realGoosed400ReturnsGatewayErrorEnvelope()`
  - 保留测试意图：真实 goosed 返回 `400` 时，网关仍包装成统一错误信封。
  - 压缩方法体为“启动 server -> stub 公共依赖 -> 调用 `performReply()` -> 调用 `assertGatewayErrorEnvelope()`”。
- `sessionEvents_realGoosed404ReturnsGatewayErrorEnvelope()`
  - 保留测试意图：真实 goosed 返回 `404` 的 SSE 接口时，控制器仍输出 `text/event-stream` 错误事件。
  - 抽离 server、controller、MockMvc 构建，并用单独 helper 断言 `event: error` 与 JSON 错误字段。
- `sessionEvents_drainedActiveReqEmitsOutputFilesAfterEvent()`
  - 保留真实链路：先发 reply，再订阅 async SSE，最终在原始事件后追加 `OutputFiles`。
  - 抽离 before/after files fixture、diff fixture、reply body、events body、asyncDispatch 执行逻辑，主方法只保留最小 orchestration。

#### 约束
- 不把真实 Reactor Netty server 替换成纯 mock，继续覆盖真实代理路径。
- 不改变现有断言语义，只减少重复样板代码。

### 3. `RuntimeSourceEndpointE2ETest` 定向改造

#### 文件
- `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/e2e/RuntimeSourceEndpointE2ETest.java`

#### 目标方法
- `metrics_admin_returnsMetricsData()`

#### 拆分思路
- 把快照构造与 JSONPath 断言拆出，测试主体回到“准备一个快照 -> 调接口 -> 验证关键输出”。

#### 计划新增的私有 helper
- `sampleMetricsSnapshot()`
  - 统一构造包含 `timestamp/instances/tokens/sessions/requests/errors/latency/ttft` 的标准样本。
- `stubMetricsSnapshot(MetricsSnapshot snapshot)`
  - 统一 stub `metricsBuffer.getSnapshots(120)` 与 `metricsBuffer.getAgentStats()`。
- `assertMetricsOverview()`
  - 集中断言 `collectionIntervalSec/maxSlots/returnedSlots/current/aggregate` 关键字段。
- `assertMetricsSeriesRow(long timestamp, int instances, int requests, int errors)`
  - 集中断言 `series[0]` 的时间点与聚合字段。

#### 目标效果
- 该测试方法本身压缩到 20-30 行。
- 新 helper 仍保留现有断言密度，不牺牲覆盖面。

### 4. 最终验证计划

#### 静态扫描
- 重新运行 `/tmp/scan_gateway_long_methods.py`。
- 预期结果：
  - `"main": []`
  - `"test": []`

#### 嵌套深度检查
- 对本轮改动的测试文件与已处理主源码文件做嵌套深度检查，标准为最大深度 `<= 5`。
- 若某个 helper 仍因多层 lambda/try/if 组合超限，继续下沉为更细粒度的断言或构造助手。

#### 诊断检查
- 对近期修改文件执行诊断检查，优先清理编译错误和新增阻断项。
- 对已有非阻断 warning 仅在能低成本修复时顺手处理，不把 warning 清零作为本轮目标。

#### 测试执行
- 最少执行：
  - `ReplyControllerRealProxyTest`
  - `RuntimeSourceEndpointE2ETest`
- 建议执行：
  - `cd gateway && mvn test`

#### 手工复核点
- `SessionController.startSession()` 仍返回 `startResponse`，不回归到错误变量。
- `GoosedProxy.proxySessionEventsToEmitter()` 仍在同一响应生命周期内处理 SSE。
- `HostRelationController` 的 `ArrayList` import 与其他近期修复不被误覆盖。

## Assumptions & Decisions
- 决策：范围按“主源码 + 测试”执行，最终以 36 个原始目标方法全部达标为收尾标准。
- 决策：用户提出的“嵌套深度不能小于 5”按已确认语义执行为“最大嵌套深度不超过 5”。
- 决策：优先通过提取私有 helper、局部 record/内部类、fixture builder 与断言助手实现压缩，不引入跨包迁移或公共 API 变化。
- 决策：测试改造优先复用真实链路，不将集成/代理测试退化成纯 mock-heavy 单测。
- 假设：`/tmp/gateway_long_methods_after.json` 与测试文件当前内容反映的是仓库最新状态，可作为本轮计划的事实基线。

## Verification Steps
- 扫描复核：重新生成超长方法报告，确认 `gateway` 下不再存在 `> 50` 行目标方法。
- 深度复核：对近期改动文件执行嵌套深度检查，确认 `<= 5`。
- 编译/测试：先跑目标测试类，再视时间执行 `cd gateway && mvn test`。
- 诊断复核：检查最近编辑文件是否存在新增编译错误、缺失 import 或变量名残留。
- 回归复核：针对 `SSE`、`SSH`、`session start`、`host credential` 这些近期高风险改动点做重点结果检查。

## Suggested Execution Order
1. 先重构 `ReplyControllerRealProxyTest.java` 的 3 个超长测试，最大化消除剩余超长方法数量。
2. 再重构 `RuntimeSourceEndpointE2ETest.java` 的 `metrics_admin_returnsMetricsData()`。
3. 重新运行长方法扫描，确认 `test` 清零。
4. 对近期改动文件跑诊断与目标测试类，修复阻断项。
5. 最后执行一次更完整的 `gateway` 测试回归，并记录结果。
