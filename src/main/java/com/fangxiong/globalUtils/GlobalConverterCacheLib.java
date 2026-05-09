package com.fangxiong.globalUtils;

import com.fangxiong.globalExceptions.GlobalConverterCacheLibError;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.fangxiong.constants.SystemConstants.SET;

public class GlobalConverterCacheLib {
    private static final Map<Class<?>, Field[]> converterFieldCache = new HashMap<>();
    private static final Map<Class<?>,Map<Field, Method>> converterSetMethodCache = new HashMap<>();
    private static final Map<Class<?>,Map<Field, Type>> converterPartTypeCache = new HashMap<>();
    private static final Map<Class<?>, ArrayList<String>> converterFiledNameCache = new HashMap<>();

    public static Field[] getConverterFieldCache(Class<?> clazz){
        Field[] fields = converterFieldCache.get(clazz);
        if (fields == null){
            return cacheAllField(clazz);
        }
        return fields;
    }

    public static Map<Field, Method> getConverterSetMethodCache(Class<?> clazz){
        Map<Field, Method> stringMethodMap = converterSetMethodCache.get(clazz);
        if(stringMethodMap == null){
            return cacheAllSetMethod(clazz);
        }
        return stringMethodMap;
    }

    public static Map<Field, Type> getConverterPartTypeCache(Class<?> clazz){
        Map<Field, Type> stringTypeMap = converterPartTypeCache.get(clazz);
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

    private static Map<Field,Type> cacheAllFieldType(Class<?> clazz){
        Field[] df = clazz.getDeclaredFields();
        Map<Field,Type> cacheMap = new HashMap<>();
        for(Field f : df){
            cacheMap.put(f,f.getGenericType());
        }
        converterPartTypeCache.put(clazz,cacheMap);
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
        converterSetMethodCache.put(clazz,cacheMap);
        return cacheMap;
    }
}
