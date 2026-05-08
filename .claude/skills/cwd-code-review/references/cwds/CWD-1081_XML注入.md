# CWD-1081 XML注入

**别名: **XML注入（又名盲XPath注入）

**描述**
XML注入：XML Injection（又名盲XPath注入）无法正确中和XML中使用的特殊元素，使得攻击者能够在终端系统处理XML之前修改其语法、内容或命令。攻击者利用精心编制的XML用户可控输入来探测、攻击并将数据注入XML数据库，使用类似SQL注入的技术。用户可控的输入可能允许未经授权的查看数据，绕过身份验证或直接访问XML数据库的前端应用程序，并可能更改数据库信息。影响产品的机密性、完整性和可用性。


- 注：在XML中，特殊元素可以包括保留字或字符，如 `<` 、`>` 、` ` 和 `&`，然后可以使用这些保留字或字符添加新数据或修改XML语法。
**语言: **JAVA,GO,JAVASCRIPT,TYPESCRIPT

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: XML操作直接拼接用户输入导致XML注入**
**语言: **JAVA

**描述**
如果在 XML 文档中写入未验证的数据，可能会使攻击者修改 XML 的结构和内容。XML injection 会在以下情况中出现：
1. 数据从一个不可信赖的数据源进入程序。
2. 数据写入到 XML 文档中。

应用程序通常使用 XML 来存储数据或发送消息。当 XML 用于存储数据时，XML 文档通常会像数据库一样进行处理，而且可能会包含敏感信息。XML 消息通常在 web 服务中使用，也可用于传输敏感信息。XML 消息甚至还可用于发送身份验证凭据。
如果攻击者能够写入原始 XML，则可以更改 XML 文档和消息的语义。
- 危害最轻的情况下，攻击者可能会插入无关的标签，导致 XML 解析器抛出异常。
- XML injection 更为严重的情况下，攻击者可以添加 `XML` 元素，更改身份验证凭据或修改 `XML` 电子商务数据库中的价格。还有一些情况，`XML injection` 可以导致 `cross-site scripting` 或 `dynamic code evaluation`。

**案例分析**
假设攻击者能够控制下列 XML 中 UserQuery 的`ID`:
```xml
<UserQuery>
<Condition>ID=1</Condition>
</UserQuery>
```
现在假设，在后端 Web 服务请求中包含该 XML。假设攻击者可以修改请求，并将 `UserQuery` 替换成 `1</Condition><Condition>ID=2 or 1=1</Condition>`。新的 XML 如下所示：
```xml
<UserQuery>
<Condition>ID=1</Condition>
<Condition>ID=2 or 1=1</Condition>
</UserQuery>
```
攻击者通过这种方式可以修改查询逻辑，可能获取所有用户信息。

**反例**
```java
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XMLInjectionVulnerable {
    public static void main(String[] args) {
        // 模拟用户输入
        String userInput = args.length > 0 ? args[0] : "1"; // 正常情况下用户输入ID
        
        try {
            // 创建XML文档
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            
            // 创建根元素
            Element root = doc.createElement("UserQuery");
            doc.appendChild(root);
            
            // 直接使用用户输入构造XML查询条件 - 存在XML注入风险
            Element condition = doc.createElement("Condition");
            condition.appendChild(doc.createTextNode("ID=" + userInput));
            root.appendChild(condition);
            
            // 输出XML文档
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = newDOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            
            transformer.transform(source, result);
            Logger.info("生成的XML查询:");
            Logger.info(writer.toString());
            
            // 这里应该是实际的XML查询执行逻辑...
            
        } catch (Exception e) {
            // ...异常处理
        }
    }
}
```

**正例**
```java
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.text.StringEscapeUtils;
import java.io.*;

public class XMLInjectionFixed {
    public static void main(String[] args) {
        // 模拟用户输入
        String userInput = args.length > 0 ? args[0] : "1";
        
        try {
            // 创建XML文档
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            // 禁用外部实体，防止XXE攻击
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            
            // 创建根元素
            Element root = doc.createElement("UserQuery");
            doc.appendChild(root);
            
            // 对用户输入进行XML编码后再使用
            String sanitizedInput = StringEscapeUtils.escapeXml11(userInput);
            Element condition = doc.createElement("Condition");
            condition.appendChild(doc.createTextNode("ID=" + sanitizedInput));
            root.appendChild(condition);
            
            // 输出XML文档
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            
            transformer.transform(source, result);
            Logger.info("生成的XML查询:");
            Logger.info(writer.toString());
            
        } catch (Exception e) {
            // ...异常处理
        }
    }
}
```

**修复建议**
- 输入验证：确保用户输入符合预期格式（如只包含数字）。
- XML编码：使用专门的库（如Apache Commons Text的`StringEscapeUtils.escapeXml11()`）对用户输入进行XML编码，将特殊字符转换为对应的实体引用：`<` 转换为 `&lt;`；`>` 转换为 `&gt;`；`&` 转换为 `&amp;`；`"` 转换为 `&quot;`；`'` 转换为 `&apos;`。
- 禁用外部实体：配置XML解析器禁用外部实体引用，防止XXE攻击。
- 使用参数化查询：如果可能，使用预编译的XPath或XQuery，并将用户输入作为参数传递。

**案例2: XML操作直接拼接用户输入导致XML注入**
**语言: **GO

**描述**
如果在 XML 文档中写入未验证的数据，可能会使攻击者修改 XML 的结构和内容。XML injection 会在以下情况中出现：
1. 数据从一个不可信赖的数据源进入程序。
2. 数据写入到 XML 文档中。

应用程序通常使用 XML 来存储数据或发送消息。当 XML 用于存储数据时，XML 文档通常会像数据库一样进行处理，而且可能会包含敏感信息。XML 消息通常在 web 服务中使用，也可用于传输敏感信息。XML 消息甚至还可用于发送身份验证凭据。
如果攻击者能够写入原始 XML，则可以更改 XML 文档和消息的语义。
- 危害最轻的情况下，攻击者可能会插入无关的标签，导致 XML 解析器抛出异常。
- XML injection 更为严重的情况下，攻击者可以添加 `XML` 元素，更改身份验证凭据或修改 `XML` 电子商务数据库中的价格。还有一些情况，`XML injection` 可以导致 `cross-site scripting` 或 `dynamic code evaluation`。

**案例分析**
- 攻击者可以注入额外的XML标签或属性，例如：添加`<role>admin</role>`来提升权限，注入恶意实体声明导致XXE攻击，
破坏XML结构导致解析错误。
- XXE攻击：虽然Go的`encoding/xml`默认禁用外部实体解析，但直接拼接用户输入仍然可能导致XML结构破坏或数据篡改。

**反例**
```go
package main

import (
	"encoding/xml"
	"fmt"
	"os"
)

type User struct {
	XMLName xml.Name `xml:"user"`
	Name    string   `xml:"name"`
	Email   string   `xml:"email"`
}

func main() {
	// 模拟用户输入
	userInput := `<name>John Doe</name><email>john@example.com</email><role>admin</role>`

	// 直接拼接用户输入到XML中
	xmlData := fmt.Sprintf(`<?xml version="1.0"?>
<user>
	%s
</user>`, userInput)

	// 解析XML
	var user User
	err := xml.Unmarshal([]byte(xmlData), &user)
	if err != nil {
		fmt.Printf("Error unmarshaling XML: %v\n", err)
		return
	}

	// 输出结果
	fmt.Printf("Name: %s\n", user.Name)
	fmt.Printf("Email: %s\n", user.Email)

	// 将XML写入文件
	file, err := os.Create("user.xml")
	if err != nil {
		fmt.Printf("Error creating file: %v\n", err)
		return
	}
	defer file.Close()

	_, err = file.WriteString(xmlData)
	if err != nil {
		fmt.Printf("Error writing to file: %v\n", err)
		return
	}
	fmt.Println("XML data written to user.xml")
}
```

**正例**
```go
package main

import (
	"encoding/xml"
	"fmt"
	"os"
	"strings"
)

type User struct {
	XMLName xml.Name `xml:"user"`
	Name    string   `xml:"name"`
	Email   string   `xml:"email"`
}

func sanitizeXML(input string) string {
	// 替换XML特殊字符为实体引用
	input = strings.ReplaceAll(input, "&", "&amp;")
	input = strings.ReplaceAll(input, "<", "&lt;")
	input = strings.ReplaceAll(input, ">", "&gt;")
	input = strings.ReplaceAll(input, "\"", "&quot;")
	input = strings.ReplaceAll(input, "'", "&apos;")
	return input
}

func main() {
	// 模拟用户输入
	userInput := `<name>John Doe</name><email>john@example.com</email><role>admin</role>`

	// 对用户输入进行转义处理
	sanitizedInput := sanitizeXML(userInput)

	// 使用结构体生成XML而不是拼接字符串
	user := User{
		Name:  "John Doe",
		Email: "john@example.com",
	}

	xmlData, err := xml.MarshalIndent(user, "", "  ")
	if err != nil {
		fmt.Printf("Error marshaling XML: %v\n", err)
		return
	}

	// 输出结果
	fmt.Printf("Name: %s\n", user.Name)
	fmt.Printf("Email: %s\n", user.Email)

	// 将XML写入文件
	file, err := os.Create("user.xml")
	if err != nil {
		fmt.Printf("Error creating file: %v\n", err)
		return
	}
	defer file.Close()

	_, err = file.Write(xmlData)
	if err != nil {
		fmt.Printf("Error writing to file: %v\n", err)
		return
	}
	fmt.Println("XML data written to user.xml")
}
```

**修复建议**
- 避免直接拼接XML：使用Go的 `encoding/xml` 包提供的 `Marshal` 方法从结构体生成XML，而不是拼接字符串。
- 输入验证和转义：对于必须处理用户提供的XML片段的情况，实现严格的输入验证和XML特殊字符转义。
- 使用安全解析器：利用Go标准库中默认安全的XML解析器，它默认禁用外部实体解析。
- 最小化信任原则：只接受必要的字段，而不是整个XML片段。

**案例3: XML操作直接拼接用户输入导致XML注入**
**语言: **JAVASCRIPT

**描述**
如果在 XML 文档中写入未验证的数据，可能会使攻击者修改 XML 的结构和内容。XML injection 会在以下情况中出现：
1. 数据从一个不可信赖的数据源进入程序。
2. 数据写入到 XML 文档中。

应用程序通常使用 XML 来存储数据或发送消息。当 XML 用于存储数据时，XML 文档通常会像数据库一样进行处理，而且可能会包含敏感信息。XML 消息通常在 web 服务中使用，也可用于传输敏感信息。XML 消息甚至还可用于发送身份验证凭据。
如果攻击者能够写入原始 XML，则可以更改 XML 文档和消息的语义。
- 危害最轻的情况下，攻击者可能会插入无关的标签，导致 XML 解析器抛出异常。
- XML injection 更为严重的情况下，攻击者可以添加 `XML` 元素，更改身份验证凭据或修改 `XML` 电子商务数据库中的价格。还有一些情况，`XML injection` 可以导致 `cross-site scripting` 或 `dynamic code evaluation`。

**案例分析**
直接拼接用户输入：代码直接将用户提供的 `username` 和 `password` 拼接到XML字符串中，没有进行任何过滤或编码。
现在假设攻击者输入如下用户名：
```xml
const maliciousUsername = 'admin</username><isAdmin>true</isAdmin><username>user';
```
生成的XML将变为：
```xml
<userAuthRequest>
<username>admin</username>
<isAdmin>true</isAdmin>
<username>user</username>
<password>anyPassword</password>
</userAuthRequest>
```
这样攻击者可能通过注入 `isAdmin` 标签来提升权限。

**反例**
```javascript
// 从用户输入获取搜索条件
const username = document.getElementById('usernameInput').value;
const password = document.getElementById('passwordInput').value;

// 构造XML查询
const xmlQuery = `
<userAuthRequest>
    <username>${username}</username>
    <password>${password}</password>
</userAuthRequest>
`;

// 发送XML请求
fetch('/api/authenticate', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/xml'
    },
    body: xmlQuery
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

**正例**
```javascript
// 从用户输入获取搜索条件
const username = document.getElementById('usernameInput').value;
const password = document.getElementById('passwordInput').value;

// 对用户输入进行XML编码
function encodeForXML(input) {
    if (!input) return '';
    return input.replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&apos;');
}

// 使用编码后的值构造XML查询
const xmlQuery = `
<userAuthRequest>
    <username>${encodeForXML(username)}</username>
    <password>${encodeForXML(password)}</password>
</userAuthRequest>
`;

// 发送XML请求
fetch('/api/authenticate', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/xml'
    },
    body: xmlQuery
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

**修复建议**
- 实施XML编码：创建 `encodeForXML` 函数对用户输入进行编码，将特殊字符转换为对应的XML实体。
- XML编码：对用户输入进行XML编码，将特殊字符转换为对应的实体引用：`<` 转换为 `&lt;`；`>` 转换为 `&gt;`；`&` 转换为 `&amp;`；`"` 转换为 `&quot;`；`'` 转换为 `&apos;`。
- 使用编码后的值：在构造XML字符串时，所有用户提供的输入都经过编码函数处理。

**案例4: XML操作直接拼接用户输入导致XML注入**
**语言: **TYPESCRIPT

**描述**
如果在 XML 文档中写入未验证的数据，可能会使攻击者修改 XML 的结构和内容。XML injection 会在以下情况中出现：
1. 数据从一个不可信赖的数据源进入程序。
2. 数据写入到 XML 文档中。

应用程序通常使用 XML 来存储数据或发送消息。当 XML 用于存储数据时，XML 文档通常会像数据库一样进行处理，而且可能会包含敏感信息。XML 消息通常在 web 服务中使用，也可用于传输敏感信息。XML 消息甚至还可用于发送身份验证凭据。
如果攻击者能够写入原始 XML，则可以更改 XML 文档和消息的语义。
- 危害最轻的情况下，攻击者可能会插入无关的标签，导致 XML 解析器抛出异常。
- XML injection 更为严重的情况下，攻击者可以添加 `XML` 元素，更改身份验证凭据或修改 `XML` 电子商务数据库中的价格。还有一些情况，`XML injection` 可以导致 `cross-site scripting` 或 `dynamic code evaluation`。

**案例分析**
直接拼接用户输入：代码直接将用户提供的 `username` 和 `password` 拼接到XML字符串中，没有进行任何过滤或编码。
现在假设攻击者输入如下用户名：
```xml
const maliciousUsername = 'admin</username><isAdmin>true</isAdmin><username>user';
```
生成的XML将变为：
```xml
<userAuthRequest>
<username>admin</username>
<isAdmin>true</isAdmin>
<username>user</username>
<password>anyPassword</password>
</userAuthRequest>
```
这样攻击者可能通过注入 `isAdmin` 标签来提升权限。

**反例**
```javascript
// 从用户输入获取搜索条件
const username = document.getElementById('usernameInput').value;
const password = document.getElementById('passwordInput').value;

// 构造XML查询
const xmlQuery = `
<userAuthRequest>
    <username>${username}</username>
    <password>${password}</password>
</userAuthRequest>
`;

// 发送XML请求
fetch('/api/authenticate', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/xml'
    },
    body: xmlQuery
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

**正例**
```javascript
// 从用户输入获取搜索条件
const username = document.getElementById('usernameInput').value;
const password = document.getElementById('passwordInput').value;

// 对用户输入进行XML编码
function encodeForXML(input) {
    if (!input) return '';
    return input.replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&apos;');
}

// 使用编码后的值构造XML查询
const xmlQuery = `
<userAuthRequest>
    <username>${encodeForXML(username)}</username>
    <password>${encodeForXML(password)}</password>
</userAuthRequest>
`;

// 发送XML请求
fetch('/api/authenticate', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/xml'
    },
    body: xmlQuery
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

**修复建议**
- 实施XML编码：创建 `encodeForXML` 函数对用户输入进行编码，将特殊字符转换为对应的XML实体。
- XML编码：对用户输入进行XML编码，将特殊字符转换为对应的实体引用：`<` 转换为 `&lt;`；`>` 转换为 `&gt;`；`&` 转换为 `&amp;`；`"` 转换为 `&quot;`；`'` 转换为 `&apos;`。
- 使用编码后的值：在构造XML字符串时，所有用户提供的输入都经过编码函数处理。

#### CWD-1081-000 XML注入

#### CWD-1081-001 【dom4j】使用org.dom4j.Document及其子类下的addDocType方法为XML文档添加文档类型声明时，参数使用了不可信的外部数据，造成XML注入

#### CWD-1081-002 【dom4j】使用org.dom4j.Document及其子类下的addProcessingInstruction方法为XML文档添加处理指令时，参数使用了不可信的外部数据，造成XML注入

#### CWD-1081-003 【dom4j】使用org.dom4j.Element及其子类下的addCDATA方法为XML节点添加CDATA片段时，参数使用了不可信的外部数据，造成XML注入

#### CWD-1081-004 【dom4j】使用org.dom4j.Element及其子类下的addComment方法为XML节点添加注释时，参数使用了不可信的外部数据，造成XML注入

#### CWD-1081-005 【通用】将拼接了不可信数据的XML字符串写入XML文件，业务期望的值可能被覆盖，造成XML注入

**业界缺陷**

- [CWE-91: XML Injection (aka Blind XPath Injection)](https://cwe.mitre.org/data/definitions/91.html)
---

