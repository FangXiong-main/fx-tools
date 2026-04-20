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
                Map<String, String> splitMainJsonToPartlyMap = StrUtils.getSplitMainJsonToPartlyMap(sbMain, json);
                for(String key : splitMainJsonToPartlyMap.keySet()){
                    convertedMap.put(key.substring(1,key.length()-1), GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt2.getRawType()).convert(splitMainJsonToPartlyMap.get(key),pt2));
                }
            }else{
                ParameterizedType ptTemp = (ParameterizedType) type;
                Class<?> tempClazz = (Class<?>) ptTemp.getActualTypeArguments()[pt.getActualTypeArguments().length-1];
                return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) ptTemp.getRawType()).convert(json,tempClazz);
            }
        }else {
            Map<String, String> jsonKeysAndValuesWithPartlyMap = StrUtils.getJSONKeysAndValuesWithPartlyMap(json);
            for(String key : jsonKeysAndValuesWithPartlyMap.keySet()){
                convertedMap.put(key,NonGenericTypeConverterFactory.getConverter((Class<?>) type).convert(jsonKeysAndValuesWithPartlyMap.get(key),(Class<?>) type));
            }
        }
        return convertedMap;
    }
}
