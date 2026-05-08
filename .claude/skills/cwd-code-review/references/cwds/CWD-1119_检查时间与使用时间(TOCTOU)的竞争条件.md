# CWD-1119 检查时间与使用时间(TOCTOU)的竞争条件

**描述**
程序在检查资源状态和实际使用资源之间存在时间窗口，攻击者可以利用这个窗口修改资源的状态，导致检查结果失效。这种漏洞可能导致攻击者执行未经授权的操作，例如访问受限的资源、替换文件导致读取或执行恶意文件，以及绕过权限检查，获取未授权的访问或操作权限。为了防止TOCTOU漏洞，建议采用原子操作、使用文件锁或其他同步机制来确保资源的检查和使用是原子的，避免中间的时间窗口被利用。

**语言: **C,CPP,JAVA

**严重等级**
提示

**cleancode特征**
安全,可靠

**示例**
**案例1: 文件检查与打开之间的时间窗口导致竞争条件**
**语言: **C

**描述**
在IO操作中，即使先检查文件存在，再打开文件，也会有文件在刚检查完，还没有打开的时间差内被删除或修改的情况，导致竞争条件漏洞。

**案例分析**
首先使用access函数检查文件filename是否存在。如果文件存在，程序打开文件并读取内容。问题在于，access和fopen之间存在时间窗口，如果在此期间文件被删除或修改，fopen可能会失败或读取到不一致的数据，导致竞争条件漏洞。

**反例**
```c
int Process(char *filename)
{
    // 检查文件是否存在
    if (access(filename, F_OK) == 0) {
        ... // 时间窗口，攻击者可能删除或修改文件
        // 打开文件
        FILE *file = fopen(filename, "r");
        ...
        fclose(file);
    }
    return SUCCESS;
}
```

**正例**
```c
int Process(char *filename)
{
    // 直接打开文件
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        return FAILURE;
    }
    ...
    fclose(file);
    return SUCCESS;
}
```

**修复建议**
直接尝试打开文件，并在打开失败时处理错误，而不是先检查文件是否存在。这样可以避免竞争条件漏洞。

**案例2: 文件检查与打开之间的时间窗口导致竞争条件**
**语言: **CPP

**描述**
在IO操作中，即使先检查文件存在，再打开文件，也会有文件在刚检查完，还没有打开的时间差内被删除或修改的情况，导致竞争条件漏洞。

**案例分析**
首先使用access函数检查文件filename是否存在。如果文件存在，程序打开文件并读取内容。问题在于，access和文件打开之间存在时间窗口，如果在此期间文件被删除或修改，文件打开可能会失败或读取到不一致的数据，导致竞争条件漏洞。

**反例**
```cpp
int Process(char *filename)
{
    // 不符合：检查文件是否存在
    if (access(fileName, R_OK) == 0) {
        std::ifstream file(fileName);   // 有可能失败
        ...
    }
    return SUCCESS;
}
```

**正例**
```cpp
int Process(char *filename)
{
    std::ifstream file(fileName);
    if (file.fail()) {   // 符合：及时检查处理并中断执行
        return FAILURE;
    }
    ...
    return SUCCESS;
}
```

**修复建议**
直接尝试打开文件，并在打开失败时处理错误，而不是先检查文件是否存在。这样可以避免竞争条件漏洞。

#### CWD-1119-000 检查时间与使用时间(TOCTOU)的竞争条件

#### CWD-1119-001 检查时间与使用时间时间差导致的竞争

**业界缺陷**

- [CWE-367: Time-of-check Time-of-use (TOCTOU) Race Condition](https://cwe.mitre.org/data/definitions/367.html)
---

