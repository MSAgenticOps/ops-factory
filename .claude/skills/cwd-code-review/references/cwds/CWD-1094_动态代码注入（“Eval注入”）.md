# CWD-1094 动态代码注入（“Eval注入”）

**别名: **动态执行代码中的指令控制/清理/中和不当（“Eval注入”）；直接执行动态代码（“Eval注入”）

**描述**
从上游组件接收输入，但在动态评估调用（例如`eval`）中使用输入之前，不会中和或不正确地中和代码语法。这可能允许攻击者执行任意代码，或者至少修改可以执行的代码。


- 注入的代码可以访问受限的数据/文件。- 在某些情况下，可注入代码控制身份验证；这可能导致远程漏洞。- 通过注入的代码，攻击者可以直接访问资源。- 当产品允许用户的输入包含代码语法时，攻击者可能会精心制作代码，从而改变产品的预期控制流。因此，代码注入通常会导致执行任意代码。代码注入攻击在几乎所有情况下都可能导致数据完整性的丧失，因为注入的控制平面数据总是在数据调用或写入时偶然发生的。- 通常，注入的控制代码执行的操作是未记录的。
**语言: **JAVA,PYTHON

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: 使用eval()拼接外部命令导致代码注入**
**语言: **JAVA

**描述**
动态代码注入（Eval注入）是指应用程序接收用户输入并将其作为代码直接执行的安全漏洞。在Java中，虽然不像某些脚本语言有直接的`eval()`函数，但通过脚本引擎（如JavaScript引擎）或反射机制，仍然可能实现类似功能。攻击者可以利用这种漏洞执行任意代码，导致服务器被完全控制。

**案例分析**
- 直接执行用户输入：代码直接将用户提供的字符串作为JavaScript代码执行，没有任何过滤或验证。
- 任意代码执行：攻击者可以构造恶意输入执行任意Java代码，如启动计算器（示例中）、删除文件、下载恶意软件等。
- 权限提升：执行的代码具有与Java应用程序相同的权限，可能导致服务器完全沦陷。
- 脚本引擎风险：使用脚本引擎（如JavaScript）时，攻击者可以利用脚本语言的特性绕过一些Java安全限制。

**反例**
```java
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalInjectionVulnerable {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        
        // 用户提供的输入（实际可能来自HTTP请求参数）
        String userInput = "java.lang.Runtime.getRuntime().exec('calc.exe')";
        
        try {
            // 直接执行用户输入的代码
            engine.eval(userInput);
        } catch (ScriptException e) {
            // ...异常处理;
        }
    }
}
```

**正例**
```java
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.Map;
import java.util.HashMap;

public class EvalInjectionFixed {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        
        // 用户提供的输入（实际可能来自HTTP请求参数）
        String userInput = "2 + 3"; // 只允许数学表达式
        
        // 验证输入是否为简单的数学表达式
        if (!isSafeMathExpression(userInput)) {
            // ...处理;;
            return;
        }
        
        try {
            // 使用绑定对象限制可访问的变量和方法
            Map<String, Object> bindings = new HashMap<>();
            bindings.put("allowedValue", 5);
            
            // 在受限环境中执行
            Object result = engine.eval(userInput, new SimpleBindings(bindings));
            Logger.info("Result: " + result);
        } catch (ScriptException e) {
            // ...异常处理;
        }
    }
    
    private static boolean isSafeMathExpression(String input) {
        // 只允许数字、基本运算符和括号
        return input.matches("^[0-9+\\-*/()\\s.]+$");
    }
}
```

**修复建议**
- 输入验证：严格验证用户输入，只允许预期的格式（如示例中只允许简单的数学表达式）。
- 使用绑定对象：通过 `SimpleBindings` 限制脚本可以访问的变量和方法。
- 白名单机制：建立允许的操作和字符白名单，拒绝所有不符合的内容。
- 沙箱环境：考虑使用Java安全管理器创建沙箱环境，限制脚本引擎的权限。
- 避免动态执行：尽可能寻找不需要动态执行代码的替代方案。

**案例2: 使用eval()拼接外部命令导致代码注入**
**语言: **PYTHON

**描述**
脚本要求用户提供一个数字列表作为输入，并将它们相加。

**案例分析**
`eval()`函数可以获取用户提供的列表并将其转换为Python列表对象，因此允许程序员使用列表理解方法来处理数据。但是，如果将代码提供给`eval()`函数，它将执行该代码。例如，恶意用户可以提供以下字符串：
```python
__import__('subprocess').getoutput('rm -r *')
```
这将删除当前目录中的所有文件。因此，不建议将`eval()`与不可信输入一起使用。

**反例**
```python
def main():
sum = 0
numbers = eval(input("Enter a space-separated list of numbers: "))
for num in numbers:
sum = sum + num
print(f"Sum of {numbers} = {sum}")
main()
```

**正例**
```python
def main():
sum = 0
numbers = input("Enter a space-separated list of numbers: ").split(" ")
try:
for num in numbers:
sum = sum + int(num)
print(f"Sum of {numbers} = {sum}")
except ValueError:
print("Error: invalid input")
main()
```

**修复建议**
- 在不使用`eval()`的情况下实现此目的的一种方法是在`try/extect`块中对输入应用整数转换。如果用户提供的输入不是数字，这将引发ValueError。通过避免`eval()`，输入字符串没有机会作为代码执行。
- 另一种常用的缓解方法是使用`ast.iteral_eval()`函数，因为它是有意避免执行代码的。但是，攻击者仍然可能通过深度嵌套的结构导致过多的内存或堆栈消耗。所以python文档不鼓励在不可信的数据上使用`ast.iteral_eval()`。

#### CWD-1094-000 动态代码注入（“Eval注入”）

#### CWD-1094-001 动态回调函数注入

#### CWD-1094-002 数据存储字段代码化

**业界缺陷**

- [CWE-95: Improper Neutralization of Directives in Dynamically Evaluated Code ('Eval Injection')](https://cwe.mitre.org/data/definitions/95.html)
---

