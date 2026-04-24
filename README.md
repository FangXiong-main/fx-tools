# fx-tools 通用工具包

 Json工具库（Java）,Redis工具库。

# 核心功能

## Redis工具库

### Redis基础缓存操作

- `setStringValue`：存储字符串数据
- `getStringValue`：获取字符串数据

### Redis高并发缓存解决方案

- 缓存穿透（缓存空值）
- 缓存击穿（互斥锁）
- 逻辑过期（异步更新）

### 基于Redis的全局随机ID生成器

## Json工具库

### 完全自主开发的Json基本解析工具

### 提供两个工具：jsonToBean 和 BeanToJson

### 支持List，Map,Set等基本类型（支持单双泛型的无限嵌套，其余类型会在后续更新中支持），以及自定义类型的序列化与反序列化

### 支持Json反序列化为局部变量（支持单，双泛型），需提供泛型对应的字符串即可，解析器会自动解析出对应的类型，列子：JsonUtils.jsonToBean(json, new CustomizeGenericTypes("Map<String, List<Map<String,Object>>>"))

## 版本更新日志
### v1.0.0 完成基于缓存穿透、缓存击穿、逻辑过期等高并发查询的开发。
### v1.0.1 完成基于Redis自增的全局随机Id生成器的开发
### v1.1.1 完成JsonUtil的开发，支持List，Map等基本类型（支持单双泛型的无限嵌套，Set会在后续更新中支持），以及自定义类型的序列化与反序列化。
### v1.1.2 JsonUtil新增对Set的支持

## 快速开始

### Maven项目导入Jar包
在项目主目录下新建lib文件夹,并将jar文件放入

### 引入 Maven 依赖

    <dependency>
            <groupId>com.fangxiong</groupId>
            <artifactId>fx-tools</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/lib/jar包文件名称.jar(例:fx-tools-1.0.jar)</systemPath>
    </dependency>

### 配置 Redis 连接(如要使用Redis工具必须配置，若只是用Json工具则无需配置)

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

#### 全局随机ID生成器
    
    redisUtil.uniqueIdGenerator(String keyPrefix,LocalDateTime startTime,int timeStampDigits,int machineCodeDigits,int sequenceDigits,long machineCode)


## 环境依赖

- JDK 17+
- SpringBoot 3.x
- Redis
- Hutool
