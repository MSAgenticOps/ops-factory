# CWD-1424 使用不必要的else

**描述**
else存在或不存在不影响代码逻辑，可能会导致代码的可读性下降。例如，在raise、return、continue等语句后使用不必要的else。if i == 7:return aelse: # 不符合：多余的elsereturn b

**语言: **PYTHON

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1424-000 使用不必要的else

---

