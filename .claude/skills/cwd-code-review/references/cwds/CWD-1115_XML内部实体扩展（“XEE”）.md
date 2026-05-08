# CWD-1115 XML内部实体扩展（“XEE”）

**别名: **XEE；XML炸弹；指数型实体扩展攻击；DTD中递归实体引用的限制不当（“XML实体扩展”）；DTD中不受限制的递归实体引用（“XML炸弹”）

**描述**
XEE攻击，是XML实体扩展（XML Entity Expansion attack），攻击利用XML DTD中的一种功能，该功能允许创建可在整个文档中使用的自定义宏（称为实体）。通过在文档顶部递归定义一组自定义实体，攻击者可以通过强制解析器几乎无限期地在这些递归定义上迭代来压倒试图完全解析实体的解析器。恶意XML消息用于强制递归实体扩展（或其他重复处理），完全耗尽可用服务器资源。

**语言: **JAVA

**严重等级**
严重

**cleancode特征**
安全,可靠,高效

**示例**
**案例1: 通过禁用 DTD 加载防止`DocumentBuilderFactory`解析器的 XEE**
**语言: **JAVA

**描述**
某系统允许用户上传XML格式的订单文件。系统后端使用Java的`DocumentBuilderFactory`来解析这些XML文件。攻击者发现该系统存在XML内部实体扩展（XEE）漏洞，通过上传一个包含恶意实体定义的XML文件，导致服务器内存耗尽，服务中断。

**案例分析**
使用`DocumentBuilderFactory`来解析XML文件，但默认情况下允许实体扩展。攻击者可以构造一个包含恶意实体的XML文件，导致实体递归展开，耗尽服务器内存。

**反例**
```java
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import java.io.File;

public class VulnerableXMLParser {
    public static void main(String[] args) {
        try {
            File inputFile = new File("malicious.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            Logger.info("XML parsed successfully");
        } catch (Exception e) {
            // ...异常处理
        }
    }
}
```

**正例**
```java
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import java.io.File;

public class SecureXMLParser {
    public static void main(String[] args) {
        try {
            File inputFile = new File("safe.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            // 禁用实体扩展
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            Logger.info("XML parsed successfully");
        } catch (Exception e) {
            // ...异常处理
        }
    }
}
```

**修复建议**
- 通过设置多个安全相关的特性，禁用了外部实体和文档类型声明（DTD）的加载，确保在解析XML时不会处理外部实体，从而避免内存耗尽的风险。

#### CWD-1115-000 XML内部实体扩展（“XEE”）

#### CWD-1115-001 【使用JDK组件解析XML内容导致实体扩展攻击】javax.xml.bind.JAXBContext造成的内部实体扩展攻击

#### CWD-1115-002 【使用JDK组件解析XML内容导致实体扩展攻击】javax.xml.parsers.DocumentBuilderFactory造成的内部实体扩展攻击

#### CWD-1115-003 【使用JDK组件解析XML内容导致实体扩展攻击】javax.xml.parsers.SAXParserFactory造成的内部实体扩展攻击

#### CWD-1115-004 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.stream.XMLInputFactory造成的内部实体扩展攻击

#### CWD-1115-005 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.transform.sax.SAXTransformerFactory造成的内部实体扩展攻击

#### CWD-1115-006 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.transform.TransformerFactory造成的内部实体扩展攻击

#### CWD-1115-007 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.validation.SchemaFactory造成的内部实体扩展攻击

#### CWD-1115-008 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.xpath.XPathExpression造成的内部实体扩展攻击

#### CWD-1115-009 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】org.xml.sax.helpers.XMLReaderFactory造成的内部实体扩展攻击

#### CWD-1115-010 【使用jdom2解析XML内容存在实体注入的问题】org.jdom2.input.SAXBuilder造成的内部实体扩展攻击

#### CWD-1115-011 【使用xerces解析XML内容存在实体注入的问题】org.apache.xerces.jaxp.DocumentBuilderFactoryImpl造成的内部实体扩展攻击

#### CWD-1115-012 【使用dom4j解析XML内容存在实体注入的问题】org.dom4j.io.SAXReader造成的内部实体扩展攻击

**业界缺陷**

- [CWE-776: Improper Restriction of Recursive Entity References in DTDs ('XML Entity Expansion')](https://cwe.mitre.org/data/definitions/776.html)
---

