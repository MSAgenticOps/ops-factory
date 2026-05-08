# CWD-1032 使用try-catch捕获NullPointerException

**描述**
在Java中，空指针解引用问题会抛出NullPointerException。通过try...catch的方式进行处理，不仅可能影响代码的可读性，还可能降低系统的运行效率。因此，推荐在访问对象之前，使用条件语句（如if语句）来检查对象是否为null，以避免抛出异常。这样可以提高代码的可读性和性能，同时确保错误被明确处理。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
高效,可读

**示例**
**案例1: 通过捕获NullPointerException来处理空指针解引用错误**
**语言: **JAVA

**描述**
通过捕获NullPointerException来处理空指针异常，这可能影响代码的可读性和系统性能。

**反例**
```java
public boolean validateUntrustInput(String paramValue) { // paramValue 可能为空
    try {
        if (paramValue.length() < 6 || paramValue.length() > 20) {
            return false;
        }
        ...
        return true;
    } catch (NullPointerException ex) { // 不符合：通过捕获NullPointerException进行处理
        return false;
    }
}
```

**正例**
```java
public boolean validateUntrustInput(String paramValue) {  // paramValue 可能为空
    if (paramValue == null) { // 符合：明确检查了参数是否为null
        return false;
    }
    if (paramValue.length() < 6 || paramValue.length() > 20) {
        return false;
    }
    ...
    return true;
}
```

**修复建议**
在代码中明确检查可能为空的变量，避免使用try...catch来处理这些异常。

#### CWD-1032-000 使用try-catch捕获NullPointerException

#### CWD-1032-001 不要直接捕获可通过预检查进行处理的RuntimeException，如NullPointerException、IndexOutOfBoundsException等

**业界缺陷**

- [CWE-395: Use of NullPointerException Catch to Detect NULL Pointer Dereference](https://cwe.mitre.org/data/definitions/395.html)
---

