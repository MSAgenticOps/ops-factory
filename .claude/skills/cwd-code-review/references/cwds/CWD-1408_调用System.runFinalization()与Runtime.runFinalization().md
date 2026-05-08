# CWD-1408 调用System.runFinalization()与Runtime.runFinalization()

**描述**
即使是主动调用System.gc()和System.runFinalization()，也仅会增加Finalizer机制被执行的几率，也不能保证一定会被执行。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1408-000 调用System.runFinalization()与Runtime.runFinalization()

---

