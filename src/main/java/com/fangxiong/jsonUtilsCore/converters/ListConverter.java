package com.fangxiong.jsonUtilsCore.converters;

import com.fangxiong.jsonUtilsCore.coreUtil.JsonOperationUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ListConverter implements GenericTypeJsonConverter {
    private static final Map<String,Object> listConverterCache= new HashMap<>();
    @Override
    public Object convert(String json, Type type) {
        if(json==null){
            return null;
        }
        Object cache = listConverterCache.get(json);
        if(cache != null){
            return cache;
        }
        List<Object> convertedList = new ArrayList<>();
        if (type instanceof ParameterizedType pt){
            if(pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1] instanceof ParameterizedType pt2){
                Map<String, String> partlyMap = JsonOperationUtil.getKeysAndValuesMapWithJsonStr(json);
                for (String key : partlyMap.keySet()){
                    convertedList.add(GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt2.getRawType()).convert(partlyMap.get(key),pt2));
                }
            }else {
                ParameterizedType ptTemp = (ParameterizedType) type;
                Class<?> rawClazz = (Class<?>) ptTemp.getRawType();
                return GenericTypeConverterFactory.getGenericTypeJsonConverter(rawClazz).convert(json,ptTemp.getActualTypeArguments()[pt.getActualTypeArguments().length-1]);
            }
        }else{
            ArrayList<String> valueListToArr = JsonOperationUtil.getConvertJsonValueListToArr(json);
            if(valueListToArr.isEmpty()){
                if (json.equals("null")){
                    convertedList.add(null);
                } else if (JsonOperationUtil.jsonIsBlankMap(json)) {
                    convertedList.add(new HashMap<>());
                } else if (JsonOperationUtil.jsonIsBlankList(json)) {
                    convertedList.add(new ArrayList<>());
                }
            }else {
                if (type == Object.class){
                    for (String values: valueListToArr){
                        Type tempType = ObjectConverter.detectObjectType(values);
                        if (tempType instanceof ParameterizedType pt){
                            convertedList.add(GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt.getRawType()).convert(values,pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1]));
                        }else if (tempType==null) {
                            if (JsonOperationUtil.jsonIsBlankMap(values)){
                                convertedList.add(new HashMap<>());
                            } else if (JsonOperationUtil.jsonIsBlankList(values)) {
                                convertedList.add(new ArrayList<>());
                            }else {
                                convertedList.add(null);
                            }
                        }else {
                            convertedList.add(NonGenericTypeConverterFactory.getConverter((Class<?>) tempType).convert(values,(Class<?>) tempType,null));
                        }
                    }
                }else{
                    Class<?> tempClazz = (Class<?>) type;
                    for(String value : valueListToArr){
                        convertedList.add(NonGenericTypeConverterFactory.getConverter(tempClazz).convert(value,tempClazz,null));
                    }
                }
            }
        }
        listConverterCache.put(json,convertedList);
        return convertedList;
    }
}
