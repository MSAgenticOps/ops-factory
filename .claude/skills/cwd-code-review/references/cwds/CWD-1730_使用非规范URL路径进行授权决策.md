# CWD-1730 使用非规范URL路径进行授权决策

**描述**
**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1730-000 使用非规范URL路径进行授权决策

#### CWD-1730-001 【nginx配置不当导致越权访问问题】nginx配置中拒绝访问场景使用正则匹配，可能导致绕过黑名单访问

#### CWD-1730-002 通过字符串或正则匹配相关函数对HttpServletRequest.getContextPath()方法获取的URL进行校验时容易被绕过

#### CWD-1730-003 通过字符串或正则匹配相关函数对HttpServletRequest.getRequestURL()、getRequestURI()获取的URL/URI进行校验，容易被绕过

#### CWD-1730-004 springboot框架通过FilterRegistrationBean对过滤器的urlPatterns使用后缀匹配模式

#### CWD-1730-005 springboot框架通过FilterRegistrationBean对过滤器的urlPatterns使用精确匹配模式

#### CWD-1730-006 通过HttpServletRequest的getRequestDispatcher(url).forward/include方法转发请求时鉴权不足

---

