# ops-common Architecture

## Overview

`ops-common` is a shared library module that provides common functionality for all services in the ops-factory ecosystem. It includes authentication, exception handling, and reusable components that ensure consistency across all microservices.

## Module Structure

```
ops-common/
├── src/main/java/com/huawei/opsfactory/common/
│   ├── aop/
│   │   ├── BasicAuth.java           # Basic authentication annotation
│   │   └── BasicAuthAspect.java     # AOP aspect for authentication
│   └── exception/
│       ├── ApiCallException.java         # API call exception
│       ├── AuthException.java           # Authentication exception
│       └── CommonGlobalExceptionHandler.java  # Global exception handler
└── src/main/resources/
    └── ops-common-default.properties  # Default configuration
```

## Core Components

### 1. Basic Authentication (@BasicAuth)

The `@BasicAuth` annotation provides machine-to-machine authentication for REST endpoints.

#### Annotation

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BasicAuth {
}
```

#### Usage

Apply `@BasicAuth` to individual controller methods that require machine-to-machine authentication:

```java
@PostMapping("/sensitive-operation")
@BasicAuth
public ResponseEntity<?> performSensitiveOperation() {
    // ...
}
```

For controllers with multiple protected endpoints, annotate each method:

```java
@RestController
@RequestMapping("/machine/gateway/business-services")
public class BusinessServiceMachineController {

    @GetMapping
    @BasicAuth
    public ResponseEntity<?> listServices() {
        // ...
    }

    @PostMapping
    @BasicAuth
    public ResponseEntity<?> createService() {
        // ...
    }
}
```

**⚠️ Important Limitation:**

Due to ServiceComb's `@RestSchema` annotation affecting the AOP proxy mechanism, **class-level `@BasicAuth` is not supported**. The aspect only intercepts methods annotated directly with `@BasicAuth`. Applying `@BasicAuth` at the class level will have no effect — all methods will be publicly accessible without authentication.

This has been verified through testing: the AOP proxy chain does not properly resolve class-level annotations when `@RestSchema` is present on the controller.

```java
@PostMapping("/sensitive-operation")
@BasicAuth
public ResponseEntity<?> performSensitiveOperation() {
    // ...
}
```

#### Authentication Flow

1. **Request Header**: The aspect checks for `Authorization` header with Basic authentication
2. **Invocation Context**: If header is missing, checks ServiceComb invocation context
3. **Credential Decoding**: Decodes Base64-encoded `username:password` credentials
4. **Validation**: Compares against configured username and password
5. **Proceed**: On success, allows the method to execute; on failure, throws `AuthException`

#### Configuration

Configure credentials in one of the following ways:

**Option 1: In config.yaml** (recommended)

```yaml
# Machine-to-machine authentication (required for /machine/** endpoints)
common:
  aop:
    machine:
      username: "${COMMON_AOP_MACHINE_USERNAME:}"
      password: "${COMMON_AOP_MACHINE_PASSWORD:}"
```

**Option 2: In application.properties or ops-common-default.properties**

```properties
common.aop.machine.username=machine-user
common.aop.machine.password=secure-password
```

**Option 3: Via environment variables**

```bash
export COMMON_AOP_MACHINE_USERNAME=my-user
export COMMON_AOP_MACHINE_PASSWORD=my-password
```

**Important**: The default configuration has empty values. If not configured, any request to `@BasicAuth` protected endpoints will be rejected with 401 Unauthorized.

#### AOP Aspect

The `BasicAuthAspect` intercepts methods annotated with `@BasicAuth`:

- **Method-level annotation only** — class-level annotations are not supported due to ServiceComb's `@RestSchema` affecting AOP proxy resolution
- Handles missing or invalid credentials appropriately
- Logs authentication attempts for debugging
- Wraps configuration errors in `AuthException`

### 2. Exception Handling

#### Custom Exceptions

**ApiCallException**
- Used for API call failures (missing configuration, invalid parameters)
- Returns HTTP 400 Bad Request when handled globally

```java
throw new ApiCallException("Invalid parameter: userId");
```

**AuthException**
- Used for authentication failures
- Returns HTTP 401 Unauthorized when handled globally

```java
throw new AuthException("Authentication failed");
```

#### Global Exception Handler

The `CommonGlobalExceptionHandler` provides centralized exception handling:

```java
@ControllerAdvice
public class CommonGlobalExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApiCallException.class)
    public ResponseEntity<String> handleApiCallException(ApiCallException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
```

**Benefits:**
- Consistent error responses across all services
- Centralized logging of exceptions
- Clean separation of business logic from error handling
- Easy to add new exception types

## Integration Guide

### Adding ops-common as a Dependency

Add to your module's `pom.xml`:

```xml
<dependency>
    <groupId>com.huawei.msz</groupId>
    <artifactId>opsfactory-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

### Auto-Configuration

ops-common provides Spring Boot auto-configuration. Simply adding the dependency will automatically:

1. Register `BasicAuthAspect` for AOP-based authentication
2. Register `CommonGlobalExceptionHandler` for centralized exception handling
3. Enable aspect-oriented programming with `@EnableAspectJAutoProxy`

No additional configuration is required in your Application class.

### Using @BasicAuth in Your Controller

1. Add the import:
```java
import com.huawei.opsfactory.common.aop.BasicAuth;
```

2. Annotate your controller or methods:
```java
@RestController
@RestSchema(schemaId = "myController")
@RequestMapping("/machine/my-module")
@BasicAuth
public class MyController {
    // All methods require authentication
}
```

3. Configure credentials:
```properties
# In application.properties or ops-common-default.properties
common.aop.machine.username=my-service-user
common.aop.machine.password=my-secure-password
```

### Throwing Exceptions

```java
import com.huawei.opsfactory.common.exception.ApiCallException;
import com.huawei.opsfactory.common.exception.AuthException;

// For API call errors
if (userId == null) {
    throw new ApiCallException("userId is required");
}

// For authentication errors
if (!isAuthenticated) {
    throw new AuthException("User not authenticated");
}
```

## Testing

The module includes comprehensive unit tests:

- **ApiCallExceptionTest**: Tests exception constructor, status getter/setter
- **AuthExceptionTest**: Tests exception constructor, status getter/setter
- **CommonGlobalExceptionHandlerTest**: Tests exception handling responses
- **BasicAuthAspectTest**: Tests authentication success/failure scenarios

### Running Tests

```bash
cd ops-common
mvn test
```

### Test Coverage

- Valid credentials authentication
- Invalid credentials handling
- Missing configuration handling
- Malformed Base64 handling
- Invocation context fallback
- Request header retrieval failures

## Security Considerations

1. **Credential Storage**: Store passwords securely (use environment variables or secure vaults in production)
2. **HTTPS**: Always use HTTPS in production to protect credentials in transit
3. **Credential Rotation**: Implement regular credential rotation policies
4. **Logging**: Be mindful not to log sensitive credentials

## Configuration Reference

### Properties

| Property | Description | Default | Required |
|----------|-------------|---------|----------|
| `common.aop.machine.username` | Username for machine authentication | None | Yes |
| `common.aop.machine.password` | Password for machine authentication | None | Yes |

### Environment Variables

You can also use environment variables:

```bash
export COMMON_AOP_MACHINE_USERNAME=my-user
export COMMON_AOP_MACHINE_PASSWORD=my-password
```

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.1.0 | 2026-06-08 | Add Spring Boot auto-configuration; remove default credentials (fail-closed); fix ServiceComb fallback; improve logging |
| 1.0.0 | 2026-06-06 | Initial release with @BasicAuth, global exception handling |

## Related Documentation

- [API Boundaries](./api-boundaries.md) - Guidelines for API design
- [Logging Guidelines](../development/logging-guidelines.md) - Logging best practices
- [Testing Guidelines](../development/testing-guidelines.md) - Testing standards

## Support

For issues or questions:
1. Check the test cases for usage examples
2. Review the source code Javadoc
3. Contact the ops-factory development team
