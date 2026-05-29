package com.fangxiong.jsonUtilsCore.converters;

import com.fangxiong.jsonUtilsCore.coreUtil.JsonOperationUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MapConverter implements GenericTypeJsonConverter {

    @Override
    public Object convert(String json, Type type) {
        if(json==null){
            return null;
        }
        Map<Object,Object> convertedMap = new HashMap<>();
        if(type instanceof ParameterizedType pt){
            if(pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1] instanceof ParameterizedType pt2){
                Map<String, String> partlyMap = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(json,null);
                for(String key : partlyMap.keySet()){
                    convertedMap.put(key, GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt2.getRawType()).convert(partlyMap.get(key),pt2));
                }
            }else{
                ParameterizedType ptTemp = (ParameterizedType) type;
                Class<?> tempClazz = (Class<?>) ptTemp.getActualTypeArguments()[pt.getActualTypeArguments().length-1];
                return GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) ptTemp.getRawType()).convert(json,tempClazz);
            }
        }else {
            Map<String, String> partlyMap = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(json,null);
            if(partlyMap.isEmpty()){
                if (json.equals("null")){
                    convertedMap.put(null,null);
                } else if (JsonOperationUtil.jsonIsBlankMap(json)) {
                    convertedMap.put(null,new HashMap<>());
                } else if (JsonOperationUtil.jsonIsBlankList(json)) {
                    convertedMap.put(null,new ArrayList<>());
                }
            }else{
                if(type == Object.class){
                    for(String key : partlyMap.keySet()){
                        Type tempType = ObjectConverter.detectObjectType(partlyMap.get(key));
                        if (tempType instanceof ParameterizedType pt){
                            convertedMap.put(key,GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt.getRawType()).convert(partlyMap.get(key),pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1]));
                        } else if (tempType==null) {
                            String tempValue = partlyMap.get(key);
                            if (JsonOperationUtil.jsonIsBlankMap(tempValue)){
                                convertedMap.put(key,new HashMap<>());
                            } else if (JsonOperationUtil.jsonIsBlankList(tempValue)) {
                                convertedMap.put(key,new ArrayList<>());
                            }else {
                                convertedMap.put(key,null);
                            }
                        } else{
                            convertedMap.put(key,NonGenericTypeConverterFactory.getConverter((Class<?>) tempType).convert(partlyMap.get(key),(Class<?>) tempType,null));
                        }
                    }
                }else {
                    for(String key : partlyMap.keySet()){
                        convertedMap.put(key,NonGenericTypeConverterFactory.getConverter((Class<?>) type).convert(partlyMap.get(key),(Class<?>) type,null));
                    }
                }
            }
        }
        return convertedMap;
    }
}
