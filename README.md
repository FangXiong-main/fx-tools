# FxTools

**A lightweight, zero-dependency Java toolkit for JSON processing, MySQL mapping, and Redis caching.**

## What's in This Repository

This repository contains three independent Java libraries that share the same build environment:

| Library | Description |
|:---|:---|
| **FxJSON** | A lightweight, zero-dependency JSON codec engine optimized for cold-start performance and Unicode robustness. |
| **FxMySQL** | A lightweight, annotation-driven JDBC wrapper using dynamic proxies for simple database mapping. |
| **FxRedis** | A collection of production-ready Redis utilities covering common caching scenarios. |

They have no functional dependencies on each other. You can use any of them independently.

---

## FxJSON — JSON Codec Engine

FxJSON is a self-contained, annotation-driven engine for JSON serialization and deserialization, designed specifically for lightweight deployment environments such as microservices and serverless functions.

### Why Another JSON Library?

Mainstream JSON libraries like Jackson and Gson are built for generality. They load full metadata caches at startup and construct complete syntax trees during parsing. In environments where cold-start latency matters—serverless functions, short-lived containers, microservices that scale from zero—that overhead adds up. FxJSON takes a different approach: it extracts only the first level of a JSON string using a character-level state machine, defers reflection metadata loading until a class is first encountered, and separates format validation from the parsing path entirely.

### Key Features

- **Zero dependencies.** Runs with only the JDK (16+). No third-party JARs needed.
- **Streaming split parser.** Extracts key-value pairs field by field without building a full AST.
- **Deferred caching.** Reflection metadata (fields, setters, generic types) is cached on first use, not at startup.
- **Optional Unicode Support.** Safely decodes `\uXXXX` escape sequences, including 4-byte surrogate pairs (e.g., Emojis 👨‍💻). Built with strict defensive boundaries to intercept malformed surrogate data. *(Configurable to maintain zero-overhead when not needed).*
- **Annotation-driven validation.** `@NotNullField`, `@NotNullClass`, `@IgnoredField`, and `@TimeType` give you fine-grained control.
- **Generic type resolution.** `CustomizeGenericTypes` handles generic type parameters without anonymous inner classes.
- **Configurable safety levels.** Choose between `SKIP`, `FAST`, and `NORMAL` pre-validation.

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

### Unicode Support & Performance Trade-off

FxJSON defaults to **not** processing Unicode escape sequences to maintain maximum cold-start performance. If your JSON payloads contain `\uXXXX` escape sequences, including 4-byte Emoji surrogate pairs (like `\uD83D\uDE02`), you can enable the built-in defensive preprocessor by passing `EnableUnicodeDecode.YES`.

```java
String jsonWithEmoji = "{\"message\":\"Hello \\uD83D\\uDE02\"}";

// Disabled by default (fastest)
// User user = JsonUtils.jsonToBean(jsonWithEmoji, User.class, SafetyCheckLevel.SKIP);

// Enable Unicode preprocessing and strict boundary checks
User user = JsonUtils.jsonToBean(
    jsonWithEmoji, 
    User.class, 
    SafetyCheckLevel.SKIP, 
    EnableUnicodeDecode.YES
);
// Output: user.getMessage() -> "Hello 😂"
```
*The preprocessor is built with strict boundary validations to prevent JDK-level crashes caused by malformed surrogate data (e.g., orphaned high surrogates or invalid low surrogate ranges).*

### Supported Types

- Primitive types and their wrappers: `int`, `long`, `double`, `boolean`, `String`, `BigDecimal`
- Date and time: `LocalDateTime` (with `@TimeType` for custom formats)
- Collections: `List`, `Map`, `Set` (with recursive generic nesting)
- Custom user-defined classes

### Safety Levels

| Level | What It Checks |
|:---|:---|
| `SKIP` | No pre-validation. Fastest path. |
| `FAST` | Illegal character detection only. |
| `NORMAL` | Illegal character detection + bracket matching. |

---

## FxMySQL — Lightweight JDBC Mapper

FxMySQL is a simple, reflection and dynamic proxy-based JDBC wrapper. It eliminates the need for heavy ORMs (like MyBatis or Hibernate) while providing a clean, interface-driven mapping experience.

### Key Features

- **Zero dependencies.** Pure JDK and standard `java.sql`.
- **Dynamic Proxy Mapper.** Define interfaces with annotations; FxMySQL generates the implementation at runtime.
- **Annotation-driven SQL.** Supports `@Select`, `@Insert`, `@Update`, and `@Delete`.
- **Parameter Binding.** Use `#{paramName}` in your SQL. Supports both primitive parameters and custom entity objects (with `@ParamName` for multiple params).
- **Automatic Naming Strategy.** Built-in support for converting CamelCase Java fields to snake_case database columns.
- **Generic Return Types.** Supports returning single entities, `List<T>`, and `Map<String, Object>`.
- **SQL Injection Protection.** Built-in string escaping for string parameters.

### Usage Example

**1. Define your Mapper Interface:**

```java
public interface UserMapper {
    @Select("SELECT id, user_name, create_time FROM users WHERE id = #{id}")
    User getUserById(@ParamName("id") int id);

    @Select("SELECT * FROM users")
    List<User> getAllUsers();

    @Insert("INSERT INTO users (user_name, create_time) VALUES (#{userName}, #{createTime})")
    int insertUser(User user);
}
```

**2. Execute:**

```java
Connection connection = DriverManager.getConnection(url, user, password);

// Enable camelCase to snake_case mapping
UserMapper mapper = MysqlUtils.useMapper(connection, UserMapper.class, EnableCamelCaseToUnderscore.ENABLE);

User user = mapper.getUserById(1);
```

---

## FxRedis — Redis Utilities

A set of utility classes built on `StringRedisTemplate` for common caching scenarios in Spring Boot applications.

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

---

## Installation

1. Download the latest JAR from [GitHub Releases](https://github.com/FangXiong-main/fx-tools/releases).
2. Place it in your project's `lib/` directory.
3. Add the Maven dependency:

```xml
<dependency>
    <groupId>com.fangxiong</groupId>
    <artifactId>fx-tools</artifactId>
    <version>1.2.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/fx-tools-1.2.0.jar</systemPath>
</dependency>
```

> **Note:** If you only need FxJSON or FxMySQL, you can skip the Spring Boot and Redis configuration entirely. Both libraries have no external dependencies.

---

## Changelog

| Version | Date | Changes |
|:---|:---|:---|
| v1.2.0 | Oct 2026 | Added **FxMySQL** module. Added **Optional Unicode preprocessing** to FxJSON with strict surrogate pair boundary checks. |
| v1.0.0 | Apr 2026 | Initial release with FxJSON (JSON codec engine) and FxRedis (Redis utilities). |

---
## Citation

The version of FxTools described in the associated research paper is archived at Zenodo:

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.20156271.svg)](https://doi.org/10.5281/zenodo.20156271)

## License

This project is distributed under the [MIT License](LICENSE).

---

## Contact

For questions, bug reports, or feedback, please open an issue on this repository or contact the maintainer at `fangxiong520@outlook.com`.

## Related Thesis

[![DOI](https://img.shields.io/badge/DOI-10.1016/j.softx.2026.102848-blue)](https://doi.org/10.1016/j.softx.2026.102848)
