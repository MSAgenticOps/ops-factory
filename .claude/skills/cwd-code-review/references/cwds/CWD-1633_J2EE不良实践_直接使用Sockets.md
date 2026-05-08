# CWD-1633 J2EE不良实践：直接使用Sockets

**描述**
J2EE应用程序直接使用Sockets，而不是使用框架方法调用。J2EE标准只允许在没有更高级的协议可用时使用Sockets用于与遗留系统的通信。编写自己的通信协议需要解决棘手的安全问题。如果没有安全专家的认真审查，自定义通信协议很有可能会出现安全问题。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1633-000 J2EE不良实践：直接使用Sockets

**业界缺陷**

- [CWE-246: J2EE Bad Practices: Direct Use of Sockets](https://cwe.mitre.org/data/definitions/246.html)
---

