package com.fangxiong.redis;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.fangxiong.redis.SystemConstants.TEMP_LOCK_KEY;

public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造方法
     * @param stringRedisTemplate redis模板
     */
    public RedisUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 存储字符串数据（无过期时间）
     * @param key 键
     * @param o 要存储的对象
     */
    public void setStringValue(String key,Object o) {
        setStringValue(key,o,0L);
    }

    /**
     * 存储字符串数据（带过期时间）
     * @param key 键
     * @param o 要存储的对象
     * @param l 过期时间
     */
    public void setStringValue(String key,Object o,Long l){
        String jsonStr = JSONUtil.toJsonStr(o);
        if(l==0L){
            stringRedisTemplate.opsForValue().set(key,jsonStr);
        }else{
            stringRedisTemplate.opsForValue().set(key,jsonStr,l);
        }
    }

    /**
     * 获取字符串数据并转换为指定对象
     * @param key 键
     * @param clazz 目标类型
     * @return 转换后的对象
     */
    public <R> R getStringValue(String key,Class<R> clazz) {
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(jsonStr)){
            return JSONUtil.toBean(jsonStr, clazz);
        }
        return null;
    }

    /**
     * 缓存穿透-查询方法
     * @param key 缓存键前缀
     * @param dbQueryId 查询id
     * @param clazz 返回类型
     * @param ttl 过期时间
     * @param timeUnit 时间单位
     * @param dbQueryFunction 数据库查询函数
     * @return 查询结果
     */
    public <R,ID> R queryWithPassThrough(String key,ID dbQueryId, Class<R> clazz,Long ttl,TimeUnit timeUnit,Function<ID,R> dbQueryFunction) {
        R r;
        String fullKey = key+":"+dbQueryId;
        String shopJson = stringRedisTemplate.opsForValue().get(fullKey);
        if (StrUtil.isNotBlank(shopJson)) {
            return JSONUtil.toBean(shopJson, clazz);
        }
        if(shopJson!=null){
            return null;
        }
        r = dbQueryFunction.apply(dbQueryId);
        if (r == null) {
            stringRedisTemplate.opsForValue().set(fullKey,"",ttl, timeUnit);
            return null;
        }
        stringRedisTemplate.opsForValue().set(fullKey, JSONUtil.toJsonStr(r),ttl, timeUnit);
        return r;
    }

    /**
     * 缓存击穿-互斥锁查询
     * @param key 缓存键前缀
     * @param dbQueryId 查询id
     * @param clazz 返回类型
     * @param ttl 过期时间
     * @param timeUnit 时间单位
     * @param dbQueryFunction 数据库查询函数
     * @return 查询结果
     */
    public <R,ID> R queryWithMutex(String key,ID dbQueryId, Class<R> clazz,Long ttl,TimeUnit timeUnit,Function<ID,R> dbQueryFunction) {
        R r=null;
        String fullKey = key+":"+dbQueryId;
        do {
            String shopJson = stringRedisTemplate.opsForValue().get(fullKey);
            if (StrUtil.isNotBlank(shopJson)) {
                return JSONUtil.toBean(shopJson, clazz);
            }
            if(shopJson!=null){
                return null;
            }
            try {
                if(!tryLock(TEMP_LOCK_KEY + dbQueryId)){
                    continue;
                }
                shopJson = stringRedisTemplate.opsForValue().get(fullKey);
                if (StrUtil.isNotBlank(shopJson)) {
                    return JSONUtil.toBean(shopJson, clazz);
                }
                r = dbQueryFunction.apply(dbQueryId);
                if (r == null) {
                    stringRedisTemplate.opsForValue().set(fullKey,"",ttl, timeUnit);
                    return null;
                }
                stringRedisTemplate.opsForValue().set(fullKey, JSONUtil.toJsonStr(r),ttl, timeUnit);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                unlock(TEMP_LOCK_KEY+dbQueryId);
            }
            return r;
        }while (!tryLock(TEMP_LOCK_KEY+dbQueryId));
        return null;
    }

    /**
     * 逻辑过期查询
     * @param key 缓存键前缀
     * @param dbQueryId 查询id
     * @param clazz 返回类型
     * @param logicalTTL 逻辑过期时间
     * @param timeUnit 时间单位
     * @param executorService 线程池
     * @param dbQueryFunction 数据库查询函数
     * @return 查询结果
     */
    public <R,ID> R queryWithLogicalExpire(String key, ID dbQueryId, Class<R> clazz, Long logicalTTL, TimeUnit timeUnit, ExecutorService executorService, Function<ID,R> dbQueryFunction) {
        R r;
        String fullKey = key+":"+dbQueryId;
        String shopJson = stringRedisTemplate.opsForValue().get(fullKey);
        if (StrUtil.isBlank(shopJson)) {
            return null;
        }
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        r = JSONUtil.toBean((JSONObject) redisData.getData(),clazz);
        if (redisData.getExpireTime().isAfter(LocalDateTime.now())) {
            return r;
        }
        if (!tryLock(fullKey)) {
            return r;
        }
        executorService.submit(() -> {
            try {
                this.saveToRedis(key,dbQueryId,logicalTTL,dbQueryFunction,timeUnit);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                unlock(fullKey);
            }
        });
        return r;
    }

    /**
     * 计算过期时间
     * @param now 当前时间
     * @param logicalTTL 存活时间
     * @param timeUnit 单位
     * @return 过期时间
     */
    private LocalDateTime calculateExpireTime(LocalDateTime now,Long logicalTTL, TimeUnit timeUnit) {
        return switch (timeUnit) {
            case SECONDS -> now.plusSeconds(logicalTTL);
            case MINUTES -> now.plusMinutes(logicalTTL);
            case HOURS -> now.plusHours(logicalTTL);
            case DAYS -> now.plusDays(logicalTTL);
            default -> throw new IllegalArgumentException("不支持的时间单位,Unsupported Time Unit：" + timeUnit);
        };
    }

    /**
     * 保存数据到redis（带逻辑过期）
     * @param key 键前缀
     * @param id 数据id
     * @param logicalTTL 过期时间
     * @param dbQueryFunction 查询函数
     * @param timeUnit 时间单位
     */
    private  <ID,R> void saveToRedis(String key,ID id,Long logicalTTL,Function<ID,R> dbQueryFunction,TimeUnit timeUnit) {
        String fullKey = key+":"+id;
        R r = dbQueryFunction.apply(id);
        RedisData redisData = new RedisData();
        redisData.setData(r);
        LocalDateTime localDateTime = LocalDateTime.now();
        redisData.setExpireTime(calculateExpireTime(localDateTime,logicalTTL,timeUnit));
        stringRedisTemplate.opsForValue().set(fullKey,JSONUtil.toJsonStr(redisData));
    }

    /**
     * 获取锁
     * @param key 锁键
     * @return 是否获取成功
     */
    private boolean tryLock(String key) {
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(b);
    }

    /**
     * 释放锁
     * @param key 锁键
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}