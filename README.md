# FxTools

**A lightweight, zero-dependency Java toolkit for JSON processing and Redis caching.**

## What's in This Repository

This repository contains two independent Java libraries that share the same build environment:

| Library | Description |
|:---|:---|
| **FxJSON** | A lightweight, zero-dependency JSON codec engine optimized for cold-start performance. |
| **FxRedis** | A collection of production-ready Redis utilities covering common caching scenarios. |

They have no functional dependencies on each other. You can use either one independently.

---

## FxJSON — JSON Codec Engine

FxJSON is the primary software described in the accompanying academic paper.
It provides a self-contained, annotation-driven engine for JSON serialization
and deserialization, designed specifically for lightweight deployment
environments such as microservices and serverless functions.

### Why Another JSON Library?

Mainstream JSON libraries like Jackson and Gson are built for generality.
They load full metadata caches at startup and construct complete syntax trees
during parsing. In environments where cold-start latency matters—serverless
functions, short-lived containers, microservices that scale from zero—that
overhead adds up. FxJSON takes a different approach: it extracts only the
first level of a JSON string using a character-level state machine, defers
reflection metadata loading until a class is first encountered, and separates
format validation from the parsing path entirely.

### Key Features

- **Zero dependencies.** Runs with only the JDK (16+). No third-party JARs needed.
- **Streaming split parser.** Extracts key-value pairs field by field without building a full AST.
- **Deferred caching.** Reflection metadata (fields, setters, generic types) is cached on first use, not at startup.
- **Annotation-driven validation.** `@NotNullField`, `@NotNullClass`, `@IgnoredField`, and `@TimeType` give you fine-grained control without writing extra validation code.
- **Generic type resolution.** `CustomizeGenericTypes` handles generic type parameters without anonymous inner classes.
- **Configurable safety levels.** Choose between `SKIP`, `FAST`, and `NORMAL` pre-validation depending on your performance and safety needs.

### Quick Start

```java
// Define a simple entity
public class User {
    private String name;
    private int age;
    // getters and setters
}

// Deserialize a JSON string to a Java entity
String json = "{\"name\":\"Alice\",\"age\":25}";
User user = JsonUtils.jsonToBean(json, User.class, SafetyCheckLevel.SKIP);

// Serialize an entity back to JSON
String output = JsonUtils.beanToJson(user);  // {"name":"Alice","age":25}
```
### Supported Types

- Primitive types and their wrappers: `int`, `long`, `double`, `boolean`, `String`, `BigDecimal`
- Date and time: `LocalDateTime` (with `@TimeType` for custom formats)
- Collections: `List`, `Map`, `Set` (with recursive generic nesting)
- Custom user-defined classes (automatic reflection-based mapping)
- Local variables with generic type parameters (via `CustomizeGenericTypes`)

### Safety Levels

| Level | What It Checks |
|:---|:---|
| `SKIP` | No pre-validation. Fastest path. |
| `FAST` | Illegal character detection only. |
| `NORMAL` | Illegal character detection + bracket matching. |

Regardless of the level you pick, field-level type matching and non-null
annotation checks are always enforced.

### Custom Annotations

| Annotation | Scope | Purpose |
|:---|:---|:---|
| `@IgnoredField` | Field | Skip the field during serialization and deserialization. |
| `@NotNullField` | Field | Throw `JsonConvertFailureError` if the value is null after parsing. |
| `@NotNullClass` | Class | Same as `@NotNullField`, but applies to every field in the class. |
| `@TimeType` | Field | Specify a custom `LocalDateTime` format pattern. |

### Experimental Evaluation

All performance experiments reported in the paper were conducted against
Jackson 2.15.2 and Gson 2.14.0 on both Windows and macOS, using Amazon
Corretto 16. Test payloads covered three sizes (≈0.9 KB, ≈8.0 KB, ≈17.9 KB)
with multi-level nested Maps and `LocalDateTime` fields. Key findings:

- **Cold-start latency:** 6–7× faster than Jackson and Gson on both platforms.
- **Hot-path performance:** On par with Jackson and Gson for kilobyte-scale payloads.
- **Long-run stability:** Lower latency variation than Jackson under sustained load.

The test suite was developed and executed in a separate benchmarking project.
For details on the experimental setup, see Section 4 of the [paper].

### Paper

For a full description of the engine's design, architecture, and experimental
evaluation, please refer to the *SoftwareX* submission:

> Fang X, Pan C, Xie M. FxTools: A lightweight JSON codec engine for Java.
> *SoftwareX*, 2026.

If you use this software in your research, please cite the paper above.

---

## FxRedis — Redis Utilities

A set of utility classes built on `StringRedisTemplate` for common caching
scenarios in Spring Boot applications.

### Key Features

- Basic `set` / `get` string operations
- **Cache penetration** protection (null-value caching)
- **Cache breakdown** protection (mutex lock)
- **Logical expiration** (asynchronous cache refresh)
- **Redis-based unique ID generator** (increment-based)

### Requirements

- JDK 17+
- Spring Boot 3.x
- A configured `StringRedisTemplate` bean

### Usage

```java
// Cache penetration
redisUtil.queryWithPassThrough(key, id, User.class,
    30L, TimeUnit.MINUTES, userMapper::selectById);

// Cache breakdown
redisUtil.queryWithMutex(key, id, User.class,
    30L, TimeUnit.MINUTES, userMapper::selectById);

// Logical expiration
redisUtil.queryWithLogicalExpire(key, id, User.class,
    30L, TimeUnit.MINUTES, executorService, userMapper::selectById);
```
### Configuration

Add your Redis connection details to `application.yml`:

```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

Register `RedisUtil` as a Spring bean:

```java
@Configuration
public class RedisUtilConfig {
    @Bean
    public RedisUtil redisUtil(StringRedisTemplate stringRedisTemplate) {
        return new RedisUtil(stringRedisTemplate);
    }
}
```

---

## Installation

1. Download the latest JAR from [GitHub Releases](https://github.com/FangXiong-main/fx-tools/releases).
2. Place it in your project's `lib/` directory.
3. Add the Maven dependency:

```xml
<dependency>
    <groupId>com.fangxiong</groupId>
    <artifactId>fx-tools</artifactId>
    <version>1.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/fx-tools-1.0.jar</systemPath>
</dependency>
```

> **Note:** If you only need FxJSON, you can skip the Redis configuration
> and Spring Boot setup entirely. FxJSON has no external dependencies.

---

## Changelog

| Version | Date | Changes |
|:---|:---|:---|
| v1.0.0 | Apr 2026 | Initial release with FxJSON (JSON codec engine) and FxRedis (Redis utilities). |

---

## License

This project is distributed under the [MIT License](LICENSE).

---

## Contact

For questions, bug reports, or feedback, please open an issue on this
repository or contact the maintainer at `1255404327@qq.com`.
