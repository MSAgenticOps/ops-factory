# CWD-1084 XSLT注入

**别名: **可扩展样式表语言转换中和不当（“XSLT注入”）

**描述**
XSLT注入是一种攻击技术，利用XSLT（Extensible Stylesheet Language Transformations）的功能，通过注入恶意代码来攻击Web应用程序。如果处理未经验证的 XSL 样式表，攻击者可以通过修改XSLT文件中的代码，来执行任意的操作，包括读取敏感数据、修改数据、甚至是控制整个Web应用程序。XSLT injection 会在以下情况中出现：


1. 数据从一个不可信赖的数据源进入程序。2. 数据写入到 XSL 样式表中。3. XSLT处理器启用了危险的扩展功能。
通常，应用程序利用 XSL 样式表来转换 XML 文档的格式。XSL 样式表中包括特殊函数，虽然此类函数能改善转换进程，但如果使用不当也会带来更多漏洞。


- XSLT注入攻击通常发生在Web应用程序中使用XML和XSLT来呈现数据的情况下。攻击者可以在XML数据中注入恶意的XSLT代码，然后将其发送到Web应用程序。当Web应用程序使用XSLT来呈现数据时，更改 XSL 样式表和处理的语义，攻击者的恶意代码会被执行。攻击者可能会更改样式表的输出以启用 XSS (Cross-Site Scripting) 攻击、公开本地文件系统资源的内容或执行任意代码。- 为了防止XSLT注入攻击，开发人员应该遵循安全编码实践，如输入验证、输出编码和安全配置。此外，使用最新版本的XSLT引擎和安全库，以及限制XSLT文件的访问权限也可以提高Web应用程序的安全性。
**语言: **JAVA

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: 通过开启secure-processing防护XSLT注入**
**语言: **JAVA

**描述**
XSLT是一种样式转换标记语言，可以将XML数据转换为另外的XML或其他格式，如HTML网页，纯文字。因为XSLT的功能十分强大，可以导致任意代码执行，当使用TransformerFactory转换XML格式数据的时候，需要添加安全策略禁止不安全的XSLT代码执行。

**反例**
```java
// transformer of StreamSource without setFeature
public static void XsltTrans(String src, String dst, String xslt) {
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    try {
        // 获取转换器对象实例
        /* 【POTENTIAL FLAW】XSLT是一种样式转换标记语言，可以将XML数据档转换为另外的XML或其它格式，
         * 如HTML网页，纯文字。因为XSLT的功能十分强大，可以导致任意代码执行，
         * 当使用TransformerFactory转换XML格式数据的时候，需要添加安全策略禁止不安全的XSLT代码执行。
         */
        Transformer transformer = tf.newTransformer(new StreamSource(xslt));

        // 进行转换
        transformer.transform(new StreamSource(src), new StreamResult(new FileOutputStream(dst)));
    } catch (Exception e) {
        LOGGER.error(e.getMessage());
    }
}
```

**正例**
```java
//create transformer after executing setFeature
public static void XsltTrans(String src, String dst, String xslt) {
    TransformerFactory tf = TransformerFactory.newInstance();
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    try {

        // 【GOOD】转换器工厂设置黑名单，禁用一些不安全的方法，类似XXE防护
        tf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);

        // 获取转换器对象实例
        Transformer transformer = tf.newTransformer(new StreamSource(xslt));

        // 进行转换
        transformer.transform(new StreamSource(src), new StreamResult(new FileOutputStream(dst)));
    } catch (Exception e) {
        LOGGER.error(e.getMessage());
    }
}
```

**修复建议**
- 使用TransformerFactory对xml进行格式转换操作时，要开启其安全防护策略。

**案例2: 处理未经验证的XSL样式表导致XSLT注入**
**语言: **JAVA

**描述**
如果处理未经验证的 XSL 样式表，则可能会使攻击者能够更改生成的 XML 的结构和内容、在文件系统中加入任意文件或执行任意代码。

**案例分析**
当攻击者将标识的 XSL 传递到 XSTL 处理器时，下列代码会导致三种不同的漏洞利用：
1. 在处理 XSL 样式表时，`<script>` 会进入受害者的浏览器，从而能够实施 `cross-site scripting` 攻击。
```xml
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<script>alert(123)</script>
</xsl:template>
</xsl:stylesheet>
```
2. 读取服务器文件系统中的任意文件： XSL 样式表将返回 `/etc/passwd` 文件的内容。
```xml
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<xsl:copy-of select="document('/etc/passwd')"/>
</xsl:template>
</xsl:stylesheet>
```
3. 执行任意 Java 代码：XSLT 处理器如果不禁用，能将本机 Java 语言方法暴露为 XSLT 函数。样式表将在服务器上执行 `ls` 命令。
```xml
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rt="http://xml.apache.org/xalan/java/java.lang.Runtime" xmlns:ob="http://xml.apache.org/xalan/java/java.lang.Object">
<xsl:template match="/">
<xsl:variable name="rtobject" select="rt:getRuntime()"/>
<xsl:variable name="process" select="rt:exec($rtobject,'ls')"/>
<xsl:variable name="processString" select="ob:toString($process)"/>
<xsl:value-of select="$processString"/>
</xsl:template>
</xsl:stylesheet>
```

**反例**
```java
InputStream xmlInputStream = Utils.getFromURL(request.getParameter("xmlurl"));
InputStream xsltInputStream = Utils.getFromURL(request.getParameter("xslurl"));

// 创建 Source 对象
Source xmlSource = new StreamSource(xmlInputStream);
Source xsltSource = new StreamSource(xsltInputStream);

// 创建TransformerFactory
TransformerFactory transFact = TransformerFactory.newInstance();

// 创建 Transformer
Transformer trans = transFact.newTransformer(xsltSource);

// 执行转换
Result result = new StreamResult(System.out);
trans.transform(xmlSource, result);
```

**正例**
```java
public class XSLTProcessor {
    // 定义允许的 URL 白名单模式（例如，只允许特定的域名或路径）
    private static final Pattern ALLOWED_URL_PATTERN = Pattern.compile("^https?://example\\.com/.*$");

    public void processXML(String xmlUrlParam, String xslUrlParam) throws Exception {
        // 验证 URL 参数是否符合预期格式
        if (!isUrlValid(xmlUrlParam) || !isUrlValid(xslUrlParam)) {
            throw new IllegalArgumentException("Invalid URL format");
        }

        // 限制资源访问范围（仅允许访问白名单内的 URL）
        URL xmlUrl = new URL(xmlUrlParam);
        URL xsltUrl = new URL(xslUrlParam);

        // 使用安全的 URLClassLoader 加载资源
        ClassLoader safeLoader = new URLClassLoader(new URL[]{xmlUrl, xsltUrl});
        InputStream xmlInputStream = safeLoader.getResourceAsStream(xmlUrl.getPath());
        InputStream xsltInputStream = safeLoader.getResourceAsStream(xsltUrl.getPath());

        // 创建 Source 对象
        Source xmlSource = new StreamSource(xmlInputStream);
        Source xsltSource = new StreamSource(xsltInputStream);

        // 使用安全的 TransformerFactory 配置
        TransformerFactory transFact = TransformerFactory.newInstance();
        // 禁用危险的 XSLT 特性（如外部实体、脚本执行等）
        transFact.setFeature("http://xml.org/sax/features/allow-external-dtd", false);
        transFact.setFeature("http://xml.org/sax/features/allow-external-schema", false);
        transFact.setFeature("http://xml.org/sax/features/allow-external-parser", false);
        transFact.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        transFact.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        // 创建 Transformer
        Transformer trans = transFact.newTransformer(xsltSource);

        // 执行转换
        Result result = new StreamResult(System.out);
        trans.transform(xmlSource, result);
    }

    // 验证 URL 是否符合预期格式
    private boolean isUrlValid(String url) {
        return ALLOWED_URL_PATTERN.matcher(url).matches();
    }
}
```

**修复建议**
- 参数验证：在处理用户输入的 `xmlurl` 和 `xslurl` 参数之前，必须对这些参数进行严格的格式验证。例如，使用正则表达式限制 URL 的格式，确保它们只指向预期的资源。示例中使用了 `ALLOWED_URL_PATTERN` 来限制 URL 的格式，只允许特定的域名或路径。
- 禁用危险的 XSLT 特性：使用 `TransformerFactory` 时，禁用可能导致安全问题的特性，例如：`allow-external-dtd`：防止加载外部 DTD。`allow-external-schema`：防止加载外部模式。`allow-external-parser`：防止使用外部解析器。`disallow-doctype-decl`：防止文档类型声明。这些设置可以有效防止通过 XSLT 注入恶意代码。
- 限制资源访问：使用 `URLClassLoader` 限制资源访问范围，确保 XML 和 XSLT 文件只能从白名单内的 URL 加载。这可以防止攻击者通过恶意 URL 访问敏感资源或执行任意代码。

#### CWD-1084-000 XSLT注入

#### CWD-1084-001 【XSLT】使用javax.xml.transform.Transformer下的transform方法对xml进行格式转换时，xslt文件受控(上游文件或者动态构造注入)，造成的代码执行漏洞

---

