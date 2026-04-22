package com.fangxiong.common.converters;

import com.fangxiong.common.GenericTypeConverterFactory;
import com.fangxiong.common.GenericTypeJsonConverter;
import com.fangxiong.common.NonGenericTypeConverterFactory;
import com.fangxiong.common.StrUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListConverter implements GenericTypeJsonConverter {
    @Override
    public Object convert(String json, Type type) {
        StringBuilder tempSb = new StringBuilder();
        List<Object> convertedList = new ArrayList<>();
        if (type instanceof ParameterizedType pt){
            if(pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1] instanceof ParameterizedType pt2){
                Map<String, String> partlyMap = StrUtils.getSplitMainJsonToPartlyMap(tempSb,json);
                for (String key : partlyMap.keySet()){
                    convertedList.add(GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt2.getRawType()).convert(partlyMap.get(key),pt2));
                }
            }else {
                ParameterizedType ptTemp = (ParameterizedType) type;
                Class<?> rawClazz = (Class<?>) ptTemp.getRawType();
                return GenericTypeConverterFactory.getGenericTypeJsonConverter(rawClazz).convert(json,ptTemp.getActualTypeArguments()[pt.getActualTypeArguments().length-1]);
            }
        }else{
            ArrayList<String> valueListToArr = StrUtils.getConvertJsonValueListToArr(json);
            if (type == Object.class){
                for (String values: valueListToArr){
                    Type tempType = ObjectConverter.detectObjectType(values);
                    if (tempType instanceof ParameterizedType pt){
                        convertedList.add(GenericTypeConverterFactory.getGenericTypeJsonConverter((Class<?>) pt.getRawType()).convert(values,pt.getActualTypeArguments()[pt.getActualTypeArguments().length-1]));
                    }else {
                        convertedList.add(NonGenericTypeConverterFactory.getConverter((Class<?>) tempType).convert(values,(Class<?>) tempType));
                    }
                }
            }else{
                Class<?> tempClazz = (Class<?>) type;
                for(String value : valueListToArr){
                    convertedList.add(NonGenericTypeConverterFactory.getConverter(tempClazz).convert(value,tempClazz));
                }
            }
        }
        return convertedList;
    }
}
