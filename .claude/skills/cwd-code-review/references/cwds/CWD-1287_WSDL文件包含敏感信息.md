# CWD-1287 WSDL文件包含敏感信息

**描述**
WSDL指网络服务描述语言 (Web Services Description Language)。WSDL 是一种使用 XML 编写的文档。WSDL文件可能包含敏感信息，如用户名、密码等，我们需要确保不会泄漏这些信息。Web服务可能需要公开Web服务定义语言（WSDL）文件，但该文件中包含了不能公开的敏感信息。

**语言: **JAVA,GO,JAVASCRIPT,TYPESCRIPT

**严重等级**
提示

**cleancode特征**
安全

**示例**
**案例1: WSDL文件中包含了不应对外公开的API**
**语言: **JAVA

**描述**
假设sayHelloRequest方法是不对外声明的API，而WSDL文件中直接对外声明，可以导致该API可以被前端访问。

**反例**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                  xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                  xmlns:tns="http://example.com/helloworld"
                  targetNamespace="http://example.com/helloworld">

    <!-- 定义数据类型 -->
    <wsdl:types>
        <xsd:schema targetNamespace="http://example.com/helloworld">
            <xsd:element name="sayHelloRequest">
                <xsd:complexType>
                    <xsd:sequence>
                        <xsd:element name="name" type="xsd:string"/>
                    </xsd:sequence>
                </xsd:complexType>
            </xsd:element>
        </xsd:schema>
    </wsdl:types>

</wsdl:definitions>
```

**正例**
```java
<?xml version="1.0" encoding="UTF-8"?>
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                  xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                  xmlns:tns="http://example.com/helloworld"
                  targetNamespace="http://example.com/helloworld">

    <!-- 定义数据类型 -->
    <wsdl:types>
        <xsd:schema targetNamespace="http://example.com/helloworld">
            <xsd:element name="externalAPI">
                <xsd:complexType>
                    <xsd:sequence>
                        <xsd:element name="name" type="xsd:string"/>
                    </xsd:sequence>
                </xsd:complexType>
            </xsd:element>
        </xsd:schema>
    </wsdl:types>

</wsdl:definitions>
```

**修复建议**
WSDL文件中仅声明可外部访问的API。

#### CWD-1287-000 WSDL文件包含敏感信息

**业界缺陷**

- [CWE-651: Exposure of WSDL File Containing Sensitive Information](https://cwe.mitre.org/data/definitions/651.html)
---

