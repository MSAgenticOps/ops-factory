# Operation Intelligence 对接说明

## 1. 目标与边界

`operation-intelligence` 是 Ops Factory 中独立的 QoS 健康曲线数据采集、计算与查询服务，负责：

- 定时从 DV 系统采集性能指标与告警数据
- 对原始数据进行归一化处理
- 按分段策略计算各维度评分
- 计算加权综合健康评分
- 提供健康指标、明细数据与贡献度分析查询 API

适用范围：

- 前端需要展示 QoS 健康曲线与指标明细
- 其他后端服务需要获取运维健康评分数据
- 管理台需要查看贡献度分析与配置规则

非边界：

- 不负责用户鉴权、多租户鉴权策略。本服务当前接口本身未定义鉴权头，接入方需要在上层网关统一收口。
- 不负责 DV 系统的数据生产与管理。
- 不负责前端路由与页面渲染逻辑。

## 2. 运行与配置加载

### 2.1 配置来源

服务启动时会加载：

- `operation-intelligence/src/main/resources/application.yaml`
- 运行目录下的 `./config.yaml`（`spring.config.import: optional:file:./config.yaml`）

环境变量 `OI_CONFIG_PATH` 可用于显式指定配置文件路径，优先级高于默认的 `./config.yaml`。

### 2.2 本地启动

在服务目录下执行：

```bash
cd operation-intelligence
mvn spring-boot:run
```

打包运行：

```bash
cd operation-intelligence
mvn package
java -jar target/operation-intelligence.jar
```

### 2.3 运行目录

服务默认运行目录为 `./data`，通常包含：

- `raw/<environment>/`：原始采集数据 JSON 文件
- `normalize/<environment>/`：归一化数据 JSON 文件
- `detail/<environment>/`：评分明细数据 JSON 文件

建议：

- 生产环境使用独立磁盘路径，不要和代码目录混放
- 把数据目录指到可持久化目录
- 上层部署时提前评估磁盘容量，长期运行会累积 JSON 文件

## 3. 配置项说明

示例文件见 `operation-intelligence/config.yaml.example`。

### 3.1 运行时配置

```yaml
operation-intelligence:
  server:
    port: 8096
    address: "0.0.0.0"
  qos:
    enabled: true
    collect-interval: "0 */5 * * * *"
    rotation-interval: 300
    retention-days: 30
  logging:
    level:
      root: INFO
      com.huawei.opsfactory.operationintelligence: INFO
```

#### `operation-intelligence.server.port`

- 含义：服务监听端口
- 默认值：`8096`

#### `operation-intelligence.server.address`

- 含义：服务监听地址
- 默认值：`0.0.0.0`

### 3.2 QoS 配置

#### `operation-intelligence.qos.enabled`

- 含义：是否启用 QoS 数据采集与计算
- 默认值：`true`
- 说明：设为 `false` 时服务仅提供查询 API，不执行定时采集任务

#### `operation-intelligence.qos.collect-interval`

- 含义：数据采集调度 cron 表达式
- 默认值：`0 */5 * * * *`（每 5 分钟执行一次）
- 说明：遵循标准 `cron` 格式

#### `operation-intelligence.qos.rotation-interval`

- 含义：数据轮转间隔，单位秒
- 默认值：`300`
- 说明：采集任务按此间隔写入新 JSON 文件

#### `operation-intelligence.qos.retention-days`

- 含义：数据保留天数
- 默认值：`30`
- 说明：超出保留天数的 JSON 文件会在采集任务执行时被清理

### 3.3 评分权重配置

```yaml
operation-intelligence:
  qos:
    weights:
      availability: 0.4
      performance: 0.3
      resource: 0.3
```

#### `operation-intelligence.qos.weights.availability`

- 含义：可用性维度权重
- 默认值：`0.4`
- 约束：三个权重之和必须为 `1.0`

#### `operation-intelligence.qos.weights.performance`

- 含义：性能维度权重
- 默认值：`0.3`

#### `operation-intelligence.qos.weights.resource`

- 含义：资源/告警维度权重
- 默认值：`0.3`

### 3.4 评分阈值配置

```yaml
operation-intelligence:
  qos:
    thresholds:
      availability:
        - 99.99
        - 99.9
        - 99.0
        - 95.0
      performance:
        p50: 200
        p90: 500
        p95: 1000
        p99: 3000
      alarm:
        low: 5
        medium: 20
        high: 50
```

#### `operation-intelligence.qos.thresholds.availability`

- 含义：可用性分段阈值（百分比），从高到低排列
- 说明：对应评分 100/90/70/50/30 五档

#### `operation-intelligence.qos.thresholds.performance`

- 含义：性能分段阈值（毫秒）
- 说明：`p50`/`p90`/`p95`/`p99` 分别对应响应时间百分位阈值

#### `operation-intelligence.qos.thresholds.alarm`

- 含义：告警密度分段阈值
- 说明：`low`/`medium`/`high` 对应告警数量阈值

### 3.5 DV 环境配置

```yaml
operation-intelligence:
  dv:
    environments:
      - name: "production"
        baseUrl: "https://dv.example.com"
        authType: "token"
        token: "${DV_PRODUCTION_TOKEN}"
        sslVerify: true
        connectTimeout: 5000
        readTimeout: 30000
      - name: "staging"
        baseUrl: "https://dv-staging.example.com"
        authType: "token"
        token: "${DV_STAGING_TOKEN}"
        sslVerify: true
        connectTimeout: 5000
        readTimeout: 30000
```

#### `operation-intelligence.dv.environments[].name`

- 含义：环境标识，用于查询接口中指定目标环境
- 说明：全局唯一

#### `operation-intelligence.dv.environments[].baseUrl`

- 含义：DV 系统 API 地址
- 说明：必须包含协议前缀 `https://` 或 `http://`

#### `operation-intelligence.dv.environments[].authType`

- 含义：认证方式
- 当前支持：`token`

#### `operation-intelligence.dv.environments[].token`

- 含义：认证 token
- 说明：支持环境变量引用，例如 `${DV_PRODUCTION_TOKEN}`

#### `operation-intelligence.dv.environments[].sslVerify`

- 含义：是否验证 SSL 证书
- 默认值：`true`
- 说明：生产环境应保持 `true`，仅在测试环境可设为 `false`

#### `operation-intelligence.dv.environments[].connectTimeout`

- 含义：连接超时，单位毫秒
- 默认值：`5000`

#### `operation-intelligence.dv.environments[].readTimeout`

- 含义：读取超时，单位毫秒
- 默认值：`30000`

## 4. API 规范

### 4.1 基础约定

- Base Path：`/operation-intelligence/qos`
- 数据格式：`application/json`
- 通用请求结构：

```json
{
  "environment": "production",
  "startTime": "2026-05-01T00:00:00Z",
  "endTime": "2026-05-08T00:00:00Z"
}
```

### 4.2 错误返回

当前统一错误格式：

```json
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "No data found for environment: staging"
}
```

已实现错误码：

- `RESOURCE_NOT_FOUND`
  - HTTP `404`
  - 典型场景：指定环境不存在、时间范围内无数据
- `REQUEST_FAILED`
  - HTTP `400`
  - 典型场景：参数非法、时间范围格式错误
- `DV_CONNECTION_ERROR`
  - HTTP `502`
  - 典型场景：DV 系统连接失败、认证失败、超时

### 4.3 接口明细

#### `POST /operation-intelligence/qos/getHealthIndicator`

用途：获取综合健康评分与各维度评分概要。

请求体：

```json
{
  "environment": "production",
  "startTime": "2026-05-01T00:00:00Z",
  "endTime": "2026-05-08T00:00:00Z"
}
```

返回示例：

```json
{
  "healthScore": 85.6,
  "availability": { "score": 90, "weight": 0.4 },
  "performance": { "score": 82, "weight": 0.3 },
  "resource": { "score": 84, "weight": 0.3 },
  "timestamp": "2026-05-08T00:00:00Z"
}
```

#### `POST /operation-intelligence/qos/getAvailableIndicatorDetail`

用途：获取可用性维度明细数据。

请求体：

```json
{
  "environment": "production",
  "startTime": "2026-05-01T00:00:00Z",
  "endTime": "2026-05-08T00:00:00Z"
}
```

返回示例：

```json
{
  "availabilityRate": 99.95,
  "faultCount": 2,
  "mttr": 15.3,
  "score": 90,
  "details": []
}
```

#### `POST /operation-intelligence/qos/getPerformanceIndicatorDetail`

用途：获取性能维度明细数据。

请求体：

```json
{
  "environment": "production",
  "startTime": "2026-05-01T00:00:00Z",
  "endTime": "2026-05-08T00:00:00Z"
}
```

返回示例：

```json
{
  "p50": 120,
  "p90": 350,
  "p95": 800,
  "p99": 2500,
  "throughput": 1500,
  "score": 82,
  "details": []
}
```

#### `POST /operation-intelligence/qos/getResourceIndicatorDetail`

用途：获取资源维度明细数据。

请求体：

```json
{
  "environment": "production",
  "startTime": "2026-05-01T00:00:00Z",
  "endTime": "2026-05-08T00:00:00Z"
}
```

返回示例：

```json
{
  "cpuUsage": 65.2,
  "memoryUsage": 72.8,
  "diskUsage": 58.1,
  "score": 84,
  "details": []
}
```

#### `POST /operation-intelligence/qos/getAlarmIndicatorDetail`

用途：获取告警维度明细数据。

请求体：

```json
{
  "environment": "production",
  "startTime": "2026-05-01T00:00:00Z",
  "endTime": "2026-05-08T00:00:00Z"
}
```

返回示例：

```json
{
  "alarmCount": 12,
  "criticalCount": 1,
  "warningCount": 11,
  "score": 84,
  "distribution": []
}
```

#### `POST /operation-intelligence/qos/getContributionData`

用途：获取各子指标对总评分的贡献度分析。

请求体：

```json
{
  "environment": "production",
  "startTime": "2026-05-01T00:00:00Z",
  "endTime": "2026-05-08T00:00:00Z"
}
```

返回示例：

```json
{
  "contributions": [
    { "name": "availabilityRate", "weight": 0.4, "value": 90, "contribution": 36.0 },
    { "name": "responseTime", "weight": 0.3, "value": 82, "contribution": 24.6 },
    { "name": "alarmDensity", "weight": 0.3, "value": 84, "contribution": 25.2 }
  ],
  "totalScore": 85.8
}
```

#### `POST /operation-intelligence/qos/getProductConfigRule`

用途：获取当前生效的产品配置规则。

请求体：

```json
{
  "environment": "production"
}
```

返回示例：

```json
{
  "weights": { "availability": 0.4, "performance": 0.3, "resource": 0.3 },
  "thresholds": {
    "availability": [99.99, 99.9, 99.0, 95.0],
    "performance": { "p50": 200, "p90": 500, "p95": 1000, "p99": 3000 },
    "alarm": { "low": 5, "medium": 20, "high": 50 }
  }
}
```

#### `GET /operation-intelligence/qos/getEnvironments`

用途：获取已配置的 DV 环境列表。

返回示例：

```json
{
  "environments": [
    { "name": "production", "displayName": "生产环境" },
    { "name": "staging", "displayName": "预发环境" }
  ]
}
```

## 5. 集成建议

### 5.1 如果你是前端管理台

推荐接入：

1. 页面初始化先调 `getEnvironments` 获取环境列表
2. 调 `getProductConfigRule` 获取当前评分规则
3. 健康曲线主页调 `getHealthIndicator` 展示综合评分
4. 各维度详情页按需调对应的 `getXXXIndicatorDetail`
5. 贡献度分析调 `getContributionData`

### 5.2 如果你是网关

建议：

- 由网关统一暴露外部稳定接口和鉴权
- 网关可按业务侧需要再封装环境隔离、调用审计、限流和租户维度控制
- 不建议让浏览器直接绕过网关访问 `operation-intelligence`

### 5.3 如果你是其他后端服务

推荐接入：

1. 需要获取运维健康数据时，直接调用查询接口
2. 不要依赖 JSON 文件存储结构，只通过 API 获取数据
3. 大批量查询时注意时间范围，避免单次请求过重

## 6. cURL 示例

### 6.1 获取环境列表

```bash
curl http://127.0.0.1:8096/operation-intelligence/qos/getEnvironments
```

### 6.2 获取综合健康评分

```bash
curl -X POST http://127.0.0.1:8096/operation-intelligence/qos/getHealthIndicator \
  -H 'Content-Type: application/json' \
  -d '{
    "environment": "production",
    "startTime": "2026-05-01T00:00:00Z",
    "endTime": "2026-05-08T00:00:00Z"
  }'
```

### 6.3 获取可用性明细

```bash
curl -X POST http://127.0.0.1:8096/operation-intelligence/qos/getAvailableIndicatorDetail \
  -H 'Content-Type: application/json' \
  -d '{
    "environment": "production",
    "startTime": "2026-05-01T00:00:00Z",
    "endTime": "2026-05-08T00:00:00Z"
  }'
```

### 6.4 获取性能明细

```bash
curl -X POST http://127.0.0.1:8096/operation-intelligence/qos/getPerformanceIndicatorDetail \
  -H 'Content-Type: application/json' \
  -d '{
    "environment": "production",
    "startTime": "2026-05-01T00:00:00Z",
    "endTime": "2026-05-08T00:00:00Z"
  }'
```

### 6.5 获取资源明细

```bash
curl -X POST http://127.0.0.1:8096/operation-intelligence/qos/getResourceIndicatorDetail \
  -H 'Content-Type: application/json' \
  -d '{
    "environment": "production",
    "startTime": "2026-05-01T00:00:00Z",
    "endTime": "2026-05-08T00:00:00Z"
  }'
```

### 6.6 获取告警明细

```bash
curl -X POST http://127.0.0.1:8096/operation-intelligence/qos/getAlarmIndicatorDetail \
  -H 'Content-Type: application/json' \
  -d '{
    "environment": "production",
    "startTime": "2026-05-01T00:00:00Z",
    "endTime": "2026-05-08T00:00:00Z"
  }'
```

### 6.7 获取贡献度分析

```bash
curl -X POST http://127.0.0.1:8096/operation-intelligence/qos/getContributionData \
  -H 'Content-Type: application/json' \
  -d '{
    "environment": "production",
    "startTime": "2026-05-01T00:00:00Z",
    "endTime": "2026-05-08T00:00:00Z"
  }'
```

### 6.8 获取配置规则

```bash
curl -X POST http://127.0.0.1:8096/operation-intelligence/qos/getProductConfigRule \
  -H 'Content-Type: application/json' \
  -d '{
    "environment": "production"
  }'
```

## 7. 当前实现限制

接入方需要明确以下现状：

- 当前仅支持 JSON 文件存储，不支持数据库持久化
- 采集任务依赖定时调度，不支持实时推送
- DV 系统集成当前仅支持 `token` 认证方式
- 评分算法的权重与阈值通过配置文件管理，不支持运行时动态调整
- 前端当前直接访问 `operation-intelligence`，与独立服务部署模式一致

如果其他服务要基于这些能力做稳定集成，建议只依赖当前已经被测试覆盖的闭环：

- `getHealthIndicator` 综合健康评分查询
- `getAvailableIndicatorDetail` / `getPerformanceIndicatorDetail` / `getResourceIndicatorDetail` / `getAlarmIndicatorDetail` 各维度明细查询
- `getContributionData` 贡献度分析
- `getEnvironments` 环境列表查询
- `getProductConfigRule` 配置规则查询
