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
        converterMap.put(Integer.class,(s,f)-> Integer.parseInt(s));
        converterMap.put(Long.class,(s,f)-> Long.parseLong(s));
        converterMap.put(String.class,(s,f)-> s);
        converterMap.put(int.class, (s,f)-> Integer.parseInt(s));
        converterMap.put(long.class,(s,f)-> Long.parseLong(s));
    }

    public static Converter getConverter(Class<?> clazzType){
        return converterMap.get(clazzType);
    }

}
