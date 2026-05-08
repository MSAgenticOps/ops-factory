# CWD（代码缺陷字典）参考索引

本索引提供 CWD 缺陷类别的按需加载。每个类别可以单独加载或按组加载。

----

## 完整类别列表

### 内存安全

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1002 | 内存分配大小未受限 | 未正确分配内存 | C,CPP,JAVA | cwds/CWD-1002_内存分配大小未受限.md | 
| CWD-1003 | 用于内存分配的缓冲区大小计算错误 | 未正确分配内存 | C,CPP | cwds/CWD-1003_用于内存分配的缓冲区大小计算错误.md | 
| CWD-1005 | 不正确的字节序 | 依赖错误的内存布局 | C,CPP | cwds/CWD-1005_不正确的字节序.md | 
| CWD-1006 | 依赖带位域的结构体的内存布局 | 依赖错误的内存布局 | C,CPP | cwds/CWD-1006_依赖带位域的结构体的内存布局.md | 
| CWD-1007 | 不正确的逐位操作 | 依赖错误的内存布局 | CPP | cwds/CWD-1007_不正确的逐位操作.md | 
| CWD-1008 | 使用可能导致内存布局不兼容的std__vector_bool_ | 依赖错误的内存布局 | CPP | cwds/CWD-1008_使用可能导致内存布局不兼容的std__vector_bool_.md | 
| CWD-1009 | 未受认可的内存安全函数 |  | C,CPP | cwds/CWD-1009_未受认可的内存安全函数.md | 
| CWD-1015 | 内存操作函数的源缓冲区访问长度设置不正确 | 内存访问长度不正确 | C,CPP | cwds/CWD-1015_内存操作函数的源缓冲区访问长度设置不正确.md | 
| CWD-1016 | 内存操作函数的目的缓冲区访问长度设置不正确 | 内存访问长度不正确 | C,CPP | cwds/CWD-1016_内存操作函数的目的缓冲区访问长度设置不正确.md | 
| CWD-1017 | 内存拷贝重叠 |  | C,CPP | cwds/CWD-1017_内存拷贝重叠.md | 
| CWD-1019 | 返回栈变量地址 | 栈变量地址传递到其作用域外 | C,CPP | cwds/CWD-1019_返回栈变量地址.md | 
| CWD-1021 | 释放非堆内存 | 释放无效内存 | C,CPP | cwds/CWD-1021_释放非堆内存.md | 
| CWD-1022 | 内存的申请和释放函数未配对 | 释放无效内存 | C,CPP | cwds/CWD-1022_内存的申请和释放函数未配对.md | 
| CWD-1023 | 释放未在缓冲区起始处的指针 | 释放无效内存 | C,CPP | cwds/CWD-1023_释放未在缓冲区起始处的指针.md | 
| CWD-1025 | 双重释放内存 | 过期指针解引用 | C,CPP | cwds/CWD-1025_双重释放内存.md | 
| CWD-1026 | 访问已释放内存 | 过期指针解引用 | C,CPP | cwds/CWD-1026_访问已释放内存.md | 
| CWD-1027 | 内存在有效生命周期后未释放（内存泄漏） |  | C,CPP | cwds/CWD-1027_内存在有效生命周期后未释放（内存泄漏）.md | 
| CWD-1028 | 数组索引越界 |  | C,CPP | cwds/CWD-1028_数组索引越界.md | 
| CWD-1029 | 指针偏移量超出范围 |  | C,CPP | cwds/CWD-1029_指针偏移量超出范围.md | 
| CWD-1030 | 访问未初始化的指针 |  | C,CPP,JAVA | cwds/CWD-1030_访问未初始化的指针.md | 
| CWD-1031 | 空指针解引用 |  | C,CPP,JAVA | cwds/CWD-1031_空指针解引用.md | 
| CWD-1034 | 不受信任的指针解引用 |  | C,CPP | cwds/CWD-1034_不受信任的指针解引用.md | 
| CWD-1038 | 不同类型的对象指针之间转换错误 | 指针类型转换错误 | C,CPP | cwds/CWD-1038_不同类型的对象指针之间转换错误.md | 
| CWD-1039 | 指针与非指针类型之间转换错误 | 指针类型转换错误 | C,CPP | cwds/CWD-1039_指针与非指针类型之间转换错误.md | 
| CWD-1040 | 不正确的null结束符 |  | C,CPP | cwds/CWD-1040_不正确的null结束符.md | 
| CWD-1042 | 未受控的格式化字符串 |  | C,CPP,JAVA | cwds/CWD-1042_未受控的格式化字符串.md | 
| CWD-1043 | 容器访问越界 |  | CPP,JAVA | cwds/CWD-1043_容器访问越界.md | 

### 输入校验

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1044 | 文件名或路径外部可控 |  | C,CPP,JAVA | cwds/CWD-1044_文件名或路径外部可控.md | 
| CWD-1045 | 依赖外部提供文件的文件名或扩展名 |  | C,CPP,JAVA | cwds/CWD-1045_依赖外部提供文件的文件名或扩展名.md | 
| CWD-1046 | 无限制上传具有危险类型的文件 |  | C,CPP,JAVA | cwds/CWD-1046_无限制上传具有危险类型的文件.md | 
| CWD-1047 | 验证框架使用不当 |  | JAVA | cwds/CWD-1047_验证框架使用不当.md | 
| CWD-1059 | 路径遍历 |  | C,CPP,JAVA | cwds/CWD-1059_路径遍历.md | 
| CWD-1062 | 路径等价解析不当 |  | JAVA | cwds/CWD-1062_路径等价解析不当.md | 
| CWD-1063 | 文件访问前链接解析不当（链接跟随） |  | JAVA | cwds/CWD-1063_文件访问前链接解析不当（链接跟随）.md | 
| CWD-1064 | Hook函数的入参验证不当 |  | C,CPP,JAVA,JAVASCRIPT | cwds/CWD-1064_Hook函数的入参验证不当.md | 
| CWD-1065 | 跨站请求伪造（CSRF） |  | JAVA,JAVASCRIPT,TYPESCRIPT | cwds/CWD-1065_跨站请求伪造（CSRF）.md | 
| CWD-1066 | 服务器请求伪造（SSRF） |  | JAVA,JAVASCRIPT,TYPESCRIPT | cwds/CWD-1066_服务器请求伪造（SSRF）.md | 
| CWD-1622 | 输入的语法合法性验证不当 |  | CPP,JAVA | cwds/CWD-1622_输入的语法合法性验证不当.md | 

### 注入

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1068 | OS命令注入 | 命令注入 | C,CPP,JAVA | cwds/CWD-1068_OS命令注入.md | 
| CWD-1069 | 参数注入 | 命令注入 | C,CPP,JAVA | cwds/CWD-1069_参数注入.md | 
| CWD-1070 | 正则表达式注入 | 命令注入 | JAVA,PYTHON | cwds/CWD-1070_正则表达式注入.md | 
| CWD-1071 | 表达式语言注入 | 命令注入 | JAVA | cwds/CWD-1071_表达式语言注入.md | 
| CWD-1072 | 邮件命令注入 | 命令注入 | JAVA | cwds/CWD-1072_邮件命令注入.md | 
| CWD-1073 | XSS注入 |  | JAVA,JSP,PHP | cwds/CWD-1073_XSS注入.md | 
| CWD-1081 | XML注入 |  | JAVA,GO,JAVASCRIPT,TYPESCRIPT | cwds/CWD-1081_XML注入.md | 
| CWD-1082 | XPath注入 |  | JAVA | cwds/CWD-1082_XPath注入.md | 
| CWD-1083 | XQuery注入 |  | JAVA | cwds/CWD-1083_XQuery注入.md | 
| CWD-1084 | XSLT注入 |  | JAVA | cwds/CWD-1084_XSLT注入.md | 
| CWD-1085 | JSON注入 |  | JAVA | cwds/CWD-1085_JSON注入.md | 
| CWD-1086 | CRLF注入 |  | JAVA | cwds/CWD-1086_CRLF注入.md | 
| CWD-1089 | 日志输出中和不当（“日志注入”） |  | JAVA,GO,JAVASCRIPT,TYPESCRIPT | cwds/CWD-1089_日志输出中和不当（“日志注入”）.md | 
| CWD-1094 | 动态代码注入（“Eval注入”） | 代码注入 | JAVA,PYTHON | cwds/CWD-1094_动态代码注入（“Eval注入”）.md | 
| CWD-1095 | 静态代码注入 | 代码注入 | JAVA | cwds/CWD-1095_静态代码注入.md | 
| CWD-1096 | 模板注入 | 代码注入 | JAVA | cwds/CWD-1096_模板注入.md | 
| CWD-1098 | 文件和其他资源的名称限制不当 | 资源注入 | JAVA | cwds/CWD-1098_文件和其他资源的名称限制不当.md | 
| CWD-1099 | 使用具有重复标识符的多个资源 | 资源注入 | C,CPP,JAVA | cwds/CWD-1099_使用具有重复标识符的多个资源.md | 
| CWD-1100 | Content_Provider_URI_注入 | 资源注入 | JAVA | cwds/CWD-1100_Content_Provider_URI_注入.md | 
| CWD-1101 | SQL注入 | 数据查询逻辑中特殊元素处理不当 | C,CPP,JAVA,PYTHON | cwds/CWD-1101_SQL注入.md | 
| CWD-1112 | LDAP注入 | 数据查询逻辑中特殊元素处理不当 | JAVA | cwds/CWD-1112_LDAP注入.md | 
| CWD-1113 | CSV注入 |  | JAVA | cwds/CWD-1113_CSV注入.md | 
| CWD-1114 | XML外部实体攻击（“XXE”） |  | JAVA,PYTHON | cwds/CWD-1114_XML外部实体攻击（“XXE”）.md | 
| CWD-1115 | XML内部实体扩展（“XEE”） |  | JAVA | cwds/CWD-1115_XML内部实体扩展（“XEE”）.md | 
| CWD-1842 | NoSQL注入 | 数据查询逻辑中特殊元素处理不当 | JAVA | cwds/CWD-1842_NoSQL注入.md | 
| CWD-1845 | JNDI注入 |  | JAVA | cwds/CWD-1845_JNDI注入.md | 

### 并发与并行

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1117 | 信号处理例程中发生的竞争条件 | 竞争条件 | C,CPP,JAVA | cwds/CWD-1117_信号处理例程中发生的竞争条件.md | 
| CWD-1118 | 线程内的竞争条件 | 竞争条件 | C,CPP,JAVA | cwds/CWD-1118_线程内的竞争条件.md | 
| CWD-1119 | 检查时间与使用时间(TOCTOU)的竞争条件 | 竞争条件 | C,CPP,JAVA | cwds/CWD-1119_检查时间与使用时间(TOCTOU)的竞争条件.md | 
| CWD-1120 | 上下文切换竞争条件 | 竞争条件 | C,CPP,JAVA | cwds/CWD-1120_上下文切换竞争条件.md | 
| CWD-1122 | 未受限制的外部可触及的锁 | 锁定不当 | JAVA | cwds/CWD-1122_未受限制的外部可触及的锁.md | 
| CWD-1123 | 资源未被正确锁定 | 锁定不当 | C,CPP,JAVA | cwds/CWD-1123_资源未被正确锁定.md | 
| CWD-1124 | 缺少锁定检查 | 锁定不当 | C,CPP,JAVA | cwds/CWD-1124_缺少锁定检查.md | 
| CWD-1125 | 双重检查锁定 | 锁定不当 | JAVA | cwds/CWD-1125_双重检查锁定.md | 
| CWD-1126 | 关键资源的多次锁定 | 锁定不当 | C,CPP,JAVA | cwds/CWD-1126_关键资源的多次锁定.md | 
| CWD-1127 | 关键资源的多次解锁 | 锁定不当 | C,CPP,JAVA | cwds/CWD-1127_关键资源的多次解锁.md | 
| CWD-1128 | 解锁未锁定的资源 | 锁定不当 | C,CPP,JAVA | cwds/CWD-1128_解锁未锁定的资源.md | 
| CWD-1129 | 死锁 | 锁定不当 | C,CPP,JAVA | cwds/CWD-1129_死锁.md | 
| CWD-1130 | 缺少同步 |  | C,CPP,JAVA | cwds/CWD-1130_缺少同步.md | 
| CWD-1132 | 在多线程上下文中对共享数据的非同步访问 |  | C,CPP,JAVA | cwds/CWD-1132_在多线程上下文中对共享数据的非同步访问.md | 
| CWD-1134 | 同步不正确 |  | C,CPP,JAVA | cwds/CWD-1134_同步不正确.md | 
| CWD-1135 | 在同步访问远程资源时未设置超时时间 |  | C,CPP,JAVA | cwds/CWD-1135_在同步访问远程资源时未设置超时时间.md | 
| CWD-1137 | 并发上下文中使用不可重入函数 |  | C,CPP,JAVA | cwds/CWD-1137_并发上下文中使用不可重入函数.md | 
| CWD-1611 | 持有锁时执行耗时或阻塞性操作 | 锁定不当 | C,CPP,JAVA | cwds/CWD-1611_持有锁时执行耗时或阻塞性操作.md | 
| CWD-1623 | 使用不正确的锁对象类型 | 锁定不当 | C,CPP,JAVA | cwds/CWD-1623_使用不正确的锁对象类型.md | 

### 资源管理

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1139 | 资源未初始化 | 初始化不正确 | C,CPP,JAVA | cwds/CWD-1139_资源未初始化.md | 
| CWD-1140 | 使用未初始化的资源 | 初始化不正确 | C,CPP,JAVA | cwds/CWD-1140_使用未初始化的资源.md | 
| CWD-1151 | 未受限的资源分配 | 未受控的资源消耗 | C,CPP,JAVA | cwds/CWD-1151_未受限的资源分配.md | 
| CWD-1152 | 缺失对已分配资源的引用 | 未受控的资源消耗 | C,CPP,JAVA | cwds/CWD-1152_缺失对已分配资源的引用.md | 
| CWD-1153 | 日志中记录的内容过多 | 未受控的资源消耗 | JAVA | cwds/CWD-1153_日志中记录的内容过多.md | 
| CWD-1155 | 高度压缩数据的处理不当 | 未受控的资源消耗 | C,CPP,JAVA | cwds/CWD-1155_高度压缩数据的处理不当.md | 
| CWD-1156 | 复杂度高且低效的正则表达式（正则ReDOS） | 未受控的资源消耗 | C,CPP,JAVA,PYTHON | cwds/CWD-1156_复杂度高且低效的正则表达式（正则ReDOS）.md | 
| CWD-1157 | 重复的正则预编译导致资源消耗过多 | 未受控的资源消耗 | JAVA | cwds/CWD-1157_重复的正则预编译导致资源消耗过多.md | 
| CWD-1158 | 循环中资源消耗过多 | 未受控的资源消耗 | C,CPP,JAVA | cwds/CWD-1158_循环中资源消耗过多.md | 
| CWD-1159 | 在性能关键的操作中不正确地使用自动装箱和拆箱 | 未受控的资源消耗 | JAVA | cwds/CWD-1159_在性能关键的操作中不正确地使用自动装箱和拆箱.md | 
| CWD-1160 | 忽视数据传递成本的函数参数与返回类型（按值、引用、指针） | 未受控的资源消耗 | C,CPP,JAVA | cwds/CWD-1160_忽视数据传递成本的函数参数与返回类型（按值、引用、指针）.md | 
| CWD-1161 | 资源消耗过大的线程操作 | 未受控的资源消耗 | JAVA | cwds/CWD-1161_资源消耗过大的线程操作.md | 
| CWD-1162 | 具有非常大迭代次数的循环 | 未受控的资源消耗 | JAVA | cwds/CWD-1162_具有非常大迭代次数的循环.md | 
| CWD-1163 | 性能低下的字符串拼接操作 | 未受控的资源消耗 | JAVA | cwds/CWD-1163_性能低下的字符串拼接操作.md | 
| CWD-1164 | 非跨语言场景中使用ESObject标记 | 未受控的资源消耗 | ARKTS | cwds/CWD-1164_非跨语言场景中使用ESObject标记.md | 
| CWD-1165 | 重复访问某个相同操作的表达式 | 未受控的资源消耗 | C,CPP,JAVA | cwds/CWD-1165_重复访问某个相同操作的表达式.md | 
| CWD-1166 | 单个条件扩展为多个条件导致的低效问题 | 未受控的资源消耗 | JAVA,SQL | cwds/CWD-1166_单个条件扩展为多个条件导致的低效问题.md | 
| CWD-1170 | 大数据表中的数据查询操作过多 | 未受控的资源消耗 | SQL | cwds/CWD-1170_大数据表中的数据查询操作过多.md | 
| CWD-1171 | 不受信任数据的反序列化 |  | C,CPP,JAVA,PYTHON | cwds/CWD-1171_不受信任数据的反序列化.md | 
| CWD-1172 | 修改假定不可变数据 |  | C,CPP,JAVA | cwds/CWD-1172_修改假定不可变数据.md | 
| CWD-1176 | protect_private成员被不正确的访问 |  | C,CPP,JAVA,PYTHON | cwds/CWD-1176_protect_private成员被不正确的访问.md | 
| CWD-1177 | clone方法实现不正确 |  | JAVA | cwds/CWD-1177_clone方法实现不正确.md | 
| CWD-1180 | 创建_chroot_Jail_而不改变工作目录 |  | C,CPP | cwds/CWD-1180_创建_chroot_Jail_而不改变工作目录.md | 
| CWD-1182 | 数值类型间转换错误 | 类型转换不正确 | C,CPP,JAVA | cwds/CWD-1182_数值类型间转换错误.md | 
| CWD-1183 | 不同进制的解析错误 | 类型转换不正确 | C,CPP,JAVA | cwds/CWD-1183_不同进制的解析错误.md | 
| CWD-1184 | 类型转换移除类型中的常量或易失性限定 | 类型转换不正确 | C,CPP | cwds/CWD-1184_类型转换移除类型中的常量或易失性限定.md | 
| CWD-1185 | 使用不兼容类型访问资源 | 类型转换不正确 | C,CPP | cwds/CWD-1185_使用不兼容类型访问资源.md | 
| CWD-1186 | 不合理的使用_auto_类型推导 | 类型转换不正确 | CPP | cwds/CWD-1186_不合理的使用_auto_类型推导.md | 
| CWD-1188 | 大小写敏感处理不正确 | 类型转换不正确 | C,CPP,JAVA | cwds/CWD-1188_大小写敏感处理不正确.md | 
| CWD-1190 | lambda表达式使用外部的循环代码中定义的变量 | 资源的跨作用域引用 | CPP,JAVA,PYTHON | cwds/CWD-1190_lambda表达式使用外部的循环代码中定义的变量.md | 
| CWD-1191 | 在闭包中直接使用循环控制变量 | 资源的跨作用域引用 | CPP,GO | cwds/CWD-1191_在闭包中直接使用循环控制变量.md | 
| CWD-1193 | 使用已过期的资源 | 访问已过期或关闭的资源 | C,CPP,JAVA | cwds/CWD-1193_使用已过期的资源.md | 
| CWD-1194 | 多次释放同一资源 | 访问已过期或关闭的资源 | C,CPP,JAVA | cwds/CWD-1194_多次释放同一资源.md | 
| CWD-1196 | 未在有效生命周期后释放资源 | 资源关闭或释放不正确 | C,CPP,JAVA,PYTHON | cwds/CWD-1196_未在有效生命周期后释放资源.md | 
| CWD-1197 | 对象的析构函数未被正确调用 | 资源关闭或释放不正确 | CPP | cwds/CWD-1197_对象的析构函数未被正确调用.md | 
| CWD-1198 | 资源创建和关闭未配对 | 资源关闭或释放不正确 | C,CPP,JAVA | cwds/CWD-1198_资源创建和关闭未配对.md | 
| CWD-1199 | 管理资源的引用计数器未被正确使用 | 资源关闭或释放不正确 | C,CPP | cwds/CWD-1199_管理资源的引用计数器未被正确使用.md | 
| CWD-1612 | 不安全的反射 |  | JAVA | cwds/CWD-1612_不安全的反射.md | 
| CWD-1624 | 绑定不受限制的IP地址 |  | C,CPP | cwds/CWD-1624_绑定不受限制的IP地址.md | 
| CWD-1625 | 将可变对象传递或返回给不受信任的域 |  | C,JAVA | cwds/CWD-1625_将可变对象传递或返回给不受信任的域.md | 
| CWD-1627 | 创建具有不安全权限的临时文件 | 不安全的临时文件 | C,CPP,JAVA | cwds/CWD-1627_创建具有不安全权限的临时文件.md | 
| CWD-1628 | 在具有不安全权限的目录中创建临时文件 | 不安全的临时文件 | C,CPP,JAVA | cwds/CWD-1628_在具有不安全权限的目录中创建临时文件.md | 
| CWD-1843 | 资源初始化不正确 | 初始化不正确 | C,CPP,JAVA | cwds/CWD-1843_资源初始化不正确.md | 
| CWD-1844 | 未完成清理 | 资源关闭或释放不正确 | C,CPP,JAVA | cwds/CWD-1844_未完成清理.md | 
| CWD-1847 | std__move使用不当 |  | CPP | cwds/CWD-1847_std__move使用不当.md | 

### 控制流管理

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1032 | 使用try-catch捕获NullPointerException | 未正确抛出异常或返回错误 | JAVA | cwds/CWD-1032_使用try-catch捕获NullPointerException.md | 
| CWD-1204 | 忽略异常 | 未正确抛出异常或返回错误 | CPP,JAVA | cwds/CWD-1204_忽略异常.md | 
| CWD-1205 | 抛出过于宽泛的异常 | 未正确抛出异常或返回错误 | CPP,JAVA | cwds/CWD-1205_抛出过于宽泛的异常.md | 
| CWD-1206 | 抛出的异常为对象指针 | 未正确抛出异常或返回错误 | CPP | cwds/CWD-1206_抛出的异常为对象指针.md | 
| CWD-1207 | 异常携带的信息缺失 | 未正确抛出异常或返回错误 | CPP,JAVA | cwds/CWD-1207_异常携带的信息缺失.md | 
| CWD-1208 | 构造方法直接抛出异常 | 未正确抛出异常或返回错误 | CPP,JAVA | cwds/CWD-1208_构造方法直接抛出异常.md | 
| CWD-1209 | 析构方法直接抛出异常 | 未正确抛出异常或返回错误 | CPP | cwds/CWD-1209_析构方法直接抛出异常.md | 
| CWD-1210 | 在main函数启动前或终止后抛出异常 | 未正确抛出异常或返回错误 | CPP | cwds/CWD-1210_在main函数启动前或终止后抛出异常.md | 
| CWD-1212 | 函数返回类型不一致 | 未正确抛出异常或返回错误 | C,CPP,PYTHON,JAVASCRIPT,TYPESCRIPT | cwds/CWD-1212_函数返回类型不一致.md | 
| CWD-1213 | 缺少状态码或错误码 | 未正确抛出异常或返回错误 | C,CPP,JAVA | cwds/CWD-1213_缺少状态码或错误码.md | 
| CWD-1214 | 不正确的状态码或错误码 | 未正确抛出异常或返回错误 | C,CPP,JAVA | cwds/CWD-1214_不正确的状态码或错误码.md | 
| CWD-1216 | 函数的返回值未检查 | 异常情况检查不正确 | C,CPP,JAVA | cwds/CWD-1216_函数的返回值未检查.md | 
| CWD-1217 | 函数的返回值检查不当 | 异常情况检查不正确 | C,CPP,JAVA | cwds/CWD-1217_函数的返回值检查不当.md | 
| CWD-1218 | 未捕获异常 | 异常情况检查不正确 | CPP,JAVA | cwds/CWD-1218_未捕获异常.md | 
| CWD-1220 | 捕获过于宽泛的异常 | 异常情况检查不正确 | CPP,JAVA | cwds/CWD-1220_捕获过于宽泛的异常.md | 
| CWD-1225 | 错误状态未作处置 | 异常情况处理不正确 | C,CPP,JAVA | cwds/CWD-1225_错误状态未作处置.md | 
| CWD-1226 | 初始化失败后不退出 | 异常情况处理不正确 | C,CPP,JAVA,GO | cwds/CWD-1226_初始化失败后不退出.md | 
| CWD-1227 | finally代码块非正常结束 | 异常情况处理不正确 | JAVA | cwds/CWD-1227_finally代码块非正常结束.md | 
| CWD-1228 | 缺少自定义错误页面 | 异常情况处理不正确 | JAVA | cwds/CWD-1228_缺少自定义错误页面.md | 
| CWD-1230 | 异常继承不正确 |  | CPP,JAVA | cwds/CWD-1230_异常继承不正确.md | 
| CWD-1231 | 未正确使用退出函数 |  | C,CPP,JAVA | cwds/CWD-1231_未正确使用退出函数.md | 
| CWD-1234 | 部分字符串比较 | 不完整的比较 | C,CPP,JAVA | cwds/CWD-1234_部分字符串比较.md | 
| CWD-1235 | 多条件表达式中缺少默认分支 | 不完整的比较 | C,CPP,JAVA | cwds/CWD-1235_多条件表达式中缺少默认分支.md | 
| CWD-1236 | 不带最小值检查的范围比较 | 不完整的比较 | C,CPP,JAVA | cwds/CWD-1236_不带最小值检查的范围比较.md | 
| CWD-1237 | 比较时两个对象的类型不兼容 |  | C,CPP,JAVA | cwds/CWD-1237_比较时两个对象的类型不兼容.md | 
| CWD-1238 | 比较操作使用了错误的对象 |  | C,CPP,JAVA | cwds/CWD-1238_比较操作使用了错误的对象.md | 
| CWD-1244 | 浮点数比较方法使用错误 |  | C,CPP,JAVA | cwds/CWD-1244_浮点数比较方法使用错误.md | 
| CWD-1246 | 正则表达式书写没有确切的边界 | 正则表达式定义错误 | C,CPP,JAVA | cwds/CWD-1246_正则表达式书写没有确切的边界.md | 
| CWD-1247 | 正则表达式书写未转义元字符 | 正则表达式定义错误 | JAVA | cwds/CWD-1247_正则表达式书写未转义元字符.md | 
| CWD-1248 | 操作符使用不正确 |  | C,CPP,JAVA | cwds/CWD-1248_操作符使用不正确.md | 
| CWD-1252 | 代码块定界不正确 |  | C,CPP | cwds/CWD-1252_代码块定界不正确.md | 
| CWD-1257 | 发布版本中存在可达断言 | 断言使用不正确 | C,CPP,JAVA | cwds/CWD-1257_发布版本中存在可达断言.md | 
| CWD-1262 | 带有多个逻辑表达式的条件语句含有副作用 |  | C,CPP,JAVA | cwds/CWD-1262_带有多个逻辑表达式的条件语句含有副作用.md | 
| CWD-1264 | 未受控制的递归 | 过度迭代 | C,CPP,JAVA | cwds/CWD-1264_未受控制的递归.md | 
| CWD-1265 | 循环退出条件不可达（死循环、无限循环） | 过度迭代 | C,CPP,JAVA | cwds/CWD-1265_循环退出条件不可达（死循环、无限循环）.md | 
| CWD-1266 | 在单线程、非阻塞上下文中使用阻塞代码 | 过度迭代 | C,CPP,JAVA | cwds/CWD-1266_在单线程、非阻塞上下文中使用阻塞代码.md | 
| CWD-1268 | 未正确验证循环条件 | 循环条件控制不正确 | C,CPP,JAVA | cwds/CWD-1268_未正确验证循环条件.md | 
| CWD-1269 | 在循环中更新循环条件值 | 循环条件控制不正确 | C,CPP,JAVA | cwds/CWD-1269_在循环中更新循环条件值.md | 
| CWD-1270 | 使用浮点变量来索引循环 | 循环条件控制不正确 | C,CPP,JAVA,GO | cwds/CWD-1270_使用浮点变量来索引循环.md | 
| CWD-1271 | 过度使用无条件跳转 |  | C,CPP,JAVA | cwds/CWD-1271_过度使用无条件跳转.md | 
| CWD-1272 | switch语句中缺少的break |  | C,CPP,JAVA | cwds/CWD-1272_switch语句中缺少的break.md | 
| CWD-1273 | 操作符优先级逻辑错误 |  | C,CPP,JAVA | cwds/CWD-1273_操作符优先级逻辑错误.md | 
| CWD-1274 | 控制语句中包含过多的条件 |  | C,CPP,JAVA | cwds/CWD-1274_控制语句中包含过多的条件.md | 
| CWD-1613 | 抛出过多的异常 | 未正确抛出异常或返回错误 | CPP,JAVA,PYTHON | cwds/CWD-1613_抛出过多的异常.md | 
| CWD-1629 | 重定向后执行(EAR) | 异常情况处理不正确 | JAVA,SHELL | cwds/CWD-1629_重定向后执行(EAR).md | 
| CWD-1846 | 函数返回个数不一致 | 未正确抛出异常或返回错误 | PYTHON,JAVASCRIPT,TYPESCRIPT | cwds/CWD-1846_函数返回个数不一致.md | 

### 信息暴露

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1275 | 错误消息中包含敏感信息 |  | C,CPP,JAVA | cwds/CWD-1275_错误消息中包含敏感信息.md | 
| CWD-1276 | 调试代码中插入敏感信息 |  | C,CPP,JAVA | cwds/CWD-1276_调试代码中插入敏感信息.md | 
| CWD-1277 | 序列化类中包含敏感信息 |  | JAVA,GO | cwds/CWD-1277_序列化类中包含敏感信息.md | 
| CWD-1278 | 缓存中包含敏感信息 |  | C,CPP,JAVA | cwds/CWD-1278_缓存中包含敏感信息.md | 
| CWD-1279 | 通过进程调用暴露敏感信息 |  | C,CPP,JAVA,JAVASCRIPT,CANGJIE,ARKTS | cwds/CWD-1279_通过进程调用暴露敏感信息.md | 
| CWD-1280 | 日志中包含敏感信息 |  | C,CPP,JAVA | cwds/CWD-1280_日志中包含敏感信息.md | 
| CWD-1281 | 在源代码中包含敏感信息 |  | C,CPP,JAVA | cwds/CWD-1281_在源代码中包含敏感信息.md | 
| CWD-1286 | 持久性Cookie中包含敏感信息 |  | JAVA | cwds/CWD-1286_持久性Cookie中包含敏感信息.md | 
| CWD-1287 | WSDL文件包含敏感信息 |  | JAVA,GO,JAVASCRIPT,TYPESCRIPT | cwds/CWD-1287_WSDL文件包含敏感信息.md | 
| CWD-1288 | 敏感信息在存储或传输前处理不当 |  | C,CPP,JAVA | cwds/CWD-1288_敏感信息在存储或传输前处理不当.md | 
| CWD-1289 | 敏感信息使用完毕后未清理 |  | C,CPP,JAVA,GO,SHELL | cwds/CWD-1289_敏感信息使用完毕后未清理.md | 
| CWD-1292 | 文件描述符泄露 |  | C,CPP | cwds/CWD-1292_文件描述符泄露.md | 

### 数值处理

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1295 | 整数回绕 | 整数溢出或回绕 | C,CPP,JAVA,GO,RUST | cwds/CWD-1295_整数回绕.md | 
| CWD-1296 | 整数溢出 | 整数溢出或回绕 | C,CPP,JAVA,GO,C# | cwds/CWD-1296_整数溢出.md | 
| CWD-1297 | 差一错误 |  | C,CPP,JAVA | cwds/CWD-1297_差一错误.md | 
| CWD-1298 | 除0错误 |  | C,CPP,JAVA | cwds/CWD-1298_除0错误.md | 
| CWD-1299 | 整数按位偏移不正确 |  | C,CPP,JAVA | cwds/CWD-1299_整数按位偏移不正确.md | 
| CWD-1300 | 多字节字符串长度计算不正确 |  | C,CPP | cwds/CWD-1300_多字节字符串长度计算不正确.md | 
| CWD-1302 | 浮点型数据精度或准确性不足 |  | C,CPP,JAVA | cwds/CWD-1302_浮点型数据精度或准确性不足.md | 
| CWD-1304 | sizeof计算错误 |  | C,CPP | cwds/CWD-1304_sizeof计算错误.md | 
| CWD-1309 | 使用指针减法确定大小 |  | C,CPP | cwds/CWD-1309_使用指针减法确定大小.md | 
| CWD-1310 | 含有变量自增或自减运算的表达式中再次引用该变量 |  | C,CPP,JAVA | cwds/CWD-1310_含有变量自增或自减运算的表达式中再次引用该变量.md | 
| CWD-1311 | 依赖未计算的操作数中的副作用 |  | C,CPP,JAVA | cwds/CWD-1311_依赖未计算的操作数中的副作用.md | 

### 不良实践

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1167 | 使用低效的API接口 |  | JAVA | cwds/CWD-1167_使用低效的API接口.md | 
| CWD-1168 | 使用低效的集合判空方法 |  | JAVA | cwds/CWD-1168_使用低效的集合判空方法.md | 
| CWD-1312 | 未遵循单一定义原则 |  | C,CPP,JAVA,PYTHON,JAVASCRIPT | cwds/CWD-1312_未遵循单一定义原则.md | 
| CWD-1313 | 变量声明未遵循就近原则 |  | C,CPP,JAVA,PYTHON,GO,JAVASCRIPT,TYPESCRIPT,ARKTS,KOTLIN | cwds/CWD-1313_变量声明未遵循就近原则.md | 
| CWD-1314 | 过于依赖全局变量 |  | JAVASCRIPT,TYPESCRIPT,ARKTS,LUA,PHP | cwds/CWD-1314_过于依赖全局变量.md | 
| CWD-1315 | 不合理的声明或定义 |  | TYPESCRIPT,ARKTS,PHP | cwds/CWD-1315_不合理的声明或定义.md | 
| CWD-1328 | 不合理的条件表达式 |  | ARKTS,PHP | cwds/CWD-1328_不合理的条件表达式.md | 
| CWD-1337 | 不合理的赋值表达式 |  | JAVA | cwds/CWD-1337_不合理的赋值表达式.md | 
| CWD-1340 | 不合理的lambda表达式 |  | PYTHON | cwds/CWD-1340_不合理的lambda表达式.md | 
| CWD-1344 | 不合理的模板与泛型编程 |  | CPP,JAVA | cwds/CWD-1344_不合理的模板与泛型编程.md | 
| CWD-1348 | 预处理使用不当 |  | C,CPP | cwds/CWD-1348_预处理使用不当.md | 
| CWD-1360 | 头文件使用不当 |  | C,CPP,JAVA | cwds/CWD-1360_头文件使用不当.md | 
| CWD-1369 | 包导入不当 |  | ARKTS | cwds/CWD-1369_包导入不当.md | 
| CWD-1374 | 类使用不当 |  | CANGJIE,ARKTS,PHP | cwds/CWD-1374_类使用不当.md | 
| CWD-1397 | 未正确编码或解码数据(Encoding_Error) |  | CPP,JAVA,RUST | cwds/CWD-1397_未正确编码或解码数据(Encoding_Error).md | 
| CWD-1403 | 未正确使用CGO特性 |  | C,CPP,JAVA | cwds/CWD-1403_未正确使用CGO特性.md | 
| CWD-1406 | 显式调用finalize(） |  | JAVA | cwds/CWD-1406_显式调用finalize(）.md | 
| CWD-1410 | 封装不足 |  | CPP,JAVA,TYPESCRIPT | cwds/CWD-1410_封装不足.md | 
| CWD-1414 | 无关代码 |  | JAVA,ARKTS,RUBY,PHP | cwds/CWD-1414_无关代码.md | 
| CWD-1429 | 使用被禁止的代码 |  | JAVASCRIPT,TYPESCRIPT,ARKTS,PHP | cwds/CWD-1429_使用被禁止的代码.md | 
| CWD-1430 | 不合理的函数设计或使用 |  | JAVASCRIPT,TYPESCRIPT,RUST,ARKTS,PHP | cwds/CWD-1430_不合理的函数设计或使用.md | 
| CWD-1454 | 函数参数使用不正确 |  | CPP,JAVA | cwds/CWD-1454_函数参数使用不正确.md | 
| CWD-1459 | 代码复杂度过高 |  | ARKTS,PHP | cwds/CWD-1459_代码复杂度过高.md | 
| CWD-1468 | 数据复杂度过高 |  | CPP,JAVA | cwds/CWD-1468_数据复杂度过高.md | 
| CWD-1473 | 不合理的日志信息记录 |  | C,CPP,JAVA,PYTHON,SHELL,ARKTS | cwds/CWD-1473_不合理的日志信息记录.md | 
| CWD-1484 | 在JS文件中使用JSX语法而不进行正确的编译或转换 |  | JAVASCRIPT | cwds/CWD-1484_在JS文件中使用JSX语法而不进行正确的编译或转换.md | 
| CWD-1485 | 使用已过时或弃用方法 |  | JAVASCRIPT,TYPESCRIPT,ARKTS,LUA,PHP | cwds/CWD-1485_使用已过时或弃用方法.md | 
| CWD-1487 | 发布版本中包含调试代码 |  | JAVASCRIPT,TYPESCRIPT,SHELL,ARKTS,PHP | cwds/CWD-1487_发布版本中包含调试代码.md | 
| CWD-1488 | 违反对象模型_只定义了Equals和Hashcode中的一个 |  | JAVA | cwds/CWD-1488_违反对象模型_只定义了Equals和Hashcode中的一个.md | 
| CWD-1491 | 在自动生成的代码中依赖指定的运行时组件 |  | C,CPP,JAVA | cwds/CWD-1491_在自动生成的代码中依赖指定的运行时组件.md | 
| CWD-1492 | 未考虑平台差异性 |  | C,CPP,JAVA | cwds/CWD-1492_未考虑平台差异性.md | 
| CWD-1498 | 使用与平台相关的第三方组件 |  | C,CPP,JAVA | cwds/CWD-1498_使用与平台相关的第三方组件.md | 
| CWD-1500 | 程序依赖于存在漏洞的第三方组件 |  | C,CPP,JAVA | cwds/CWD-1500_程序依赖于存在漏洞的第三方组件.md | 
| CWD-1502 | 不合理的测试用例 |  | CPP,JAVA,PYTHON,GO,TYPESCRIPT,SHELL,ARKTS,RUBY | cwds/CWD-1502_不合理的测试用例.md | 
| CWD-1503 | 使用告警抑制器来抑制工具检测 |  | PYTHON | cwds/CWD-1503_使用告警抑制器来抑制工具检测.md | 
| CWD-1504 | SHELL不良实践 |  | SHELL | cwds/CWD-1504_SHELL不良实践.md | 
| CWD-1513 | LUA不良实践 |  | LUA | cwds/CWD-1513_LUA不良实践.md | 
| CWD-1530 | SQL不良实践 |  | JAVA,ARKTS,SQL | cwds/CWD-1530_SQL不良实践.md | 
| CWD-1538 | VUE不良实践 |  | JAVA,JAVASCRIPT,TYPESCRIPT,SQL | cwds/CWD-1538_VUE不良实践.md | 
| CWD-1544 | RUBY不良实践 |  | RUBY | cwds/CWD-1544_RUBY不良实践.md | 
| CWD-1547 | KOTLIN不良实践 |  | KOTLIN | cwds/CWD-1547_KOTLIN不良实践.md | 
| CWD-1549 | XML不良实践 |  | XML | cwds/CWD-1549_XML不良实践.md | 
| CWD-1552 | FILE不良实践 |  | FILE | cwds/CWD-1552_FILE不良实践.md | 
| CWD-1553 | 补丁不良实践 |  | FILE | cwds/CWD-1553_补丁不良实践.md | 
| CWD-1616 | 注解使用不当 |  | CPP,JAVA,TYPESCRIPT | cwds/CWD-1616_注解使用不当.md | 
| CWD-1617 | 配置不良实践 |  | JAVASCRIPT,TYPESCRIPT,PHP | cwds/CWD-1617_配置不良实践.md | 
| CWD-1618 | YANG不良实践 |  | YANG | cwds/CWD-1618_YANG不良实践.md | 
| CWD-1619 | REACT不良实践 |  | JAVASCRIPT,TYPESCRIPT | cwds/CWD-1619_REACT不良实践.md | 
| CWD-1620 | YAML不良实践 |  | JAVA | cwds/CWD-1620_YAML不良实践.md | 
| CWD-1639 | 可序列化数据中包含不可序列化项 |  | C,CPP,JAVA | cwds/CWD-1639_可序列化数据中包含不可序列化项.md | 
| CWD-1640 | 数据库事务不良实践 |  | JAVA,PYTHON | cwds/CWD-1640_数据库事务不良实践.md | 

### 代码风格

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1554 | 命名不合规约 |  | C,CPP,ARKTS | cwds/CWD-1554_命名不合规约.md | 
| CWD-1559 | 注释不合规约 |  | C,CPP,JAVA,ARKTS | cwds/CWD-1559_注释不合规约.md | 
| CWD-1565 | 格式不合规约 |  | C,CPP,TYPESCRIPT,ARKTS,PHP | cwds/CWD-1565_格式不合规约.md | 

### 代码度量

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1586 | 代码规模过大 |  | C,CPP,ARKTS | cwds/CWD-1586_代码规模过大.md | 
| CWD-1587 | 超大目录 |  | C,CPP,ARKTS,MICROCODE | cwds/CWD-1587_超大目录.md | 
| CWD-1588 | 文件行过大 |  | C,CPP,JAVA,ARKTS | cwds/CWD-1588_文件行过大.md | 
| CWD-1592 | 文件重复 |  | C,CPP,JAVA,ARKTS | cwds/CWD-1592_文件重复.md | 
| CWD-1595 | 函数或方法行过大 |  | C,CPP,JAVA,ARKTS | cwds/CWD-1595_函数或方法行过大.md | 
| CWD-1598 | 代码重复 |  | C,CPP,JAVA,ARKTS | cwds/CWD-1598_代码重复.md | 
| CWD-1601 | 圈复杂度过大 |  | C,CPP,JAVA,ARKTS | cwds/CWD-1601_圈复杂度过大.md | 
| CWD-1604 | 代码嵌套深度过大 |  | C,CPP,JAVA,ARKTS,MICROCODE | cwds/CWD-1604_代码嵌套深度过大.md | 
| CWD-1605 | 目录层级深度过大 |  | JAVA,ARKTS | cwds/CWD-1605_目录层级深度过大.md | 
| CWD-1606 | 注释掉的代码块密度过大（代码度量_冗余代码） |  | C,CPP,JAVA,ARKTS,MICROCODE | cwds/CWD-1606_注释掉的代码块密度过大（代码度量_冗余代码）.md | 
| CWD-1607 | 编译告警密度过大 |  | C,CPP,ARKTS,MICROCODE | cwds/CWD-1607_编译告警密度过大.md | 
| CWD-1608 | 不安全函数 |  | C,CPP,ARKTS,MICROCODE | cwds/CWD-1608_不安全函数.md | 
| CWD-1609 | Halstead复杂度过大 |  | C,CPP,ARKTS | cwds/CWD-1609_Halstead复杂度过大.md | 
| CWD-1610 | 扇入扇出过大 |  | C,CPP,ARKTS | cwds/CWD-1610_扇入扇出过大.md | 

### 架构度量

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1824 | 循环依赖-Cyclic_Dependencies |  | C,CPP,JAVA | cwds/CWD-1824_循环依赖-Cyclic_Dependencies.md | 
| CWD-1826 | 破坏稳定抽象原则-SAP_Breakers |  | CPP,JAVA | cwds/CWD-1826_破坏稳定抽象原则-SAP_Breakers.md | 
| CWD-1827 | 拒绝父类馈赠-Refused_Parent_Bequest |  | CPP,JAVA | cwds/CWD-1827_拒绝父类馈赠-Refused_Parent_Bequest.md | 
| CWD-1828 | 传统破坏者-Tradition_Breaker |  | CPP,JAVA | cwds/CWD-1828_传统破坏者-Tradition_Breaker.md | 
| CWD-1829 | 继承层次混乱-Distorted_Hierarchy |  | CPP,JAVA | cwds/CWD-1829_继承层次混乱-Distorted_Hierarchy.md | 
| CWD-1830 | 上帝类-God_Class |  | CPP,JAVA | cwds/CWD-1830_上帝类-God_Class.md | 
| CWD-1831 | 上帝文件-God_File |  | C,CPP | cwds/CWD-1831_上帝文件-God_File.md | 
| CWD-1832 | 复杂类-Blob_Class |  | CPP,JAVA | cwds/CWD-1832_复杂类-Blob_Class.md | 
| CWD-1833 | 复杂文件-Blob_File |  | C,CPP | cwds/CWD-1833_复杂文件-Blob_File.md | 
| CWD-1834 | 精神分裂类-Schizophrenic_Class |  | CPP,JAVA | cwds/CWD-1834_精神分裂类-Schizophrenic_Class.md | 
| CWD-1835 | 精神分裂文件-Schizophrenic_File |  | C,CPP | cwds/CWD-1835_精神分裂文件-Schizophrenic_File.md | 
| CWD-1838 | 霰弹式修改-Shotgun_Surgery |  | C,CPP,JAVA | cwds/CWD-1838_霰弹式修改-Shotgun_Surgery.md | 
| CWD-1839 | 依恋情节-Feature_Envy |  | C,CPP,JAVA | cwds/CWD-1839_依恋情节-Feature_Envy.md | 
| CWD-1841 | 数据泥团-Data_Clumps |  | C,CPP,JAVA | cwds/CWD-1841_数据泥团-Data_Clumps.md | 

### 产品线自定义缺陷

| ID | 名称 | 子类别 | 语言 | 索引文件 | 
|----|------|-------|----------|----------| 
| CWD-1664 | 证书信任链的不当遵循 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1664_证书信任链的不当遵循.md | 
| CWD-1665 | 主机不匹配的证书验证不正确 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1665_主机不匹配的证书验证不正确.md | 
| CWD-1666 | 证书吊销检查不当 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1666_证书吊销检查不当.md | 
| CWD-1669 | 关键的功能缺少身份验证 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1669_关键的功能缺少身份验证.md | 
| CWD-1682 | 加密强度不足 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1682_加密强度不足.md | 
| CWD-1687 | 随机数生成的熵不足 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1687_随机数生成的熵不足.md | 
| CWD-1688 | 随机值空间小 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1688_随机值空间小.md | 
| CWD-1691 | 伪随机数生成器（PRNG）中存在可预测种子 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1691_伪随机数生成器（PRNG）中存在可预测种子.md | 
| CWD-1692 | 加密场景中使用伪随机数生成器（PRNG） | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1692_加密场景中使用伪随机数生成器（PRNG）.md | 
| CWD-1694 | 在基于环境动态变化的情景中使用不变值 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1694_在基于环境动态变化的情景中使用不变值.md | 
| CWD-1717 | 通过用户控制的SQL主键绕过授权 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1717_通过用户控制的SQL主键绕过授权.md | 
| CWD-1721 | URL重定向到不受信任的站点（“打开重定向”） | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1721_URL重定向到不受信任的站点（“打开重定向”）.md | 
| CWD-1730 | 使用非规范URL路径进行授权决策 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1730_使用非规范URL路径进行授权决策.md | 
| CWD-1736 | 权限保存不当 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1736_权限保存不当.md | 
| CWD-1764 | 使用有风险的密码学算法 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1764_使用有风险的密码学算法.md | 
| CWD-1781 | 不安全协议 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1781_不安全协议.md | 
| CWD-1848 | 对外开放API变更未保持向后兼容 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1848_对外开放API变更未保持向后兼容.md | 
| CWD-1849 | 没有“HttpOnly”标志的敏感Cookie | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1849_没有“HttpOnly”标志的敏感Cookie.md | 
| CWD-1850 | 不使用连接池的数据资源访问 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1850_不使用连接池的数据资源访问.md | 
| CWD-1851 | 访问控制粒度不够 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1851_访问控制粒度不够.md | 
| CWD-1852 | 具有不当SameSite属性的敏感Cookie | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1852_具有不当SameSite属性的敏感Cookie.md | 
| CWD-1853 | 权限分配不正确 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1853_权限分配不正确.md | 
| CWD-1854 | 证书信任链的不当遵循 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1854_证书信任链的不当遵循.md | 
| CWD-1855 | 证书过期验证不正确 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1855_证书过期验证不正确.md | 
| CWD-1856 | 敏感信息明文传输 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1856_敏感信息明文传输.md | 
| CWD-1857 | 符号名称未映射到正确的对象 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1857_符号名称未映射到正确的对象.md | 
| CWD-1858 | 网络消息量控制不足（网络放大） | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1858_网络消息量控制不足（网络放大）.md | 
| CWD-1859 | 使用包含敏感信息的Web浏览器缓存 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1859_使用包含敏感信息的Web浏览器缓存.md | 
| CWD-1860 | 通过目录列表公开信息 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1860_通过目录列表公开信息.md | 
| CWD-1861 | 敏感查询字符串的GET请求方法的使用 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1861_敏感查询字符串的GET请求方法的使用.md | 
| CWD-1862 | HTTPS会话中没有“安全”属性的敏感Cookie | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1862_HTTPS会话中没有“安全”属性的敏感Cookie.md | 
| CWD-1863 | 服务器端请求伪造(SSRF) | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1863_服务器端请求伪造(SSRF).md | 
| CWD-1864 | 非信任域的允许跨域策略 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1864_非信任域的允许跨域策略.md | 
| CWD-1865 | 数据存储约束不合理 | GTS产品线自定义缺陷 | SQL | cwds/CWD-1865_数据存储约束不合理.md | 
| CWD-1866 | 数据库存储的数据格式不兼容 | GTS产品线自定义缺陷 | SQL | cwds/CWD-1866_数据库存储的数据格式不兼容.md | 
| CWD-1867 | Nginx配置中的location优先级不正确 | GTS产品线自定义缺陷 | C,CPP,JAVA,PYTHON | cwds/CWD-1867_Nginx配置中的location优先级不正确.md | 
| CWD-1868 | 空值未能正确处理 | GTS产品线自定义缺陷 | JAVA | cwds/CWD-1868_空值未能正确处理.md | 

