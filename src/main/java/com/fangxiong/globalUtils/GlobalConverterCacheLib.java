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

import static com.fangxiong.constants.SystemConstants.GET;
import static com.fangxiong.constants.SystemConstants.SET;

public class GlobalConverterCacheLib {
    private static final Map<Class<?>, Field[]> converterFieldCache = new HashMap<>();
    private static final Map<Class<?>,Map<Field, Method>> converterSetMethodCache = new HashMap<>();
    private static final Map<Class<?>,Map<Field, Method>> converterGetMethodCache = new HashMap<>();
    private static final Map<Class<?>,Map<Field, Type>> converterPartTypeCache = new HashMap<>();
    private static final Map<Class<?>, ArrayList<String>> converterFiledNameCache = new HashMap<>();
    private static final Map<Class<?>, Map<String,Method>> mysqlMapperMethodCache = new HashMap<>();
    private static final Map<Method,Map<String,Integer>> mysqlParamIndexCache = new HashMap<>();

    public static Map<String,Integer> getMysqlParamIndexCache(Method method){
        Map<String, Integer> parameterIntegerMap = mysqlParamIndexCache.get(method);
        if(parameterIntegerMap==null||parameterIntegerMap.isEmpty()){
            return cacheAllMysqlParamIndex(method);
        }
        return parameterIntegerMap;
    }

    public static Map<String,Method> getMysqlMapperMethodCache(Class<?> clazz){
        Map<String,Method> cacheMap = mysqlMapperMethodCache.get(clazz);
        if (cacheMap==null||cacheMap.isEmpty()){
            return cacheAllMysqlMapperMethod(clazz);
        }
        return cacheMap;
    }

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

    public static Map<Field,Method> getConverterGetMethodCache(Class<?> clazz){
        Map<Field, Method> fieldMethodMap = converterGetMethodCache.get(clazz);
        if(fieldMethodMap == null){
            return cacheAllGetMethod(clazz);
        }
        return fieldMethodMap;
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

    private static Map<String,Method> cacheAllMysqlMapperMethod(Class<?> clazz){
        Method[] methods = clazz.getMethods();
        Map<String,Method> cacheMap = new HashMap<>();
        for (Method m : methods){
            cacheMap.put(m.getName(),m);
        }
        mysqlMapperMethodCache.put(clazz,cacheMap);
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
        mysqlParamIndexCache.put(method,cacheMap);
        return cacheMap;
    }

    private static Map<Field,Method> cacheAllGetMethod(Class<?> clazz){
        Field[] fields = clazz.getDeclaredFields();
        Map<Field,Method> cacheMap = new HashMap<>();
        String methodName = null;
        try {
            cacheMap = new HashMap<>();
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
        converterGetMethodCache.put(clazz, cacheMap);
        return cacheMap;
    }

}
