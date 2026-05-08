# CWD-1450 将Character串联到 StringBuffer.append(String)中

**描述**
在java中StringBuffer()的append()提供了Character和String两种方式，对于需要添加char类型的时候，避免使用String，例如：如下代码：（推荐，性能高）String text = new StringBuffer().append("some string").append('c').toString();（不推荐，性能低）String text = new StringBuffer().append("some string").append("c").toString();

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1450-000 将Character串联到 StringBuffer.append(String)中

---

