# CWD-1085 JSON注入

**别名: **JSON劫持；JSON中的数据中和不当（“JSON注入”）

**描述**
JSON注入是一种安全漏洞，当应用程序在构造JSON数据时未对用户输入进行适当处理，攻击者可以注入恶意内容来破坏JSON结构或执行恶意操作。应对输入数据进行严格的验证和过滤，使用安全的JSON解析器和避免将敏感数据存储在JSON中。常见场景：


- 动态JSON构造：应用程序拼接用户输入来构造JSON响应。- JSONP回调：未验证JSONP回调函数名。- 存储的JSON数据：用户提供的JSON数据被存储后未经处理直接使用。
**语言: **JAVA

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: 未对用户输入进行处理导致JSON结构被破坏**
**语言: **JAVA

**描述**
一个Java Web应用中存在的JSON注入漏洞。该应用允许用户提交个人资料信息，后端将用户输入直接拼接成JSON字符串返回给前端或存储到数据库。未对用户输入进行适当处理可能导致JSON结构被破坏、XSS攻击或数据篡改。

**案例分析**
- `getUserProfileJson`方法中直接拼接用户控制的bio字段到JSON字符串。
- `parseUserInput`方法中直接解析未经验证的用户输入JSON。
当bio输入为：`\"}, \"admin\":true, \"xss\":\"<script>alert(1)</script>`时，生成的JSON变为：
```json
{"username":"test", "bio":"\"}, "admin":true, "xss":"<script>alert(1)</script>"}
```
- 这可能导致：JSON结构被破坏，注入额外的属性(如admin:true)，存储XSS攻击载荷。

**反例**
```java
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class UserProfileService {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    // 有风险的JSON构造方式 - 直接拼接用户输入
    public String getUserProfileJson(String username, String bio) {
        // 直接拼接用户输入的bio到JSON字符串
        String json = "{\"username\":\"" + username + 
                      "\",\"bio\":\"" + bio + "\"}";
        return json;
    }
    
    // 有风险的JSON解析方式 - 直接解析未验证的JSON输入
    public Map<String, Object> parseUserInput(String userInput) throws Exception {
        // 直接解析未经验证的用户输入
        return mapper.readValue(userInput, HashMap.class);
    }
}
```

**正例**
```java
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringEscapeUtils;

public class SecureUserProfileService {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    // 安全的JSON构造方式 - 使用对象映射
    public String getUserProfileJson(String username, String bio) throws Exception {
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("username", username);
        // 对用户输入进行HTML转义
        userProfile.put("bio", StringEscapeUtils.escapeHtml4(bio));
        return mapper.writeValueAsString(userProfile);
    }
    
    // 安全的JSON解析方式 - 使用严格的对象模型
    public UserProfile parseUserInput(String userInput) throws Exception {
        // 使用具体的POJO类而非通用的Map
        UserProfile profile = mapper.readValue(userInput, UserProfile.class);
        
        // 验证必要字段
        if (profile.getUsername() == null || profile.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        // 对字段进行清理
        profile.setBio(StringEscapeUtils.escapeHtml4(profile.getBio()));
        
        return profile;
    }
    
    // 定义具体的用户资料类
    public static class UserProfile {
        private String username;
        private String bio;
        
        // getters和setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
    }
}
```

**修复建议**
- 使用对象映射代替字符串拼接：使用 `Jackson/Gson` 等库的 `writeValueAsString` 方法生成JSON，避免手动拼接JSON字符串。
- 输入验证与清理：对用户输入进行HTML转义(使用 `StringEscapeUtils` )，验证必填字段和格式。
- 严格的对象模型：使用具体的POJO类而非通用的Map接收JSON数据，定义明确的输入数据结构。
- 内容安全策略：设置适当的Content-Type头( `application/json` )，实现CSRF保护。

#### CWD-1085-000 JSON注入

---

