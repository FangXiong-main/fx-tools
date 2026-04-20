package com.fangxiong.common;

import com.fangxiong.common.converters.MapConverter;

import java.util.HashMap;
import java.util.Map;

public class GenericTypeConverterFactory {
    private static final Map<Class<?>,GenericTypeJsonConverter> genericTypeConverter = new HashMap<>();

    static{
        genericTypeConverter.put(Map.class,new MapConverter());
    }

    public static GenericTypeJsonConverter getGenericTypeJsonConverter(Class<?> clazz){
        return genericTypeConverter.get(clazz);
    }

}
