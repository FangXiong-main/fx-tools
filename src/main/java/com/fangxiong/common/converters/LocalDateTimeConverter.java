package com.fangxiong.common.converters;

import com.fangxiong.annotations.TimeType;
import com.fangxiong.common.NonGenericTypeJsonConverter;
import com.fangxiong.common.exceptions.JsonConvertFailureError;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements NonGenericTypeJsonConverter {

    @Override
    public Object convert(String s, Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        if (s==null){
            return null;
        }
        for (Field f:declaredFields){
            if(f.getAnnotation(TimeType.class)!=null){
                TimeType timeType = f.getAnnotation(TimeType.class);
                return LocalDateTime.parse(s,DateTimeFormatter.ofPattern(timeType.value()));
            }
        }
        LocalDateTime parse = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        try {
            parse = LocalDateTime.parse(s,formatter);
        } catch (Exception e) {
            throw new JsonConvertFailureError("Convert '"+s+"'"+" to LocalDateTime failure,the format can't be identified or parsed with default patter:yyyy-MM-dd HH:mm:ss",e);
        }
        return parse;
    }
}
