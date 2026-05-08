# CWD-1065 跨站请求伪造（CSRF）

**别名: **会话制导；跨站点引用伪造；XSRF

**描述**
Web应用程序未能验证或未能充分验证请求是否是由发送请求的用户故意提供的，而发送请求的用户可能来自未授权的参与者，攻击者通过诱导用户点击一个链接或者访问一个恶意网站，从而在用户不知情的情况下，以用户的身份向目标网站发送请求，执行一些操作，比如转账、修改密码等。这种攻击利用了用户的认证状态，比如cookie，来执行恶意操作。因此针对状态不断变化的 HTTP 请求必须包含用户特有的密码，以防止攻击者发出未经授权的请求。跨站请求伪造（CSRF）漏洞会在以下情况下发生：


1. Web 应用程序使用会话 Cookie。2. 应用程序未验证请求是否经过用户同意便处理 HTTP 请求。

- 由于该应用程序无法确定请求的来源，才有可能受到 CSRF 攻击。任何请求都有可能是用户选定的合法操作，也有可能是攻击者设置的伪操作。攻击者无法查看伪请求生成的网页，因此，这种攻击技术仅适用于篡改应用程序状态的请求。- 如果受害者是管理员或特权用户，其后果可能包括获得对Web应用程序的完全控制-删除或窃取数据，卸载产品，或使用它对产品的所有用户发起其他攻击。由于攻击者具有受害者的身份，因此CSRF的范围仅受受害者的权限限制。- 如果应用程序通过 URL 传递会话标识符（而不是 Cookie），则不会出现 CSRF 问题，因为攻击者无法访问会话标识符，也无法在伪请求中包含会话标识符。- 部分框架会自动将 CSRF 随机数 包含在其中，以保护应用程序的安全。禁用此功能会将应用程序置于风险之中。
**语言: **JAVA,JAVASCRIPT,TYPESCRIPT

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: HTTP请求使用用户特有密码防CSRF攻击**
**语言: **JAVA

**描述**
状态不断变化的 HTTP 请求必须包含用户特有的密码，以防止攻击者发出未经授权的请求。

**案例分析**
攻击者可以设置一个包含以下代码的恶意网站：
```java
RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "http://www.example.com/new_user");
body = addToPost(body, "attacker";
body = addToPost(body, "haha");
rb.sendRequest(body, new NewAccountCallback(callback));
```
- 如果 `example.com` 的管理员在网站上具有活动会话时访问了恶意页面，则他们会在毫不知情的情况下为攻击者创建一个帐户。这就是 CSRF 攻击。正是由于该应用程序无法确定请求的来源，才有可能受到 CSRF 攻击。任何请求都有可能是用户选定的合法操作，也有可能是攻击者设置的伪操作。攻击者无法查看伪请求生成的网页，因此，这种攻击技术仅适用于篡改应用程序状态的请求。
- 如果应用程序通过 URL 传递会话标识符（而不是 Cookie），则不会出现 CSRF 问题，因为攻击者无法访问会话标识符，也无法在伪请求中包含会话标识符。
- 部分框架会自动将 CSRF 随机数包含在其中，以保护应用程序的安全。禁用此功能会将应用程序置于风险之中。

**反例**
```java
RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/new_user");
body = addToPost(body, new_username);
body = addToPost(body, new_passwd);
rb.sendRequest(body, new NewAccountCallback(callback));
```

**正例**
```java
// 生成CSRF令牌并存储在会话中
String csrfToken = UUID.randomUUID().toString();
session.setAttribute("csrfToken", csrfToken);

// 在客户端获取CSRF令牌并添加到请求体中
RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/new_user");
String body = addToPost("", new_username);
body = addToPost(body, new_passwd);
body = addToPost(body, "csrf_token", csrfToken);

// 发送请求
rb.sendRequest(body, new NewAccountCallback(callback));

// 在服务器端验证CSRF令牌
public void doPost(HttpServletRequest request, HttpServletResponse response) {
    String providedToken = request.getParameter("csrf_token");
    String sessionToken = (String) request.getSession().getAttribute("csrfToken");
    
    if (providedToken == null || !providedToken.equals(sessionToken)) {
        // 拒绝请求，返回错误信息
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return;
    }
    
    // 处理请求
}
```

**修复建议**
- 生成CSRF令牌：在用户登录时，服务器使用 `UUID.randomUUID().toString()` 生成一个唯一的随机令牌，并将其存储在用户的会话中。
- 添加令牌到请求体：在客户端发送敏感请求时，将CSRF令牌作为参数`csrf_token`添加到请求体中。
- 验证令牌：在服务器端的`doPost`方法中，获取请求中的`csrf_token`和会话中的令牌进行比较，如果不匹配则拒绝请求。

**案例2: HTTP请求使用用户特有密码防CSRF攻击**
**语言: **JAVASCRIPT

**描述**
HTTP 请求必须包含用户特定的密码，以防止攻击者发出未经授权的请求。

**案例分析**
随机数是加密随机值，随消息发送，以防止转发攻击。如果请求中不包含证明其来源的随机数，则用来处理该请求的代码容易受到 CSRF 攻击（除非请求不改变应用程序的状态）。这意味着使用会话 Cookie 的 Web 应用程序必须采取特殊的预防措施，以确保攻击者无法诱骗用户提交虚假请求。某个 Web 应用程序允许管理员新建帐户。攻击者可以设置一个包含以下代码的恶意网站。
```js
var req = new XMLHttpRequest();
req.open("POST", "http://www.example.com/new_user", true);
body = addToPost(body, "attacker");
body = addToPost(body, "haha");
req.send(body);
```
- 如果 `example.com` 的管理员在网站上具有活动会话时访问了恶意页面，则会在毫不知情的情况下为攻击者创建一个帐户。这就是 CSRF 攻击。正是由于该应用程序无法确定请求的来源，才有可能受到 CSRF 攻击。任何请求都有可能是用户选定的合法操作，也有可能是攻击者设置的伪操作。攻击者无法查看伪请求生成的网页，因此，这种攻击技术仅适用于篡改应用程序状态的请求。
- 如果应用程序通过 URL 传递会话标识符（而不是 cookie），则不会出现 CSRF 问题，因为攻击者无法访问会话标识符，也无法在伪请求中包含会话标识符。

**反例**
```js
var req = new XMLHttpRequest();
req.open("POST", "/new_user", true);
body = addToPost(body, new_username);
body = addToPost(body, new_passwd);
req.send(body);
```

**正例**
```js
var req = new XMLHttpRequest();
req.open("POST", "/new_user", true);

// 假设csrfToken是服务器生成的令牌，并嵌入到页面中
var body = addToPost(body, new_username);
body = addToPost(body, new_passwd);
body = addToPost(body, csrfToken); // 添加CSRF令牌到请求体

req.send(body);
```

**修复建议**
- 生成CSRF令牌：在服务器端生成一个唯一的、不可预测的令牌，并将其嵌入到页面中，例如通过一个隐藏的input字段或JavaScript变量。
- 添加令牌到请求：在发送POST请求时，将CSRF令牌添加到请求体中。
- 服务器端验证：在处理请求时，检查请求中是否包含有效的CSRF令牌，并与服务器端存储的令牌进行匹配，如果不匹配则拒绝请求。

**案例3: HTTP请求使用用户特有密码防CSRF攻击**
**语言: **TYPESCRIPT

**描述**
HTTP 请求必须包含用户特定的密码，以防止攻击者发出未经授权的请求。

**案例分析**
随机数是加密随机值，随消息发送，以防止转发攻击。如果请求中不包含证明其来源的随机数，则用来处理该请求的代码容易受到 CSRF 攻击（除非请求不改变应用程序的状态）。这意味着使用会话 Cookie 的 Web 应用程序必须采取特殊的预防措施，以确保攻击者无法诱骗用户提交虚假请求。某个 Web 应用程序允许管理员新建帐户。攻击者可以设置一个包含以下代码的恶意网站。
```ts
var req = new XMLHttpRequest();
req.open("POST", "http://www.example.com/new_user", true);
body = addToPost(body, "attacker");
body = addToPost(body, "haha");
req.send(body);
```
- 如果 `example.com` 的管理员在网站上具有活动会话时访问了恶意页面，则会在毫不知情的情况下为攻击者创建一个帐户。这就是 CSRF 攻击。正是由于该应用程序无法确定请求的来源，才有可能受到 CSRF 攻击。任何请求都有可能是用户选定的合法操作，也有可能是攻击者设置的伪操作。攻击者无法查看伪请求生成的网页，因此，这种攻击技术仅适用于篡改应用程序状态的请求。
- 如果应用程序通过 URL 传递会话标识符（而不是 cookie），则不会出现 CSRF 问题，因为攻击者无法访问会话标识符，也无法在伪请求中包含会话标识符。

**反例**
```ts
var req = new XMLHttpRequest();
req.open("POST", "/new_user", true);
body = addToPost(body, new_username);
body = addToPost(body, new_passwd);
req.send(body);
```

**正例**
```ts
var req = new XMLHttpRequest();
req.open("POST", "/new_user", true);

// 假设csrfToken是服务器生成的令牌，并嵌入到页面中
var body = addToPost(body, new_username);
body = addToPost(body, new_passwd);
body = addToPost(body, csrfToken); // 添加CSRF令牌到请求体

req.send(body);
```

**修复建议**
- 生成CSRF令牌：在服务器端生成一个唯一的、不可预测的令牌，并将其嵌入到页面中，例如通过一个隐藏的input字段或JavaScript变量。
- 添加令牌到请求：在发送POST请求时，将CSRF令牌添加到请求体中。
- 服务器端验证：在处理请求时，检查请求中是否包含有效的CSRF令牌，并与服务器端存储的令牌进行匹配，如果不匹配则拒绝请求。

#### CWD-1065-000 跨站请求伪造（CSRF）

#### CWD-1065-001 Spring Security中禁用CSRF保护

#### CWD-1065-002 使用GET请求执行状态变更操作

#### CWD-1065-003 仅通过Referer头验证请求来源

#### CWD-1065-004 CSRF令牌未与用户会话绑定

#### CWD-1065-005 doGet方法中直接调用非查询类方法，存在CSRF攻击的风险

#### CWD-1065-006 doPost方法中直接调用doGet方法，doGet方法存在非查询类操作，通过doGet方法进行CSRF攻击

#### CWD-1065-007 USecurity安全框架通过web.xml将X-Frame-Options的值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari中不支持，存在风险

#### CWD-1065-008 Huawei WSF安全框架通过hwsf-security.xml配置文件中frame-options元素属性的disable属性关闭了点击劫持头的加固

#### CWD-1065-009 Huawei WSF安全框架通过web.xml将X-Frame-Options的值设置为ALLOW-FROM

#### CWD-1065-010 Tomcat通过FilterRegistrationBean<HttpHeaderSecurityFilter>过滤器关闭了X-Frame-Options头加固

#### CWD-1065-011 Tomcat通过web.xml配置过滤器时关闭了X-Frame-Options头加固

#### CWD-1065-012 Huawei WSF安全框架通过FilterRegistrationBean<HeaderWriterFilter>过滤器设置X-Frame-Options的值为ALLOW-FROM

#### CWD-1065-013 nginx.conf配置文件中add_header配置项将X-Frame-Options的值设置为ALLOW-FROM

#### CWD-1065-014 Spring Security框架通过HttpSecurity类关闭了frameOptions头加固，存在点击劫持风险

#### CWD-1065-015 通过setHeader/addHeader方式单独为请求设置控制响应头的X-Frame-Options

#### CWD-1065-016 USecurity安全框架通过FilterRegistrationBean<UsHeaderWriterFilter>过滤器设置X-Frame-Options的值为ALLOW-FROM

#### CWD-1065-017 Tomcat通过HttpSecurity类设置X-Frame-Options的header加固项值为ALLOW-FROM

#### CWD-1065-018 Huawei WSF安全框架通过hwsf-security.xml配置文件将frame-options元素的policy属性设置为ALLOW-FROM

#### CWD-1065-019 Tomcat通过HttpSecurity类设置X-Frame-Options的header加固项值为ALLOW-FROM

#### CWD-1065-020 servlet框架通过setHeader/addHeader配置X-Frame-Options值为ALLOW-FROM

**业界缺陷**

- [CWE-352: Cross-Site Request Forgery (CSRF)](https://cwe.mitre.org/data/definitions/352.html)
---

