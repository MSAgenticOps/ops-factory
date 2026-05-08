# CWD-1862 HTTPS会话中没有“安全”属性的敏感Cookie

**描述**
**语言: **JAVA

**严重等级**


**cleancode特征**


#### CWD-1862-000 HTTPS会话中没有“安全”属性的敏感Cookie

#### CWD-1862-001 使用Spring框架的CookieGenerator类创建cookie时，未调用setCookieSecure函数将Secure设置为true

#### CWD-1862-002 【未给cookie设置secure标志，无法防止cookie在http（明文协议）下被嗅探】手动构建cookie响应头时，未将Secure值加到header值中

#### CWD-1862-003 通过实例化Cookie类实现cookie功能时，未通过setSecure函数设置Secure为true

#### CWD-1862-004 使用Spring框架的ResponseCookie类创建cookie时，未调用secure函数将secure设置为true

#### CWD-1862-005 web.xml中配置session-config会话属性时，未设置secure属性为true或被错误设置为false

---

