# CWD-1177 clone方法实现不正确

**别名: **错误使用Clone方法

**描述**
在Java中，Object类提供了一个protected方法clone()，用于创建对象的浅拷贝。当自定义类重写clone()方法时，如果不正确实现，可能会导致以下问题：1.父类字段未正确复制： 父类的clone()方法负责复制父类的字段。如果不调用super.clone()，父类的字段可能不会被正确复制，导致克隆出来的对象缺少父类的初始化状态。2.父类的初始化逻辑未执行： 如果父类的clone()方法中包含一些初始化逻辑，不调用super.clone()会导致这些逻辑未被执行，可能引发对象状态不一致。3.线程不安全： Object类的clone()方法是线程安全的，因为它使用了同步块。如果不调用super.clone()，自定义的clone()方法可能不具备线程安全性，导致在多线程环境下出现竞态条件。4.代码抛出异常：如果一个类定义了clone()方法但没有实现Cloneable接口，那么在调用clone()方法时会抛出CloneNotSupportedException异常，因为在Java中，只有实现了Cloneable接口的类才能被克隆。

**语言: **JAVA

**严重等级**
提示

**cleancode特征**
可靠

**示例**
**案例1: clone方法没有调用super.clone()**
**语言: **JAVA

**描述**
Person类的clone()方法没有调用super.clone()，直接创建了一个新的Person对象。如果Person类的父类（如Object）有需要复制的字段或初始化逻辑，这些不会被执行。

**案例分析**
1.Person类的clone()方法没有调用super.clone()，直接创建了一个新的Person对象。
2.如果Person类的父类（如Object）有需要复制的字段或初始化逻辑，这些不会被执行。
3.在多线程环境下，clone()方法可能不具备线程安全性，导致竞态条件。

**反例**
```java
public class Person implements Cloneable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person clone() {
        // 未调用 super.clone()
        Person newPerson = new Person(this.name, this.age);
        return newPerson;
    }

    // getters and setters
}

public class Main {
    public static void main(String[] args) {
        Person original = new Person("Alice", 30);
        Person cloned = original.clone();
        System.out.println("Original: " + original.getName());
        System.out.println("Cloned: " + cloned.getName());
    }
}
```

**正例**
```java
public class Person implements Cloneable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public Person clone() {
        Person newPerson = (Person) super.clone();
        // 进行深拷贝，如果需要
        return newPerson;
    }

    // getters and setters
}

public class Main {
    public static void main(String[] args) {
        Person original = new Person("Alice", 30);
        Person cloned = original.clone();
        System.out.println("Original: " + original.getName());
        System.out.println("Cloned: " + cloned.getName());
    }
}
```

**修复建议**
1.调用super.clone()： 在自定义的clone()方法中，首先调用super.clone()，确保父类的字段和初始化逻辑被正确复制。
2.实现Cloneable接口： 确保自定义类实现Cloneable接口，避免在调用clone()时抛出CloneNotSupportedException异常。
3.进行深拷贝： 如果类包含引用类型的字段，应在clone()方法中进行深拷贝，避免浅拷贝带来的问题。

#### CWD-1177-000 clone方法实现不正确

#### CWD-1177-001 clone方法没有调用super.clone()

#### CWD-1177-002 clone方法没有实现Cloneable接口

---

