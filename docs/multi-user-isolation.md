# 多用户数据隔离方案

> 状态：方案确定，待实施

## 1. 目标

在不引入完整用户管理系统的前提下，实现多用户数据隔离。用户认证和权限由外部系统控制（反向代理 / API Gateway 等），本系统只负责按用户标识隔离数据。

**隔离范围：**
- Session（会话历史）
- 文件产出（artifacts）
- 定时任务（schedules）及其产生的 session
- 前端本地状态（localStorage）

**不隔离（共享）：**
- Agent 实例和配置（AGENTS.md、MCP、Skills）
- Secret Key（所有用户共用）

**默认用户：** `robby`（系统预置，所有历史数据归属此用户）

---

## 2. 用户身份

### 2.1 登录

前端提供简易登录页（`/login`），只需输入用户名，无需密码：

```
┌─────────────────────────────────┐
│                                 │
│         OpsFactory              │
│                                 │
│    ┌─────────────────────┐      │
│    │  Enter your name    │      │
│    └─────────────────────┘      │
│         [  Enter  ]             │
│                                 │
└─────────────────────────────────┘
```

- 用户名存入 `localStorage: opsfactory:userId`
- 下次访问自动跳过登录
- 未登录时所有路由重定向到 `/login`

### 2.2 用户标识传递

```
浏览器 → 前端（从 localStorage 读 userId）→ SDK（x-user-id header）→ Gateway → Goosed
```

- SDK 在每个请求的 header 中附带 `x-user-id`
- Gateway 从 header 读取用户标识
- 缺失时 fallback 为 `"robby"`（兼容现有部署）

### 2.3 登出

- 清除 `localStorage: opsfactory:userId`
- 跳转回 `/login`

---

## 3. 核心机制

### 3.1 用户注册表

Gateway 维护一个轻量 JSON 文件，记录 session 和 schedule 到用户的映射关系：

```
gateway/data/user-registry.json

{
  "sessions": {
    "session-uuid-1": "robby",
    "session-uuid-2": "alice"
  },
  "schedules": {
    "daily-report-job": "robby",
    "weekly-summary": "alice"
  }
}
```

**写入时机：** Gateway 拦截 session/schedule 创建响应时记录。

**读取时机：** 列表过滤、访问/删除时校验所有权。

**Session 归属查找优先级：**
1. `sessions` 注册表有记录 → 使用该 owner
2. session 有 `schedule_id` → 查 `schedules` 注册表找 owner
3. 都没有 → 归属 `"robby"`（历史数据）

### 3.2 文件产出隔离

Gateway 拦截 session 创建请求，改写 `working_dir` 为用户专属路径：

**改造后目录结构：**
```
agents/universal-agent/artifacts/
└── users/
    ├── robby/          ← robby 的产出
    │   ├── report.md
    │   └── analysis.xlsx
    ├── alice/          ← alice 的产出
    │   └── summary.pdf
    └── bob/            ← bob 的产出
        └── data.csv
```

**所有用户一致使用 `artifacts/users/{userId}/` 路径，包括默认用户 robby，不做特殊处理。**

---

## 4. 数据流

### 4.1 创建 Session（改造重点）

```
前端: POST /agents/{agentId}/agent/start
      Body: { working_dir: "agents/universal-agent" }
      Headers: x-user-id: robby

         │
         ▼
Gateway（拦截处理，非盲目代理）:
  1. 读取 x-user-id → "robby"
  2. 改写 working_dir → "agents/universal-agent/artifacts/users/robby"
  3. 确保目录存在 (mkdir -p)
  4. fetch goosed POST /agent/start（改写后的 body）
  5. 从响应中提取 session.id
  6. 写入注册表: sessions["session-id"] = "robby"
  7. 返回响应给前端
```

### 4.2 列出 Session

```
前端: GET /sessions
      Headers: x-user-id: robby

         │
         ▼
Gateway:
  1. 从各 Agent 聚合全部 session（已有逻辑）
  2. 加载注册表
  3. 按归属查找优先级过滤，只返回属于 robby 的 session
  4. 返回过滤后的列表
```

### 4.3 访问/删除 Session

```
Gateway:
  1. 读取 x-user-id
  2. 按归属查找优先级确认 session 归属
  3. 不匹配 → 403 Forbidden
  4. 匹配 → 正常处理
```

### 4.4 代理路由（reply/resume/stop 等）

这些路由在请求体中包含 `session_id`，需要校验所有权：

```
Gateway:
  1. Buffer 请求 body
  2. 解析 session_id
  3. 查注册表校验所有权
  4. 通过 → proxy 转发
  5. 不通过 → 403
```

### 4.5 定时任务（Schedule）

创建：
```
Gateway 拦截 schedule 创建请求:
  1. 读取 x-user-id
  2. proxy 转发到 goosed
  3. 写入注册表: schedules["schedule-id"] = userId
```

列表：
```
Gateway 拦截 schedule 列表:
  1. 获取全部 schedules
  2. 按注册表过滤，只返回属于当前用户的
```

Schedule 产生的 session：
```
Schedule 运行时 goosed 自动创建 session（不经过 gateway）
→ 该 session 有 schedule_id 字段
→ 列表/访问时，gateway 通过 schedule_id 查找 schedule owner → 确定 session 归属
```

### 4.6 文件列表/下载

```
GET /agents/{agentId}/files
Headers: x-user-id: robby

Gateway:
  1. 读取 x-user-id → "robby"
  2. 路径限定为 artifacts/users/robby/
  3. 返回该目录下的文件
```

---

## 5. UI 变化

### 5.1 布局

```
┌─────────────┬─────────────────────────────┐
│ OpsFactory  │                             │
│─────────────│                             │
│ 🏠 Home     │                             │
│ [+ New Chat]│      Main Content           │
│ 📋 History  │                             │
│ 📥 Inbox (3)│                             │
│ 🤖 Agents   │                             │
│ 📁 Files    │                             │
│ ⏰ Scheduler│                             │
│             │                             │
│   (spacer)  │                             │
│─────────────│                             │
│ 🟣 robby   │                             │
│ ⚙ Settings  │                             │
│ 🚪 Logout   │                             │
└─────────────┴─────────────────────────────┘
```

- **New Chat**: 紧跟 Home 下方
- **用户区域**: 侧栏底部，显示 emoji 头像 + 用户名
- **Settings**: 进入独立的 `/settings` 页面
- **Logout**: 清除身份，返回登录页

### 5.2 Settings 页面

- 用户 emoji 头像
- 当前用户名
- 登出按钮
- （未来可扩展：通知偏好、默认 Agent 等）

---

## 6. 改动范围

### 6.1 Gateway

| 文件 | 改动 | 说明 |
|------|------|------|
| `gateway/src/user-registry.ts` | **新增** | session→user、schedule→user 映射的读写，JSON 文件持久化 |
| `gateway/src/index.ts` | **修改** | 用户解析、session/schedule 创建拦截、列表过滤、所有权校验、文件路径隔离、`GET /me` 端点 |
| `gateway/src/process-manager.ts` | **修改** | `getArtifactsPath` 支持 userId 参数，返回 `artifacts/users/{userId}` |

### 6.2 TypeScript SDK

| 文件 | 改动 | 说明 |
|------|------|------|
| `typescript-sdk/src/types.ts` | **修改** | `GoosedClientOptions` 增加可选 `userId` 字段 |
| `typescript-sdk/src/client.ts` | **修改** | `headers()` 在 `userId` 存在时附带 `x-user-id`，不影响原始 goosed 兼容性 |

### 6.3 Web App

| 文件 | 改动 | 说明 |
|------|------|------|
| `web-app/src/pages/Login.tsx` | **新增** | 登录页，用户名输入 |
| `web-app/src/pages/Settings.tsx` | **新增** | 用户设置页 |
| `web-app/src/contexts/UserContext.tsx` | **新增** | userId 状态管理、ProtectedRoute 组件 |
| `web-app/src/App.tsx` | **修改** | 添加 `/login`、`/settings` 路由，ProtectedRoute 包装 |
| `web-app/src/components/Sidebar.tsx` | **修改** | New Chat 移到 Home 下方，底部增加用户区域 |
| `web-app/src/contexts/GoosedContext.tsx` | **修改** | 从 UserContext 获取 userId，传给 SDK client |
| `web-app/src/contexts/InboxContext.tsx` | **修改** | localStorage key 加 `${userId}:` 前缀 |
| `web-app/src/pages/ScheduledActions.tsx` | **修改** | localStorage key 加 `${userId}:` 前缀 |

**总计：4 新增 + 8 修改 = 12 个文件**

---

## 7. 数据迁移

首次启动时执行一次性迁移（Gateway 启动逻辑）：

1. **文件迁移**: 对每个 agent，将 `artifacts/` 下现有文件移入 `artifacts/users/robby/`
2. **Session 注册**: 扫描所有 agent 的现存 session，注册到 `user-registry.json` 归属 robby
3. **Schedule 注册**: 扫描所有 agent 的现存 schedule，注册到 `user-registry.json` 归属 robby

**已知限制**: 迁移后，旧 session 在 goosed SQLite 中的 `working_dir` 仍指向旧路径。这些 session 已完成，历史对话记录不受影响。

---

## 8. 兼容性

| 场景 | 行为 |
|------|------|
| 现有单用户部署 | 缺失 `x-user-id` 时 fallback 为 `"robby"`，行为不变 |
| SDK 直连 goosed | `userId` 为可选字段，不传时 SDK 与原始 goosed 完全兼容 |
| 未来接入真实认证 | 只需改 userId 来源（从 localStorage 改为 JWT/OAuth），其余逻辑不变 |

---

## 9. Goose 上游调研结论

- Goosed **没有**多用户/多租户支持（Session 无 user_id 字段，全局单例 SessionManager）
- Block 将 Goose 定位为 "on-machine AI agent"，无云端/托管产品
- Goose 社区正在做 per-session agent 隔离（解决并发，非多用户）
- 唯一隔离原语 `GOOSE_PATH_ROOT` 过重，不适合本场景
- **结论**: Gateway 层拦截是最合理的方案，不依赖上游改动
