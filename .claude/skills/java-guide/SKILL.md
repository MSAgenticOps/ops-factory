---
name: java-guide
description: Help write clean, secure, and maintainable Java code following Huawei Java coding standards (Java 8+). Use whenever reviewing, writing, or refactoring Java code - this includes code reviews, bug fixes, feature development, or any Java-related task. Provides guidance on naming conventions, code style, exception handling, concurrency, security practices, and performance optimization.
---

# Java Guide

A comprehensive guide for writing high-quality Java code following Huawei Java Coding Standards (V5.x). This guide covers code style, programming practices, security, and performance optimization.

## When to Use This Skill

Use this skill whenever working with Java code:
- Writing new Java code
- Reviewing or refactoring existing Java code
- Fixing bugs or adding features
- Code reviews or pull requests
- Understanding Java best practices
- Improving code quality, security, or performance

## Quick Reference

### Naming Conventions
- **Classes/Interfaces**: PascalCase (e.g., `ArrayList`, `Collection`)
- **Methods/Variables**: camelCase (e.g., `getName()`, `userName`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_SIZE`)
- **Packages**: lowercase (e.g., `com.huawei.project`)
- **Booleans**: Start with `is`, `has`, `can`, `should` (e.g., `isValid`)

### Key Rules Summary
- Use 4 spaces for indentation (no tabs)
- Line width: max 120 characters
- Use braces `{}` for all control statements
- Use try-with-resources for resource management
- Avoid null, use Optional instead
- Don't catch generic exceptions
- Validate external input before use

---

## 1 Code Style

### 1.1 Naming

#### Identifiers
Use meaningful names (max 64 characters) with full words. Avoid abbreviations except common ones (resp, req, msg). Use English only, no pinyin.

```java
// Bad
String name_;
String file_name;
String custNm;

// Good
String name;
String fileName;
String customerName;
```

#### Packages
Package names should be lowercase with dot-separated levels. Start with `com.huawei` for self-developed code.

```java
package com.huawei.mobilecontrol.views;
package xxx.yyy.v2;
```

#### Classes, Enums, and Interfaces
Use PascalCase. Class names should be nouns, interfaces can be nouns or adjectives. Test classes should end with `Test`.

```java
// Good
class OrderProcessor {}
interface Comparable {}
abstract class AbstractBaseHandler {}

// Bad
class marcoPolo {}
interface TAPromotion {}
```

#### Methods
Use camelCase with verbs or verb phrases:
- `get` + property name
- `is`/`has` + boolean property name
- `set` + property name
- Callback methods: `onCreate()`, `onDestroy()`

```java
public boolean isFinished() {}
public void setVisible(boolean visible) {}
public void addKeyListener(Listener listener) {}
```

#### Constants
Use UPPER_SNAKE_CASE with underscores. Replace magic numbers with named constants.

```java
// Good
static final int MAX_USER_NUM = 200;
static final String APPLICATION_NAME = "Launcher";

// Bad
static int MAXUSERNUM = 200;
static final int NUM_FIVE = 5;
```

#### Variables
Use camelCase for local variables and method parameters. Use plural form for collections.

```java
// Good
String customerName;
List<User> users;

// Bad
String customername;
List<User> userList;
```

#### Boolean Variables
Avoid negative names. Start with `is`, `has`, `can`, `should`.

```java
// Good
boolean hasLicense;
boolean shouldAbort;

// Bad
boolean isNoError;
boolean isNotFound;
```

### 1.2 Comments

#### Principles
Comments are as important as code. Comment to explain **why**, not **what** (code should be self-explanatory). Keep comments up-to-date with code changes.

#### Javadoc
Document public and protected classes, interfaces, methods, and fields with Javadoc (`/** */`).

```java
/**
 * Process user order and generate invoice.
 *
 * @param order the order to process
 * @return generated invoice ID
 * @throws OrderException if order is invalid
 * @since 2024-01-01
 */
public String processOrder(Order order) throws OrderException {}
```

#### File Header
Include copyright/license information at the top of the file:

```java
/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020. All rights reserved.
 */
```

#### Code Comments
Add blank lines between comments and code. Use `//` for single-line comments.

```java
int foo = 100; // Variable comment

if (nr % 15 == 0) { // When nr is divisible by 3 and 5
    System.out.println("fizzbuzz");
}
```

#### TODO/FIXME
Don't leave TODO/FIXME comments in production code. If needed during development, use a standard format:

```java
// TODO(author-name): Complete XX processing
// FIXME: XX defect
```

### 1.3 Formatting

#### Source File Structure
Order: License → package → imports → top-level class (separated by blank lines).

```java
/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020. All rights reserved.
 */

package com.huawei.example;

import java.util.List;
import com.huawei.utils.StringHelper;

public class ExampleClass {
    // Class content
}
```

#### Import Order
Static imports → Android → Huawei → Other commercial → Open source → net/org → javacard → Java base → Java other → Java extensions

```java
import static all.statics.imports;

import android.xx.Xyz;
import com.huawei.xx.Xyz;
import com.google.common.io.Files;
import net.sf.json.xx.Xyz;
import org.linux.apache.server.SoapServer;
import java.io.IOException;
import javax.swing.JPanel;
```

#### Class Member Order
1. Static variables
2. Static initialization blocks
3. Instance variables
4. Instance initialization blocks
5. Constructors
6. Methods

#### Braces
Use K&R style: opening brace at line end, closing brace on its own line.

```java
// Good
try {
    if (condition) {
        doSomething();
    } else {
        doSomethingElse();
    }
} catch (MyException ex) {
    handleException(ex);
}

// Bad
try { doSomething(); } catch (MyException ex) { handleException(ex); }
```

#### Indentation
Use 4 spaces per indent level. No tabs.

#### Line Width
Keep lines under 120 characters. Break long lines before operators.

```java
Student student = Student.builder()
    .setName("zhangsan")
    .setAge(14)
    .build();
```

#### Switch Statements
Include default case. Add fall-through comments when break is intentionally omitted.

```java
switch (label) {
    case 0:
        System.out.println("0");
        // $FALL-THROUGH$
    case 1:
        System.out.println("1");
        break;
    default:
        System.out.println("default");
}
```

#### Horizontal Spacing
Add spaces around binary operators, after commas, and before `{`. No spaces around member access operators.

```java
// Good
int result = a + b;
for (int i = 0; i < 10; i++) {}
void process() { }

// Bad
int result = a+b;
for (int i=0;i<10;i++){}
```

---

## 2 Programming Practices

### 2.1 Declaration and Initialization

#### Variable Declaration
Declare one variable per line. Declare variables close to their first use.

```java
// Good
int level;
int size;

// Bad
int level, size;
```

#### Array Declaration
Use `Type[] name` style, not C-style.

```java
// Good
int[] values;

// Bad
int values[];
```

#### Constants
Prefer enums over constants for business状态/类型.

```java
// Good
public enum TemperatureScale {CELSIUS, FAHRENHEIT}

// Bad
public static final int TEMP_CELSIUS = 0;
public static final int TEMP_FAHRENHEIT = 1;
```

#### Mutable Objects
Don't declare mutable objects as `public static final`.

### 2.2 Data Types

#### Integer Operations
Avoid integer overflow. Use long for large values.

```java
// Good
long sum = 1000000L * 1000000L;

// Bad - may overflow
int sum = 1000000 * 1000000;
```

#### Division Safety
Check for zero divisor before division/modulo operations.

#### Floating Point
Don't use floating-point as loop counters. Use BigDecimal for precise calculations.

```java
// Good
BigDecimal price = new BigDecimal("0.1");
price = price.add(new BigDecimal("0.2")); // 0.3, not 0.30000000000000004

// Bad
double price = 0.1 + 0.2; // 0.30000000000000004
```

#### Floating Point Comparison
Don't use `==` for floating-point comparison. Use range comparison.

```java
// Good
Math.abs(a - b) < 0.0001;

// Bad
a == b;
```

#### String Operations
Use `Locale.ROOT` or `Locale.ENGLISH` for case conversion and number formatting.

```java
// Good
String upper = str.toUpperCase(Locale.ROOT);

// Bad
String upper = str.toUpperCase(); // May have locale-specific behavior
```

#### Character/Byte Conversion
Always specify encoding when converting between characters and bytes.

```java
// Good
byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

// Bad
byte[] bytes = str.getBytes();
```

#### Sensitive Data
Clear sensitive data from memory immediately after use.

```java
char[] password = {'p', 'a', 's', 's'};
// Use password...
Arrays.fill(password, '\0'); // Clear immediately
```

#### Type Conversion
Use explicit type casts. Check with `instanceof` before downcasting.

```java
// Good
if (obj instanceof String) {
    String str = (String) obj;
}

// Bad
String str = (String) obj; // May throw ClassCastException
```

### 2.3 Expressions

#### Multiple Assignments
Don't assign the same variable more than once in a single expression.

#### Operator Precedence
Use parentheses to clarify operator precedence.

```java
// Good
if ((a & MASK) == b)

// Bad - confusing
if (a & MASK == b)
```

#### Ternary Operator
Ensure both branches have the same type.

#### Null Handling
Don't dereference potentially null objects without checks.

```java
// Good
if (str != null && str.length() > 0) {}

// Bad
if (str.length() > 0) {} // May throw NPE
```

### 2.4 Control Statements

#### Assignment in Conditions
Don't assign variables in control conditions.

```java
// Bad
if ((result = calculate()) == null) {}
```

#### If-Else Chains
Add an else branch at the end of if-else-if chains.

#### Switch Statements
Always include a default case.

#### Loop Termination
Ensure loops always terminate. Avoid infinite loops.

```java
// Good
for (int i = 0; i < items.size(); i++) {}

// Bad - potentially infinite
for (int i = 0; i < items.size(); ) {}
```

#### Modifying Loop Variables
Don't modify loop control variables inside the loop body.

### 2.5 Methods

#### Method Length
Keep methods short and focused.

#### Deprecated APIs
Don't use methods/classes marked as `@Deprecated`.

#### Method Parameters
Don't use parameters as temporary variables.

#### Return Values
Return empty collections instead of null. Use Optional for optional return values.

```java
// Good
public List<Item> getItems() {
    return items != null ? items : Collections.emptyList();
}

public Optional<String> findName() {
    return Optional.ofNullable(name);
}

// Bad
public List<Item> getItems() {
    return items;
}
```

#### Optional Usage
- Don't return null from methods returning Optional
- Don't use Optional as method parameters
- Don't use Optional in fields or collections

#### Return Value Usage
Don't ignore method return values.

### 2.6 Classes and OOP

#### Class Fields
Avoid public non-final class fields.

#### Constructor Design
- Don't call overridable methods in constructors
- Reuse constructors when there are multiple

#### Method Overriding
Always add `@Override` when overriding methods.

#### equals/hashCode
Override both `equals()` and `hashCode()` together.

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    User user = (User) obj;
    return Objects.equals(id, user.id);
}

@Override
public int hashCode() {
    return Objects.hash(id);
}
```

#### Singleton Pattern
Implement singleton correctly.

```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```

#### Static Method Calls
Use class name, not instance, for static methods.

```java
// Good
int value = Integer.parseInt(str);

// Bad
Integer obj = new Integer(1);
int value = obj.parseInt(str);
```

### 2.7 Exception Handling

#### Empty Catch Blocks
Never silently swallow exceptions.

```java
// Bad
try {
    doSomething();
} catch (Exception e) {
    // Do nothing
}
```

#### Exception Catching
Don't catch generic `Throwable`, `Exception`, or `RuntimeException`.

```java
// Bad
try {
    doSomething();
} catch (Exception e) {
    handle(e);
}
```

#### NullPointerException/IndexOutOfBoundsException
Don't catch these - they indicate bugs that should be fixed.

#### Sensitive Information
Don't leak sensitive information in exceptions.

```java
// Bad - exposes internal details
throw new AuthenticationException("Password for user " + userId + " is incorrect");

// Good - generic message
throw new AuthenticationException("Invalid credentials");
```

#### Rethrowing Exceptions
Preserve original exception when rethrowing.

```java
try {
    doSomething();
} catch (IOException e) {
    throw new ServiceException("Failed to process", e); // Preserve stack trace
}
```

#### Exception Count
A method shouldn't throw more than 5 types of exceptions.

#### Finally Blocks
Don't use return, break, continue, or throw in finally blocks.

#### System.exit()
Don't call `System.exit()` to terminate JVM.

#### SuppressWarnings
Don't suppress warnings broadly on classes. Apply to specific elements.

### 2.8 Concurrency

#### Data Race Prevention
Synchronize access to shared mutable data.

#### Deadlock Prevention
Acquire locks in a consistent order.

#### Lock Management
Release locks in finally blocks or use try-with-resources.

```java
// Good
private final Lock lock = new ReentrantLock();

public void safeMethod() {
    lock.lock();
    try {
        // Critical section
    } finally {
        lock.unlock();
    }
}

// Better - try-with-resources
private final ReentrantLock lock = new ReentrantLock();

public void safeMethod() {
    lock.lock();
    try (var ignored = new LockAutoCloseable(lock)) {
        // Critical section
    }
}
```

#### Expensive Operations in Locks
Don't perform expensive or blocking operations while holding locks.

#### Double-Checked Locking
Implement correctly if used:

```java
private volatile Singleton instance;

public Singleton getInstance() {
    if (instance == null) {
        synchronized (this) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

#### Thread Naming
Give meaningful names to threads.

```java
Thread worker = new Thread(task, "order-processor");
```

#### Uncaught Exceptions
Handle uncaught exceptions in threads.

```java
Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
    logger.error("Uncaught exception in thread " + t.getName(), e);
});
```

#### Thread Termination
Don't use `Thread.stop()`. Use interrupt or cancellation flags.

#### Thread Pool
Use thread pools instead of creating threads directly.

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
```

#### ThreadLocal Cleanup
Clean up ThreadLocal variables after task completion.

### 2.9 Generics and Collections

#### Generic Methods
Prefer generic methods when applicable.

#### Generic Collections
Use generic collections over arrays.

#### foreach Loop
Don't modify collections in foreach loops.

```java
// Bad
for (Item item : items) {
    items.remove(item);
}

// Good
Iterator<Item> iter = items.iterator();
while (iter.hasNext()) {
    Item item = iter.next();
    if (condition) {
        iter.remove();
    }
}
```

### 2.10 Input/Output

#### File Paths
Validate and canonicalize file paths from external sources.

#### Zip File Security
Validate zip files to prevent zip slip and other attacks.

#### Stream Operations
Use `int` return type for methods reading single bytes/characters.

#### Process IO
Handle process input/output streams to prevent blocking.

#### Temporary Files
Delete temporary files after use.

```java
Path tempFile = Files.createTempFile("prefix", ".tmp");
try {
    // Use file
} finally {
    Files.deleteIfExists(tempFile);
}
```

### 2.11 Serialization

#### Serializable
Avoid implementing Serializable if possible.

#### serialVersionUID
Explicitly declare `serialVersionUID` for Serializable classes.

#### Self-References
Don't serialize HashMap/HashSet/Hashtable containing references to themselves.

#### Resource References
Don't serialize references to system resources.

#### Inner Classes
Don't serialize non-static inner classes.

#### Sensitive Data
Prevent sensitive information leakage during serialization.

#### Deserialization Security
Prevent deserialization from bypassing constructor security checks.

#### External Data
Don't deserialize untrusted data directly.

### 2.12 External Data Validation

#### SQL Injection
Never concatenate external data into SQL statements. Use parameterized queries.

```java
// Bad
String query = "SELECT * FROM users WHERE id = " + userId;

// Good
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM users WHERE id = ?");
stmt.setInt(1, userId);
```

#### Command Injection
Don't pass external data to Runtime.exec() or ProcessBuilder.

#### XML Injection
Don't concatenate external data into XML. Use safe XML parsing.

#### XXE Protection
Prevent XML External Entity attacks.

```java
// Good - disable external entities
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
```

#### ReDoS Protection
Keep regular expressions simple to prevent ReDoS attacks.

#### Reflection Security
Don't use external data directly in reflection operations.

#### Expression Injection
Prevent expression language injection in templates.

### 2.13 Logging

#### Logging Framework
Use facade pattern logging (e.g., SLF4J).

```java
private static final Logger logger = LoggerFactory.getLogger(MyClass.class);
```

#### Log Levels
Use appropriate log levels (ERROR, WARN, INFO, DEBUG, TRACE).

#### Sensitive Data
Never log sensitive information (passwords, tokens, personal data).

#### Localization
Don't use Chinese in logs for non-Chinese market products.

### 2.14 Performance and Resource Management

#### Collection to Array
Use `toArray(T[])` method for collection to array conversion.

#### Array Copy
Use `System.arraycopy()` or `Arrays.copyOf()`.

#### Collection Capacity
Specify initial capacity when creating collections with known size.

#### Regex Compilation
Pre-compile regex patterns and reuse them.

```java
private static final Pattern EMAIL_PATTERN =
    Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
```

#### Object Creation
Avoid creating unnecessary objects.

#### Hash Collection Keys
Ensure hashCode doesn't change for objects used in HashMap/HashSet.

#### Resource Management
Always close resources using try-with-resources.

```java
try (InputStream is = new FileInputStream(file)) {
    // Use stream
} // Auto-closed
```

#### Garbage Collection
Don't trigger explicit GC (except in cryptography, RMI).

#### Finalizer
Don't use finalizers.

#### Return Values
Don't create temporary variables for return values.

### 2.15 Platform Security

#### Defensive Copy
Make defensive copies of mutable objects received from or returned to untrusted code.

#### Security Methods
Declare security-check methods as `private` or `final`.

#### ClassLoader Security
Call super.getPermission() when overriding getPermission() in custom ClassLoader.

#### JAR Security
Don't rely on default signature verification for external JARs.

#### Security Manager
Use Security Manager to protect sensitive operations.

### 2.16 Other Best Practices

#### Secure Random Numbers
Use cryptographically secure random numbers for security-sensitive operations.

```java
// Good
SecureRandom random = new SecureRandom();
byte[] bytes = new byte[32];
random.nextBytes(bytes);
```

#### SSL/TLS
Use SSLSocket instead of plain Socket for secure communication.

#### Code Removal
Remove unused code and imports instead of commenting them out.

#### Public Addresses
Don't include public network addresses in code.

---

## 3 Security Checklist

Before committing Java code, verify:

- [ ] No hardcoded passwords or secrets
- [ ] All external input is validated
- [ ] SQL uses parameterized queries
- [ ] XML parsing is protected against XXE
- [ ] No sensitive data in logs
- [ ] Exceptions don't leak sensitive info
- [ ] Resources are properly closed
- [ ] Concurrency is properly synchronized
- [ ] Serialization is secure
- [ ] Cryptographic operations use secure random numbers

---

## 4 Common Patterns

### 4.1 Null-Safe Operations

```java
// Using Optional
Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getStreet)
    .orElse("Unknown");
```

### 4.2 Stream Operations

```java
// Filter and collect
List<String> activeUsers = users.stream()
    .filter(User::isActive)
    .map(User::getName)
    .collect(Collectors.toList());
```

### 4.3 Try-with-Resources

```java
try (Connection conn = dataSource.getConnection();
     PreparedStatement stmt = conn.prepareStatement(query)) {
    // Use resources
} // Auto-closed
```

### 4.4 Builder Pattern

```java
User user = User.builder()
    .name("John")
    .email("john@example.com")
    .build();
```

---

## 5 Quick Reference Tables

### 5.1 Naming Styles

| Element | Style | Example |
|---------|-------|---------|
| Class/Interface | PascalCase | `OrderProcessor` |
| Method/Variable | camelCase | `processOrder()` |
| Constant | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| Package | lowercase | `com.huawei.example` |
| Boolean variable | is/has/can/should | `isValid` |

### 5.2 Log Levels

| Level | Use When |
|-------|----------|
| ERROR | Unexpected failures, needs attention |
| WARN | Unexpected but recoverable, degraded functionality |
| INFO | Important business events, state changes |
| DEBUG | Detailed info for debugging |
| TRACE | Finest details, entry/exit of methods |

### 5.3 Common Imports Order

1. Static imports
2. Android packages
3. com.huawei.* and com.hisilicon.*
4. Other commercial (com.*)
5. Open source third-party
6. net/org organizations
7. Java base modules (java.*)
8. Java extension packages (javax.*)

---

## References

- Huawei Java Coding Standards V5.x
- Java SE Documentation
- Effective Java (Joshua Bloch)
- Secure Coding Guidelines for Java SE