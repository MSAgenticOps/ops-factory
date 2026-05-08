# CWD-1008 使用可能导致内存布局不兼容的std：：vector<bool>

**描述**
`std::vector<bool>` 并不是简单地将每个布尔值存储在一个单独的 `bool` 类型的元素中，而是通过位域（bitfield）的方式进行存储，这样可以极大地节省内存空间。

但是，这种实现方式导致了 `std::vector<bool>` 的行为与普通 `std::vector<T>` 有所不同。

因此，不建议使用 `std::vector<bool>`，可以考虑 `std::bitset、std::array<bool, N>、std::deque` 或将 bool 包装起来等方法替代。

**语言: **CPP

**严重等级**
提示

**cleancode特征**
可靠,可移植

**示例**
**案例1: 使用`std::vector<bool>`**
**语言: **CPP

**描述**
由于 `std::vector<bool>` 的特殊实现，可能会导致内存布局上的不兼容问题，从而引发未定义行为或程序崩溃。

**反例**
```cpp
std::vector<bool> vec(8); // 不符合：使用 std::vector<bool>
// std::vector<bool>不支持此操作，编译会报错：auto &tmp = vec[0];
```

**正例**
```cpp
std::deque<bool> deq(8, false);  // 符合
auto &x = deq[0]; // 支持此操作
```

**修复建议**
需要处理布尔值集合时，不建议使用 `std::vector<bool>`，改用推荐的替代容器。

#### CWD-1008-000 使用可能导致内存布局不兼容的std::vector<bool>

#### CWD-1008-001 使用std::vector<bool>类型对象，导致内存布局上的不兼容问题

---

