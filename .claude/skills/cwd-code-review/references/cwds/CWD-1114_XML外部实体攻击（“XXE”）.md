# CWD-1114 XML外部实体攻击（“XXE”）

**别名: **XML外部实体引用的限制不当（“XXE”）；XML外部实体注入；XXE注入；XML外部实体引用引起信息泄露；XML外部实体文件泄露导致的信息泄露

**描述**
XXE注入，是XML外部实体注入（XML External Entity Injection），当应用程序处理用户提供的XML文档而不禁用对外部资源的引用时，URI解析为预期控制范围之外的文档，导致产品将不正确的文档嵌入到其输出中，会出现XML外部实体（XXE）注入漏洞。XML解析器通常默认支持外部引用。


- 外部实体可以引用解析器文件系统上的文件；利用此功能可能允许检索任意文件，或通过使服务器读取`/dev/random`等文件而拒绝服务。- 外部实体通常也可以通过HTTP协议处理程序引用网络资源。向其他系统发送请求的能力可以允许易受攻击的服务器被用作攻击代理。通过提交适当的负载，攻击者可以使应用服务器攻击它可以与交互的其他系统。这可能包括公共第三方系统、同一组织内的内部系统或应用服务器自身的本地环回适配器上可用的服务。根据网络架构的不同，这可能会暴露外部攻击者无法访问的高度易受攻击的内部服务。
**语言: **JAVA,PYTHON

**严重等级**
严重

**cleancode特征**
安全,可靠,高效

**示例**
**案例1: 通过禁用外部实体解析功能防止XXE注入**
**语言: **PYTHON

**描述**
一个Web应用程序，该程序允许用户上传XML文件，并在服务器端解析这些文件以提取数据。然而，该程序没有对XML文件进行充分的安全检查，导致存在XML外部实体注入（XXE）漏洞。攻击者可以利用这个漏洞构造一个恶意的XML文件，通过外部实体引用服务器上的敏感文件或执行其他恶意操作。

**案例分析**
1. 攻击者构造恶意XML文件：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE root [
<!ENTITY xxe SYSTEM "file:///etc/passwd">
]>
<data>
<content>&xxe;</content>
</data>
```
这个XML文件定义了一个外部实体`xxe`，指向服务器上的`/etc/passwd`文件。当服务器解析这个XML文件时，会尝试读取`/etc/passwd`文件的内容，并将其嵌入到响应中。
2. 上传恶意XML文件。
3. 服务器解析XML文件：服务器使用默认的XML解析器（如`xml.etree.ElementTree`或`lxml`）解析上传的XML文件。由于解析器默认启用外部实体解析，服务器会尝试读取`/etc/passwd`文件的内容。
4. 漏洞利用成功：服务器返回的响应中包含`/etc/passwd`文件的内容，攻击者成功获取了服务器上的敏感信息。

**反例**
```python
import xml.etree.ElementTree as ET

def parse_xml(xml_file):
    tree = ET.parse(xml_file)
    root = tree.getroot()
    for child in root:
        print(child.text)

parse_xml('malicious.xml')
```

输出结果：

```text
root:x:0:0:root:/root:/bin/bash
daemon:x:1:1:daemon:/usr/sbin:/usr/sbin/nologin
...
```

服务器解析XML文件时，读取了`/etc/passwd`文件的内容并输出，导致敏感信息泄露。

**正例**
```python
import xml.etree.ElementTree as ET
from xml.sax.xmlreader import XMLReader

def parse_xml(xml_file):
    # 禁用外部实体解析
    parser = ET.XMLParser()
    parser.entity = None  # 禁用实体解析
    tree = ET.parse(xml_file, parser)
    root = tree.getroot()
    for child in root:
        print(child.text)

parse_xml('malicious.xml')
```

输出结果：

```text
None
```

服务器在解析XML文件时，禁用了外部实体解析，因此无法读取`/etc/passwd`文件的内容，攻击失败。

**修复建议**
- 禁用外部实体解析：在解析XML文件时，禁用外部实体解析功能。对于不同的XML解析库，禁用方法可能不同，例如在`xml.etree.ElementTree`中可以设置`parser.entity = None`。

#### CWD-1114-000 XML外部实体攻击（“XXE”）

#### CWD-1114-001 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.bind.JAXBContext造成的外部实体注入（XXE）攻击

#### CWD-1114-002 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.parsers.DocumentBuilderFactory造成的外部实体注入（XXE）攻击

#### CWD-1114-003 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.parsers.SAXParserFactory造成的外部实体注入XXE攻击

#### CWD-1114-004 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.stream.XMLInputFactory造成的外部实体注入（XXE）攻击

#### CWD-1114-005 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.transform.sax.SAXTransformerFactory造成的外部实体注入（XXE）攻击

#### CWD-1114-006 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.transform.TransformerFactory造成的外部实体注入（XXE）攻击

#### CWD-1114-007 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.validation.SchemaFactory造成的外部实体注入（XXE）攻击

#### CWD-1114-008 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】javax.xml.xpath.XPathExpression造成的外部实体注入（XXE）攻击

#### CWD-1114-009 【使用JDK包下存在的组件解析XML内容存在实体注入的问题】org.xml.sax.helpers.XMLReaderFactory造成的外部实体注入（XXE）攻击

#### CWD-1114-010 【使用jdom2解析XML内容存在实体注入的问题】org.jdom2.input.SAXBuilder造成的外部实体注入（XXE）攻击

#### CWD-1114-011 【使用xerces解析XML内容存在实体注入的问题】org.apache.xerces.jaxp.DocumentBuilderFactoryImpl造成的外部实体注入（XXE）攻击

#### CWD-1114-012 【使用dom4j解析XML内容存在实体注入的问题】org.dom4j.io.SAXReader造成的外部实体注入（XXE）攻击

**业界缺陷**

- [CWE-611: Improper Restriction of XML External Entity Reference](https://cwe.mitre.org/data/definitions/611.html)
---

