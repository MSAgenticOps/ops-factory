# CWD-1227 finally代码块非正常结束

**描述**
在finally代码块中，由于直接使用return、break、continue语句，或者由于调用方法的异常未处理，会导致finally代码块无法正常结束。非正常结束的finally代码块会影响try或catch代码块中异常的抛出，也可能会影响函数或方法的返回值。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

**示例**
**案例1: finally代码块中使用return语句**
**语言: **JAVA

**描述**
finally代码块中执行return 语句，会影响函数的实际返回值。

**反例**
```java
public int doSomething() {    
    int result = 0;
    ... 
    try {
        result = doSomethingElse();
        ... 
        return result;		
    } catch (IOException ex) {
        ...
    } finally {
        return -1;
    }   
}
```

**正例**
```java
public int doSomething() {    
    int result = 0;
    ... 
    try {
        result = doSomethingElse();
        ... 
        return result;		
    } catch (IOException ex) {
        ...
    }
    return -1;   
}
```

**修复建议**
避免在finally代码块中执行return 语句。

#### CWD-1227-000 finally代码块非正常结束

#### CWD-1227-001 finally存在抛出异常的逻辑

**业界缺陷**

- [CWE-584: Return Inside Finally Block](https://cwe.mitre.org/data/definitions/584.html)
---

