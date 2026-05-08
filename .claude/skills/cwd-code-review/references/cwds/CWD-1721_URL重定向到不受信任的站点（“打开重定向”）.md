# CWD-1721 URL重定向到不受信任的站点（“打开重定向”）

**描述**
**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1721-000 URL重定向到不受信任的站点（“打开重定向”）

#### CWD-1721-001 若getContextPath返回为空，URL拼接的不可信数据被视作绝对路径，造成不安全重定向

#### CWD-1721-002 HttpServletResponse.sendRedirect进行重定向时，URL/HOST值完全能够被外部控制

#### CWD-1721-003 HttpServletResponse.sendRedirect使用的URL拼接了不可信数据导致服务器的HOST限制被绕过

#### CWD-1721-004 通过HttpServletResponse.sendRedirect进行重定向时，host校验方式不严格

#### CWD-1721-005 【通过Springboot框架的redirect的方式，使用受污染的URL字符串造成不安全重定向问题】URL/HOST值完全能够被外部控制，直接重定向到钓鱼网站页面

#### CWD-1721-006 Springboot框架的redirect的使用的URL拼接了不可信数据导致服务器的HOST限制被绕过

#### CWD-1721-007 【通过Springboot框架的redirect的方式，使用受污染的URL字符串造成不安全重定向问题】失效的host校验方式导致不安全的重定向问题

#### CWD-1721-008 【通过Spring框架的RedirectView的方式，使用受污染的URL字符串造成不安全重定向问题】URL/HOST值完全能够被外部控制，直接重定向到钓鱼网站页面

#### CWD-1721-009 org.springframework.web.servlet.view.AbstractUrlBasedView#setUrl设置的URL拼接了不可信数据导致服务器的HOST限制被绕过

#### CWD-1721-010 【通过Spring框架的RedirectView的方式，使用受污染的URL字符串造成不安全重定向问题】失效的host校验方式导致不安全的重定向问题

#### CWD-1721-011 【通过设置HTTP协议Location响应头方式，使用受污染的URL字符串造成不安全重定向问题】URL/HOST值完全能够被外部控制，直接重定向到钓鱼网站页面

#### CWD-1721-012 javax.servlet.http.HttpServletResponse#addHeader设置的URL拼接了不可信数据导致服务器的HOST限制被绕过

#### CWD-1721-013 【通过设置HTTP协议Location响应头方式，使用受污染的URL字符串造成不安全重定向问题】失效的host校验方式导致不安全的重定向问题

---

