# CWD-1086 CRLF注入

**别名: **CRLF序列中和不当；未能清理CRLF序列（“CRLF注入”）

**描述**
CRLF的含义是：回车（carriage return）、换行（line feed）。`CR`是回车（`%0d`或`\r`），`LF`是换行（`%0a`或`\n`）。不正确的解析或过滤回车和换行字符时导致的漏洞。CRLF注入漏洞的原因是使用CRLF（回车换行符）作为特殊元素，例如用于分隔行或记录，但不会中和或错误中和来自输入的CRLF序列。


- 通过URL相关参数中的CRLF拆分HTTP响应。例如：HTTP协议中使用CRLF（回车换行）作为消息头和消息体之间的分隔符，攻击者可以在HTTP响应中注入非法的CRLF字符，从而篡改HTTP消息头，欺骗浏览器或服务器，控制HTTP响应，实现攻击。如：
  1. HTTP头注入：攻击者可以在HTTP头中注入恶意代码，从而欺骗Web服务器执行攻击者的指令。  2. HTTP响应欺骗：攻击者可以篡改HTTP响应，从而欺骗用户访问恶意网站或下载恶意文件。  3. 会话劫持：攻击者可以通过CRLF注入攻击，修改HTTP响应头中的Cookie，从而劫持用户会话。- CRLF注入可以使用电子邮件地址或名称进行垃圾邮件代理（添加邮件头）。- 使用CRLF注入注入带有虚假时间戳的虚假日志条目。
**语言: **JAVA

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: 日志未考虑用户输入数据中的CRLF字符导致CRLF注入**
**语言: **JAVA

**描述**
未检查最终写入日志消息的用户输入数据中的CRLF字符，则攻击者可能伪造日志文件中的条目。

**案例分析**
下列应用程序代码将外部用户输入的数据直接拼接至日志文件中。如果用户为 `streetAddress` 提交字符串 `twenty-one` ，则会记录以下条目：
```log
[INFO] - User's street address: twenty-one
```
然而，如果攻击者提交字符串 `twenty-one%0a%0aINFO:+User+logged+out%3A+badguy` ，则日志中会记录以下条目：
```log
[INFO] - User's street address: twenty-one
[INFO] - User logged out: badguy
```
显然，攻击者可以使用同样的机制插入任意日志条目。

**反例**
```java
logger.info("User's street address: " + request.getParameter("streetAddress"));
```

**正例**
```java
String streetAddress = request.getParameter("streetAddress");
if (streetAddress != null) {
    // `CR`是回车（`%0d`或`\r`），`LF`是换行（`%0a`或`\n`）
    streetAddress = streetAddress.replaceAll("[\r|%0d|\n|%0a]", ""); 
}
logger.info("User's street address: " + streetAddress);
```

**修复建议**
- 输入过滤：在处理用户输入时，始终检查并移除潜在的危险字符，如 `CR`是回车（`%0d`或`\r`），`LF`是换行（`%0a`或`\n`）。
- 使用安全的API：考虑使用参数化的日志记录方法，如 `logger.info("User's street address: {}", streetAddress)`，以减少注入风险。
- 输入验证：使用正则表达式或其他验证机制，确保输入符合预期格式，例如只允许字母、数字和特定符号。

#### CWD-1086-000 CRLF注入

#### CWD-1086-001 HTTP请求头CRLF序列中和不当（“HTTP请求/响应拆分”）

#### CWD-1086-002 SMTP请求头CRLF序列中和不当（“SMTP请求/响应拆分”）

#### CWD-1086-003 日志CRLF序列中和不当（“日志拆分”）

**业界缺陷**

- [CWE-93: Improper Neutralization of CRLF Sequences ('CRLF Injection')](https://cwe.mitre.org/data/definitions/93.html)
---

