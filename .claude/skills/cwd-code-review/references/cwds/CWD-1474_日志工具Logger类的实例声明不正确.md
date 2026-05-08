# CWD-1474 日志工具Logger类的实例声明不正确

**描述**
对于工具类的实例，如果声明时并进行了初始化，应该声明为private static final，如果只是声明但未初始化，则应声明为private final。声明为private是出于访问封装的考虑，防止Logger类的实例对象被其他类非法使用；声明为static是为了防止重复new出Logger类的实例，造成资源的浪费，同时防止实例被序列化，造成安全风险（精心设计的library除外）；声明为final是因为在类的生命周期内无需变更Logger类的实例。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1474-000 日志工具Logger类的实例声明不正确

---

