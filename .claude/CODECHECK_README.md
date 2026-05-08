# Code Quality Check System

## 系统架构

```
编码规范 (.claude/rules/)
    ↓ 引用
Stop Hook (.claude/settings.json) → 对话结束时触发
    ↓ 执行
codecheck.sh → 自动修复 + 检查 → codecheck_output.md
```

## 环境要求

| 工具 | 版本 | 验证命令 |
|------|------|---------|
| Java JDK | 21 | `java -version` |
| Maven | 3.6+ | `mvn -version` |
| Node.js | 18+ | `node -version` |
| Git Bash | - | Windows 必须用 Git Bash 执行脚本 |

## 使用方法

```bash
# 完整检查 + 自动修复（Windows 必须用 Git Bash）
./.claude/codecheck.sh

# 快速模式 - 仅格式检查
./.claude/codecheck.sh --fast-mode
```

## 脚本工作流

```
阶段 1: 自动修复
├── Spotless apply (Java 格式化 + 文件头 + import)
├── Prettier --write (JS/Vue 格式化 + 分号)
└── ESLint --fix (JS/Vue 代码检查修复)

阶段 2: 检查
├── mvn clean compile (编译)
├── Spotless check (格式)
├── Checkstyle check (G.TYP.08/G.LOG.04/G.LOG.01/G.MET.03/G.MET.02/G.DCL.07)
├── SpotBugs check (缺陷检测)
├── Prettier check (格式)
└── ESLint check (G.AOD.05/G.AOD.03)

输出: .claude/codecheck_output.md（剩余问题需人工修复）
```

## 编码规范

详见 `.claude/rules/coding-standards.md`。

## 配置文件

| 工具 | 配置文件 | 自动修复能力 |
|------|---------|-------------|
| Spotless | `pom.xml` | ✅ 格式化 + 文件头 + import |
| Checkstyle | `checkstyle.xml` | ❌ 仅检查 |
| SpotBugs | `pom.xml` | ❌ 仅检查 |
| Prettier | `frontend/.prettierrc` | ✅ 格式化 + 分号 |
| ESLint | `frontend/.eslintrc.cjs` | ✅ 部分修复 |

## 单独使用各工具

```bash
# 后端
mvn spotless:apply                    # Java 格式化
mvn spotless:check                    # Java 格式检查
mvn checkstyle:check                  # Java 代码风格检查
mvn spotbugs:spotbugs                 # Java 缺陷检测

# 前端
cd frontend
npx prettier --write .               # JS/Vue 格式化
npx prettier --check .               # JS/Vue 格式检查
npm run lint -- --fix                 # JS/Vue 代码检查修复
npm run lint                          # JS/Vue 代码检查
```

## IDEA 配置（一次性）

### Import 排序（G.FMT.03）
File → Settings → Editor → Code Style → Java → Imports，按齿轮按钮 Import Schema 导入 XML：

```xml
<code_scheme name="Default" version="173">
   <JavaCodeStyleSettings>
      <option name="CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND" value="9999" />
      <option name="NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND" value="9999" />
      <option name="IMPORT_LAYOUT_TABLE">
         <value>
            <package name="" withSubpackages="true" static="false" module="true" />
            <package name="" withSubpackages="true" static="true" />
            <package name="android" withSubpackages="true" static="false" />
            <emptyLine />
            <package name="com.huawei" withSubpackages="true" static="false" />
            <package name="cn.huawei" withSubpackages="true" static="false" />
            <emptyLine />
            <package name="" withSubpackages="true" static="false" />
            <emptyLine />
            <package name="net" withSubpackages="true" static="false" />
            <emptyLine />
            <package name="org" withSubpackages="true" static="false" />
            <emptyLine />
            <package name="java" withSubpackages="true" static="false" />
            <emptyLine />
            <package name="javax" withSubpackages="true" static="false" />
            <emptyLine />
         </value>
      </option>
   </JavaCodeStyleSettings>
</code_scheme>
```

修复：单文件 `Ctrl+Alt+O`，整工程 Code → Optimize Imports。

### 行宽（G.FMT.10）
Settings → Editor → Code Style → General → Hard wrap at 120。
Settings → Editor → Code Style → Java → Wrapping and Braces → 勾选 Ensure right margin is not exceeded。
修复：`Ctrl+Alt+L`。

## 常见问题

### G.LOG.04 日志中文
推荐让 Claude Code 批量修复：将 `codecheck_output.md` 中所有 `Log content must be in English` 问题交给 Claude Code。

### G.AOD.03 console.log
IDEA 全局替换：`Ctrl+Shift+R` → 正则 `^\s*console\.(log|warn|info|debug)[\s\S]*?;\s*$` → 留空。替换后重新 `npm run dev` 验证。

### Maven 编译失败
```bash
java -version   # 确认 21
mvn clean compile -DskipTests -U
```

### 前端依赖失败
```bash
cd frontend && rm -rf node_modules package-lock.json && npm install
```

## 文件结构

```
.claude/
├── rules/
│   ├── coding-standards.md          # 编码规范
│   └── frontend-architecture.md     # 前端架构规则
├── skills/
│   ├── java-guide/                  # Java 编码规范技能
│   ├── js-ts-guide/                 # JS/TS 编码规范技能
│   └── cwd-code-review/             # CWD 缺陷扫描技能
├── settings.json                    # Stop hook 配置
├── codecheck.sh                     # 质量检查脚本
└── CODECHECK_README.md              # 本文档
```

## 命令速查

| 操作 | 命令 |
|------|------|
| 完整检查 | `./.claude/codecheck.sh` |
| 快速检查 | `./.claude/codecheck.sh --fast-mode` |
| Java 格式化 | `mvn spotless:apply` |
| Java 编译 | `mvn clean compile -DskipTests` |
| 前端格式化 | `cd frontend && npx prettier --write .` |
| 前端检查 | `cd frontend && npm run lint` |
