# CWD-1406 显式调用finalize(）

**描述**
从终结器外部显式调用了 finalize() 方法。例如：显式调用final()意味着将多次调用final()：第一次是显式调用，最后一次是对象被垃圾回收后进行的调用。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1406-000 显式调用finalize(）

**业界缺陷**

- [CWE-586: Explicit Call to Finalize()](https://cwe.mitre.org/data/definitions/586.html)
---

