# fx-tools 通用工具包

基于 SpringBoot 开发的 Redis 增强工具库，提供缓存穿透、缓存击穿、逻辑过期等高并发解决方案。

## 项目介绍

通用工具包，简化 Redis 操作，封装企业级缓存方案，开箱即用。

## 快速开始

### 引入 Maven 依赖

    <dependency>
        <groupId>com.fangxiong</groupId>
        <artifactId>fx-tools</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

### 配置 Redis 连接

在 `application.yml` 中配置 Redis 信息

    spring:
      redis:
        host: localhost
        port: 6379

### 注册配置类（必须）

创建配置类，将 `RedisUtil` 交给 Spring 管理

    @Configuration
    public class RedisUtilConfig {
        @Bean
        public RedisUtil redisUtil(StringRedisTemplate stringRedisTemplate) {
            return new RedisUtil(stringRedisTemplate);
        }
    }

## 使用方式

### 注入 RedisUtil

    @Autowired
    private RedisUtil redisUtil;

### 常用方法示例

#### 缓存穿透解决方案

    redisUtil.queryWithPassThrough(key, id, User.class, 30L, TimeUnit.MINUTES, userMapper::selectById);

#### 缓存击穿解决方案

    redisUtil.queryWithMutex(key, id, User.class, 30L, TimeUnit.MINUTES, userMapper::selectById);

#### 逻辑过期解决方案

    redisUtil.queryWithLogicalExpire(key, id, User.class, 30L, TimeUnit.MINUTES, executorService, userMapper::selectById);

## 核心功能

### 基础缓存操作

- `setStringValue`：存储字符串数据
- `getStringValue`：获取字符串数据

### 高并发缓存解决方案

- 缓存穿透（缓存空值）
- 缓存击穿（互斥锁）
- 逻辑过期（异步更新）

## 环境依赖

- JDK 17+
- SpringBoot 3.x
- Redis
- Hutool
