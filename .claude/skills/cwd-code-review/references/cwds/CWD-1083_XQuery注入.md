# CWD-1083 XQuery注入

**别名: **XQuery表达式中的数据中和不当（“XQuery注入”）；未安全处理XQuery输入；未能清理XQuery表达式中的数据（“XQuery注入”）

**描述**
使用外部输入动态构造用于从XML数据库检索数据的XQuery表达式，但未处理或不正确处理该输入，这允许攻击者控制查询的结构，可以控制从XML数据库中选择的信息，并可能使用这种能力来控制应用程序流，修改逻辑，检索未经授权的数据，或者绕过重要的检查（例如身份验证）。

**语言: **JAVA

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: 未对用户输入进行处理导致XQuery注入**
**语言: **JAVA

**描述**
一个Java应用程序中存在的XQuery注入漏洞。该应用程序允许用户通过输入员工ID来查询员工信息，后端使用XQuery从XML数据库中检索数据。由于未对用户输入进行适当处理，攻击者可以构造恶意输入来修改XQuery查询逻辑，可能导致未授权数据访问或数据库信息泄露。

**案例分析**
代码直接将用户提供的empId参数拼接到XQuery查询字符串中，没有进行任何验证或转义。
- 正常输入：`"12345"` → 查询ID为12345的员工
- 恶意输入：`"' or '1'='1"` → 查询变为：
```xquery
for $emp in /employees/employee where $emp/id = '' or '1'='1' return $emp
```
这将返回所有员工记录，而不仅仅是匹配ID的记录

**反例**
```java
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;
import net.xqj.exist.ExistXQDataSource;

public class EmployeeQuery {
    public void queryEmployee(String empId) {
        XQDataSource xqs = new ExistXQDataSource();
        try {
            xqs.setProperty("serverName", "localhost");
            xqs.setProperty("port", "8080");
            
            XQConnection conn = xqs.getConnection();
            XQExpression expr = conn.createExpression();
            
            // 有风险的XQuery构造方式 - 直接拼接用户输入
            String query = "for $emp in /employees/employee " +
                           "where $emp/id = '" + empId + "' " +
                           "return $emp";
            
            XQResultSequence result = expr.executeQuery(query);
            
            // 处理查询结果...
            while (result.next()) {
                Logger.info(result.getItemAsString(null));
            }
            
            result.close();
            expr.close();
            conn.close();
        } catch (XQException e) {
            // ...异常处理
        }
    }
}
```

**正例**
```java
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import net.xqj.exist.ExistXQDataSource;

public class SecureEmployeeQuery {
    public void queryEmployee(String empId) {
        // 输入验证
        if (empId == null || !empId.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Invalid employee ID");
        }
        
        XQDataSource xqs = new ExistXQDataSource();
        try {
            xqs.setProperty("serverName", "localhost");
            xqs.setProperty("port", "8080");
            
            XQConnection conn = xqs.getConnection();
            
            // 使用参数化查询
            String query = "declare variable $id as xs:string external; " +
                           "for $emp in /employees/employee " +
                           "where $emp/id = $id " +
                           "return $emp";
            
            XQPreparedExpression expr = conn.prepareExpression(query);
            expr.bindString(new QName("id"), empId, null);
            
            XQResultSequence result = expr.executeQuery();
            
            // 处理查询结果...
            while (result.next()) {
                Logger.info(result.getItemAsString(null));
            }
            
            result.close();
            expr.close();
            conn.close();
        } catch (XQException e) {
            // ...异常处理
        }
    }
}
```

**修复建议**
- 输入验证：在将用户输入用于查询前，验证其是否符合预期格式（本例中只允许数字）。
- 参数化查询：使用 `XQPreparedExpression` 代替简单的 `XQExpression` ；在XQuery中声明外部变量；使用 `bindString` 方法安全地绑定参数值。
- 最小权限原则：确保数据库连接使用具有最小必要权限的账户。

#### CWD-1083-000 XQuery注入

#### CWD-1083-001 修改XML查询条件

#### CWD-1083-002 XML文档篡改

**业界缺陷**

- [CWE-652: Improper Neutralization of Data within XQuery Expressions ('XQuery Injection')](https://cwe.mitre.org/data/definitions/652.html)
---

