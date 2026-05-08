# CWD-1852 具有不当SameSite属性的敏感Cookie

**描述**
**语言: **JAVA

**严重等级**


**cleancode特征**


#### CWD-1852-000 具有不当SameSite属性的敏感Cookie

#### CWD-1852-001 使用Spring框架的CookieGenerator类创建会话cookie时，未增加SameSite值

#### CWD-1852-002 手动构建会话cookie响应头时，未将SameSite值加到header值中

#### CWD-1852-003 通过实例化Cookie类实现会话cookie功能时，未增加SameSite值

#### CWD-1852-004 使用Spring框架的ResponseCookie类创建会话cookie时，未调用sameSite函数为会话cookie增加sameSite属性

---

