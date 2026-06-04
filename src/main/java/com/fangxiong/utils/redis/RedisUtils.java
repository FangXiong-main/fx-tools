package com.fangxiong.utils.redis;

import com.fangxiong.jsonUtilsCore.coreUtil.CustomizeGenericTypes;
import com.fangxiong.utils.json.JsonUtils;
import com.fangxiong.jsonUtilsCore.coreUtil.JsonOperationUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.fangxiong.constants.SystemConstants.*;

public class RedisUtils {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造方法
     * @param stringRedisTemplate redis模板
     */
    public RedisUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void remove(String key) {
        stringRedisTemplate.delete(key);
    }

    public boolean checkSMSCodeSendOverTimes(String key,Integer times,Long ttl){
        String s = stringRedisTemplate.opsForValue().get(key);
        if(!JsonOperationUtil.strIsNotBlank(s)){
            stringRedisTemplate.opsForValue().set(key,"0",ttl,TimeUnit.MILLISECONDS);
            return false;
        }
        Integer redisTimes = JsonUtils.jsonToBean(s, Integer.class);
        return redisTimes >= times;
    }

    public void increaseSMSCodeSendTimes(String key){
        stringRedisTemplate.opsForValue().increment(key,1);
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
        String jsonStr = JsonUtils.beanToJson(o);
        if(l==0L){
            stringRedisTemplate.opsForValue().set(key,jsonStr);
        }else{
            stringRedisTemplate.opsForValue().set(key,jsonStr,l,TimeUnit.MILLISECONDS);
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
        if(JsonOperationUtil.strIsNotBlank(jsonStr)){
            return JsonUtils.jsonToBean(jsonStr, clazz);
        }
        return null;
    }

    public Object getStringValueWithCustomizeGenericTypes(String key, CustomizeGenericTypes typeParams) {
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        if(JsonOperationUtil.strIsNotBlank(jsonStr)){
            return JsonUtils.jsonToBean(jsonStr, typeParams);
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
        if (JsonOperationUtil.strIsNotBlank(shopJson)) {
            return JsonUtils.jsonToBean(shopJson, clazz);
        }
        if(shopJson!=null){
            return null;
        }
        r = dbQueryFunction.apply(dbQueryId);
        if (r == null) {
            stringRedisTemplate.opsForValue().set(fullKey,"",ttl, timeUnit);
            return null;
        }
        stringRedisTemplate.opsForValue().set(fullKey, JsonUtils.beanToJson(r),ttl, timeUnit);
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
            if (JsonOperationUtil.strIsNotBlank(shopJson)) {
                return JsonUtils.jsonToBean(shopJson, clazz);
            }
            if(shopJson!=null){
                return null;
            }
            try {
                if(!tryLock(TEMP_LOCK_KEY + dbQueryId)){
                    continue;
                }
                shopJson = stringRedisTemplate.opsForValue().get(fullKey);
                if (JsonOperationUtil.strIsNotBlank(shopJson)) {
                    return JsonUtils.jsonToBean(shopJson, clazz);
                }
                r = dbQueryFunction.apply(dbQueryId);
                if (r == null) {
                    stringRedisTemplate.opsForValue().set(fullKey,"",ttl, timeUnit);
                    return null;
                }
                stringRedisTemplate.opsForValue().set(fullKey, JsonUtils.beanToJson(r),ttl, timeUnit);
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
        if (JsonOperationUtil.strIsNotBlank(shopJson)) {
            return null;
        }
        RedisData redisData = JsonUtils.jsonToBean(shopJson, RedisData.class);
        r = JsonUtils.jsonToBean(redisData.getData().toString(),clazz);
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
     * 基于Redis自增的全局唯一Id生成器 格式：自定义时间戳位数，机器码位数，序列号位数(时间戳+机器码+序列号)
     * @param keyPrefix Key值前缀 redis自增Key值格式：（keyPrefix：startTime:）
     * @param startTime 初始时间 时间戳格式：now - startTime
     * @param timeKeyPattern 时间Key值格式
     * @param timeStampDigits Id中时间戳所占的位数
     * @param machineCodeDigits Id中机器码所占的位数
     * @param sequenceDigits Id中序列号所占的位数
     * @param machineCode 机器码
    */
    public long uniqueIdGenerator(String keyPrefix,String timeKeyPattern,LocalDateTime startTime,int timeStampDigits,int machineCodeDigits,int sequenceDigits,long machineCode) {
        if(timeStampDigits+machineCodeDigits+sequenceDigits>MAX_DIGITS_OF_ID || timeStampDigits+machineCodeDigits+sequenceDigits<=0){
            throw new RuntimeException("设定的Id位数超出最大63位的限制！且最少为1位，The digits of the Id digits must be less than or equal to 63 and greater than or equal to 1");
        }
        long id = 0;long digitsCount = 0;
        LocalDateTime now = LocalDateTime.now();
        String timeKey = now.format(DateTimeFormatter.ofPattern(timeKeyPattern));
        long timeStampKey = now.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC);
        Long sequenceNum = stringRedisTemplate.opsForValue().increment(keyPrefix + ":" + timeKey);
        if(timeStampDigits!=0){
            id = timeStampKey<<(MAX_DIGITS_OF_ID-timeStampDigits);
            digitsCount+=timeStampDigits;
        }
        if(machineCodeDigits!=0){
            long temp = machineCode << MAX_DIGITS_OF_ID - machineCodeDigits - digitsCount;
            digitsCount+=machineCodeDigits;
            id = id | temp;
        }
        if(sequenceDigits!=0){
            long temp = sequenceNum << MAX_DIGITS_OF_ID - sequenceDigits - digitsCount ;
            id = id | temp;
        }
        return id;
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
        stringRedisTemplate.opsForValue().set(fullKey,JsonUtils.beanToJson(redisData));
    }

    public Boolean enableLock(String key) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 20, TimeUnit.SECONDS);
    }

    public Boolean disableLock(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 获取锁
     * @param key 锁键
     * @return 是否获取成功
     */
    private boolean tryLock(String key) {
        Boolean b = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return b.booleanValue();
    }

    /**
     * 释放锁
     * @param key 锁键
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}