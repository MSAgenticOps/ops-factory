# CWD-1210 在main函数启动前或终止后抛出异常

**描述**
在 main 函数启动前或终止后抛出异常会无法被捕获。

**语言: **CPP

**严重等级**
提示

**cleancode特征**
可维护

**示例**
**案例1: main 函数启动前或终止后抛出异常**
**语言: **CPP

**描述**
在 main 函数启动前或终止后抛出异常会无法被捕获

**反例**
```cpp
class C
{
    public :
    C ( )
    {
        throw (  ); // Non-compliant – thrown before main starts
    }
    ~C ( )
    {
        throw (  ); // Non-compliant – thrown after main exits
    }
};
C c; // An exception thrown in C  s constructor or destructor
// will cause the program to terminate, and will not be
// caught by the handler in main
int main ( … )
{
    try
    {
        // program code
        return ;
    }
    // The following catch-all exception handler can only catch exceptions
    // thrown in the above program code
    catch ( … )
    {
        // Handle exception
        return ;
    }
}
```

**正例**
```cpp
class C
{
    public :
    C ( )
    {
        throw (  ); 
    }
    ~C ( )
    {
    }
};

int main ( … )
{
    C c; 
    try
    {
        // program code
        return ;
    }
    catch ( … )
    {
        // Handle exception
        return ;
    }
}
```

**修复建议**
避免在 main 函数启动前或终止后抛出异常

#### CWD-1210-000 在main函数启动前或终止后抛出异常

---

