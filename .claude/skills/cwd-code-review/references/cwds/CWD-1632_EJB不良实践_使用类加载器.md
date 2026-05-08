# CWD-1632 EJB不良实践：使用类加载器

**描述**
使用类加载器违反了Enterprise JavaBeans (EJB)规范。Enterprise JavaBeans规范要求每个Bean提供者遵循一组编程指南，这些指南旨在确保Bean是可移植的，并且在任何EJB容器中行为一致。在这种情况下，产品违反了以下EJB准则：“企业Bean不得尝试创建类加载器；获取当前类加载器；设置上下文类加载器；设置安全管理器；创建新的安全管理器；停止JVM；或更改输入、输出和错误流。”该规范以以下方式证明了此要求的正当性：这些函数是为EJB容器保留的。允许企业Bean使用这些功能可能会损害安全性，并降低容器正确管理运行时环境的能力。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1632-000 EJB不良实践：使用类加载器

**业界缺陷**

- [CWE-578: EJB Bad Practices: Use of Class Loader](https://cwe.mitre.org/data/definitions/578.html)
---

