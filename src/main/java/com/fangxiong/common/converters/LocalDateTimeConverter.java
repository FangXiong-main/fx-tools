package com.fangxiong.common.converters;

import com.fangxiong.annotations.TimeType;
import com.fangxiong.common.Converter;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements Converter {


    @Override
    public Object convert(String s) {
        return null;
    }

    public Object convertWithAnnotation(String s,Class<?> clazz){
        Field[] dc = clazz.getDeclaredFields();
        TimeType timeType = null;
        for (Field f : dc){
            timeType = f.getDeclaredAnnotation(TimeType.class);
            if(timeType!=null) {
                break;
            }
        }
        if (timeType != null){
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(timeType.value()));
        }
        return LocalDateTime.parse(s);
    }
}
