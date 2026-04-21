package com.fangxiong.common;

import com.fangxiong.common.converters.ListConverter;
import com.fangxiong.common.converters.MapConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericTypeConverterFactory {
    private static final Map<Class<?>,GenericTypeJsonConverter> genericTypeConverter = new HashMap<>();

    static{
        genericTypeConverter.put(Map.class,new MapConverter());
        genericTypeConverter.put(List.class,new ListConverter());
    }

    public static GenericTypeJsonConverter getGenericTypeJsonConverter(Class<?> clazz){
        return genericTypeConverter.get(clazz);
    }

}
