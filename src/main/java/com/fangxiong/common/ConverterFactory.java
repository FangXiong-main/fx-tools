package com.fangxiong.common;

import com.fangxiong.annotations.TimeType;
import com.fangxiong.common.converters.LocalDateTimeConverter;

import java.lang.reflect.Field;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ConverterFactory {
    private static final Map<Class<?>,Converter> converterMap = new HashMap<>();

    static {
        converterMap.put(LocalDateTime.class,new LocalDateTimeConverter());
        converterMap.put(Integer.class,Integer::valueOf);
        converterMap.put(Long.class,Long::valueOf);
        converterMap.put(String.class,jsonString -> jsonString);
        converterMap.put(int.class, Integer::parseInt);
        converterMap.put(long.class,Long::parseLong);
    }

    public static Converter getConverter(Class<?> clazzType){
        return converterMap.get(clazzType);
    }

}
