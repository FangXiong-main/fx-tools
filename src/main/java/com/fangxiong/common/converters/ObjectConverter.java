package com.fangxiong.common.converters;

import com.fangxiong.common.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static com.fangxiong.redis.SystemConstants.SET;

public class ObjectConverter implements NonGenericTypeJsonConverter {

    @Override
    public Object convert(String s, Class<?> clazz) {
        if(CustomizeClazzDetector.isCustomizeClazz(clazz)){
            try {
                Map<String,Method> setMethodCache = cacheAllSetMethod(clazz);
                Map<String,Type> partTypeCache = cacheAllFieldType(clazz);
                Map<String, String> allFieldValueCache = cacheAllFieldValue(clazz, s);
                Object convertedObj = clazz.getDeclaredConstructor().newInstance();
                for(Field f : clazz.getDeclaredFields()){
                    setMethodCache.get(f.getName()).invoke(convertedObj,convertFiled(allFieldValueCache.get(f.getName()),partTypeCache.get(f.getName())));
                }
                return convertedObj;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {

        }
        return null;
    }

    private static Map<String,String> cacheAllFieldValue(Class<?> clazz,String json){
        StringBuilder sbMain = new StringBuilder();
        Field[] df = clazz.getDeclaredFields();
        Map<String,String> cacheFiledValueMap = new HashMap<>();
        Map<String,String> cacheMapOrListValueMap = StrUtils.getSplitMainJsonToPartlyMap(sbMain,json);
        Map<String,String> cacheNormalFieldValueMap = StrUtils.getJSONKeysAndValuesWithPartlyMap(sbMain.toString());
        for (Field f:df){
            cacheFiledValueMap.put(f.getName(),cacheNormalFieldValueMap.get(f.getName()));
        }
        for (String key : cacheMapOrListValueMap.keySet()){
            cacheFiledValueMap.remove(key);
            cacheFiledValueMap.put(key.substring(1,key.length()-1),cacheMapOrListValueMap.get(key));
        }
        return cacheFiledValueMap;
    }

//    private static Map<String,ArrayList<String>> cacheEntityFieldInnerValueList(String json,Class<?> clazz){
//
//    }

    private static Map<String,Type> cacheAllFieldType(Class<?> clazz){
        Field[] df = clazz.getDeclaredFields();
        Map<String,Type> cacheMap = new HashMap<>();
        for(Field f : df){
            cacheMap.put(f.getName(),f.getGenericType());
        }
        return cacheMap;
    }

    private static Object convertFiled(String s,Type type){
        if (type instanceof ParameterizedType pt){
            return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt.getRawType()).convert(s,pt);
        }else{
            return NonGenericTypeConverterFactory.getConverter((Class<?>) type).convert(s,(Class<?>) type);
        }
    }

    private static Map<String,Method> cacheAllSetMethod(Class<?> clazz) throws NoSuchMethodException {
        Field[] df = clazz.getDeclaredFields();
        Map<String,Method> cacheMap = new HashMap<>();
        String methodName ="";
        for(Field f : df){
            if(f.getName().length()==1){
                methodName = SET+Character.toUpperCase(f.getName().charAt(0));
                cacheMap.put(f.getName(),clazz.getDeclaredMethod(methodName,f.getType()));
            }else{
                methodName = SET+Character.toUpperCase(f.getName().charAt(0))+f.getName().substring(1);
                cacheMap.put(f.getName(), clazz.getDeclaredMethod(methodName,f.getType()));
            }
        }
        return cacheMap;
    }
}
