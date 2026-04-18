package com.fangxiong.common.parsers;

import com.fangxiong.annotations.TimeType;
import com.fangxiong.common.JSONParser;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeParser implements JSONParser {
    @Override
    public String parse(Object o, Field f) {
        if(f!=null){
            TimeType timeType = f.getDeclaredAnnotation(TimeType.class);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timeType.value());
            return "\""+dateTimeFormatter.format((LocalDateTime)o)+"\"";
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "\""+dateTimeFormatter.format((LocalDateTime)o)+"\"";
    }
}
