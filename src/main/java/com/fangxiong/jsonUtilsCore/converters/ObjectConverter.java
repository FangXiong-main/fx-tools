package com.fangxiong.jsonUtilsCore.converters;

import com.fangxiong.globalUtils.GlobalConverterCacheLib;
import com.fangxiong.jsonUtilsCore.annotations.IgnoredField;
import com.fangxiong.jsonUtilsCore.annotations.NotNullClass;
import com.fangxiong.jsonUtilsCore.annotations.NotNullField;
import com.fangxiong.globalUtils.GlobalCustomizeClazzDetector;
import com.fangxiong.jsonUtilsCore.coreUtil.CustomizeGenericTypes;
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
    public Object convert(String s, Class<?> clazz,Field field) {
        String tempFiledName=null;String tempValue=null;String tempFiledType=null;
        if(GlobalCustomizeClazzDetector.isCustomizeClazz(clazz)){
            try {
                if (s == null){
                    return null;
                }
                Field[] df = GlobalConverterCacheLib.getConverterFieldCache(clazz);
                Map<Field,Method> setMethodCache = GlobalConverterCacheLib.getConverterSetMethodCache(clazz);
                Map<Field,Type> partTypeCache = GlobalConverterCacheLib.getConverterPartTypeCache(clazz);
                Map<String, String> allFieldValue = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(s);
                Object convertedObj = clazz.getDeclaredConstructor().newInstance();
                for(Field f : df){
                    tempFiledType=f.getType().getTypeName();tempFiledName = f.getName();tempValue=allFieldValue.get(f.getName());
                    if(f.getAnnotation(IgnoredField.class)!=null){
                        setMethodCache.get(f).invoke(convertedObj,convertFiled(null,partTypeCache.get(f),f));
                    } else if(tempValue!=null&&!tempValue.equals("null")){
                        setMethodCache.get(f).invoke(convertedObj,convertFiled(tempValue,partTypeCache.get(f),f));
                    } else if (clazz.getAnnotation(NotNullClass.class)!=null&&tempValue==null) {
                        throw new JsonConvertFailureError("Detected class:'"+clazz.getName()+"' with @NotNullClass annotation,current converting field is:'"+tempFiledName+"'"+",but the value ready to convert is null.Caused by Json syntax error.");
                    } else if (clazz.getAnnotation(NotNullClass.class)!=null&& Objects.equals(tempValue, "null")) {
                        throw new JsonConvertFailureError("Detected class:'"+clazz.getName()+"' with @NotNullClass annotation,current converting field is:'"+tempFiledName+"'"+",but the value ready to convert is null.Caused by custom restrict error.");
                    } else if (f.getAnnotation(NotNullField.class) != null&&tempValue==null) {
                        throw new JsonConvertFailureError("Detected field '"+tempFiledName+"'"+" with @NotNullFiled annotation,but the value ready to convert is null.Caused by Json syntax error.");
                    } else if (f.getAnnotation(NotNullField.class) != null&&Objects.equals(tempValue, "null")) {
                        throw new JsonConvertFailureError("Detected field '"+tempFiledName+"'"+" with @NotNullFiled annotation,but the value ready to convert is null.Caused by custom restrict error.");
                    } else {
                        setMethodCache.get(f).invoke(convertedObj,convertFiled(null,partTypeCache.get(f),f));
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
                return NonGenericTypeConverterFactory.getConverter((Class<?>) type).convert(s,(Class<?>) type,null);
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

    private static Object convertFiled(String s,Type type,Field field){
        if (type instanceof ParameterizedType pt){
            return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt.getRawType()).convert(s,pt);
        }else{
            return NonGenericTypeConverterFactory.getConverter((Class<?>) type).convert(s,(Class<?>) type,field);
        }
    }

}
