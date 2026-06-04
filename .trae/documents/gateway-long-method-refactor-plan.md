# Gateway 超长方法改造计划

## Summary
- 目标范围：覆盖 `gateway` 下前面识别出的全部超长 Java 方法，含 `src/main/java` 的 32 个方法与 `src/test/java` 的 4 个方法。
- 硬性目标：每个目标方法改造后方法体行数 `< 50`，且最大嵌套深度 `<= 5`。
- 改造原则：保持行为不变、先拆阶段再收口分支、优先提炼私有辅助方法与值对象、补齐针对性测试，避免引入新的跨模块依赖。
- 交付方式：按“主流程编排方法 + 若干私有辅助方法/小型记录类”的模式重构；测试方法通过测试夹具、断言助手、Mock 搭建助手降行数。

## Current State Analysis

### 方法清单

#### 主源码 `src/main/java`
| 方法 | 类 | 文件 | 行号 | 当前行数 |
| --- | --- | --- | --- | ---: |
| `getTopologyForBusinessService()` | `BusinessServiceService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/BusinessServiceService.java` | `359-458` | 100 |
| `migrateFromBusinessField()` | `BusinessServiceService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/BusinessServiceService.java` | `466-543` | 78 |
| `doCollect()` | `MetricsCollector` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/monitoring/MetricsCollector.java` | `73-171` | 99 |
| `doSpawn()` | `InstanceManager` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/process/InstanceManager.java` | `458-554` | 97 |
| `buildEnvironment()` | `InstanceManager` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/process/InstanceManager.java` | `604-673` | 70 |
| `execute()` | `RemoteExecutionService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/RemoteExecutionService.java` | `80-166` | 87 |
| `doBuildOverview()` | `LangfuseService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/LangfuseService.java` | `161-245` | 85 |
| `metrics()` | `InternalRuntimeSourceController` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/InternalRuntimeSourceController.java` | `160-235` | 76 |
| `getClusterNeighbors()` | `ClusterRelationService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/ClusterRelationService.java` | `645-716` | 72 |
| `proxySessionEventsToEmitter()` | `GoosedProxy` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/proxy/GoosedProxy.java` | `559-625` | 67 |
| `getNeighbors()` | `HostRelationService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostRelationService.java` | `471-534` | 64 |
| `install()` | `AgentSkillInstallService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/AgentSkillInstallService.java` | `75-136` | 62 |
| `getTree()` | `HostGroupService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostGroupService.java` | `135-196` | 62 |
| `enrichWithBusinessServices()` | `HostRelationController` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/HostRelationController.java` | `183-243` | 61 |
| `buildGraphNodes()` | `ClusterRelationService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/ClusterRelationService.java` | `556-615` | 60 |
| `testConnection()` | `HostService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostService.java` | `660-718` | 59 |
| `extractFinalAssistantText()` | `SessionBridgeService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/channel/SessionBridgeService.java` | `390-448` | 59 |
| `createHost()` | `HostService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostService.java` | `386-443` | 58 |
| `collect()` | `SessionTraceService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/SessionTraceService.java` | `160-217` | 58 |
| `sendSseFrame()` | `GoosedProxy` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/proxy/GoosedProxy.java` | `638-694` | 57 |
| `createRelation()` | `ClusterRelationService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/ClusterRelationService.java` | `188-244` | 57 |
| `readSessions()` | `UsageSnapshotService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/finops/UsageSnapshotService.java` | `181-237` | 57 |
| `logResumeConversationDigest()` | `ReplyController` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/ReplyController.java` | `743-798` | 56 |
| `createAgent()` | `AgentConfigService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/AgentConfigService.java` | `1201-1256` | 56 |
| `updateHost()` | `HostService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostService.java` | `456-510` | 55 |
| `checkInstanceHealth()` | `InstanceWatchdog` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/process/InstanceWatchdog.java` | `64-117` | 54 |
| `corsFilter()` | `ServletWebConfig` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/config/ServletWebConfig.java` | `51-102` | 52 |
| `process()` | `FileAttachmentHook` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/hook/FileAttachmentHook.java` | `60-111` | 52 |
| `loadResidentInstances()` | `AgentConfigService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/AgentConfigService.java` | `174-225` | 52 |
| `diffFiles()` | `FileService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/FileService.java` | `485-536` | 52 |
| `startSession()` | `SessionController` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/SessionController.java` | `108-158` | 51 |
| `splitShellPipe()` | `CommandWhitelistService` | `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/CommandWhitelistService.java` | `330-380` | 51 |

#### 测试代码 `src/test/java`
| 方法 | 类 | 文件 | 行号 | 当前行数 |
| --- | --- | --- | --- | ---: |
| `sessionEvents_drainedActiveReqEmitsOutputFilesAfterEvent()` | `ReplyControllerRealProxyTest` | `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/controller/ReplyControllerRealProxyTest.java` | `191-271` | 81 |
| `sessionReply_realGoosed400ReturnsGatewayErrorEnvelope()` | `ReplyControllerRealProxyTest` | `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/controller/ReplyControllerRealProxyTest.java` | `64-127` | 64 |
| `metrics_admin_returnsMetricsData()` | `RuntimeSourceEndpointE2ETest` | `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/e2e/RuntimeSourceEndpointE2ETest.java` | `148-203` | 56 |
| `sessionEvents_realGoosed404ReturnsGatewayErrorEnvelope()` | `ReplyControllerRealProxyTest` | `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/controller/ReplyControllerRealProxyTest.java` | `133-183` | 51 |

### 现状共性
- 业务服务层与流程型控制器方法普遍同时承担“校验、聚合、转换、构造返回值、日志/异常处理”多个阶段，天然超 50 行。
- 若干方法已有可复用片段雏形，例如 `ClusterRelationService` 内已经存在 `trackBusinessService()`、`trackHostNode()`、`buildGraphEdges()` 等助手，说明同类拆分能与现有风格保持一致。
- 典型高风险方法如 [BusinessServiceService.java](file:///Users/zlj/Documents/ZLJ/works/code/ops-factory-quality/gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/BusinessServiceService.java)、[InstanceManager.java](file:///Users/zlj/Documents/ZLJ/works/code/ops-factory-quality/gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/process/InstanceManager.java)、[GoosedProxy.java](file:///Users/zlj/Documents/ZLJ/works/code/ops-factory-quality/gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/proxy/GoosedProxy.java) 都属于“主流程过长，但阶段边界清晰”的类型，适合拆成编排方法。
- 测试长方法主要由“本地 HTTP Server 搭建 + Mock 装配 + MVC 调用 + 多断言”串在一起造成，不需要改变断言语义，只需提取 fixture/build helper 即可达标。

## Proposed Changes

### A. 先处理高风险主流程方法

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/BusinessServiceService.java`
- `getTopologyForBusinessService()`：拆成 `loadEntryHosts()`、`expandDownstreamHosts()`、`collectDiscoveredEdges()`、`loadClusterLookup()`、`buildTopologyNodes()`、`prependBusinessServiceEdges()`。
- `migrateFromBusinessField()`：拆成 `buildClusterGroupMap()`、`groupHostsByBusinessAndGroup()`、`collectMigratedHostIds()`、`businessServiceExists()`、`buildMigrationBody()`。
- 目标：公开方法只保留“加载 -> 扩展 -> 组装 -> 返回”主链路，避免多层 `for/try/if` 交织。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/monitoring/MetricsCollector.java`
- `doCollect()`：拆成 `loadRunningInstances()`、`fetchInsights()`、`sumInsights()`、`computeTimingStats()`、`computeTokensPerSecond()`、`buildSnapshot()`。
- 引入小型私有记录类或静态内部类，例如 `TimingStats`、`InsightTotals`，降低返回多个统计量时的参数噪音。
- 保持 `collect()` 外层异常兜底逻辑不变。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/process/InstanceManager.java`
- `doSpawn()`：按“并发复用检查 -> 限流校验 -> runtime 准备 -> 进程启动 -> drain 线程 -> readiness 检查”拆 5-6 个私有步骤。
- 建议新增 `enforceInstanceLimits()`、`prepareRuntimeForSpawn()`、`startGoosedProcess()`、`startDrainThread()`、`registerAndWaitReady()`。
- `buildEnvironment()`：拆成 `applyScalarEnv()`、`applyGooseCoreEnv()`、`generateInstanceSecret()`、`applyGatewayCallbackEnv()`、`applyGatewayApiPassword()`。
- 注意：不得改变 `GOOSE_SERVER__SECRET_KEY`、`XDG_CONFIG_HOME`、`GOOSE_TLS` 等关键环境变量的生成与日志语义。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/RemoteExecutionService.java`
- `execute()`：拆成 `resolveExecutionContext()`、`resolveEffectiveCommand()`、`openSshSession()`、`configureAuthentication()`、`executeAndCapture()`、`disconnectQuietly()`。
- 使用已有 `ExecutionContext`、`EnvResolution`、`CommandResolution` 继续承载中间数据，避免新建过多状态变量。
- 保证返回字段 `command`、`effectiveCommand`、`duration` 与异常分支结果结构保持兼容。

### B. 再处理聚合/转换型长方法

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/LangfuseService.java`
- `doBuildOverview()`：拆为 `normalizeApiArray()`、`aggregateTraceMetrics()`、`aggregateObservationMetrics()`、`buildDailyOverview()`、`buildOverviewPayload()`。
- 引入 `OverviewAccumulator` 之类的私有聚合对象，统一持有 `totalCost`、`errorCount`、`dailyMap`、`latencies`。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/InternalRuntimeSourceController.java`
- `metrics()`：拆为 `buildCurrentMetrics()`、`aggregateSnapshots()`、`buildSeries()`。
- 用 `MetricsAggregate` 小对象封装 `totalRequests/avgLatency/p95...`，避免控制器方法内维护一组平行变量。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/ClusterRelationService.java`
- `buildGraphNodes()`：拆为 `buildClusterGraphNodes()`、`mergeClusterHostsIntoHostNodes()`、`buildHostGraphNodes()`、`buildBusinessServiceNodes()`。
- `getClusterNeighbors()`：拆为 `loadActiveHostsByClusterMode()`、`appendDownstreamNeighbor()`、`appendUpstreamNeighbor()`、`buildNeighborPayload()`。
- `createRelation()`：拆为 `validateRelationBody()`、`validateRelationSource()`、`validateRelationTarget()`、`buildRelationEntity()`、`syncBusinessServiceIfNeeded()`。
- 这些方法已有明显阶段边界，重构后可进一步与 `updateRelation()`、`deleteRelation()` 共享校验逻辑。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/channel/SessionBridgeService.java`
- `extractFinalAssistantText()`：拆为 `ensureNoErrorEvent()`、`isVisibleAssistantMessage()`、`appendTextContent()`。
- 目标是把循环体改成“过滤 + 追加”的结构，减少 `continue` 链和深层 `instanceof` 嵌套。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/SessionTraceService.java`
- `collect()`：拆为 `prepareTraceCollection()`、`buildCollectorCommand()`、`runCollectorProcess()`、`finalizeArchive()`、`markTraceFailure()`。
- 保留 `finally` 中的 `completedAt` 与 `runningBySession.remove()`，避免泄露运行态。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/FileService.java`
- `diffFiles()`：拆为 `resolveAllowedExtensions()`、`indexFilesByIdentity()`、`isCapsuleRelevantChange()`、`toChangedFileEntry()`。
- 重点消除配置解析与比对扫描的混杂逻辑。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/finops/UsageSnapshotService.java`
- `readSessions()`：拆为 `readSessionRow()`、`parseSessionRecipe()`、`parseSessionModelConfig()`、`toSessionUsageRecord()`。
- 使用单行 `while (rs.next()) { ... }` 主循环 + 行级转换助手，减少构造记录时的巨大参数块。

### C. 处理协议/解析与输入校验型长方法

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/proxy/GoosedProxy.java`
- `proxySessionEventsToEmitter()`：拆为 `buildEventStreamRequest()`、`applyLastEventId()`、`handleUpstreamSseResponse()`、`subscribeEventStream()`。
- `sendSseFrame()`：拆为 `normalizeSseFrame()`、`applySseField()`、`parseRetryField()`。
- 保持关键约束：仍在 `exchangeToMono` 生命周期内完成响应体消费与订阅，不能把已修复的响应体释放问题重新引入。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/ReplyController.java`
- `logResumeConversationDigest()`：拆为 `extractConversationNode()`、`countConversationInversions()`、`buildConversationDigestHead()`。
- 保持 debug-only 行为，不改变日志内容字段名。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/hook/FileAttachmentHook.java`
- `process()`：拆为 `parseContentItems()`、`buildUserAgentsDir()`、`validateReferencedPath()`。
- 优先使用 guard clause，方法主体控制在“解析 -> 遍历 text 项 -> 校验 path -> 返回”。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/CommandWhitelistService.java`
- `splitShellPipe()`：拆为 `handleEscape()`、`toggleQuoteState()`、`isShellDelimiter()`、`flushCurrentPart()`。
- 如拆分后仍接近 50 行，可引入局部状态对象 `ShellSplitState`，但不改变原有分隔语义。

### D. 处理 CRUD/配置装配型长方法

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/HostService.java`
- `createHost()`：拆为 `ensureUniqueHostName()`、`buildHostEntity()`、`encryptCredential()`、`syncNewHostRelations()`、`maskCredential()`。
- `updateHost()`：拆为 `loadHostOrThrow()`、`ensureUpdatedNameUnique()`、`applyMutableFields()`、`applyEncryptedCredential()`、`syncUpdatedHostRelations()`。
- `testConnection()`：拆为 `loadHostForConnectionTest()`、`configureSessionAuth()`、`buildConnectionSuccessResult()`、`buildConnectionFailureResult()`。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/service/AgentConfigService.java`
- `loadResidentInstances()`：拆为 `isResidentInstancesEnabled()`、`extractResidentEntries()`、`normalizeResidentAgentIds()`、`filterValidResidentAgentIds()`。
- `createAgent()`：拆为 `validateNewAgentId()`、`ensureAgentNameUnique()`、`createAgentDirectoryStructure()`、`copyOrSeedDefaultConfig()`、`registerCreatedAgent()`。
- 注意同时保持 registry、磁盘目录、缓存失效三者的顺序一致。

#### `gateway/gateway-service/src/main/java/com/huawei/opsfactory/gateway/controller/SessionController.java`
- `startSession()`：拆为 `resolveUserIdOrReturnError()`、`injectWorkingDir()`、`startGoosedSession()`、`resumeSessionExtensions()`。
- 保证 HTTP 返回仍返回 `startResponse`，避免误返回 `resumeResult`。

#### 其他中等复杂度方法
- `AgentSkillInstallService.install()`：拆成预校验、下载/解析、冲突处理、落盘更新。
- `HostGroupService.getTree()`：拆成树节点索引、根节点筛选、递归挂载子节点。
- `HostRelationService.getNeighbors()`、`HostRelationController.enrichWithBusinessServices()`、`InstanceWatchdog.checkInstanceHealth()`、`ServletWebConfig.corsFilter()` 等方法统一采用“构造配置/结果对象”的短小助手拆分。

### E. 测试方法改造

#### `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/controller/ReplyControllerRealProxyTest.java`
- 提取测试夹具工厂：
  - `startProxyTestServer(...)`
  - `buildReplyController(...)`
  - `buildMockMvcWithAlice(...)`
  - `stubCommonReplyFlow(...)`
  - `assertGatewayErrorEnvelope(...)`
- 三个长测试分别缩为“搭环境 -> 执行请求 -> 验证结果”三段，每段通过 helper 包装。
- 保持现有真实 Reactor Netty server 路径，不改为大面积 `vi.mock` 式替身。

#### `gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/e2e/RuntimeSourceEndpointE2ETest.java`
- `metrics_admin_returnsMetricsData()`：提取准备快照数据、触发接口、断言聚合字段/序列字段的助手，避免单测试囊括所有准备与断言细节。

## Assumptions & Decisions
- 决策：范围包含主源码与测试代码，最终需要把以上 36 个目标方法全部压到 `< 50` 行。
- 决策：嵌套深度按“最大不超过 5 层”执行，而不是字面“至少 5 层”。
- 决策：优先通过提取私有方法、局部记录类、聚合对象完成重构，不进行跨包迁移，不新增公共 API。
- 决策：行为兼容优先于“绝对最少方法数”；允许增加少量私有辅助方法或私有静态 record/class，只要不破坏现有序列化、日志与异常语义。
- 假设：当前行数统计口径以方法声明块的起止行计算，辅助方法新增后不要求类总行数下降，只要求目标方法达标。
- 假设：现有测试足以兜底主要行为；若在重构中暴露出未覆盖分支，仅补充与当前文件高度相关的小型单测，不新增大规模 mock-heavy 页面式测试。

## Verification Steps
- 静态核验：
  - 重新运行超长方法扫描脚本，确认上述 36 个目标方法均 `< 50` 行。
  - 对改动文件做一次嵌套深度检查，确认最大嵌套深度 `<= 5`。
- 编译/测试：
  - `cd gateway && mvn test`
  - 如全量测试过慢，至少先跑受影响测试类：
    - `ReplyControllerRealProxyTest`
    - `RuntimeSourceEndpointE2ETest`
    - `InstanceManagerTest` / `InstanceManagerExtendedTest`
    - `RemoteExecutionService` 相关测试
    - `BusinessServiceService` / `ClusterRelationService` / `HostService` 相关测试
- 手工复核重点：
  - `SessionController.startSession()` 仍返回 `startResponse`
  - `GoosedProxy.proxySessionEventsToEmitter()` 仍在同一响应生命周期消费 SSE
  - `InstanceManager.buildEnvironment()` 关键环境变量与日志不漂移
  - `HostService` 的 credential 加密/掩码逻辑不变化
  - `ReplyController` 与 `SessionBridgeService` 的日志、错误与可见文本提取行为不回归

## Suggested Execution Order
1. 先改 `BusinessServiceService`、`MetricsCollector`、`InstanceManager`、`RemoteExecutionService`、`GoosedProxy` 这 5 个最长且风险最高的文件。
2. 再改聚合/转换类：`LangfuseService`、`InternalRuntimeSourceController`、`ClusterRelationService`、`SessionTraceService`、`UsageSnapshotService`。
3. 再改 CRUD/配置类与控制器：`HostService`、`AgentConfigService`、`SessionController`、`ReplyController`、`FileAttachmentHook`、`FileService`、`CommandWhitelistService` 等。
4. 最后压缩测试方法并回跑相关测试，随后重新扫描长方法与嵌套深度。
