package com.fangxiong.jsonUtilsCore.parsers;

import com.fangxiong.globalAnnotations.TimeType;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateParser implements JsonParser{
    @Override
    public String parse(Object o, Field f) {
        if(o==null){
            return null;
        }
        Date tempDate = (Date) o;
        if(f!=null&&f.getAnnotation(TimeType.class)!=null){
            TimeType timeType = f.getDeclaredAnnotation(TimeType.class);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timeType.value());
            return "\""+dateTimeFormatter.format(tempDate.toInstant().atZone(ZoneId.systemDefault()))+"\"";
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return "\""+dateTimeFormatter.format(tempDate.toInstant().atZone(ZoneId.systemDefault()))+"\"";
    }
}
