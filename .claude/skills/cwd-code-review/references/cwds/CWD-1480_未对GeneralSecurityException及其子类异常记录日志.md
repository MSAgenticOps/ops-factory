# CWD-1480 未对GeneralSecurityException及其子类异常记录日志

**描述**
java.security.GeneralSecurityException是一个一般安全异常类，其子类定义了各种跟安全相关的异常，比如加解密操作相关异常、证书验证相关异常。当系统中抛出这些异常时，表示系统中的一些安全相关的功能出现问题或遇到一些非预期的场景。所以对于这些异常建议在日志中进行详细记录，方便后续进行问题分析定位及对系统的安全性进行优化完善。这些异常中如果含有秘钥、口令等敏感信息时，记录日志前应该对这些敏感信息进行过滤。java.security 中 GeneralSecurityException 的子类包括： DigestException 、 InvalidAlgorithmParameterException 、 InvalidKeyException 、 KeyException 、 KeyManagementException 、 KeyStoreException 、 NoSuchAlgorithmException 、 NoSuchProviderException 、 SignatureException 、 UnrecoverableEntryException 、 UnrecoverableKeyException 等。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1480-000 未对GeneralSecurityException及其子类异常记录日志

---

