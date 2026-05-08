# CWD-1849 没有“HttpOnly”标志的敏感Cookie

**描述**
**语言: **JAVA

**严重等级**


**cleancode特征**


#### CWD-1849-000 没有“HttpOnly”标志的敏感Cookie

#### CWD-1849-001 使用Spring框架的CookieGenerator类创建会话cookie时，未调用setCookieHttpOnly函数将httponly设置为true

#### CWD-1849-002 手动构建cookie响应头时，未将HttpOnly值加到header值中

#### CWD-1849-003 通过实例化Cookie类实现会话cookie功能时，未通过setHttpOnly函数设置httponly为true

#### CWD-1849-004 使用Spring框架的ResponseCookie类创建会话cookie时，未调用httpOnly函数将httponly设置为true

#### CWD-1849-005 web.xml中配置session-config会话属性时，未设置http-only属性为true或被错误设置为false

---

