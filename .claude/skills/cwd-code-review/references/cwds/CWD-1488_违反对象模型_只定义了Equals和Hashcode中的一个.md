# CWD-1488 违反对象模型：只定义了Equals和Hashcode中的一个

**描述**
没有为相等的对象维护相等的hashcode值（相等的对象hashcode值必须相同）。例如：如果a.equals(b) == true，则a.hashCode() == b.hashCode()。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1488-000 违反对象模型：只定义了Equals和Hashcode中的一个

**业界缺陷**

- [CWE-581: Object Model Violation: Just One of Equals and Hashcode Defined](https://cwe.mitre.org/data/definitions/581.html)
---

