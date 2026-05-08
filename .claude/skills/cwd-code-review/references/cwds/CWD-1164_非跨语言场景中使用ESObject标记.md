# CWD-1164 非跨语言场景中使用ESObject标记

**描述**
在非跨语言场景中使用ESObject标记会导致不必要的性能开销和代码耦合度增加。ESObject主要用于跨语言环境，确保对象在不同语言间正确传递。在同语言环境中使用ESObject，会引入序列化和反序列化过程，增加处理时间，影响性能。

**语言: **ARKTS

**严重等级**
提示

**cleancode特征**
高效,可维护

**示例**
**案例1: 非跨语言场景中使用ESObject标记**
**语言: **ARKTS

**描述**
ESObject主要用在ArkTS和TS/JS跨语言调用场景中的类型标注，在非跨语言调用场景中使用ESObject标注类型，会引入不必要的跨语言调用，造成额外性能开销。

**反例**
```arkts
// lib.ets
export interface I {
  sum: number
}
export function getObject(value: number): I {
  let obj: I = { sum: value };
  return obj
}
// app.ets
import { getObject } from 'lib'
let obj: ESObject = getObject(123); // 使用ESObject，导致额外性能开销
```

**正例**
```arkts
// lib.ets
export interface I {
  sum: number
}
export function getObject(value: number): I {
  let obj: I = { sum: value };
  return obj
}
// app.ets
import { getObject, I } from 'lib'
let obj: I = getObject(123);
```

**修复建议**
非跨语言调用场景不使用ESObject标注类型

#### CWD-1164-000 非跨语言场景中使用ESObject标记

---

