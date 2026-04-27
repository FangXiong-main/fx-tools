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
        try {
            parse = LocalDateTime.parse(s);
        } catch (Exception e) {
            throw new JsonConvertFailureError("Convert '"+s+"'"+" to LocalDateTime failure,the format can't be identified",e);
        }
        return parse;
    }
}
