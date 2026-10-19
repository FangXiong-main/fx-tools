package com.fangxiong.globalUtils;

import com.fangxiong.globalExceptions.GlobalConverterCacheLibError;
import com.fangxiong.jsonUtilsCore.exceptions.JsonParserFailureError;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fangxiong.constants.SystemConstants.GET;
import static com.fangxiong.constants.SystemConstants.SET;

public class GlobalConverterCacheLib {
    private static final Map<Class<?>, Field[]> converterFieldCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>,Map<Field, Method>> converterSetMethodCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>,Map<Field, Method>> converterGetMethodCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>,Map<Field, Type>> converterPartTypeCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String,Method>> mysqlMapperMethodCache = new ConcurrentHashMap<>();
    private static final Map<Method,Map<String,Integer>> mysqlParamIndexCache = new ConcurrentHashMap<>();

    public static Map<String,Integer> getMysqlParamIndexCache(Method method){
        return mysqlParamIndexCache.computeIfAbsent(method, GlobalConverterCacheLib::cacheAllMysqlParamIndex);
    }

    public static Map<String,Method> getMysqlMapperMethodCache(Class<?> clazz){
        return mysqlMapperMethodCache.computeIfAbsent(clazz, GlobalConverterCacheLib::cacheAllMysqlMapperMethod);
    }

    public static Field[] getConverterFieldCache(Class<?> clazz){
        return converterFieldCache.computeIfAbsent(clazz, GlobalConverterCacheLib::cacheAllField);
    }

    public static Map<Field, Method> getConverterSetMethodCache(Class<?> clazz){
        return converterSetMethodCache.computeIfAbsent(clazz, GlobalConverterCacheLib::cacheAllSetMethod);
    }

    public static Map<Field,Method> getConverterGetMethodCache(Class<?> clazz){
        return converterGetMethodCache.computeIfAbsent(clazz,GlobalConverterCacheLib::cacheAllGetMethod);
    }

    public static Map<Field, Type> getConverterPartTypeCache(Class<?> clazz){
        return converterPartTypeCache.computeIfAbsent(clazz, GlobalConverterCacheLib::cacheAllFieldType);
    }

    private static Field[] cacheAllField(Class<?> clazz){
        return clazz.getDeclaredFields();
    }

    private static Map<Field,Type> cacheAllFieldType(Class<?> clazz){
        Field[] df = clazz.getDeclaredFields();
        Map<Field,Type> cacheMap = new HashMap<>();
        for(Field f : df){
            cacheMap.put(f,f.getGenericType());
        }
        return cacheMap;
    }

    private static Map<Field,Method> cacheAllSetMethod(Class<?> clazz)  {
        Map<Field,Method> cacheMap;
        String methodName = null;
        try {
            Field[] df = clazz.getDeclaredFields();
            cacheMap = new HashMap<>();
            for(Field f : df){
                char upperCase = Character.toUpperCase(f.getName().charAt(0));
                if(f.getName().length()==1){
                    methodName = SET+ upperCase;
                    cacheMap.put(f,clazz.getDeclaredMethod(methodName,f.getType()));
                }else{
                    methodName = SET+ upperCase +f.getName().substring(1);
                    cacheMap.put(f, clazz.getDeclaredMethod(methodName,f.getType()));
                }
            }
        } catch (NoSuchMethodException e) {
            throw new GlobalConverterCacheLibError("Cannot find method named '"+methodName+"' in class "+clazz.getName(),e);
        }
        return cacheMap;
    }

    private static Map<String,Method> cacheAllMysqlMapperMethod(Class<?> clazz){
        Method[] methods = clazz.getMethods();
        Map<String,Method> cacheMap = new HashMap<>();
        for (Method m : methods){
            cacheMap.put(m.getName(),m);
        }
        return cacheMap;
    }

    private static Map<String,Integer> cacheAllMysqlParamIndex(Method method){
        int index = 0;
        Map<String,Integer> cacheMap = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for(Parameter p : parameters){
            cacheMap.put(p.getName(),index);
            index++;
        }
        return cacheMap;
    }

    private static Map<Field,Method> cacheAllGetMethod(Class<?> clazz){
        Field[] fields = clazz.getDeclaredFields();
        Map<Field,Method> cacheMap = new HashMap<>();
        String methodName = null;
        try {
            for (Field f : fields) {
                char upperCase = Character.toUpperCase(f.getName().charAt(0));
                if(f.getName().length()==1){
                    methodName = GET+ upperCase;
                    cacheMap.put(f,clazz.getDeclaredMethod(methodName));
                }else{
                    methodName = GET+ upperCase +f.getName().substring(1);
                    cacheMap.put(f, clazz.getDeclaredMethod(methodName));
                }
            }
        } catch (Exception e) {
            throw new GlobalConverterCacheLibError("Cannot find method named '"+methodName+"' in class "+clazz.getName(),e);
        }
        return cacheMap;
    }

}
