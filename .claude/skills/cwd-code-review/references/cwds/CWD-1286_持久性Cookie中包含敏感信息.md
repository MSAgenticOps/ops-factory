# CWD-1286 持久性Cookie中包含敏感信息

**描述**
有两种不同类型的 Cookie：会话 Cookie 和持久性 Cookie。会话 cookie 仅存在于浏览器的内存中，不会存储在任何地方，但持久性 cookie 存储在浏览器的硬盘驱动器上。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
安全

**示例**
**案例1: 持久型cookie中包含敏感信息**
**语言: **JAVA

**描述**
在持久型cookie中包含明暗信息，信息会被保存在磁盘中，容易产生泄露风险。

**反例**
```java
String jwt = createJwt();		
Cookie jwtCookie = new Cookie("Authorization", jwt);
jwtCookie.setMaxAge(-1);
response.addCookie(jwtCookie);
```

**正例**
```java
String jwt = createJwt();		
Cookie jwtCookie = new Cookie("Authorization", jwt);
response.addCookie(jwtCookie);
```

**修复建议**
必须使用cookie传递敏感信息时，使用会话cookie。

#### CWD-1286-000 持久性Cookie中包含敏感信息

#### CWD-1286-001 带有Max-Age的Cookie的值中包含了敏感信息

**业界缺陷**

- [CWE-539: Use of Persistent Cookies Containing Sensitive Information](https://cwe.mitre.org/data/definitions/539.html)
---

