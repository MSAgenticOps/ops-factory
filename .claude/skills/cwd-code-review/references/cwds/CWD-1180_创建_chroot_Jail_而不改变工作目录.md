# CWD-1180 创建 chroot Jail 而不改变工作目录

**别名: **无法在 chroot Jail 中更改工作目录

**描述**
在使用chroot系统调用来创建一个受限的运行环境（即chroot Jail）时，如果未更改当前工作目录到chroot Jail的根目录，可能会导致以下问题：1.chroot Jail失效： 程序可能仍然在原来的根目录下运行，无法实现预期的隔离效果。2.潜在的安全风险： 如果程序试图访问或修改系统的关键文件，可能会导致系统受损。3.不可预测的行为： 程序在chroot Jail外运行，可能导致不可预测的行为，影响系统的稳定性和安全性。

**语言: **C,CPP

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: 创建chroot Jail而不改变工作目录**
**语言: **C

**描述**
在风险代码中使用chroot创建一个受限的运行环境，但当前工作目录没有修改到chroot Jail下。

**案例分析**
chroot函数被调用来创建一个受限的运行环境，chroot之后，程序没有更改当前工作目录到chroot Jail的根目录。
这意味着程序仍然在原来的根目录下运行，chroot Jail并未生效，程序可以访问系统其他部分，导致安全风险。

**反例**
```c
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>

int main() {
    const char *chroot_dir = "/path/to/chroot/jail";

    // 创建chroot Jail
    if (chroot(chroot_dir) == -1) {
        perror("chroot");
        exit(EXIT_FAILURE);
    }

    // 未更改工作目录到chroot Jail的根目录

    // 执行一些操作
    execl("/bin/bash", "bash", NULL);

    return 0;
}
```

**正例**
```c
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>

int main() {
    const char *chroot_dir = "/path/to/chroot/jail";

    // 创建chroot Jail
    if (chroot(chroot_dir) == -1) {
        perror("chroot");
        exit(EXIT_FAILURE);
    }

    // 更改工作目录到chroot Jail的根目录
    if (chdir("/") == -1) {
        perror("chdir");
        exit(EXIT_FAILURE);
    }

    // 执行一些操作
    execl("/bin/bash", "bash", NULL);

    return 0;
}
```

**修复建议**
在chroot之后立即更改工作目录。

#### CWD-1180-000 创建 chroot Jail 而不改变工作目录

**业界缺陷**

- [CWE-243: Creation of chroot Jail Without Changing Working Directory](https://cwe.mitre.org/data/definitions/243.html)
---

