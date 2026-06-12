# 运营商服务与软件 JavaScript 3.0 代码审查规则
> 本文件由一个公司 电子表格 规则集生成，并保留源文件中的全部规则；不要把它当作最小子集使用。
## 来源
- 来源 电子表格：`运营商服务与软件JAVASCRIPT3.0(运营商服务与软件JAVASCRIPT3.0最小规则集(有构建),unsafe_algorithm).xlsx`
- 来源目录：原始规则目录
- 提取规则数：102
- 问题级别分布：一般: 37, 严重: 31, 提示: 25, 信息: 6, 致命: 3
- 语言分布：JAVASCRIPT: 102
- 规则类型分布：通用规范规则类: 27, 通用规范建议类: 25, 安全规范规则类: 22, 安全类（非编程规范）: 19, 代码坏味道类: 9

## 代码审查使用方式
- 仅在审查触及对应语言、构建选项或安全面的变更时加载本规则集。
- 只有当变更差异 或必要的相邻代码中存在明确证据时，才输出规则违反项。
- 输出 合入请求审查问题时，需要包含来源规则名称和关联工具规则。
- `严重` 默认视为阻塞问题，除非已有明确例外说明；`一般` 默认合入前修复；`提示` 默认作为建议项，除非项目策略另有要求。

## 规则索引

| 序号 | 规则 | 级别 | 语言 | 工具规则 | 类型 |
|---|---|---|---|---|---|
| 1 | [Cookie Security: Cookie not Sent Over SSL](#rule-1) | 严重 | JAVASCRIPT | Cookie Security: Cookie not Sent Over SSL | 安全类（非编程规范） |
| 2 | [Cross-Session Contamination](#rule-2) | 严重 | JAVASCRIPT | Cross-Session Contamination | 安全类（非编程规范） |
| 3 | [G.AOD.01 禁止使用 eval()](#rule-3) | 一般 | JAVASCRIPT | no_eval | 通用规范规则类 |
| 4 | [G.AOD.02 禁止使用隐式的 eval()](#rule-4) | 一般 | JAVASCRIPT | no_implied_eval | 通用规范规则类 |
| 5 | [G.AOD.03 尽量避免使用console](#rule-5) | 提示 | JAVASCRIPT | no_console | 通用规范建议类 |
| 6 | [G.AOD.04 禁止使用 alert](#rule-6) | 一般 | JAVASCRIPT | no_alert | 通用规范规则类 |
| 7 | [G.AOD.05 禁止使用 debugger](#rule-7) | 一般 | JAVASCRIPT | no_debugger | 通用规范规则类 |
| 8 | [G.CMT.02 注释符和注释内容之间留有一个空格](#rule-8) | 提示 | JAVASCRIPT | spaced_comment | 通用规范建议类 |
| 9 | [G.CMT.03 正式交付给客户的代码不应包含 TODO/FIXME 注释](#rule-9) | 提示 | JAVASCRIPT | no_warning_comments | 通用规范建议类 |
| 10 | [G.CORS.01 使用postMessage方法实现跨域时，应严格校验Origin](#rule-10) | 一般 | JAVASCRIPT | SecJS_no_wildcard_postmessage | 安全规范规则类 |
| 11 | [G.CTL.01 每个 switch 语句都应包含一个 default 分支](#rule-11) | 一般 | JAVASCRIPT | default_case | 通用规范规则类 |
| 12 | [G.CTL.02 在 switch 语句的每一个有内容的 case 中都应放置一条 break 语句](#rule-12) | 一般 | JAVASCRIPT | no_fallthrough | 通用规范规则类 |
| 13 | [G.CTL.03 case 语句中需要词法声明时, 大括号{}不能省略](#rule-13) | 一般 | JAVASCRIPT | no_case_declarations | 通用规范规则类 |
| 14 | [G.CTL.04 含else if分支的条件判断应在最后加一个else分支](#rule-14) | 提示 | JAVASCRIPT | SecJS_must_else_end | 通用规范建议类 |
| 15 | [G.CTL.06 不要在控制性条件表达式中执行赋值操作](#rule-15) | 一般 | JAVASCRIPT | no_cond_assign | 通用规范规则类 |
| 16 | [G.DCL.01 声明变量时要求使用 const 或 let 而不是 var（ECMAScript 6）](#rule-16) | 一般 | JAVASCRIPT | no_var | 通用规范规则类 |
| 17 | [G.DCL.03 每个语句只声明一个变量](#rule-17) | 一般 | JAVASCRIPT | one_var | 通用规范规则类 |
| 18 | [G.DCL.04 禁止连续赋值](#rule-18) | 一般 | JAVASCRIPT | no_multi_assign | 通用规范规则类 |
| 19 | [G.DCL.05 不要使用 undefined 初始化变量](#rule-19) | 一般 | JAVASCRIPT | no_undef_init | 通用规范规则类 |
| 20 | [G.DCL.06 使用字面量风格的声明](#rule-20) | 一般 | JAVASCRIPT | no_new_wrappers,no_array_constructor,no_new_object | 通用规范规则类 |
| 21 | [G.DCL.07 避免当前作用域中的变量覆盖更外层作用域的变量](#rule-21) | 提示 | JAVASCRIPT | no_shadow | 通用规范建议类 |
| 22 | [G.EDV.01 禁止直接对不可信的JS对象进行序列化](#rule-22) | 严重 | JAVASCRIPT | SecJS_JSON_Injection | 安全规范规则类 |
| 23 | [G.EDV.02 禁止跳转至不可信地址](#rule-23) | 严重 | JAVASCRIPT | SecJS_Open_Redirect | 安全规范规则类 |
| 24 | [G.EDV.03 若输出到客户端或者解释器的数据来自不可信的数据源，则须对该数据进行相应的编码或转义](#rule-24) | 严重 | JAVASCRIPT | SecJS_Cross_Site_Scripting | 安全规范规则类 |
| 25 | [G.EDV.04 正则表达式要尽量简单，防止ReDos攻击](#rule-25) | 严重 | JAVASCRIPT | SecJS_denial_of_service | 安全规范规则类 |
| 26 | [G.EDV.05 禁止将用户输入的数据用于动态创建的模板](#rule-26) | 严重 | JAVASCRIPT | SecJS_Client_Side_Template_Injection | 安全规范规则类 |
| 27 | [G.ERR.02 应使用 Error 对象作为 Promise 拒绝的原因](#rule-27) | 提示 | JAVASCRIPT | prefer_promise_reject_errors | 通用规范建议类 |
| 28 | [G.ERR.03 不要使用return、break、continue或抛出异常使finally块非正常结束](#rule-28) | 一般 | JAVASCRIPT | no_unsafe_finally | 通用规范规则类 |
| 29 | [G.ERR.04 禁止通过异常泄露敏感数据](#rule-29) | 严重 | JAVASCRIPT | SecJS_System_Information_Leak | 安全规范规则类 |
| 30 | [G.EXP.02 判断相等时应使用 === 和 !== ，而不是 == 和 !=](#rule-30) | 一般 | JAVASCRIPT | eqeqeq | 通用规范规则类 |
| 31 | [G.EXP.03 禁止使用嵌套的三元表达式](#rule-31) | 一般 | JAVASCRIPT | no_nested_ternary | 通用规范规则类 |
| 32 | [G.FMT.01 使用空格进行缩进](#rule-32) | 提示 | JAVASCRIPT | SecJS_indent | 通用规范建议类 |
| 33 | [G.FMT.02 行宽不宜过长](#rule-33) | 提示 | JAVASCRIPT | max_len | 通用规范建议类 |
| 34 | [G.FMT.04 对象字面量属性超过 4 个, 需要都换行](#rule-34) | 提示 | JAVASCRIPT | SecJS_max_object_prop_number_one_line | 通用规范建议类 |
| 35 | [G.FMT.05 链式调用对象方法时，一行最多调用 4 次，否则需要换行](#rule-35) | 提示 | JAVASCRIPT | newline_per_chained_call | 通用规范建议类 |
| 36 | [G.FMT.08 用空格突出关键字和重要信息](#rule-36) | 提示 | JAVASCRIPT | space_infix_ops,space_before_blocks,func_call_spacing,array_bracket_spacing,no_multi_spaces,comma_spacing,template_curly_spacing,semi_spacing,keyword_spacing,space_before_function_paren | 通用规范建议类 |
| 37 | [G.FMT.09 建议 if、for、do、while 等语句的执行体加大括号 {}](#rule-37) | 提示 | JAVASCRIPT | curly | 通用规范建议类 |
| 38 | [G.FMT.12 每句代码后加分号](#rule-38) | 提示 | JAVASCRIPT | SecJS_semi | 通用规范建议类 |
| 39 | [G.MET.03 块语句的最大可嵌套深度不要超过 4 层](#rule-39) | 提示 | JAVASCRIPT | max_depth | 通用规范建议类 |
| 40 | [G.MET.04 回调函数嵌套的层数不超过 4 层](#rule-40) | 提示 | JAVASCRIPT | max_nested_callbacks | 通用规范建议类 |
| 41 | [G.MET.06 始终将默认参数放在最后](#rule-41) | 提示 | JAVASCRIPT | default_param_last | 通用规范建议类 |
| 42 | [G.MET.07 建议使用一致的 return 语句](#rule-42) | 一般 | JAVASCRIPT | SecJS_consistent_return | 通用规范规则类 |
| 43 | [G.MET.08 不要动态创建函数](#rule-43) | 一般 | JAVASCRIPT | no_new_func | 安全规范规则类 |
| 44 | [G.MET.09 不要给函数的入参重新赋值](#rule-44) | 提示 | JAVASCRIPT | no_param_reassign | 通用规范建议类 |
| 45 | [G.MET.10 不要使用 arguments，可以选择 rest 语法替代](#rule-45) | 一般 | JAVASCRIPT | prefer_rest_params | 通用规范规则类 |
| 46 | [G.MET.12 用到匿名函数时优先使用箭头函数，替代保存this引用的方式](#rule-46) | 一般 | JAVASCRIPT | prefer_arrow_callback | 通用规范规则类 |
| 47 | [G.MOD.01 需要导出的变量必须是不可变的类型](#rule-47) | 一般 | JAVASCRIPT | SecJS_export_variable | 通用规范规则类 |
| 48 | [G.NAM.02 函数应采用小驼峰风格命名](#rule-48) | 提示 | JAVASCRIPT | SecJS_fun_named | 通用规范建议类 |
| 49 | [G.NAM.04 避免使用否定的布尔变量名](#rule-49) | 提示 | JAVASCRIPT | SecJS_nouse_deny_bool_var | 通用规范建议类 |
| 50 | [G.NAM.07 使用统一的文件命名风格，文件扩展名应小写](#rule-50) | 提示 | JAVASCRIPT | SecJS_filename_pattern | 通用规范建议类 |
| 51 | [G.NOD.01 禁止直接使用外部数据拼接命令](#rule-51) | 严重 | JAVASCRIPT | SecJS_Command_Injection | 安全规范规则类 |
| 52 | [G.NOD.02 使用外部数据构造的文件路径在校验前必须对文件路径进行规范化处理](#rule-52) | 严重 | JAVASCRIPT | SecJS_Path_Manipulation | 安全规范规则类 |
| 53 | [G.NOD.03 禁止直接使用外部数据来拼接SQL语句](#rule-53) | 严重 | JAVASCRIPT | SecJS_SQL_Injection | 安全规范规则类 |
| 54 | [G.NOD.04 解压文件必须进行安全检查](#rule-54) | 一般 | JAVASCRIPT | SecJS_decompress_file_secure_check | 安全规范规则类 |
| 55 | [G.NOD.06 禁止直接使用外部数据记录日志](#rule-55) | 严重 | JAVASCRIPT | SecJS_Log_Forging_Debug,SecJS_Log_Forging | 安全规范规则类 |
| 56 | [G.OBJ.01 通过 new 调用构造函数时，应始终使用圆括号](#rule-56) | 提示 | JAVASCRIPT | new_parens | 通用规范建议类 |
| 57 | [G.OBJ.05 禁止在对象实例上直接使用 hasOwnProperty、isPrototypeOf、propertyIsEnumerable方法](#rule-57) | 一般 | JAVASCRIPT | no_prototype_builtins | 安全规范规则类 |
| 58 | [G.OBJ.06 需要约束 for-in](#rule-58) | 一般 | JAVASCRIPT | guard_for_in | 通用规范规则类 |
| 59 | [G.OBJ.07 不要修改内置对象的原型，或向原型添加方法](#rule-59) | 一般 | JAVASCRIPT | no_extend_native | 安全规范规则类 |
| 60 | [G.OTH.01 安全场景下必须使用密码学意义上的安全随机数](#rule-60) | 严重 | JAVASCRIPT | SecJS_no_insecure_randomness | 安全规范规则类 |
| 61 | [G.OTH.03 不用的代码段直接删除，不要注释掉](#rule-61) | 一般 | JAVASCRIPT | SecJS_no_commented_code | 安全规范规则类 |
| 62 | [G.OTH.04 正式发布的代码及注释内容不应包含开发者个人信息](#rule-62) | 严重 | JAVASCRIPT | SecJS_no_personal_info_in_comment | 安全规范规则类 |
| 63 | [G.OTH.05 禁止代码中包含公网地址](#rule-63) | 提示 | JAVASCRIPT | SecJS_no_public_url | 通用规范建议类 |
| 64 | [G.OTH.06 禁止在用户界面、日志中暴露不必要信息](#rule-64) | 严重 | JAVASCRIPT | SecJS_no_exposure_of_unnecessary_info,SecJS_no_sensitive_info_in_log | 安全规范规则类 |
| 65 | [G.TYP.01 禁止省略浮点数小数点前后的 0](#rule-65) | 一般 | JAVASCRIPT | no_floating_decimal | 通用规范规则类 |
| 66 | [G.TYP.02 判断变量是否为NaN时必须使用 isNaN() 方法](#rule-66) | 一般 | JAVASCRIPT | use_isnan | 通用规范规则类 |
| 67 | [G.TYP.03 浮点型数据判断相等不要直接使用== 或===](#rule-67) | 一般 | JAVASCRIPT | SecJS_float_equal | 通用规范规则类 |
| 68 | [G.TYP.05 使用模板字符串（`）实现字符串拼接](#rule-68) | 提示 | JAVASCRIPT | prefer_template | 通用规范建议类 |
| 69 | [G.TYP.06 不应使用字符串的行连续符号](#rule-69) | 提示 | JAVASCRIPT | no_multi_str | 通用规范建议类 |
| 70 | [G.TYP.07 不要在数组上定义或使用非数字属性（length 除外）](#rule-70) | 一般 | JAVASCRIPT | SecJS_array_property | 通用规范规则类 |
| 71 | [G.TYP.09 不要在数组遍历中对其进行元素的 remove/add 操作](#rule-71) | 一般 | JAVASCRIPT | SecJS_array_loop_operate | 通用规范规则类 |
| 72 | [G.TYP.11 使用展开语法 … 或concat复制数组](#rule-72) | 提示 | JAVASCRIPT | SecJS_array_copy | 通用规范建议类 |
| 73 | [G.TYP.14 使用显式的类型转换](#rule-73) | 一般 | JAVASCRIPT | no_implicit_coercion | 通用规范规则类 |
| 74 | [G.WSQ.01 禁止在 Web SQL database和Indexed database数据库中存储敏感数据](#rule-74) | 致命 | JAVASCRIPT | SecJS_no_web_sql_database | 安全规范规则类 |
| 75 | [G.WST.01 禁止在 localStorage和sessionStorage中存储敏感数据](#rule-75) | 严重 | JAVASCRIPT | SecJS_no_sensitive_local_storage | 安全规范规则类 |
| 76 | [G.WST.02 如果数据仅需要临时存储在客户端，使用非持久性会话cookie或者sessionStorage而不是localStorage](#rule-76) | 一般 | JAVASCRIPT | SecJS_no_staged_data_local_storage | 安全规范规则类 |
| 77 | [HW_GTS_JS_HardCode_email_EmployeeId_DTSId](#rule-77) | 致命 | JAVASCRIPT | HW_GTS_JS_HardCode_email_EmployeeId_DTSId | 安全类（非编程规范） |
| 78 | [HW_GTS_JS_IP_Hardcode](#rule-78) | 致命 | JAVASCRIPT | HW_GTS_JS_IP_Hardcode | 安全类（非编程规范） |
| 79 | [Insecure SSL: Server Identity Verification Disabled](#rule-79) | 严重 | JAVASCRIPT | Insecure SSL: Server Identity Verification Disabled | 安全类（非编程规范） |
| 80 | [Insecure Transport](#rule-80) | 严重 | JAVASCRIPT | Insecure Transport | 安全类（非编程规范） |
| 81 | [Insecure Transport: Weak SSL Protocol](#rule-81) | 严重 | JAVASCRIPT | Insecure Transport: Weak SSL Protocol | 安全类（非编程规范） |
| 82 | [JSON Injection](#rule-82) | 严重 | JAVASCRIPT | JSON Injection | 安全类（非编程规范） |
| 83 | [Key Management: Empty Encryption Key](#rule-83) | 严重 | JAVASCRIPT | Key Management: Empty Encryption Key | 安全类（非编程规范） |
| 84 | [Key Management: Hardcoded Encryption Key](#rule-84) | 严重 | JAVASCRIPT | Key Management: Hardcoded Encryption Key | 安全类（非编程规范） |
| 85 | [Key Management: Null Encryption Key](#rule-85) | 严重 | JAVASCRIPT | Key Management: Null Encryption Key | 安全类（非编程规范） |
| 86 | [Open Redirect](#rule-86) | 严重 | JAVASCRIPT | Open Redirect | 安全类（非编程规范） |
| 87 | [Password Management: Hardcoded Password](#rule-87) | 严重 | JAVASCRIPT | Password Management: Hardcoded Password | 安全类（非编程规范） |
| 88 | [Password Management: Weak Cryptography](#rule-88) | 严重 | JAVASCRIPT | Password Management: Weak Cryptography | 安全类（非编程规范） |
| 89 | [Path Manipulation](#rule-89) | 严重 | JAVASCRIPT | Path Manipulation | 安全类（非编程规范） |
| 90 | [SecH_GTS_JS_HardCode_Email_EmployeeId_DTSId](#rule-90) | 严重 | JAVASCRIPT | SecH_GTS_JS_HardCode_Email_EmployeeId_DTSId | 安全类（非编程规范） |
| 91 | [SecH_GTS_JS_IP_Hardcode](#rule-91) | 严重 | JAVASCRIPT | SecH_GTS_JS_IP_Hardcode | 安全类（非编程规范） |
| 92 | [Weak Cryptographic Hash](#rule-92) | 严重 | JAVASCRIPT | Weak Cryptographic Hash | 安全类（非编程规范） |
| 93 | [Weak Encryption: Insufficient Key Size](#rule-93) | 严重 | JAVASCRIPT | Weak Encryption: Insufficient Key Size | 安全类（非编程规范） |
| 94 | [huge_cyclomatic_complexity[js]](#rule-94) | 信息 | JAVASCRIPT | huge_cyclomatic_complexity | 代码坏味道类 |
| 95 | [huge_cyclomatic_complexity_ratio[js]](#rule-95) | 信息 | JAVASCRIPT | huge_cyclomatic_complexity_ratio | 代码坏味道类 |
| 96 | [huge_folder[js]](#rule-96) | 信息 | JAVASCRIPT | huge_folder | 代码坏味道类 |
| 97 | [huge_folder_ratio[js]](#rule-97) | 一般 | JAVASCRIPT | huge_folder_ratio | 代码坏味道类 |
| 98 | [huge_method[js]](#rule-98) | 信息 | JAVASCRIPT | huge_method | 代码坏味道类 |
| 99 | [huge_method_ratio[js]](#rule-99) | 信息 | JAVASCRIPT | huge_method_ratio | 代码坏味道类 |
| 100 | [huge_non_headerfile[js]](#rule-100) | 信息 | JAVASCRIPT | huge_non_headerfile | 代码坏味道类 |
| 101 | [huge_non_headerfile_ratio[js]](#rule-101) | 一般 | JAVASCRIPT | huge_non_headerfile_ratio | 代码坏味道类 |
| 102 | [redundant_code_kloc[js]](#rule-102) | 一般 | JAVASCRIPT | redundant_code_kloc | 代码坏味道类 |

## 规则详情

<a id="rule-1"></a>

### 1. Cookie Security: Cookie not Sent Over SSL

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Cookie Security: Cookie not Sent Over SSL |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 614 |
| 预估误报率 | 20% |

**审查要点**

创建了 cookie，但未将 secure 标记设置为 true。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-2"></a>

### 2. Cross-Session Contamination

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Cross-Session Contamination |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 501 |
| 预估误报率 | 20% |

**审查要点**

在 localStorage 和 sessionStorage 之间传输值会不知不觉地暴露敏感信息。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-3"></a>

### 3. G.AOD.01 禁止使用 eval()

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_eval |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

使用eval()会让程序比较混乱，导致可读性较差。

**修复建议**

禁止使用 eval()

**正确示例**

```text
const feed = '{ "name": "Alice", "id": 31502 }';
const userInfo = JSON.parse(feed);
const id = userInfo.id;
```

**错误示例**

console.log(eval({ a:2 })); // 输出[object Object]

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-21 17:57:33

<a id="rule-4"></a>

### 4. G.AOD.02 禁止使用隐式的 eval()

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_implied_eval |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
在 JavaScript 中避免使用 eval() 被认为是一个很好的实践。有一些其它方式，通过传递一个字符串，并将它解析为JavaScript代码，也有类似的问题。首当其冲的就是 setTimeout()、setInterval() 或者 execScript() (仅限IE浏览器)，它们都可以接受一个 JavaScript 字符串代码作为第一个参数。
```

**修复建议**

禁止使用隐式的 eval()

**正确示例**

const val: unknown = value;

**错误示例**

setTimeout('alert("Hi!");', 100);

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-5"></a>

### 5. G.AOD.03 尽量避免使用console

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_console |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 20% |

**审查要点**

在 JavaScript，虽然console 被设计为在浏览器中执行的，但避免使用 console 的方法被认为是一种最佳实践。这样的消息被认为是用于调试的，因此不适合输出到客户端。通常，在发布正式交付的产品之前尽量删除不必要的 console 调用。

**修复建议**

尽量避免使用console

**正确示例**

Console.log("Hello world!");

**错误示例**

console.log("Log a debug level message.");

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:46

<a id="rule-6"></a>

### 6. G.AOD.04 禁止使用 alert

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_alert |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

JavaScript 的 alert、confirm 和 prompt 被广泛认为是突兀的 UI 元素，应该被一个更合适的自定义的 UI 界面代替。此外，alert 经常被用于调试代码，部署到生产环境之前应该全部删除。

**修复建议**

禁止使用 alert

**正确示例**

customAlert("Something happened!");

**错误示例**

alert("here!");

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:46

<a id="rule-7"></a>

### 7. G.AOD.05 禁止使用 debugger

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_debugger |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

debugger 语句用于告诉 JavaScript 执行环境停止执行并在代码的当前位置启动调试器。随着现代调试和开发工具的出现，使用调试器已不是最佳实践。部署到生产环境之前应该删除。

**修复建议**

禁止使用 debugger

**正确示例**

```text
function isTruthy(x) {
return Boolean(x); // set a breakpoint at this line
}
```

**错误示例**

```text
function isTruthy(x) {
debugger;
return Boolean(x);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:46

<a id="rule-8"></a>

### 8. G.CMT.02 注释符和注释内容之间留有一个空格

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | spaced_comment |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
注释符与注释内容之间留有一个空格，这会让注释看起来清晰美观。
注释符包括//和/* */。
```

**修复建议**

注释符与注释内容之间留有一个空格

**正确示例**

bar()\n\n/** block block block\n * block \n */\n\nvar a = 1;

**错误示例**

bar()\n/** block block block\n * block \n */\nvar a = 1;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:15

<a id="rule-9"></a>

### 9. G.CMT.03 正式交付给客户的代码不应包含 TODO/FIXME 注释

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_warning_comments |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

在版本开发阶段，可以使用此类注释用于突出标注；不过在代码交付给客户之前，应该全部处理掉。

**修复建议**

在版本开发阶段，可以使用此类注释用于突出标注；不过在代码交付给客户之前，应该全部处理掉。

**正确示例**

// any comment

**错误示例**

// fixme

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-10"></a>

### 10. G.CORS.01 使用postMessage方法实现跨域时，应严格校验Origin

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_wildcard_postmessage |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
HTML5增加了window.postMessage(message,targetOrigin,[transfer])方法，该方法可以实现跨域通信，以允许不同来源的Window对象之间进行消息交换。因为任何窗口都可以从其他窗口发送/接收消息，所以验证发送者/接收者的来源很重要，如果目标来源设置的过于宽松，恶意脚本就能趁机采用不当方式与受害者窗口进行通信，从而导致发送欺骗、数据被盗、转发及其他攻击。
因此，无论是作为消息的发送方还是接受方，都应该对目标源进行最小化校验，推荐使用白名单的方式，禁止使用indexOf()来校验目标源。
```

**修复建议**

谨慎使用postMessage方法实现跨域

**正确示例**

```text
function good001(){
let url=document.getElementById("targetURL").value
if( whitelistVerification(url) ){
window.postMessage("wwx1134524",url);
}
}
```

**错误示例**

```text
function bad001(){
window.postMessage("wwx1134524","*");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:47

<a id="rule-11"></a>

### 11. G.CTL.01 每个 switch 语句都应包含一个 default 分支

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | default_case |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

开发人员可能会忘记定义默认分支而导致程序发生错误，所以在 每个switch 语句中都应包含一个 default 分支。或者也可以在最后一个 case 分支下，使用 // no default 来表明此处不需要 default 分支。注释可以任何形式出现，比如 // No Default。

**修复建议**

每个 switch 语句都应包含一个 default 分支

**正确示例**

```text
switch (num) {
case 1:
...
break;
default:
...
break;
}
```

**错误示例**

```text
switch (num) {
case 1:
...
break;
case 2:
...
break;
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:32

<a id="rule-12"></a>

### 12. G.CTL.02 在 switch 语句的每一个有内容的 case 中都应放置一条 break 语句

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_fallthrough |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

在 switch 语句的每一个有内容的 case 中都应放置一条 break 语句，遗漏break，可能导致程序误入下一个case分支，执行了预期之外的代码，导致异常。而且，不推荐做出有意不写break的设计。

**修复建议**

在 switch 语句的每一个有内容的 case 中都应放置一条 break 语句

**正确示例**

function getGreetings(): string[] {

**错误示例**

return ['hello', 'world'];

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:33

<a id="rule-13"></a>

### 13. G.CTL.03 case 语句中需要词法声明时, 大括号{}不能省略

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_case_declarations |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

词法声明在整个 switch 语句块中是可见的，但是它只有在运行到它定义的 case 语句时，才会进行初始化操作。为了保证词法声明语句只在当前 case 语句中有效，应该将语句包裹在块中。

**修复建议**

语句中需要词法声明时, 大括号{}不能省略

**正确示例**

```text
const num = 0;
switch (foo) {
// 下面的case子句使用括号包装成块
case 1: {
let age = 1;
break;
}
case 2: {
const bar = 2;
break;
}
default: {
class ClassA {}
}
}
```

**错误示例**

```text
switch (foo) {
case 1:
let age = 1;
break;
case 2:
const bar = 2;
break;
default:
class ClassA {}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-14"></a>

### 14. G.CTL.04 含else if分支的条件判断应在最后加一个else分支

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_must_else_end |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

多个else if条件组合的判断逻辑，往往会出现开发人员忽略的分支，需要设置else默认操作(类似中switch-case语句要有default分支) 。

**修复建议**

if-else if（有多个 else if）类型的条件判断，最后应包含一个 else分支

**正确示例**

```text
if (employee.age > 65) {
...
} else if (employee.age < 65) {
...
} else if (employee.age === 65) {
...
} else {
...
}
```

**错误示例**

```text
if (employee.age > 65) {
...
} else if (employee.age < 65) {
...
} else if (employee.age === 65) {
...
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:33

<a id="rule-15"></a>

### 15. G.CTL.06 不要在控制性条件表达式中执行赋值操作

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_cond_assign |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

说明： 不要在控制性条件表达式中执行赋值操作。

**修复建议**

在控制性条件表达式中执行赋值，常常导致意料之外的行为

**正确示例**

```text
const a = 2;
let x = a + a;
```

**错误示例**

let x = a + (a=2);

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-16"></a>

### 16. G.DCL.01 声明变量时要求使用 const 或 let 而不是 var（ECMAScript 6）

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_var |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

日志打印采用大量字符串拼接，不易维护，效率低。

**修复建议**

打印日志使用占位符，不能使用字符串拼接

**正确示例**

```text
if (log.isDebugEnabled()) {
log.debug("输入参数信息id={}", id);
}
if (log.isInfoEnabled()) {
log.info("输入参数信息id={}", id);
}
if (log.isTraceEnabled()) {
log.trace("输入参数信息id={}", id);
}
//使用占位符形式
log.debug("输入参数信息id={},obj={}", id, obj);
```

**错误示例**

log.debug("输入参数信息id=" + id + ",obj=" + obj);

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:22

<a id="rule-17"></a>

### 17. G.DCL.03 每个语句只声明一个变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | one_var |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

数据库的更新、删除返回值代表了本次操作的执行结果，需要对结果进行判断，再进行下一步的业务操作，防止异常情况发生

**修复建议**

数据库的更新、删除返回值代表了本次操作的执行结果，需要对结果进行判断，再进行下一步的业务操作

**正确示例**

```text
// 场景1
....
if (selectedVO==null || selectedVO.getDownload()==null) {
// 业务处理
....
}
....
// 场景2
....
if (selectedVO!=null && selectedVO.getDownload()==null) {
// 业务处理
....
}
....
```

**错误示例**

```text
// 场景1
....
if (selectedVO==null && selectedVO.getDownload()==null) {
// 业务处理
....
}
....
// 场景2
....
if (selectedVO!=null || selectedVO.getDownload()==null) {
// 业务处理
....
}
....
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:22

<a id="rule-18"></a>

### 18. G.DCL.04 禁止连续赋值

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_multi_assign |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
java中在使用Runtime.getRuntime().exec(command)调用系统命令后，一般会调用Process.waitFor()来等待命令执行结束，获取执行结果，但外部命令的执行存在诸多不确定性，需要指定命令执行的等待时间；
如果该外部命令的执行结果不能在指定时间内返回，需要对该进程进行关闭操作；如果不指定等待时间，当外部命令出现异常情况时，waitfor可能会发生无休止的阻塞，导致主线程卡死，如果卡死线程过多，可能会导致系统卡死
```

**修复建议**

需要指定waitfor的等待时间，如果在预期内没有结果返回，需要合理的指定处理逻辑（参考正例）

**正确示例**

```text
此正例来自GDE软件专家孙桂林的最优实践：
public void run() {
Process process = new ProcessBuilder().start();
int exit = process.waitFor(10000, TimeUnit.MILLISECONDS);
if (process.isAlive()) {
process.destroy();
Thread.sleep(1000);
}
while (process.isAlive()) {
process.destroyForcibly();
Thread.sleep(1000);
}
}
```

**错误示例**

```text
public void run() {
try {
int exit = process.waitFor();
} catch (InterruptedException ignore) {
return;
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-19"></a>

### 19. G.DCL.05 不要使用 undefined 初始化变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_undef_init |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

java.util.stream.Stream.findAny和findFirst的返回结果是Optional对象，因此需要对数据合法性进行检测，防止异常出现

**修复建议**

对java.util.stream.Stream.findAny和findFirst的返回结果合法性检测或者使用orElse方法

**正确示例**

```text
##### 场景1：对冗余括号进行格式化
- 修复示例1：使用gofmt对代码进行格式化
```go
func AddAtEnd(val int) {
n := 1
// 符合：gofmt后冗余括号消除
if val == 0 {
val = n
return
}
}
```
```

**错误示例**

```text
##### 场景1：对冗余括号进行格式化
- 错误示例：
```go
func AddAtEnd(val int) {
n := 1
// 不符合：存在冗余括号
if (val == 0) {
val = n
return
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:23

<a id="rule-20"></a>

### 20. G.DCL.06 使用字面量风格的声明

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_new_wrappers,no_array_constructor,no_new_object |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

SFTP链接未正常释放，大量session会话残留，内存缓慢泄漏直至溢出

**修复建议**

```text
在使用sftp连接时，在其使用完毕后，按先关闭sftp后关闭session的顺序关闭channel对象和session对象
另外可使用池化技术管理sftp连接，避免创建大量的sftp连接对象
```

**正确示例**

```text
public void logout() {
if (sftp != null) {
if (sftp.isConnected()) {
log.info("关闭sftp:{}", sftp);
sftp.disconnect(); // 关闭channel对象
}
try {
if (sftp.getSession() != null) {
if (sftp.getSession().isConnected()) {
log.info("关闭sftp.getSession()");
sftp.getSession().disconnect(); // 关闭session对象
}
}
} catch (Exception e) {
log.error("关闭sftp.getSession()出现异常!", e);
}
}
}
```

**错误示例**

```text
public void logout() {
if (sftp != null) {
if (sftp.isConnected()) {
log.info("关闭sftp");
sftp.disconnect(); // 需要同时关闭channel对象和session对象
}
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:23

<a id="rule-21"></a>

### 21. G.DCL.07 避免当前作用域中的变量覆盖更外层作用域的变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_shadow |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 20% |

**审查要点**

分开设置jedis键值和过期时间无法保证操作事务的原子性，如在设置键值后其它客户端对该键执行了操作可能会导致expire操作失败或无法应用到该键值。

**修复建议**

在设置键值和过期时间时要保证操作的事务的原子性。

**正确示例**

set key value expiretime

**错误示例**

```text
set key value
expire key expiretime
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-22"></a>

### 22. G.EDV.01 禁止直接对不可信的JS对象进行序列化

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecJS_JSON_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

JavaScript支持面向对象编程（OOP）技术。它有很多不同的内置对象，并允许用户创建对象。一个JS对象可以同时拥有数据和方法。如果这些数据或者方法来自用户输入，那么这个对象的序列化将产生可以被注入代码利用的安全漏洞。

**修复建议**

```text
1.不应序列化安全敏感对象。
2.验证反序列化对象的数据，过滤& < > " ' /等特殊字符。
特殊字符列表见右侧标准和【要求参考：编程规范】附录A。
```

**正确示例**

```text
##### 场景1：对象数据获取
- 修复示例：对象数据自创建
```javascript
// 创建JS对象，数据不是来自外部。
let message = {
subject: 'I am fine',
body: 'Long message here',
};
// 将JS对象进行序列化
let target = JSON.stringify(message);
```
##### 场景2：对象数据防护
- 修复示例：封装一个安全的序列化方法替换原有方法
```javascript
function safeStringify(obj) {
const seen = new WeakSet();
function sanitize(obj) {
if (obj === null) return null;
if (typeof obj !== 'object') return obj;
if (seen.has(obj)) return '[Circular]';
seen.add(obj);
if (Array.isArray(obj)) return obj.map(sanitize);
const result = {};
for (const key in obj) {
if (Object.prototype.hasOwnProperty.call(obj, key)){
result[key] = sanitize(obj[key]);
}
}
return result;
}
try {
return JSON.stringify(sanitize(obj));
} catch (err) {
return '[]';
}
}
// 替换原有未经校验直接序列化的方法
// localStorage.setItem('winwinwin', JSON.stringify(winwinwin));
localStorage.setItem('winwinwin', safeStringify(winwinwin));
```
```

**错误示例**

```text
##### 场景1：对象数据获取
- 错误示例：对象数据来自于外部输入
```javascript
// 创建JS对象，其中有2个字段需要从页面输入框获得
let message = {
from: document.getElementById('fromEmail').value,
to: document.getElementById('toEmail').value,
subject: 'I am fine',
body: 'Long message here',
showsubject: function () { document.write(this.subject) }
};
// 将JS对象进行序列化
let target = JSON.stringify(message);
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-11-10 20:23:24

<a id="rule-23"></a>

### 23. G.EDV.02 禁止跳转至不可信地址

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecJS_Open_Redirect |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

```text
当用户控制的数据直接作为跳转目标地址时，可能会发生Open redirect 漏洞，因此，在跳转之前，应使用白名单的方式对跳转目标地址进行校验，确保页面跳转至可信的地址。
主要包括以下场景：
使用函数方法：location.href、window.open()、location.assign()、location.replace()
赋值或更新HTML属性：iframe.src、form.action、a.href、embed.src、object.data；
```

**修复建议**

在跳转之前，应使用白名单的方式对跳转目标地址进行校验，确保页面跳转至可信的地址。

**正确示例**

```text
// 对目标地址URL进行合法性校验，比如通过白名单的正则表达式进行校验。
// 例如，示例中的获取到的sTargetUrl 值为http://www.example.com/abc.jsp?parm1=test1，那么，可以采用以下代码进行校验：
const sTargetUrl = getURLParam('target');
const characterPattern = new RegExp('^(http|https)\\://www.example.com/([a-zA-Z0-9-_.]+)\\?\\w+\\=\\w+$');
if (sTargetUrl.match(characterPattern)){
location.replace(sTargetUrl);
} else {
// 该url不合法
}
```

**错误示例**

const sTargetUrl = getURLParam('target'); location.replace(sTargetUrl); // 页面跳转至不可信地址

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-02-20 07:20:29

<a id="rule-24"></a>

### 24. G.EDV.03 若输出到客户端或者解释器的数据来自不可信的数据源，则须对该数据进行相应的编码或转义

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecJS_Cross_Site_Scripting |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

输出编码或转义是指将输出数据中的一些特殊字符转换成安全的形式，使得目标解释器不会将其当作语法符号或指令，以防止原本预期的语义被更改，避免跨站脚本攻击与各种类型的注入攻击。

**修复建议**

Web应用中常见编码有URL编码、XSS防护的HTML编码或JavaScript编码或CSS编码、以及对SQL、XML、LDAP的编码或转义等。实际应用中需根据不可信数据的使用方式采用不同的编码方式，如用作为HTML标签内容时需要进行HTML编码、用作页面中超链接的URL值时需要进行URL编码等。

**正确示例**

```text
// untrustedData为外部数据，可根据相应的场景，采取合适的编码措施。
const safeData = encodeForHTML(untrustedData);
const safeData = encodeForHTMLAttribute( (untrustedData);
const safeData = encodeForCSS(untrustedData);
const safeData = encodeForJavascript(untrustedData);
```

**错误示例**

```text
// untrustedData为外部数据，未对外部不可信数据采取合适的编码措施
document.write(untrustedData);
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-02-20 07:20:29

<a id="rule-25"></a>

### 25. G.EDV.04 正则表达式要尽量简单，防止ReDos攻击

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 关联工具规则 | SecJS_denial_of_service |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

直接使用外部输入数据进行正则匹配，攻击者可通过构造特殊的正则表达式或目标字符串触发“回溯陷阱”，造成拒绝服务。

**修复建议**

```text
对于ReDos攻击的防护手段主要包括：
1、进行正则匹配前，先对匹配的文本的长度进行校验；
2、应检视程序内的正则表达式，避免使用存在风险的表达式结构. 尽量不要使用过于复杂的正则，尽量少用分组。应当将多余的分组删除。
3、避免动态构建正则，若不可避免，则必须使用白名单或黑名单对外部数据进行校验和净化；
```

**正确示例**

```text
// 将正则表达式精简为a+b+c，与(a+)+(b+)+c相比，可以在实现相同功能的前提下消除ReDos风险。
function regexEvaluate(str) {
if (str.match(/a+b+c/)) {
...
} else {
...
}
}
```

**错误示例**

```text
// 1、未对匹配的文本长度进行校验。
// 2、正则表达式(a+)+(b+)+c存在ReDos风险，当匹配的字符串格式类似"aaaaaaabbbbbbbbbbbbbx"时，随待匹配中的字符"ab"的增加，代码执行时间将成指数级增长。
function regexEvaluate(str) {
if (str.match(/(a+)+(b+)+c/)) {
...
} else {
...
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-06-14 20:00:57

<a id="rule-26"></a>

### 26. G.EDV.05 禁止将用户输入的数据用于动态创建的模板

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecJS_Client_Side_Template_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

当将“用户输入的数据”作为模板或模板的一部分之后，该模板会将用户输入数据作为逻辑执行，这给入侵者带来了执行逻辑的机会。

**修复建议**

模板不能使用用户输入的数据来动态创建。

**正确示例**

```text
// 在模板引擎中处理"%{...}"这样的模板标记
// - 这种情况下dataObject.USER_DATA是不会执行的
Templet.prototype.apply = function (context, dataObject) {
let tag = "USER_DATA"; // 从templet中解析标记
this.applyData(tag, dataObject[tag]); // 标记仅作为数据存取
...
};
// 在模板中使用标记
aTemplet = new Templet("some rules ... %{USER_DATA} ... more...");
// 让模板引擎（that Templet class)处理"%{USER_DATA}"标记
let elementContext = aTemple.apply(ctx, {
USER_DATA: _DATA_FROM_USER_INPUT,
});
```

**错误示例**

```text
// 标记会作为逻辑执行
aTemplet = new Templet(`some rules ...${_DATA_FROM_USER_INPUT}more...`);
// apply()可能会将模板中的内容作为逻辑（例如表达式）处理
let elementContext = aTemplet.apply(ctx);
...
// 以es6模板为例
let inject = "console.log('or, do anything...')";
let user_data = "${eval(inject)}";
let templet = "`" + user_data + "`"; // 将用户数据作为模板
applyed = eval(templet); // 动态创建模板过程中inject成功
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-02-20 07:20:30

<a id="rule-27"></a>

### 27. G.ERR.02 应使用 Error 对象作为 Promise 拒绝的原因

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | prefer_promise_reject_errors |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 20% |

**审查要点**

在 Promise 中只传递内置的 Error 对象实例给 reject() 函数作为自定义错误，被认为是个很好的实践。Error 对象会自动存储堆栈跟踪，在调试时，通过它可以用来确定错误是从哪里来的。如果 Promise 使用了非 Error 的值作为拒绝原因，那么就很难确定 reject 在哪里产生。

**修复建议**

```text
应使用 Error 对象作为 Promise 拒绝的原因
【级别】建议
```

**正确示例**

Promise.reject(new Error('something bad happened'));

**错误示例**

Promise.reject('something bad happened');

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:44

<a id="rule-28"></a>

### 28. G.ERR.03 不要使用return、break、continue或抛出异常使finally块非正常结束

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_unsafe_finally |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

在finally代码块中，直接使用return、break、continue、throw语句，或由于调用方法的异常未处理，会导致finally代码块无法正常结束。非正常结束的finally代码块会影响try或catch代码块中异常的抛出，也可能会影响方法的返回值。所以要保证finally代码块正常结束。

**修复建议**

不要使用return、break、continue或抛出异常使finally块非正常结束

**正确示例**

```text
function foo() {
try {
...
return 1;
} catch(err) {
...
return 2;
} finally {
console.log('XXX!');
}
}
```

**错误示例**

```text
function foo() {
try {
...
return 1;
} catch(err) {
...
return 2;
} finally {
return 3;
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:44

<a id="rule-29"></a>

### 29. G.ERR.04 禁止通过异常泄露敏感数据

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecJS_System_Information_Leak |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

如果在传递异常的时候未对其中的敏感数据进行过滤常常会导致信息泄露，而这可能帮助攻击者尝试发起进一步的攻击。攻击者可以通过构造恶意的输入参数来发掘应用的内部结构和机制。不管是异常中的文本消息，还是异常本身的类型都可能泄露敏感数据。

**修复建议**

当异常会被传递到信任边界以外时，必须同时对敏感的异常消息和敏感的异常类型进行过滤。

**正确示例**

```text
app.use(function(err, req, res, next) {
console.error(err.stack);
res.status(500).send('Something broke!');
});
```

**错误示例**

```text
app.use(function(err, req, res, next) {
res.status(500).send(err.stack);
});
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-02-20 07:20:30

<a id="rule-30"></a>

### 30. G.EXP.02 判断相等时应使用 === 和 !== ，而不是 == 和 !=

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | eqeqeq |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

JavaScript 中使用双等 == 做相等判断时会自动做类型转换，如：[] == false、[] == ![]、3 == '03'都是true，当类型确定时使用全等 === 做比较可以提高效率。

**修复建议**

判断相等时应使用 === 和 !== ，而不是 == 和 !=

**正确示例**

age === bee

**错误示例**

age == bee

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-31"></a>

### 31. G.EXP.03 禁止使用嵌套的三元表达式

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_nested_ternary |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

嵌套的三元表达式使代码更加难以理解。因此，应使用条件语句来代替嵌套的三元表达式。

**修复建议**

使用条件语句来代替嵌套的三元表达式。

**正确示例**

```text
if (foo) {
if (baz === qux) {
quxx();
} else {
foobar();
}
} else {
bar();
}
```

**错误示例**

foo ? baz === qux ? quxx() : foobar() : bar();

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:31

<a id="rule-32"></a>

### 32. G.FMT.01 使用空格进行缩进

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_indent |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

只允许使用空格(space)进行缩进。

**修复建议**

只允许使用空格(space)进行缩进2个。

**正确示例**

space 空格缩进两个

**错误示例**

tab缩进或者缩进不是2个空格

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-33"></a>

### 33. G.FMT.02 行宽不宜过长

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | max_len |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

行宽不宜过长

**修复建议**

较长的语句、表达式或参数以及注释行（大于160个字符）要进行换行处理。

**正确示例**

代码行宽不宜过长

**错误示例**

每行字符超过160

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:16

<a id="rule-34"></a>

### 34. G.FMT.04 对象字面量属性超过 4 个, 需要都换行

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_max_object_prop_number_one_line |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

对象字面量要么每个属性都换行, 要么所有属性都在同一行。当对象字面量属性超过4个的时候，建议统一换行。

**修复建议**

对象字面量属性超过 4 个, 需要都换行

**正确示例**

```text
const yourObj = {
name: 'a',
age: 'b',
value: 'c',
bar: 'd',
foo: 'e',
ha: 'f',
};
```

**错误示例**

const yourObj = { name: 'a', age: 'b', value: 'c', bar: 'd', foo: 'e', ha: 'f' };

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:17

<a id="rule-35"></a>

### 35. G.FMT.05 链式调用对象方法时，一行最多调用 4 次，否则需要换行

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | newline_per_chained_call |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

链式调用对象方法时，一行最多调用 4 次，否则需要换行。换行的时候，需要按照缩进章节的要求进行缩进，并且在前面加点 . 强调这是方法调用而不是新语句。

**修复建议**

链式调用对象方法时，一行最多调用 4 次，否则需要换行。

**正确示例**

```text
$('#items')
.find('.selected')
.highlight()
.end()
.find('.open')
.updateCount();
```

**错误示例**

```text
// 没有进行换行
$('#items').find('.selected').highlight().end().find('.open').updateCount();
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-36"></a>

### 36. G.FMT.08 用空格突出关键字和重要信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | space_infix_ops,space_before_blocks,func_call_spacing,array_bracket_spacing,no_multi_spaces,comma_spacing,template_curly_spacing,semi_spacing,keyword_spacing,space_before_function_paren |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

空格应该突出关键字和重要信息。

**修复建议**

数组的中括号[]内侧不要加空格。

**正确示例**

```text
##### 场景1：数组
- 修复示例：数组中括号[]内侧无空格
```javascript
var arr = ['foo', 'bar', 'baz'];
```
```

**错误示例**

```text
##### 场景1：数组
- 错误示例：数组中括号[]内侧出现空格
```javascript
var arr = [ 'foo', 'bar' ];
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-11-06 14:48:43

<a id="rule-37"></a>

### 37. G.FMT.09 建议 if、for、do、while 等语句的执行体加大括号 {}

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | curly |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

在if、for、do、while 等语句的执行体加大括号 {}是一种最佳实践，因为省略大括号容易导致错误，并且降低代码的清晰度。

**修复建议**

在if、for、do、while 等语句的执行体加大括号 {}是一种最佳实践，因为省略大括号容易导致错误，并且降低代码的清晰度。

**正确示例**

```text
if (foo) {
foo++;
}
```

**错误示例**

if (foo) foo++;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:20

<a id="rule-38"></a>

### 38. G.FMT.12 每句代码后加分号

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_semi |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

建议任何时候都不要省略它。

**修复建议**

建议任何时候都不要省略它。

**正确示例**

```text
const foo = myfoo; // 赋值
(function(){
const name = 'Skywalker';
return name;
})(); // 自执行函数
```

**错误示例**

```text
const foo = myfoo // 没有分号，会连着下一行一起解析，变成调用myfoo(fn)();
(function(){
const name = 'Skywalker'
return name
})(); // 自执行函数
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:22

<a id="rule-39"></a>

### 39. G.MET.03 块语句的最大可嵌套深度不要超过 4 层

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | max_depth |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

很多开发者认为如果块语句嵌套深度超过某个值，代码就很难阅读。建议块语句的最大可嵌套深度不超过4层，以达到降低代码复杂性的目的。

**修复建议**

块语句的最大可嵌套深度不要超过 4 层。

**正确示例**

```text
##### 场景1：块语句嵌套深度
- 修复示例：不超过4层嵌套
```javascript
function foo() {
for (;;) { // 嵌套1层
if (true) { // 嵌套2层
if (true) { // 嵌套3层
if (true) { // 嵌套4层
}
}
};
}
}
```
```

**错误示例**

```text
##### 场景1：块语句嵌套深度
- 错误示例：超过4层嵌套
```javascript
function foo() {
for (;;) { // 嵌套1层
if (true) { // 嵌套2层
if (true) { // 嵌套3层
if (true) { // 嵌套4层
if (true) { // 嵌套5层
}
}
}
};
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-40"></a>

### 40. G.MET.04 回调函数嵌套的层数不超过 4 层

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | max_nested_callbacks |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

很多 JavaScript 类库是使用回调模式处理异步操作。一个最常见的隐患就是嵌套的回调，代码嵌套层级越深越难以阅读。

**修复建议**

回调函数嵌套的层数不超过 4 层

**正确示例**

```text
foo1(handleFoo1);
function handleFoo1() {
foo2(handleFoo2);
}
function handleFoo2() {
foo3(handleFoo3);
}
function handleFoo3() {
foo4(handleFoo4);
}
function handleFoo4() {
foo5(handleFoo5);
}
function handleFoo5() {
foo6();
}
```

**错误示例**

```text
foo1(function() { // 嵌套1层
foo2(function() { // 嵌套2层
foo3(function() { // 嵌套3层
foo4(function() { // 嵌套4层
foo5(function() { // 嵌套5层
...
});
});
});
});
});
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:36

<a id="rule-41"></a>

### 41. G.MET.06 始终将默认参数放在最后

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | default_param_last |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 20% |

**审查要点**

将默认参数放在最后，在调用函数时，若默认参数没有值，就可以不传入这个参数，让调用代码更简单。

**修复建议**

始终将默认参数放在最后

**正确示例**

```text
function handleThings(name, opts = {}) {
...
}
```

**错误示例**

```text
function handleThings(opts = {}, name) {
...
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-42"></a>

### 42. G.MET.07 建议使用一致的 return 语句

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_consistent_return |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
为了保证函数的返回值是可预测和一致的，我们应该确保函数在所有可能的执行路径上都有明确的返回值，尤其是条件语句的不同分支中，避免函数在某些情况下返回`undefined`。
注意：JavaScript中，在一个函数内，如果在某一行没有`return`语句，或只有`return;`而没有显式指定返回值时，这个函数在这一行并非不返回值，而是返回`undefined`.
```

**修复建议**

```text
函数中的每个分支，都**显式**返回相同类型的值。
**【注意】** 请检查错误示例2中的，未被条件语句覆盖，但是可能执行到的隐式分支。
```

**正确示例**

```text
**修复示例1：两个分支使用一致的返回值。**
```javascript
function doSomething(condition) {
if (condition) {
...
return true;
} else {
...
return false; // 保证所有路径都以相同的方式返回值
}
}
```
**【注意】修复示例2：所有路径使用一致的返回值。**
```javascript
function doSomething(condition) {
if (condition) {
...
return true;
}
...
// 隐式路径，即没有被if条件语句覆盖的路径，也需要指定其返回值。
return false;
}
```
```

**错误示例**

```text
**错误示例1：两个分支的返回值不一致：`true`和`undefined`。**
```javascript
function doSomething(condition) {
if (condition) {
...
return true;
} else {
...
return; // 不符合，没有显式返回false
}
}
```
**【注意】错误示例2：条件语句显式返回`true`，没有条件语句覆盖的隐式返回`undefined`。**
```javascript
function doSomething(condition) {
if (condition) {
...
return true;
}
// 这个在函数结尾，没有明确写出的“分支”，用例1中的代码等价。
// 相当于return;
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-01-07 15:13:01

<a id="rule-43"></a>

### 43. G.MET.08 不要动态创建函数

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_new_func |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

定义函数的方法包括3种：函数声明、Function构造函数和函数表达式。不管用哪种方法定义函数，它们都是Function对象的实例，并将继承Function对象所有默认或自定义的方法和属性。以函数构造器创建函数的方式类似于函数eval(),可以接受任何字符串形式作为它的函数体，这就会有安全漏洞的风险。

**修复建议**

不使用Function的构造器

**正确示例**

function f1(){consolo.log('hello word')}

**错误示例**

new Function('p1','p2','p1+p2');

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:38

<a id="rule-44"></a>

### 44. G.MET.09 不要给函数的入参重新赋值

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_param_reassign |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 20% |

**审查要点**

对函数参数中的变量重新赋值可能会误导读者，导致混乱，也会改变 arguments 对象。

**修复建议**

不要给函数的入参重新赋值

**正确示例**

```text
function foo(bar) {
const baz = bar;
...
}
```

**错误示例**

```text
function foo(bar) {
bar.aaa++;
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:38

<a id="rule-45"></a>

### 45. G.MET.10 不要使用 arguments，可以选择 rest 语法替代

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | prefer_rest_params |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

rest 参数是一个真正的数组，而 arguments 是一个类数组。rest参数必须是列表中的最后一个参数。

**修复建议**

不要使用 arguments，可以选择 rest 语法替代

**正确示例**

```text
function concatenateAll(...args) {
return args.join('');
}
```

**错误示例**

```text
function concatenateAll() {
const args = Array.prototype.slice.call(arguments);
return args.join('');
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:38

<a id="rule-46"></a>

### 46. G.MET.12 用到匿名函数时优先使用箭头函数，替代保存this引用的方式

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | prefer_arrow_callback |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

箭头函数提供了更简洁的语法，并且箭头函数中的this对象指向是不变的，this绑定到定义时所在的对象，有更好的代码可读性。而保存this引用的方式，容易让开发人员搞混。

**修复建议**

用到匿名函数时优先使用箭头函数，替代保存this引用的方式

**正确示例**

```text
function foo() {
return () => {
console.log(this);
};
}
```

**错误示例**

```text
function foo() {
const self = this;
return function() {
console.log(self);
};
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:39

<a id="rule-47"></a>

### 47. G.MOD.01 需要导出的变量必须是不可变的类型

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_export_variable |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

不要导出var或let变量，如果避免不了的话，应该导出对该变量进行读和写的函数。

**修复建议**

需要导出的变量必须是不可变的类型

**正确示例**

export const count = 1

**错误示例**

export let count = 2;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:42

<a id="rule-48"></a>

### 48. G.NAM.02 函数应采用小驼峰风格命名

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_fun_named |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

函数的命名通常是动词或动词短语，采用小驼峰命名

**修复建议**

函数的命名通常是动词或动词短语，采用小驼峰命名

**正确示例**

function draw() {}

**错误示例**

function DRAW() {}

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:13

<a id="rule-49"></a>

### 49. G.NAM.04 避免使用否定的布尔变量名

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_nouse_deny_bool_var |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

布尔型的局部变量建议加上表达是非意义的前缀，比如is，也可以是has、can、should等。但是，当使用逻辑非运算符，并出现双重否定时，会出现理解问题，比如 !isNotError，意味着什么，不是很好理解。因此，应避免定义否定的布尔变量名。

**修复建议**

避免定义否定的布尔变量名。

**正确示例**

const isError = false;

**错误示例**

const isNoError = false;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-50"></a>

### 50. G.NAM.07 使用统一的文件命名风格，文件扩展名应小写

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_filename_pattern |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

使用统一的文件命名风格，文件扩展名应小写

**修复建议**

文件扩展名小写

**正确示例**

```text
// 若存储的文件名为'afilename.txt'，则以下文件名的使用与创建符合
afilename.txt
```

**错误示例**

```text
// 若存储的文件名为'afilename.txt'，则以下文件名的使用与创建均不符合
afileName.txt
afilename.TXT
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:14

<a id="rule-51"></a>

### 51. G.NOD.01 禁止直接使用外部数据拼接命令

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecJS_Command_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

执行系统命令时，应限定或校验命令的内容，禁止直接使用不可信任的外部数据拼接命令，避免产生命令注入的漏洞。

**修复建议**

```text
开发者可以从以下两个方面来保护应用程序遭受命令注入的攻击
如果可以枚举可执行的命令和输入的参数内容或者格式，则应以白名单的方式进行限定。
如果无法枚举命令或参数，则必须过滤或者转义指定符号，包括：| ; & $ ( ) > < ` !
```

**正确示例**

```text
// 通过白名单的方式限定命令范围，过滤/转义命令内容中的特殊符号
const express = require('express');
const cp = require('child_process');
const router = express.Router();
const ALLOWED_CMDS = {
update: 'update.sh',
delete: 'delete.sh',
};
router.get('/run_cmd', (req, res) => {
const cmd = req.query.cmd;
let param = req.query.param || '';
// 对参数中特殊字符进行转义
param = param.replace(/(\||;|&|\$\(|\(|\)|>|<|\`|!)/gi, '');
// 校验命令是否在白名单中
if (cmd in ALLOWED_CMDS) {
cp.exec(ALLOWED_CMDS[cmd] + param, (err, stdout, stderr) => {
if (err) {
res.send({ status: 1, error: err });
}
res.send({ status: 0, result: stdout, error: stderr });
});
} else {
res.send({ status: 1, error: `The command ${cmd} is not allowed!` });
}
});
```

**错误示例**

```text
// 下面示例中的代码可以使攻击者通过篡改接口中的参数值，欺骗应用程序去运行恶意代码，从而破坏系统或取得系统控制权。
const express = require('express');
const cp = require('child_process');
const router = express.Router();
router.get('/run_cmd', (req, res) => {
const cmd = req.query.cmd;
cp.exec(cmd, (err, stdout, stderr) => {
if (err) {
res.send({ status: 1, error: err });
}
res.send({ status: 0, result: stdout, error: stderr });
});
});
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-02-20 07:20:31

<a id="rule-52"></a>

### 52. G.NOD.02 使用外部数据构造的文件路径在校验前必须对文件路径进行规范化处理

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecJS_Path_Manipulation |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

绝对路径或者相对路径中可能会包含如符号（软）链接（symbolic [soft] links）、硬链接（hard links）、快捷方式（shortcuts）、影子文件（shadows）、别名（aliases）和连接文件（junctions）等形式，在进行文件验证操作之前必须完整解析这些文件链接。

**修复建议**

可以使用path.normalize，path.join，path.resolve等方法对文件进行规范化处理。

**正确示例**

```text
const fs = require('fs');
const path = require('path');
let { fileName } = req.query;
let newFileName = path.normalize(fileName);
if (newFileName.indexOf('..') < 0 && !validatePath(newFileName)) {
fs.readFile(newFileName, (err, data) => {
if (err) {
return console.error(err);
}
console.log(data.toString());
});
}
function validatePath(filePath) {
if (filePath.startsWith('/home/xxx')) {
return true;
} else {
return false;
}
}
```

**错误示例**

```text
const fs = require('fs');
let { filename } = req.query;
fs.readFile(filename, (err, data) => {
if (err) {
return console.error(err);
}
console.log(data.toString());
});
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-02-20 07:20:31

<a id="rule-53"></a>

### 53. G.NOD.03 禁止直接使用外部数据来拼接SQL语句

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 关联工具规则 | SecJS_SQL_Injection |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

SQL注入是指原始SQL查询被恶意动态更改成一个与程序预期完全不同的查询。执行更改后的查询可能会导致信息泄露或者数据被篡改。

**修复建议**

```text
防止SQL注入的方式主要有以下三类：
使用参数化查询：最有效的防护手段，对于sql语句中的表名、字段名、部分场景下的in条件不适用；
对不可信数据进行白名单校验：适用于拼接sql语句中的表名、字段名；
对不可信数据进行转码：适用于拼接到sql语句中的由引号限制的字段。
参数化查询是一种简单有效的防止SQL注入的查询方式，应该被优先考虑使用。另外，参数化查询还能提高数据库访问的性能，例如，SQL Server与Oracle数据库会为其缓存一个查询计划，以便在后续重复执行相同的查询语句时无需编译而直接使用。
```

**正确示例**

```text
// good：使用预编译绑定变量构造SQL语句
const mysql = require('mysql');
const connection = mysql.createConnection(options);
connection.connect();
const sql = 'SELECT * from some_table WHERE Id = ? and Name = ?';
const sqlParams = [req.body.id, req.body.name];
connection.query(sql, sqlParams, (err, result) => {
// ...
});
```

**错误示例**

```text
// bad：拼接SQL语句查询，存在安全风险
const mysql = require('mysql');
const connection = mysql.createConnection(options);
connection.connect();
const sql = util.format(
'SELECT * from some_table WHERE Id = %s and Name = %s',
req.body.id,
req.body.name
);
connection.query(sql, (err, result) => {
// ...
});
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-06-14 20:00:18

<a id="rule-54"></a>

### 54. G.NOD.04 解压文件必须进行安全检查

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_decompress_file_secure_check |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

攻击者有可能上传一个很小的zip文件，但是完全解压缩之后达到几百万GB甚至更多，消耗完磁盘空间导致系统或程序无法正常运行。

**修复建议**

```text
在解压前最少进行以下几点基础安全检查：
1.文件个数是否超出业务预期个数；
2.文件大小是否超出业务预期大小。
建议根据需要，依赖其它工程手段加强防范，这些工程手段包括并不限于：
1.为解压文件单独分区，限定大小；
2.对压缩包做健康检查，包括其中内容是否符合业务预期。
以上两点建议非强制执行，正例中展示的是常用的校验方法，不能规避所有问题。
具体问题还需要结合产品特点具体分析，最终使解压后的文件大小在可控范围内。
```

**正确示例**

```text
const fs = require('fs');
const pathmodule = require('path');
const JSZip = require('jszip');
const MAX_FILES = 10000;
const MAX_SIZE = 1000000000; // 1 GB
let fileCount = 0;
let totalSize = 0;
let targetDirectory = __dirname + '/archive_tmp';
fs.readFile("foo.zip", function(err, data) {
if (err) throw err;
JSZip.loadAsync(data).then(function (zip) {
zip.forEach(function (relativePath, zipEntry) {
fileCount++;
if (fileCount > MAX_FILES) {
throw 'Reached max. number of files';
}
// Prevent ZipSlip path traversal (S6096)
const resolvedPath = pathmodule.join(targetDirectory, zipEntry.name);
if (!resolvedPath.startsWith(targetDirectory)) {
throw 'Path traversal detected';
}
if (!zip.file(zipEntry.name)) {
fs.mkdirSync(resolvedPath);
} else {
zip.file(zipEntry.name).async('nodebuffer').then(function (content) {
totalSize += content.length;
if (totalSize > MAX_SIZE) {
throw 'Reached max. size';
}
fs.writeFileSync(resolvedPath, content);
});
}
});
});
});
```

**错误示例**

```text
解压前未进行以下基础安全检查：
1.文件个数是否超出业务预期个数；
2.文件大小是否超出业务预期大小。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-02-20 07:20:32

<a id="rule-55"></a>

### 55. G.NOD.06 禁止直接使用外部数据记录日志

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecJS_Log_Forging_Debug,SecJS_Log_Forging |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

直接将外部数据记录到日志中，可能存在日志注入、敏感信息泄露、垃圾日志或日志覆盖的风险。

**修复建议**

```text
常见的消减措施：
隐藏用户输入，打印可以有效进行描述输入错误的信息；
黑名单校验，在输入之前，黑名单会有选择地拒绝或避免潜在的危险字符；
白名单校验，创建一份白名单，只允许其中的字符出现在日志条目中，并且只接受完全由这些经认可的字符组成的输入。
```

**正确示例**

```text
const cp = require('child_process');
const http = require('http');
const url = require('url');
function listener(request, response) {
const value = url.parse(request.url, true)['query']['value'];
if (isNaN(value)) {
// 合理提示外部数据错误，不要打印原始数据
console.log("INFO: Failed to parse value. Needs to be a number.);
}
}
http.createServer(listener).listen(8080);
```

**错误示例**

```text
const cp = require('child_process');
const http = require('http');
const url = require('url');
function listener(request, response) {
const value = url.parse(request.url, true)['query']['value'];
if (isNaN(value)) {
// 直接将外部传入数据打印到控制台
console.log("INFO: Failed to parse value = " + value);
}
}
http.createServer(listener).listen(8080);
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-02-20 07:20:33

<a id="rule-56"></a>

### 56. G.OBJ.01 通过 new 调用构造函数时，应始终使用圆括号

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | new_parens |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

通过 new 调用构造函数时，如果不带参数，可以省略后面的圆括号。但这样会造成与整体的代码风格不一致，建议团队约定使用圆括号。

**修复建议**

通过 new 调用构造函数时，应始终使用圆括号

**正确示例**

const person = new Person();

**错误示例**

const person = new Person;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:40

<a id="rule-57"></a>

### 57. G.OBJ.05 禁止在对象实例上直接使用 hasOwnProperty、isPrototypeOf、propertyIsEnumerable方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_prototype_builtins |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
对象实例可以具有属性，这些属性可以将 Object.prototype 的内建函数隐藏，可能导致意外行为或拒绝服务安全漏洞。例如，web 服务器解析来自客户机的 JSON 输入并直接在结果对象上调用 hasOwnProperty 是不安全的，因为恶意客户机可能发送一个JSON值，如 { hasOwnProperty: 1 }，并导致服务器崩溃。因此，禁止在对象实例上直接使用 hasOwnProperty、isPrototypeOf、propertyIsEnumerable方法。
```

**修复建议**

禁止在对象实例上直接使用 hasOwnProperty、isPrototypeOf、propertyIsEnumerable方法

**正确示例**

```text
const hasBarProperty = Object.prototype.hasOwnProperty.call(foo, 'bar');
const isPrototypeOfBar = Object.prototype.isPrototypeOf.call(foo, bar);
const barIsEnumerable = Object.prototype.propertyIsEnumerable.call(foo, 'bar');
```

**错误示例**

```text
const hasBarProperty = foo.hasOwnProperty('bar');
const isPrototypeOfBar = foo.isPrototypeOf(bar);
const barIsEnumerable = foo.propertyIsEnumerable('bar');
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-21 17:57:34

<a id="rule-58"></a>

### 58. G.OBJ.06 需要约束 for-in

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | guard_for_in |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

在使用 for-in 遍历对象时，会把从原型链继承来的属性也包括进来。这样会导致意想不到的项出现。因此，应该在 for-in 循环中使用Object.prototype.hasOwnProperty来排除不需要的原型属性。也可以使用Object.keys()替代for-in进行遍历。

**修复建议**

需要约束 for-in

**正确示例**

Object.keys(student).forEach(key => console.log(key))

**错误示例**

```text
Object.prototype.bind = function() {}
const student = {
name: 'Lilei',
age: 18,
};
for (const key in student) {
// 打印name、age、bind，其中bind并不是预期属性，有可能导致代码出错
console.log(key);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:42

<a id="rule-59"></a>

### 59. G.OBJ.07 不要修改内置对象的原型，或向原型添加方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_extend_native |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 15% |

**审查要点**

内置对象作为一套公共接口，具有约定俗成的行为方式，若修改其原型，可能破坏接口语义。因此，永远不要修改内置对象的原型，或向原型添加方法。

**修复建议**

不要修改内置对象的原型，或向原型添加方法

**正确示例**

function greet(): void {}

**错误示例**

function getGreetings(): Array<any> {

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:42

<a id="rule-60"></a>

### 60. G.OTH.01 安全场景下必须使用密码学意义上的安全随机数

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_insecure_randomness |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
禁止使用不安全的随机数；
不安全的随机数可能被部分或全部预测到，导致系统存在安全隐患，安全场景下使用的随机数必须是密码学意义上的安全随机数。
```

**修复建议**

在安全场景下，应使用加密性较强的crypto库来生成随机数。

**正确示例**

```text
##### 场景1：安全随机数
- 修复示例：安全场景下使用crypto库来生成随机数。
```javascript
// === 客户端 ===
const crypto = window.crypto || window.msCrypto;
let array = new Uint32Array(1);
crypto.getRandomValues(array);
// === 服务器端 ===
const crypto = require('crypto');
const buf = crypto.randomBytes(1);
```
```

**错误示例**

```text
##### 场景1：安全随机数
- 错误示例：在sha256,encodeBase64,encryptRSA,encryptAES,signHMAC,padAesCBC函数场景下使用Math.random()函数来生成随机数。
```javascript
function sha256(message) {
// 使用 Math.random() 生成随机盐值并添加到消息中
const salt = Math.random().toString(36).substring(2, 15);
const saltedMessage = message + salt;
// 将消息转换为 Uint8Array
const encoder = new TextEncoder();
const data = encoder.encode(saltedMessage);
// 计算 SHA-256 哈希值
//const hashBuffer = await crypto.subtle.digest('SHA-256', data);
const hashArray = Array.from(new Uint8Array(hashBuffer));
// 将哈希值转换为十六进制字符串
const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
return hashHex;
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-04 09:26:05

<a id="rule-61"></a>

### 61. G.OTH.03 不用的代码段直接删除，不要注释掉

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_commented_code |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 15% |

**审查要点**

不需要的代码直接删除掉，被注释掉的代码无法被正常维护。

**修复建议**

失效代码不应放在注释里

**正确示例**

不用的代码段直接删除，不要注释掉

**错误示例**

// function oldFunc(){console.log('helloword')}

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-62"></a>

### 62. G.OTH.04 正式发布的代码及注释内容不应包含开发者个人信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_personal_info_in_comment |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

正式发布的代码及注释内容如果包含开发者个人信息，可能会泄露具体的开发人员信息，存在引发社会工程学方面的风险。

**修复建议**

注释内容不应包含开发者个人信息

**正确示例**

正式发布的代码及注释内容不应包含开发者个人信息

**错误示例**

//@email user@example.com

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-63"></a>

### 63. G.OTH.05 禁止代码中包含公网地址

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_public_url |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 20% |

**审查要点**

```text
代码或脚本中包含用户不可见，不可知的公网地址，可能会引起客户质疑。
当前工具版本默认不检查url，只检查email和ip。
```

**修复建议**

禁止代码中包含公网IP地址或者email。

**正确示例**

```text
##### 场景1：特殊IP例外排除
- 修复示例：限制广播地址 255.255.255.255例外排除
```javascript
let id = '255.255.255.255';
```
```

**错误示例**

```text
##### 场景1：IP地址
- 错误示例：包含IP地址
```javascript
let y = '100.101.100.101';
```
##### 场景2：email
- 错误示例：包含email
```javascript
let x = 'user@example.com';
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-08-06 17:13:13

<a id="rule-64"></a>

### 64. G.OTH.06 禁止在用户界面、日志中暴露不必要信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_exposure_of_unnecessary_info,SecJS_no_sensitive_info_in_log |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
如果攻击者获取了系统所涉及操作系统、中间件、应用程序、实现细节（如函数调用栈、SQL语句等）、密钥、银行账号、会话标识等信息，则会根据这些信息进行有针对性的漏洞分析或直接窃取有价值信息，
因此，我们要尽量避免将系统上的信息暴露给任何潜在的攻击者。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-04-07 13:46:17

<a id="rule-65"></a>

### 65. G.TYP.01 禁止省略浮点数小数点前后的 0

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_floating_decimal |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

由于这个原因，必须在小数点前面和后面有一个数字，以明确表明是要创建一个小数。

**修复建议**

小数不要省略.前面0

**正确示例**

// 若不关系参数的类型，只考虑类型的一致时，可使用泛型来替代any

**错误示例**

return items[0];

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:24

<a id="rule-66"></a>

### 66. G.TYP.02 判断变量是否为NaN时必须使用 isNaN() 方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | use_isnan |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

必须使用 Number.isNaN() 或 全局的 isNaN() 函数来测试一个值是否是 NaN。

**修复建议**

必须使用 Number.isNaN() 或 全局的 isNaN() 函数来测试一个值是否是 NaN。

**正确示例**

```text
if (isNaN(foo)) {
// ...
}
```

**错误示例**

```text
if (foo == NaN) {
// ...
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:25

<a id="rule-67"></a>

### 67. G.TYP.03 浮点型数据判断相等不要直接使用== 或===

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_float_equal |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

由于浮点数在计算机表示中存在精度的问题，数学上相等的数字，经过运算后，其浮点数表示可能不再相等，因而不能使用相等运算符== 或===判断浮点数是否相等。

**修复建议**

由于浮点数在计算机表示中存在精度的问题，数学上相等的数字，经过运算后，其浮点数表示可能不再相等，因而不能使用相等运算符== 或===判断浮点数是否相等。

**正确示例**

const num1 = 0.1;

**错误示例**

0.1 + 0.2 == 0.3;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-68"></a>

### 68. G.TYP.05 使用模板字符串（`）实现字符串拼接

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | prefer_template |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 20% |

**审查要点**

在ECMAScript 6中，如果有变量的拼接，则需要使用模板字符串实现字符串拼接。

**修复建议**

使用模板字符串实现字符串拼接。

**正确示例**

```text
function sayHi(name) {
return `How are you, ${name}?`;
}
```

**错误示例**

```text
function sayHi(name) {
return 'How are you, ' + name + '?';
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:26

<a id="rule-69"></a>

### 69. G.TYP.06 不应使用字符串的行连续符号

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_multi_str |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 20% |

**审查要点**

不应在字符串行结束处，使用反斜杠作为连续符号，即使ECMAScript 5允许这样做。因为，使用反斜杠作为连续符号时，如果在反斜杠之后还有空格，则会导致错误，并且该错误不容易被发现。

**修复建议**

不应使用字符串的行连续符号

**正确示例**

```text
const longString = 'This is a very long string that far exceeds the 80 ' +
'column limit. It does not contain long stretches of spaces since ' +
'the concatenated strings are cleaner.';
```

**错误示例**

```text
const longString = 'This is a very long string that far exceeds the 80 \
column limit. It unfortunately contains long stretches of spaces due \
to how the continued lines are indented.';
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:26

<a id="rule-70"></a>

### 70. G.TYP.07 不要在数组上定义或使用非数字属性（length 除外）

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_array_property |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

在JavaScript中，数组也是对象，可以往数组上添加属性，但为了处理方便和避免出错，数组只应该用来存储有序（即索引连续）的一组数据。必须要添加属性时，考虑用Map或者Object替代。

**修复建议**

不要在数组上定义或使用非数字属性（length 除外）

**正确示例**

```text
const map = new Map();
map.set('key1', 'val1');
map.set('key2', 'val2');
```

**错误示例**

```text
const myHash = [];
myHash['key1'] = 'val1';
myHash['key2'] = 'val2';
myHash[0] = '222';
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:26

<a id="rule-71"></a>

### 71. G.TYP.09 不要在数组遍历中对其进行元素的 remove/add 操作

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_array_loop_operate |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

在forEach循环中删除、增加元素，会破坏原本的遍历顺序，可能导致意料之外的结果。

**修复建议**

不要在数组遍历中进行元素的 remove/add 操作

**正确示例**

```text
const myArr = [1, 2];
const tmpArr = [...myArr];
tmpArr.forEach((val, index) => {
console.log(val); // 输出 1, 2
if (val === 1) {
myArr.splice(index, 1);
}
});
console.log(myArr); // [2]
```

**错误示例**

```text
const myArr = [1, 2];
myArr.forEach((val, index) => {
console.log(val); // 只输出了 1
if (val === 1) {
myArr.splice(index, 1);
}
});
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:27

<a id="rule-72"></a>

### 72. G.TYP.11 使用展开语法 … 或concat复制数组

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_array_copy |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

如果对局部线程池没有及时shutdown，局部线程池无法被GC回收，会导致线程泄露，因为ThreadPoolExecutor->Worker->Thread, 由于存在这样的引用关系, 并且 Thread 作为 GC Root,所以无法被回收

**修复建议**

使用局部线程池，为防止出现线程泄露的情况出现，请使用shutdown方法进行停止

**正确示例**

```text
public void createData() {
Executor executor = new ThreadPoolExecutor(5,
10,
60,
TimeUnit.SECONDS,
new ArrayBlockingQueue<>(1000),
new NamedThreadFactory("业务名"));
.....
.....
executor.shutdown();
}
```

**错误示例**

```text
public void createData() {
Executor executor = new ThreadPoolExecutor(5,
10,
60,
TimeUnit.SECONDS,
new ArrayBlockingQueue<>(1000),
new NamedThreadFactory("业务名"));
.....
.....
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:22:28

<a id="rule-73"></a>

### 73. G.TYP.14 使用显式的类型转换

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_implicit_coercion |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

在JavaScript中，有许多不同的方式进行类型转换。其中有些可能难于阅读和理解，因此，应该使用显式的类型转换。

**修复建议**

使用显式的类型转换

**正确示例**

// 可以将任何值（包括 null 和 undefined）赋给 val，

**错误示例**

```text
const baz = ~foo.indexOf('.');
const num = +foo;
const num = 1 * foo;
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-74"></a>

### 74. G.WSQ.01 禁止在 Web SQL database和Indexed database数据库中存储敏感数据

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_web_sql_database |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
Web SQL database和Indexed database存储的数据是明文的，客户端上有本地访问权限的操作系统用户都能访问，容易泄露。而且，利用XSS漏洞，攻击者可以构造脚本对Web SQL database和Indexed database数据库进行操纵，数据容易被窃取，即使对敏感数据加密，一旦密钥泄露，也将导致敏感数据泄露。因此，禁止在WebSQL database和Indexed database数据库中存储敏感数据。
```

**修复建议**

禁止在 Web SQL database和Indexed database数据库中存储敏感数据

**正确示例**

```text
let db=openDatabase("testDB","1.0.0","d1",1024*1024);
db.transaction(function (tx) {
tx.executeSql('INSERT INTO province (id, name) VALUES (1, "zhejiang")');
}
```

**错误示例**

```text
let db=openDatabase("testDB","1.0.0","d1",1024*1024);
db.transaction(function (tx) {
tx.executeSql('INSERT INTO account (id, password) VALUES (1, "password")');
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-03-22 18:57:02

<a id="rule-75"></a>

### 75. G.WST.01 禁止在 localStorage和sessionStorage中存储敏感数据

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_sensitive_local_storage |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

localStorage和sessionStorage本身无防XSS的机制，数据容易被窃取，即使对敏感数据加密，一旦密钥泄露，也将导致敏感数据泄露。

**修复建议**

禁止在 localStorage和sessionStorage中存储敏感数据

**正确示例**

localStorage.setItem('name', name);

**错误示例**

localStorage.setItem('password', password);

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-76"></a>

### 76. G.WST.02 如果数据仅需要临时存储在客户端，使用非持久性会话cookie或者sessionStorage而不是localStorage

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_staged_data_local_storage |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

localStorage是持久性（没有时间限制）的数据存储，会导致数据长期存留，增加数据泄露的风险。存储于非持久性会话cookie或sessionStorage中的数据会被及时清除。

**修复建议**

如果数据仅需要临时存储在客户端，使用非持久性会话cookie或者sessionStorage而不是localStorage

**正确示例**

sessionStorage.setItem('tempData', tempData);

**错误示例**

localStorage.setItem('tempData', tempData);

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-03-22 18:56:54

<a id="rule-77"></a>

### 77. HW_GTS_JS_HardCode_email_EmployeeId_DTSId

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | HW_GTS_JS_HardCode_email_EmployeeId_DTSId |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
场景一:匹配JS中设置DTS单号
场景二: 匹配JS中所有公司工号
场景三:匹配JS中所有公司邮箱
```

**修复建议**

清理JS中的DTS单号，公司工号，公司邮箱

**正确示例**

```text
```
不涉及，禁止在jS代码中硬编码DTS单号与工号，邮箱
```
```

**错误示例**

```text
```
不涉及，在jS代码中硬编码DTS单号与工号，邮箱
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-02-17 16:05:42

<a id="rule-78"></a>

### 78. HW_GTS_JS_IP_Hardcode

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | HW_GTS_JS_IP_Hardcode |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

硬编码ip地址不能写在js中，引起客户质疑

**修复建议**

建议把IP地址写在配置文件中（非js文件）,或者数据库中，从上述文件中获取IP地址

**正确示例**

```text
```
不涉及，不能在js代码中硬编码ip地址
```
```

**错误示例**

```text
```
function main(){}{
var value101 = “101.00.00.00”;
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-02-17 16:06:40

<a id="rule-79"></a>

### 79. Insecure SSL: Server Identity Verification Disabled

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Insecure SSL: Server Identity Verification Disabled |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 297 |
| 预估误报率 | 20% |

**审查要点**

当进行 SSL 连接时，服务器身份验证处于禁用状态。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-80"></a>

### 80. Insecure Transport

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Insecure Transport |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 311 |
| 预估误报率 | 20% |

**审查要点**

该调用会使用未加密的协议（而非加密的协议）与服务器通信。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-81"></a>

### 81. Insecure Transport: Weak SSL Protocol

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Insecure Transport: Weak SSL Protocol |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 326 |
| 预估误报率 | 20% |

**审查要点**

SSLv2、SSLv23 和 SSLv3 协议包含多个使它们变得不安全的缺陷，因此不应该使用它们来传输敏感数据。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-82"></a>

### 82. JSON Injection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | JSON Injection |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 91 |
| 预估误报率 | 50% |

**审查要点**

该方法会将未经验证的输入写入 JSON。攻击者可以利用此调用将任意元素或属性注入 JSON 实体。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-83"></a>

### 83. Key Management: Empty Encryption Key

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Key Management: Empty Encryption Key |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 321 |
| 预估误报率 | 20% |

**审查要点**

Empty 加密密钥可能会削弱系统安全性，一旦出现安全问题将无法轻易修正。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-84"></a>

### 84. Key Management: Hardcoded Encryption Key

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Key Management: Hardcoded Encryption Key |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 321 |
| 预估误报率 | 20% |

**审查要点**

Hardcoded 加密密钥可能会削弱系统安全性，一旦出现安全问题将无法轻易修正。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-85"></a>

### 85. Key Management: Null Encryption Key

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Key Management: Null Encryption Key |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 321 |
| 预估误报率 | 20% |

**审查要点**

Null 加密密钥可能会削弱系统安全性，一旦出现安全问题将无法轻易修正。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-86"></a>

### 86. Open Redirect

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Open Redirect |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 601 |
| 预估误报率 | 30% |

**审查要点**

如果允许未验证的输入控制重定向机制所使用的 URL，可能会有利于攻击者发动钓鱼攻击。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-87"></a>

### 87. Password Management: Hardcoded Password

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Password Management: Hardcoded Password |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 259 |
| 预估误报率 | 20% |

**审查要点**

Hardcoded password 可能会危及系统安全性，并且无法轻易修正出现的安全问题。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-88"></a>

### 88. Password Management: Weak Cryptography

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Password Management: Weak Cryptography |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 261 |
| 预估误报率 | 20% |

**审查要点**

采用普通的编码方式给密码加密并不能有效地保护密码。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-89"></a>

### 89. Path Manipulation

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Path Manipulation |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 22 |
| 预估误报率 | 20% |

**审查要点**

允许用户输入控制文件系统操作所用的路径会导致攻击者能够访问或修改其他受保护的系统资源。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-90"></a>

### 90. SecH_GTS_JS_HardCode_Email_EmployeeId_DTSId

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JS_HardCode_Email_EmployeeId_DTSId |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 10%~20% |

**审查要点**

```text
场景一:匹配JS中设置DTS单号
场景二: 匹配JS中所有公司工号
场景三:匹配JS中所有公司邮箱
```

**修复建议**

清理JS中的DTS单号，公司工号，公司邮箱

**正确示例**

```text
```
禁止在jS代码中硬编码DTS单号与工号，邮箱
```
```

**错误示例**

```text
```
var Globa2 = "DTS2020021209779";
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:42:16

<a id="rule-91"></a>

### 91. SecH_GTS_JS_IP_Hardcode

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JS_IP_Hardcode |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 10%~20% |

**审查要点**

硬编码ip地址不能写在js中，引起客户质疑

**修复建议**

建议把IP地址写在配置文件中（非js文件）,或者数据库中，从上述文件中获取IP地址

**正确示例**

```text
```
从配置文件中（非js文件）或者数据库中获取IP地址
```
```

**错误示例**

```text
```
function main(){}{
var value101 = “101.00.00.00”;
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:11:51

<a id="rule-92"></a>

### 92. Weak Cryptographic Hash

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Weak Cryptographic Hash |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 328 |
| 预估误报率 | 20% |

**审查要点**

弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-93"></a>

### 93. Weak Encryption: Insufficient Key Size

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVASCRIPT |
| 标签 | Fortify |
| 关联工具规则 | Weak Encryption: Insufficient Key Size |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 326 |
| 预估误报率 | 20% |

**审查要点**

另外，当使用的密钥长度不够时，强大的加密算法便容易受到强力攻击。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-29 15:34:20

<a id="rule-94"></a>

### 94. huge_cyclomatic_complexity[js]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | JAVASCRIPT |
| 标签 | cmetrics |
| 关联工具规则 | huge_cyclomatic_complexity |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
超大圈复杂度：圈复杂度超过阈值的函数，超大圈复杂度的阈值可以在CodeCheck上进行配置。圈复杂度的理论标准算法需要绘制控制流图，然后采用计算公式进行计算。对于复杂的程序，确保人人都能准确绘制其控制流图还是比较困难，不能直观快捷计算圈复杂度。因此，CMetrics采用了直观便捷的人人都可手工计算对比的简单方法，即计算控制条件的个数，其中包括了if、else if、else、for、while、case、?表达式、&&、||等的个数，在此基础上再 +1 的结果即为函数的圈复杂度个数。这种计算方法，也与公司早期使用的SourceMonitor工具计算方法一致。而业界其他的工具，可能不会计算else的个数，这是最主要的差异。相比较而言，计算else的个数更为合理，即有无else，其代码复杂度还是稍有差别的。
```

**修复建议**

降低函数复杂度，可以解决超大圈复杂度问题，牵引良好设计。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-07-03 19:00:55

<a id="rule-95"></a>

### 95. huge_cyclomatic_complexity_ratio[js]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | JAVASCRIPT |
| 标签 | cmetrics |
| 关联工具规则 | huge_cyclomatic_complexity_ratio |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
超大圈复杂度函数比例：圈复杂度超过阈值的函数个数占比，超大圈复杂度函数个数作为分子，函数总数作为分母，计算他们的比值得到超大圈复杂度函数比例。超大圈复杂度的阈值可以在CodeCheck上进行配置。CMetrics函数的圈复杂度计算方法：计算控制条件的个数，其中包括了if、else if、else、for、while、case、?表达式、&&、||等的个数，在此基础上再 +1 的结果即为函数的圈复杂度个数。
```

**修复建议**

降低函数复杂度，可以提升代码的可维护性，牵引良好设计。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 最新更新时间：2021-11-15 16:48:00

<a id="rule-96"></a>

### 96. huge_folder[js]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | JAVASCRIPT |
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
- 最新更新时间：2020-07-03 19:00:55

<a id="rule-97"></a>

### 97. huge_folder_ratio[js]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | cmetrics |
| 关联工具规则 | huge_folder_ratio |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

超大目录个数占比，超大目录的个数作为分子，目录总数作为分母，计算他们的比值得到超大目录比例。超大目录指本目录节点下子目录数和文件数之和超过阈值的目录（注意：不计算该目录节点下子目录下更深层次的子目录数和文件数）。超大目录的阈值可以在CodeCheck上进行配置。

**修复建议**

设置合理的目录结构，也可以提升易读性，可维护性。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 最新更新时间：2021-11-15 16:48:05

<a id="rule-98"></a>

### 98. huge_method[js]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | JAVASCRIPT |
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
- 最新更新时间：2020-07-03 19:00:55

<a id="rule-99"></a>

### 99. huge_method_ratio[js]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | JAVASCRIPT |
| 标签 | cmetrics |
| 关联工具规则 | huge_method_ratio |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

代码行超过阈值的函数个数占比：代码行超过阈值的函数个数作为分子，函数总数作为分母，计算它们的比值得到超大函数占比。超大函数的阈值可以在CodeCheck上进行配置。

**修复建议**

降低函数复杂度，可以解决超大函数问题，牵引良好设计。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 最新更新时间：2021-11-15 16:48:00

<a id="rule-100"></a>

### 100. huge_non_headerfile[js]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | JAVASCRIPT |
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
- 最新更新时间：2020-07-03 19:00:55

<a id="rule-101"></a>

### 101. huge_non_headerfile_ratio[js]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | cmetrics |
| 关联工具规则 | huge_non_headerfile_ratio |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
超大源文件占比: 代码行超过阈值的源文件（不包含头文件）个数占比，超大源文件的个数作为分子，文件总数作为分母，计算他们的比值得到超大源文件比例。
头文件：以下文件名后缀的文件会被识别为头文件
```
.h,hh,.hpp,.hxx,.h++,.inc,.inl
```
超大源文件的阈值可以在CodeCheck上进行配置。
```

**修复建议**

超大源文件意味着代码架构管理不清晰，可以通过代码重构等方式消减。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 最新更新时间：2021-11-15 16:48:06

<a id="rule-102"></a>

### 102. redundant_code_kloc[js]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVASCRIPT |
| 标签 | cmetrics |
| 关联工具规则 | redundant_code_kloc |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
冗余代码块密度 = 冗余代码数 / (代码行总数 / 1000) （可理解为每千行代码冗余代码数）。
注：如果代码行总数少于1000，不计算密度，直接等于0。
冗余代码数：
统计注释中冗余代码块的个数（**注释中的代码被视作冗余代码**）。
CMetrics通过正则表达式找出的#if 0代码和注释中的代码。
如果在注释中出现以下表达，则认为是冗余代码：
```
return;
return ......;
};
}......;
=......;
......(......);
......[......];
if/else/else if/for/while/switch/case/catch/finally/try/default
```
冗余代码极小概率会存在漏报的情况，某些情况下连续单词组可以认为是注释，也可以认为是代码，工具无法从语义上识别出是否为冗余代码，此情况本着允许漏报，不许误报的原则，不予预警。
注意：
```
/*
code
code
*/
```
块注释有多行冗余代码，只算一个冗余代码数。
```

**修复建议**

删除冗余代码。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 最新更新时间：2021-11-15 16:48:10

