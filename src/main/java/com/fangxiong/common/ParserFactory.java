package com.fangxiong.common;

import com.fangxiong.common.parsers.LocalDateTimeParser;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ParserFactory {
    private static final Map<Class<?>, JSONParser> parserMap = new HashMap<>();

    static{
        parserMap.put(String.class,(o,f)->o.toString());
        parserMap.put(int.class,(o,f)->o.toString());
        parserMap.put(long.class,(o,f)->o.toString());
        parserMap.put(Integer.class,(o,f)->o.toString());
        parserMap.put(Long.class,(o,f)->o.toString());
        parserMap.put(LocalDateTime.class,new LocalDateTimeParser());
    }

    public static JSONParser getParser(Class<?> clazz){
        return parserMap.get(clazz);
    }
}
