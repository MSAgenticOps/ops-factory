---
name: cwd-code-review
description: 基于 CWD（代码缺陷字典） 的代码审查(Code Review)技能。用于审查代码中的潜在缺陷、安全漏洞或代码质量问题。根据 CWD 缺陷模式执行全面分析，生成包含缺陷统计、代码位置和修复建议的详细报告。支持 C、C++、Java、Python、Go、JavaScript/TypeScript 等语言。
---

# CWD 代码审查技能

基于 CWD（代码缺陷字典） 的综合代码审查技能。该技能针对已知缺陷模式执行静态分析，并生成包含统计信息、代码位置和修复建议的详细报告。

## 使用场景

在以下情况使用此技能：
- 执行代码审查(Code Review)或 Pull Request / Merge Request 审查
- 扫描代码中的潜在安全漏洞
- 识别代码中潜在缺陷
- 检查代码是否符合编码规范和最佳实践
- 为团队审查生成代码质量报告

## 功能特性

- **全面缺陷检测**：检查 CWD 缺陷模式
- **多语言支持**：C、C++、Java、Python、Go、JavaScript/TypeScript、SQL、Shell
- **懒加载引用**：按需加载 CWD 类别以提高性能
- **详细报告**：生成包含以下内容的综合报告：
  - 未在 CWD 字典中的代码缺陷， 为自定义缺陷类型，ID采用为 `Defect-{num}`
  - 按 CWD 类别 或 自定义缺陷类型 分类的缺陷统计
  - 精确的代码位置（文件路径、起止行号、代码片段）
  - 严重程度和Clean Code特性
  - 包含示例的修复建议

## 核心职责

1. **语言检测**：识别目标文件的编程语言
2. **模式匹配**：根据语言支持应用 CWD 缺陷模式
3. **缺陷识别**：检测具有精确位置信息的潜在缺陷
4. **报告生成**：创建包含统计信息和修复方案的全面报告

## 工作流程

### 1. 分析请求

1. 识别所有目标源文件（从指定路径递归）
2. 过滤掉测试文件（排除匹配测试模式的代码）
3. 检测每个文件的编程语言
4. 根据检测到的语言，查询 CWD 类别索引文件`cwd_index.md`，加载相关的 CWD 类别

#### 测试文件排除规则

从代码审查中排除以下内容：
- 名称包含的文件：`test`、`spec`、`__tests__`、`tests`
- 目录名：`test`、`tests`、`__tests__`、`spec`、`specs`、`.test`、`.tests`
- 文件扩展名：`.test.js`、`.spec.js`、`.test.ts`、`.spec.ts`、`.test.tsx`、`.spec.tsx`、`_test.go`、`_spec.go`、`.test.py`、`.spec.py`、`test_*.py`
- 路径包含：`/test/`、`/tests/`、`/__tests__/`、`/spec/`、`/specs/`

**注意**：只审查生产代码。测试文件和测试目录会自动跳过。

### 2. 代码分析

对每个源文件：

1. **解析代码结构**
   - 读取文件内容
   - 识别代码块、函数、控制结构
   - 跟踪行号以精确定位缺陷

2. **应用 CWD 缺陷模式**
   - 识别潜在缺陷，分类为Top3的 CWD 类型分类
   - 加载对应的 CWD 类型文件，再进行结构分析
   - 确认最合适的 CWD 类型分类
   - 未能确认为 CWD 类型分类，为自定义缺陷类型，ID采用为 `Defect-{num}`

3. **记录发现**
   - CWD 类别或自定义缺陷类型和子类别（若有）
   - 文件路径
   - 起始和结束行号
   - 代码片段（相关行）
   - 严重程度级别
   - Clean Code特性

### 3. 生成报告

使用 `templates/report_template.md` 中定义的模板生成综合报告。报告包括：

1. **执行摘要** - 审查的文件数、发现的缺陷、严重程度分布
2. **CWD 类别统计** - 每个类别的缺陷及百分比分布
3. **详细发现** - 按 CWD 类别分组，包含代码片段和修复方案
4. **建议** - 按严重程度排序的修复列表

### 4. 报告保存文件

生成报告内容后，将其写入 markdown 文件：

1. **生成文件名**：`cwd_code_review_report_{datetime}.md`
   - 日期时间格式：`YYYYMMDD_HHMMSS`

2. **写入报告**：使用 Write 工具保存完整报告

3. **位置**：保存在当前工作目录

## 缺陷检测模式

### 内存分配（CWD-1002）

**检测模式：**
```regex
// 对于 C/C++
(malloc|realloc|calloc|new)\s*\([^)]*(user|input|external|param|size|len|length)
```

**示例发现：**
```c
// 文件：src/memory.c:45-48
void *ptr = malloc(userSize);  // userSize 来自外部输入
if (ptr == NULL) {
    return ERROR;
}
```

**修复方案：**
```c
#define MAX_ALLOC_SIZE 1048576  // 1MB 限制

if (userSize == 0 || userSize > MAX_ALLOC_SIZE) {
    return ERROR;
}
void *ptr = malloc(userSize);
```

### SQL 注入（CWD-1101）

**检测模式：**
```regex
// 对于 Java
(executeQuery|executeUpdate|createStatement)\s*\(\s*"[^"]*"\s*\+\s*
```

**示例发现：**
```java
// 文件：src/UserDAO.java:78-80
String query = "SELECT * FROM users WHERE id = " + userId;
stmt.executeQuery(query);  // SQL 注入漏洞
```

**修复方案：**
```java
String query = "SELECT * FROM users WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(query);
stmt.setString(1, userId);
```

### 路径遍历（CWD-1059）

**检测模式：**
```regex
(file|File|open|Open)\s*\([^)]*\.\.|[\/\.\.]+(user|input|param|filename|name)
```

## 报告格式参考

完整报告结构、发现格式模板、统计表格模板和报告文件命名约定请参阅 `templates/report_template.md`。

## 重要说明

1. **懒加载**：只加载与检测到的语言相关的 CWD 类别
2. **误报**：在报告中记录潜在的误报
3. **上下文感知**：报告发现时考虑代码上下文
4. **行号精确度**：始终准确报告起始和结束行号
5. **代码片段**：包含足够的上下文（前后 3-5 行）以便理解

## 审查示例

**输入：**
```
审查 src/ 目录中的 C 代码。关注内存安全。
```

**分析：**
1. 检测语言：C
2. 加载 CWD 内存类别（1002、1003、1016）
3. 分析每个 .c 文件
4. 检测缺陷：
   - src/memory.c:45 (CWD-1002)
   - src/buffer.c:128 (CWD-1016)
5. 生成包含发现的报告

**输出**：完整报告格式请参阅 `templates/report_template.md`。

## 与其他技能的集成

该技能与以下技能配合：
- `java-guide`：Java 编码规范
- `c-guide`：C 编码规范
- `cpp-guide`：C++ 编码指南
- `python-guide`：Python 编码规范
- `code-review:code-review`：通用代码审查指南

## CWD 类别参考

根据语言按需加载 CWD 类别：

### 内存安全（C、C++、Java）
- CWD-1002：内存分配大小未限制
- CWD-1003：缓冲区大小计算错误
- CWD-1009：未识别的内存安全函数
- ... 等等

### 安全（所有语言）
- CWD-1044：文件名或路径外部可控
- CWD-1059：路径遍历
- CWD-1066：SSRF
- ... 等等

### 并发（C、C++、Java）
- CWD-1117：信号处理器中的竞态条件
- CWD-1118：线程竞态条件
- CWD-1119：TOCTOU 竞态条件
- CWD-1129：死锁
- ... 等等

### 代码质量（所有语言）
- CWD-1216：函数返回值未检查
- CWD-1234：部分字符串比较
- CWD-1264：不受控制的递归
- CWD-1295：整数回绕
- ... 等等 

## 参考资料

- CWD 缺陷字典：`references/cwds/`
- CWD 类别索引：`references/cwd_index.md`
- 报告模板：`templates/`
