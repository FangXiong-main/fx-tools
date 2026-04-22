package com.fangxiong.common.converters;

import com.fangxiong.common.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MapConverter implements GenericTypeJsonConverter {

    @Override
    public Object convert(String json, Type type) {
        StringBuilder sbMain = new StringBuilder();
        Map<Object,Object> convertedMap = new HashMap<>();
        if(type instanceof ParameterizedType pt){
            if(pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1] instanceof ParameterizedType pt2){
                Map<String, String> partlyMap = StrUtils.getJSONKeysAndValuesWithPartlyMap(json);
                for(String key : partlyMap.keySet()){
                    convertedMap.put(key, GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt2.getRawType()).convert(partlyMap.get(key),pt2));
                }
            }else{
                ParameterizedType ptTemp = (ParameterizedType) type;
                Class<?> tempClazz = (Class<?>) ptTemp.getActualTypeArguments()[pt.getActualTypeArguments().length-1];
                return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) ptTemp.getRawType()).convert(json,tempClazz);
            }
        }else {
            Map<String, String> partlyMap = StrUtils.getJSONKeysAndValuesWithPartlyMap(json);
            if(type == Object.class){
                for(String key : partlyMap.keySet()){
                    Type tempType = ObjectConverter.detectObjectType(partlyMap.get(key));
                    if (tempType instanceof ParameterizedType pt){
                        convertedMap.put(key,GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt.getRawType()).convert(partlyMap.get(key),pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1]));
                    }else{
                        convertedMap.put(key,NonGenericTypeConverterFactory.getConverter((Class<?>) tempType).convert(partlyMap.get(key),(Class<?>) tempType));
                    }
                }
            }else {
                for(String key : partlyMap.keySet()){
                    convertedMap.put(key,NonGenericTypeConverterFactory.getConverter((Class<?>) type).convert(partlyMap.get(key),(Class<?>) type));
                }
            }
        }
        return convertedMap;
    }
}
