# CWD-1343 使用lambda作为decltype 或 typeid 的操作数

**描述**
lambda 表达式的类型（也是闭包对象的类型）是唯一的、未命名的非联合类类型[…]。每个 lambda 表达式都有一个不同的唯一底层类型，因此该类型不能用作 decltype 或 typeid 参数。允许使用它作为模板参数和函数参数。

**语言: **CPP

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1343-000 使用lambda作为decltype 或 typeid 的操作数

---

