package com.fangxiong.jsonUtilsCore.parsers;

import com.fangxiong.globalAnnotations.TimeType;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateParser implements JsonParser {
    @Override
    public String parse(Object o, Field f) {
        if(o==null){
            return null;
        }
        if(f!=null&&f.getAnnotation(TimeType.class)!=null){
            TimeType timeType = f.getDeclaredAnnotation(TimeType.class);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timeType.value());
            return "\""+dateTimeFormatter.format((LocalDate)o)+"\"";
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "\""+dateTimeFormatter.format((LocalDate)o)+"\"";
    }
}
