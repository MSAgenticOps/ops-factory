# CWD-1409 finalize()声明为public

**描述**
将一个 finalize() 方法声明为public，违反了移动代码的安全编码原则。永远不应该显式调用final，除非在final()的实现内部调用super.finalize()。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1409-000 finalize()声明为public

**业界缺陷**

- [CWE-583: finalize() Method Declared Public](https://cwe.mitre.org/data/definitions/583.html)
---

