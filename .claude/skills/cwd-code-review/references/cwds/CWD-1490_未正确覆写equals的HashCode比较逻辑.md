# CWD-1490 未正确覆写equals的HashCode比较逻辑

**描述**
只覆写了equals方法，而没有正确地覆写hashCode方法，两个对象在equals方法比较相等时，但它们的hashCode值却不相等，会导致它们在HashMap或HashSet等集合中被认为是不同的对象。

**语言: **JAVA,KOTLIN

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1490-000 未正确覆写equals的HashCode比较逻辑

---

