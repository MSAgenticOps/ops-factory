# CWD-1431 Iterator实现类的hasNext()方法中调用了next()方法

**描述**
调用Iterator.hasNext()不应该产生任何副作用，因此不应该改变迭代器的状态。Iterator.next()将迭代器前进了一项。因此在Iterator.hasNext()内部调用它会违反hasNext()的契约，并在生产环境中导致意外行为。

**语言: **JAVA,KOTLIN

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1431-000 Iterator实现类的hasNext()方法中调用了next()方法

---

