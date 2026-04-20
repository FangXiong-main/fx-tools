package com.fangxiong.common.converters;

import com.fangxiong.annotations.TimeType;
import com.fangxiong.common.NonGenericTypeJsonConverter;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements NonGenericTypeJsonConverter {

    @Override
    public Object convert(String s, Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field f:declaredFields){
            if(f.getAnnotation(TimeType.class)!=null){
                TimeType timeType = f.getAnnotation(TimeType.class);
                return LocalDateTime.parse(s,DateTimeFormatter.ofPattern(timeType.value()));
            }
        }
        return LocalDateTime.parse(s);
    }
}
