# CWD-1859 使用包含敏感信息的Web浏览器缓存

**描述**
**语言: **JAVA

**严重等级**


**cleancode特征**


#### CWD-1859-000 使用包含敏感信息的Web浏览器缓存

#### CWD-1859-001 通过addHeader/setHeader方式单独为请求设置控制响应头Cache-control，造成部分请求对应的Response响应没有设置

#### CWD-1859-002 HTML/JSP页面在META元素配置Cache-control时配置值不合理

#### CWD-1859-003 BME框架通过bme.properties配置文件设置Cache-Control的值不正确

#### CWD-1859-004 BME框架通过HtmlResourceCacheFilter过滤器设置Cache-Control的值不正确

#### CWD-1859-005 servlet框架通过setHeader/addHeader配置Cache-Control的配置值不合理

#### CWD-1859-006 spring security安全框架通过HttpSecurity类调用disable函数关闭缓存控制响应头Cache-control的安全配置

#### CWD-1859-007 Usecurity安全框架通过FilterRegistrationBean<UsHeaderWriterFilter>过滤器设置Cache-Control的值不正确

#### CWD-1859-008 Usecurity安全框架通过web.xml配置过滤器时设置Cache-Control的值不正确

#### CWD-1859-009 Huawei WSF安全框架通过FilterRegistrationBean<HeaderWriterFilter>过滤器设置Cache-Control的值不正确

#### CWD-1859-010 Huawei WSF安全框架通过hwsf-security.xml配置文件中Cache-control节点配置disable关闭了默认安全头

#### CWD-1859-011 Huawei WSF安全框架通过web.xml配置过滤器时设置Cache-Control的值不正确

#### CWD-1859-012 nginx.conf配置文件中add_header配置项的缓存控制响应头Cache-Control的配置值不合理

---

