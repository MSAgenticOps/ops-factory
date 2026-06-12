# TypeScript 最小规则集代码审查规则
> 本文件由一个公司 电子表格 规则集生成，并保留源文件中的全部规则；不要把它当作最小子集使用。
## 来源
- 来源 电子表格：`运营商服务与软件TypeScript最小规则集.xlsx`
- 来源目录：原始规则目录
- 提取规则数：54
- 问题级别分布：一般: 48, 严重: 3, 信息: 2, 提示: 1
- 语言分布：TYPESCRIPT: 54
- 规则类型分布：通用规范规则类: 43, 代码坏味道类: 8, 安全类（非编程规范）: 2, 通用规范建议类: 1

## 代码审查使用方式
- 仅在审查触及对应语言、构建选项或安全面的变更时加载本规则集。
- 只有当变更差异 或必要的相邻代码中存在明确证据时，才输出规则违反项。
- 输出 合入请求审查问题时，需要包含来源规则名称和关联工具规则。
- `严重` 默认视为阻塞问题，除非已有明确例外说明；`一般` 默认合入前修复；`提示` 默认作为建议项，除非项目策略另有要求。

## 规则索引

| 序号 | 规则 | 级别 | 语言 | 工具规则 | 类型 |
|---|---|---|---|---|---|
| 1 | [G.AOD.01 禁止使用 eval()](#rule-1) | 一般 | TYPESCRIPT | no_eval | 通用规范规则类 |
| 2 | [G.AOD.02 禁止使用隐式的 eval()](#rule-2) | 一般 | TYPESCRIPT | no_implied_eval | 通用规范规则类 |
| 3 | [G.AOD.04 禁止使用 alert](#rule-3) | 一般 | TYPESCRIPT | no_alert | 通用规范规则类 |
| 4 | [G.AOD.05 禁止使用 debugger](#rule-4) | 一般 | TYPESCRIPT | no_debugger | 通用规范规则类 |
| 5 | [G.CLS.01-TS 当方法返回值是this，返回值的类型也应该是this](#rule-5) | 一般 | TYPESCRIPT | prefer_return_this_type | 通用规范规则类 |
| 6 | [G.CMT.01-TS 使用// @ts-&lt;directive&gt;注释时必须添加说明](#rule-6) | 一般 | TYPESCRIPT | ban_ts_comment | 通用规范规则类 |
| 7 | [G.CORS.01 使用postMessage方法实现跨域时，应严格校验Origin](#rule-7) | 一般 | TYPESCRIPT | SecJS_no_wildcard_postmessage | 通用规范规则类 |
| 8 | [G.CTL.01 每个 switch 语句都应包含一个 default 分支](#rule-8) | 一般 | TYPESCRIPT | default_case | 通用规范规则类 |
| 9 | [G.CTL.02 在 switch 语句的每一个有内容的 case 中都应放置一条 break 语句](#rule-9) | 一般 | TYPESCRIPT | no_fallthrough | 通用规范规则类 |
| 10 | [G.CTL.03 case 语句中需要词法声明时, 大括号{}不能省略](#rule-10) | 一般 | TYPESCRIPT | no_case_declarations | 通用规范规则类 |
| 11 | [G.CTL.06 不要在控制性条件表达式中执行赋值操作](#rule-11) | 一般 | TYPESCRIPT | no_cond_assign | 通用规范规则类 |
| 12 | [G.DCL.01 声明变量时要求使用 const 或 let 而不是 var（ECMAScript 6）](#rule-12) | 一般 | TYPESCRIPT | no_var | 通用规范规则类 |
| 13 | [G.DCL.03 每个语句只声明一个变量](#rule-13) | 一般 | TYPESCRIPT | one_var | 通用规范规则类 |
| 14 | [G.DCL.04 禁止连续赋值](#rule-14) | 一般 | TYPESCRIPT | no_multi_assign | 通用规范规则类 |
| 15 | [G.DCL.05 不要使用 undefined 初始化变量](#rule-15) | 一般 | TYPESCRIPT | no_undef_init | 通用规范规则类 |
| 16 | [G.DCL.06 使用字面量风格的声明](#rule-16) | 一般 | TYPESCRIPT | no_new_wrappers,no_array_constructor,no_new_object | 通用规范规则类 |
| 17 | [G.ENU.02-TS 应该显式定义枚举属性值](#rule-17) | 一般 | TYPESCRIPT | prefer_enum_initializers | 通用规范规则类 |
| 18 | [G.ENU.03-TS 枚举值应该使用字面量而非变量](#rule-18) | 一般 | TYPESCRIPT | prefer_literal_enum_member | 通用规范规则类 |
| 19 | [G.ERR.03 不要使用return、break、continue或抛出异常使finally块非正常结束](#rule-19) | 一般 | TYPESCRIPT | no_unsafe_finally | 通用规范规则类 |
| 20 | [G.EXP.02 判断相等时应使用 === 和 !== ，而不是 == 和 !=](#rule-20) | 一般 | TYPESCRIPT | eqeqeq | 通用规范规则类 |
| 21 | [G.EXP.03 禁止使用嵌套的三元表达式](#rule-21) | 一般 | TYPESCRIPT | no_nested_ternary | 通用规范规则类 |
| 22 | [G.MET.08 不要动态创建函数](#rule-22) | 一般 | TYPESCRIPT | no_new_func | 通用规范规则类 |
| 23 | [G.MET.10 不要使用 arguments，可以选择 rest 语法替代](#rule-23) | 一般 | TYPESCRIPT | prefer_rest_params | 通用规范规则类 |
| 24 | [G.MET.12 用到匿名函数时优先使用箭头函数，替代保存this引用的方式](#rule-24) | 一般 | TYPESCRIPT | prefer_arrow_callback | 通用规范规则类 |
| 25 | [G.MOD.01 需要导出的变量必须是不可变的类型](#rule-25) | 一般 | TYPESCRIPT | SecJS_export_variable | 通用规范规则类 |
| 26 | [G.NOD.04 解压文件必须进行安全检查](#rule-26) | 一般 | TYPESCRIPT | SecJS_decompress_file_secure_check | 通用规范规则类 |
| 27 | [G.OBJ.04 优先使用点号来访问对象的属性，只有计算属性使用 []](#rule-27) | 一般 | TYPESCRIPT | dot_notation | 通用规范规则类 |
| 28 | [G.OBJ.05 禁止在对象实例上直接使用 hasOwnProperty、isPrototypeOf、propertyIsEnumerable方法](#rule-28) | 一般 | TYPESCRIPT | no_prototype_builtins | 通用规范规则类 |
| 29 | [G.OBJ.06 需要约束 for-in](#rule-29) | 一般 | TYPESCRIPT | guard_for_in | 通用规范规则类 |
| 30 | [G.OBJ.07 不要修改内置对象的原型，或向原型添加方法](#rule-30) | 一般 | TYPESCRIPT | no_extend_native | 通用规范规则类 |
| 31 | [G.OTH.01 安全场景下必须使用密码学意义上的安全随机数](#rule-31) | 一般 | TYPESCRIPT | SecJS_no_insecure_randomness | 通用规范规则类 |
| 32 | [G.OTH.03 不用的代码段直接删除，不要注释掉](#rule-32) | 一般 | TYPESCRIPT | SecJS_no_commented_code | 通用规范规则类 |
| 33 | [G.OTH.04 正式发布的代码及注释内容不应包含开发者个人信息](#rule-33) | 一般 | TYPESCRIPT | SecJS_no_personal_info_in_comment | 通用规范规则类 |
| 34 | [G.OTH.05 禁止代码中包含公网地址](#rule-34) | 提示 | TYPESCRIPT | SecJS_no_public_url | 通用规范建议类 |
| 35 | [G.SCO.02 禁止使用 with() {}](#rule-35) | 一般 | TYPESCRIPT | no_with | 通用规范规则类 |
| 36 | [G.TYP.01 禁止省略浮点数小数点前后的 0](#rule-36) | 一般 | TYPESCRIPT | no_floating_decimal | 通用规范规则类 |
| 37 | [G.TYP.02 判断变量是否为NaN时必须使用 isNaN() 方法](#rule-37) | 一般 | TYPESCRIPT | use_isnan | 通用规范规则类 |
| 38 | [G.TYP.03 浮点型数据判断相等不要直接使用== 或===](#rule-38) | 一般 | TYPESCRIPT | SecJS_float_equal | 通用规范规则类 |
| 39 | [G.TYP.07 不要在数组上定义或使用非数字属性（length 除外）](#rule-39) | 一般 | TYPESCRIPT | SecJS_array_property | 通用规范规则类 |
| 40 | [G.TYP.09 不要在数组遍历中对其进行元素的 remove/add 操作](#rule-40) | 一般 | TYPESCRIPT | SecJS_array_loop_operate | 通用规范规则类 |
| 41 | [G.TYP.14 使用显式的类型转换](#rule-41) | 一般 | TYPESCRIPT | no_implicit_coercion | 通用规范规则类 |
| 42 | [G.WSQ.01 禁止在 Web SQL database和Indexed database数据库中存储敏感数据](#rule-42) | 一般 | TYPESCRIPT | SecJS_no_web_sql_database | 通用规范规则类 |
| 43 | [G.WST.01 禁止在 localStorage和sessionStorage中存储敏感数据](#rule-43) | 严重 | TYPESCRIPT | SecJS_no_sensitive_local_storage | 通用规范规则类 |
| 44 | [G.WST.02 如果数据仅需要临时存储在客户端，使用非持久性会话cookie或者sessionStorage而不是localStorage](#rule-44) | 一般 | TYPESCRIPT | SecJS_no_staged_data_local_storage | 通用规范规则类 |
| 45 | [[试行]SecJS_Open_Redirect](#rule-45) | 严重 | TYPESCRIPT | SecJS_Open_Redirect | 安全类（非编程规范） |
| 46 | [do not use insecure randomness function](#rule-46) | 严重 | TYPESCRIPT | SecJS_no_insecure_randomness | 安全类（非编程规范） |
| 47 | [duplication_file[TYPESCRIPT]](#rule-47) | 一般 | TYPESCRIPT | duplication_file | 代码坏味道类 |
| 48 | [huge_cca_cyclomatic_complexity[TYPESCRIPT]](#rule-48) | 一般 | TYPESCRIPT | huge_cca_cyclomatic_complexity | 代码坏味道类 |
| 49 | [huge_cyclomatic_complexity[TYPESCRIPT]](#rule-49) | 信息 | TYPESCRIPT | huge_cyclomatic_complexity | 代码坏味道类 |
| 50 | [huge_depth[TYPESCRIPT]](#rule-50) | 一般 | TYPESCRIPT | huge_depth | 代码坏味道类 |
| 51 | [huge_folder[TYPESCRIPT]](#rule-51) | 一般 | TYPESCRIPT | huge_folder | 代码坏味道类 |
| 52 | [huge_method[TYPESCRIPT]](#rule-52) | 信息 | TYPESCRIPT | huge_method | 代码坏味道类 |
| 53 | [huge_non_headerfile[TYPESCRIPT]](#rule-53) | 一般 | TYPESCRIPT | huge_non_headerfile | 代码坏味道类 |
| 54 | [redundant_code[TYPESCRIPT]](#rule-54) | 一般 | TYPESCRIPT | redundant_code | 代码坏味道类 |

## 规则详情

<a id="rule-1"></a>

### 1. G.AOD.01 禁止使用 eval()

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_eval |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

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
- 最新更新时间：2022-06-21 17:57:35

<a id="rule-2"></a>

### 2. G.AOD.02 禁止使用隐式的 eval()

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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

// 但在缩窄类型或者类型转换之前并不能使用它。

**错误示例**

setTimeout('alert("Hi!");', 100);

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-21 17:57:35

<a id="rule-3"></a>

### 3. G.AOD.04 禁止使用 alert

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2022-06-17 06:20:04

<a id="rule-4"></a>

### 4. G.AOD.05 禁止使用 debugger

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-5"></a>

### 5. G.CLS.01-TS 当方法返回值是this，返回值的类型也应该是this

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | prefer_return_this_type |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

方法链是 面向对象编程里一个常见的模式，即方法的返回值还是类本身，可以继续调用下去。如果方法需要返回自身，返回类型应该声明为this，否则会出现获取不到父类的子方法。

**修复建议**

当方法返回值是this，返回值的类型也应该是this

**正确示例**

```text
class Foo {
f1(): this {
return this;
}
}
class Bar extends Foo {
f2(): this {
return this;
}
}
const bar = new Bar();
bar.f1().f2(); // f1 返回的是 this，也就是根据上下文确定返回的类型，此处是 Bar ，因此不会报错
```

**错误示例**

```text
class Foo {
f1(): Foo {
return this;
}
}
class Bar extends Foo {
f2(): Bar {
return this;
}
}
const bar = new Bar();
bar.f1().f2(); // 提示：类型“Foo”上不存在属性“f2”，原因：虽然 f1 返回的是 this，但是返回类型是 Foo ，Foo而没有 f2 方法，因此会提示错误
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:20:15

<a id="rule-6"></a>

### 6. G.CMT.01-TS 使用// @ts-<directive>注释时必须添加说明

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | ban_ts_comment |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 10% |

**审查要点**

```text
TS语法支持下面4种抑制错误的语法，要求使用下面语法时必须在指令后面增加说明。
// @ts-expect-error
// @ts-ignore
// @ts-nocheck
// @ts-check
```

**修复建议**

使用// @ts-<directive>注释时必须添加说明

**正确示例**

```text
if (false) {
// @ts-expect-error: Unreachable code error
console.log('hello');
}
```

**错误示例**

```text
if (false) {
// @ts-expect-error
console.log('hello');
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:20:20

<a id="rule-7"></a>

### 7. G.CORS.01 使用postMessage方法实现跨域时，应严格校验Origin

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_wildcard_postmessage |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
HTML5增加了window.postMessage(message,targetOrigin,[transfer])方法，该方法可以实现跨域通信，以允许不同来源的Window对象之间进行消息交换。因为任何窗口都可以从其他窗口发送/接收消息，所以验证发送者/接收者的来源很重要，如果目标来源设置的过于宽松，恶意脚本就能趁机采用不当方式与受害者窗口进行通信，从而导致发送欺骗、数据被盗、转发及其他攻击。
因此，无论是作为消息的发送方还是接受方，都应该对目标源进行最小化校验，推荐使用白名单的方式，禁止使用indexOf()来校验目标源。
```

**修复建议**

使用postMessage方法实现跨域时，应严格校验Origin

**正确示例**

```text
var myWindow = document.getElementById('myIFrame'.contentWindow);
myWindow.postMessage(message,'https://example2.com'); // 该消息只能发送给https://example2.com的窗口
```

**错误示例**

```text
// 发送方为https://example1.com页面的窗口，接收方为https://example2.com页面的窗口
var myWindow = document.getElementById('myIFrame'.contentWindow);
// 该消息可以发送到任何可以接受消息的窗口，不止https://example2.com页面的窗口
myWindow.postMessage(message,'*')；
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:20:07

<a id="rule-8"></a>

### 8. G.CTL.01 每个 switch 语句都应包含一个 default 分支

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-9"></a>

### 9. G.CTL.02 在 switch 语句的每一个有内容的 case 中都应放置一条 break 语句

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_fallthrough |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

在 switch 语句的每一个有内容的 case 中都应放置一条 break 语句，遗漏break，可能导致程序误入下一个case分支，执行了预期之外的代码，导致异常。而且，不推荐做出有意不写break的设计。

**修复建议**

在 switch 语句的每一个有内容的 case 中都应放置一条 break 语句

**正确示例**

return ['hello', 'world'];

**错误示例**

}

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:19:43

<a id="rule-10"></a>

### 10. G.CTL.03 case 语句中需要词法声明时, 大括号{}不能省略

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2022-06-17 06:19:43

<a id="rule-11"></a>

### 11. G.CTL.06 不要在控制性条件表达式中执行赋值操作

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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

<a id="rule-12"></a>

### 12. G.DCL.01 声明变量时要求使用 const 或 let 而不是 var（ECMAScript 6）

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_var |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

代码中流式操作的结果往往都是计算结果，如果忽略返回结果可能导致程序产生非预期的结果

**修复建议**

要对流式计算的结果进行判断，防止程序出现意外情况

**正确示例**

```text
eturn serviceIds.stream()
.map(
along -> {
bulkTypeServiceMappingList.add(
BulkTypeServiceMapping.builder()
.bulkTypeServiceId(
IDUtil.generateIDLong(IDGeneratorKey.BULK_TYPE_SERVICE_ID))
.serviceId(along)
.bulkType(bulkType)
.approveSwitch(null)
.tenantId(tenantId)
.archiveDate(DateAS.getCurrentTimestamp())
.lastUpdateTime(DateAS.getCurrentTimestamp())
.createTime(DateAS.getCurrentTimestamp())
.build());
return null;
})
.collect(Collectors.toList());
```

**错误示例**

```text
serviceIds.stream()
.map(
along -> {
bulkTypeServiceMappingList.add(
BulkTypeServiceMapping.builder()
.bulkTypeServiceId(
IDUtil.generateIDLong(IDGeneratorKey.BULK_TYPE_SERVICE_ID))
.serviceId(along)
.bulkType(bulkType)
.approveSwitch(null)
.tenantId(tenantId)
.archiveDate(DateAS.getCurrentTimestamp())
.lastUpdateTime(DateAS.getCurrentTimestamp())
.createTime(DateAS.getCurrentTimestamp())
.build());
return null;
})
.collect(Collectors.toList());
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-13"></a>

### 13. G.DCL.03 每个语句只声明一个变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | one_var |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

map集合key对象没有实现hashCode和equals方法会出现与期望不一致结果性能损耗等严重问题，导致程序出现许多不可知的问题

**修复建议**

使用对象作为Map的key，要保证对象类实现了hashCode和equals方法

**正确示例**

```text
Map<Task, Integer> rationMapCopy = new HashMap<>(bulkTaskRations);
…
public class Task {
private final TaskPlan taskPlan;
// 放置扩展信息数据
private Map<String, String> extendDataMap = new HashMap<String, String>();
public Map<String, String> getExtendDataMap() {
return extendDataMap;
}
public void setExtendDataMap(Map<String, String> extendDataMap) {
this.extendDataMap = extendDataMap;
}
public Task(BulkType bulkType, TaskPlan taskPlan, JobTaskVo jobTaskVo) {
this.taskPlan = taskPlan;
}
public TaskPlan getTaskPlan() {
return taskPlan;
}
@Override
public boolean equals(Object o) {
if (this == o) {
return true;
}
if (o == null || getClass() != o.getClass()) {
return false;
}
Task task = (Task) o;
return taskPlan.equals(task.taskPlan) && extendDataMap.equals(task.extendDataMap);
}
@Override
public int hashCode() {
return Objects.hash(taskPlan, extendDataMap);
}
}
```

**错误示例**

```text
Map<Task, Integer> rationMapCopy = new HashMap<>(bulkTaskRations);
…
public class Task {
private final TaskPlan taskPlan;
// 放置扩展信息数据
private Map<String, String> extendDataMap = new HashMap<String, String>();
public Map<String, String> getExtendDataMap() {
return extendDataMap;
}
public void setExtendDataMap(Map<String, String> extendDataMap) {
this.extendDataMap = extendDataMap;
}
public Task(BulkType bulkType, TaskPlan taskPlan, JobTaskVo jobTaskVo) {
this.taskPlan = taskPlan;
}
public TaskPlan getTaskPlan() {
return taskPlan;
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:19:27

<a id="rule-14"></a>

### 14. G.DCL.04 禁止连续赋值

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_multi_assign |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

如果在关闭kafka生产者时未指定超时时间，超时时间默认设置为long类型的最大值，因此当KafkaProducer客户端在刷新事务，或者异常重新创建时,可能会导致发送数据重试过程中线程数量一直增加，导致服务OOM

**修复建议**

关闭kafka生产者时需要指定超时时间，避免出现无限阻塞或长时间等待的情况

**正确示例**

```text
produList<String> list = new ArrayList<>(2);
list.add("guan");
list.add("bao");
String[] array = list.toArray(new String[0]);cer.close(Duration.ofSeconds(0L));
```

**错误示例**

producer.close();

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:19:28

<a id="rule-15"></a>

### 15. G.DCL.05 不要使用 undefined 初始化变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_undef_init |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
现网问题规则抽取，
（1）Mysql数据库
（2）在.java代码中检测涉及DDL（data definition language）关键字，会造成MDL锁的批量操作
"ALTER DATABASE" && ("CHARACTER set utf8 collate utf8_bin"||"XXXX(持续梳理)")
其他ALTER涉及表结构变更的，操作持续补充
```

**修复建议**

升级场景，谨慎触发涉及批量修改MYSQL数据库表结构的操作，防止触发大量MDL锁，阻塞数据库其他会话，导致服务不可用

**正确示例**

升级场景，不涉及批量修改MYSQL数据库表结构的操作，防止触发大量MDL锁

**错误示例**

```text
OC环境，升级版本时，服务连接数据库出现 Failed to obtain JDBC Connection ， 导致全部服务实例心跳检查readiness异常， liveness正常， 服务不可用
private void modifyCollate(DruidPooledConnection connection) {
if (connection == null) {
return;
}
try (Statement statement = connection.createStatement()){
log.info("Modify collate '{}' start.", schema);
String sql = "ALTER DATABASE " + schema + " CHARACTER set utf8 collate utf8_bin";
statement.executeUpdate(sql);
} catch (SQLException e) {
log.error("Modify collate failed! Schema: {}", schema);
} finally {
try {
connection.close();
} catch (SQLException e) {
log.error("Connection close error!");
}
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-16"></a>

### 16. G.DCL.06 使用字面量风格的声明

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_new_wrappers,no_array_constructor,no_new_object |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

java采用公共安全组件执行命令

**修复建议**

参考WIKI，使用CBB组件提供的公共方法执行命令/脚本。

**正确示例**

```text
##### 场景1：文件头注释没有首先包含“版权说明”
- 修复示例：文件头注释首先包含“版权说明”，然后包含其他可选内容
```go
/*
* Copyright (c) 公司 2021-2021. All rights reserved.
* ...
*/
/* 其它说明：... */
```
##### 场景2：版权说明中公司和注释时间顺序错误
- 修复示例：文件头注释首先包含“版权说明”，版权说明的内容及格式必须和规范规定的一致，然后包含其他可选内容
```go
/*
* Copyright (c) 公司 2021-2021. All rights reserved.
* ...
*/
```
```

**错误示例**

```text
##### 场景1：文件头注释没有首先包含“版权说明”
- 错误示例：
```go
/* 其它说明：... */
/*
* Copyright (c) 公司 2021-2021. All rights reserved.
* ...
*/
```
##### 场景2：版权说明中公司和注释时间顺序错误
- 错误示例：
```go
/*
* 2021-2021. Copyright (c) 公司 All rights reserved.
* ...
*/
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:19:29

<a id="rule-17"></a>

### 17. G.ENU.02-TS 应该显式定义枚举属性值

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | prefer_enum_initializers |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
枚举值的默认值是根据定义顺序排序的，如果在后期附加枚举值定义，则很可能破坏原有语义，建议显式定义枚举的值。
只定义了枚举对象，默认不赋值，当前枚举对象的第一个值从0开始递增。
枚举对象的成员值递增只会根据当前值的前一个枚举成员是否有值，有值的话后面依次递增，与第一个枚举成员的值无关。
字符串的枚举值没有值递增。
```

**修复建议**

应该显式定义枚举属性值

**正确示例**

```text
enum Direction {
UP = 0,
DOWN = 1,
}
```

**错误示例**

```text
enum Direction {
UP,
DOWN,
}
console.log(Direction.UP, Direction.DOWN); // 输出0,1；原因：只定义了枚举对象，默认不赋值，当前枚举对象的第一个值从0开始递增
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:20:17

<a id="rule-18"></a>

### 18. G.ENU.03-TS 枚举值应该使用字面量而非变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | prefer_literal_enum_member |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

枚举值一般具有确定性，如果在枚举值定义时使用变量，在运行时的变量取值具有不确定性，可能会导致问题。

**修复建议**

枚举值应该使用字面量而非变量

**正确示例**

```text
// 使用字面量赋值枚举成员属性值
//将@typescript-eslint/prefer-literal-enum-member中的参数allowBitwiseExpressions设置为true将允许你在enum初始化器中使用按位表达式(默认值为false)
enum Person {
NUM = '1',
NAME = 'Jony' ,
AGE = 14,
DESC = 'Cute Boy!',
DEGREE = null,
FILTER = /some_regex/
}
```

**错误示例**

```text
import data from 'Persons';
// 除了NUM，其他会报错：不允许在具有字符串值成员的枚举中使用计算值
enum Person {
NUM = '1',
NAME = data.first + data.last ,
AGE = data.age,
DESC = `${data.desc}!`,
DEGREE = new Set(1, 2, 3),
FILTER = data.regex
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:20:17

<a id="rule-19"></a>

### 19. G.ERR.03 不要使用return、break、continue或抛出异常使finally块非正常结束

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2022-06-17 06:20:01

<a id="rule-20"></a>

### 20. G.EXP.02 判断相等时应使用 === 和 !== ，而不是 == 和 !=

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | eqeqeq |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

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

<a id="rule-21"></a>

### 21. G.EXP.03 禁止使用嵌套的三元表达式

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2022-06-17 06:19:40

<a id="rule-22"></a>

### 22. G.MET.08 不要动态创建函数

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_new_func |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

定义函数的方法包括3种：函数声明、Function构造函数和函数表达式。不管用哪种方法定义函数，它们都是Function对象的实例，并将继承Function对象所有默认或自定义的方法和属性。以函数构造器创建函数的方式类似于函数eval(),可以接受任何字符串形式作为它的函数体，这就会有安全漏洞的风险。

**修复建议**

不要动态创建函数

**正确示例**

```text
// 函数声明
function add(a,b){
return a+b;
}
// 函数表达式
let add = function(a,b){
return a+b;
}
```

**错误示例**

```text
let add = new Function('a','b','return a + b');
// Function构造函数也可以只有一个参数，该参数可以为任意的字符串:
let dd = new Function('alert("hello")');
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:19:51

<a id="rule-23"></a>

### 23. G.MET.10 不要使用 arguments，可以选择 rest 语法替代

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2022-06-17 06:19:52

<a id="rule-24"></a>

### 24. G.MET.12 用到匿名函数时优先使用箭头函数，替代保存this引用的方式

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2022-06-17 06:19:53

<a id="rule-25"></a>

### 25. G.MOD.01 需要导出的变量必须是不可变的类型

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_export_variable |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

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
- 最新更新时间：2022-06-17 06:19:59

<a id="rule-26"></a>

### 26. G.NOD.04 解压文件必须进行安全检查

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_decompress_file_secure_check |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
攻击者有可能上传一个很小的zip文件，但是完全解压缩之后达到几百万GB甚至更多，消耗完磁盘空间导致系统或程序无法正常运行。因此在解压缩文件时需要限制所使用的磁盘空间。
因此，在解压前最少进行以下几点基础安全检查：
1.文件个数是否超出业务预期个数；
2.文件大小是否超出业务预期大小。
```

**修复建议**

解压文件必须进行安全检查

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

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-03-22 14:17:43

<a id="rule-27"></a>

### 27. G.OBJ.04 优先使用点号来访问对象的属性，只有计算属性使用 []

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | dot_notation |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

在 JavaScript 中，可以使用点号 (foo.bar) 或者方括号 (foo['bar'])来访问属性。然而，点号通常是首选，因为它更加易读，简洁，也更适于 JavaScript 压缩。

**修复建议**

使用点号来访问对象的属性，只有计算属性使用 []

**正确示例**

const name = obj.name;

**错误示例**

var x = foo["bar"];

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-01-13 21:37:04

<a id="rule-28"></a>

### 28. G.OBJ.05 禁止在对象实例上直接使用 hasOwnProperty、isPrototypeOf、propertyIsEnumerable方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_prototype_builtins |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
对象实例可以具有属性，这些属性可以将 Object.prototype 的内建函数隐藏，可能导致意外行为或拒绝服务安全漏洞。例如，web 服务器解析来自客户机的 JSON 输入并直接在结果对象上调用 hasOwnProperty 是不安全的，因为恶意客户机可能发送一个JSON值，如 { hasOwnProperty: 1 }，并导致服务器崩溃。因此，禁止在对象实例上直接使用 hasOwnProperty、isPrototypeOf、propertyIsEnumerable方法。
```

**修复建议**

禁止在对象实例上直接使用 hasOwnProperty、isPrototypeOf、propertyIsEnumerable方法

**正确示例**

const hasBarProperty = Object.prototype.hasOwnProperty.call(foo, 'bar');

**错误示例**

const hasBarProperty = foo.hasOwnProperty('bar');

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-21 17:57:35

<a id="rule-29"></a>

### 29. G.OBJ.06 需要约束 for-in

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2022-06-17 06:19:57

<a id="rule-30"></a>

### 30. G.OBJ.07 不要修改内置对象的原型，或向原型添加方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_extend_native |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

内置对象作为一套公共接口，具有约定俗成的行为方式，若修改其原型，可能破坏接口语义。因此，永远不要修改内置对象的原型，或向原型添加方法。

**修复建议**

不要修改内置对象的原型，或向原型添加方法

**正确示例**

const ages: number[] = [18, 19];

**错误示例**

function greet(): any {}

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:19:57

<a id="rule-31"></a>

### 31. G.OTH.01 安全场景下必须使用密码学意义上的安全随机数

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_insecure_randomness |
| 规则类型 | 通用规范规则类 |
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
- 最新更新时间：2025-12-04 09:30:02

<a id="rule-32"></a>

### 32. G.OTH.03 不用的代码段直接删除，不要注释掉

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_commented_code |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
被注释掉的代码，无法被正常维护；当企图恢复使用这段代码时，极有可能引入易被忽略的缺陷。
无效或永不执行的代码（即无效代码或无法访问的代码）通常是编程错误的结果，并且可能导致意外行为。
正确的做法是，不需要的代码直接删除掉。若再需要时，考虑移植或重写这段代码。无效或永不执行的代码应及时识别，删除或修正。
```

**修复建议**

不用的代码段直接删除，不要注释掉

**正确示例**

不用的代码段直接删除，不要注释掉

**错误示例**

```text
mModel.reloadIcons();
// if (!mModel.isAllAppsLoaded()) {
// function make(tag) {
// return element;
// }
// }
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-33"></a>

### 33. G.OTH.04 正式发布的代码及注释内容不应包含开发者个人信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_personal_info_in_comment |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

JavaScript属于解释性语言，正式发布的代码及注释内容如果包含开发者个人信息，可能会泄露具体的开发人员信息，存在引发社会工程学方面的风险。因此应从正式发布的版本中删除开发者个人信息，比如工号、姓名、部门、邮箱、问题单号等。

**修复建议**

正式发布的代码及注释内容不应包含开发者个人信息

**正确示例**

正式发布的代码及注释内容不应包含开发者个人信息

**错误示例**

//@email user@example.com

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-34"></a>

### 34. G.OTH.05 禁止代码中包含公网地址

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2025-08-06 17:12:56

<a id="rule-35"></a>

### 35. G.SCO.02 禁止使用 with() {}

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_with |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

使用with让代码在语义上变得不清晰，因为with的对象，可能会与局部变量产生冲突，从而改变程序原本的用义。

**修复建议**

禁止使用 with() {}

**正确示例**

```text
const foo = { x: 5 };
foo.x = 3
console.log(foo.x); // x = 3
```

**错误示例**

```text
const foo = { x: 5 };
with (foo) {
let x = 3;
console.log(x); // x = 3
}
console.log(foo.x); // x = 3
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-21 17:57:34

<a id="rule-36"></a>

### 36. G.TYP.01 禁止省略浮点数小数点前后的 0

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_floating_decimal |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

由于这个原因，必须在小数点前面和后面有一个数字，以明确表明是要创建一个小数。

**修复建议**

小数不要省略.前面0

**正确示例**

const num = 0.5;

**错误示例**

const num = .5;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:19:31

<a id="rule-37"></a>

### 37. G.TYP.02 判断变量是否为NaN时必须使用 isNaN() 方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | use_isnan |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

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
- 最新更新时间：2022-06-17 06:19:32

<a id="rule-38"></a>

### 38. G.TYP.03 浮点型数据判断相等不要直接使用== 或===

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_float_equal |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

由于浮点数在计算机表示中存在精度的问题，数学上相等的数字，经过运算后，其浮点数表示可能不再相等，因而不能使用相等运算符== 或===判断浮点数是否相等。

**修复建议**

浮点型数据判断相等不要直接使用== 或===

**正确示例**

const num1 = 0.1;

**错误示例**

0.1 + 0.2 == 0.3;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:19:32

<a id="rule-39"></a>

### 39. G.TYP.07 不要在数组上定义或使用非数字属性（length 除外）

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_array_property |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

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
- 最新更新时间：2022-06-17 06:19:34

<a id="rule-40"></a>

### 40. G.TYP.09 不要在数组遍历中对其进行元素的 remove/add 操作

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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
- 最新更新时间：2022-06-17 06:19:36

<a id="rule-41"></a>

### 41. G.TYP.14 使用显式的类型转换

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | no_implicit_coercion |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

在JavaScript中，有许多不同的方式进行类型转换。其中有些可能难于阅读和理解，因此，应该使用显式的类型转换。

**修复建议**

使用显式的类型转换

**正确示例**

```text
const baz = foo.indexOf('.') !== -1;
const num = Number(foo);
const num = Number(foo);
const str = String(foo);
```

**错误示例**

```text
const baz = ~foo.indexOf('.');
const num = +foo;
const num = 1 * foo;
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-06-17 06:19:37

<a id="rule-42"></a>

### 42. G.WSQ.01 禁止在 Web SQL database和Indexed database数据库中存储敏感数据

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_web_sql_database |
| 规则类型 | 通用规范规则类 |
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
- 最新更新时间：2022-06-21 17:57:34

<a id="rule-43"></a>

### 43. G.WST.01 禁止在 localStorage和sessionStorage中存储敏感数据

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_sensitive_local_storage |
| 规则类型 | 通用规范规则类 |
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
- 最新更新时间：2022-09-30 11:37:42

<a id="rule-44"></a>

### 44. G.WST.02 如果数据仅需要临时存储在客户端，使用非持久性会话cookie或者sessionStorage而不是localStorage

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_staged_data_local_storage |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

localStorage是持久性（没有时间限制）的数据存储，数据永远不会过期的，除非主动删除数据，不应该用于存储临时数据，否则会导致数据长期存留，增加数据泄露的风险，同时，浪费客户端的磁盘空间。而存储于非持久性会话cookie或sessionStorage中的数据会被及时清除。

**修复建议**

如果数据仅需要临时存储在客户端，使用非持久性会话cookie或者sessionStorage而不是localStorage

**正确示例**

localStorage.setItem('key',value);

**错误示例**

localStorage.setItem('key',temp);

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2023-03-22 14:17:42

<a id="rule-45"></a>

### 45. [试行]SecJS_Open_Redirect

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | TYPESCRIPT |
| 标签 | SecBrella |
| 关联工具规则 | SecJS_Open_Redirect |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

如果允许未验证的输入控制重定向机制所使用的 URL，可能会有利于攻击者发动钓鱼攻击。

**参考信息**

- 最新更新时间：2024-03-11 15:34:26

<a id="rule-46"></a>

### 46. do not use insecure randomness function

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | TYPESCRIPT |
| 标签 | CodeMars |
| 关联工具规则 | SecJS_no_insecure_randomness |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

禁止使用不安全的随机数

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-12-16 10:08:57

<a id="rule-47"></a>

### 47. duplication_file[TYPESCRIPT]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | cmetrics |
| 关联工具规则 | duplication_file |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

重复文件：计算所有文件的md5值，如果md5值一样，则认为是重复文件。

**修复建议**

高重复率意味着相同或类似功能的单板、模块或功能单元缺乏抽象和管理。代码重构，提升代码的抽象和管理可以减少重复文件。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 最新更新时间：2020-11-18 23:17:46

<a id="rule-48"></a>

### 48. huge_cca_cyclomatic_complexity[TYPESCRIPT]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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

- 最新更新时间：2020-12-24 15:02:41

<a id="rule-49"></a>

### 49. huge_cyclomatic_complexity[TYPESCRIPT]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | TYPESCRIPT |
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

- 最新更新时间：2020-11-18 23:17:46

<a id="rule-50"></a>

### 50. huge_depth[TYPESCRIPT]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
| 标签 | cmetrics |
| 关联工具规则 | huge_depth |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
超大深度函数：函数深度默认为1，每出现以下关键字一次且关键字之间为嵌套关系，则该代码片段深度加1，若关键字之间不是嵌套关系，则深度不变。
c/c++,java,microcode,javascrip,typescript,c#,cangjie,arkts: if|else if|for|switch|do|while|try|catch|finally
go:else if|if|for|switch|select|func
lua:if|for|while|repeat|until|end
rust: if|match|for|while|loop|fn
python 按照缩进计算深度
注意！！！闭包也会计算深度，但是不会计算圈复杂度，即：其他大括号嵌套场景也会计算深度。
超大深度的阈值可以在CodeCheck上进行配置。
注意：并不是每一处超大深度函数的告警，都需要修复，度量是一个全局数据，仅是对代码的整体现状进行评估；
产品自己选择此规则，并进行扫描，意图在于：将可能需要重构的代码扫描出来进行确认，这也是业界的通用方式；这个扫描跟业务无关，不会考虑业务实际需要多少行可以完成功能；
并且度量：是以业务优先，并牵引更好的架构和设计；他产生告警，是为了指出可疑的超大深度函数，而不是要消灭一切超大深度函数，确认合理的超大深度函数，告警可以屏蔽。
关于规则和场景使用说明，请见：内部链接已省略
```

**修复建议**

降低函数复杂度，可以解决超大深度函数问题，牵引良好设计。

**正确示例**

度量指标，无正确示例。

**错误示例**

度量指标，无错误示例。

**参考信息**

- 最新更新时间：2025-07-09 17:19:55

<a id="rule-51"></a>

### 51. huge_folder[TYPESCRIPT]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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

- 最新更新时间：2020-11-18 23:17:46

<a id="rule-52"></a>

### 52. huge_method[TYPESCRIPT]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | TYPESCRIPT |
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

- 最新更新时间：2020-11-18 23:17:00

<a id="rule-53"></a>

### 53. huge_non_headerfile[TYPESCRIPT]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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

- 最新更新时间：2020-11-18 23:17:46

<a id="rule-54"></a>

### 54. redundant_code[TYPESCRIPT]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | TYPESCRIPT |
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

- 最新更新时间：2020-11-18 23:17:46

