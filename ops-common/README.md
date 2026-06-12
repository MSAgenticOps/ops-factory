# ops-common

Common library shared across all ops-factory backend services.

## Purpose

This module contains shared code, utilities, and common dependencies used by:
- control-center
- finops
- business-intelligence
- knowledge-service
- operation-intelligence
- skill-market
- prometheus-exporter
- gateway (gateway-service, gateway-common)

## Contents

Common utilities for:
- Base models and DTOs
- Shared constants and enums
- Common exception handling
- Utility classes for validation, conversion, etc.
- Shared configuration patterns

## Usage

Add dependency to your service's `pom.xml`:

```xml
<dependency>
    <groupId>com.huawei.msz</groupId>
    <artifactId>opsfactory-common</artifactId>
</dependency>
```

## Package Structure

- `com.huawei.opsfactory.common.model` - Shared data models
- `com.huawei.opsfactory.common.constant` - Common constants
- `com.huawei.opsfactory.common.enums` - Shared enumerations
- `com.huawei.opsfactory.common.util` - Utility classes
- `com.huawei.opsfactory.common.exception` - Common exceptions
