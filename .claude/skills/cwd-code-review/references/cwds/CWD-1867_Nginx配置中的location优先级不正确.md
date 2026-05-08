# CWD-1867 Nginx配置中的location优先级不正确

**描述**
Nginx允许配置多个location。当同一个URL能够被多个location匹配的情况下，优先级高的location生效。在向已有的nginx配置中增加新的location时，需要注意新增location规则与存量location规则之间的优先级关系，避免新增location失效或覆盖了不应覆盖的原有location配置。Nginx的location匹配规则和优先级详见： https://nginx.org/en/docs/http/ngx_http_core_module.html#location

**语言: **C,CPP,JAVA,PYTHON

**严重等级**
一般

**cleancode特征**
可维护,可测试

#### CWD-1867-000 ginx配置中的location优先级不正确

---

