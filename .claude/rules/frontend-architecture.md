# Frontend Architecture Standards

> Vue 3 + Element Plus 前端架构规则。新建或修改 .vue/.js 文件必须遵守。

## 目录结构

```
frontend/src/
├── assets/styles/
│   ├── variables.scss          # Design tokens（CSS 变量 + SCSS 变量，唯一真相源）
│   ├── main.scss               # Element Plus 覆写 + 全局工具类 + mixin
│   └── element-variables.scss  # Element Plus 主题变量
├── api/
│   ├── base.js                 # BaseAPI 基类（所有 API 模块必须继承）
│   └── *.js                    # 每个业务模块一个文件
├── composables/                # 共享组合式函数
├── components/                 # 共享基础组件
├── stores/                     # Pinia（仅全局状态：auth/app）
├── router/                     # 路由（必须 lazy load）
├── utils/                      # 纯工具函数
└── views/<module>/
    ├── components/             # 模块内部组件
    └── *.vue
```

## R1 样式：必须使用 Design Token

所有颜色、间距、圆角、阴影、动画必须引用 `variables.scss` 中的 CSS 变量，禁止硬编码 `#xxx`/`rgba()`/`px` 值。

**Token 速查：**
- 颜色：`--cl-primary`/`--cl-text`/`--cl-bg-card`/`--cl-border`/`--cl-success`/`--cl-danger`/`--cl-warning`
- 间距：`--cl-space-xs(4px)`/`sm(8)`/`md(12)`/`lg(16)`/`xl(24)`
- 圆角：`--cl-radius-xs(4px)`/`sm(6)`/`md(10)`/`lg(14)`/`xl(20)`
- 阴影：`--cl-shadow-sm`/`md`/`lg`/`primary`
- 动画：`--cl-ease`/`--cl-duration-fast(0.15s)`/`normal(0.25s)`/`slow(0.35s)`

新增视觉值必须先添加到 `variables.scss` 再引用。

**SCSS Mixin（main.scss）：** `@include glass-card`/`tech-card`/`gradient-btn`/`tech-grid`/`dot-grid`

## R2 API：必须继承 BaseAPI

`api/base.js` 提供标准 CRUD：`getPage`/`getList`/`getDetail`/`create`/`update`/`delete`/`batchDelete`/`publish`/`unpublish`。

禁止独立 `request()` 函数写法。每个 API 模块一个类继承 BaseAPI，仅添加非标准方法。

## R3 逻辑：必须使用 Composables

相同逻辑出现在 2+ 个文件，必须提取到 `src/composables/`。

| Composable | 替代的重复模式 |
|-----------|---------------|
| `usePagination(fetchFn)` | 分页 reactive + handleSizeChange/handleCurrentChange |
| `useCrudDialog(options)` | dialog + form + CRUD 样板 |
| `useTableData(fetchFn)` | loading + tableData + loadData |
| `useTreeSelect(fetchFn)` | 树构建逻辑 |

`<script setup>` 超过 100 行时必须审查提取。

## R4 组件：优先使用现有组件

- 列表页用 `DataTable.vue`（已含 table + 分页 + 搜索 + 加载 + 选择）
- 表单弹窗用 `FormDialog.vue`（已含 dialog + form + 校验 + 提交）
- 禁止每个 view 重新内联 `<el-table>` + `<el-pagination>` + `<el-dialog>`

## R5 文件大小限制
| 类型 | 最大行数 | 超限处理 |
|------|---------|---------|
| View 页面 (.vue) | 800 | 拆分子组件到 `views/<module>/components/` |
| 组件 (.vue) | 400 | 拆分为更小组件 |
| API (.js) | 50 | 继承 BaseAPI |
| Composable (.js) | 150 | 进一步提取 |

## R6 性能：必须懒加载
- 路由组件必须 `() => import(...)`，禁止 eager import
- ECharts/BPMN/LogicFlow 等重型库必须 `defineAsyncComponent` 或动态 `import()`
- 条件渲染组件（dialog/drawer/tab）必须懒加载

## R7 Lint：提交前必须通过
`npm run lint` 零错误才能提交。禁止无理由的 `eslint-disable`。

## R8 状态：Pinia 仅全局状态
Pinia 仅用于 auth（token/用户）、app shell（sidebar/theme/tabs）。页面级数据留在组件或 composable 中。

## R9 样式治理：三层架构
```
Layer 0: Design Tokens (variables.scss)  ← 视觉值唯一真相源
Layer 1: Base 样式层 (main.scss)         ← Element Plus 覆写 + 全局工具类 + mixin
Layer 2: Base 组件层 (components/)       ← 高频 UI 模式封装为 Vue 组件
Layer 3: 业务组件层 (views/<module>/)    ← 只写布局 + 业务逻辑
```

**`<style scoped>` 限制：** View ≤ 60 行，组件 ≤ 40 行。合法内容：仅布局定位 + 业务状态样式（颜色引用 token）+ 间距微调。禁止重写 Element Plus 组件样式。

**全局工具类（main.scss，禁止重新实现）：**
- 布局：`.page-container`/`.page-header`/`.page-content`/`.page-card`/`.list-page-container`/`.search-card`/`.table-card`
- 视觉：`.glass-search-card`/`.tech-stat-card`/`.gradient-text`/`.glow-border`/`.filter-tabs`/`.batch-action-bar`
- 间距：`.mt-8`/`.mt-16`/`.mt-24`/`.mb-8`/`.mb-16`/`.ml-8`/`.ml-16`/`.mr-8`/`.mr-16`
- 动画：`.skeleton`/`.skeleton-text`/`.stagger-item`/`.pulse-animation`/`.data-flow`

**新增视觉需求决策：** 先查 Token → 查全局类/mixin → 需 3+ props 则封装 Base 组件 → 否则加全局类
