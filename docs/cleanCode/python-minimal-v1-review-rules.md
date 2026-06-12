# Python 最小规则集 V1 代码审查规则
> 本文件由一个公司 电子表格 规则集生成，并保留源文件中的全部规则；不要把它当作最小子集使用。
## 来源
- 来源 电子表格：`运营商服务与软件python规则最小集(编程规范3.1)_V1.0.xlsx`
- 来源目录：原始规则目录
- 提取规则数：79
- 问题级别分布：严重: 65, 一般: 12, 信息: 2
- 语言分布：PYTHON: 79
- 规则类型分布：安全规范规则类: 29, 通用规范规则类: 20, 安全类（非编程规范）: 12, 代码坏味道类: 7, 通用规范建议类: 7, 通用类（非编程规范）: 3, 安全规范建议类: 1

## 代码审查使用方式
- 仅在审查触及对应语言、构建选项或安全面的变更时加载本规则集。
- 只有当变更差异 或必要的相邻代码中存在明确证据时，才输出规则违反项。
- 输出 合入请求审查问题时，需要包含来源规则名称和关联工具规则。
- `严重` 默认视为阻塞问题，除非已有明确例外说明；`一般` 默认合入前修复；`提示` 默认作为建议项，除非项目策略另有要求。

## 规则索引

| 序号 | 规则 | 级别 | 语言 | 工具规则 | 类型 |
|---|---|---|---|---|---|
| 1 | [E0202](#rule-1) | 严重 | PYTHON | E0202 | 通用类（非编程规范） |
| 2 | [E0601](#rule-2) | 严重 | PYTHON | E0601 | 通用类（非编程规范） |
| 3 | [E1206](#rule-3) | 严重 | PYTHON | E1206 | 通用类（非编程规范） |
| 4 | [G.CLS.01 如果子类和父类中都有__init__方法，则子类中的__init__方法必须正确调用其父类__init__方法](#rule-4) | 严重 | PYTHON | H3601 | 通用规范规则类 |
| 5 | [G.CLS.09 重写类的魔法函数时必须返回其原型指定的类型](#rule-5) | 严重 | PYTHON | E0308,E0305,E0304,E0309,E0301,E0306,E0307,E0312,E0310,E0313,E0311,E0303 | 通用规范规则类 |
| 6 | [G.CLS.10 未实现的数值运算类型魔法函数必须返回NotImplemented而不是抛出NotImplementedError](#rule-6) | 严重 | PYTHON | H3710 | 通用规范规则类 |
| 7 | [G.CNP.01 建议为communicate传入超时参数来防止子进程死锁或失去响应](#rule-7) | 严重 | PYTHON | BD_communicate_need_set_timeout | 安全规范建议类 |
| 8 | [G.CTL.02 所有的代码都必须是逻辑可达的](#rule-8) | 严重 | PYTHON | H0302 | 通用规范规则类 |
| 9 | [G.DSP.01 将敏感对象发送出信任区域前进行签名并加密](#rule-9) | 严重 | PYTHON | BD_store_sensitive_data | 安全类（非编程规范） |
| 10 | [G.DSP.02 安全场景下必须使用密码学意义上的安全随机数](#rule-10) | 严重 | PYTHON | BD_insecure_random_func | 安全规范规则类 |
| 11 | [G.DSP.03 必须使用ssl.SSLSocket代替socket.Socket来进行安全数据交互](#rule-11) | 严重 | PYTHON | BD_forbid_use_socket_in_insecure_channel | 安全规范规则类 |
| 12 | [G.DSP.04 禁止在用户界面、日志中暴露不必要信息](#rule-12) | 严重 | PYTHON | BD_forbid_leak_sensitive_data_in_log | 安全规范规则类 |
| 13 | [G.EDV.01 禁止使用eval和exec函数执行不可信代码](#rule-13) | 严重 | PYTHON | SecPy_VF_Code_Injection | 安全规范规则类 |
| 14 | [G.EDV.02 禁止使用OS命令解析器或“危险函数”调用系统命令](#rule-14) | 严重 | PYTHON | SecPy_VF_Command_Injection | 安全规范规则类 |
| 15 | [G.EDV.03 避免在命令解析器中使用通配符](#rule-15) | 严重 | PYTHON | BD_linux_commands_wildcard_injection | 安全规范规则类 |
| 16 | [G.EDV.04 禁止使用subprocess模块中的shell=True选项](#rule-16) | 严重 | PYTHON | BD_subprocess_with_shell_equals_true | 安全规范规则类 |
| 17 | [G.EDV.06 禁止直接使用外部数据来拼接SQL语句](#rule-17) | 严重 | PYTHON | SecPy_VF_SQL_Injection | 安全类（非编程规范） |
| 18 | [G.EDV.07 不受信任的外部数据禁止使用.format()进行格式化](#rule-18) | 严重 | PYTHON | SecPy_VF_Format_Information_Leak | 安全类（非编程规范） |
| 19 | [G.EDV.08 防止正则表达式引起的ReDos攻击](#rule-19) | 严重 | PYTHON | BD_insecure_regex | 安全规范规则类 |
| 20 | [G.EDV.09 禁止直接使用外部数据来拼接XML](#rule-20) | 严重 | PYTHON | SecPy_VF_XML_Injection | 安全类（非编程规范） |
| 21 | [G.EDV.10 禁止在处理XML数据时解析不可信的实体](#rule-21) | 严重 | PYTHON | BD_xmlparser_without_resolve_entities_equals_false | 安全类（非编程规范） |
| 22 | [G.ERR.03 异常的类型定义必须继承自Exception](#rule-22) | 严重 | PYTHON | H3703 | 通用规范规则类 |
| 23 | [G.ERR.05 使用有明确业务属性的异常类型](#rule-23) | 严重 | PYTHON | W0702 | 通用规范规则类 |
| 24 | [G.ERR.06 raise语句必须包含异常实例](#rule-24) | 一般 | PYTHON | W0708 | 通用规范规则类 |
| 25 | [G.ERR.07 避免抑制或忽略异常](#rule-25) | 严重 | PYTHON | BD_try_except_ignore | 安全类（非编程规范） |
| 26 | [G.ERR.08 禁止通过异常泄露敏感数据](#rule-26) | 严重 | PYTHON | BD_exception_info_leak | 安全规范规则类 |
| 27 | [G.ERR.09 在单个except代码块内，禁止重复捕获同类异常](#rule-27) | 严重 | PYTHON | H0718 | 通用规范规则类 |
| 28 | [G.ERR.10 同时使用多个except语句时，要注意异常捕获的顺序](#rule-28) | 一般 | PYTHON | H3713 | 通用规范建议类 |
| 29 | [G.ERR.13 捕获异常后避免直接重新抛出](#rule-29) | 一般 | PYTHON | W0706 | 通用规范建议类 |
| 30 | [G.ERR.14 不要使用return、break、continue或抛出异常使finally块非正常结束](#rule-30) | 严重 | PYTHON | W0150 | 通用规范规则类 |
| 31 | [G.FIO.01 在多用户系统中创建文件时应根据需要指定合适的权限](#rule-31) | 严重 | PYTHON | BD_create_file_with_bad_permission | 安全规范规则类 |
| 32 | [G.FIO.02 使用外部数据构造的文件路径前必须进行校验，校验前必须对文件路径进行规范化处理](#rule-32) | 严重 | PYTHON | SecPy_VF_Path_Manipulation | 安全规范规则类 |
| 33 | [G.FIO.03 禁止使用tempfile.mktemp创建临时文件](#rule-33) | 严重 | PYTHON | BD_forbid_use_mktemp | 安全规范规则类 |
| 34 | [G.FIO.04 临时文件使用完毕应及时删除](#rule-34) | 严重 | PYTHON | BD_delete_temporary_file | 安全规范规则类 |
| 35 | [G.FIO.06 解压文件必须进行安全检查](#rule-35) | 严重 | PYTHON | BD_unzip_file_without_check_size | 安全类（非编程规范） |
| 36 | [G.FMT.05 导入部分(imports)应该置于模块注释和文档字符串之后，模块全局变量和常量声明之前](#rule-36) | 一般 | PYTHON | W0410,H2305 | 通用规范建议类 |
| 37 | [G.FNM.01 禁止使用可变对象作为参数默认值](#rule-37) | 严重 | PYTHON | W0102 | 通用规范规则类 |
| 38 | [G.FNM.04 不要将没有返回值的函数调用结果赋值给变量](#rule-38) | 严重 | PYTHON | E1111 | 通用规范规则类 |
| 39 | [G.FNM.06 使用return代替StopIteration来结束生成器](#rule-39) | 严重 | PYTHON | BD_use_return_replace_stopiter | 安全规范规则类 |
| 40 | [G.IMP.02 使用 from ... import ... 语句的注意事项](#rule-40) | 一般 | PYTHON | H303,F403 | 通用规范建议类 |
| 41 | [G.IMP.03 避免使用__import__函数](#rule-41) | 一般 | PYTHON | W1802 | 通用规范建议类 |
| 42 | [G.LOG.01 logging模块应尽量使用懒插值的能力记录debug等低级别日志](#rule-42) | 严重 | PYTHON | BD_not_recommended_log_usage | 安全类（非编程规范） |
| 43 | [G.LOG.03 禁止直接使用外部数据记录日志](#rule-43) | 严重 | PYTHON | SecPy_VF_Log_Forging,SecPy_VF_Log_Forging_Debug | 安全规范规则类 |
| 44 | [G.OPR.01 对除法运算和模运算中的除数为0的情况做相应保护](#rule-44) | 严重 | PYTHON | BD_divide_by_zero | 安全规范规则类 |
| 45 | [G.OPR.02 与None作比较要使用is或is not，不要使用等号](#rule-45) | 严重 | PYTHON | H3212 | 通用规范规则类 |
| 46 | [G.OPR.03 禁止使用is或is not运算符在内置类型之间作比较](#rule-46) | 严重 | PYTHON | F632 | 通用规范规则类 |
| 47 | [G.PRJ.03 产品代码不要包含任何调试入口点](#rule-47) | 严重 | PYTHON | BD_forbid_use_dbg | 安全规范规则类 |
| 48 | [G.PRJ.04 建议同一项目内的编码方式保持一致，推荐使用UTF-8](#rule-48) | 一般 | PYTHON | W1801 | 通用规范建议类 |
| 49 | [G.PRJ.05 不用的代码段直接删除，不要注释掉](#rule-49) | 严重 | PYTHON | H3115 | 通用规范规则类 |
| 50 | [G.PRJ.06 正式发布的代码及注释内容不应包含开发者个人信息](#rule-50) | 严重 | PYTHON | Personal_Info_Check | 安全类（非编程规范） |
| 51 | [G.PRJ.07 禁止代码中包含公网地址](#rule-51) | 严重 | PYTHON | Public_Url_Check | 安全规范规则类 |
| 52 | [G.PSL.01 避免使用已经被标记为弃用并有明确替代的方法](#rule-52) | 严重 | PYTHON | H3111,H3112,W4902 | 通用规范建议类 |
| 53 | [G.SER.01 禁止使用pickle.load、_pickle.load和shelve模块加载外部数据](#rule-53) | 严重 | PYTHON | SecPy_VF_Deserialization_Injection | 安全规范规则类 |
| 54 | [G.SER.02 禁止序列化未加密的敏感数据](#rule-54) | 严重 | PYTHON | BD_forbid_serialize_sensitive_data | 安全规范规则类 |
| 55 | [G.SER.03 禁止使用yaml模块的load函数](#rule-55) | 严重 | PYTHON | BD_yaml_load | 安全规范规则类 |
| 56 | [G.SER.04 禁止使用jsonpickle模块的encode/decode或dumps/loads函数](#rule-56) | 严重 | PYTHON | BD_forbid_use_jsonpickle_encode | 安全规范规则类 |
| 57 | [G.TES.01 禁止在生产版本的业务代码中使用assert](#rule-57) | 严重 | PYTHON | BD_assert_used | 安全规范规则类 |
| 58 | [G.TYP.02 浮点型数据判断相等不要直接使用==](#rule-58) | 严重 | PYTHON | H0135 | 通用规范规则类 |
| 59 | [G.TYP.06 同一个字典表达式中各个键值不要相同](#rule-59) | 严重 | PYTHON | W0109 | 通用规范规则类 |
| 60 | [G.TYP.08 必须使用isinstance判断变量类型](#rule-60) | 严重 | PYTHON | H3148 | 通用规范规则类 |
| 61 | [G.VAR.01 禁止在变量的生命周期内修改其类型](#rule-61) | 严重 | PYTHON | W0643 | 通用规范规则类 |
| 62 | [G.VAR.02 禁止使用global关键字声明不存在的外部变量](#rule-62) | 严重 | PYTHON | W0601 | 通用规范规则类 |
| 63 | [G.VAR.03 禁止覆盖外部作用域中的标识符](#rule-63) | 严重 | PYTHON | H2104 | 通用规范规则类 |
| 64 | [SecH_GTS_PYTHON_CHECK_file_size](#rule-64) | 严重 | PYTHON | SecH_GTS_PYTHON_CHECK_file_size | 安全类（非编程规范） |
| 65 | [SecH_GTS_PYTHON_HardCode_Tel_No](#rule-65) | 严重 | PYTHON | SecH_GTS_PYTHON_HardCode_Tel_No | 安全类（非编程规范） |
| 66 | [SecH_GTS_PYTHON_check_SSLv23](#rule-66) | 严重 | PYTHON | SecH_GTS_PYTHON_check_SSLv23 | 安全类（非编程规范） |
| 67 | [SecPy_VF_Mail_Command_Injection](#rule-67) | 严重 | PYTHON | SecPy_VF_Mail_Command_Injection | 安全规范规则类 |
| 68 | [SecPy_VF_Memcached_Injection](#rule-68) | 严重 | PYTHON | SecPy_VF_Memcached_Injection | 安全规范规则类 |
| 69 | [SecPy_VF_Open_Redirect](#rule-69) | 严重 | PYTHON | SecPy_VF_Open_Redirect | 安全规范规则类 |
| 70 | [SecPy_VF_Server_Side_Request_Forgery](#rule-70) | 严重 | PYTHON | SecPy_VF_Server_Side_Request_Forgery | 安全规范规则类 |
| 71 | [SecPy_VF_Server_Side_Template_Injection](#rule-71) | 严重 | PYTHON | SecPy_VF_Server_Side_Template_Injection | 安全规范规则类 |
| 72 | [SecPy_VF_XSLT_Injection](#rule-72) | 严重 | PYTHON | SecPy_VF_XSLT_Injection | 安全规范规则类 |
| 73 | [duplication_file[PYTHON]](#rule-73) | 一般 | PYTHON | duplication_file | 代码坏味道类 |
| 74 | [huge_cca_cyclomatic_complexity[PYTHON]](#rule-74) | 信息 | PYTHON | huge_cca_cyclomatic_complexity | 代码坏味道类 |
| 75 | [huge_folder[PYTHON]](#rule-75) | 一般 | PYTHON | huge_folder | 代码坏味道类 |
| 76 | [huge_method[PYTHON]](#rule-76) | 信息 | PYTHON | huge_method | 代码坏味道类 |
| 77 | [huge_non_headerfile[PYTHON]](#rule-77) | 一般 | PYTHON | huge_non_headerfile | 代码坏味道类 |
| 78 | [redundant_code[PYTHON]](#rule-78) | 一般 | PYTHON | redundant_code | 代码坏味道类 |
| 79 | [warning_suppression[PYTHON]](#rule-79) | 一般 | PYTHON | warning_suppression | 代码坏味道类 |

## 规则详情

<a id="rule-1"></a>

### 1. E0202

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | E0202 |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
*An attribute defined in %s line %s hides this method*
Used when a class defines a method which is hidden by an instance attribute
from an ancestor class or set by some client code.
```

**参考信息**

- 最新更新时间：2019-10-29 15:53:00

<a id="rule-2"></a>

### 2. E0601

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | E0601 |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

在赋值前使用变量

**修复建议**

在使用变量前先定义变量

**正确示例**

```text
```python
hello = "Hello World !"
print(hello)
```
```

**错误示例**

```text
```python
print(hello) # [used-before-assignment]
hello = "Hello World !"
```
```

**参考信息**

- 最新更新时间：2019-10-29 15:53:31

<a id="rule-3"></a>

### 3. E1206

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | E1206 |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
*Not enough arguments for logging format string*
Used when a logging format string is given too few arguments.
```

**参考信息**

- 最新更新时间：2019-10-29 15:53:31

<a id="rule-4"></a>

### 4. G.CLS.01 如果子类和父类中都有__init__方法，则子类中的__init__方法必须正确调用其父类__init__方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H3601 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
如果子类和父类都有__init__初始化方法，子类其实是重写了父类的__init__方法，如果不显式调用父类的__init__方法，父类的__init__方法就不会被执行，导致子类实例访问父类__init__方法中初始化的变量时出现问题。因此，如果子类和父类都有初始化方法，则子类必须在自己的初始化方法中调用父类的初始化方法。
```

**修复建议**

在子类中使用super.__init__方法来调用父类的初始化方法。

**正确示例**

```text
# 【正例1】
class Base:
def __init__(self):
self.base_attr = 1
class Derived(Base):
def __init__(self):
super().__init__()
self.derived_attr = 2
if __name__ == '__main__':
derived_obj = Derived()
print(derived_obj.base_attr) # 1
```

**错误示例**

```text
# 【反例1】单继承场景，不调用父类初始化方法，导致父类初始化过程遗漏。
class Base:
def __init__(self):
self.base_attr = 1
class Derived(Base):
def __init__(self):
self.derived_attr = 2
if __name__ == '__main__':
derived_obj = Derived()
print(derived_obj.base_attr) # AttributeError: 'Derived' object has no attribute 'base_attr'
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:35

<a id="rule-5"></a>

### 5. G.CLS.09 重写类的魔法函数时必须返回其原型指定的类型

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | E0308,E0305,E0304,E0309,E0301,E0306,E0307,E0312,E0310,E0313,E0311,E0303 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
类的魔法函数（__len__, __bool__等）可以依赖解释器隐式地辅助完成对象的基本功能。
由于和解释器的框架代码耦合，如果重写类的魔法函数时与其原型返回值不一致，将在很多基本功能中出错（真值判断，比较等）。
如果自行定义了类的魔法函数，那么这些魔法函数必须按照其原型要求返回指定类型的对象。
```

**修复建议**

重写类的魔法方法`__getnewargs_ex__`时返回tuple类型，并且元素是(tuple, dict)类型。

**正确示例**

```text
# 【正例1】
class Repostory:
def __init__(self):
self.objs_repo = list()
def __len__(self):
return len(self.objs_repo) // 10 # __len__应该返回一个非负整数，取整
def __bool__(self):
return True if self.objs_repo else False # __bool__应该返回一个bool类型，True or False
def __setitem__(self, key, value):
self.objs_repo.append((key, value))
def __getitem__(self, index):
print(index, len(self.objs_repo))
return None if (index < -len(self.objs_repo) or index >= len(self.objs_repo)) else self.objs_repo[index]
```

**错误示例**

```text
# 【反例1】
class Repostory:
def __init__(self):
self.objs_repo = list()
def __len__(self):
return len(self.objs_repo) / 10 # __len__应该返回一个非负整数，此处返回float
def __bool__(self):
return len(self.objs_repo) # __bool__返回要求bool类型，此处返回int
def __setitem__(self, key, value):
self.objs_repo.append((key, value))
def __getitem__(self, index):
print(index, len(self.objs_repo))
return None if index >= len(self.objs_repo) else self.objs_repo[index]
repo_obj = Repostory()
repo_obj[0] = "zero"
if len(repo_obj): # TypeError: 'float' object cannot be interpreted as an integer
print(repo_obj)
if repo_obj: # TypeError: __bool__ should return bool, returned int
print(repo_obj)
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:37

<a id="rule-6"></a>

### 6. G.CLS.10 未实现的数值运算类型魔法函数必须返回NotImplemented而不是抛出NotImplementedError

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H3710 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
当魔法函数返回NotImplemented时，解释器将尝试使用其它关联方法（或其他后备情况，具体取决于运算符）完成功能。因此，要求数值运算类型的魔法函数（如__eq__()，__lt__()，__add__()，__rsub__()等）在有定义却无具体实现时返回NotImplemented，而不是抛出NotImplementedError异常。
```

**修复建议**

要求数值运算类型的魔法函数（如__eq__()，__lt__()，__add__()，__rsub__()等）在有定义却无具体实现时返回NotImplemented，而不是抛出NotImplementedError异常

**正确示例**

```text
# 【正例1】在a == b的比较中解释器会先调用a.__eq__，发现a.__eq__方法返回NotImplemented后，会调用b.__eq__方法来完成功能。
class Entity:
def __init__(self, eid: int = 0):
self.eid = eid
class EntityA(Entity):
def __eq__(self, other):
return NotImplemented
class EntityB(Entity):
def __eq__(self, other):
return self.eid == other.eid
if __name__ == '__main__':
a = EntityA(eid=1)
b = EntityB(eid=2)
print(a == b) # False
```

**错误示例**

```text
# 【反例1】
class Entity:
def __init__(self, eid: int = 0):
self.eid = eid
class EntityA(Entity):
def __eq__(self, other):
raise NotImplementedError()
class EntityB(Entity):
def __eq__(self, other):
return self.eid == other.eid
if __name__ == '__main__':
a = EntityA(eid=1)
b = EntityB(eid=2)
print(a == b) # NotImplementedError
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:42

<a id="rule-7"></a>

### 7. G.CNP.01 建议为communicate传入超时参数来防止子进程死锁或失去响应

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_communicate_need_set_timeout |
| 规则类型 | 安全规范建议类 |
| 预估误报率 | 20% |

**审查要点**

在使用subprocess模块来管理子进程时，给communicate方法传入timeout参数，以避免子进程死锁或失去响应。

**正确示例**

```text
import subprocess
if __name__ == "__main__":
proc = subprocess.Popen("ping 192.168.23.45 -t")
try:
outs, errs = proc.communicate(timeout=5)
except subprocess.TimeoutExpired:
proc.kill()
```

**错误示例**

```text
import subprocess
if __name__ == "__main__":
proc = subprocess.Popen("ping 192.168.23.45 -t")
print(proc.communicate())
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 10:35:27

<a id="rule-8"></a>

### 8. G.CTL.02 所有的代码都必须是逻辑可达的

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H0302 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
跳转语句（return, break, continue 和 raise）将会使程序执行跳出当前代码块。在跳转语句同缩进之后的代码块将永远无法执行到，这种代码被称为”死代码”。
“死代码”虽然一时无法被执行到，但后续附近代码的修改有可能会触发到”死代码”的执行，存在潜在风险。且”死代码”本身就存在编码逻辑上的错误，需要删除。
```

**修复建议**

修改方法：要么清理无法执行到的代码，要么调整代码位置，把逻辑修改正确

**正确示例**

```text
# 【正例1】
def function_with_return_no_dead_code(a):
i = 10
return i + a
# 【正例2】
def function_with_raise_no_dead_code(a):
if a:
return a
else:
do_something_else() # 如果do_something_else确实需要执行，那么调整它的位置
raise Exception("invalid value")
```

**错误示例**

```text
# 【反例1】
def function_with_return_dead_code(a):
i = 10
return i + a
i += 1 # 这里的代码无法被执行到
# 【反例2】
def function_with_raise_dead_code(a):
if a:
return a
else:
raise Exception("invalid value")
do_something_else() # 这里的处理将永远无法被执行
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:30

<a id="rule-9"></a>

### 9. G.DSP.01 将敏感对象发送出信任区域前进行签名并加密

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_store_sensitive_data |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
敏感数据在发送出信任区域前，如果不对其进行签名并加密，可能会被窃取和恶意篡改，导致安全性问题。如以下场景：
1. 序列化或传输敏感数据
2. 没有使用类似于SSL传输通道
3. 敏感数据需要长久保存（比如在硬盘驱动器上）
因此，需要对对象加密和数字签名来保证数据安全，防止对象被非法篡改，保持其完整性。
```

**修复建议**

敏感信息跨信任域传递时要进行签名并加密，关于加密算法的规定请参考公司规范库发布的《密码算法应用规范》。

**正确示例**

```text
##### 场景1：
- 修复示例：加密，以SHA256算法为例
```python
import crypt
key = "********"
file_path = 'xxxx'
key = crypt.crypt(key, crypt.METHOD_SHA256) # 符合，加密，以SHA256算法为例
with open(file_path, 'w') as file:
file.write(key)
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：敏感信息跨信任域传递时未进行签名并加密
```python
key = "********"
file_path = 'xxxx'
with open(file_path, 'w') as file:
file.write(key) # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:28:00

<a id="rule-10"></a>

### 10. G.DSP.02 安全场景下必须使用密码学意义上的安全随机数

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_insecure_random_func |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

Python的random模块实现了基于各种分布的伪随机数生成器。产生的随机数可以是均匀分布、高斯分布、对数正态分布、负指数分布以及alpha、beta分布，但是这些随机数都是伪随机数，禁止应用于安全加密目的的应用中。出于安全加密目的的应用中禁止使用random模块生成的伪随机数，必须使用安全随机数。

**正确示例**

```text
【正例1】Python3.6以上版本的os.urandom方法以阻塞模式从系统指定的随机源获取随机字节，因此使用os.urandom方法生成随机数是安全的：
import os
import platform
# 长度请参见密码算法规范，不同场景要求长度不一样
rand_length = 16
_rand_lst = list(os.urandom(rand_length))
print(_rand_lst)
_rand_bytes = os.urandom(rand_length)
print(_rand_bytes)
注意： Python3.6以下版本中，os.urandom在linux系统环境中生成的随机数不安全，不符合我司标准。
【正例2】推荐使用secrets模块生成随机数：
import secrets
# 生成随机整数
number = secrets.randbelow(10)
print("Secure random number is ", number)
# Secure random number is 0
secrets_generator = secrets.SystemRandom()
random_number = secrets_generator.randint(0, 50)
print("Secure random number is ", random_number)
# Secure random number is 26
# 指定范围并设置步长
random_number = secrets_generator.randrange(5, 50, 5)
print("Secure random number within range is ", random_number)
# Secure random number within range is 15
# 从指定的数据集中选择
number_list = [6, 12, 18, 24, 30, 36, 42, 48, 54, 60]
secure_choice = secrets_generator.choice(number_list)
print("Secure random choice using secrets is ", secure_choice)
# Secure random choice using secrets is 30
secure_sample = secrets_generator.sample(number_list, 3)
print("Secure random sample using secrets is ", secure_sample)
# Secure random sample using secrets is [54, 6, 42]
# 随机安全浮点数
secure_float = secrets_generator.uniform(2.5, 25.5)
print("Secure random float number using secrets is ", secure_float)
# Secure random float number using secrets is 9.445927455984885
```

**错误示例**

```text
import random
sr = random.randint(0, 100)
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-11"></a>

### 11. G.DSP.03 必须使用ssl.SSLSocket代替socket.Socket来进行安全数据交互

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_forbid_use_socket_in_insecure_channel |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
Python提供的socket.Socket类可用于创建套接字，传输敏感数据时应使用ssl.SSLSocket类创建安全套接字，该类提供了SSL/TSL等安全传输协议来确保传输通道不受攻击者的监听或恶意篡改。
SSLSocket类提供的主要保护包括：
1、完整性保护：SSL防止消息被主动窃取者篡改。
2、认证：在大多数模式下，SSL都对对端进行认证。服务器通常都被认证，如果服务器要求，客户端也可以被认证。
3、保密性：在大多数模式下，SSL对客户端和服务器之间传输的数据进行加密。这样保护了数据的隐私性，被动窃听器不能监听诸如财务或者个人信息等敏感信息。
```

**正确示例**

```text
1、服务端：
import socket, ssl, time
# python 3.X begin
context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
context.load_cert_chain(certfile="zxcert.pem", keyfile="zxkey.pem")
# python 3.X end
bindsocket = socket.socket()
print("socket create success")
bindsocket.bind(('127.0.0.1', 10023))
print("socket bind success")
bindsocket.listen(5)
print("socket listen success")
def do_something(connstream, data):
print("data length:", len(data))
return True
def deal_with_client(connstream):
t_recv = 0
t_send = 0
n = 0
t1 = time.clock()
data = connstream.recv(1024)
t2 = time.clock()
print("receive time:", t2 - t1)
# empty data means the client is finished with us
while data:
if not do_something(connstream, data):
# we'll assume do_something returns False
# when we're finished with client
break
n = n + 1
t1 = time.clock()
connstream.send(b'b' * 1024)
t2 = time.clock()
t_send += t2 - t1
print("send time:", t2 - t1)
t1 = time.clock()
data = connstream.recv(1024)
t2 = time.clock()
t_recv += t2 - t1
print("receive time:", t2 - t1)
print("avg send time:", t_send / n, "avg receive time:", t_recv / n)
# finished with client
while True:
newsocket, fromaddr = bindsocket.accept()
print("socket accept one client")
# python 3.X begin
connstream = context.wrap_socket(newsocket, server_side=True)
# python 3.X end
try:
deal_with_client(connstream)
finally:
connstream.shutdown(socket.SHUT_RDWR)
connstream.close()
2、客户端：
import socket, ssl, pprint, time
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("socket create success")
# require a certificate from the server
ssl_sock = ssl.wrap_socket(s,
ca_certs="zxcert.pem",
cert_reqs=ssl.CERT_REQUIRED)
ssl_sock.connect(('127.0.0.1', 10023))
print("socket connect success")
pprint.pprint(ssl_sock.getpeercert())
# note that closing the SSLSocket will also close the underlying socket
n = 0
t_send = 0
t_recv = 0
while n < 10:
n = n + 1
t1 = time.clock()
ssl_sock.send(b'a' * 100)
t2 = time.clock()
t_send += t2 - t1
print("send time:", t2 - t1)
t1 = time.clock()
data = ssl_sock.recv(1024)
t2 = time.clock()
t_recv += t2 - t1
print("receive time:", t2 - t1)
print("avg send time:", t_send / n, "avg receive time:", t_recv / n)
ssl_sock.close()
# 该正确代码示例应用ssl库使用SSL/TLS安全协议保护传输的报文。使用ssl的程序如果尝试连接不使用SSL的端口，那么这个程序将失败。同理，一个不使用ssl的程序如果要同一个使用SSL的端口建立连接也将失败。
```

**错误示例**

```text
1、服务端：
# Echo server program
import socket
# Symbolic name meaning all available interfaces
HOST = ''
# Arbitrary non-privileged port
PORT = 50007
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(1)
conn, addr = s.accept()
print('Connected by', addr)
while True:
data = conn.recv(1024)
if not data:
break
conn.sendall(data)
conn.close()
2、客户端：
# Echo client program
import socket
# The remote host
HOST = ''
# The same port as used by the server
PORT = 50007
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST, PORT))
s.sendall(b'Hello, world')
data = s.recv(1024)
s.close()
print('Received', repr(data))
【例外】
因为SSLSocket提供的报文安全传输机制性，将造成巨大的性能开销。在以下情况下，普通的套接字就可以满足需求：
1、套接字上传输的数据不敏感。
2、数据虽然敏感，但是已经过恰当加密。
3、套接字的网络路径没有越出信任边界。这种情况只有在特定的情况下才能发生，例如，套接字的两端都在同一个局部网络，而且整个网络都是可信的情况时。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 10:35:45

<a id="rule-12"></a>

### 12. G.DSP.04 禁止在用户界面、日志中暴露不必要信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_forbid_leak_sensitive_data_in_log |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

在日志中禁止记录口令、密钥等敏感信息，包括这些敏感信息的加密密文，防止产生敏感信息泄露风险。若因为特殊原因必须要记录日志，应该使用固定长度的星号（*）代替这些敏感信息。

**正确示例**

```text
import logging
...
logging.info("Login success,user is:%s password is:********", user_name)
```

**错误示例**

```text
import logging
...
logging.info("Login success,user is:%s password is:%s", user_name, encrypt(password))
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-01-13 21:36:52

<a id="rule-13"></a>

### 13. G.EDV.01 禁止使用eval和exec函数执行不可信代码

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Code_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

```text
在使用eval和exec函数执行代码时，如果输入的代码来自不可信来源，例如用户输入的字符串或从网络上下载的代码，那么使用eval和exec函数执行代码就会存在安全问题。攻击者可以利用这些函数来执行恶意代码，如获取敏感信息、损坏系统或进行远程控制等。因此，为了保证程序的安全性，禁止在执行不可信代码时使用eval和exec函数。
```

**修复建议**

对传入eval、exec等函数的不可信参数进行校验。

**正确示例**

```text
##### 场景1：
- 修复示例：对外部数据进行校验
```python
def string_verify(input_str):
# 检查输入字符串的长度
if not input_str or len(input_str) > 20:
raise ValueError("输入字符串无效")
# 删除或转义输入字符串中的特殊字符
verified_str = input_str.replace("'", "").replace('"', "").replace("\\", "")
return verified_str
code = input("请输入代码：")
# 校验外部数据
verified_code = string_verify(code)
eval(verified_code) # 符合
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：直接使用外部数据执行eval函数
```python
code = input("请输入代码：")
# 直接使用外部数据执行eval函数
eval(code) # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:11:51

<a id="rule-14"></a>

### 14. G.EDV.02 禁止使用OS命令解析器或“危险函数”调用系统命令

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Command_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
使用未经校验的不可信输入作为系统命令的参数或命令的一部分，可能导致命令注入漏洞。
因此，执行命令的时候，必须注意以下几点：
1. 命令执行的字符串不要去拼接输入的参数，如果必须拼接时，要对输入参数进行白名单过滤。
2. 对传入的参数要做类型校验，例如：整数数据，可以对数据进行整数强制转换。
3. 对于要求限定格式化字符串类型的业务场景，要保证占位符的正确性。例如：int类型参数的拼接，使用`{:d}`，不能用`{:s}`。
```

**修复建议**

```text
1. 尽量使用标准的API替代运行系统命令来完成任务。
2. 如果无法避免使用os.system或os.popen等“危险函数”，则必须要对输入数据进行白名单校验。
```

**正确示例**

```text
##### 场景1：
- 修复示例1：使用系统函数
```python
import os
import sys
try:
# 使用os.listdir来列举目录下的内容
print(os.listdir(sys.argv[1])) # 符合
except Exception as ex:
print(ex)
```
- 修复示例2：对外部数据进行校验
```python
import os
import re
import sys
def args_verify(args_list):
# 校验数据长度
if len(args_list) > 20:
print('Parameter length error.')
sys.exit()
# 校验数据内容
if re.match(r"^[.0-9A-Za-z@]+$", args_list[1]):
return args_list[1]
try:
# 校验外部数据
verified_arg = args_verify(sys.argv)
if verified_arg:
print(os.system("ls " + verified_arg)) # 符合
except Exception as ex:
print('exception:', ex)
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：使用os.system直接执行外部命令
```python
import os
import sys
try:
# 使用os.system直接执行外部命令
print(os.system("ls " + sys.argv[1])) # 不符合
except Exception as ex:
print('exception:', ex)
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:08:06

<a id="rule-15"></a>

### 15. G.EDV.03 避免在命令解析器中使用通配符

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_linux_commands_wildcard_injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

通配符是一种特殊符号，可以匹配任意文件名，包括隐藏文件、系统文件和重要的配置文件等，Linux系统中许多命令接受通配符，如果攻击者将软链接或者一些特性文件置于给定的路径位置，则可能造成严重后果。因此，在日常开发中执行命令时应该尽量避免使用通配符。

**修复建议**

函数`chown`，`chmod`，`subprocess`等操作文件访问许可的函数或执行子进程的函数参数中不要包含"*"通配符。

**正确示例**

```text
##### 场景1：
- 修复示例：执行子进程的函数参数中不包含"*"通配符
```python
import subprocess
import os
file_path = 'test.txt'
subprocess.Popen(['/bin/chmod', '640', file_path], shell=False) # 符合
subprocess.Popen(['/bin/chown', 'user:user', file_path], shell=False) # 符合
os.chown(file_path, uid, gid, follow_symlinks=False) # 符合，禁止对软链接对应的目标文件操作
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：执行子进程的函数参数中包含"*"通配符
```python
import subprocess
import os
os.system('/bin/chmod 640 *') # 不符合
subprocess.Popen('/bin/chown user:user *', shell=True) # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-16"></a>

### 16. G.EDV.04 禁止使用subprocess模块中的shell=True选项

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_subprocess_with_shell_equals_true |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
subprocess模块帮助开发者执行外部命令和程序，为开发者带来便利的同时也带来了风险，该模块下多个接口（如call、check_call、Popen、check_output等）都可以传递shell参数。
shell=True这个参数的功能是Python解释器先运行一个shell环境，再用这个shell来执行Popen函数的第一个命令行字符串参数。
推荐调用子进程的方式是使用run()函数，对于其他需要使用到更高阶的用法的可以使用底层Popen()接口，需要注意的一点是使用Popen方式时，当stdout=PIPE或者stderr=PIPE时有可能会造成阻塞，除非调用Popen.communicate()才能解决。
```

**正确示例**

```text
# 安全的做法是将shell参数置为False，前面的参数转成list列表，这样传入的参数里即使有注入指令也不会被解析执行，代码如下
import subprocess
def listdir_right(directory_):
return subprocess.check_output(["notepad.exe", directory_], shell=False)
if __name__ == "__main__":
_out = listdir_right("d:/abc.txt && calc.exe")
# 此时在Windows系统上执行上面的代码，你会发现系统记事本程序会尝试打开文件名由“d:/abc.txt && calc.exe”字符串组成的文件。
# 但是，以上正确示例的代码成为无风险代码还有个前提：
# 切记函数第一个参数列表的第一个元素不允许是bash，cmd，cmd.exe，/bin/sh，/usr/bin/expect等，只有满足了这个前提条件，参数显示设置shell=False参数才是有效防止命令注入的。遇到无法规避的情况，请联系安全SE备案解决。
import subprocess
def show_file(file_name):
return subprocess.check_output(("cmd.exe", file_name), shell=False)
if __name__ == "__main__":
_out = show_file("/c d:/abc.txt & echo bad_code >> d:/def.txt")
# 上面的代码在windows下以cmd.exe为主命令，后面的file_name整体作为cmd.exe的参数传入，内容中同样包含了危险的命令注入。虽然使用了命令序列化和shell=False参数，却仍然无法阻止命令注入。
```

**错误示例**

```text
【反例1】
import subprocess
def run_cmd_wrong(_directory):
return subprocess.check_output("notepad.exe %s" % _directory, shell=True)
if __name__ == "__main__":
_out = run_cmd_wrong("d:/abc.txt && calc.exe")
# 此时执行脚本，你会发现脚本利用记事本打开了 d:/abc.txt 文件，接着也成功运行了计算器程序。
【反例2】演示第1个参数为什么不能是bash、cmd、cmd.exe、/bin/sh、 /usr/bin/expect 等系统命令，否则就相当于使用了shell=True参数。
expect_script = """
echo sssss22222 >> test_write_something.txt
echo sssss33333 >> test_write_something.txt
calc.exe
"""
# windows环境类似场景， 假设cmd.exe 是宿主程序，比如 /usr/bin/expect
# 传入交互的命令，如上面的多行写入
process = subprocess.run(["cmd.exe"], input=expect_script.encode("utf8"), shell=False, stdout=subprocess.PIPE)
# 宿主程序的标准输出
print(process.stdout)
执行上面的代码你会发现生成了文件“test_write_something.txt”，并且成功运行了计算器程序。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 10:35:38

<a id="rule-17"></a>

### 17. G.EDV.06 禁止直接使用外部数据来拼接SQL语句

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_SQL_Injection |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
如果直接使用外部数据来拼接SQL语句，攻击者可以通过在输入框中输入恶意的SQL语句来执行任意的SQL操作，从而导致信息泄露或者数据被篡改。
因此，为了保证数据库的安全性，禁止直接使用外部数据来拼接SQL语句。
```

**修复建议**

```text
SQL注入产生的根本原因是使用外部数据直接拼接SQL语句，防护措施主要有以下三类：
1. 使用参数化查询：最有效的防护手段。
2. 对外部数据进行白名单校验。
3. 对外部数据中的与SQL注入相关的特殊字符进行转义：适用于必须通过字符串拼接构造SQL语句的场景，转义仅对由引号限制的字段有效。
```

**正确示例**

```text
##### 场景1：
- 修复示例：对外部数据进行校验
对于字符型的字段进行处理时，要对输入做转义，将单引号替换为两个单引号。
```python
import sqlite3
import sys
def string_verify(input_str):
# 将单引号替换为两个单引号
verified_str = input_str.replace("'", "''")
return verified_str
def good():
conn = sqlite3.connect("e:/test_sqlite3.db")
con_cursor = conn.cursor()
# SOURCE：read data from system
username = sys.argv[0]
# 校验外部数据
verified_username = string_verify(username)
cmd = "select * from COMPANY where NAME = '{:s}'".format(verified_username)
# SINK: data concatenated into SQL statement used in execute(), which could result in SQL Injection
con_cursor.execute(cmd) # 符合
conn.commit()
conn.close()
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：直接使用外部数据拼接SQL语句
```python
import sqlite3
import sys
def bad():
conn = sqlite3.connect("e:/test_sqlite3.db")
con_cursor = conn.cursor()
# SOURCE：read data from system
username = sys.argv[0]
# 直接使用外部数据拼接SQL语句
cmd = "select * from COMPANY where NAME = '{:s}'".format(username)
# SINK: data concatenated into SQL statement used in execute(), which could result in SQL Injection
con_cursor.execute(cmd) # 不符合
conn.commit()
conn.close()
```
如果username获取到字符串为`A' or 1=1 -- `，最终拼接的SQL语句为：
```sql
select * from COMPANY where NAME = 'A' or 1=1 --'
```
该SQL语句被注入后，会导致所有的数据被返回到结果集中，使攻击者非法绕过。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:12:21

<a id="rule-18"></a>

### 18. G.EDV.07 不受信任的外部数据禁止使用.format()进行格式化

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Format_Information_Leak |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Python 2.6版本引入str.format字符串格式化方法，支持通过string类的format函数处理复杂变量替换以及值的格式化。在使用.format()进行格式化时，如果参数来自不可信任来源的外部数据，这些数据包含恶意代码，就会导致全安漏洞。因此，为了防止代码注入和其他安全问题，最好使用其他方法来格式化外部数据。若不可避免，则需通过白名单或黑名单方式对外部数据进行校验或净化。
```

**修复建议**

禁止使用.format()对不受信任的外部数据进行格式化，若不可避免，则需通过白名单或黑名单方式对外部数据进行校验或净化。

**正确示例**

```text
##### 场景1：
- 修复示例：格式化前校验外部数据
```python
import re
class User:
def __init__(self, name, password):
self.name = name
self.password = password
def string_verify(input_string):
# 数字或字母组成的6位输入（可根据具体场景定制合法输入正则表达式）
pattern_safety = '^[0-9a-zA-z]{6}$'
compile_pattern = re.compile(pattern_safety)
if compile_pattern.match(input_string):
return True
return False
user = User('admin', 'password@123')
user_input = input('Please input something:')
# 对外部数据进行校验
if string_verify(user_input):
data_format = user_input.format(u=user) # 符合
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：直接对外部数据进行格式化
```python
class User:
def __init__(self, name, password):
self.name = name
self.password = password
user = User('admin', 'password@123')
user_input = input('Please input something:')
# 直接对外部数据进行格式化
data_format = user_input.format(u=user) # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:07:39

<a id="rule-19"></a>

### 19. G.EDV.08 防止正则表达式引起的ReDos攻击

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_insecure_regex |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 625 |
| 预估误报率 | 20% |

**审查要点**

如果正则表达式中包含复杂的匹配规则，例如重复的模式、回溯等，那么在匹配较长的字符串时，可能会导致正则表达式引擎的回溯次数增加，从而导致redos攻击。

**修复建议**

应检视程序内的正则表达式，避免使用存在风险的表达式结构。尽量不要使用过于复杂的正则，尽量少用分组，应将多余的分组删除。

**正确示例**

```text
##### 场景1：
- 修复示例：安全的正则表达式
```python
import re
# 符合：安全的正则表达式
pattern1 = re.compile(r'^(\d+)$')
pattern2 = re.compile(r'^(\d*)$')
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：可能引起redos攻击的正则表达式
```python
import re
# 不符合：具有自我重复的重复性分组的正则表达式
pattern1 = re.compile(r'^(\d+)+$')
pattern2 = re.compile(r'^(\d*)*$')
pattern3 = re.compile(r'^(\d+)*$')
pattern4 = re.compile(r'^(\d+|\s+)*$')
# 不符合：包含替换的重复性分组的正则表达式
pattern5 = re.compile(r'^(\d|\d|\d)+$')
pattern6 = re.compile(r'^(\d|\d?)+$')
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:23:55

<a id="rule-20"></a>

### 20. G.EDV.09 禁止直接使用外部数据来拼接XML

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_XML_Injection |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 30% |

**审查要点**

```text
Python直接使用外部数据来拼接XML，可能存在安全风险，如果外部数据包含恶意代码或者SQL注入等，可能导致程序运行异常或者数据泄露等问题。例如，如果用户的输入中包含特殊字符（例如“<”、“>”、“&”、“’”、“"”等），攻击者可能会通过构造特定的输入来影响XML的结构和内容，从而引发攻击。一个典型的攻击场景是，攻击者可以通过控制XML文档修改电子商务中的认证凭据，或修改商品的价格，对商家造成经济损失。因此，禁止直接使用外部数据拼接XML，若无法避免，建议在拼接XML时采取一定的安全措施。
```

**修复建议**

禁止直接使用外部数据拼接XML，若无法避免，则需对外部数据进行严格校验和净化；此外，建议使用defusedxml模块处理XML数据，详情可参考defusedxml官方文档。

**正确示例**

```text
##### 场景1：
- 修复示例1：白名单方式校验示例一（使用正则表达式）
```python
import xml.etree.ElementTree as ET
import re
def my_clean(input_str):
# 白名单校验：使用正则表达式
compile_pattern = re.compile('^\d+$')
if compile_pattern.match(input_str):
return input_str
return '0'
root = ET.Element("root")
child = ET.Element("child")
amount = input()
# 校验外部数据
verified_amount = my_clean(amount)
child.text = '<order>' \
'<item>laptop</item>' \
'<price>2800.00</price>' \
'<amount>' + verified_amount + '</amount>' \
'</order>'
root.append(child) # 符合
```
- 修复示例2：白名单方式校验示例二（使用字符串方法isdigit()判断字符串中是否只包含数字字符）
```python
import xml.etree.ElementTree as ET
def my_clean(input_str):
# 白名单校验：判断字符串中是否只包含数字字符
if input_str.isdigit():
return input_str
return '0'
root = ET.Element("root")
child = ET.Element("child")
amount = input()
# 校验外部数据
verified_amount = my_clean(amount)
child.text = '<order>' \
'<item>laptop</item>' \
'<price>2800.00</price>' \
'<amount>' + verified_amount + '</amount>' \
'</order>'
root.append(child) # 符合
```
- 修复示例3：黑名单方式校验
```python
import xml.etree.ElementTree as ET
import re
def my_clean(input_str):
# 黑名单校验：使用正则表达式
compile_pattern = re.compile('[<>"\'＆]+')
if compile_pattern.search(input_str):
return '0'
return input_str
root = ET.Element("root")
child = ET.Element("child")
amount = input()
# 校验外部数据
verified_amount = my_clean(amount)
child.text = '<order>' \
'<item>laptop</item>' \
'<price>2800.00</price>' \
'<amount>' + verified_amount + '</amount>' \
'</order>'
root.append(child) # 符合
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：直接使用外部数据拼接XML
以下XML文档定义了电子商城中笔记本电脑的价格，同时接受外部数据修改笔记本电脑的数量
```python
import xml.etree.ElementTree as ET
root = ET.Element("root")
child = ET.Element("child")
amount = input()
# 直接使用外部数据拼接XML
child.text = '<order>' \
'<item>laptop</item>' \
'<price>2800.00</price>' \
'<amount>' + amount + '</amount>' \
'</order>'
root.append(child) # 不符合
```
若输入为：
```python
1000</amount><item>laptop</item><price>28.00</price><amount>10000
```
则实际生成的XML文档为：
```python
<order>
<item>laptop</item>
<price>2800.00</price>
<amount>1000</amount>
<item>laptop</item>
<price>28.00</price>
<amount>10000</amount>
</order>
```
使用sax解析器解析上述XML文档时，第二个价格标签将覆盖第一个价格标签，第二个数量标签也将覆盖第一个数量标签，则最终仅花费28元就可购买到价值2800元的笔记本电脑。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:09:30

<a id="rule-21"></a>

### 21. G.EDV.10 禁止在处理XML数据时解析不可信的实体

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_xmlparser_without_resolve_entities_equals_false |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
XML实体包括内部实体和外部实体：
1. 外部实体格式： `<!ENTITY 实体名 SYSTEM "URI"\>` 或者 `<!ENTITY 实体名 PUBLIC "public_ID" "URI"\>`。XXE（XML External Entity）漏洞全称为XML外部实体漏洞，XML文档中可能包含带有URI的外部实体，解析该文档时可通过URI访问其指向的内容，若XML文档中的URI可控，则攻击者可以修改URI指向特定的恶意文件，执行拒绝服务攻击，或未经授权访问系统文件。
2. XML内部实体格式： `<!ENTITY 实体名 "实体的值"\>` 。XEE（XML Entity Expansion）漏洞全称为XML实体扩展漏洞，又称十亿狂笑（Billion Laughs）或XML炸弹（XML Bomb），若在处理XML数据时允许使用DTD功能定义XML文档结构，且构造该文档结构的数据可控，则攻击者可在XML文档中构造存在大量嵌套或递归结构的实体，导致解析该文档时数据爆炸性增长，从而造成拒绝服务。
```

**修复建议**

```text
1. 在处理XML数据时关闭外部实体解析开关(例如lxml模块将resolve_entities参数设置为False)。
2. 建议使用defusedxml模块处理XML数据，详情可参考defusedxml官方文档(https://pypi.org/project/defusedxml)。
```

**正确示例**

```text
##### 场景1：
- 修复示例：设置resolve_entities=False
```python
from lxml import etree
xml_str = ''
with open('xml_file.xml', 'r') as fp:
xml_str = fp.read()
parserSafe = etree.XMLParser(resolve_entities=False) # 符合：将resolve_entities参数设置为False
root = etree.XML(xml_str.encode('utf-8'), parser=parserSafe)
_config = list()
for GuestOSType_node in root.getchildren():
one_guestos = {}
for item in GuestOSType_node.items():
one_guestos["@" + item[0]] = item[1]
for node in GuestOSType_node.getchildren():
one_guestos[node.tag] = node.text
_config.append(one_guestos)
print(_config)
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：存在XML外部实体漏洞
假设存在用户可配置的XML文件（xml_file.xml）如下：
```xml
<OSCONFIG version="v001">
<GuestOSType id="1" name="CentOS 7.4" OSType="linux" OSbit="64">
<MaxCPU>96</MaxCPU>
<MaxMemory>128</MaxMemory>
</GuestOSType>
</OSCONFIG>
```
解析该XML文件代码如下：
```python
from lxml import etree
xml_str = ''
with open('xml_file.xml', 'r') as fp:
xml_str = fp.read()
root = etree.XML(xml_str.encode('utf-8')) # 不符合：存在XML外部实体漏洞
_config = list()
for GuestOSType_node in root.getchildren():
one_guestos = {}
for item in GuestOSType_node.items():
one_guestos["@" + item[0]] = item[1]
for node in GuestOSType_node.getchildren():
one_guestos[node.tag] = node.text
_config.append(one_guestos)
print(_config)
```
若攻击者将XML文件（xml_file.xml）内容改为：
```xml
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE title [
<!ELEMENT title ANY>
<!ENTITY xxe SYSTEM "file:///d:/abc.txt">
]>
<OSCONFIG version="v001">
<GuestOSType id="1" name="CentOS 7.4" OSType="linux" OSbit="64">
<MaxCPU>96</MaxCPU>
<MaxMemory>&xxe;</MaxMemory>
</GuestOSType>
</OSCONFIG>
```
则输入为：
```
[{'@id': '1', '@name': 'CentOS 7.4', '@OSType': 'linux', '@OSbit': '64','MaxCPU': '96', 'MaxMemory': 'aaaaa\nbbbbb'}]
```
“aaaaa\nbbbbb”为D盘下abc.txt文件中的内容，此时系统文件已被未授权访问。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-22"></a>

### 22. G.ERR.03 异常的类型定义必须继承自Exception

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H3703 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
BaseException是所有异常的基类，Exception是所有不需要进程退出的异常基类，具体的继承关系如下：
- BaseException
|- KeyboardInterrupt
|- SystemExit
|- GeneratorExit
|- Exception
|- (all other current built-in exceptions)
自定义的异常类型必须继承自Exception，按照已知异常->Exception的顺序捕获时，不需要关注对键盘中断、进程退出、生成器结束等异常的特殊处理。
禁止使用非异常类型作为raise对象，如NotImplemented、可迭代对象等。
```

**修复建议**

异常类必须继承自Exception，不能是BaseException

**正确示例**

```text
# 【正例1】
class AuthenticationError(Exception):
def __init__(self, message=None):
super(AuthenticationError, self).__init__(message)
self.http_code = 404
def handle_request(req):
try:
...
except AuthenticationError as e:
return Response(e.http_code, e.message)
except Exception as e:
return Response(400, e.message)
# 【例外】如果明确某些自定义异常不需要被except Exception捕获，可以继承自BaseException。
class NoSpaceOnDiskError(BaseException): # 如果需要磁盘空间不足时直接退出进程，可以继承自BaseException
...
def main():
try:
...
except Exception as e:
do_something(e)
```

**错误示例**

```text
# 【反例1】
class AuthenticationError(Exception):
def __init__(self, message=None):
super(AuthenticationError, self).__init__(message)
self.http_code = 404
def handle_request(req):
try:
...
except AuthenticationError as e:
return Response(e.http_code, e.message)
except Exception as e:
return Response(400, e.message)
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:43

<a id="rule-23"></a>

### 23. G.ERR.05 使用有明确业务属性的异常类型

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W0702 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
在使用try-except对代码做保护时，要明确异常类型所包含的业务属性。
1、只捕获必须在本try-except代码块内处理的异常，对于其他预料外的异常，交给上层函数捕获，或者透传到外部以暴露问题。
2、禁止直接使用except:语句，如果要捕获所有类型的异常，必须使用except BaseException:进行显式说明。
如果直接使用except:语句，实际捕获的异常是BaseException，常常与设计预期不一致，且该问题容易被代码维护人员忽视。
3、禁止在多个except语句的第一顺位使用except Exception:或except BaseException:。
BaseException是所有异常的基类，Exception是大多数异常的基类，异常捕获只处理与本代码块有逻辑关联的异常，捕获宽范围的异常会感知到更多的信息，不符合最小知识原则。
4、在主动抛出异常时，要明确异常类型，不要直接抛出Exception或BaseException。
```

**修复建议**

尽量使用具体的异常类型,少使用范围很大的异常类型

**正确示例**

```text
# 【正例1】
try:
with open(file_name) as fd:
...
except FileNotFoundError as e:
...
except PermissionError as e:
...
except Exception as e: # 将Exception放在最后捕获，用于处理未知异常
…
# 【例外】在顶层调用函数中，在确信已知异常均已在下层函数中得到处理的情况下，可以直接捕获Exception。
def fun1():
try:
...
except UserDefined.RecoverableException as e:
...
def fun2():
try:
...
except UserDefined.RecoverableException as e:
...
def main():
try:
fun1()
fun2()
except Exception as e:
...
```

**错误示例**

```text
# 【反例1】
try:
...
exit(1)
except: # 会将exit(1)抛出的SystemExit异常捕获
…
# 【反例2】
try:
...
except Exception: # 捕获的异常范围太大
…
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-24"></a>

### 24. G.ERR.06 raise语句必须包含异常实例

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W0708 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

使用raise语句时，Python会查找被except语句捕获的最后一个异常，如果不存在对应的异常（即没有except语句），抛出的异常类型为RuntimeError，丢失了原始错误调用栈信息。

**修复建议**

重新抛出异常

**正确示例**

```text
# 【正例1】
try:
...
except UserDefined.UnrecoverableException as e:
do_something(e)
raise # 重新抛出异常UserDefined.UnrecoverableException
# 【正例2】
try:
...
except UserDefined.UnrecoverableException as e:
do_something(e)
raise e # 重新抛出异常UserDefined.UnrecoverableException
```

**错误示例**

```text
# 【反例1】
raise # 会抛出RuntimeError
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-11-16 16:09:54

<a id="rule-25"></a>

### 25. G.ERR.07 避免抑制或忽略异常

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_try_except_ignore |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
编码时应尽量避免捕获一个异常但什么都不做的情况，抑制或忽略异常很可能丢掉关键的错误和应对，对程序的稳定运行产生风险。
如果业务场景需要忽略某些异常，并且这些异常不需要做日志记录，此时应增加注释说明忽略的理由。
```

**修复建议**

捕获异常，明确对这些异常的处理策略，包括但不限于重置状态、填充默认值、重试、返回错误等。

**正确示例**

```text
##### 场景1：
- 修复示例：捕获异常，并对异常做出相应的处理
```python
try:
i = input('Please enter an integer: ')
check_integer(i)
except Exception as e:
log_something(e)
do_something(e) # 符合：对异常做出相应的处理
```
```

**错误示例**

```text
##### 场景1：
- 错误示例1：捕获异常后pass
```python
try:
i = input('Please enter an integer: ')
check_integer(i)
except Exception as e:
pass # 不符合：这里看似合理，实际上在捕获异常后需要给用户足够的错误信息
```
- 错误示例2：捕获异常后continue
```python
try:
i = input('Please enter an integer: ')
check_integer(i)
except Exception as e:
continue # 不符合：这里看似合理，实际上在捕获异常后需要给用户足够的错误信息
```
- 错误示例3：捕获异常后return
```python
try:
i = input('Please enter an integer: ')
check_integer(i)
except Exception as e:
return # 不符合：这里看似合理，实际上在捕获异常后需要给用户足够的错误信息
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:18:18

<a id="rule-26"></a>

### 26. G.ERR.08 禁止通过异常泄露敏感数据

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_exception_info_leak |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
如果在传递异常的时候未对其中的敏感信息进行过滤，会导致信息泄露，可能帮助攻击者尝试发起进一步的攻击。攻击者可以通过构造恶意的输入参数来发掘应用的内部结构和机制。异常中的错误信息，以及异常本身的类型都可能泄露敏感数据。因此，当异常会被传递到信任边界以外时，必须同时对敏感的异常信息和敏感的异常类型进行过滤。
需要注意，在某些场景下，不进行异常处理，反而会泄漏敏感数据，必须慎重审视跨信任边界的交互。敏感数据的具体范围取决于产品的应用场景，产品应根据风险进行分析和判断。典型的敏感数据包括认证凭据、个人数据和通信内容等。
```

**修复建议**

当异常会被传递到信任边界以外时，禁止包含敏感信息。

**正确示例**

```text
##### 场景1：
- 修复示例：异常中没有敏感数据
```python
try:
password = 'password@123'
print(int(password))
# 异常中没有密码信息
except ValueError:
print('Invalid value') # 符合
except Exception:
print('Invalid value') # 符合
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：通过异常泄露敏感数据
```python
try:
password = 'password@123'
print(int(password))
# 在异常中泄露了密码信息
except Exception as e:
print(e) # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:18:29

<a id="rule-27"></a>

### 27. G.ERR.09 在单个except代码块内，禁止重复捕获同类异常

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H0718 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
在异常捕获语句中，重复捕获同类异常不会直接产生运行问题，但这不是好的编码习惯。
在较长的软件生命周期中，会带来可读性和可维护性问题。
具体要求：
1.禁止同时捕获父类异常和子类异常
2.禁止重复捕获同一个异常
```

**修复建议**

```text
禁止同时捕获父类异常和子类异常
禁止重复捕获同一个异常
```

**正确示例**

```text
# 【正例1】
try:
with open(file_name) as fd:
...
except (FileNotFoundError, PermissionError):
…
```

**错误示例**

```text
# 【反例1】
try:
with open(file_name) as fd:
...
except (FileNotFoundError, OSError): # FileNotFoundError继承自OSError，逻辑冗余
…
# 【反例2】
try:
with open(file_name) as fd:
...
except (OSError, OSError): # 重复捕获同一个异常
…
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-28"></a>

### 28. G.ERR.10 同时使用多个except语句时，要注意异常捕获的顺序

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H3713 |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
对于同一个try语句，支持配对多个except语句，按照except语句的次序捕获对应的异常。
捕获异常A的语句应该放在其父类之前。如果先捕获异常A的父类，因为对应的异常已经被处理，则捕获异常A的语句不会被执行，即产生了永远不会被执行的代码块。
```

**修复建议**

捕获异常A的语句应该放在其父类之前

**正确示例**

```text
# 【正例1】
try:
with open(file_name) as fd:
...
except (FileNotFoundError, PermissionError) as e:
do_something(e)
except Exception as e:
do_something€
```

**错误示例**

```text
# 【反例1】
try:
with open(file_name) as fd:
...
except OSError as e:
do_something(e)
except FileNotFoundError as e: # FileNotFoundError继承自OSError，该代码块永远不会被执行
do_something€
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:46

<a id="rule-29"></a>

### 29. G.ERR.13 捕获异常后避免直接重新抛出

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W0706 |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

except语句在捕获异常后，避免直接重新抛出，捕获并重新抛出异常与不写except语句没区别，这些无效的逻辑影响了代码的可读性。

**修复建议**

捕获异常后抛出新异常或者调用函数进行异常处理

**正确示例**

```text
# 【正例1】同时使用多个except语句时，如果需要对某个异常子类做特殊处理，但要加必要的注释。
try:
1 / 0
except ZeroDivisionError:
# 除0异常需要外抛，由调用者捕获并处理
raise
except Exception as e:
do_something€
```

**错误示例**

```text
# 【反例1】
try:
do_something()
except SomeException:
raise
except Exception as e:
do_something€
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-30"></a>

### 30. G.ERR.14 不要使用return、break、continue或抛出异常使finally块非正常结束

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W0150 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
try/finally的finally代码块中如果包含return, break, continue, 那么在try代码块运行过程中出现的异常将不会被抛出。
这种不抛出异常的行为可能会掩盖try代码块中的代码问题，所以禁止在finally代码块中出现return, break和continue语句。
```

**修复建议**

finnaly语句应该做try-except语句正常结束的代码总处理

**正确示例**

```text
### 场景1：finally块
- 修复示例：finally块使用break语句
```python
while True:
try:
pass
finally:
break
```
```

**错误示例**

```text
### 场景1：finally块
- 错误示例：finally块中continue语句
```python
while True:
try:
pass
finally:
continue
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 16:00:24

<a id="rule-31"></a>

### 31. G.FIO.01 在多用户系统中创建文件时应根据需要指定合适的权限

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_create_file_with_bad_permission |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
多用户系统中的文件通常归属于一个特定的用户。如果在创建文件时没有指定合适的权限，可能会导致以下问题：
1. 安全问题：如果文件的权限设置不当，可能会导致其他用户可以访问、修改或删除该文件，从而导致安全问题。
2. 数据丢失：如果文件的权限设置不当，可能会导致其他用户意外地删除或修改该文件，从而导致数据丢失。
3. 不必要的访问：如果文件的权限设置不当，可能会导致其他用户可以访问该文件，从而导致不必要的访问和干扰。
因此，在多用户系统中，应该根据需要指定合适的权限，以确保文件的安全性和可靠性。
```

**修复建议**

```text
1. 使用os.open函数创建文件，并在第三个参数显示地指定文件权限。
2. 建议按照文件使用场景严格限制文件访问许可，阻止未授权的访问。
```

**正确示例**

```text
##### 场景1：
- 修复示例：创建文件时指定了合适的权限
```python
import os
import stat
flags = os.O_WRONLY | os.O_CREAT | os.O_EXCL # 注意根据具体业务的需要设置文件读写方式
mode = stat.S_IWUSR | stat.S_IRUSR # 注意根据具体业务的需要设置文件权限
with os.fdopen(os.open('testfile.txt', flags, mode), 'w') as fout: # 符合：创建文件时在os.open的第三个参数指定了合适的权限
fout.write('secrets!')
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：创建文件时没有指定权限
```python
with open('testfile.txt', 'w') as fout: # 不符合：创建文件时没有指定权限
fout.write('hi, 2012')
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:15:28

<a id="rule-32"></a>

### 32. G.FIO.02 使用外部数据构造的文件路径前必须进行校验，校验前必须对文件路径进行规范化处理

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Path_Manipulation |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

使用外部数据构造文件路径时，必须对其合法性进行校验，否则可能会产生路径遍历（Path Traversal）漏洞。文件路径有多种表现形式，如绝对路径、相对路径，路径中可能会包含各种链接、快捷方式、影子文件等，这些都会对文件路径的校验产生影响，所以在文件路径校验前要对文件路径进行规范化处理。

**修复建议**

```text
先用os.path.realpath获取真实路径，再通过对生成的真实路径做预期判断，预期校验的方式有很多种，最终的校验效果只要确保真实路径是在安全目录下即可。例如可以用startswith或反过来判断real_path不在哪些路径下等方式，判断该真实路径在能够安全路径下（但注意不能使用endswith、os.path.exists、os.path.isdir等属于无效预期判断的方式进行预期判断）。
说明：os.path.realpath()方法标准化的文件路径，文件目录会缺少末尾的文件路径分隔符，进行文件路径校验时，要考虑该场景的影响，防止文件路径校验出现与预期不符的结果。
```

**正确示例**

```text
##### 场景1：
- 修复示例：对文件路径进行校验
```python
import os
def path_verify(path, safe_start):
# 校验函数里是对参数进行realpath方法生成真实路径，并对生成的路径进行预期校验，预期校验是根据业务的角度去校验的，常见的预期校验方法如：startswith、正则等。
# 注：os.path.exists不属于预期校验
real_path = os.path.realpath(path) # 清理告警第一步：用realpath方法
if real_path.startswith(safe_start): # 清理告警第二步：预期校验
return real_path
path = input() # 污染源，例如input、sys.argv、os.environ 等
safe_start = "/usr/test"
try:
# 对文件路径进行校验
verified_path = path_verify(path, safe_start) # 污染源数据经过特定函数名的校验函数，对污染源数据进行校验，污染源数据即不可信数据变为可信数据
if verified_path:
os.removedirs(verified_path) # 符合
except Exception as ex:
print(ex)
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：未对文件路径进行规范化处理
```python
import os
path = input()
try:
# 未对文件路径进行规范化处理
os.removedirs(os.path.abspath(path)) # 不符合
except Exception as ex:
print(ex)
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:08:28

<a id="rule-33"></a>

### 33. G.FIO.03 禁止使用tempfile.mktemp创建临时文件

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_forbid_use_mktemp |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
使用tempfile.mktemp函数创建临时文件存在已知风险，比如文件名冲突导致文件覆盖，潜在的竞争条件等。
因此，建议使用安全的方法创建临时文件，包括但不限于tempfile模块的mkstemp, mkdtemp, NamedTemporaryFile函数。
```

**修复建议**

使用安全的方法创建临时文件，包括但不限于tempfile模块的mkstemp, mkdtemp, NamedTemporaryFile函数。

**正确示例**

```text
##### 场景1：
- 修复示例：使用安全的函数创建临时文件
```python
import os
import shutil
import tempfile
tmp_file_one = tempfile.mkstemp() # 符合：(3, '/tmp/tmpyo8ddjhq') 创建临时文件
tmp_file_two = tempfile.mkdtemp() # 符合：/tmp/tmpf_od5le5 创建临时目录
tmp_file_three = tempfile.NamedTemporaryFile(delete=False) # 符合：<tempfile._TemporaryFileWrapper object at 0x000001E3D182BE88>
if os.path.isfile(tmp_file_one[1]):
os.close((tmp_file_one[0]))
os.remove(tmp_file_one[1])
if os.path.isfile(tmp_file_three.name):
tmp_file_three.close()
os.remove(tmp_file_three.name)
if os.path.isdir(tmp_file_two):
os.mkdir(os.path.join(tmp_file_two, '1.txt'))
shutil.rmtree(tmp_file_two)
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：使用tempfile.mktemp创建临时文件
```python
import tempfile
filename = tempfile.mktemp() # 不符合：生成的文件名与时间强相关，有可能引起重名风险
with open(filename, "w+") as tmp_file:
...
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:27:36

<a id="rule-34"></a>

### 34. G.FIO.04 临时文件使用完毕应及时删除

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_delete_temporary_file |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
Python 中临时文件使用完毕如果不及时删除，可能会导致以下问题：
1、临时文件可能占用大量的硬盘空间，如果不及时删除，可能会导致硬盘空间不足。
2、临时文件可能包含敏感信息，如果不及时删除，可能会导致泄露隐私。
3、临时文件存在期间，可能会被其他程序误删或锁定，导致程序出错。
因此，对于Python中的临时文件，应该在使用完毕后立即删除，以保障系统的安全性和稳定性。
```

**修复建议**

```text
1. 删除使用完毕的临时文件。应根据业务实际的场景，选择关闭文件时自动删除文件的操作，还是手动显式删除临时文件的操作。
2. 推荐使用with… as… 语句处理删除事宜。
```

**正确示例**

```text
##### 场景1：
- 修复示例1：使用os.remove或os.unlink函数删除
```python
import os
diy_file = "diyData.txt"
content = "Data"
try:
file_handle = open(diy_file, os.O_WRONLY, int("0600", 8))
try:
file_handle.write(content)
except Exception as e:
doing_something()
finally:
file_handle.close()
except Exception as ex:
print(ex)
# 建议使用with语句实现上述功能
# 结束时，显式的删除它
if os.path.isfile(diy_file):
os.remove(diy_file) # 符合
```
- 修复示例2：使用NamedTemporaryFile ()方法
这个示例创建临时文件时用到了NamedTemporaryFile ()方法，该方法会新建一个随机的文件名。文件使用try-finally构造块，在finally处手动关闭文件。而不管是否有异常发生，由于在打开文件时用到了delete选项，使得文件在关闭后会被自动删除。
```python
import tempfile
tmp_file_handle = tempfile.NamedTemporaryFile(delete=True) # 符合
print(tmp_file_handle.name)
try:
tmp_file_handle.file.write(b"abc")
except Exception as e:
doing_something()
finally:
tmp_file_handle.close()
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：临时文件使用完毕未及时删除
```python
import os
import tempfile
fd, path = tempfile.mkstemp() # 不符合
try:
with os.fdopen(fd, "w") as f:
f.write('Hello, world!')
except Exception as e:
doing_something()
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:20:50

<a id="rule-35"></a>

### 35. G.FIO.06 解压文件必须进行安全检查

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_unzip_file_without_check_size |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

在解压文件时，如果不进行安全检查，攻击者有可能上传一个很小的zip文件，但是完全解压缩之后达到几百万GB甚至更多，可能会使磁盘空间消耗殆尽导致系统或程序无法正常运行。因此在解压缩文件时必须要进行安全检查。

**修复建议**

```text
1. 禁止直接一次性递归解压压缩包全部内容，在解压前最少进行以下几点基础安全检查：
- 文件个数是否超出业务预期个数；
- 文件大小是否超出业务预期大小；
- 文件大小是否超出目标目录所在磁盘空间。
2. 建议根据需要，依赖其它工程手段加强防范，这些工程手段包括并不限于：
- 为解压文件单独分区，限定大小；
- 对压缩包做健康检查，包括其中内容是否符合业务预期；
- 对CPU和内存的使用量进行限定，防止大量压缩包中海量小文件等攻击手段。（需结合程序实际运行情况估计，慎用）
```

**正确示例**

```text
##### 场景1：未进行安全检查
- 修复示例：解压前最少进行以下几点基础安全检查
```python
import os
import zipfile
import psutil
class MyZip:
# 限制解压后大小不能超过1M，文件个数不能超过10个
MAX_SIZE = 1 * 1024 * 1024
MAX_FILE_CNT = 10
@staticmethod
def unzip(path, zip_file):
file_path = path + os.sep + zip_file
dest_dir = path
with zipfile.ZipFile(file_path, 'r') as src_file:
# 检查点1：检查文件个数，文件个数大于预期值时上报异常退出
file_count = len(src_file.infolist())
if file_count >= MyZip.MAX_FILE_CNT:
raise IOError(f'zipfile({zip_file}) contains {file_count} files exceed max file count')
# 检查点2：检查第一层解压文件总大小，总大小超过设定的上限值
total_size = sum(info.file_size for info in src_file.infolist())
if total_size >= MyZip.MAX_SIZE:
raise IOError(f'zipfile({zip_file}) size({total_size}) exceed max limit')
# 检查点3：检查第一层解压文件总大小，总大小超过磁盘剩余空间
dest_dir_partition = '/' # 解压目录所在分区
if total_size >= psutil.disk_usage(dest_dir_partition).free:
raise IOError(f'zipfile({zip_file}) size({total_size}) exceed remain target disk space')
# 所有检查通过之后，解压所有文件
for filename in zip_file.namelist():
zip_file.extract(filename, dest_dir) # 符合
```
```

**错误示例**

```text
##### 场景1：未进行安全检查
- 错误示例：
```python
import zipfile
import os
def unzip(path, zip_file):
file_path = path + os.sep + zip_file
dest_dir = path
src_file = zipfile.ZipFile(file_path, 'r')
for info in src_file.infolist():
src_file.extract(info.filename, dest_dir) # 不符合，解压前未进行安全检查
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-08-19 20:01:15

<a id="rule-36"></a>

### 36. G.FMT.05 导入部分(imports)应该置于模块注释和文档字符串之后，模块全局变量和常量声明之前

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W0410,H2305 |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

导入部分(imports)应位于文件的顶端，通常是置于模块注释和文档字符串之后，模块全局变量和常量声明之前。

**修复建议**

```text
1. 导入语句应该置于模块注释和文档字符串之后，模块全局变量和常量声明之前。
2. 非导入语句如全局变量等应置于导入语句之后，更改环境路径为目的的代码如sys.path.append以及猴子补丁等可以视作导入代码。
3. 如果文件中定义了类似`__all__`、`__version__`这种全局变量（以两个下划线开头、以两个下划线结尾），那么导入部分应该放在这类定义的后面，但`__future__`模块的导入例外，`__future__`模块的导入必须放在文档字符串之后，其他内容之前。
故放置顺序为：
- `__future__`模块
- `__all__`、`__version__`这种全局变量
- 导入模块语句
```

**正确示例**

```text
# 【正例1】
"""This is a module
Functions of this module
"""
# 导入部分位于文档字符串之后，全局变量之前
import os
import sys
sample_global_variable = 0
M_SAMPLE_GLOBAL_CONSTANT = 0
# 【正例2】如果文件中定义了类似__all__、__version__这种全局变量（以两个下划线开头、以两个下划线结尾），那么导入部分应该放在这类定义的后面，但__future__模块的导入例外，__future__模块的导入必须放在文档字符串之后，其他内容之前。
"""This is a module
Functions of this module
"""
from __future__ import print_function
__all__ = ['hello', 'world']
__version__ = 'V1.0'
import os
import sys
from time import sleep
```

**错误示例**

```text
##### 场景1：导入语句的位置错误
- 错误示例：导入部分位于文档字符串和全局变量之后
```python
"""This is a module
Functions of this module
"""
sample_global_variable = 0
M_SAMPLE_GLOBAL_CONSTANT = 0
import os # 不符合，导入位置错误
import sys # 不符合，导入位置错误
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 01:57:37

<a id="rule-37"></a>

### 37. G.FNM.01 禁止使用可变对象作为参数默认值

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W0102 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
在Python程序中，函数的参数默认值只会被初始化一次，并且被重复利用。
当参数默认值为可变对象时，若函数调用时不传入参数值，则对该参数的默认值进行的任何操作实际上操作的是同一个对象。
因此，仅可使用基本类型字面常量或常量作为参数默认值：整数、bool、浮点数、字符串、None。
```

**修复建议**

仅可使用基本类型字面常量或常量作为参数默认值：整数、bool、浮点数、字符串、None

**正确示例**

```text
# 【正例1】使用None作为默认值，在程序中重新初始化为空列表。
def fun(arg_a, list_arg=None):
list_arg = list_arg or []
list_arg.append(arg_a)
print(list_arg)
fun(1) # 打印[1]
fun(1) # 打印[1]
fun(1, []) # 打印[1]
fun(1, {}) # 打印[1]
fun(1, ()) # 打印[1]
fun(1, False) # 打印[1]
# 使用上面的写法，当list_arg为False或{}或()，均会被重新初始化为[]。
# 通常不建议一个参数支持多种数据类型，但若确实存在该情况，可采用下面的写法：
def fun(arg_a, string_arg='', list_arg=None):
list_arg = [] if list_arg is None else list_arg
list_arg.append(arg_a)
print(list_arg)
```

**错误示例**

```text
# 【反例1】使用空列表作为默认值
def fun(arg_a, list_arg=[]):
list_arg.append(arg_a)
print(list_arg)
fun(1) # 打印[1]
fun(1) # 打印[1, 1]
fun(1, []) # 打印[1]
# 【反例2】使用空字典作为默认值
def fun(arg_a, list_arg={}):
list_arg[arg_a] = arg_a
print(list_arg)
fun(1) # 打印{1: 1}
fun(2) # 打印{1: 1, 2: 2}
fun(3, {}) # 打印{3: 3}
# 【反例3】使用函数调用作为默认值
def init_data():
return [1]
def fun(arg_a, list_arg=init_data()):
list_arg = list_arg or []
list_arg.append(arg_a)
print(list_arg)
fun(1) # 打印[1, 1]
fun(1) # 打印[1, 1, 1]
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:32

<a id="rule-38"></a>

### 38. G.FNM.04 不要将没有返回值的函数调用结果赋值给变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | E1111 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
将没有返回值的函数调用赋值给变量时，变量实际被赋值为None，没有意义。
应合理设计函数的返回结果以支持后续处理操作。
```

**修复建议**

没有返回值的函数不要把调用结果赋值给变量

**正确示例**

```text
# 【正例1】
def division_if_success_return_true(x, y):
try:
print(x / y)
except ZeroDivisionError:
return False
return True
result = division_if_success_return_true(1, 2)
```

**错误示例**

```text
# 【反例1】
def division(x, y):
print(x / y)
result = division(1, 2)
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:33

<a id="rule-39"></a>

### 39. G.FNM.06 使用return代替StopIteration来结束生成器

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_use_return_replace_stopiter |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
Python 中的生成器是一种使用 `yield` 关键字定义的迭代器，能够逐步生成值。使用 `StopIteration` 结束生成器存在以下缺点：
1. 额外的异常处理：显式抛出 `StopIteration` 会导致调用生成器时需要额外的异常处理逻辑，从而增加代码复杂性和维护难度。
2. 可读性和一致性问题：`StopIteration` 的使用与普通函数的结束方式（`return`）不一致，可能使开发者在理解生成器逻辑时产生混淆，降低代码的可读性和一致性。
```

**修复建议**

用 `return` 来结束生成器的迭代。

**正确示例**

```text
##### 场景1：
- 修复示例：return结束生成器
```python
def correct_example():
for i in range(100):
if i == 10:
return # 符合，当函数执行到return时候，__next__()会抛出异常，直接终止当前生成器
yield i
for i in correct_example():
print(i) # for循环会自动捕获该异常，直接停止遍历
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：raise StopIteration结束生成器
```python
def wrong_example():
for i in range(100):
if i == 10:
raise StopIteration() # 不符合，i等于10时，这里抛出一个异常
yield i
for i in wrong_example():
print(i) # for循环并不会自动捕获该异常，程序将报错，这显然不是我们所预期的
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:29:25

<a id="rule-40"></a>

### 40. G.IMP.02 使用 from ... import ... 语句的注意事项

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | flake8 |
| 关联工具规则 | H303,F403 |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
import和from ... import ...作为Python最常用的两种导入方式，各有优缺点，要结合实际场景进行选取。
导入方式 import module from module import ...
主要优点 使用时，模块-对象对应关系明确 严格控制使用的对象清单
主要缺点 module.obj的使用方式冗余啰嗦 模块间存在同名对象时需要通过from ... import ... as ...重命名，使用时容易混淆
如果模块名较长，为了减少使用时的语句冗余，可以使用import module as mo的方式，也可以使用from module import ...的方式。
禁止使用from ... import *，该方法会导入模块内所有非_前缀的对象，存在以下缺点：
1.如果多个模块存在同名对象，后导入的对象会覆盖先导入的同名对象，开发者无法快速识别该覆盖行为；
2.无法明确各对象的所属模块，给代码的功能调测、长期维护等带来额外的开销。
测试代码使用from ... import *，同样存在上述缺点。虽然测试代码不会在生产环境运行，但会给开发调试带来不必要的麻烦，建议避免使用该方式导入。
```

**修复建议**

使用 from ... import ... 语句参照编程规范的注意事项

**正确示例**

```text
# 【正例1】常见的导入方式，两种导入方式按需混用。
import os
import sys
from sqlalchemy import create_engine
# 【正例2】对于某些导入耗时较高的模块，可以使用示例方式控制使用的对象清单，同时规避易混淆的缺点。
from os import path as os_path
from sys import path as sys_path
```

**错误示例**

from sys import * # 不符合，os模块的path被sys模块的path覆盖

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:48

<a id="rule-41"></a>

### 41. G.IMP.03 避免使用__import__函数

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W1802 |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
建议使用importlib.import_module函数替代__builtins.__import__。
在importlib模块中，提供了比内置函数__import__更为丰富的接口，不需要关注导入细节，使用更为简单。
除了用于无法通过声明来表示的初始化以外，init函数的一个常用法是在真正执行之前进行验证或者修复程序状态的正确性。
一个文件只定义一个init函数，同时建议init函数尽量放置在源文件靠前的位置，这样有助于代码阅读。
```

**修复建议**

建议使用importlib.import_module函数替代__builtins.__import__

**正确示例**

```text
# 【正例1】
import importlib
# 以下用法为建议用法
os_path_recommend = importlib.import_module('os.path')
# <module 'posixpath' from '.../pythonX/posixpath.py'>
# 以下用法可实现功能，但使用略为复杂，不推荐使用
os_path_accessible = __import__('os.path', fromlist=('path',))
# <module 'posixpath' from '.../pythonX/posixpath.py'>
```

**错误示例**

```text
# 【反例1】
os_path_negative = __import__('os.path')
# <module 'os' from '.../pythonX/os.py'>
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-11-16 16:09:54

<a id="rule-42"></a>

### 42. G.LOG.01 logging模块应尽量使用懒插值的能力记录debug等低级别日志

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_not_recommended_log_usage |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
logging模块具有懒插值特性：当采用`logging.<logging method>(format_string, format_args)`形式，logger将对日志消息中的参数字符串进行延迟插值。日志懒插值特性是指在代码执行时进行`format_args`的求值和日志文本的格式化，相较于缺省的及早求值计算方式，能够节省一定的计算时间和存储空间。
生产环境下，一般不输出debug日志，有选择地输出info日志，输出warning、error等日志，所以对于debug等低级别的日志打印，建议采用懒插值的模式（生产环境下不需要打印debug级别日志时，懒差值模式可以避免构造日志内容，从而节省内存和执行时间）。对于在生产环境下不打印日志的场景，建议所有日志统一采用懒插值的模式。
对于info以上级别的日志，生产环境下一般都会打印，对于这类级别的日志，建议采用f-string构造日志内容，既可以高效构造日志内容，也会提升代码的可读性。
注意：此规则仅针对python的logging模块，其他log框架或自定义log框架不在检查范围。
```

**修复建议**

对于debug等低级别的日志打印，建议采用懒插值的模式。

**正确示例**

```text
##### 场景1：
- 修复示例：使用`logging.<logging method>(format_string, format_args)`
```python
import logging
error_details = "it is really a big bug."
error_details_ext = "Yes, really big."
logging.debug("Here catch some errors, detail is:%s more:%s", error_details, error_details_ext) # 符合
```
```

**错误示例**

```text
##### 场景1：
- 错误示例1：采用`logging.<logging method>(format_string % format_args)`形式打印日志。
```python
import logging
error_details = "it is really a big bug"
logging.debug("Here catch some errors, detail is:%s" % error_details) # 不符合
```
- 错误示例2：采用format方法打印日志。
```python
import logging
error_details = "it is really a big bug"
logging.debug("Here catch some errors, detail is:{}".format(error_details)) # 不符合
```
- 错误示例3：采用f-string这种高效的日志内容构造模式，但是相对于懒插值模式，在不打印debug级别日志时仍无法避免构造日志内容，性能上仍不如懒差值模式。
```python
import logging
error_details = "it is really a big bug"
logging.debug(f"Here catch some errors, detail is:{error_details}") # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-08-16 15:24:19

<a id="rule-43"></a>

### 43. G.LOG.03 禁止直接使用外部数据记录日志

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Log_Forging,SecPy_VF_Log_Forging_Debug |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 25% |

**审查要点**

```text
如果直接将外部输入作为日志记录的内容，可能会导致安全风险：
1. 日志注入：恶意用户可利用回车、换行等字符注入一条完整的日志；
2. 敏感信息泄露：当用户输入敏感信息时，直接记录到日志中可能会导致敏感信息泄露；
3. 垃圾日志或日志覆盖：当用户输入的是很长的字符串，直接记录到日志中可能会导致产生大量垃圾日志；当日志被循环覆盖时，这样还可能会导致有效日志被恶意覆盖。
因此，为了防止安全风险，应该在将外部数据用于日志记录之前对其进行严格的过滤和验证，或者使用相应的安全框架来限制外部输入的内容和格式。
```

**修复建议**

```text
1. 对于较长字符串可以截断。
2. 将其中的\r\n等导致换行的字符进行替换，消除注入风险。
3. 口令、密钥等敏感数据若因为特殊原因必须要记录日志，应该使用固定长度的星号（*）代替这些敏感信息。
```

**正确示例**

```text
##### 场景1：
- 修复示例：替换换行符
```python
import logging
def string_verify(input_str):
# 将\r\n等导致换行的字符进行替换
if input_str:
verified_str = input_str.replace('\n', '_').replace('\r', '_')
else:
verified_str = ''
return verified_str
with open('example', 'r') as f:
data = f.read()
# 校验外部数据
verified_data = string_verify(data)
logging.error("Request data validate fail! Request data: %s" + verified_data) # 符合
```
##### 场景2：
- 修复示例：用（*）代替敏感信息
```python
import logging
def string_verify(input_tel):
# 校验数据长度
if len(input_tel) == 11:
return True
return False
with open('example', 'r') as f:
tel = f.read()
# 校验外部数据
if string_verify(tel):
logging.info("Request data userphone: %s" + tel.replace(tel[3:7], '****')) # 符合
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：直接使用外部数据记录日志
```python
import logging
with open('example', 'r') as f:
data = f.read()
# 直接使用外部数据记录日志
logging.error("Request data validate fail! Request data: " + data) # 不符合
```
##### 场景2：
- 错误示例：直接使用外部数据记录日志
```python
import logging
with open('example', 'r') as f:
tel = f.read()
# 直接使用外部数据记录日志
logging.info("Request data userphone: %s" + tel) # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:14:52

<a id="rule-44"></a>

### 44. G.OPR.01 对除法运算和模运算中的除数为0的情况做相应保护

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_divide_by_zero |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

如果除法或模运算中的除数为零可能会导致程序终止，因此需要对除数为0的情况做相应保护。

**修复建议**

```text
1. 在除法运算前，对除数是否为0进行判断。
2. 通过捕获除0异常ZeroDivisionError的方式，来防止程序意外终止。
3. 明确参数的使用约束，由调用方确保除数不为零。
```

**正确示例**

```text
##### 场景1：
- 修复示例1：对除数进行非零判断
```python
dividen_num = 0
divisor_num = 0
# 符合：在进行除法运算前，先对除数做非0判断
if divisor_num != 0:
division_result = dividen_num / divisor_num
remainder_result = dividen_num % divisor_num
else:
...
```
- 修复示例2：捕获除零异常
```python
dividen_num = 0
divisor_num = 0
# 符合：通过捕获除0异常ZeroDivisionError的方式，来防止程序意外终止
try:
division_result = dividen_num / divisor_num
except ZeroDivisionError:
...
else:
...
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：未对除数进行非零判断
```python
dividen_num = 0
divisor_num = 0
# 不符合：未对除数进行非0判断
division_result = dividen_num / divisor_num
remainder_result = dividen_num % divisor_num
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:21:34

<a id="rule-45"></a>

### 45. G.OPR.02 与None作比较要使用is或is not，不要使用等号

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H3212 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

is判断是否指向同一个对象（判断两个对象的id是否相等），==会调用__eq__方法判断是否等价（判断两个对象的值是否相等）。

**修复建议**

与None作比较要使用`is`或`is not`。

**正确示例**

```text
# 【示例1】同一个实例，使用is和==的判断结果不同
class PersonMetaClass(object):
def __init__(self, name, age, gender):
self.name = name
self.age = age
self.gender = gender
def __eq__(self, other):
if hasattr(other, 'gender'):
return self.gender == other.gender
else:
return 'The assertion property does not exist'
student1 = PersonMetaClass('Xiao li', 17, 'female')
student2 = PersonMetaClass('Han Hong', 18, 'female')
print(student2 is None) # False
print(student2 == None) # The assertion property does not exist
print(student2 == student1) # True
print(student2 is student1) # False
```

**错误示例**

```text
##### 场景1：对象与None的比较
- 错误示例：对象与None用等于号做比较
```python
class PersonMetaClass(object):
def __init__(self, name, age, gender):
self.name = name
self.age = age
self.gender = gender
def __eq__(self, other):
if hasattr(other, 'gender'):
return self.gender == other.gender
else:
return 'The assertion property does not exist'
student1 = PersonMetaClass('Xiao li', 17, 'female')
student2 = PersonMetaClass('Han Hong', 18, 'female')
print(student2 == None) # 不符合，判断两个对象的值是否相等，结果为The assertion property does not exist
print(student2 == student1) # True
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 01:57:43

<a id="rule-46"></a>

### 46. G.OPR.03 禁止使用is或is not运算符在内置类型之间作比较

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | flake8 |
| 关联工具规则 | F632 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

is和is not是用来检查两个实例是否指向同一个对象的，禁止用is或is not运算符在内置类型之间作比较，内置类型有int、float、complex、list、tuple、set、frozenset、dict、str、bytes，建议用==或!=运算符替代。

**修复建议**

内置类型数值之间用`==`或`!=`来比较。

**正确示例**

```text
# 【正例1】
from sys import intern
def literal_comparison(param):
return param == 2000
literal_comparison(2000) # 将返回True
literal_comparison(int("2000")) # 将返回True
() == tuple() # 符合，将返回True
(1,) == tuple([1]) # 符合，将返回True
```

**错误示例**

```python

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:26

<a id="rule-47"></a>

### 47. G.PRJ.03 产品代码不要包含任何调试入口点

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_forbid_use_dbg |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

如果产品代码包含调试入口点，则黑客可以使用这些入口点来执行恶意代码或破坏系统，导致安全风险。因此，开发者在交付前必须删除所有调试相关的代码。

**修复建议**

交付前必须删除所有调试相关的代码。

**正确示例**

```text
##### 场景1：
- 修复示例：删除所有调试相关的代码
```python
if not flag:
... # 符合
print('.....')
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：代码中引入pdb模块
```python
if not flag:
import pdb
pdb.set_trace() # 不符合
print('.....')
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-48"></a>

### 48. G.PRJ.04 建议同一项目内的编码方式保持一致，推荐使用UTF-8

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W1801 |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
Python3默认使用UTF-8编码，使用其它的编码（如`latin-1`，`gbk`等等）可能在使用默认解码的程序解码时出现解码失败。
建议同一项目内的编码方式保持一致。推荐使用默认的UTF-8，如果使用UTF-8编码可以省略不写编码方式。
```

**修复建议**

```text
文件编码统一使用utf-8格式编码，或者文件开头使用# coding: utf-8进行编码声明。
文件开头注释中的编码申明，不代表文件的实际编码格式，工具检查的是文件的实际编码格式。
如果使用了utf-8编码申明仍然有告警，则说明文件实际编码和编码申明不一致。
需要使用notepad++或VSCode等软件查看文件实际编码，然后将文件编码转换为utf-8编码。
注意文件编码是否带windows系统的bom头，如果带bom头标志，则会触发告警，需要用软件将文件编码utf-8-bom转换为utf-8编码。
```

**正确示例**

```text
##### 场景1：编码申明
- 修复示例：使用utf-8编码申明
```python
#!/usr/local/bin/python
# coding: utf-8
import os
import sys
...
```
- 修复示例：默认不写编码方式
```python
#!/usr/bin/python
import os
import sys
...
```
##### 场景2：文件实际编码和申明编码不符
- 修复示例：
```

**错误示例**

```text
##### 场景1：编码申明
- 错误示例：`# latin-1`编码格式错误
```python
#!/usr/local/bin/python
# latin-1 # 不符合
import os
import sys
...
```
- 错误示例：编码申明不在第1、第2行
```python
#!/usr/local/bin/python
#
# -*- coding: latin-1 -*- # 不符合
import os
import sys
...
```
- 错误示例：不支持的编码方式
```python
#!/usr/local/bin/python
# -*- coding: utf-42 -*- # 不符合
import os
import sys
...
```
##### 场景2：文件实际编码和申明编码不符
- 错误示例：
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-49"></a>

### 49. G.PRJ.05 不用的代码段直接删除，不要注释掉

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H3115 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
python的注释包含：单行注释、多行注释、文档字符串等。文档字符串是常用来描述类或者函数的功能、参数、返回值等信息，其余形式注释都是使用#符号开头用来注释掉#后面的内容。基于python语言运行时编译的特殊性，如果在提供代码的时候提供的是py文件，该文件中包含被注释掉的代码，当企图恢复使用这段代码时，极有可能引入易被忽略的缺陷；尤其是某些接口函数，如果不在代码中进行彻底删除，可能在不知情的情况下就被启用了某些本应被屏蔽的功能。正确的做法是，不需要的代码直接删除。若再需要时，考虑移植或重写这段代码。
```

**修复建议**

对不使用的旧代码应该及时删除，以免暴露程序接口，造成不安全的因素

**正确示例**

```text
# 【正例1】
if __name__ == "__main__":
if sys.argv[1].startswith('--'):
option = sys.argv[1][2:]
if option == "load":
# 安装应用
LoadCmd(option, sys.argv[2:3][0])
elif option == "unload":
# 卸载应用
UnloadCmd(sys.argv[2:3][0])
elif option == "unloadproc":
# 卸载流程
UnloadProcessCmd(sys.argv[2:3][0])
else:
Loginfo("Command %s is unknown" % (sys.argv[1]))
```

**错误示例**

```text
# 【反例1】例中很容易让其他人看到我们程序中的两个屏蔽的接口，容易造成不安全的因素，注释的代码应该删除。
if __name__ == "__main__":
if sys.argv[1].startswith('--'):
option = sys.argv[1][2:]
if option == "load":
# 安装应用
LoadCmd(option, sys.argv[2:3][0])
elif option == "unload":
# 卸载应用
UnloadCmd(sys.argv[2:3][0])
elif option == "unloadproc":
# 卸载流程
UnloadProcessCmd(sys.argv[2:3][0])
# elif option == 'active':
# ActiveCmd(sys.argv[2:3][0])
# elif option == 'inactive':
# InActiveCmd(sys.argv[2:3][0])
else:
Loginfo("Command %s is unknown" % (sys.argv[1]))
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-09-30 15:17:51

<a id="rule-50"></a>

### 50. G.PRJ.06 正式发布的代码及注释内容不应包含开发者个人信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | Personal_Info_Check |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

正式发布的代码及注释内容如果包含开发者个人信息，可能会泄露具体的开发人员信息，存在引发社会工程学方面的风险。

**修复建议**

```text
1. 应从正式发布的版本中删除开发者个人信息，比如工号、姓名、部门、邮箱、问题单号等。版权声明是组织（公司）的声明，开发者个人没必要也不能用个人信息来做类似于版权声明的事情。
2. 如果需要在代码中标明个人贡献，或者方便联系原作者进行代码维护，一般通过配置管理git等方式来实现，如果需要通过在开发代码中添加开发者个人信息，在发布代码时必须通过相关工具删除这类注释信息，否则会有社会工程学方面的风险。
3. 该规则支持配置用户自定义模型，用于自定义检查关键字或正则表达式。如疑似误报，请先确认是否配置了自定义模型，如有疑问，可咨询 CodeCheck 平台右侧人工客服。
```

**正确示例**

```text
##### 场景1：
- 修复示例：代码中没有个人信息
```python
# 符合: 代码中没有个人信息
line_no = 1
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：代码中包含个人信息
```python
# 不符合: 代码中包含了个人工号信息
employee_no = 'a00123456'
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:24:09

<a id="rule-51"></a>

### 51. G.PRJ.07 禁止代码中包含公网地址

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | Public_Url_Check |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

代码或脚本中包含用户不可见，不可知的公网地址，可能会引起客户质疑。

**修复建议**

这条规则必须配置自定义模型，否则不检查。如有误报，请点击codecheck页面的设置选项，点击高级配置，点击PYTHON模型集右边的眼睛图标，可下载自定义模型xml文件查看，或向模型集创建者咨询。

**正确示例**

删除自定义模型中禁止的公网地址。

**错误示例**

与自定义模型中的正则匹配的公网地址。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-11-16 17:32:03

<a id="rule-52"></a>

### 52. G.PSL.01 避免使用已经被标记为弃用并有明确替代的方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H3111,H3112,W4902 |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

当代码运行出现DeprecationWarning，明确指出某些模块、类或函数计划废弃，并给出了新的替代方案时，必须按照其提示的方法、版本正确地使用新方案替代计划废弃的老方案。

**修复建议**

使用新版本的函数替换旧版本的函数

**正确示例**

```text
# 【正例1】
from collections.abc import Iterable
```

**错误示例**

```text
# 【反例1】
from collections import Iterable # DeprecationWarning: Using or importing the ABCs from 'collections' instead of from 'collections.abc' is deprecated, and in 3.8 it will stop working
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-09-30 15:17:51

<a id="rule-53"></a>

### 53. G.SER.01 禁止使用pickle.load、_pickle.load和shelve模块加载外部数据

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Deserialization_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
使用pickle.load、_pickle.load和shelve模块加载外部数据存在安全风险。这些模块可以将Python对象序列化为二进制数据，然后将其保存到文件中。但是，如果不小心加载了恶意的二进制数据，就可能导致代码执行任意命令，从而造成安全漏洞。
如果需要加载外部数据，可以使用更安全的方式，比如json。
```

**修复建议**

```text
1. 采用更为安全的序列化格式（例如json）处理不受信任的数据。
2. 使用hmac对序列化数据进行签名，确保数据没有被篡改。
```

**正确示例**

```text
##### 场景1：
- 修复示例：使用json加载外部数据
```python
import json
with open('data.json', 'r') as f:
# 使用json.loads加载外部数据
data = json.loads(f.read()) # 符合
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：使用pickle加载外部数据
```python
import pickle
with open('data.pkl', 'rb') as f:
# 使用pickle.loads加载外部数据
data = pickle.loads(f.read()) # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:09:54

<a id="rule-54"></a>

### 54. G.SER.02 禁止序列化未加密的敏感数据

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_forbid_serialize_sensitive_data |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

python在进行数据序列化时，如果未加密的敏感数据被序列化后，攻击者将其截获，通过反序列化又能重新构造出原来的对象，不仅导致数据泄露，还可以确定该对象的实现细节，对系统造成危害。序列化并非加密存储，因此，被序列化信息中不应该包括：密钥、数字证书、以及那些在序列化时引用敏感数据的类。

**修复建议**

在进行序列化时，程序必须确保敏感数据不被序列化，可以在序列化包含敏感信息的数据之前删除其中的敏感数据。

**正确示例**

```text
##### 场景1：
- 修复示例：序列化之前删除敏感数据
```python
import pickle
class Password:
def __init__(self, password, id):
self.password = password
self.id = id
def __getstate__(self):
state = dict(self.__dict__)
del state['password'] # 符合
return state
dump_str = pickle.dumps(Password(12, 3))
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：序列化未加密含敏感信息
```python
import pickle
class Password:
def __init__(self, password, id):
self.password = password
self.id = id
def __getstate__(self):
state = dict(self.__dict__)
return state
dump_str = pickle.dumps(Password(12, 3)) # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:18:43

<a id="rule-55"></a>

### 55. G.SER.03 禁止使用yaml模块的load函数

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_yaml_load |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
yaml模块在数据序列化和配置文件中使用比较广泛，其在解析数据的时候遇到特定格式的数据类型会自动转换为Python的对象，比如会将时间类型的数据自动转化Python时间对象。这个特点让攻击者有机可乘。因此，禁止使用 yaml模块的load函数加载外部数据，会存在潜在的安全风险。
# PyYAML==3.13 模块的load函数实现
def load(stream, Loader=Loader):
"""
Parse the first YAML document in a stream
and produce the corresponding Python object.
"""
loader = Loader(stream)
try:
return loader.get_single_data()
finally:
loader.dispose()
PyYAML执行反序列化支持指定不同的加载器。
```

**修复建议**

```text
下载PyYAML模块的最新版本，并使用其提供的安全函数：
yaml.load(data, Loader=SafeLoader)
yaml.load_all(data, Loader=SafeLoader)
yaml.safe_load(data)
yaml.safe_load_all(data)
也可使用LibYAML库的CSafeLoader函数。
```

**正确示例**

```text
import yaml
poc = "!!python/object/apply:subprocess.check_output [[\"calc.exe\"]]"
# 等价于 yaml.load(poc, Loader=yaml.SafeLoader)
yaml.safe_load(poc)
# 上例中，SafeLoader加载器未定义python/object/apply标签的构造函数导致执行报错，未启动计算器。报错信息如下：
# ...
# yaml.constructor.ConstructorError: could not determine a constructor for the tag
# 'tag:yaml.org,2002:python/object/apply:subprocess.check_output'
# in "<unicode string>", line 1, column 1:
# !!python/object/apply:subprocess ...
# ^
```

**错误示例**

```text
【反例1】
# PyYAML<5.1 版本成功启动计算器
import yaml
poc = "!!python/object/apply:subprocess.check_output [[\"calc.exe\"]]"
# 等价于 yaml.load(poc, Loader=yaml.Loader)
yaml.load(poc)
【反例2】
# PyYAML<5.4 版本成功启动计算器
import yaml
poc = """!!python/object/new:tuple
- !!python/object/new:map
- !!python/name:eval
- [ "__import__('os').system('calc.exe')" ]"""
yaml.load(poc)
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-56"></a>

### 56. G.SER.04 禁止使用jsonpickle模块的encode/decode或dumps/loads函数

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_forbid_use_jsonpickle_encode |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

使用`jsonpickle`模块允许序列化 Python 对象中的任意代码，并在反序列化时执行它们，如果加载来源不可信的JSON字符串，攻击者可构造恶意输入获取执行权限。因此，建议使用标准的 JSON 序列化库（如 `json`）来序列化和反序列化 Python 对象。

**修复建议**

使用标准的 JSON 序列化库（如 json）来序列化和反序列化 Python 对象。

**正确示例**

```text
##### 场景1：
- 修复示例：使用json模块
```python
import json
poc = '{"py/reduce": [{"py/type": "subprocess.Popen"}, {"py/tuple": [{"py/tuple": ["cmd.exe", "/c", "calc.exe"]}]}]}'
json.decode(poc) # 符合
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：使用jsonpickle模块
```python
import jsonpickle
poc = '{"py/reduce": [{"py/type": "subprocess.Popen"}, {"py/tuple": [{"py/tuple": ["cmd.exe", "/c", "calc.exe"]}]}]}'
# 成功启动计算器
jsonpickle.decode(poc) # 不符合
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:22:43

<a id="rule-57"></a>

### 57. G.TES.01 禁止在生产版本的业务代码中使用assert

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | CodeMars |
| 关联工具规则 | BD_assert_used |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
在编译时编译优化参数如果大于等于1，编译器就会删除assert语句。如果在业务代码中使用了assert语句做了一些特殊的业务匹配，同时使用了编译优化，则可能会出现不可预期的结果。
因此，assert语句通常只在测试代码中使用，禁止在生产版本的业务代码中使用assert。
```

**修复建议**

正式发布代码中应删除assert语句。

**正确示例**

```text
##### 场景1：
- 修复示例：不使用assert
```python
import sys
class NotSupportedError(Exception):
def __str__(self):
return "Code is Linux only"
def check_system():
if "linux" in sys.platform:
return
raise NotSupportedError()
check_system() # 符合：不使用assert
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：使用了assert
```python
import sys
def check_system():
assert ("linux" in sys.platform), "Code is Linux only" # 不符合：使用了assert
check_system()
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-26 17:27:08

<a id="rule-58"></a>

### 58. G.TYP.02 浮点型数据判断相等不要直接使用==

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H0135 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

由于浮点数在计算机表示中存在精度的问题，数学上相等的数字，经过运算后，其浮点数表示可能不再相等，因而禁止使用相等运算符 == 来比较浮点数是否相等。另外，也不要把浮点数作为HashMap的Key使用。

**修复建议**

浮点型数据判断相等不要直接使用==，使用math.isclose方法来处理，不要把浮点数作为HashMap的Key使用

**正确示例**

```text
# 【正例1】考虑浮点数的精度问题，可在一定的误差范围内判定两个浮点数值是否相等。这个误差应根据实际需要进行定义。
data1 = 1.0 - 0.8
data2 = 0.8 - 0.6
EPSILON = 1e-15
if abs(data1 - data2) < EPSILON:
pass
# 【正例2】Python3.5以后的版本新增了math.isclose()方法，可以实现两个浮点数的比较，如果指接近相等则返回True，否则返回False。
import math
data1 = 3.0
data2 = 2.99998
print(math.isclose(data1, data2, rel_tol=1e-5)) # 此处打印结果为True
```

**错误示例**

```text
# 【反例1】
data1 = 1.0 - 0.8
data2 = 0.8 - 0.6
if data1 == data2:
do_something_true() # 预期进入此代码块，执行其他业务逻辑，但事实上data1 == data2的结果在很多情况下可能为False
else:
do_something_false() # 实际上会进入这里
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:23

<a id="rule-59"></a>

### 59. G.TYP.06 同一个字典表达式中各个键值不要相同

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W0109 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

同一个字典表达式中的各个键值不要重复，如果存在重复的键值，后面的键值对会覆盖前面的键值对，所以应该避免这种情况。

**修复建议**

字典表达式不能包含相同的键值

**正确示例**

```text
# 【正例1】
>>> dict_no_duplicate_values = {"lilei":20, "hanmeimei":30, "wangqiang":45}
>>> dict_no_duplicate_values
{'lilei': 20, 'hanmeimei': 30, 'wangqiang': 45}
```

**错误示例**

```text
# 【反例1】
>>> dict_duplicate_values = {"lilei":20, "hanmeimei":30, "wangqiang":45, "lilei":23}
>>> dict_duplicate_values
{'lilei': 23, 'hanmeimei': 30, 'wangqiang': 45}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:24

<a id="rule-60"></a>

### 60. G.TYP.08 必须使用isinstance判断变量类型

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H3148 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

Optional中的数据可能为null，如果不使用isPresent方法判空,会直接抛出java.util.NoSuchElementException: No value present

**修复建议**

使用Optional的get方法获取元素前，需要先试用isPresent方法判空

**正确示例**

```text
public static String check(Optional<String> opt) {
if (opt.isPresent()) {
return opt.get();
}
return "Error";
}
```

**错误示例**

```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:25

<a id="rule-61"></a>

### 61. G.VAR.01 禁止在变量的生命周期内修改其类型

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W0643 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

Python是动态类型语言，允许变量被赋值为不同类型对象，但这么做可能会导致运行时错误，另外变量上下文语义的变化会导致代码复杂度提升，从而难以调试和维护，也不会有任何性能的提升。因此，给变量赋值时，值的类型应该和变量的类型匹配，并且禁止变量在其生命周期内的值类型发生变化。

**修复建议**

保证变量在生命周期内部不改变其类型。

**正确示例**

```text
# 【正例1】
variable: int = 1
variable = 2
```

**错误示例**

```text
# 【反例1】
variable: str = 1
variable = b'hello world'
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-62"></a>

### 62. G.VAR.02 禁止使用global关键字声明不存在的外部变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | W0601 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

global、nonlocal关键字只能用于引用已被定义的变量，禁止用于声明未被定义的变量。同时尽量避免在函数中修改外部变量的值，对于仅需要读的场景，不使用global或nonlocal关键字。

**修复建议**

global、nonlocal关键字只能用于引用已被定义的变量，禁止用于声明未被定义的变量

**正确示例**

```text
# 【正例1】
_variable = 1
def test():
global _variable
_variable = 2
test()
print(_variable)
```

**错误示例**

```text
# 【反例1】外部作用域中没有全局变量variable，在test函数内使用global创建了全局变量variable，这是不好的实践。禁止在函数内使用 global直接创建全局变量
def test():
global variable
variable = 1
test()
print(variable)
# 【反例2】在test函数中仅读取全局变量时，不需要使用global进行声明。
_variable = 1
def test():
global _variable
print(_variable)
test()
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 15:59:56

<a id="rule-63"></a>

### 63. G.VAR.03 禁止覆盖外部作用域中的标识符

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | pylint |
| 关联工具规则 | H2104 |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

禁止覆盖外部作用域和内置作用域中的变量。

**修复建议**

避免局部变量和全局变量重名。

**正确示例**

```text
# 【正例1】
_global = 1
def test():
local_number = 2
if local_number == _global:
raise MyException
```

**错误示例**

```text
# 【反例1】
_global = 1
def test():
_global = 2
…
# 【反例2】
def test():
type = "str" # 覆盖了内置作用域中的type
…
# 【反例3】
def test():
RuntimeError = MyException # 覆盖了内置作用域中的RuntimeError
if condition_not_met:
raise RuntimeError
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-05 17:51:34

<a id="rule-64"></a>

### 64. SecH_GTS_PYTHON_CHECK_file_size

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_PYTHON_CHECK_file_size |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

检测python中使用file_size来获取压缩包大小

**修复建议**

不要使用file_size，该大小可以伪造，需要边解压边统计大小来判断，使用实际解压的大小来限制

**正确示例**

```text
```
import os
import zipfile
def check_zip(zip_file):
num1024 = 1024
num5120 = 5120
file_size = os.path.getsize(zip_file)
file_size = file_size/(num1024*num1024)
if file_size>num1024:
return False
is_zip=zipfile.is_zipfile(zip_file)
print(is_zip)
if is_zip:
zip_f=zipfile.ZipFile(zip_file,'r')
else:
return False
count = 0
total_size = 0
for i in zip_f.infolist():
size = zip_f.read(i).__sizeof__()
if size > 0:
count += 1
total_size += size//num1024//num1024
if count > num1024:
return False
if total_size > num5120:
return False
return True
```
```

**错误示例**

```text
```
import os
import zipfile
def check_zip(zip_file):
file_size = "123"
num1024 = 1024
num32 = 32
num5120 = 5120
file_size = os.path.getsize(zip_file)
file_size = file_size/(num1024*num1024)
if file_size>num1024:
return False
is_zip=zip_file.is_zipfile(zip_file)
if is_zip:
zip_f=zipfile.zipfile(zip_file,'r')
else:
return False
count = 0
total_size = 0
for i in zip_f.infolist():
if i.external_attr == num32 or i.file_size > 0:
count += 1
total_size += i.file_size/num1024/num1024
if count > num1024:
return False
if total_size > num5120:
return False
return True
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:30:26

<a id="rule-65"></a>

### 65. SecH_GTS_PYTHON_HardCode_Tel_No

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_PYTHON_HardCode_Tel_No |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

在python注释中匹配座机号码和手机号码

**修复建议**

将座机号码和手机号码从python注释中去掉

**正确示例**

不涉及，禁止在python代码的注释中有座机号码和手机号码

**错误示例**

不涉及，python代码的注释中有座机号码和手机号码

**参考信息**

- 最新更新时间：2026-01-21 11:40:23

<a id="rule-66"></a>

### 66. SecH_GTS_PYTHON_check_SSLv23

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_PYTHON_check_SSLv23 |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

检查SSL使用版本是否正确，如果使用SSLContext函数，并且参数值包含PROTOCOL_SSLv23字符，则告警

**修复建议**

不涉及，按规范要求，按照业务要求请使用使用tls1.2及以上版本

**正确示例**

不涉及，按规范要求，按照业务要求请使用使用tls1.2及以上版本

**错误示例**

不涉及，使用SSL 2.0、3.0版本则认为违反规范要求

**参考信息**

- 最新更新时间：2026-01-21 11:42:01

<a id="rule-67"></a>

### 67. SecPy_VF_Mail_Command_Injection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Mail_Command_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

执行來自不可信来源的 SMTP 命令会导致 SMTP 服务器执行恶意命令。

**修复建议**

传入外部数据之前需要对其进行校验

**正确示例**

```text
def verify(data):
if data == 'safe':
return data.replace('\n', '_').replace('\r', '_')
else:
...
def good_case():
b = input()
b_verify =verify(b)
smtplib.SMTP.docmd("ehlo",b_verify)
```

**错误示例**

```text
import smtplib
def bad():
b = input()
# POTENTIAL FLAW
smtplib.SMTP.docmd("ehlo",b)
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-04-27 11:54:38

<a id="rule-68"></a>

### 68. SecPy_VF_Memcached_Injection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Memcached_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

使用来自不受信任来源的输入来调用Memcached操作可能会使攻击者在Memcached缓存中引入新的键/值对。

**修复建议**

传入外部数据之前需要对其进行校验

**正确示例**

```text
def verify(data):
if data == 'safe':
return data.replace('\n', '_').replace('\r', '_')
else:
...
def good_case():
a = input()
b = input()
c = input()
a_verify = verify(a)
b_verify = verify(b)
c_verify = verify(c)
# POTENTIAL FLAW
pylibmc.client.Client.set(a_verify, b_verify, c_verify)
```

**错误示例**

```text
import pylibmc
def bad_case():
a = input()
b = input()
c = input()
# POTENTIAL FLAW
pylibmc.client.Client.set(a, b, c)
# POTENTIAL FLAW
pylibmc.client.Client.add(a, b, c)
if __name__ == '__main__':
bad_case()
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-04-27 11:54:39

<a id="rule-69"></a>

### 69. SecPy_VF_Open_Redirect

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Open_Redirect |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

检测Open Redirect（开放式重定向）漏洞，覆盖场景与fortify强制规则 Open Redirect 相同

**修复建议**

传入外部数据之前需要对其进行校验

**正确示例**

```text
def verify(data):
if data == 'safe':
return data.replace('\n', '_').replace('\r', '_')
else:
...
def good_case():
a = input()
a_verify = verify(a)
django.shortcuts.redirect(a_verify)
```

**错误示例**

```text
import django
def bad():
a = input()
# POTENTIAL FLAW
django.shortcuts.redirect(a)
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-04-27 11:54:41

<a id="rule-70"></a>

### 70. SecPy_VF_Server_Side_Request_Forgery

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Server_Side_Request_Forgery |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
SSRF（Server-Side RequestForgery, 服务端请求伪造）利用漏洞可以发起网络请求来攻击内网服务，故在server端发起用户输入的请求前，都要严格校验用户输入的内容。当攻击者可以影响应用程序服务器建立的网络连接时，将会发生 Server-Side Request Forgery。网络连接源自于应用程序服务器内部 IP 地址，因此攻击者将可以使用此连接来避开网络控制，并扫描或攻击没有以其他方式暴露的内部资源。
```

**修复建议**

传入外部数据之前需要对其进行校验

**正确示例**

```text
def verify(data):
if data == 'safe':
return data.replace('\n', '_').replace('\r', '_')
else:
...
a_verify = verify(a)
def good_case():
a = input()
a_verify = verify(a)
socket.create_connection(address=a_verify)
```

**错误示例**

```text
def try_connect(ip_address):
try:
port = get_mgmt_ir_port()
if common.check_ip(ip_address) and re.match('^[0-9]+$', port):
sock = socket.create_connection((ip_address, port), 20)
sock.close()
return True
else:
return False
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-04-27 11:54:35

<a id="rule-71"></a>

### 71. SecPy_VF_Server_Side_Template_Injection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_Server_Side_Template_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

检测服务器模板注入，覆盖场景与fortify规则 Server Side Template Injection 相同

**修复建议**

传入外部数据之前需要对其进行校验

**正确示例**

```text
def verify(data):
if data == 'safe':
return data.replace('\n', '_').replace('\r', '_')
else:
...
def good_case():
a = input()
b = input()
a_verify = verify(a)
b_verify = verify(b)
django.template.engine.Engine.from_string(a_verify, b_verify)
```

**错误示例**

```text
import django
def bad_case():
a = input()
b = input()
# POTENTIAL FLAW
django.template.engine.Engine.from_string(a, b)
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-72"></a>

### 72. SecPy_VF_XSLT_Injection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | PYTHON |
| 标签 | SecBrella |
| 关联工具规则 | SecPy_VF_XSLT_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

检测XSLT(扩展样式表转换语言)漏洞,覆盖场景与fortify强制规则 XSLT Injection 相同

**修复建议**

传入外部数据之前需要对其进行校验

**正确示例**

```text
import lxml
def verify(x):
if x == 'clean data':
return x.replace('\n', '_').replace('\r', '_');
else:
return None
def good_case():
a = input()
b = input()
verify_a = verify(a)
verify_b = verify(b)
lxml.etree._ElementTree.xslt(verify_a, verify_b)
```

**错误示例**

```text
import lxml
def bad_case():
a = input()
b = input()
# POTENTIAL FLAW
lxml.etree._ElementTree.xslt(a, b)
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-04-27 11:54:46

<a id="rule-73"></a>

### 73. duplication_file[PYTHON]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | cmetrics |
| 关联工具规则 | duplication_file |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
重复文件：计算所有文件的md5值，如果md5值一样，则认为是重复文件。
__init__.py是常见的代码场景, 会导致重复文件问题, 由于度量与业界保持一致, 工具并未排除此类场景, 开发人员可以予以屏蔽。
```

**修复建议**

高重复率意味着相同或类似功能的单板、模块或功能单元缺乏抽象和管理。代码重构，提升代码的抽象和管理可以减少重复文件。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-11-04 11:10:08

<a id="rule-74"></a>

### 74. huge_cca_cyclomatic_complexity[PYTHON]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | PYTHON |
| 标签 | cmetrics |
| 关联工具规则 | huge_cca_cyclomatic_complexity |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

超大CCA圈复杂度：CCA圈复杂度超过阈值的函数，超大CCA圈复杂度的阈值可以在CodeCheck上进行配置。对于圈复杂度和CCA圈复杂度，是有区别的：一般的圈复杂度，switch里面有多少case，圈复杂度就加多少，CCA圈复杂度：不管switch里面有多少个case，圈复杂度只加1。

**修复建议**

降低函数复杂度，可以解决超大CCA圈复杂度问题，牵引良好设计。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-08-29 14:53:34

<a id="rule-75"></a>

### 75. huge_folder[PYTHON]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | cmetrics |
| 关联工具规则 | huge_folder |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

超大目录：本目录节点下子目录数和文件数之和超过阈值的目录（注意：不计算该目录节点下子目录下更深层次的子目录数和文件数）。超大目录的阈值可以在CodeCheck上进行配置。

**修复建议**

设置合理的目录结构，也可以提升易读性，可维护性。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-11-04 11:10:08

<a id="rule-76"></a>

### 76. huge_method[PYTHON]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | PYTHON |
| 标签 | cmetrics |
| 关联工具规则 | huge_method |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

超大函数：代码行超过阈值的函数，超大函数的阈值可以在CodeCheck上进行配置。

**修复建议**

降低函数复杂度，可以解决超大函数问题，牵引良好设计。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-11-04 11:10:08

<a id="rule-77"></a>

### 77. huge_non_headerfile[PYTHON]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | cmetrics |
| 关联工具规则 | huge_non_headerfile |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

超大源文件：代码行超过阈值的源文件（不包含头文件），超大源文件的阈值可以在CodeCheck上进行配置。

**修复建议**

超大源文件意味着代码架构管理不清晰，可以通过代码重构等方式消减。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-11-27 17:54:00

<a id="rule-78"></a>

### 78. redundant_code[PYTHON]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | cmetrics |
| 关联工具规则 | redundant_code |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
冗余代码：Cmetrics通过正则表达式找出的#if 0代码和注释中的代码。
如果在注释中出现以下表达，则认为是冗余代码：
```
return;
return ......;
};
}......;
=......;
......(......);
......[......];
if/else/else if/for/while/switch/case/catch/finally/try/default等表达式。
```
冗余代码极小概率会存在漏报的情况，某些情况下连续单词组可以认为是注释，也可以认为是代码，工具无法从语义上识别出是否为冗余代码，此情况本着允许漏报，不许误报的原则，不予预警。
```

**修复建议**

删除冗余代码

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-11-04 11:10:00

<a id="rule-79"></a>

### 79. warning_suppression[PYTHON]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | PYTHON |
| 标签 | cmetrics |
| 关联工具规则 | warning_suppression |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
告警抑制：通过在代码中增加注释的方式，使代码静态检查不产生告警。这种方式污染了源码，且告警处理不可追溯，可能导致问题遗漏。
通过正则表达式匹配代码中的告警抑制声明，在代码中出现如下表达，则认为是告警抑制声明：
// lint ......
/* lint ......
// coverity [......]
/* coverity [......]
#pragma ... diagnostic ignored
#pragma ... diagnostic warning
#pragma warning(disable)
@......SuppressWarnings
```

**修复建议**

删除告警抑制代码。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-11-04 11:10:08

