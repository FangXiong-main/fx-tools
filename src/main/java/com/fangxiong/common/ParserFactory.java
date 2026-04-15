package com.fangxiong.common;

import com.fangxiong.common.parsers.ListParser;
import com.fangxiong.common.parsers.LocalDateTimeParser;
import com.fangxiong.common.parsers.MapParser;
import com.fangxiong.common.parsers.ObjectParser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserFactory {
    private static final Map<Class<?>, JSONParser> parserMap = new HashMap<>();

    static{
        parserMap.put(String.class,(o,f)->o==null ? null : "\""+ o +"\"");
        parserMap.put(int.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(long.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(Integer.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(Long.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(LocalDateTime.class,new LocalDateTimeParser());
        parserMap.put(HashMap.class,new MapParser());
        parserMap.put(Map.class,new MapParser());
        parserMap.put(Boolean.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(Double.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(Float.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(ArrayList.class,new ListParser());
        parserMap.put(List.class,new ListParser());
    }

    private static JSONParser addParser(Class<?> clazz) {
        parserMap.put(clazz,new ObjectParser());
        return parserMap.get(clazz);
    }

    public static JSONParser getParser(Class<?> clazz){
        JSONParser jsonParser = parserMap.get(clazz);
        if(jsonParser==null){
            jsonParser = addParser(clazz);
        }
        return jsonParser;
    }
}
