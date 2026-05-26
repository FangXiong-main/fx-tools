package com.fangxiong.jsonUtilsCore.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericTypeConverterFactory {
    private static final Map<Class<?>,GenericTypeJsonConverter> genericTypeConverter = new HashMap<>();

    static{
        genericTypeConverter.put(Map.class,new MapConverter());
        genericTypeConverter.put(List.class,new ListConverter());
        genericTypeConverter.put(Set.class,new SetConverter());
    }

    public static GenericTypeJsonConverter getGenericTypeJsonConverter(Class<?> clazz){
        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> i : interfaces){
            if(genericTypeConverter.containsKey(i)){
                return genericTypeConverter.get(i);
            }
        }
        return genericTypeConverter.get(clazz);
    }
}
