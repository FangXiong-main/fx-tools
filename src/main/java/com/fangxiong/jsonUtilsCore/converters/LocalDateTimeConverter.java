package com.fangxiong.jsonUtilsCore.converters;

import com.fangxiong.jsonUtilsCore.annotations.TimeType;
import com.fangxiong.jsonUtilsCore.exceptions.JsonConvertFailureError;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements NonGenericTypeJsonConverter {

    @Override
    public Object convert(String s, Class<?> clazz,Field field) {
        Field[] declaredFields = clazz.getDeclaredFields();
        if (s==null){
            return null;
        }
        if(field.getAnnotation(TimeType.class)!=null){
            TimeType timeType = field.getAnnotation(TimeType.class);
            try {
                return LocalDateTime.parse(s,DateTimeFormatter.ofPattern(timeType.value()));
            } catch (Exception e) {
                throw new JsonConvertFailureError("Convert '"+s+"'"+" to LocalDateTime failure,the format can't be identified or parsed with pattern : "+timeType.value(),e);
            }
        }
        LocalDateTime parse = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        try {
            parse = LocalDateTime.parse(s,formatter);
        } catch (Exception e) {
            throw new JsonConvertFailureError("Convert '"+s+"'"+" to LocalDateTime failure,the format can't be identified or parsed with default pattern :yyyy-MM-dd'T'HH:mm:ss",e);
        }
        return parse;
    }
}
