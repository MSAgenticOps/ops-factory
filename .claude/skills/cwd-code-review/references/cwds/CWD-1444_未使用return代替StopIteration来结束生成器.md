# CWD-1444 未使用return代替StopIteration来结束生成器

**描述**
从Python3.7开始，生成器退出时的StopIteration将会被解释器转换成RuntimeError。在结束生成器时，无论是是使用StopIteration还是RuntimeError来结束生成器，外层都要用try/except语句去捕捉，这样不仅会让代码变得不太优雅，还会增加其复杂性。因此，使用return语句来结束生成器的迭代，可以像处理其他函数返回值一样处理生成器的返回值，使代码更加清晰易懂。

**语言: **PYTHON

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1444-000 未使用return代替StopIteration来结束生成器

---

