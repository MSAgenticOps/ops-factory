# CWD-1272 switch语句中缺少的break

**描述**
switch语句用于根据不同的条件执行不同的代码块，如果没有使用break语句，程序将继续执行下一个case语句的代码块。当只打算执行与一个条件关联的代码时，缺少break语句会导致执行意外的逻辑。

**语言: **C,CPP,JAVA

**严重等级**
提示

**cleancode特征**
可读

**示例**
**案例1: switch语句中缺少break**
**语言: **JAVA

**描述**
switch语句中缺少break

**反例**
```java
switch (label) {
    case 0:
    case 1:
        System.out.println("1");
    case 2:
        System.out.println("2");
    case 3:
        System.out.println("3");
        break;
    default:
        System.out.println("Default case!");
}
```

**正例**
```java
switch (label) {
    case 0:
    case 1:
        System.out.println("1");
        // $FALL-THROUGH$
    case 2:
        System.out.println("2");
        // $FALL-THROUGH$
    case 3:
        System.out.println("3");
        break;
    default:
        System.out.println("Default case!");
}
```

**修复建议**
switch语句中，当没有终止语句（break，return或抛出异常）时会执行到switch语句的结束处。当case语句块中没有终止语句时，需要添加注释，表明会继续执行到下一个case语句块。任何符合fall-through概念的注释都可以（通常是// $FALL-THROUGH$）。

#### CWD-1272-000 switch语句中缺少的break

#### CWD-1272-001 由于switch语句的缺少break而覆盖了在前一个switch case中存储的值

#### CWD-1272-002 【循环退出】循环条件始终成立时，循环体存在某个分支无break、return、异常抛出等退出条件，在不可信数据控制该分支始终执行时，造成死循环

**业界缺陷**

- [CWE-484: Omitted Break Statement in Switch](https://cwe.mitre.org/data/definitions/484.html)
---

