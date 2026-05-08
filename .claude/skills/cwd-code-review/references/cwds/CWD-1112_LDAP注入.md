# CWD-1112 LDAP注入

**别名: **对LDAP查询中使用的特殊元素中和不当（“LDAP注入”）；未能清理LDAP查询中的数据（“LDAP注入”）

**描述**
直接使用来自受外部影响的输入构造全部或部分LDAP查询，未进行处理可能修改预期LDAP查询的特殊元素。


- LDAP(Lightweight Directory Access Protocol):轻量级目录访问协议，是用于访问目录服务（特别是基于X.500的目录服务）的轻量级客户端服务器协议，它通过TCP/IP传输服务运行。- LDAP注入的攻击手法和SQL注入的原理非常相似，在有漏洞的环境中，查询参数没有得到合适的过滤，因而攻击者可以注入任意恶意代码。
**语言: **JAVA

**严重等级**
严重

**cleancode特征**
安全,可靠

**示例**
**案例1: 直接使用外部数据执行LDAP查询**
**语言: **JAVA

**描述**
一个使用LDAP（轻量级目录访问协议）作为员工信息管理系统，允许员工通过Web界面搜索同事的联系信息。系统接收用户输入的搜索条件，构造LDAP查询语句并在目录服务器上执行。由于缺乏输入验证和参数化处理，攻击者能够构造恶意输入实施LDAP注入攻击，获取敏感员工信息或执行未授权操作。

**案例分析**
攻击者可以：
1. 通过输入 `*)(objectClass=*))(|(objectClass=*` 作为用户名，绕过认证获取所有用户；
2. 通过输入 `*)(uid=admin` 尝试获取管理员账户信息；
3. 通过输入 `*)(|(password=*` 尝试获取密码字段（如果可读）。

**反例**
```java
// 不安全的LDAP查询构造方式
public List<User> searchUsers(String username, String department) {
    String searchFilter = "(&(objectClass=user)(cn=" + username + ")(department=" + department + "))";
    
    // 创建LDAP搜索请求
    SearchControls controls = new SearchControls();
    controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    
    // 【危险】：直接执行LDAP查询
    NamingEnumeration<SearchResult> results = context.search("ou=employees,dc=company,dc=com", 
        searchFilter, controls);
    
    // 处理结果...
    return processResults(results);
}
```

**正例**
```java
// 安全的LDAP查询构造方式
public List<User> searchUsers(String username, String department) {
    // 验证输入
    if (!isValidInput(username) || !isValidInput(department)) {
        throw new IllegalArgumentException("Invalid input detected");
    }
    
    // 使用参数化查询
    String searchFilter = "(&(objectClass=user)(cn={0})(department={1}))";
    
    // 创建LDAP搜索请求
    SearchControls controls = new SearchControls();
    controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    
    // 转义特殊字符并执行查询
    NamingEnumeration<SearchResult> results = context.search("ou=employees,dc=company,dc=com", 
        searchFilter, new Object[]{escapeLDAP(username), escapeLDAP(department)}, controls);
    
    // 处理结果...
    return processResults(results);
}

// LDAP特殊字符转义方法
private String escapeLDAP(String input) {
    if (input == null) {
        return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        switch (c) {
            case '\\':
                sb.append("\\5c");
                break;
            case '*':
                sb.append("\\2a");
                break;
            case '(':
                sb.append("\\28");
                break;
            case ')':
                sb.append("\\29");
                break;
            case '\0':
                sb.append("\\00");
                break;
            default:
                sb.append(c);
        }
    }
    return sb.toString();
}

// 输入验证方法
private boolean isValidInput(String input) {
    // 检查输入长度
    if (input == null || input.length() > 64) {
        return false;
    }
    // 检查输入格式（示例：只允许字母数字和特定字符）
    return input.matches("^[a-zA-Z0-9 .\\-@_]+$");
}
```

**修复建议**
- 输入验证：实施严格的白名单输入验证，只允许预期的字符集，验证输入长度和格式是否符合预期。
- 特殊字符转义：对所有用户提供的输入中的LDAP特殊字符进行转义，需要转义的字符包括：`\` `*` `(` `)` `\0`等。
- 使用参数化查询：使用LDAP API提供的参数化查询功能，而不是字符串拼接，大多数LDAP库都支持预编译的搜索过滤器与参数替换。

#### CWD-1112-000 LDAP注入

#### CWD-1112-001 特殊字符未校验，绕过认证执行敏感操作

**业界缺陷**

- [CWE-90: Improper Neutralization of Special Elements used in an LDAP Query ('LDAP Injection')](https://cwe.mitre.org/data/definitions/90.html)
---

