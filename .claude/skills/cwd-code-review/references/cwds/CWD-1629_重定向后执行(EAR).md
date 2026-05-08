# CWD-1629 重定向后执行(EAR)

**描述**
Web应用程序发送重定向到另一个位置，但没有退出，而是执行其他代码。可能会影响应用程序的控制流，并允许执行不受信任的代码。

**语言: **JAVA,SHELL

**严重等级**
提示

**cleancode特征**
可靠

**示例**
**案例1: 重定向逻辑异常**
**语言: **JAVA

**描述**
sendRedirect()方法后还有其他的逻辑代码。

**反例**
```java
public void doFilter(ServletRequest arg0, ServletResponse arg1,
		FilterChain arg2) throws IOException, ServletException {
	HttpServletRequest  request = (HttpServletRequest)arg0;
	HttpServletResponse response = (HttpServletResponse) arg1;	
	
	String url = doSomething();
	if(url != null) {
	    response.sendRedirect(url);
	}
	doSomethingElse();
    arg2.doFilter(arg0, arg1);	
}
```

**正例**
```java
public void doFilter(ServletRequest arg0, ServletResponse arg1,
		FilterChain arg2) throws IOException, ServletException {
	HttpServletRequest  request = (HttpServletRequest)arg0;
	HttpServletResponse response = (HttpServletResponse) arg1;	
	
	String url = doSomething();
	if(url != null) {
	    response.sendRedirect(url);
	} else {
	    doSomethingElse();
        arg2.doFilter(arg0, arg1);
	}	
}
```

**修复建议**
sendRedirect()方法后不要再执行其他的逻辑代码。

#### CWD-1629-000 重定向后执行(EAR)

**业界缺陷**

- [CWE-698: Execution After Redirect (EAR)](https://cwe.mitre.org/data/definitions/698.html)
---

