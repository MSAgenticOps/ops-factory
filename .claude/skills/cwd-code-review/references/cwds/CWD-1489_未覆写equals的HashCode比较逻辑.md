# CWD-1489 未覆写equals的HashCode比较逻辑

**描述**
当对象需要进行逻辑相等的比较时（比如判断String、Integer对象中的值是否相同），应对Object的equals()方法进行覆写，实现具体的判断逻辑。覆写equals()方法时，要同步覆写hashCode()方法。Java对象在存放到基于Hash的集合（如HashMap、HashTable等）时，会使用其Hash码进行索引，如果只覆写了equals()方法，而没有正确覆写hashCode()方法，则会导致效率低下甚至出错。

**语言: **JAVA,RUST,KOTLIN,PHP

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1489-000 未覆写equals的HashCode比较逻辑

---

