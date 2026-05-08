package com.fangxiong.jsonUtilsCore.converters;

import com.fangxiong.globalUtils.GlobalConverterCacheLib;
import com.fangxiong.jsonUtilsCore.annotations.IgnoredField;
import com.fangxiong.jsonUtilsCore.annotations.NotNullClass;
import com.fangxiong.jsonUtilsCore.annotations.NotNullField;
import com.fangxiong.globalUtils.CustomizeClazzDetector;
import com.fangxiong.globalUtils.CustomizeGenericTypes;
import com.fangxiong.jsonUtilsCore.exceptions.JsonConvertFailureError;
import com.fangxiong.jsonUtilsCore.coreUtil.JsonOperationUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectConverter implements NonGenericTypeJsonConverter {

    private static final Pattern isIntegerPattern = Pattern.compile("-?(\\d+)");
    private static final Pattern isDicimalPattern = Pattern.compile("-?(\\d+\\.\\d+)");
    @Override
    public Object convert(String s, Class<?> clazz) {
        String tempFiledName=null;String tempValue=null;String tempFiledType=null;
        if(CustomizeClazzDetector.isCustomizeClazz(clazz)){
            try {
                Field[] df = GlobalConverterCacheLib.getConverterFieldCache(clazz);
                Map<String,Method> setMethodCache = GlobalConverterCacheLib.getConverterSetMethodCache(clazz);
                Map<String,Type> partTypeCache = GlobalConverterCacheLib.getConverterPartTypeCache(clazz);
                Map<String, String> allFieldValue = getAllFieldValue(df, s);
                Object convertedObj = clazz.getDeclaredConstructor().newInstance();
                for(Field f : df){
                    tempFiledType=f.getType().getTypeName();tempFiledName = f.getName();tempValue=allFieldValue.get(f.getName());
                    if(f.getAnnotation(IgnoredField.class)!=null){
                        setMethodCache.get(f.getName()).invoke(convertedObj,convertFiled(null,partTypeCache.get(f.getName())));
                    } else if(tempValue!=null&&!tempValue.equals("null")){
                        setMethodCache.get(f.getName()).invoke(convertedObj,convertFiled(allFieldValue.get(f.getName()),partTypeCache.get(f.getName())));
                    } else if (clazz.getAnnotation(NotNullClass.class)!=null&&tempValue==null) {
                        throw new JsonConvertFailureError("Detected class:'"+clazz.getName()+"' with @NotNullClass annotation,current converting field is:'"+tempFiledName+"'"+",but the value ready to convert is null.Caused by Json syntax error.");
                    } else if (clazz.getAnnotation(NotNullClass.class)!=null&& Objects.equals(tempValue, "null")) {
                        throw new JsonConvertFailureError("Detected class:'"+clazz.getName()+"' with @NotNullClass annotation,current converting field is:'"+tempFiledName+"'"+",but the value ready to convert is null.Caused by custom restrict error.");
                    } else if (f.getAnnotation(NotNullField.class) != null&&tempValue==null) {
                        throw new JsonConvertFailureError("Detected field '"+tempFiledName+"'"+" with @NotNullFiled annotation,but the value ready to convert is null.Caused by Json syntax error.");
                    } else if (f.getAnnotation(NotNullField.class) != null&&Objects.equals(tempValue, "null")) {
                        throw new JsonConvertFailureError("Detected field '"+tempFiledName+"'"+" with @NotNullFiled annotation,but the value ready to convert is null.Caused by custom restrict error.");
                    } else {
                        setMethodCache.get(f.getName()).invoke(convertedObj,convertFiled(null,partTypeCache.get(f.getName())));
                    }
                }
                return convertedObj;
            } catch (Exception e) {
                throw new JsonConvertFailureError("Convert fieldType: '"+tempFiledType+"',fieldName: '"+tempFiledName+"' with"+" value: '"+tempValue+"' failed.",e);
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
            ArrayList<String> listToArr = JsonOperationUtil.getConvertJsonValueListToArr(objectValueStr);
            if (!listToArr.isEmpty()){
                return new CustomizeGenericTypes(List.class,Object.class);
            }else {
                return null;
            }
        } else if (objectValueStr.charAt(0)=='{') {
            Map<String, String> partlyMap = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(objectValueStr);
            if(!partlyMap.isEmpty()){
                return new CustomizeGenericTypes(Map.class,Object.class);
            }else {
                return null;
            }
        }
        return String.class;
    }

    private static Map<String,String> getAllFieldValue(Field[] df, String json){
        Map<String,String> cacheFiledValueMap = new HashMap<>();
        Map<String,String> tempFieldValueMap = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(json);
        for (Field f:df){
            cacheFiledValueMap.put(f.getName(),tempFieldValueMap.get(f.getName()));
        }
        return cacheFiledValueMap;
    }

    private static Object convertFiled(String s,Type type){
        if (type instanceof ParameterizedType pt){
            return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt.getRawType()).convert(s,pt);
        }else{
            return NonGenericTypeConverterFactory.getConverter((Class<?>) type).convert(s,(Class<?>) type);
        }
    }

}
