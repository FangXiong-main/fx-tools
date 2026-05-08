package com.fangxiong.globalUtils;

import com.fangxiong.globalExceptions.GlobalConverterCacheLibError;
import com.fangxiong.mysqlUtilsCore.EnableUnderscoreToCamelCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.fangxiong.SystemConstants.SET;

public class GlobalConverterCacheLib {
    private static final Map<Class<?>, Field[]> converterFieldCache = new HashMap<>();
    private static final Map<Class<?>,Map<String, Method>> converterSetMethodCache = new HashMap<>();
    private static final Map<Class<?>,Map<String, Type>> converterPartTypeCache = new HashMap<>();
    private static final Map<Class<?>, ArrayList<String>> converterFiledNameCache = new HashMap<>();

    public static Field[] getConverterFieldCache(Class<?> clazz){
        Field[] fields = converterFieldCache.get(clazz);
        if (fields == null){
            return cacheAllField(clazz);
        }
        return fields;
    }

    public static Map<String, Method> getConverterSetMethodCache(Class<?> clazz){
        Map<String, Method> stringMethodMap = converterSetMethodCache.get(clazz);
        if(stringMethodMap == null){
            return cacheAllSetMethod(clazz);
        }
        return stringMethodMap;
    }

    public static Map<String, Type> getConverterPartTypeCache(Class<?> clazz){
        Map<String, Type> stringTypeMap = converterPartTypeCache.get(clazz);
        if(stringTypeMap == null){
            return cacheAllFieldType(clazz);
        }
        return stringTypeMap;
    }

    private static Field[] cacheAllField(Class<?> clazz){
        Field[] df = clazz.getDeclaredFields();
        converterFieldCache.put(clazz,df);
        return df;
    }

    private static Map<String,Type> cacheAllFieldType(Class<?> clazz){
        Field[] df = clazz.getDeclaredFields();
        Map<String,Type> cacheMap = new HashMap<>();
        for(Field f : df){
            cacheMap.put(f.getName(),f.getGenericType());
        }
        converterPartTypeCache.put(clazz,cacheMap);
        return cacheMap;
    }

    private static Map<String,Method> cacheAllSetMethod(Class<?> clazz)  {
        Map<String,Method> cacheMap;
        String methodName = null;
        try {
            Field[] df = clazz.getDeclaredFields();
            cacheMap = new HashMap<>();
            for(Field f : df){
                char upperCase = Character.toUpperCase(f.getName().charAt(0));
                if(f.getName().length()==1){
                    methodName = SET+ upperCase;
                    cacheMap.put(f.getName(),clazz.getDeclaredMethod(methodName,f.getType()));
                }else{
                    methodName = SET+ upperCase +f.getName().substring(1);
                    cacheMap.put(f.getName(), clazz.getDeclaredMethod(methodName,f.getType()));
                }
            }
        } catch (NoSuchMethodException e) {
            throw new GlobalConverterCacheLibError("Cannot find method named '"+methodName+"' in class "+clazz.getName(),e);
        }
        converterSetMethodCache.put(clazz,cacheMap);
        return cacheMap;
    }
}
