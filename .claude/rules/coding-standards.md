# Coding Standards

> 代码质量检查工具（Spotless/Checkstyle/SpotBugs/ESLint）会自动验证大部分规则。以下为必须知晓的约束。

## Java 编码规则

| Rule ID | 规则 | 约束 |
|---------|------|------|
| G.COM.01 | JavaDoc | 所有 public/protected 的类、接口、枚举、方法必须有 `/** */` JavaDoc。实现方法可用 `{@inheritDoc}` |
| G.TYP.08 | Locale | `toUpperCase()`/`toLowerCase()`/`String.format()` 必须传 `Locale.ROOT` 或 `Locale.ENGLISH` |
| G.MET.02 | 圈复杂度 | ≤ 20 |
| G.MET.03 | 嵌套深度 | ≤ 4 层 |
| G.MET.04 | 可变参数 | 禁止 varargs（`String...`），用 `List<String>` 或数组替代 |
| G.DCL.07 | 变量遮蔽 | 禁止内层变量遮蔽外层同名变量 |
| G.DCL.08 | 布尔命名 | 必须用肯定前缀：`is`/`has`/`can`/`should`/`allow`。禁止否定命名如 `isNoError` |
| G.STY.01 | 空行 | 逻辑段之间最多 1 个空行，禁止连续空行 |
| G.STY.02 | 行宽 | ≤ 120 字符 |
| G.EXC.01 | 异常捕获 | 禁止 `catch Exception/Throwable` 基类，必须 catch 具体异常类型 |

### Import 规则（G.FMT.03）

- **禁止通配符 import**（如 `import java.util.*`），必须显式导入
- **Import 顺序**（组间空行分隔，每组内按字母序）：

1. `static` imports
2. `android.*`（安卓，本项目无）
3. `com.huawei.*`, `cn.huawei.*`（华为公司）
4. `com.*`（其他商业组织，如 `com.baomidou`、`com.alibaba`）
5. 其他开源第三方（非 com/net/org 前缀，如 `lombok`、`io.*`、`maven`）
6. `net.*`, `org.*`（开源组织）
7. `javacard.*`
8. `java.*`（java.base 模块）
9. 其他 Java SE 模块的包
10. `javax.*`, `jakarta.*`（Java 扩展包）

IDEA 快捷键 `Ctrl+Alt+O` 自动修复。

### 方法约束

- 方法参数最多 3 个，超过用 DTO
- 缩进 4 空格，K&R 大括号风格

## 前端编码规则
| 规则 | 约束 |
|------|------|
| 分号 | 每条语句必须以分号结尾 |
| 变量声明 | `const`/`let`，禁止 `var` |
| 组件命名 | PascalCase（`UserList`） |
| 文件命名 | kebab-case（`user-list.vue`） |
| Props | 必须声明 type + default，禁止数组语法 `props: ['title']` |
| `v-for` | 必须有稳定 `:key`（用 id），禁止用 index |
| Import 路径 | 使用 `@/` 别名，禁止 `../../../` |

## 安全清单
- 禁止硬编码密码/密钥
- 外部输入必须校验
- SQL 使用参数化查询（MyBatis-Plus 默认）
- 禁止 `v-html`（防 XSS）
- 禁止日志中输出敏感数据
- 异常信息不得泄露敏感内容
