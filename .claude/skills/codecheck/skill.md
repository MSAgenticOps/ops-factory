---
name: codecheck
description: |
  Use when code writing is complete - automatically triggers code quality checks, auto-fixes issues, and exports reports.

  **Triggers automatically after:**
  - Writing or editing Java files (*.java)
  - Writing or editing frontend files (*.vue, *.js, *.ts)
  - User mentions "/codecheck"
  - Completing any code generation or modification task

  **What it does:**
  1. Detects recently modified files
  2. Runs quality checks based on project coding standards
  3. Auto-fixes issues (imports, formatting, semicolons, etc.)
  4. Exports check report to sibling directory
  5. Loops until all checks pass (max 3 retries)

  **Do NOT use when:**
  - Only reading/analyzing code without modifications
  - User explicitly requests no quality check

arguments:
  - name: directory
    description: |
      Required. The directory to scan for files.
      - Scan only the specified directory (recursive)
      Supports both relative and absolute paths.
    required: true
    examples:
      - "/codecheck business-auth"             # Scan business-auth directory
      - "/codecheck frontend/src/views"        # Scan specific frontend directory
      - "/codecheck ./business-metric"         # Relative path with ./
      - "/codecheck ."                         # Scan current directory (project root)
---

# Post-Code Quality Check

Automatic code quality verification after code writing is complete.

## Overview

This skill ensures all generated/modified code meets project coding standards before finalizing work.

```
Code Complete → Quality Check → Auto-Fix → Re-Check → Export Report → Done
                    ↓                        ↓
                Issues Found            All Pass
                    ↓                        ↓
              Auto-Fix & Retry      Export Success Report
```

## Trigger Conditions

**Automatic triggers (no user request needed):**
- ✅ Just wrote or edited Java files
- ✅ Just wrote or edited Vue/JS/TS files
- ✅ Completed code generation task
- ✅ Finished implementing a feature

**Manual triggers:**
- User says: "/codecheck business-auth"             # Scan specific directory
- User says: "/codecheck frontend/src/views"        # Scan frontend directory
- User says: "/codecheck ."                         # Scan current directory
- User says: "run code quality check on [directory]"
- User says: "check the code in [directory]"
- User says: "check code in [directory]"

## Execution Flow

### Step 0: Parse Directory Parameter

**IMPORTANT: Directory argument is REQUIRED. If not provided, ask user to specify.**

Determine the scan scope based on user input:

| User Input | Scan Scope | Example |
|------------|------------|---------|
| Relative directory | Specified directory (recursive) | `/codecheck business-auth` → scan `./business-auth` |
| Absolute directory | Specified directory (recursive) | `/codecheck /path/to/dir` |
| Current directory | Use `.` to scan current directory | `/codecheck .` |

**Implementation:**
```bash
# Check if directory argument is provided
if [ -z "$DIRECTORY_ARG" ]; then
    echo "Error: Directory argument is required."
    echo "Usage: /codecheck <directory>"
    echo "Examples:"
    echo "  /codecheck business-auth"
    echo "  /codecheck frontend/src/views"
    echo "  /codecheck ."
    exit 1
fi

# Validate directory exists
SCAN_DIR="$DIRECTORY_ARG"
if [ ! -d "$SCAN_DIR" ]; then
    echo "Error: Directory '$SCAN_DIR' does not exist"
    exit 1
fi
```

### Step 1: Detect Modified Files

Scan for recently modified files in the target directory:

**IMPORTANT: Exclude test directories and test files from all checks.**

**Exclusion rules:**
- Directories: `**/test/**`, `**/tests/**`, `**/__tests__/**`, `**/*.test.*`, `**/*.spec.*`
- Java test files: Files under `src/test/java/`
- Frontend test files: `*.test.js`, `*.spec.js`, `*.test.ts`, `*.spec.ts`

```bash
# Java files modified in last 5 minutes (exclude test directories)
find "$SCAN_DIR" -name "*.java" -mmin -5 -type f ! -path "*/test/*" ! -path "*/tests/*"

# Frontend files modified in last 5 minutes (exclude test directories and test files)
find "$SCAN_DIR" \( -name "*.vue" -o -name "*.js" -o -name "*.ts" \) -mmin -5 -type f \
    ! -path "*/test/*" ! -path "*/tests/*" ! -path "*/__tests__/*" \
    ! -name "*.test.js" ! -name "*.spec.js" ! -name "*.test.ts" ! -name "*.spec.ts"
```

**If no recently modified files found, scan all files in directory:**
```bash
# All Java files in target directory (exclude test directories)
find "$SCAN_DIR" -name "*.java" -type f ! -path "*/test/*" ! -path "*/tests/*"

# All frontend files in target directory (exclude test directories and test files)
find "$SCAN_DIR" \( -name "*.vue" -o -name "*.js" -o -name "*.ts" \) -type f \
    ! -path "*/test/*" ! -path "*/tests/*" ! -path "*/__tests__/*" \
    ! -name "*.test.js" ! -name "*.spec.js" ! -name "*.test.ts" ! -name "*.spec.ts"
```

**Track these files for quality checking.**

### Step 2: Run Quality Checks

**IMPORTANT: 明确分工 - 能自动化的交给脚本，AI 只做脚本做不到的**

#### 分工总览

| 处理者 | 负责内容 |
|--------|----------|
| **codecheck.sh (自动)** | 空行、格式化、分号、const/let、静态检查 |
| **AI (手动)** | 日志翻译、JavaDoc、import、命名、逻辑重构 |

#### 执行流程

```
Step 2.1: 调用脚本自动处理
    ↓
Step 2.2: AI 处理脚本无法修复的问题
    ↓
Step 2.3: 验证并导出报告
```

### Step 2.1: Run Codecheck Script (自动化)

First, run the project codecheck script to handle all automated fixes:

```bash
bash .claude/codecheck.sh
```

**脚本自动处理的规则:**

| 规则 | 工具 | 说明 |
|------|------|------|
| File Header | Spotless | 自动添加版权头 |
| Code style | Spotless | 4空格缩进、120字符行宽 |
| G.STY.01 空行 | `reduce_blank_lines.py` | 去除多余空行 |
| 前端分号 | ESLint | 自动添加分号 |
| const/let | ESLint | var → const/let |
| Checkstyle | Maven | 静态检查报告 |
| SpotBugs | Maven | Bug 检查报告 |

### Step 2.2: AI Manual Fixes (需 AI 处理)

After script completes, check and fix issues that scripts cannot handle:

**IMPORTANT: Skip all checks for test methods and test classes.**
- Methods annotated with `@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`
- Classes in `src/test/java/` directory
- Test files matching `*Test.java`, `*Tests.java`, `*IT.java`

#### Java AI Fixes

| 规则 | 问题 | AI 修复动作 |
|------|------|-------------|
| Wildcard imports | `import java.util.*` | 展开为显式导入: `List`, `ArrayList`, `Map` 等 |
| G.OTH.03 | 不用的 import / 代码段 | 直接删除，不要注释掉 |
| G.LOG.04 | 中文日志 | 翻译为英文 |
| G.LOG.01 | 直接用 Log4j/Logback | 改为 SLF4J facade |
| G.TYP.08 | `str.toUpperCase()` | 加 `Locale.ROOT` 参数 |
| G.DCL.07 | 变量遮蔽 | 重命名内层变量 |
| G.COM.01 | 缺失 JavaDoc | 生成中文 JavaDoc (含 @param, @return, @throws) |
| G.MET.02/03 | 复杂度/嵌套超标 | 重构代码，提取方法 |

**Wildcard Import 展开对照表:**

| 通配符导入 | 展开为 |
|-----------|--------|
| `import java.util.*` | `List`, `ArrayList`, `Map`, `HashMap`, `Set`, `HashSet`, `Collections`, `Optional`, `Objects` |
| `import jakarta.validation.constraints.*` | `NotBlank`, `NotNull`, `Size`, `Pattern`, `Min`, `Max`, `NotEmpty`, `Positive` |
| `import com.baomidou.mybatisplus.annotation.*` | `TableName`, `TableField`, `TableId`, `TableLogic`, `IdType`, `Version` |
| `import org.springframework.web.bind.annotation.*` | `RestController`, `RequestMapping`, `GetMapping`, `PostMapping`, `PutMapping`, `DeleteMapping`, `RequestParam`, `PathVariable`, `RequestBody` |

#### Frontend AI Fixes

| 规则 | 问题 | AI 修复动作 |
|------|------|-------------|
| 命名规范 | 组件非 PascalCase | 重命名文件和组件 |
| Vue props | 无类型定义 | 添加 type 和 default |
| v-html | XSS 风险 | 改为 `v-text` 或 `{{ }}` |

### Step 2.3: Verify and Report

Loop back to Step 2.1 if issues remain (max 3 times):

```
┌─────────────────────────────────────────────────────────────────────┐
│  1. Run: bash .claude/codecheck.sh                                  │
│     → Handles: 空行、格式化、分号、const/let                          │
├─────────────────────────────────────────────────────────────────────┤
│  2. AI fixes remaining issues:                                      │
│     - 展开通配符 import                                              │
│     - 翻译中文日志为英文                                             │
│     - 添加 JavaDoc 注释                                             │
│     - 修复 Locale 问题                                              │
├─────────────────────────────────────────────────────────────────────┤
│  3. Re-run codecheck.sh to verify                                   │
│     → Issues found? → Loop (max 3 times)                            │
│     → All passed? → Export Report                                   │
└─────────────────────────────────────────────────────────────────────┘
```

### Step 4: Export Check Report

**Report location:** `{project_parent}/codecheck-reports/{project_name}/`

```
../codecheck-reports/
└── Operation-Plat/
    ├── codecheck-report-{timestamp}.md
    └── latest-report.md  → symlink to latest
```

#### Report Format

```markdown
# Code Quality Check Report

**Project:** Operation-Plat
**Timestamp:** 2026-03-26 10:30:45
**Check Duration:** 12.5s

## Summary

| Category | Status | Issues Found | Auto-Fixed |
|----------|--------|--------------|------------|
| Java Quality | ✅ Pass | 5 | 5 |
| Frontend Quality | ✅ Pass | 2 | 2 |
| Project Codecheck | ✅ Pass | 0 | 0 |
| **Total** | ✅ **Pass** | **7** | **7** |

## Files Checked

### Java Files (8 files)
- ✅ business-auth/.../AuthService.java
- ✅ business-auth/.../AuthController.java
- ✅ business-auth/.../User.java
- ...

### Frontend Files (3 files)
- ✅ src/views/auth/Login.vue
- ✅ src/api/auth.js
- ✅ src/router/index.js

## Issues Fixed

### Java Issues

| File | Issue | Fix Applied |
|------|-------|-------------|
| AuthService.java | Wildcard import `java.util.*` | Replaced with explicit imports |
| AuthService.java | Missing JavaDoc on public method | Added JavaDoc template |
| User.java | Chinese in log statement | Translated to English |

### Frontend Issues

| File | Issue | Fix Applied |
|------|-------|-------------|
| Login.vue | Missing semicolon | Added semicolon |
| auth.js | Missing semicolon | Added semicolon |

## Detailed Logs

### Java Quality Check
```
[INFO] Checking AuthService.java...
[INFO]   - Import rules: 1 issue found (wildcard import)
[INFO]   - Auto-fixed: import java.util.* → explicit imports
[INFO]   - Re-check: PASS
```

### Frontend Quality Check
```
[INFO] Checking Login.vue...
[INFO]   - Semicolon check: 1 issue found
[INFO]   - Auto-fixed: Added semicolon
[INFO]   - Re-check: PASS
```

### Project Codecheck
```
[INFO] Running .claude/codecheck.sh --fast-mode
[INFO] Spotless: PASS
[INFO] Checkstyle: PASS
[INFO] ESLint: PASS
```

## Conclusion

✅ All quality checks passed after auto-fix.

---
Generated by post-codecheck skill
```

## Quality Rules Reference

**规范定义详见:** `.claude/rules/coding-standards.md`

以下仅列出 codecheck 检查的规则索引和处理方式，详细规则定义和示例请参考 coding-standards.md。

### Java 规则索引

| Rule ID | 规则 | 处理方式 | 说明 |
|---------|------|----------|------|
| G.STY.01 | 空行 | 脚本自动 | `reduce_blank_lines.py` |
| File Header | 版权头 | Spotless 自动 | 自动添加 |
| Wildcard Imports | 通配符导入 | AI 修复 | 展开为显式导入 |
| G.LOG.04 | 日志语言 | AI 修复 | 中文 → 英文 |
| G.LOG.01 | 日志框架 | AI 修复 | 改为 SLF4J |
| G.TYP.08 | Locale | AI 修复 | 添加 Locale.ROOT |
| G.DCL.07 | 变量遮蔽 | AI 修复 | 重命名变量 |
| G.COM.01 | JavaDoc | AI 修复 | 生成中文 JavaDoc |
| G.MET.02 | 圈复杂度 | AI 重构 | Checkstyle 报告 |
| G.MET.03 | 嵌套深度 | AI 重构 | Checkstyle 报告 |
| G.DCL.08 | Boolean 命名 | AI 修复 | coding-standards.md 定义 |
| G.EXC.01 | 异常捕获 | AI 修复 | coding-standards.md 定义 |
| G.MET.04 | Varargs | AI 修复 | coding-standards.md 定义 |
| G.STY.02 | 行宽 | Spotless 自动 | 120 字符 |

### Frontend 规则索引

**规范定义详见:** `.claude/rules/coding-standards.md` (基础编码规范) + `.claude/rules/frontend-architecture.md` (架构规范)

#### 基础编码规范 (coding-standards.md)

| 规则 | 处理方式 | 说明 |
|------|----------|------|
| 分号 | ESLint 自动 | 自动添加 |
| const/let | ESLint 自动 | 替换 var |
| 命名规范 | AI 修复 | coding-standards.md 定义 |
| Props 类型 | AI 修复 | 添加 type/default |
| v-html | AI 修复 | 替换为 v-text |

#### 架构规范 (frontend-architecture.md)

| 规则 | 处理方式 | 说明 |
|------|----------|------|
| Rule 1: CSS 硬编码 | AI 修复 | 硬编码颜色/间距/圆角 → 替换为 `var(--cl-*)` 设计令牌 |
| Rule 2: API 模式 | AI 修复 | 独立函数 → 重构为继承 BaseAPI |
| Rule 3/7: Composables | AI 修复 | 重复逻辑 → 提取到 `src/composables/` |
| Rule 4: 共享组件 | AI 修复 | 重复表格/表单 → 使用 DataTable/FormDialog |
| Rule 5: 文件大小 | AI 修复 | 超限 → 拆分子组件/composables |
| Rule 6.3: Props 定义 | AI 修复 | 补全 type + default |
| Rule 6.4: v-for key | AI 修复 | 无 key/索引 key → 使用唯一 ID |
| Rule 8: 懒加载 | AI 修复 | 静态 import → `() => import()` / `defineAsyncComponent` |
| Rule 9: Lint | 脚本自动 | `npm run lint` |
| Rule 10: 状态管理 | AI 修复 | 本地状态不应放 Pinia store |
| Rule 11: Import 路径 | AI 修复 | `../../` → `@/` 别名 |

### G.COM.01 JavaDoc 补充说明

JavaDoc 规则定义详见 coding-standards.md，此处补充 codecheck 的处理逻辑：

**已有 JavaDoc**: 只检查完整性，不删除不替换
- 缺少 `@param` → 追加
- 缺少 `@return` → 追加（非 void 方法）
- 缺少 `@throws` → 追加（有 throws 声明的方法）

**无 JavaDoc**: 使用中文模板生成
```java
/**
 * 方法简要描述.
 *
 * @param paramName 参数描述
 * @return 返回值描述
 * @throws ExceptionType 异常抛出条件
 */
```

**特殊处理:**
- 接口实现方法: 使用 `{@inheritDoc}`
- Getter/Setter: 简单描述即可
- Override 方法: 可用 `{@inheritDoc}` 或新文档

## Integration with Other Skills

This skill is designed to work after other code-generation or editing tasks:

```
ddd-code-generator → post-codecheck → compilation
feature-dev        → post-codecheck → tests
manual coding      → post-codecheck → commit
```

## Common Scenarios

### Scenario 1: After Code Generation

```
User: "Generate the auth module"
Agent: [Generates code...]
Agent: Running post-code quality check on business-auth...
Agent: ✅ All checks passed. Report exported to ../codecheck-reports/...
```

### Scenario 2: Check Specific Directory

```
User: "/codecheck business-auth"
Agent: Scanning directory: business-auth
Agent: Found 3 Java files, 2 Vue files
Agent: Running quality checks...
Agent: Found 2 issues, auto-fixing...
Agent: ✅ All checks passed. Report exported.
```

### Scenario 3: Check Frontend Directory

```
User: "/codecheck frontend/src/views"
Agent: Scanning directory: frontend/src/views
Agent: Found 5 Vue files
Agent: Running quality checks...
Agent: ✅ All checks passed. Report exported.
```

### Scenario 4: Issues Found

```
Agent: Running quality checks...
Agent: ❌ Found 5 issues:
  - AuthService.java: Wildcard import
  - AuthService.java: Chinese in log (G.LOG.04)
  - User.java: Missing JavaDoc
Agent: Auto-fixing issues...
Agent: Re-checking...
Agent: ✅ All checks passed after auto-fix.
Agent: Report exported to ../codecheck-reports/Operation-Plat/latest-report.md
```

## Configuration

The skill uses project coding standards from:
- `.claude/rules/coding-standards.md` - Project-specific coding rules (Java + Frontend basics)
- `.claude/rules/frontend-architecture.md` - Frontend architecture rules (Vue 3 + Element Plus)
- `.claude/codecheck.sh` - Project check script
- `.claude/skills/ddd-code-generator/references/code-quality-check.md` - Detailed rules reference

## Important Notes

1. **Always run after code modifications** - Ensures quality before finalizing
2. **Auto-fix by default** - Fixes common issues automatically
3. **Max 3 retries** - Stops after 3 fix attempts if issues persist
4. **Report always exported** - Even if all checks pass
5. **Never skip for "simple" changes** - Quality matters for all code
6. **Test code is excluded** - All test directories (`*/test/*`, `*/tests/*`) and test methods (`@Test` annotated) are automatically skipped
