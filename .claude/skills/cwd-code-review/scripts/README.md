# 说明

本目录为开发过程，用于生成脚本分割原始的CWD文件以及生成索引文件

# 提示词

## split_cwd_dict提示词
生成Python代码完成如下功能:
 - 读取CWD代码缺陷字典 V1.5_2.md文件
 - 按H1标题对内容进行拆分
 - 每一级H1标题 拆到 一个 独立文件中，文件的名称为H1的标题名称，如`CWD-1002 内存分配大小未受限`
 

## generate_cwd_index提示词
生成python代码完成如下功能:
 - 读取CWD代码缺陷字典全景视图_2026_在研.csv
 - 转成一个名为cwd_index.md的markdown文件，格式参考如下

```
# CWD（代码缺陷字典）参考索引

本索引提供 CWD 缺陷类别的按需加载。每个类别可以单独加载或按组加载。

----

## 完整类别列表

### 内存安全

| ID | 名称 |子类别 | 语言 | 索引文件 | 
|----|------|------|----------|----------|-
| CWD-1002 | 内存分配大小未限制| 内存分配大小未限制 | C,C++,Java | cwds/CWD-1002_内存分配大小未受限.md |
| .. | ..  |..  |..  | ..  | 

### 输入校验

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|------|----------|----------|-
| CWD-1002 | 文件名或路径外部可控 |  | C,C++,Java | cwds/CWD-1044_文件名或路径外部可控.md |
| .. | ..  |..  | ..  | 

----
```
  - 其中CWD的名称调用下面sanitize_filename函数进行清理
 
def sanitize_filename(filename: str) -> str:
    """
    清理文件名，移除或替换不允许的字符
    """
    # 替换特殊字符
    filename = filename.replace('：', '_')
    filename = filename.replace(':', '_')
    filename = filename.replace('"', '_')
    filename = filename.replace('"', '_')
    filename = filename.replace('<', '_')
    filename = filename.replace('>', '_')
    filename = filename.replace('/', '_')
    filename = filename.replace('\\', '_')
    filename = filename.replace('|', '_')
    filename = filename.replace('?', '_')
    filename = filename.replace('*', '_')
    filename = filename.replace(' ', '_')
    filename = filename.strip()

    # 确保文件名不为空
    if not filename:
        filename = "unnamed_section"

    return filename
