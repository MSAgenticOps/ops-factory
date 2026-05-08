# CWD-1082 XPath注入

**别名: **XPath表达式中的数据中和不当（“XPath注入”）；未安全处理XPath输入；未能清理XPath表达式中的数据（“XPath注入”）

**描述**
使用外部输入动态构造用于从XML数据库检索数据的XPath表达式，但未处理或不正确处理该输入。这允许攻击者控制查询的结构。攻击者将控制从XML数据库中选择的信息，并可能使用这种能力来控制应用程序流，修改逻辑，检索未经授权的数据，或者绕过重要的检查（例如身份验证）。

**语言: **JAVA

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: 使用参数化查询防止XPath注入**
**语言: **JAVA

**描述**
存储身份验证信息的简单XML文档和使用XPath查询检索身份验证信息，用于根据提供的凭据检索主目录。用户输入未经过验证和转义，直接用于构建XPath表达式，导致XPath注入攻击的风险。

**案例分析**
```xml
<users>
<user>
<login>john</login>
<password>abracadabra</password>
<home_dir>/home/john</home_dir>
</user>
<user>
<login>cbc</login>
<password>1mgr8</password>
<home_dir>/home/cbc</home_dir>
</user>
</users>
```
假设用户“john”希望利用XPath注入并在没有有效密码的情况下登录。通过提供username `john` 和 password `' or ''='` ,XPath表达式现在变成
```shell
//users/user[login/text()='john' or ''='' and password/text() = '' or ''='']/home_dir/text()
```
这使得用户"john"在没有有效密码的情况下登录，从而绕过身份验证。

**反例**
```java
public static String getHomeDir(Login login) {
    try {
        // 创建XPath工厂
        XPath xpath = XPathFactory.newInstance().newXPath();
        // 危险：直接使用外部输入
        XPathExpression xlogin = xpath.compile("//users/user[login/text()='" + login.getUserName()
            + "' and password/text() = '" + login.getPassword() + "']/home_dir/text()");
        // 解析XML文件
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File("db.xml"));
        return xlogin.evaluate(doc);
    } catch (XPathExpressionException | SAXException | IOException | DOMException e) {
        // ...处理异常，返回错误信息或默认值
    }
}
```

**正例**
```java
public static String getHomeDir(Login login) {
    try {
        // 创建XPath工厂
        XPath xpath = XPathFactory.newInstance().newXPath();
        // 安全地编译XPath表达式，使用参数化查询
        String xpathExpression = "//users/user[login/text()=? and password/text()=?]/home_dir/text()";
        XPathExpression expr = xpath.compile(xpathExpression);
        // 解析XML文件
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File("db.xml"));
        // 使用参数化查询，避免注入
        String[] params = {login.getUserName(), login.getPassword()};
        return (String) expr.evaluate(doc, XPathConstants.STRING, params);
    } catch (XPathExpressionException | SAXException | IOException | DOMException e) {
        // ...处理异常，返回错误信息或默认值
    }
}
```

**修复建议**
- 参数化查询：使用`?`作为占位符，并在`evaluate`方法中传递参数数组，避免直接拼接用户输入，防止XPath注入。
- 禁用外部实体和脚本执行：设置`DocumentBuilderFactory`和`XPath`的特征，禁用外部实体和脚本执行，防止XXE攻击。
- 异常处理：使用`try-catch`块捕获可能的异常，如`XPathExpressionException`、`SAXException`、`IOException`和`DOMException`，并返回友好的错误信息或默认值，避免程序崩溃。

#### CWD-1082-000 XPath注入

#### CWD-1082-001 【dom4j】使用org.dom4j.Document及其子类下的selectNodes、selectObject、selectSingleNode、valueOf、numberValueOf、matches、createXPath等方法时，参数使用了不可信的外部数据作为XPath，造成XPath注入

#### CWD-1082-002 【JDK】使用javax.xml.xpath.XPathFactory创建的XPath对象编译XPath表达式时，参数使用了不可信的外部数据，造成XPath注入

**业界缺陷**

- [CWE-643: Improper Neutralization of Data within XPath Expressions ('XPath Injection')](https://cwe.mitre.org/data/definitions/643.html)
---

