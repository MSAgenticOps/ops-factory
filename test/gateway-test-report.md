# Gateway 模块测试报告

**日期**: 2026-03-09
**测试结果**: **全部通过** (358 tests, 0 failures, 0 errors, 0 skipped)
**构建工具**: Maven + JUnit 4 + Mockito + Spring Boot Test
**执行时间**: ~6 秒

---

## 一、测试结果总览

| 模块 | 测试类 | 用例数 | 状态 |
|------|--------|--------|------|
| **gateway-common** | | **41** | **PASS** |
| | ManagedInstanceTest | 12 | PASS |
| | PathSanitizerTest | 9 | PASS |
| | YamlLoaderTest | 9 | PASS |
| | ProcessUtilTest | 5 | PASS |
| | UserRoleTest | 3 | PASS |
| | AgentRegistryEntryTest | 3 | PASS |
| **gateway-service** | | **317** | **PASS** |
| **单元测试** | | **208** | **PASS** |
| Controllers | AgentControllerTest | 17 | PASS |
| | MonitoringControllerTest | 12 | PASS |
| | **CatchAllProxyControllerTest** ★ | **9** | **PASS** |
| | StatusControllerTest | 5 | PASS |
| Config | CorsFilterTest | 16 | PASS |
| | GatewayPropertiesTest | 8 | PASS |
| | **GlobalExceptionHandlerTest** ★ | **5** | **PASS** |
| Proxy | **GoosedProxyExtendedTest** ★ | **6** | **PASS** |
| | GoosedProxyTest | 2 | PASS |
| | SseRelayServiceTest | 1 | PASS |
| Services | AgentConfigServiceTest | 27 | PASS |
| | **LangfuseServiceBuildOverviewTest** ★ | **15** | **PASS** |
| | **FileServiceExtendedTest** ★ | **17** | **PASS** |
| | FileServiceTest | 9 | PASS |
| | SessionServiceTest | 4 | PASS |
| | LangfuseServiceTest | 3 | PASS |
| Filters | AuthWebFilterTest | 6 | PASS |
| | UserContextFilterTest | 4 | PASS |
| Hooks | VisionPreprocessHookTest | 16 | PASS |
| | FileAttachmentHookTest | 10 | PASS |
| | HookPipelineTest | 4 | PASS |
| | BodyLimitHookTest | 3 | PASS |
| | HookContextTest | 3 | PASS |
| Process | **InstanceManagerExtendedTest** ★ | **11** | **PASS** |
| | InstanceManagerTest | 8 | PASS |
| | **PrewarmServiceTest** ★ | **7** | **PASS** |
| | RuntimePreparerTest | 4 | PASS |
| | IdleReaperTest | 4 | PASS |
| | PortAllocatorTest | 2 | PASS |
| **E2E 测试** | | **109** | **PASS** |
| | AgentEndpointE2ETest | 21 | PASS |
| | MonitoringEndpointE2ETest | 17 | PASS |
| | AuthFilterE2ETest | 13 | PASS |
| | FileEndpointE2ETest | 11 | PASS |
| | SessionEndpointE2ETest | 11 | PASS |
| | **MonitoringEndpointExtendedE2ETest** ★ | **9** | **PASS** |
| | ReplyEndpointE2ETest | 9 | PASS |
| | McpEndpointE2ETest | 8 | PASS |
| | **CatchAllProxyEndpointE2ETest** ★ | **7** | **PASS** |
| | **SessionEndpointExtendedE2ETest** ★ | **7** | **PASS** |
| | StatusEndpointE2ETest | 5 | PASS |
| | **FileEndpointExtendedE2ETest** ★ | **2** | **PASS** |

> ★ 标记为本次新增的测试类

---

## 二、本次新增测试详情

### 2.1 CatchAllProxyControllerTest (单元测试, 9 用例)

**新增原因**: `CatchAllProxyController` 此前完全无测试，作为所有非标路径的入口控制器，权限分流逻辑无任何覆盖。

| 用例 | 覆盖场景 |
|------|---------|
| testAdminAccessToAdminRoute_proxies | Admin 访问 admin-only 路由 → 代理转发 |
| testUserAccessToUserAccessibleRoute_allowed | User 访问 /system_info → 允许 |
| testUserAccessToStatusRoute_allowed | User 访问 /status → 允许 |
| testUserAccessToAdminRoute_returns403 | User 访问 admin-only 路由 → 403 |
| testShortPath_returns404 | 路径段不足 → 404 |
| testQueryStringForwarding | Query string 正确转发 |
| testAdminUsesOwnUserId | Admin 使用自身 userId |
| testUserAccessToSystemInfoSubpath_allowed | User 访问 /system_info/details 子路径 → 允许 |
| testNullRole_treatedAsNonAdmin_returns403ForAdminRoute | 无角色属性 → 403 |

### 2.2 CatchAllProxyEndpointE2ETest (E2E, 7 用例)

| 用例 | 覆盖场景 |
|------|---------|
| adminAccessToSchedules_proxiesToGoosed | Admin 访问 schedules → 代理 |
| userAccessToSystemInfo_allowed | User 访问 system_info → 200 |
| userAccessToStatus_allowed | User 访问 status → 200 |
| userAccessToAdminRoute_returns403 | User 访问 schedules → 403 |
| userAccessToConfigPrompts_returns403 | User 访问 config/prompts → 403 |
| unauthenticated_returns401 | 未认证 → 401 |
| queryStringForwarded_toGoosed | Query string 透传验证 |

### 2.3 PrewarmServiceTest (单元测试, 7 用例)

**新增原因**: `PrewarmService` 为新增文件，完全无测试。

| 用例 | 覆盖场景 |
|------|---------|
| testOnUserActivity_disabled_doesNotSpawn | 禁用时不触发预热 |
| testOnUserActivity_sysUser_doesNotSpawn | sys 用户不触发预热 |
| testOnUserActivity_newUser_triggersSpawn | 新用户首次请求触发预热 |
| testOnUserActivity_alreadyWarmedUser_doesNotSpawnAgain | 已预热用户不重复触发 |
| testClearUser_allowsRewarm | clearUser 后允许重新预热 |
| testOnUserActivity_customDefaultAgent | 自定义默认 agent |
| testOnUserActivity_spawnError_doesNotThrow | 预热失败不抛异常 |

### 2.4 GoosedProxyExtendedTest (单元测试, 6 用例)

**新增原因**: 原有测试仅覆盖 getter，核心 header 处理逻辑无覆盖。

| 用例 | 覆盖场景 |
|------|---------|
| testCopyHeaders_injectsSecretKey | 请求头注入 x-secret-key |
| testCopyHeaders_overridesExistingSecretKey | 覆盖客户端提供的 secret key |
| testCopyUpstreamHeaders_filtersCorsHeaders | 过滤上游 CORS 头 (6 种) |
| testCopyUpstreamHeaders_emptySource | 空源头处理 |
| testFetchJson_returnsNonNullMono | fetchJson 构造正确性 |
| testProxyWithBody_returnsNonNullMono | proxyWithBody 构造正确性 |

### 2.5 InstanceManagerExtendedTest (单元测试, 11 用例)

**新增原因**: 核心的 `buildEnvironment`、实例限制、死进程检测、调度重置等逻辑此前无覆盖。

| 用例 | 覆盖场景 |
|------|---------|
| testBuildEnvironment_coreEnvVars | 核心环境变量 (GOOSE_PORT, HOST, SECRET_KEY, PATH_ROOT) |
| testBuildEnvironment_mergesAgentConfig | 合并 agent config.yaml + secrets.yaml 为环境变量 |
| testBuildEnvironment_secretsOverrideConfig | secrets 优先级高于 config |
| testBuildEnvironment_nonScalarValuesSkipped | 非标量值 (Map/List) 不注入 |
| testGetOrSpawn_deadProcess_removesStaleEntry | 进程已死 → 移除陈旧条目 |
| testResetStuckRunningSchedules_fixesStuckJobs | 重置卡住的 currently_running=true |
| testResetStuckRunningSchedules_noStuckJobs_noChange | 无卡住任务 → 不修改文件 |
| testResetStuckRunningSchedules_noScheduleFile_noop | 无 schedule.json → 无操作 |
| testPerUserLimitEnforced | 每用户实例限制 |
| testGlobalLimitEnforced | 全局实例限制 |
| testStoppedInstancesNotCountedForPerUserLimit | 已停止实例不计入限制 |

### 2.6 LangfuseServiceBuildOverviewTest (单元测试, 15 用例)

**新增原因**: 原有测试仅覆盖 `isConfigured()`，核心数据聚合和解析逻辑完全无覆盖。

| 用例 | 覆盖场景 |
|------|---------|
| testBuildOverview_withTracesAndObservations | 含 traces + observations 的完整聚合 (总数、成本、延迟、P95、错误数、日粒度) |
| testBuildOverview_emptyData | 空数据集聚合 |
| testBuildOverview_rawArrayFormat | 非 {data:[]} 包装的原始数组格式 |
| testParseTraces_normalData | 正常 trace 解析 (id, name, input, latency, observationCount, hasError, errorMessage) |
| testParseTraces_emptyArray | 空数组 |
| testParseTraces_nonArrayData | 非数组 data 字段 |
| testParseObservations_groupedByName | 按 name 分组聚合 (avgLatency, p95, totalTokens, totalCost) |
| testParseObservations_emptyData | 空数据 |
| testParseObservations_fallbackTokenCount | 无 totalTokens 时回退到 promptTokens + completionTokens |
| testEmptyOverview | emptyOverview 静态方法 |
| testGetTracesFormatted_notConfigured_returnsEmptyList | 未配置时返回空列表 |
| testGetObservationsFormatted_notConfigured_returnsEmptyMap | 未配置时返回空 map |
| testGetOverview_notConfigured_returnsEmptyOverview | 未配置时返回空 overview |
| testGetTraces_notConfigured_returnsEmptyArray | 未配置时返回 "[]" |
| testGetObservations_notConfigured_returnsEmptyArray | 未配置时返回 "[]" |

### 2.7 FileServiceExtendedTest (单元测试, 17 用例)

**新增原因**: `isAllowedExtension`、SKIP_DIRS/SKIP_FILES 过滤、fallback 搜索此前无覆盖。

| 用例 | 覆盖场景 |
|------|---------|
| testIsAllowedExtension_allowedTypes | 8 种允许扩展名 |
| testIsAllowedExtension_blockedTypes | 8 种被拦截扩展名 (exe, bat, dll 等) |
| testIsAllowedExtension_noExtension_allowed | 无扩展名文件允许上传 |
| testIsAllowedExtension_unknownExtension_rejected | 未知扩展名被拒绝 |
| testIsAllowedExtension_caseInsensitive | 大小写不敏感 |
| testListFiles_skipsDirs_data | 跳过 data 目录 |
| testListFiles_skipsDirs_state | 跳过 state 目录 |
| testListFiles_skipsDirs_config | 跳过 config 目录 |
| testListFiles_skipsDirs_nodeModules | 跳过 node_modules 目录 |
| testListFiles_skipsDirs_dotGoose | 跳过 .goose 目录 |
| testListFiles_skipsHiddenDirs | 跳过 `.` 开头的隐藏目录 |
| testListFiles_skipsDSStore | 跳过 .DS_Store 文件 |
| testListFiles_skipsAGENTSmd | 跳过 AGENTS.md 文件 |
| testListFiles_skipsGitkeep | 跳过 .gitkeep 文件 |
| testResolveFile_fallbackSearch_findsInSubdir | 回退搜索：在子目录找到文件 |
| testResolveFile_fallbackSearch_skipsSkipDirs | 回退搜索：不搜索 skip 目录 |
| testResolveFile_directPath_existsInSubdir | 直接路径解析：子目录文件 |

### 2.8 SessionEndpointExtendedE2ETest (E2E, 7 用例)

**新增原因**: 全局 session 端点、重命名 session、session 404 此前无 E2E 覆盖。

| 用例 | 覆盖场景 |
|------|---------|
| getSessionGlobal_authenticated_returnsSessionWithAgentId | GET /sessions/{id}?agentId=X → 200 + agentId 注入 |
| getSessionGlobal_unauthenticated_returns401 | 未认证 → 401 |
| deleteSessionGlobal_authenticated_removesOwnerAndProxies | DELETE /sessions/{id}?agentId=X → removeOwner + proxy |
| deleteSessionGlobal_unauthenticated_returns401 | 未认证 → 401 |
| renameSession_authenticated_proxiesToGoosed | PUT .../sessions/{id}/name → 代理 |
| renameSession_unauthenticated_returns401 | 未认证 → 401 |
| getSession_notFoundFromGoosed_returns404 | goosed 返回 404 → 网关返回 404 |

### 2.9 MonitoringEndpointExtendedE2ETest (E2E, 9 用例)

**新增原因**: overview、traces、observations 缺少参数时的 400 响应此前无覆盖。

| 用例 | 覆盖场景 |
|------|---------|
| overview_missingFromAndTo_returns400 | /overview 缺少 from + to → 400 |
| overview_missingTo_returns400 | /overview 缺少 to → 400 |
| overview_missingFrom_returns400 | /overview 缺少 from → 400 |
| traces_missingFromAndTo_returns400 | /traces 缺少 from + to → 400 |
| traces_missingTo_returns400 | /traces 缺少 to → 400 |
| traces_missingFrom_returns400 | /traces 缺少 from → 400 |
| observations_missingFromAndTo_returns400 | /observations 缺少 from + to → 400 |
| observations_missingTo_returns400 | /observations 缺少 to → 400 |
| observations_missingFrom_returns400 | /observations 缺少 from → 400 |

### 2.10 FileEndpointExtendedE2ETest (E2E, 2 用例)

| 用例 | 覆盖场景 |
|------|---------|
| getFile_pathTraversal_returns403 | 路径穿越请求 → 403 |
| uploadFile_notMultipart_returns400 | 非 multipart 上传 → 400 |

### 2.11 GlobalExceptionHandlerTest (单元测试, 5 用例)

**新增原因**: 全局异常处理器此前完全无测试。

| 用例 | 覆盖场景 |
|------|---------|
| testHandleInputException_decodingError | JSON 解析错误 → "Invalid JSON body" |
| testHandleInputException_otherError | 其他输入错误 → 携带 reason |
| testHandleResponseStatusException_withReason | 带 reason 的状态异常 (404) |
| testHandleResponseStatusException_forbidden | 403 异常 |
| testHandleResponseStatusException_noReason | 无 reason 时 fallback 到 getMessage() |

---

## 三、仍未覆盖的内容及建议

以下列出当前测试仍未覆盖的场景，以及测试人员手动覆盖的建议。

### 3.1 需要真实 goosed 进程的场景（建议集成测试）

这些场景需要启动真实的 `goosed` 二进制，无法在单元测试中模拟：

| 场景 | 所在文件 | 建议 |
|------|---------|------|
| `InstanceManager.doSpawn` 完整流程 | InstanceManager.java | 编写集成测试，启动真实 goosed binary，验证进程启动、端口分配、健康检查完整流程 |
| `InstanceManager.waitForReady` 健康检查轮询 | InstanceManager.java | 同上，验证重试逻辑和超时行为 |
| `InstanceManager.autoStartSysOnlyAgents` @PostConstruct | InstanceManager.java | 在集成测试中配置 sysOnly agent，验证网关启动时自动拉起 |
| `InstanceManager.registerDefaultSchedules` 调度注册 | InstanceManager.java | 准备 recipes/ 目录和 goosed 实例，验证 schedule 创建和暂停 |
| `GoosedProxy.proxy` 完整 HTTP 代理 | GoosedProxy.java | 使用 MockWebServer (需引入 okhttp3 依赖) 或 WireMock 模拟 goosed HTTP 端点 |
| `GoosedProxy.proxyWithBody` 带请求体代理 | GoosedProxy.java | 同上 |
| `GoosedProxy.fetchJson` JSON 获取 | GoosedProxy.java | 同上 |
| `SseRelayService.relay` SSE 中继 | SseRelayService.java | 使用 MockWebServer 模拟 SSE 流，验证数据传输和错误处理 |
| `SessionService.getSessionsFromInstance` HTTP 调用 | SessionService.java | 使用 MockWebServer 测试 HTTP 请求构造和响应解析 |
| `LangfuseService.doGet` / `checkReachable` HTTP 调用 | LangfuseService.java | 使用 MockWebServer 验证 Basic Auth 头、URL 构造、超时行为 |

**建议引入依赖**：
```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>mockwebserver</artifactId>
    <version>4.12.0</version>
    <scope>test</scope>
</dependency>
```

### 3.2 文件上传完整路径（建议 E2E 测试）

| 场景 | 建议 |
|------|------|
| multipart 文件上传成功 | 使用 `WebTestClient.post().uri(...).body(BodyInserters.fromMultipartData(...))` 构造真实 multipart 请求，mock `AgentConfigService.getUsersDir()` 指向临时目录 |
| 上传被拦截的扩展名 (.exe) | 同上，验证返回 400 + "File type not allowed" |
| 上传成功时返回文件元数据 | 验证 response 包含 status, filename, path, name, type, size |

### 3.3 MCP fanout 逻辑（建议单元测试）

| 场景 | 建议 |
|------|------|
| `McpController.fanoutPost` 广播到所有用户实例 | Mock `InstanceManager.getAllInstances()` 返回多个实例，验证 WebClient 对每个实例发起 POST |
| `McpController.fanoutDelete` 广播删除 | 同上，验证 DELETE 请求 |
| fanout 中单个实例失败不影响整体 | 模拟部分实例 HTTP 调用失败，验证不抛异常 |

### 3.4 VisionPreprocessHook 真实图像处理（建议集成测试）

| 场景 | 建议 |
|------|------|
| `preprocess` 模式 + Anthropic provider | 使用 MockWebServer 模拟 Anthropic API 响应，验证请求体结构 |
| `preprocess` 模式 + OpenAI provider | 使用 MockWebServer 模拟 OpenAI API 响应，验证请求体结构 |
| 图像 Base64 编码处理 | 提供小型测试图片，验证 Base64 编码和请求构造 |
| `vision_base_url` / `LITELLM_HOST` 等 env 回退链 | 设置不同组合的配置和环境变量，验证优先级 |

### 3.5 边界和异常情况

| 场景 | 所在文件 | 建议 |
|------|---------|------|
| `SessionController.extractSessionsArray` raw array fallback | SessionController.java | 单元测试 `extractSessionsArray` 方法，传入 `[{...}]` 格式而非 `{"sessions":[...]}` |
| `SessionController.injectAgentId` JSON 解析失败 | SessionController.java | 传入非法 JSON，验证返回原始字符串 |
| `SessionController.cleanupUploads` 目录删除 | SessionController.java | 创建临时 uploads 目录，调用 deleteSession，验证目录被清理 |
| `AgentController.getConfig` vision.mode 提取逻辑 | AgentController.java | Mock agentConfigYaml 包含嵌套 vision.mode，验证 visionMode 字段 |
| `FileAttachmentHook` 同一文本多个文件路径 | FileAttachmentHook.java | 构造包含多个 `/path/to/file` 的文本 content，验证全部校验 |
| `IdleReaper` prewarmService.clearUser 仅在无残余实例时调用 | IdleReaper.java | Mock 场景：用户有 2 实例，reap 1 个，验证 clearUser 未被调用 |
| `UserContextFilter` verify prewarmService.onUserActivity | UserContextFilter.java | 在现有单元测试中添加 `verify(prewarmService).onUserActivity(userId)` |
| `BodyLimitHook` 边界值测试 | BodyLimitHook.java | body 长度恰好等于限制值时的行为 |
| `MonitoringController.formatUptime` 天/小时/分钟各分支 | MonitoringController.java | 单元测试私有方法，覆盖 days > 0、hours > 0、minutes only 三种格式 |
| `WebFluxConfig` 编解码器配置 | WebFluxConfig.java | 验证 maxInMemorySize 等编解码器配置正确应用 |
| `SchedulingConfig` 注解验证 | SchedulingConfig.java | 验证 @EnableScheduling 注解存在 |

---

## 四、测试覆盖率变化

### 本次新增前后对比

| 源文件 | 新增前 | 新增后 |
|--------|--------|--------|
| CatchAllProxyController | **无测试** | 单元 9 + E2E 7 = **16** |
| PrewarmService | **无测试** | 单元 **7** |
| GlobalExceptionHandler | **无测试** | 单元 **5** |
| GoosedProxy | getter 2 用例 | getter 2 + header/CORS **6** = **8** |
| InstanceManager | 基础 8 用例 | 基础 8 + 扩展 **11** = **19** |
| LangfuseService | isConfigured 3 用例 | isConfigured 3 + 解析/聚合 **15** = **18** |
| FileService | 基础 9 用例 | 基础 9 + 扩展 **17** = **26** |
| SessionController (E2E) | 11 用例 | 11 + **7** = **18** |
| MonitoringController (E2E) | 17 用例 | 17 + **9** = **26** |
| FileController (E2E) | 11 用例 | 11 + **2** = **13** |

### 总测试数量

- **新增前**: 276 tests
- **新增后**: 358 tests
- **净增**: +82 tests (+30%)

---

## 五、新增测试文件清单

```
gateway/gateway-service/src/test/java/com/huawei/opsfactory/gateway/
├── config/
│   └── GlobalExceptionHandlerTest.java                    ★ 新增
├── controller/
│   └── CatchAllProxyControllerTest.java                   ★ 新增
├── e2e/
│   ├── CatchAllProxyEndpointE2ETest.java                  ★ 新增
│   ├── FileEndpointExtendedE2ETest.java                   ★ 新增
│   ├── MonitoringEndpointExtendedE2ETest.java             ★ 新增
│   └── SessionEndpointExtendedE2ETest.java                ★ 新增
├── process/
│   ├── InstanceManagerExtendedTest.java                   ★ 新增
│   └── PrewarmServiceTest.java                            ★ 新增
├── proxy/
│   └── GoosedProxyExtendedTest.java                       ★ 新增
└── service/
    ├── FileServiceExtendedTest.java                       ★ 新增
    └── LangfuseServiceBuildOverviewTest.java              ★ 新增
```

共新增 **11 个测试文件**，**82 个测试用例**。
