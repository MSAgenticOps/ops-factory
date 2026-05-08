# CWD-1101 SQL注入

**别名: **SQL命令中使用的特殊元素中和不当（“SQL注入”）；不可信数据直接拼接SQL语句；未能维持SQL查询结构（“SQL注入”）；SQL命令中使用的特殊元素清理不当（“SQL注入”）

**描述**
<div style="display: flex; align-items: center">
<div style="float: left; width: 80%;">
1. SQL注入（`SQL Injection` 或 `SQLi`）通常允许攻击者从易受攻击的网站中提取整个数据库，包括用户信息、加密密码和业务数据。这可能会导致用户帐户的大规模入侵，数据被加密并持有以进行赎金，或者被盗数据被出售给第三方。许多SQL注入漏洞可以很容易地使用现成的工具被发现和滥用，这使得被利用的可能性更高。2. 当应用程序以不安全的方式将用户数据合并到数据库查询中时，通常会出现SQL注入漏洞。攻击者可以操纵数据查询，从而读取或修改数据库的内容。攻击者也可以提供精心编制的输入，以脱离其输入出现在其中的数据上下文，包括读取或修改关键应用程序数据、干扰应用程序逻辑、提升数据库中的权限以及控制数据库服务器。
</div>
<div style="float: right; width: 30%;">```mermaid
sequenceDiagram
    participant 攻击者
    participant 目标系统

    攻击者->>目标系统: 发送恶意请求（信息收集），分析页面结构和输入字段
    目标系统-->>攻击者: 返回页面内容或数据库错误信息

    攻击者->>目标系统: 发送测试请求（注入点识别），确认注入点
    目标系统-->>攻击者: 返回预期响应

    攻击者->>目标系统: 发送恶意SQL代码（构造攻击）
    目标系统-->>攻击者: 执行恶意SQL并返回数据
```

</div>
</div>**语言: **C,CPP,JAVA,PYTHON

**严重等级**
严重

**cleancode特征**
安全,可靠

**示例**
**案例1: 通过`参数化查询`+`PreparedStatement`防止用户登录功能的SQL注入问题**
**语言: **JAVA

**描述**
用户登录功能，用户输入用户名和密码，系统查询数据库验证用户是否存在有SQL注入漏洞。使用PreparedStatement来防止SQL注入。

**案例分析**
直接将用户输入的username和password拼接到SQL语句中，没有进行任何验证或转义。
攻击者可以通过输入特殊字符（如' OR '1'='1）来改变SQL语句的逻辑，从而绕过身份验证。
示例攻击：
用户名输入：`' OR '1'='1`
密码输入：`' OR '1'='1`
生成的SQL语句：
```sql
SELECT * FROM users WHERE username = '' OR '1'='1' AND password = '' OR '1'='1'
```
这将导致查询返回所有用户，攻击者无需正确密码即可登录。

**反例**
```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServlet {
    public boolean login(String username, String password) {
        String URL = "jdbc:mysql://localhost:3306/testdb"; // 此处为了代码完整硬编码，真实项目采用数据库或者配置文件保存。
        String DBUSER = "root"; // 此处为了代码完整硬编码，真实项目采用数据库或者配置文件加密保存。
        String DBPASSWORD = "password"; // 此处为了代码完整硬编码，真实项目采用数据库或者配置文件加密保存。

        // 危险：直接使用用户传入数据进行SQL拼接
        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";

        try (Connection conn = DriverManager.getConnection(URL, DBUSER, DBPASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            return rs.next();
        } catch (SQLException e) {
            // ...异常处理
            return false;
        }
    }
}
```

**正例**
```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginServlet {
    public boolean login(String username, String password) {
        String URL = "jdbc:mysql://localhost:3306/testdb"; // 此处为了代码完整硬编码，真实项目采用数据库或者配置文件保存。
        String DBUSER = "root"; // 此处为了代码完整硬编码，真实项目采用数据库或者配置文件加密保存。
        String DBPASSWORD = "password"; // 此处为了代码完整硬编码，真实项目采用数据库或者配置文件加密保存。

        // 使用`?`占位符参数化查询
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, DBUSER, DBPASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            // ...异常处理
            return false;
        }
    }
}
```

**修复建议**
- 使用`PreparedStatement`预编译SQL语句，参数用`?`占位符代替。
- 通过setString方法设置参数，确保输入被正确转义，防止SQL注入。

**案例2: 通过`参数化查询`防止用户登录功能的SQL注入问题**
**语言: **PYTHON

**描述**
一个简单的Web应用程序，该程序允许用户通过用户名和密码进行登录。应用程序使用Python和SQLite数据库来存储用户信息。该程序存在一个SQL注入漏洞，攻击者可以利用该漏洞绕过身份验证。

**案例分析**
- 代码直接将用户输入的`username`和`password`拼接到SQL查询字符串中，没有对输入进行任何验证或转义。
- 攻击者可以通过构造特殊的输入（如`admin' OR '1'='1`）来改变SQL查询逻辑，从而绕过身份验证。
- 攻击者输入`username = "admin' OR '1'='1"，password = "anything"`。
- 生成的SQL查询为：`SELECT * FROM users WHERE username='admin' OR '1'='1' AND password='anything'`
- 由于`'1'='1'`总是为真，查询会返回所有用户的记录，导致攻击者无需正确密码即可登录。

**反例**
```PYTHON
import sqlite3

def login(username, password):
    conn = sqlite3.connect('users.db')
    cursor = conn.cursor()
    
    # 存在SQL注入漏洞的查询
    query = f"SELECT * FROM users WHERE username='{username}' AND password='{password}'"
    cursor.execute(query)
    
    result = cursor.fetchone()
    if result:
        print("登录成功！")
    else:
        print("用户名或密码错误！")
    
    conn.close()

