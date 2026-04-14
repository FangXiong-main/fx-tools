package com.fangxiong.common;

import com.fangxiong.annotations.TimeType;
import com.fangxiong.common.converters.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ConverterFactory {
    private static final Map<Class<?>,Converter> converterMap = new HashMap<>();
    private static TimeType timeType;
    static {
        converterMap.put(LocalDateTimeConverter.class,jsonString -> LocalDateTime.parse(jsonString, DateTimeFormatter.ofPattern(timeType.value())));
    }

    private static <A> A getAnnotation(Class<?> clazz){
        A a=null;
        return a;
    }

    public static <T> Converter getConverter(Class<T> clazzType){
        T annotation = getAnnotation(clazzType);
        TimeType timeTypeStringValue = clazzType.getAnnotation(TimeType.class);
        if (timeTypeStringValue!=null){
            timeType=timeTypeStringValue;
        }
        return converterMap.get(clazzType);
    }
}
