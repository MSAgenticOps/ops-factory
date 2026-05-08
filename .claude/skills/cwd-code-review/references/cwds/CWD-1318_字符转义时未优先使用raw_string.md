# CWD-1318 字符转义时未优先使用raw string

**描述**
raw string是一种特殊的字符串表示方式，可以在字符串中包含特殊字符而不需要进行转义。在需要转义的场景下，如未使用raw string，就需要对每个反斜杠进行转义，显得比较繁琐且难懂。

**语言: **CPP,PYTHON,GO,RUBY

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1318-000 字符转义时未优先使用raw string

---

