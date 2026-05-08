# CWD-1412 关键数据元素声明为public/protected

**描述**
将关键数据设为 public 后，任何有权访问包含该变量的对象的人都可以随意更改或读取该值，这可能间接影响安全性。因此，数据应尽可能声明为私有或受保护的，以防止未授权的访问和操作。在大多数最终类中不要使用受保护（protected）方法，因为它们无法被子类化。只有在最终类扩展了具有受保护方法的其他类（其可见性不能降低）时才允许使用。使用私有（private）或包访问权限修饰符来明确您的意图。

**语言: **CPP,JAVA

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1412-000 关键数据元素声明为public/protected

**业界缺陷**

- [CWE-766: Critical Data Element Declared Public](https://cwe.mitre.org/data/definitions/766.html)
---

