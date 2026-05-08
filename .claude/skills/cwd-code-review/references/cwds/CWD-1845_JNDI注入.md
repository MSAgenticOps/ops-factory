# CWD-1845 JNDI注入

**描述**
JNDI (Java Naming and Directory Interface) 是一组应用程序接口，它为开发人员查找和访问各种资源提供了统一的通用接口，可以用来定位用户、网络、机器、对象和服务等各种资源。比如可以利用JNDI在局域网上定位一台打印机，也可以用JNDI来定位数据库服务或一个远程Java对象。JNDI底层支持RMI远程对象，RMI注册的服务可以通过JNDI接口来访问和调用。 


- 可能被攻击的方式如下：
  1. 目标代码中调用了InitialContext.lookup(URI)，且URI为用户可控；  2. 攻击者控制URI参数为恶意的RMI服务地址，如：rmi://hacker_rmi_server//name；  3. 攻击者RMI服务器向目标返回一个Reference对象，Reference对象中指定某个精心构造的Factory类；  4. 目标在进行lookup()操作时，会动态加载并实例化Factory类，接着调用factory.getObjectInstance()获取外部远程对象实例；  5. 攻击者可以在Factory类文件的构造方法、静态代码块、getObjectInstance()方法等处写入恶意代码，达到RCE的效果； 
**语言: **JAVA

**严重等级**
一般

**cleancode特征**
安全,可靠

#### CWD-1845-000 JNDI注入

#### CWD-1845-001 使用低版本JDK（低于6u132、7u122、8u113）时，com.sun.jndi.rmi.object.trustURLCodebase属性设置为true后，允许RMI服务器从远程的codeBase加载任意代码，造成JNDI注入

#### CWD-1845-002 使用高版本JDK时，被攻击服务器中存在可利用类进行攻击

---

