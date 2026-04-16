package com.fangxiong.common;

import com.fangxiong.common.converters.LocalDateTimeJSONConverter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ConverterFactory {
    private static final Map<Class<?>, JSONConverter> converterMap = new HashMap<>();

    static {
        converterMap.put(LocalDateTime.class,new LocalDateTimeJSONConverter());
        converterMap.put(Integer.class,(s,f)-> Integer.parseInt(s));
        converterMap.put(Long.class,(s,f)-> Long.parseLong(s));
        converterMap.put(String.class,(s,f)-> s);
        converterMap.put(int.class, (s,f)-> Integer.parseInt(s));
        converterMap.put(long.class,(s,f)-> Long.parseLong(s));
    }

    public static JSONConverter getConverter(Class<?> clazzType){
        return converterMap.get(clazzType);
    }

}
