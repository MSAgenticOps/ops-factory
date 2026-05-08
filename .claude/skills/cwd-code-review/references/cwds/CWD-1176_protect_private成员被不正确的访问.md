# CWD-1176 protect/private成员被不正确的访问

**描述**
保护（protected）和私有（private）成员用于限制类成员的访问权限，以实现封装和信息隐藏。保护成员只能在类本身及其派生类中访问，而私有成员只能在类的内部访问。如果这些成员被意外或恶意修改，可能会导致数据损坏，从而使程序行为不稳定或产生错误结果。

**语言: **C,CPP,JAVA,PYTHON

**严重等级**
提示

**cleancode特征**
安全,可靠

**示例**
**案例1: 私有成员被外部函数访问**
**语言: **CPP

**描述**
在accessProtectedAndPrivate函数中，外部代码尝试直接访问Base类的保护成员protectedVar和私有成员privateVar。

**反例**
```c++
class Base {
protected:
    int protectedVar = 42;
private:
    int privateVar = 100;
};

void accessProtectedAndPrivate() {
    Base baseObject;
    baseObject.protectedVar = 10; // 错误：外部函数访问保护成员
    baseObject.privateVar = 20;   // 错误：外部函数访问私有成员
}
```

**正例**
```c++
class Base {
protected:
    int protectedVar = 42;
private:
    int privateVar = 100;

public:
    void setProtectedVar(int value) {
        protectedVar = value;
    }

    int getProtectedVar() const {
        return protectedVar;
    }
};

class Derived : public Base {
public:
    void modifyProtectedVar(int value) {
        protectedVar = value; // 正确：派生类访问保护成员
    }
};

void correctAccess() {
    Base baseObject;
    baseObject.setProtectedVar(10); // 正确：通过公有接口访问保护成员

    Derived derivedObject;
    derivedObject.modifyProtectedVar(20); // 正确：派生类访问保护成员
}
```

**修复建议**
1.正确使用访问修饰符：确保类的成员根据其访问需求正确地声明为公有、保护或私有。
2.提供公有接口：对于需要外部访问的成员，提供公有方法（如getter和setter）来控制访问和修改。
3.派生类正确访问：在派生类中，通过继承关系正确访问保护成员，避免直接访问私有成员。

**案例2: 直接访问和修改类的protected属性**
**语言: **PYTHON

**描述**
在Python中，受保护的成员通常以单下划线`_`开头，表示它们不应该被外部直接访问。直接访问受保护成员会破坏封装性，引发潜在的问题。

**案例分析**
，EntityA类直接通过entity_obj._race访问另一个实例的受保护属性_race，并且在主函数中也直接打印了_race属性。这种做法虽然在语法上是允许的，但违反了封装原则。

**反例**
```py
class Entity:
    def __init__(self, eid: int = 0, race: str = "no name", age: int = 0):
        self.__eid = eid
        self._race = race
        self.age = age


class EntityA(Entity):
    def copy_race(self, entity_obj: Entity):
        self._race = entity_obj._race  # 不符合，跨实例访问类的protected类实例属性

    def get_race(self):
        return self._race

if __name__ == "__main__":
    obj_a = EntityA(eid=1, race="dog")
    obj_b = EntityA(eid=2, race="cat")
    obj_b.copy_race(obj_a)
    print(obj_b._race)  # 不符合，直接访问类的protected类实例属性
```

**正例**
```py
class Entity:
    def __init__(self, eid: int = 0, race: str = "no name", age: int = 0):
        self.__eid = eid
        self._race = race
        self.age = age


class EntityA(Entity):
    def copy_race(self, entity_obj: Entity):
        self._race = entity_obj.get_race()  # 符合

    def get_race(self):
        return self._race

if __name__ == "__main__":
    obj_a = EntityA(eid=1, race="dog")
    obj_b = EntityA(eid=2, race="cat")
    obj_b.copy_race(obj_a)
    print(obj_b.get_race()) # 符合
```

**修复建议**
可以通过以下方法访问被保护的类实例属性：
1. 在类中定义一个公有成员函数，该函数可以访问类的受保护的成员，以提供外部访问功能。
2. 通过 property 装饰器创建只读属性的形式提供外部访问功能。

#### CWD-1176-000 protect/private成员被不正确的访问

**业界缺陷**

- [CWE-495: Private Data Structure Returned From A Public Method](https://cwe.mitre.org/data/definitions/495.html)
---

