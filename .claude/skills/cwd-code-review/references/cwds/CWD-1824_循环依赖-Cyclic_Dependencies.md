# CWD-1824 循环依赖-Cyclic Dependencies

**描述**
<div style="display: flex; align-items: center">
<div style="width: 75%; ">
两个或两个以上模块互相依赖，形成一个或多个环。“循环依赖”违反了Robert Martin提出的非循环依赖原则（ADP, Acyclic Dependencies Principle ），即模块间的依赖不是一个有向无环图（DAG, Directed Acyclic Graph）。如果两个或两个以上模块存在于同一个循环依赖中，那么单独维护或重用其中的任何一个模块都将变得困难甚至不可能。

**【典型问题】** 如右图中importer, meta, validator模块间调用形成了环，识别为循环依赖问题。经过分析，meta模块用来定义Excel配置的元数据，不应该依赖Excel导入的校验器validator模块，依赖不合理。该不合理依赖来源于ValidatorMeta校验器元数据持有了校验器作为成员变量。经过分析主要是ExcelValidatorDataProcessor需要根据元数据获取校验器的实现，因此可以将这一块的功能迁移到ExcelValidatorDataProcessor，为了避免元数据到校验器之间的重复解析，使用Map记录映射关系

</div>
<div style="width: 5%;">
</div>
<div style="width: 20%;">```mermaid
graph TD
A(importer)
B(meta)
C(validator)
A--> B--> C
C--> A
```

</div>**语言: **C,CPP,JAVA

**严重等级**
一般

**cleancode特征**
安全,可靠

#### CWD-1824-000 循环依赖-Cyclic Dependencies

---

