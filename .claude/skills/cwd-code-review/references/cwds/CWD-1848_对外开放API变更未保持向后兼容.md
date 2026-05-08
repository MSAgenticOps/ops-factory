# CWD-1848 对外开放API变更未保持向后兼容

**描述**
对外API不兼容是指在系统版本演进过程中，未遵循向后兼容原则，导致外部调用方因接口变更（如参数结构调整、字段删除/重命名、返回格式变化、校验增强、HTTP方法变更等）而无法正常调用或解析响应。此类问题虽不直接构成安全漏洞，但可能引发业务中断、数据错乱、客户端崩溃，甚至被攻击者利用进行协议混淆、注入或绕过逻辑校验。典型场景包括：

接口升级时删除或重命名必填参数，但未提供兼容路径；返回结构体中移除关键字段，导致下游强依赖该字段的服务异常；将原本宽松的字符串类型参数改为严格枚举类型，但未做旧值映射，添加特殊字符校验；修改 HTTP 状态码语义（如将 200 改为 201），影响自动化流程判断；在未通知调用方的情况下调整 API 路径（如 /v1/user → /api/v2/users）。

**语言: **JAVA

**严重等级**
一般

**cleancode特征**
可维护,可测试

**示例**
**案例1: 因盲目增强特殊字符校验，破坏 API 兼容性，影响正常业务流程**
**语言: **JAVA

**描述**
在系统升级过程中，开发团队为提升安全性或数据规范性，对接口入参增加了严格的校验规则（如非空、枚举限制、格式匹配、特殊字符校验等），但未评估现有调用方的兼容性，也未提供过渡方案（如默认值、宽松模式、版本隔离）。结果导致旧客户端因参数“看似合法”但不符合新规则而被拒绝，引发批量调用失败。

- 典型场景：
  某系统提供 /api/v1/customers/create 接口供合作伙伴创建客户资料。初期为支持国际化姓名，customerName 字段允许任意 UTF-8 字符串（包括 ', "", &, # 等）。后续安全团队发现日志中存在潜在 XSS 风险，要求禁止“危险字符”。开发团队在未评估历史数据和调用方的情况下，在原接口中直接添加正则校验 ^[a-zA-Z0-9\s]+$，导致大量包含合法特殊字符的客户名（如 O’Reilly, AT&T, Suite #101）被拒绝，引发客户投诉和数据同步中断

**反例**

- 升级前（v1 - 宽松模式，支持合法特殊字符）
```java
// CustomerControllerV1.java
@RestController
@RequestMapping(""/api/v1/customers"")
public class CustomerControllerV1 {

    public static class CreateCustomerRequest {
        private String customerName; // 允许任意字符串，包括 ' "" & # 等
        private String email;

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @PostMapping(""/create"")
    public ResponseEntity<String> createCustomer(@RequestBody CreateCustomerRequest request) {
        // 直接保存，无特殊字符校验
        customerService.save(request.getCustomerName(), request.getEmail());
        return ResponseEntity.ok(""Customer created"");
    }
}
```


- 升级后（直接修改 v1，未做兼容）
```java
// CustomerControllerV1.java（错误升级）
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping(""/api/v1/customers"")
public class CustomerControllerV1 {

    public static class CreateCustomerRequest {
        @Pattern(regexp = ""^[a-zA-Z0-9\\s]+$"", message = ""customerName must contain only letters, digits and spaces"")
        private String customerName;

        private String email;

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @PostMapping(""/create"")
    public ResponseEntity<String> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        customerService.save(request.getCustomerName(), request.getEmail());
        return ResponseEntity.ok(""Customer created"");
    }
}
```"
```

**正例**

- 发布 v2，安全输出 + 兼容输入
```java
// CustomerControllerV2.java
@RestController
@RequestMapping(""/api/v2/customers"")
public class CustomerControllerV2 {

    public static class CreateCustomerRequest {
        // 允许更宽泛的输入，但内部做安全处理
        private String customerName;
        private String email;

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @PostMapping(""/create"")
    public ResponseEntity<String> createCustomer(@RequestBody CreateCustomerRequest request) {
        String name = request.getCustomerName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(""customerName is required"");
        }

        // ✅ 安全做法：不限制输入字符，但在输出/存储时做编码或转义
        // 例如：日志记录时使用占位符，HTML 输出时自动转义
        String safeNameForLog = name.replaceAll(""[\r\n\t]"", ""_""); // 防日志注入
        logger.info(""Creating customer: {}"", safeNameForLog);     // 使用 SLF4J 参数化日志

        // 存储原始值（业务需要），前端展示时由模板引擎自动转义
        customerService.save(name, request.getEmail());

        return ResponseEntity.ok(""Customer created via v2"");
    }
}
```

**修复建议**
1. 强制 API 版本隔离
   所有破坏性变更必须通过新版本路径（如 /v2）发布；
2. 旧版本至少保留 6 个月，并通过文档/邮件通知调用方迁移。
   校验增强 ≠ 拒绝旧数据
   对枚举、格式等字段，提供兼容反序列化器（如 Jackson 的 @JsonCreator）；
3. 对缺失字段，设置业务合理的默认值，而非强制报错。
   渐进式校验策略
4. 初期开启“监控模式”：记录不合规请求但不拒绝；
   通过日志分析调用方分布，定向推动升级；
5. 最终切换为“严格模式”。
   契约先行 & 自动化验证
   使用 OpenAPI 定义每个版本的接口契约；
   通过 Pact 或 Spring Cloud Contract 实现消费者驱动契约测试；
6. CI/CD 中加入接口兼容性检查（如 diff OpenAPI spec）。

#### CWD-1848-000 对外开放API变更未保持向后兼容

#### CWD-1848-001 对外接口入参校验增强导致不兼容

---

