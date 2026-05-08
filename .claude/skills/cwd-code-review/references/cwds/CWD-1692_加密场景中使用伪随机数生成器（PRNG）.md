# CWD-1692 加密场景中使用伪随机数生成器（PRNG）

**描述**
**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1692-000 加密场景中使用伪随机数生成器（PRNG）

#### CWD-1692-001 【不安全随机数生成器】使用Random伪随机数生成器生成的不安全随机数用于盐值，iv，密钥、挑战值、会话token、sessionid等密码学场景

#### CWD-1692-002 【不安全随机数生成器】使用ThreadLocalRandom伪随机数生成器生成的不安全随机数用于盐值，iv，密钥、挑战值、会话token、sessionid等密码学场景

#### CWD-1692-003 由字符串获取的字节数组直接作为随机数用于盐值、iv、密钥、挑战值、会话token、sessionid等密码学场景

#### CWD-1692-004 使用javax.net.ssl.SSLContext.init方法初始化SSLContext对象时random参数设置为null，导致使用不安全的随机数

#### CWD-1692-005 【熵减后的安全随机数被用于密码算法场景中，存在不安全随机数使用】通过安全随机数生成索引取限定的空间中的字符，不满足不可预测性的要求

#### CWD-1692-006 【未使用符合要求的伪随机数生成器】new SecureRandom()无参构造函数缺省使用SHA1PRNG算法获取了不符合要求的伪随机数产生器

#### CWD-1692-007 【未使用符合要求的伪随机数生成器】SecureRandom.getInstance(String algorithm)实例化伪随机数对象时指定不安全的SHA1PRNG算法

#### CWD-1692-008 【未使用符合要求的伪随机数生成器】SecureRandom子类实例缺省使用SHA1PRNG算法获取了不符合要求的伪随机数产生器

#### CWD-1692-009 【未使用符合要求的伪随机数生成器】反射创建SecureRandom实例缺省使用SHA1PRNG算法获取了不符合要求的伪随机数产生器

#### CWD-1692-010 【未使用符合要求的伪随机数生成器】使用jdk中KeyGenerator类导致缺省使用SHA1PRNG算法获取了不符合要求的伪随机数产生器

#### CWD-1692-011 【未使用符合要求的伪随机数生成器】使用spring-security中KeyGenerators类导致缺省使用SHA1PRNG算法获取了不符合要求的伪随机数产生器

#### CWD-1692-012 【未使用符合要求的伪随机数生成器】使用SSLServerSocketFactory.getDefault()方法获取工厂类时随机数对象默认使用不安全的SHA1PRNG算法

#### CWD-1692-013 【未使用符合要求的伪随机数生成器】使用SSLSocketFactory.getDefault()方法获取工厂类时随机数对象默认使用不安全的SHA1PRNG算法

---

