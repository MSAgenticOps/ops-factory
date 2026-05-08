# CWD-1543 VUE不良实践：template模板的任意元素中使用 v-html属性

**描述**
禁止使用 v-html，防止将不安全的html插入浏览器而导致跨站脚本攻击(XSS)。建议使用插值表达式渲染文本，或使用渲染组件。

**语言: **JAVASCRIPT,TYPESCRIPT

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1543-000 VUE不良实践：template模板的任意元素中使用 v-html属性

---

