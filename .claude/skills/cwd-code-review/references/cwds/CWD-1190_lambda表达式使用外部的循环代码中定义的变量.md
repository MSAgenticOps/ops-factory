# CWD-1190 lambda表达式使用外部的循环代码中定义的变量

**描述**
在C++中，lambda表达式可以捕获其作用域中的变量。然而，如果在循环内部定义lambda，并且lambda捕获了循环变量，可能会导致意外的行为。这是因为lambda捕获的是变量的引用，而不是变量的当前值。当循环结束后，所有lambda可能共享同一个变量，导致它们引用的变量值在循环结束后被修改，从而引发不可预测的结果。

**语言: **CPP,JAVA,PYTHON

**严重等级**
一般

**cleancode特征**
可靠,可读

**示例**
**案例1: lambda表达式使用外部的循环代码中定义的变量**
**语言: **CPP

**描述**
在风险代码中，lambda使用引用捕获了循环变量i。当循环结束后，i的值变为3。

**案例分析**
在这个风险代码中，lambda使用引用捕获了循环变量i。当循环结束后，i的值变为3。因此，所有lambda在调用时都会输出3，而不是它们在循环中对应的值0、1、2。这是因为所有lambda引用了同一个变量i，而i在循环结束后已经被修改

**反例**
```c++
#include <vector>
#include <functional>

int main() {
    std::vector<std::function<void()>> lambdas;
    int i;

    for (i = 0; i < 3; ++i) {
        lambdas.push_back([=]() { // 使用引用捕获i
            std::cout << "Lambda value: " << i << std::endl;
        });
    }

    for (const auto& lambda : lambdas) {
        lambda();
    }

    return 0;
}
```

**正例**
```c++
#include <vector>
#include <functional>

int main() {
    std::vector<std::function<void()>> lambdas;

    for (int i = 0; i < 3; ++i) {
        int current_i = i; // 创建当前循环变量的副本
        lambdas.push_back([current_i]() { // 捕获current_i的值
            std::cout << "Lambda value: " << current_i << std::endl;
        });
    }

    for (const auto& lambda : lambdas) {
        lambda();
    }

    return 0;
}
```

**修复建议**
1.捕获变量的值：在lambda中使用值捕获，而不是引用捕获。例如，使用[=]来捕获所有变量的值，或者显式地捕获特定变量的值。
2.创建变量副本：在循环内部创建循环变量的副本，并在lambda中捕获这个副本。这样，每个lambda都会捕获当前循环迭代的值，而不是共享同一个变量。

**案例2: lambda表达式使用外部循环变量**
**语言: **PYTHON

**描述**
在Python中，当在循环中使用lambda表达式引用循环变量时，lambda会延迟绑定变量的值。这意味着所有lambda函数都会引用循环结束后的变量值，而不是循环过程中的当前值。

**案例分析**
反例分析：
- map函数返回一个生成器对象，延迟计算。
- 循环结束后，i的值为4。
- 所有生成器在遍历时使用i=4，导致结果错误。

**反例**
```python
map_list = []
for i in range(5):
    # 不符合，循环中使用lambda，延迟调用
    map_list.append(map(lambda j: i + j, range(5)))

for unit in map_list:
    print(list(unit))


执行结果：
[4, 5, 6, 7, 8]
[4, 5, 6, 7, 8]
[4, 5, 6, 7, 8]
[4, 5, 6, 7, 8]
[4, 5, 6, 7, 8]
```

**正例**
```python
for i in range(5):
    # 符合，list是强制进行计算并转为list格式，是立即执行的
    print(list(map(lambda j: i + j, range(5))))


执行结果：
 [0, 1, 2, 3, 4]
 [1, 2, 3, 4, 5]
 [2, 3, 4, 5, 6]
 [3, 4, 5, 6, 7]
 [4, 5, 6, 7, 8]
```

**修复建议**
lambda表达式不能引用循环变量，如果在循环中立即调用函数或使用lambda表达式，而不是循环结束之后才调用或使用，可以在函数或lambda表达式中使用外部循环中的变量。

#### CWD-1190-000 lambda表达式使用外部的循环代码中定义的变量

---

