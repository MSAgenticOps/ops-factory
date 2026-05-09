# Operation Intelligence 技术架构文档

## 1. 文档目标

本文档说明 `operation-intelligence` 在 Ops Factory 中的实际技术架构，覆盖：

- QoS 健康曲线数据采集、计算、评分、存储与查询的完整路径
- 后端服务分层、数据模型与存储策略
- 健康评分算法与各维度的分段评分策略
- 对外接口列表与推荐使用方式
- 当前实现限制

本文以仓库当前实现为准。凡是"接口已经暴露但运行时尚未完全消费"的能力，会单独标注。

## 2. 定位与边界

`operation-intelligence` 是独立的 QoS 健康曲线数据采集、计算与查询服务，负责：

- 从外部 DV（Data Vault）系统定时采集性能指标与告警数据
- 对原始指标进行归一化、分段评分与加权健康评分计算
- 将原始数据与评分结果持久化到 JSON 文件存储
- 提供健康指标、明细数据、贡献度分析等查询 API

不负责的内容：

- 用户鉴权、租户隔离与访问控制策略
- Agent 管理与运行时生命周期
- DV 系统本身的数据生产与管理
- 前端路由与页面渲染逻辑

## 3. 总体架构

### 3.0 架构总览图

```text
+------------------------+          +---------------------------+
| web-app                |          | DV 系统                    |
| - 健康曲线页面          |          | - 性能指标                  |
| - 指标详情页面          |          | - 告警数据                  |
| - 贡献度分析页面        |          +-------------+-------------+
+-----------+------------+                        |
            | HTTP                                     HTTPS
            v                                          v
                +-----------------------------------+
                | operation-intelligence             |
                |-----------------------------------|
                | Controller                        |
                | QosService / QosCalculationService|
                | QosDataScheduler                  |
                | DvClient / DvAuthService           |
                | JsonFileStore                      |
                +---------+---------------+---------+
                          |
              +-----------+---+
              | JSON File     |
              | Store         |
              | - raw/        |
              | - normalize/  |
              | - detail/     |
              +---------------+
```

### 3.1 架构分层

1. 前端 `web-app`
   - 健康曲线页面：展示整体健康评分与趋势
   - 指标详情页面：按可用性、性能、资源/告警维度展示明细
   - 贡献度分析页面：展示各子指标对总分的贡献数据

2. 后端控制层
   - `QosController`
   - 接收前端请求，委托给 `QosService` 处理

3. 后端业务层
   - `QosService`
   - 负责查询归一化后数据、组装接口响应
   - `QosCalculationService`
   - 负责健康评分计算，包括各维度分段评分与加权汇总

4. 定时调度层
   - `QosDataScheduler`
   - 按固定间隔定时触发数据采集任务
   - 调用 `DvClient` 从 DV 系统拉取原始数据
   - 触发归一化与评分计算
   - 将结果写入 `JsonFileStore`

5. DV 集成层
   - `DvClient`
   - 封装与 DV 系统的 HTTP 交互，包括指标查询与告警查询
   - `DvAuthService`
   - 负责 DV 系统的认证与 token 管理

6. 持久化层
   - `JsonFileStore`
   - 基于文件系统的 JSON 存储
   - 按轮转间隔管理数据文件
   - 按保留天数清理过期数据

### 3.2 核心对象

- `HealthIndicator`：综合健康评分结果，包含总分与各维度分数
- `IndicatorDetail`：单个维度的明细数据，包含子指标值与分段评分
- `ContributionData`：各子指标对总分的贡献度分析结果
- `RawData`：从 DV 系统采集的原始性能与告警数据
- `NormalizedData`：经过归一化处理后的标准化指标数据

## 4. 数据流

### 4.1 采集流

```text
QosDataScheduler         DvClient           DV 系统          JsonFileStore
     |                      |                  |                  |
     | trigger (fixedDelay) |                  |                  |
     |--------------------->|                  |                  |
     |                      | fetch metrics    |                  |
     |                      |----------------->|                  |
     |                      |<-----------------|                  |
     |                      | fetch alarms     |                  |
     |                      |----------------->|                  |
     |                      |<-----------------|                  |
     |                      |                  |                  |
     | raw data             |                  |                  |
     |<---------------------|                  |                  |
     | write raw                                              |
     |------------------------------------------------------->|
     | normalize + calculate score                            |
     |------------------------------------------------------->|
     | write normalize + detail                               |
     |------------------------------------------------------->|
```

采集流程说明：

1. `QosDataScheduler` 按固定间隔定时触发
2. `DvClient` 向 DV 系统发起性能指标与告警数据查询请求
3. `DvAuthService` 自动处理认证与 token 刷新
4. 原始数据写入 `JsonFileStore` 的 `raw/` 目录
5. 对原始数据执行归一化处理，生成标准化指标
6. 归一化数据写入 `normalize/` 目录
7. 计算各维度分段评分与综合健康评分
8. 评分明细写入 `detail/` 目录

### 4.2 查询流

```text
web-app              QosController       QosService         JsonFileStore
  |                       |                   |                    |
  | POST /getHealth...    |                   |                    |
  |---------------------->|                   |                    |
  |                       | query indicator   |                    |
  |                       |------------------>|                    |
  |                       |                   | read normalize     |
  |                       |                   |------------------->|
  |                       |                   |<-------------------|
  |                       |                   | calculate score    |
  |                       |<------------------|                    |
  |<----------------------|                   |                    |
```

查询流程说明：

1. 前端发起 HTTP 请求到 `QosController`
2. `QosController` 委托给 `QosService` 处理
3. `QosService` 从 `JsonFileStore` 读取归一化数据
4. 如需实时评分，调用 `QosCalculationService` 计算
5. 组装响应结构返回给前端

## 5. 健康评分算法

### 5.1 综合评分公式

健康评分（Health Score）采用加权求和模型：

```
HS = wA × A + wP × P + wR × R
```

其中：

- `HS`：综合健康评分，取值范围 `[0, 100]`
- `A`：可用性维度评分，取值范围 `[0, 100]`
- `P`：性能维度评分，取值范围 `[0, 100]`
- `R`：资源/告警维度评分，取值范围 `[0, 100]`
- `wA`：可用性权重，默认 `0.4`
- `wP`：性能权重，默认 `0.4`
- `wR`：资源/告警权重，默认 `0.2`

权重约束：`wA + wP + wR = 1.0`

### 5.2 各维度分段评分策略

#### 可用性（A）

| 可用率区间 | 评分 |
|-----------|------|
| ≥ 99.99%  | 100  |
| 99.9% ~ 99.99% | 90   |
| 99% ~ 99.9% | 70   |
| 95% ~ 99% | 50   |
| < 95%     | 30   |

#### 性能（P）

| 响应时间区间 | 评分 |
|-------------|------|
| ≤ P50 阈值  | 100  |
| P50 ~ P90 阈值 | 80   |
| P90 ~ P95 阈值 | 60   |
| P95 ~ P99 阈值 | 40   |
| > P99 阈值  | 20   |

#### 资源/告警（R）

| 告警密度区间 | 评分 |
|-------------|------|
| 无告警      | 100  |
| 低密度告警（低于阈值 1） | 80   |
| 中密度告警（阈值 1 ~ 阈值 2） | 60   |
| 高密度告警（高于阈值 2） | 40   |
| 严重告警    | 20   |

### 5.3 评分计算流程图

```text
raw metrics + alarms
        |
        v
  per-dimension scoring
        |
   +----+----+----+
   |         |         |
   v         v         v
  A score  P score  R score
   |         |         |
   +----+----+----+
        |
        v
  HS = wA×A + wP×P + wR×R
        |
        v
  Health Indicator (0~100)
```

## 6. 存储策略

### 6.1 JSON 文件存储

`operation-intelligence` 使用基于文件系统的 JSON 存储，不依赖外部数据库。

存储目录结构：

```text
operation-intelligence/data/
  raw/                     # 原始采集数据
    <environment>/         # 按环境隔离
      <timestamp>.json
  normalize/               # 归一化数据
    <environment>/
      <timestamp>.json
  detail/                  # 评分明细数据
    <environment>/
      <timestamp>.json
```

### 6.2 轮转间隔

数据按可配置的轮转间隔进行写入。每次采集任务完成后，生成一个新的 JSON 文件。轮转间隔通过 `config.yaml` 中的 `operation-intelligence.qos.rotation-interval-ms` 配置。

### 6.3 数据保留天数

过期数据按保留天数自动清理。保留天数通过以下配置项分别管理：

- `operation-intelligence.qos.raw-data-retention-days`（默认 7 天）
- `operation-intelligence.qos.detail-data-retention-days`（默认 30 天）
- `operation-intelligence.qos.normalize-data-retention-days`（默认 90 天）

超出保留天数的 JSON 文件会在定时清理任务执行时被删除。

## 7. 接口列表

### 7.1 健康指标接口

- `POST /operation-intelligence/qos/getHealthIndicator`
  - 用途：获取综合健康评分与各维度评分概要
  - 请求体包含环境标识与时间范围
  - 返回综合评分及可用性、性能、资源/告警三个维度的分项评分

### 7.2 指标明细接口

- `POST /operation-intelligence/qos/getAvailableIndicatorDetail`
  - 用途：获取可用性维度明细数据
  - 返回可用率、故障次数、MTTR 等子指标

- `POST /operation-intelligence/qos/getPerformanceIndicatorDetail`
  - 用途：获取性能维度明细数据
  - 返回响应时间 P50/P90/P95/P99、吞吐量等子指标

- `POST /operation-intelligence/qos/getResourceIndicatorDetail`
  - 用途：获取资源维度明细数据
  - 返回 CPU、内存、磁盘等资源使用率子指标

- `POST /operation-intelligence/qos/getAlarmIndicatorDetail`
  - 用途：获取告警维度明细数据
  - 返回告警数量、告警级别分布、告警趋势等子指标

### 7.3 分析与配置接口

- `POST /operation-intelligence/qos/getContributionData`
  - 用途：获取各子指标对总评分的贡献度分析
  - 返回子指标贡献权重与实际贡献值

- `POST /operation-intelligence/qos/getProductConfigRule`
  - 用途：获取当前生效的产品配置规则
  - 返回评分阈值、分段策略、权重配置

- `GET /operation-intelligence/qos/getEnvironments`
  - 用途：获取已配置的 DV 环境列表
  - 返回环境标识与名称

## 8. 推荐接入方式

### 8.1 给前端管理台

推荐链路：

1. 初始化先调 `getEnvironments` 获取可用环境列表
2. 调 `getProductConfigRule` 获取当前评分规则
3. 健康曲线主页调 `getHealthIndicator` 展示综合评分
4. 各维度详情页按需调对应的 `getXXXIndicatorDetail`
5. 贡献度分析调 `getContributionData`

### 8.2 给生产部署

推荐链路：

1. 保持 `operation-intelligence` 作为独立服务部署与演进
2. 前端按服务边界直接访问，无需经由 `gateway` 收口
3. 确保服务能访问 DV 系统的网络与认证信息
4. 关注 JSON 文件存储的磁盘空间

## 9. 当前实现限制

- 当前仅支持 JSON 文件存储，不支持数据库持久化
- 采集任务依赖 `QosDataScheduler` 的定时触发，不支持实时推送
- DV 系统集成依赖 `DvClient` 的 HTTP 调用，网络波动会影响数据完整性
- 健康评分算法的权重与阈值当前通过配置文件管理，不支持运行时动态调整
- 前端当前直接访问 `operation-intelligence`，与独立服务部署模式一致
- 数据归一化策略当前为固定实现，暂不支持自定义归一化规则

## 10. 结论

当前 `operation-intelligence` 已经形成一条可用闭环：

- 定时从 DV 系统采集性能与告警数据
- 原始数据归一化与分段评分
- 加权健康评分计算
- JSON 文件存储与自动轮转清理
- 健康指标、明细数据与贡献度查询 API

如果要在当前阶段做稳定集成，应优先依赖以下闭环能力：

- `getHealthIndicator` 综合健康评分查询
- `getAvailableIndicatorDetail` / `getPerformanceIndicatorDetail` / `getResourceIndicatorDetail` / `getAlarmIndicatorDetail` 各维度明细查询
- `getContributionData` 贡献度分析
- `getEnvironments` 环境列表查询
- `getProductConfigRule` 配置规则查询
