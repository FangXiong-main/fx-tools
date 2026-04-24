package com.fangxiong.common.converters;

import com.fangxiong.common.CustomizeGenericTypes;
import com.fangxiong.common.*;
import com.fangxiong.utils.json.StrUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fangxiong.utils.redis.SystemConstants.SET;

public class ObjectConverter implements NonGenericTypeJsonConverter {

    private static final Pattern isIntegerPattern = Pattern.compile("-?(\\d+)");
    private static final Pattern isDicimalPattern = Pattern.compile("-?(\\d+\\.\\d+)");

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
            Type type = detectObjectType(s);
            if (type instanceof ParameterizedType pt){
                return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt.getRawType()).convert(s,pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1]);
            }else {
                return NonGenericTypeConverterFactory.getConverter((Class<?>) type).convert(s,(Class<?>) type);
            }
        }
    }

    public static Type detectObjectType(String objectValueStr){
        if (objectValueStr.isEmpty()){
            return String.class;
        } else if (objectValueStr.equals("null")) {
            return null;
        } else if (Character.isDigit(objectValueStr.charAt(0))&&objectValueStr.length()<=9){
            Matcher intergerMatcher = isIntegerPattern.matcher(objectValueStr);
            Matcher dicimalMatcher = isDicimalPattern.matcher(objectValueStr);
            if(intergerMatcher.matches()){
                return Integer.class;
            }else if (dicimalMatcher.matches()){
                return Double.class;
            }
            return String.class;
        } else if (objectValueStr.equals("false")||objectValueStr.equals("true")) {
            return Boolean.class;
        } else if (objectValueStr.charAt(0)=='[') {
            ArrayList<String> listToArr = StrUtils.getConvertJsonValueListToArr(objectValueStr);
            if (!listToArr.isEmpty()){
                return new CustomizeGenericTypes(List.class,Object.class);
            }else {
                return null;
            }
        } else if (objectValueStr.charAt(0)=='{') {
            Map<String, String> partlyMap = StrUtils.getKeysAndValuesMapWithJsonStr(objectValueStr);
            if(!partlyMap.isEmpty()){
                return new CustomizeGenericTypes(Map.class,Object.class);
            }else {
                return null;
            }
        }
        return String.class;
    }

    private static Map<String,String> cacheAllFieldValue(Class<?> clazz,String json){
        StringBuilder sbMain = new StringBuilder();
        Field[] df = clazz.getDeclaredFields();
        Map<String,String> cacheFiledValueMap = new HashMap<>();
        //Map<String,String> cacheMapOrListValueMap = StrUtils.getSplitMainJsonToPartlyMap(sbMain,json);
        Map<String,String> cacheFieldValueMap = StrUtils.getKeysAndValuesMapWithJsonStr(json);
        for (Field f:df){
            cacheFiledValueMap.put(f.getName(),cacheFieldValueMap.get(f.getName()));
        }
        return cacheFiledValueMap;
    }


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
        String methodName;
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
        return cacheMap;
    }
}
