---
name: js-ts-guide
description: Help write clean, secure, and maintainable JavaScript/TypeScript code following Huawei JS&TS coding standards (V3.x). Use whenever reviewing, writing, or refactoring JS/TS code - this includes code reviews, bug fixes, feature development, or any JavaScript/TypeScript-related task. Provides guidance on naming conventions, code style, security practices, ES6+ features, TypeScript types, and Node.js backend security.
---

# JavaScript & TypeScript Guide

A comprehensive guide for writing high-quality JavaScript and TypeScript code following Huawei JS&TS Coding Standards (V3.x). This guide covers code style, programming practices, security, and performance optimization.

## When to Use This Skill

Use this skill whenever working with JavaScript or TypeScript code:
- Writing new JavaScript/TypeScript code (ES6+)
- Reviewing or refactoring existing JS/TS code
- Fixing bugs or adding features
- Code reviews or pull requests
- Understanding JavaScript/TypeScript best practices
- Improving code quality, security, or performance
- Node.js backend development

## Quick Reference

### Naming Conventions

| Element | Style | Example |
|---------|-------|---------|
| Classes, Constructors | UpperCamelCase | `MyClass`, `DataProcessor` |
| Functions, Variables | lowerCamelCase | `getUserName()`, `itemCount` |
| Constants | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_TIMEOUT` |
| Files | kebab-case | `my-component.js`, `utils.ts` |

### Key Rules Summary

- Use const/let instead of var
- Use === instead of ==
- Use single quotes for strings
- Use 4 spaces for indentation (no tabs)
- Always use braces for control statements
- Use arrow functions to preserve `this` context
- Validate all external data before use
- Use TypeScript for better type safety
- Avoid eval(), with, debugger in production

---

## 1 Code Style

### 1.1 Naming

#### P.01 Give identifiers meaningful names for readability

Use full English words, avoid Chinese pinyin. Use common abbreviations only.

```javascript
// Bad
function getCnt() { return count; }
const custNm = 'John';
const data = { info: {} };

// Good
function getActiveUserCount() { return activeUserCount; }
const customerName = 'John';
const responseData = { metadata: {} };
```

#### G.NAM.01 Classes and constructors use UpperCamelCase

```javascript
// Good
class UserProfile {
  constructor(userId) {
    this.userId = userId;
  }
}

function VehicleFactory() { }

// Bad
class user_profile { }
function vehicleFactory() { }
```

#### G.NAM.02 Functions use lowerCamelCase

```javascript
// Good
function getUserName() { return userName; }
function calculateTotalPrice() { }

// Bad
function GetUserName() { }
function calculate_total_price() { }
```

#### G.NAM.03 Variables use lowerCamelCase

```javascript
// Good
let userName = 'John';
const itemList = [];
let isAuthenticated = true;

// Bad
let UserName = 'John';  // UpperCamelCase
const ITEM_LIST = [];   // UPPER_SNAKE_CASE (only for constants)
```

#### G.NAM.04 Avoid negative boolean variable names

Use positive forms for better readability.

```javascript
// Bad
const isNotFound = false;
const isDisabled = true;

// Good
const isFound = true;
const isEnabled = false;
```

#### G.NAM.05 Don't use reserved words as variable names

Use synonyms instead.

```javascript
// Bad
const class = 'Class A';
const function = () => { };

// Good
const className = 'Class A';
const handler = () => { };
```

#### G.NAM.06 Constants use UPPER_SNAKE_CASE

```javascript
// Good
const MAX_RETRY_COUNT = 3;
const DEFAULT_TIMEOUT_MS = 5000;
const API_BASE_URL = 'https://api.example.com';

// Bad
const maxRetryCount = 3;
```

#### G.NAM.07 Use lowercase file extensions

```javascript
// Good
user.service.js
utils.ts
constants.js

// Bad
user.service.JS
Utils.TS
```

### 1.2 Comments

#### P.02 Comments are as important as code

Comment to explain **why**, not **what**. Keep comments up-to-date.

```javascript
// Good: explains WHY
if (user == null) {
  return;  // Early return - user not authenticated
}

// Bad: explains WHAT (unnecessary)
if (user == null) {
  return;  // If user is null, return
}
```

#### G.CMT.01 Place comments above or to the right of code

```javascript
// Calculate the discount based on user tier
const discount = calculateDiscount(userTier);

// Also acceptable: inline comments
const total = basePrice + shippingFee;  // Free shipping for premium users
```

#### G.CMT.02 Add one space after comment marker

```javascript
// Good
// This is a comment
const x = 1;  // Inline comment

/* Another single line comment */

/*
 * Multi-line comment
 * Second line
 */

// Bad
 // No space after //
const y=2;//No space
```

#### G.CMT.03 Remove TODO/FIXME comments before delivery

```javascript
// Bad: delivery code with TODO
function calculateTotal() {
  // TODO: implement tax calculation
  return price;
}

// Good: either implement or create tracked task
```

#### G.CMT.04 Include copyright notice in file header

```javascript
/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020. All rights reserved.
 */
```

### 1.3 Formatting

#### G.FMT.01 Use spaces for indentation (4 spaces)

No tabs.

```javascript
// Good
function foo() {
    if (condition) {
        doSomething();
    }
}

// Bad: tabs
function foo() {
	if (condition) {
		doSomething();
	}
}
```

#### G.FMT.02 Keep line length reasonable

Break lines when they become too long for readability.

```javascript
// Good: break long function calls
someLongFunctionName(
  param1,
  param2,
  param3,
  param4
);
```

#### G.FMT.03 Keep operators at end of line when breaking

```javascript
// Good
const total = firstValue +
  secondValue +
  thirdValue;

// Bad: operator at beginning
const total = firstValue
  + secondValue
  + thirdValue;
```

#### G.FMT.04 Break lines for object literals with more than 4 properties

```javascript
// Good
const user = {
  name: 'John',
  age: 30,
  email: 'john@example.com',
  phone: '123-456-7890',
  address: '123 Main St',
};

// Bad: all on one line
const user = { name: 'John', age: 30, email: 'john@example.com', phone: '123-456-7890', address: '123 Main St' };
```

#### G.FMT.05 Limit chained method calls to 4 per line

```javascript
// Good
const result = array
  .filter(x => x > 0)
  .map(x => x * 2)
  .slice(0, 10);

// Bad: too many chained calls
const result = array.filter(x => x > 0).map(x => x * 2).slice(0, 10).reduce((a, b) => a + b);
```

#### G.FMT.06 Place else/catch on same line as closing brace

K&R style:

```javascript
// Good
if (condition) {
  doSomething();
} else {
  doOtherThing();
}

try {
  doSomething();
} catch (error) {
  handleError(error);
}

// Bad: else on new line
if (condition) {
  doSomething();
}
else {
  doOtherThing();
}
```

#### G.FMT.07 Use blank lines appropriately

Separate logical code blocks.

```javascript
// Good
function calculateTotal(items) {
  let subtotal = 0;

  for (const item of items) {
    subtotal += item.price * item.quantity;
  }

  const tax = subtotal * TAX_RATE;
  return subtotal + tax;
}

// Bad: no blank lines
function calculateTotal(items) {
  let subtotal = 0;
  for (const item of items) {
    subtotal += item.price * item.quantity;
  }
  const tax = subtotal * TAX_RATE;
  return subtotal + tax;
}
```

#### G.FMT.08 Use spaces to highlight keywords

```javascript
// Good
if (x > 0) { }
for (const item of items) { }
function myFunc() { }
const arr = [1, 2, 3];
const obj = { a: 1, b: 2 };

// Bad: no spaces
if(x>0){ }
for (const item of items){ }
function myFunc(){}
const arr = [1,2,3];
const obj = {a:1,b:2};
```

#### G.FMT.09 Always use braces for control statements

```javascript
// Good
if (condition) {
  doSomething();
}

// Bad: single statement without braces
if (condition)
  doSomething();
```

#### G.FMT.10 Place opening brace on same line

```javascript
// Good
function myFunc() {
  // ...
}

if (condition) {
  // ...
}

// Bad: opening brace on new line
function myFunc()
{
  // ...
}
```

#### G.FMT.12 Use semicolons at end of statements

```javascript
// Good
const x = 1;
const y = 2;
doSomething();
```

---

## 2 Declaration and Initialization

### G.DCL.01 Use const or let instead of var

```javascript
// Good
const PI = 3.14159;
let counter = 0;

if (true) {
  const localVar = 'value';  // Block-scoped
}

// Bad
var globalVar = 'value';  // Function-scoped, can leak
```

### G.DCL.02 Declare variables close to their first use

```javascript
// Good
function processData(data) {
  const result = transformData(data);
  validateResult(result);
  return result;
}

// Bad: declaring all variables at top
function processData(data) {
  let result;
  let transformed;
  let validated;

  transformed = transformData(data);
  validated = validateResult(transformed);
  result = validated;
  return result;
}
```

### G.DCL.03 Declare only one variable per statement

```javascript
// Good
const name = 'John';
const age = 30;
const isActive = true;

// Bad
const name = 'John', age = 30, isActive = true;
```

### G.DCL.04 Don't use chained assignments

```javascript
// Bad: chained assignment
const a = b = c = 0;

// Good
const c = 0;
const b = c;
const a = b;

// Or in a single statement without chaining
let x = 0;
let y = 0;
let z = 0;
```

### G.DCL.05 Don't initialize variables with undefined

```javascript
// Bad
let user;
user = undefined;

const data = undefined;

// Good
let user = null;
let count = 0;
const data = null;
```

### G.DCL.06 Use literal syntax for declarations

```javascript
// Good
const numbers = [1, 2, 3];
const person = { name: 'John', age: 30 };
const isActive = true;
const value = null;

// Bad: using constructor
const numbers = new Array(1, 2, 3);
const person = new Object();
person.name = 'John';
const isActive = new Boolean(true);
```

### G.DCL.07 Avoid variable shadowing

```javascript
// Bad
const name = 'global';

function process() {
  const name = 'local';  // Shadows outer name
  console.log(name);  // Prints 'local'
}

// Good: distinct names
const globalName = 'global';

function process() {
  const localName = 'local';
  console.log(localName);
}
```

---

## 3 Data Types

### 3.1 Numbers and Operations

#### G.TYP.01 Don't omit leading/trailing zeros in decimals

```javascript
// Good
const discount = 0.5;
const price = 99.0;

// Bad
const discount = .5;
const price = 99.;
```

#### G.TYP.02 Use isNaN() to check for NaN

```javascript
// Good
if (isNaN(result)) {
  handleNaN();
}

// Bad: using comparison
if (result == NaN) { }
if (result === NaN) { }
```

#### G.TYP.03 Don't use == or === for float comparison

```javascript
// Bad: floating point precision issues
if (0.1 + 0.2 === 0.3) { }  // false!

// Good: use tolerance
function isEqual(a, b, tolerance = 0.000001) {
  return Math.abs(a - b) < tolerance;
}

if (isEqual(0.1 + 0.2, 0.3)) { }
```

### 3.2 Strings

#### G.TYP.04 Use single quotes for strings

```javascript
// Good
const name = 'John';
const message = 'Hello, World';

// Bad
const name = "John";
```

#### G.TYP.05 Use template literals for string concatenation

```javascript
// Good
const greeting = `Hello, ${name}!`;
const multiline = `
  This is
  a multiline
  string
`;

// Bad
const greeting = 'Hello, ' + name + '!';
```

#### G.TYP.06 Don't use line continuation in strings

```javascript
// Bad: line continuation
const str = 'This is a \
long string';

// Good: template literal
const str = `This is a
long string`;
```

### 3.3 Arrays

#### G.TYP.07 Don't define non-numeric properties on arrays (except length)

```javascript
// Bad: adding custom property to array
const arr = [1, 2, 3];
arr.customProperty = 'value';

// Good: use a regular object if you need custom properties
const obj = { 0: 1, 1: 2, 2: 3, customProperty: 'value' };
```

#### G.TYP.08 Use Array methods for iteration

```javascript
// Good
const numbers = [1, 2, 3, 4, 5];
const doubled = numbers.map(n => n * 2);
const evens = numbers.filter(n => n % 2 === 0);
const sum = numbers.reduce((acc, n) => acc + n, 0);

// Bad: traditional for loop
for (let i = 0; i < numbers.length; i++) {
  console.log(numbers[i]);
}
```

#### G.TYP.09 Don't modify array while iterating

```javascript
// Bad: modifying array during iteration
const items = [1, 2, 3, 4, 5];
for (const item of items) {
  if (item % 2 === 0) {
    items.splice(items.indexOf(item), 1);  // Modifies array!
  }
}

// Good: filter to create new array
const items = [1, 2, 3, 4, 5];
const filtered = items.filter(item => item % 2 !== 0);
```

#### G.TYP.10 Use array destructuring

```javascript
// Good
const [first, second, ...rest] = array;
const [a, , c] = [1, 2, 3];  // Skip middle element

// Bad
const first = array[0];
const second = array[1];
```

#### G.TYP.11 Use spread operator or concat to copy arrays

```javascript
// Good
const copy = [...originalArray];
const merged = [...arr1, ...arr2];

// Bad: shallow copy issue
const copy = originalArray.slice();  // OK but spread is clearer
const same = originalArray;  // Not a copy!
```

#### G.TYP.12 Don't use spread operator for iteration

```javascript
// Bad: spread for iteration
const sum = [...arr].reduce((a, b) => a + b, 0);

// Good: proper iteration
const sum = arr.reduce((a, b) => a + b, 0);
```

### 3.4 Map/Set

#### G.TYP.13 Use Map/Set for lookup tables

```javascript
// Good: Map for key-value lookup
const userMap = new Map();
userMap.set('id1', { name: 'John' });
const user = userMap.get('id1');

// Good: Set for unique values
const uniqueItems = new Set([1, 2, 2, 3]);  // {1, 2, 3}

// Bad: object as map
const obj = {};
obj['key1'] = value;  // Can collide with Object.prototype
```

### 3.5 Type Conversion

#### G.TYP.14 Use explicit type conversion

```javascript
// Good: explicit conversions
const num = Number(str);
const str = String(num);
const bool = Boolean(value);
const int = parseInt(str, 10);
const float = parseFloat(str);

// Bad: implicit conversion
const num = +str;  // string to number
const str = '' + num;  // number to string
const bool = !!value;  // to boolean
```

---

## 4 Expressions and Operations

### G.EXP.01 Put constant values on the left side of comparisons

This helps catch accidental assignments.

```javascript
// Good
if (null === user) { return; }
if (MAX_COUNT === currentCount) { stop(); }

// Good: consistent style - varying on right
if (user === null) { return; }
if (currentCount === MAX_COUNT) { stop(); }
```

### G.EXP.02 Use === and !== instead of == and !=

```javascript
// Good
if (value === 'test') { }
if (obj !== null) { }

// Bad: type coercion
if (value == 'test') { }  // '1' == 1 is true!
if (obj != null) { }
```

### G.EXP.03 Don't use nested ternary expressions

```javascript
// Bad
const result = a > b ? (a > c ? a : c) : (b > c ? b : c);

// Good: if/else for clarity
let result;
if (a > b) {
  result = a > c ? a : c;
} else {
  result = b > c ? b : c;
}
```

### G.EXP.04 Use parentheses to clarify operator precedence

```javascript
// Good
const result = (a + b) * (c - d);
const condition = (x > 0) && (y > 0);

// Bad: relies on operator precedence knowledge
const result = a + b * c - d;
```

### G.EXP.05 Specify whitespace count in regex

```javascript
// Good: explicit whitespace count
/  \s{3}  /;  // Exactly 3 spaces

// Bad
/   \s{1,}  /;  // At least 1 space - unclear intent
```

### G.EXP.06 Use named capture groups in regex

```javascript
// Good
const regex = /(?<year>\d{4})-(?<month>\d{2})-(?<day>\d{2})/;
const match = regex.exec(date);
const { year, month, day } = match.groups;

// Bad
const regex = /(\d{4})-(\d{2})-(\d{2})/;
const parts = regex.exec(date);
const year = parts[1];
```

---

## 5 Control Statements

### G.CTL.01 Always include default branch in switch

```javascript
switch (status) {
  case 'pending':
    handlePending();
    break;
  case 'approved':
    handleApproved();
    break;
  default:
    handleUnknown();  // Required
    break;
}
```

### G.CTL.02 Use break in each case (except fallthrough)

```javascript
// Good
switch (type) {
  case 'A':
    handleA();
    break;
  case 'B':
    handleB();
    break;
}

// For intentional fallthrough, add comment
switch (type) {
  case 'A':
  case 'B':  // fallthrough - A and B share handler
    handleAB();
    break;
  default:
    handleOther();
}
```

### G.CTL.03 Use braces when declaring variables in case

```javascript
// Good
switch (type) {
  case 'option1': {
    const temp = getTemp();
    handleOption1(temp);
    break;
  }
  case 'option2':
    handleOption2();
    break;
}

// Bad
switch (type) {
  case 'option1':
    const temp = getTemp();  // Hoisted to switch scope!
    handleOption1(temp);
    break;
}
```

### G.CTL.04 Add final else branch for else-if chains

```javascript
// Good
if (status === 'active') {
  handleActive();
} else if (status === 'pending') {
  handlePending();
} else {
  handleOther();
}

// Bad: no else at end
if (status === 'active') {
  handleActive();
} else if (status === 'pending') {
  handlePending();
}
```

### G.CTL.05 Don't use logical operators instead of control statements

```javascript
// Bad: using && for control flow
isLoggedIn && showDashboard();

// Bad: using || for default values
const name = user.name || 'Anonymous';

// Good: explicit if statement
if (isLoggedIn) {
  showDashboard();
}

// Good: nullish coalescing for null/undefined
const name = user.name ?? 'Anonymous';
```

### G.CTL.06 Don't assign in conditionals

```javascript
// Bad: assignment in condition
if ((result = getValue())) {
  useResult(result);
}

// Good: separate assignment and check
const result = getValue();
if (result) {
  useResult(result);
}
```

### G.CTL.07 Limit conditions in control statements

```javascript
// Bad: too many conditions
if (a && b && c && d && e) { }

// Good: extract to named function
const canProceed = isValid && hasPermission && isEnabled && dataReady;
if (canProceed) { }
```

---

## 6 Functions

### 6.1 Function Design

#### P.03 Single Responsibility Principle

Each function should do one thing.

```javascript
// Bad: multiple responsibilities
function processUserData(user) {
  validateUser(user);           // Validation
  saveUser(user);               // Persistence
  sendNotification(user);       // Communication
  return { success: true };
}

// Good: separate functions
function validateUser(user) { }
function saveUser(user) { }
function sendNotification(user) { }

function processUserData(user) {
  validateUser(user);
  saveUser(user);
  sendNotification(user);
}
```

#### P.04 Single Level of Abstraction Principle

Keep functions at the same abstraction level.

```javascript
// Good: all operations at same level
function processOrder(order) {
  const validatedOrder = validateOrder(order);
  const calculatedTotal = calculateTotal(validatedOrder);
  const savedOrder = saveOrder(calculatedTotal);
  return savedOrder;
}
```

#### G.MET.01 Keep functions reasonably short

Suggest max 50 lines.

```javascript
// Refactor long functions into smaller ones
function processData(data) {
  return data
    .filter(filterValid)
    .map(transformData)
    .reduce(aggregateData);
}
```

#### G.MET.03 Limit nested block depth to 4 levels

```javascript
// Bad: deeply nested
if (a) {
  if (b) {
    if (c) {
      if (d) {
        doSomething();
      }
    }
  }
}

// Good: early return pattern
if (!a) return;
if (!b) return;
if (!c) return;
if (!d) return;
doSomething();
```

#### G.MET.04 Limit callback nesting to 4 levels

```javascript
// Bad: callback hell
fetchData(data => {
  processData(data, result => {
    saveResult(result, saved => {
      notifyUser(saved, () => {
        console.log('Done');
      });
    });
  });
});

// Good: async/await
async function handleData() {
  const data = await fetchData();
  const result = await processData(data);
  const saved = await saveResult(result);
  await notifyUser(saved);
  console.log('Done');
}
```

#### G.MET.05 Limit function parameters to 5 or fewer

```javascript
// Bad: too many parameters
function createUser(name, age, email, phone, address, role, preferences) { }

// Good: use options object
function createUser(name, email, options) {
  const { age, phone, address, role, preferences } = options;
}
```

#### G.MET.06 Put default parameters last

```javascript
// Good
function createUser(name, role = 'user') { }

// Bad
function createUser(role = 'user', name) { }
```

### 6.2 Function Implementation

#### P.05 Keep function declaration style consistent

Either use function declarations or arrow functions consistently.

```javascript
// Good: function declaration
function myFunction() { }

// Good: arrow function (if not using 'this')
const myArrowFunction = () => { };

// Don't mix styles
function oneThing() { }
const anotherThing = () => { };
```

#### G.MET.07 Use consistent return statements

```javascript
// Good: explicit return at end
function calculate(a, b) {
  const result = a + b;
  return result;
}

// Good: early return pattern
function calculate(a, b) {
  if (a < 0 || b < 0) {
    return 0;  // Early return for invalid input
  }
  return a + b;
}
```

#### G.MET.08 Don't create functions dynamically

```javascript
// Bad: dynamic function creation
const add = new Function('a', 'b', 'return a + b');

// Good: regular function
function add(a, b) {
  return a + b;
}

// Bad: in loops
for (let i = 0; i < 10; i++) {
  handlers[i] = function() { return i; };  // All capture same i
}
```

#### G.MET.09 Don't reassign function parameters

```javascript
// Bad
function processUser(user) {
  user = normalizeUser(user);  // Reassigning parameter
  validateUser(user);
  return user;
}

// Good
function processUser(inputUser) {
  const user = normalizeUser(inputUser);
  validateUser(user);
  return user;
}
```

#### G.MET.10 Don't use arguments, use rest syntax

```javascript
// Bad
function example() {
  const args = Array.from(arguments);
  // ...
}

// Good
function example(...args) {
  // ...
}

// Good: named rest parameters
function printItems(first, second, ...remaining) {
  console.log(first, second, remaining);
}
```

#### G.MET.11 Use parameter destructuring

```javascript
// Good
function processUser({ name, age, role = 'user' }) {
  console.log(name, age, role);
}

function processConfig({ database, server, logging }) {
  // ...
}

// Call site is more readable
processUser({ name: 'John', age: 30 });
processConfig({ database: db, server: srv, logging: log });
```

### 6.3 This Usage

#### P.06 Avoid using this outside of methods

```javascript
// Bad: using this in plain function
function Person(name) {
  this.name = name;
  this.delayGreet = function() {
    setTimeout(function() {
      console.log('Hello, ' + this.name);  // 'this' is not Person!
    }, 1000);
  };
}

// Good: arrow function preserves 'this'
function Person(name) {
  this.name = name;
  this.delayGreet = function() {
    setTimeout(() => {
      console.log('Hello, ' + this.name);  // 'this' is Person
    }, 1000);
  };
}
```

#### G.MET.12 Prefer arrow functions for callbacks to preserve this

```javascript
// Good
const myObject = {
  value: 42,
  getValue: function() {
    return () => this.value;  // Arrow preserves 'this'
  }
};

// Bad
const myObject = {
  value: 42,
  getValue: function() {
    const that = this;  // Workaround needed
    return function() {
      return that.value;
    };
  }
};
```

---

## 7 Classes and Objects

### 7.1 Classes

#### P.07 Use class keyword for class definitions

```javascript
// Good
class User {
  constructor(name) {
    this.name = name;
  }

  greet() {
    return `Hello, ${this.name}`;
  }
}

// Use extends for inheritance
class Admin extends User {
  constructor(name, permissions) {
    super(name);
    this.permissions = permissions;
  }
}

// Bad: constructor function
function User(name) {
  this.name = name;
}
User.prototype.greet = function() { };
```

#### G.OBJ.01 Always use parentheses with new

```javascript
// Good
const user = new User();

// Bad
const user = new User;  // Harder to read, may confuse
```

### 7.2 Objects

#### P.08 Define all object properties in one place

```javascript
// Good: all properties at once
const user = {
  name: 'John',
  email: 'john@example.com',
  role: 'user',
  createdAt: new Date(),
};

// Bad: adding properties later
const user = { name: 'John' };
user.email = 'john@example.com';
user.role = 'user';
user.createdAt = new Date();
```

#### G.OBJ.02 Use identifiers for object property names

```javascript
// Good
const obj = {
  name: 'John',
  type: 'user',
  isActive: true,
};

// Avoid
const obj = {
  'name': 'John',  // Unnecessary quotes
};
```

#### G.OBJ.03 Use shorthand for methods and properties

```javascript
// Good
const name = 'John';
const user = {
  name,  // Shorthand property
  greet() {  // Shorthand method
    return `Hello, ${this.name}`;
  },
};

// Bad
const user = {
  name: name,
  greet: function() {
    return `Hello, ${this.name}`;
  },
};
```

#### G.OBJ.04 Use dot notation for property access, brackets for computed

```javascript
// Good
const value = obj.property;
obj[computedKey] = value;

// Bad
const value = obj['property'];  // Unnecessary brackets
```

#### G.OBJ.05 Don't use Object.prototype methods directly on objects

This prevents prototype pollution attacks.

```javascript
// Bad: unsafe - can be overridden
if (foo.hasOwnProperty('bar')) { }

// Good
if (Object.prototype.hasOwnProperty.call(foo, 'bar')) { }
if (Object.hasOwn(foo, 'bar')) {  // ES2022
}
```

#### G.OBJ.06 Use hasOwnProperty or Object.keys with for-in

```javascript
// Good: filter with hasOwnProperty
for (const key in obj) {
  if (Object.prototype.hasOwnProperty.call(obj, key)) {
    console.log(key, obj[key]);
  }
}

// Better: use Object.keys
Object.keys(obj).forEach(key => {
  console.log(key, obj[key]);
});
```

#### G.OBJ.07 Don't modify or add methods to built-in prototypes

```javascript
// Bad: modifying Object.prototype
Object.prototype.myMethod = function() { };

// Bad: overwriting Array methods
Array.prototype.myCustomForEach = function() { };

// Good: create your own utility
function myForEach(arr, fn) {
  arr.forEach(fn);
}
```

---

## 8 Scope

#### G.SCO.01 Avoid declaring variables and functions in global scope

```javascript
// Bad: global variables
let globalVar = 'value';
function globalFunc() { }

// Good: module scope
const myModule = (function() {
  const privateVar = 'value';

  function privateFunc() { }

  return {
    publicMethod() { },
  };
})();

// Modern: ES modules
const privateVar = 'value';
export function publicMethod() { }
```

#### G.SCO.02 Don't use with() statement

```javascript
// Bad: with is deprecated and causes confusion
with (obj) {
  a = b;  // Which 'a' and 'b'? obj.a? outer.a?
}
```

---

## 9 Modules

#### P.09 Add external imports to package.json

```javascript
// Good: import from registered dependency
import lodash from 'lodash';
import react from 'react';

// Bad: referencing package without package.json dependency
import { something } from '/path/to/local/node_modules/package/';
```

#### P.10 Import order: built-in → external → internal

```javascript
// Built-in modules (Node.js)
import fs from 'fs';
import path from 'path';

// External packages
import lodash from 'lodash';
import react from 'react';

// Internal modules
import User from './user';
import { API_BASE } from './config';
```

#### G.MOD.01 Exported values should be immutable

```javascript
// Good: export primitive or frozen object
export const MAX_SIZE = 100;
export const CONFIG = Object.freeze({
  key: 'value',
});

// Bad: exporting mutable let
export let counter = 0;
export function updateCounter() { }
```

#### G.MOD.02 Don't import modules multiple times

```javascript
// Bad: same import in multiple places
// file1.js
import { helper } from './utils';

// file2.js
import { helper } from './utils';  // Redundant

// Good: centralize or import once where needed
// utils/index.js
export { helper } from './helper';
export { formatter } from './formatter';

// file.js
import { helper, formatter } from './utils';
```

---

## 10 Error Handling

### G.ERR.01 Use exceptions appropriately

```javascript
// Good: throw for truly exceptional cases
function getUser(id) {
  if (id <= 0) {
    throw new Error('Invalid user ID');
  }
  return database.getUser(id);
}

// Good: return null/undefined or use try/catch for expected failures
function findUser(email) {
  const users = database.query(email);
  return users.length > 0 ? users[0] : null;
}
```

### G.ERR.02 Use Error objects for Promise rejections

```javascript
// Good
Promise.reject(new Error('Something went wrong'));

// Bad
Promise.reject('Something went wrong');
Promise.reject({ message: 'Error' });
```

### G.ERR.03 Don't use return/break/continue/throw to exit finally

This can override thrown exceptions.

```javascript
// Bad
try {
  return value;
} finally {
  cleanup();  // But what if cleanup throws?
}

// Good
function process() {
  try {
    return doWork();
  } catch (error) {
    handleError(error);
    return null;
  } finally {
    cleanup();  // Always runs, doesn't affect return
  }
}
```

### G.ERR.04 Don't leak sensitive data in errors

```javascript
// Bad: exposing sensitive info
throw new Error(`Database error: ${dbPassword}`);

// Good: generic error for external use
throw new Error('Operation failed');

function internalProcess() {
  log.error(`Failed: ${sensitiveData}`);  // Internal logging only
}
```

---

## 11 Production Security

### P.11 Consider using JavaScript obfuscators in production

### G.AOD.01 Don't use eval()

```javascript
// Bad: eval is dangerous
const result = eval(userInput);  // Code injection risk!
const fn = new Function('x', 'return x * 2');  // Also dangerous

// Good
const value = parseInt(userInput, 10);
JSON.parse(jsonString);  // For JSON parsing
```

### G.AOD.02 Don't use implicit eval()

```javascript
// Bad: indirect eval or dynamic code
const fn = setTimeout('doSomething()', 1000);
const result = window['someFunction'](arg);

// Good
const fn = setTimeout(() => doSomething(), 1000);
const result = someFunction(arg);
```

### G.AOD.03 Minimize console usage in production

```javascript
// During development
console.log('Debug info');
console.error('Error');

// Bad: leaving debug code
// In production, remove or use a logging library
```

### G.AOD.04 Don't use alert in production code

```javascript
// Bad
alert('Something happened');

// Good: use UI modals or logging
showModal('Something happened');
logger.info('Something happened');
```

### G.AOD.05 Don't use debugger in production

```javascript
// Bad: debugger in production
function process() {
  debugger;  // Will pause execution
  // ...
}
```

---

## 12 External Data Validation

### P.13 Always validate external data before use

```javascript
// Validate function input
function createUser(input) {
  if (!input || typeof input !== 'object') {
    throw new Error('Invalid input');
  }

  const { name, email } = input;
  if (typeof name !== 'string' || name.length === 0) {
    throw new Error('Invalid name');
  }
  if (!isValidEmail(email)) {
    throw new Error('Invalid email');
  }
}
```

### G.EDV.01 Don't serialize untrusted objects directly

```javascript
// Bad: unsafe serialization
const output = JSON.stringify(userProvidedObject);

// Good: validate and sanitize first
function safeSerialize(obj) {
  const sanitized = sanitizeObject(obj);
  return JSON.stringify(sanitized);
}
```

### G.EDV.02 Don't navigate to untrusted URLs

```javascript
// Bad: user input in URL
window.location.href = userInput;

// Good: validate URLs
function safeNavigate(url) {
  const allowedDomains = ['trusted.com', 'safe.com'];
  const parsed = new URL(url, window.location.origin);
  if (allowedDomains.includes(parsed.hostname)) {
    window.location.href = url;
  }
}
```

### G.EDV.03 Encode output based on context

```javascript
// HTML context
function escapeHtml(str) {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

// JavaScript context
function safeJs(str) {
  return JSON.stringify(str);
}
```

### G.EDV.04 Keep regex simple to avoid ReDoS attacks

```javascript
// Bad: vulnerable to ReDoS
const unsafeRegex = /^(\d+)+$/;

// Good: limit repetition and use non-vulnerable patterns
const safeRegex = /^[0-9]{1,10}$/;
const safePattern = /\d+/;
```

### G.EDV.05 Don't use user input in dynamic templates

```javascript
// Bad: template injection
const template = `Hello ${userInput}`;
document.innerHTML = template;

// Good: safe concatenation
const template = 'Hello ' + escapeHtml(userInput);
```

---

## 13 Web Storage Security

### G.WST.01 Don't store sensitive data in localStorage/sessionStorage

```javascript
// Bad: sensitive data in storage
localStorage.setItem('token', authToken);
localStorage.setItem('userData', JSON.stringify(userInfo));

// Good: use sessionStorage for temporary data (shorter lifetime)
sessionStorage.setItem('tempData', 'some-value');

// For auth tokens, consider httpOnly cookies or memory
```

### G.WST.02 Use sessionStorage for temporary data

```javascript
// For temporary data that should be cleared on session end
sessionStorage.setItem('formDraft', JSON.stringify(formData));
```

---

## 14 Web SQL Security

### G.WSQ.01 Don't store sensitive data in Web SQL/IndexedDB

```javascript
// Bad
db.execute('INSERT INTO users (name, password) VALUES (?, ?)', [name, password]);

// Good: don't store sensitive data in client-side DB
```

---

## 15 Cross-Origin Communication

### G.CORS.01 Validate origin in postMessage

```javascript
// Bad: no origin check
window.addEventListener('message', (event) => {
  processData(event.data);
});

// Good: validate origin
window.addEventListener('message', (event) => {
  if (!TRUSTED_ORIGINS.includes(event.origin)) {
    return;  // Ignore untrusted source
  }
  processData(event.data);
});
```

---

## 16 Node.js Backend Security

### G.NOD.01 Don't concatenate external data into commands

```javascript
// Bad: command injection
const command = `ffmpeg -i ${userProvidedInput} output.mp4`;
exec(command);

// Good: use parameterized commands
execFile('ffmpeg', ['-i', userProvidedInput, 'output.mp4']);
```

### G.NOD.02 Sanitize file paths before use

```javascript
// Bad: path traversal
const filePath = userInput;  // Could be '../../../etc/passwd'

// Good: sanitize and validate
const path = require('path');
const baseDir = '/safe/directory';
const filePath = path.join(baseDir, path.basename(userInput));
```

### G.NOD.03 Use parameterized queries for SQL

```javascript
// Bad: SQL injection
const query = 'SELECT * FROM users WHERE name = ' + userName;

// Good: parameterized query
const query = 'SELECT * FROM users WHERE name = ?';
db.execute(query, [userName]);
```

### G.NOD.04 Validate files before decompression

```javascript
// Good: check archive contents
const extractPath = path.resolve('/safe/path');
if (!extractPath.startsWith('/safe/path/')) {
  throw new Error('Path traversal detected');
}
```

### G.NOD.06 Don't log sensitive data

```javascript
// Bad
logger.info(`User login: ${password}`);

// Good
logger.info(`User login attempt for: ${username}`);
```

### G.NOD.07 Don't redirect to untrusted URLs

```javascript
// Bad
res.redirect(userInput);

// Good: validate URL
function safeRedirect(url) {
  const trustedDomains = ['example.com', 'www.example.com'];
  const parsed = new URL(url, 'https://example.com');
  if (trustedDomains.includes(parsed.hostname)) {
    return res.redirect(url);
  }
  return res.redirect('/default');
}
```

---

## 17 Performance

### P.14-17 DOM Performance Guidelines

- Avoid excessive nested HTML tags
- Cache DOM queries
- Use ID selectors with context
- Minimize DOM operations in loops

```javascript
// Good: cache DOM query
const button = document.getElementById('submitBtn');
button.addEventListener('click', handleClick);

// Bad: repeated query
for (let i = 0; i < 100; i++) {
  document.getElementById('submitBtn').addEventListener('click', handleClick);
}

// Good: document fragment for batch insert
const fragment = document.createDocumentFragment();
items.forEach(item => {
  const el = document.createElement('div');
  el.textContent = item;
  fragment.appendChild(el);
});
document.getElementById('container').appendChild(fragment);
```

---

## 18 Memory Management

### P.18 Minimize dynamic DOM creation/deletion

### G.MEM.01 Clean up event listeners when removing DOM

```javascript
// Bad: memory leak
document.addEventListener('click', handler);
element.remove();  // Listener still attached!

// Good
const handler = () => { };
document.addEventListener('click', handler);
document.removeEventListener('click', handler);
element.remove();

// Or use AbortController (modern)
const controller = new AbortController();
document.addEventListener('click', handler, { signal: controller.signal });
element.remove();
controller.abort();
```

### G.MEM.02 Clear timers when not needed

```javascript
// Bad: timer continues after component destruction
const timer = setInterval(doSomething, 1000);

// Good
let timer = setInterval(doSomething, 1000);
function cleanup() {
  clearInterval(timer);
}
```

---

## 19 TypeScript Specific

### 4.1 Type Declarations

#### P.21 Avoid using any when possible

```typescript
// Bad
function processData(data: any) { }

// Good: specific types
function processData(data: UserData) { }

// When unknown is acceptable
function handleInput(input: unknown) {
  if (typeof input === 'string') {
    // TypeScript now knows input is string
  }
}
```

#### G.TYP.01-TS Use consistent type definition style

```typescript
// Good: interface for object types
interface User {
  name: string;
  age: number;
}

// Good: type alias for unions/primitives
type UserId = string | number;
type Callback = (data: Data) => void;

// Be consistent within a project
```

#### G.TYP.02-TS Order class/interface members consistently

```typescript
// Good: consistent ordering
interface User {
  // Properties (readonly first)
  readonly id: string;

  // Optional properties
  nickname?: string;

  // Methods
  getName(): string;
  setName(name: string): void;
}
```

#### G.TYP.03-TS Use semicolons in interface/type declarations

```typescript
// Good
interface User {
  name: string;
  age: number;
}

type Status = 'active' | 'inactive' | 'pending';
```

#### G.TYP.04-TS Use interfaces for object types

```typescript
// Good: interface for objects
interface User {
  name: string;
  email: string;
}

// Bad: type alias for objects
type User = {
  name: string;
  email: string;
};
```

#### G.TYP.05-TS Use Array<T> for complex array types

```typescript
// Good: Array<T> for complex types
const users: Array<User> = [];
const callbacks: Array<() => void> = [];

// Also acceptable: simpler arrays
const numbers: number[] = [];
```

#### G.TYP.06-TS Don't use undefined for optional members/parameters

```typescript
// Good: use optional modifier
interface User {
  name: string;
  nickname?: string;  // Optional - may be undefined
}

function greet(name: string, greeting?: string) { }

// Bad
interface User {
  name: string;
  nickname: string | undefined;
}

function greet(name: string, greeting: string | undefined) { }
```

### 4.2 Function/Method Declarations

#### G.FUN.02-TS Use property syntax in type/interface

```typescript
// Good
interface UserService {
  getUser(id: string): Promise<User>;
  createUser(user: User): void;
}

// Bad: method signature as call signature
interface UserService {
  (id: string): Promise<User>;
}
```

### 4.3 Classes

#### G.CLS.01-TS Return type should be this for method chaining

```typescript
// Good
class Builder {
  private value = 0;

  add(n: number): this {
    this.value += n;
    return this;
  }

  build(): number {
    return this.value;
  }
}

// Usage
const result = new Builder().add(1).add(2).build();
```

### 4.4 Enums

#### G.ENU.01-TS Use PascalCase for enum members

```typescript
// Good
enum Status {
  Pending = 'PENDING',
  Active = 'ACTIVE',
  Completed = 'COMPLETED',
}
```

#### G.ENU.02-TS Explicitly define enum values

```typescript
// Good: explicit values
enum Status {
  Pending = 1,
  Active = 2,
  Completed = 3,
}

enum Direction {
  Up = 'UP',
  Down = 'DOWN',
}

// Bad: implicit values
enum Status {
  Pending,  // 0
  Active,   // 1
}
```

#### G.ENU.03-TS Use literals instead of variables

```typescript
// Good
enum Status {
  Active = 'ACTIVE',
}

// Bad: using variables
const ACTIVE_STATUS = 'ACTIVE';
enum Status {
  Active = ACTIVE_STATUS,
}
```

### 4.5 Built-in Types

#### P.22 Use built-in types for constraints

```typescript
// Good: use ReadonlyArray instead of Array for read-only
function processItems(items: ReadonlyArray<Item>) {
  // items cannot be modified
}
```

#### P.23 Don't use built-in types to create new types

```typescript
// Bad: confusing type creation
type MyString = string;  // Just use string

// Good: meaningful type names for clarity
type UserId = string;
type ProductCode = string;

// Or for complex constraints
type Items = readonly Item[];
```

### 4.6 Assertions

#### G.AST.01-TS Avoid non-null assertions

```typescript
// Bad: non-null assertion
const user = getUser()!;  // Assumes never null

// Good: check before use
const user = getUser();
if (user) {
  processUser(user);
}

// Or use optional chaining
const name = getUser()?.name;
```

#### G.AST.03-TS Use optional chaining instead of null checks

```typescript
// Good: optional chaining
const name = user?.profile?.name;
const firstItem = array?.[0];
const handler = callback?.();

// Bad: verbose null checks
const name = user && user.profile && user.profile.name;
```

### 4.7 ts-directive Comments

#### G.CMT.01-TS Add explanation with @ts-directive

```typescript
// Bad
// @ts-ignore

// Good: explain why
// @ts-ignore - legacy API return type mismatch, to be fixed in V2
```

#### G.CMT.02-TS Prefer @ts-expect-error over @ts-ignore

```typescript
// Good: @ts-expect-error when you know there's an error
// @ts-expect-error - intentionally testing invalid input
test('invalid input', () => {
  expect(() => parseInput('')).toThrow();
});
```

### 4.8 Module Imports

#### G.MDL.01-TS Use import type for type-only imports

```typescript
// Good: type-only import
import type { User } from './user';
import type { ProcessFunction } from './types';

function createUser(): User { }

// Good: mixed imports
import fs from 'fs';
import type { User } from './types';

// Bad: importing type but using as value
import { User } from './user';  // Type-only, but treated as value
const user = new User();  // Error if User is interface
```

#### G.MDL.02-TS Use import/export over namespace

```typescript
// Good: ES6 modules
import { helper } from './utils';

// Bad: namespace
namespace Utils {
  export function helper() { }
}
```

### 4.9 Other

#### G.OTH.01-TS Use explicit boolean checks

```typescript
// Good: explicit comparison
if (isActive === true) { }
if (isActive === false) { }
if (hasValue !== null) { }

// Acceptable: implicit truthiness for non-boolean
if (user) { }
if (count > 0) { }
```

---

## 20 Security Checklist

Before committing JavaScript/TypeScript code, verify:

### General JavaScript

- [ ] No `eval()` or dynamic function creation
- [ ] No `with` statement
- [ ] All external data is validated before use
- [ ] No sensitive data in logs, URLs, or localStorage
- [ ] Use `===` instead of `==`
- [ ] No `debugger` in production code
- [ ] All `console.log` calls removed or behind debug flag
- [ ] No use of `alert()` in production
- [ ] Proper error handling without leaking sensitive data

### Node.js

- [ ] No command injection (use `execFile` not `exec`)
- [ ] No path traversal (use `path.join` with validation)
- [ ] SQL parameterized queries used
- [ ] No sensitive data in redirects
- [ ] Archive validation before extraction
- [ ] No sensitive data in logs

### TypeScript

- [ ] No `any` type when specific type is known
- [ ] No non-null assertions (`!`) without justification
- [ ] Optional properties use `?` not `undefined`
- [ ] Type-only imports use `import type`
- [ ] Enums have explicit values
- [ ] Security checks using `?.` and proper null checks

### DOM/UI

- [ ] DOM output is properly escaped/sanitized
- [ ] User input not used in `innerHTML` without sanitization
- [ ] `postMessage` origin is validated
- [ ] No sensitive data in `localStorage`/`sessionStorage`
- [ ] Event listeners cleaned up on element removal
- [ ] Timers cleared when not needed

---

## Quick Reference Tables

### ES6+ Features

| Feature | Usage | Example |
|---------|-------|---------|
| `const`/`let` | Variable declaration | `const x = 1;` |
| Arrow functions | Short function syntax | `const fn = () => {};` |
| Template literals | String interpolation | `` `Hello, ${name}` `` |
| Destructuring | Object/array unpacking | `const { a, b } = obj;` |
| Spread operator | Array/object expansion | `[...arr1, ...arr2]` |
| Rest parameters | Variable arguments | `function fn(...args) {}` |
| Classes | Class syntax | `class Foo {}` |
| Modules | Import/export | `import { x } from 'y';` |
| Promises | Async handling | `new Promise((resolve) => {})` |
| Async/await | Async syntax | `const data = await fetch();` |
| Optional chaining | Safe property access | `obj?.prop?.nested` |
| Nullish coalescing | Default for null/undefined | `x ?? 'default'` |

### TypeScript Types

| Type | Use For | Example |
|------|---------|---------|
| `string` | Text | `let name: string = 'John';` |
| `number` | Numbers | `let count: number = 42;` |
| `boolean` | True/false | `let active: boolean = true;` |
| `Array<T>` | Array of T | `let nums: number[] = [1, 2];` |
| `interface` | Object shape | `interface User { name: string; }` |
| `type` | Union/alias | `type Status = 'A' \| 'B';` |
| `enum` | Named constants | `enum Color { Red, Green }` |
| `void` | No return | `function log(): void {}` |
| `null`/`undefined` | Nullish | `let x: number \| null;` |
| `unknown` | Any value | `function handle(x: unknown) {}` |
| `Readonly<T>` | Immutable | `Readonly<Array<T>>` |
| `Partial<T>` | Optional props | `Partial<User>` |
| `Pick<T, K>` | Subset of props | `Pick<User, 'id' \| 'name'>` |

### Node.js Security

| Risk | Prevention |
|------|------------|
| Command injection | `execFile()` with args array |
| Path traversal | `path.resolve()` + allowlist |
| SQL injection | Parameterized queries |
| ReDoS | Simple regex, input validation |
| Information leak | Generic error messages |

---

## References

- Huawei JavaScript & TypeScript Coding Standards V3.x
- ECMAScript 6+ Specification
- TypeScript Handbook
- OWASP JavaScript Security Guidelines
- Node.js Security Best Practices