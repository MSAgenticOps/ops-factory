# CWD-1040 不正确的null结束符

**描述**
字符串以null结束符（在C语言中是`'\0'`）结尾，表示字符串的结束。丢失或放错位置的结束符指的是在字符串操作（如复制或拼接）时，结束符未被正确放置或丢失，导致程序无法正确识别字符串的结束位置。

这种情况可能导致缓冲区溢出，程序读取或写入无效内存区域，引发程序崩溃、数据损坏或安全漏洞（如恶意代码注入）。

为了避免这种漏洞，需确保目标缓冲区足够大，使用安全的字符串函数，并在动态内存分配时预留足够的空间以容纳结束符，避免因结束符处理不当引发问题。

**语言: **C,CPP

**严重等级**
提示

**cleancode特征**
安全,可靠

**示例**
**案例1: 没有正确检查readlink返回值导致null结束符被写到缓冲区之外**
**语言: **C

**描述**
```
// 函数原型
#include <unistd.h>
ssize_t readlink(const char *path, char *buf, size_t bufsiz);
```
readlink()会将参数path的符号链接内容存储到参数buf所指的内存空间，buf不是以null结尾的。readlink()执行成功则返回字符串的字符数，失败返回-1， 错误代码存于errno。

反例中未检查readlink()的返回值r是否小于缓冲区大小BUF_SIZE，直接在filePath[r]处添加空终止符，可能导致越界。

**反例**
```c
void ReadlinkBadCase02()
{
    char filePath[BUF_SIZE];
    ssize_t r = readlink("some_file_path", filePath, sizeof(filePath));
    if (r == -1) {
        return;
    }
    filePath[r] = '\0';  // 不符合：未检查r是否小于BUF_SIZE，可能导致缓冲区溢出
}
```

**正例**
```c
void ReadlinkGoodCase01()
{
    char filePath[BUF_SIZE];
    ssize_t r = readlink("some_file_path", filePath, sizeof(filePath));
    if (r != -1 && r < sizeof(filePath)) {
        filePath[r] = '\0';  // 符合: 确认返回值不是`-1，并且小于BUF_SIZE
    }
}
```

**修复建议**
调用readlink函数时，需要：
1. 检查返回值：在调用readlink()后，首先检查返回值是否为-1，以确定是否发生错误。
2. 确保缓冲区足够：在添加空终止符之前，确保返回值小于缓冲区的大小，避免越界。

#### CWD-1040-000 不正确的null结束符

#### CWD-1040-001 数据来源为内部有结束符的字符串，经过字符串操作函数处理后，生成的字符串不带结束符，导致字符串访问越界

#### CWD-1040-002 数据来源为内部无结束符的字符串，传递给依赖 null 结束符的string变量，导致字符串访问越界

#### CWD-1040-003 数据来源为内部无结束符的字符串，传递给依赖 null 结束符的字符串操作函数，导致字符串访问越界

#### CWD-1040-004 数据来源为内部无结束符的字符串，经过字符串操作函数处理后，生成的字符串不带结束符，导致字符串访问越界

#### CWD-1040-005 数据来源为外部无结束符的字符串，传递给依赖 null 结束符的string变量，导致字符串访问越界

#### CWD-1040-006 数据来源为外部无结束符的字符串，传递给依赖 null 结束符的字符串操作函数，导致字符串访问越界

#### CWD-1040-007 数据来源为外部无结束符的字符串，经过字符串操作函数处理后，生成的字符串不带结束符，导致字符串访问越界

#### CWD-1040-008 数据来源为外部有结束符的字符串，经过字符串操作函数处理后，生成的字符串不带结束符，导致字符串访问越界

**业界缺陷**

- [CWE-170: Improper Null Termination](https://cwe.mitre.org/data/definitions/170.html)
---

