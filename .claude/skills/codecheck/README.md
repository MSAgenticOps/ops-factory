# Codecheck Skill

代码质量检查技能 - 自动化检查和修复代码质量问题。

> **规范定义详见:** `.claude/rules/coding-standards.md` (基础编码规范) + `.claude/rules/frontend-architecture.md` (前端架构规范)
> 本文档只描述检查流程和处理方式，规则定义和示例以上述规范文件为准。

## 当前能力

### 自动化处理（脚本）

| 能力 | 工具 | 规则来源 |
|------|------|----------|
| 空行处理 | `reduce_blank_lines.py` | coding-standards.md → G.STY.01 |
| Java 格式化 | Spotless | coding-standards.md → G.STY.02, 缩进, 版权头 |
| 前端格式化 | Prettier | coding-standards.md → 分号 |
| 前端修复 | ESLint | coding-standards.md → const/let, 分号 |
| 静态检查 | Checkstyle | coding-standards.md → G.MET.02/03 |
| Bug 检查 | SpotBugs | 安全缺陷检测 |

### AI 手动处理

| 能力 | 规则来源 | 说明 |
|------|----------|------|
| 通配符 import 展开 | coding-standards.md → Import Rules | `import java.util.*` → 显式导入 |
| 日志翻译 | coding-standards.md → G.LOG.04 | 中文日志 → 英文 |
| 日志框架修复 | coding-standards.md → G.LOG.01 | 改为 SLF4J |
| JavaDoc 生成 | coding-standards.md → G.COM.01 | 为公共元素添加中文 JavaDoc |
| Locale 修复 | coding-standards.md → G.TYP.08 | `toUpperCase()` → `toUpperCase(Locale.ROOT)` |
| 变量重命名 | coding-standards.md → G.DCL.07 | 解决变量遮蔽问题 |
| 代码重构 | coding-standards.md → G.MET.02/03 | 降低复杂度/嵌套深度 |

## 规则索引

> 以下仅列出 codecheck 检查的规则和处理方式，详细规则定义和正确/错误示例请参考 `coding-standards.md`。

### Java 规则

| 规则 ID | 规则名称 | 处理方式 | 定义位置 |
|---------|----------|----------|----------|
| G.STY.01 | 空行 | 脚本自动 `reduce_blank_lines.py` | coding-standards.md |
| G.STY.02 | 行宽 | Spotless 自动 | coding-standards.md |
| G.COM.01 | JavaDoc | AI 生成中文注释 | coding-standards.md |
| G.LOG.04 | 日志语言 | AI 翻译为英文 | coding-standards.md |
| G.LOG.01 | 日志框架 | AI 修复为 SLF4J | coding-standards.md |
| G.TYP.08 | Locale | AI 添加 Locale.ROOT | coding-standards.md |
| G.DCL.07 | 变量遮蔽 | AI 重命名 | coding-standards.md |
| G.MET.02 | 圈复杂度 | Checkstyle + AI 重构 | coding-standards.md |
| G.MET.03 | 嵌套深度 | Checkstyle + AI 重构 | coding-standards.md |
| - | 通配符 import | AI 展开 | coding-standards.md → Import Rules |
| - | 文件头 | Spotless 自动 | coding-standards.md |

### 前端规则

#### 基础编码规范 (coding-standards.md)

| 规则 | 处理方式 | 定义位置 |
|------|----------|----------|
| 分号 | ESLint `--fix` | coding-standards.md → Semicolons |
| const/let | ESLint `--fix` | coding-standards.md → Variable Declaration |
| 命名规范 | AI 修复 | coding-standards.md → Naming Conventions |
| Props 类型 | AI 添加 | coding-standards.md → Vue Component Props |
| v-html | AI 替换 | coding-standards.md → Security Checklist |

#### 架构规范 (frontend-architecture.md)

| 规则 | 处理方式 | 定义位置 |
|------|----------|----------|
| CSS 硬编码 | AI 修复 | frontend-architecture.md → Rule 1 |
| API 模式 | AI 修复 | frontend-architecture.md → Rule 2 |
| Composables | AI 修复 | frontend-architecture.md → Rule 3/7 |
| 共享组件 | AI 修复 | frontend-architecture.md → Rule 4 |
| 文件大小 | AI 修复 | frontend-architecture.md → Rule 5 |
| Props 定义 | AI 修复 | frontend-architecture.md → Rule 6.3 |
| v-for key | AI 修复 | frontend-architecture.md → Rule 6.4 |
| 懒加载 | AI 修复 | frontend-architecture.md → Rule 8 |
| Lint | 脚本自动 | frontend-architecture.md → Rule 9 |
| 状态管理 | AI 修复 | frontend-architecture.md → Rule 10 |
| Import 路径 | AI 修复 | frontend-architecture.md → Rule 11 |

## 使用方式

### 命令行

```bash
# 扫描整个项目
/codecheck

# 扫描指定目录
/codecheck business-auth

# 扫描前端目录
/codecheck frontend/src/views
```

### 对话触发

```
"检查代码质量"
"运行 codecheck"
"扫描 business-auth 模块"
```

### 自动触发

- 写入或编辑 Java 文件后
- 写入或编辑 Vue/JS/TS 文件后
- 完成代码生成任务后

## 执行流程

```
┌─────────────────────────────────────────────────────────────────┐
│ Step 1: 检测文件                                                 │
│    find . -name "*.java" -mmin -5                               │
├─────────────────────────────────────────────────────────────────┤
│ Step 2.1: 脚本自动化                                             │
│    bash .claude/codecheck.sh                                    │
│    → reduce_blank_lines.py (G.STY.01)                           │
│    → Spotless (格式化, 版权头)                                   │
│    → ESLint (前端分号, const/let)                                │
├─────────────────────────────────────────────────────────────────┤
│ Step 2.2: AI 手动修复                                            │
│    → 展开通配符 import                                           │
│    → 翻译中文日志 (G.LOG.04)                                     │
│    → 添加 JavaDoc (G.COM.01)                                    │
│    → 修复 Locale (G.TYP.08)                                     │
│    → 重命名遮蔽变量 (G.DCL.07)                                   │
├─────────────────────────────────────────────────────────────────┤
│ Step 2.3: 验证循环 (max 3 次)                                    │
│    → Issues found? → 回到 Step 2.1                              │
│    → All passed? → 导出报告                                     │
├─────────────────────────────────────────────────────────────────┤
│ Step 3: 导出报告                                                 │
│    → ../codecheck-reports/Operation-Plat/codecheck-report.md    │
└─────────────────────────────────────────────────────────────────┘
```

## 文件结构

```
.claude/
├── codecheck.sh              # 主脚本
├── scripts/                  # 辅助脚本
│   └── reduce_blank_lines.py # 空行处理
├── rules/
│   ├── coding-standards.md      # 编码规范（权威来源）
│   └── frontend-architecture.md # 前端架构规范（权威来源）
├── skills/
│   └── codecheck/
│       ├── skill.md          # Skill 定义
│       └── README.md         # 本文档
```

## 职责划分

```
coding-standards.md      →  WHAT (基础编码规范定义、正确/错误示例、规则详情)
frontend-architecture.md →  WHAT (前端架构规范定义、组件/样式/API/性能规则)
codecheck skill.md       →  HOW  (检查流程、处理方式、AI 修复逻辑)
codecheck README.md      →  INDEX (规则索引、文件结构、可扩展点)
```

## 可扩展点

### Java 规则扩展

| 规则 ID | 规则名称 | 当前状态 | 扩展方向 |
|---------|----------|----------|----------|
| G.LOG.04 | 日志语言 | AI 翻译 | 可脚本化：正则匹配中文 → 翻译 |
| G.LOG.01 | 日志框架 | AI 修复 | 可脚本化：检测 Log4j → 替换 SLF4J |
| G.TYP.08 | Locale | AI 添加 | 可脚本化：检测 `.toUpperCase()` → 注入 Locale |
| G.DCL.07 | 变量遮蔽 | AI 重命名 | 需语义分析 |
| G.COM.01 | JavaDoc | AI 生成 | 可半自动化：检测缺失 → 生成模板 → AI 补充 |
| - | import 展开 | AI | 可脚本化：解析文件 → 提取使用类 |
| - | import 排序 | 🔲 | 可脚本化：按 coding-standards.md 排序规则 |
| G.EXC.01 | 异常捕获 | 🔲 | 可检测：catch Exception → 提示 |
| G.MET.04 | 可变参数 | 🔲 | 可检测：`...` 参数 → 提示 |
| G.STY.02 | 行宽 | Spotless | ✅ 已完善 |

### 前端规则扩展

| 规则 | 当前状态 | 扩展方向 |
|------|----------|----------|
| 分号 | ESLint 自动 | ✅ 已完善 |
| const/let | ESLint 自动 | ✅ 已完善 |
| 命名规范 | AI 修复 | 可配置 ESLint 规则 |
| Props 类型 | AI 添加 | 可脚本化 |
| v-html | AI 替换 | 可检测 |
| API 错误处理 | 🔲 | 可检测：axios 调用 → 检查 catch |
| console.log | 🔲 | 可检测：生产代码不应有 |
| 硬编码字符串 | 🔲 | 可检测：模板中文 → 国际化 |
| 组件大小 | 🔲 | 可检测：> 500 行 → 拆分 |

### 优先级建议

| 优先级 | 规则 | 原因 |
|--------|------|------|
| P0 | import 排序/展开 | 频繁出现，易自动化 |
| P1 | console.log、硬编码字符串 | 常见问题 |
| P2 | 异常捕获、Props 类型 | 安全和最佳实践 |
| P3 | 组件大小 | 可维护性 |

## 相关文件

| 文件 | 说明 |
|------|------|
| `skill.md` | Skill 定义，触发条件和执行流程 |
| `coding-standards.md` | **编码规范权威来源**，规则定义和示例 |
| `frontend-architecture.md` | **前端架构规范权威来源**，Vue 3 组件/样式/API/性能规则 |
| `../codecheck.sh` | 主脚本，调用各种工具 |
| `../scripts/reduce_blank_lines.py` | 空行处理脚本 |
| `../CODECHECK_README.md` | 用户使用指南 |

---

**版本**: 1.1.0
**更新时间**: 2026-03-30
**维护者**: l30020200
