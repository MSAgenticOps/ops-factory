# CWD-1401 Unicode编码处理不当

**描述**
当输入包含Unicode编码时，没有正确地处理。攻击者可以向不支持Unicode的系统组件提供Unicode字符串，并使用它来绕过过滤器或导致分类机制无法正确理解请求。这可能允许攻击者将恶意数据滑过内容过滤器和/或可能导致应用程序错误地路由请求。例如：http://target.server/some_directory/../../../winnt攻击者试图遍历到不应该是标准Web服务的一部分的目录。这个技巧是相当明显的，所以很多Web服务器和脚本阻止它。然而，使用替代编码技巧，攻击者可能能够绕过错误实现的请求过滤器。

**语言: **C,CPP,JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1401-000 Unicode编码处理不当

**业界缺陷**

- [CWE-176: Improper Handling of Unicode Encoding](https://cwe.mitre.org/data/definitions/176.html)
---

