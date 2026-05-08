# CWD-1304 sizeof计算错误

**描述**
`sizeof`运算符用于计算数据类型或变量所占的字节数。如果`sizeof`运算符计算错误，会导致之后基于计算结果的内存操作出问题，例如错误的sizeof使用可能导致内存分配不足或过多，进而引发缓冲区越界错误。因此，必须确保sizeof运算符用于正确的数据类型或变量。

**语言: **C,CPP

**严重等级**
提示

**cleancode特征**
安全,可靠

**示例**
**案例1: 将指针当做数组进行sizeof操作**
**语言: **C

**描述**
将指针当作数组进行sizeof操作时，返回的是指针本身的内存大小，而不是指针指向的内存块的大小。这种错误可能导致内存操作不正确。

**反例**
```c
#define SIZE 1000

void TestBadCase1()
{
    char path[MAX_PATH];
    char *buffer = (char *)malloc(SIZE);
    ...
    memset(path, 0, sizeof(path));

    // 不符合：sizeof(buffer)返回指针大小，不是分配的内存大小
    memset(buffer, 0, sizeof(buffer));
}
```

**正例**
```c
#define SIZE 1000

void TestGoodCase1()
{
    char path[MAX_PATH];
    char *buffer = (char *)malloc(SIZE);
    ...
    memset(path, 0, sizeof(path));

    // 符合：使用分配的内存大小SIZE
    memset(buffer, 0, SIZE);
}
```

**修复建议**
对于指针变量，不能通过sizeof获取该指针指向的内存大小，应该通过上文确认指针指向的内存大小。

#### CWD-1304-000 sizeof计算错误

#### CWD-1304-001 对指针类型使用sizeof()，导致错误的计算结果

#### CWD-1304-002 对容器使用sizeof()，导致错误的计算结果

#### CWD-1304-003 对宏、常量或枚举使用sizeof()，导致错误的计算结果

#### CWD-1304-004 使用sizeof计算的数组元素个数错误

---

