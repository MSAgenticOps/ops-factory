# CWD-1432 不能抛出异常的函数无noexcept声明

**描述**
有些函数（如：C++语言中的delete 操作符、移动构造函数、移动赋值操作符、swap 函数、std::hash 的函数调用运算符、全局和线程局部`thread_local`变量的构造函数等）抛出异常，可能会导致 std::terminate。因此，建议这些函数添加 noexcept 限定词，以确保它们不抛出任何异常。

**语言: **CPP,JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1432-000 不能抛出异常的函数无noexcept声明

---

