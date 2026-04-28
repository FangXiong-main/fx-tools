package com.fangxiong.jsonUtilsCore.parsers;

import com.fangxiong.jsonUtilsCore.annotations.TimeType;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeParser implements JsonParser {
    @Override
    public String parse(Object o, Field f) {
        if(o==null){
            return null;
        }
        if(f!=null&&f.getAnnotation(TimeType.class)!=null){
            TimeType timeType = f.getDeclaredAnnotation(TimeType.class);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timeType.value());
            return "\""+dateTimeFormatter.format((LocalDateTime)o)+"\"";
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return "\""+dateTimeFormatter.format((LocalDateTime)o)+"\"";
    }
}
