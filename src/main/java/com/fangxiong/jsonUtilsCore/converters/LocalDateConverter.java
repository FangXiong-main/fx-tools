package com.fangxiong.jsonUtilsCore.converters;

import com.fangxiong.globalAnnotations.TimeType;
import com.fangxiong.jsonUtilsCore.exceptions.JsonConvertFailureError;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements NonGenericTypeJsonConverter{
    @Override
    public Object convert(String s, Class<?> clazz, Field field) {
        if (s==null){
            return null;
        }
        if(field.getAnnotation(TimeType.class)!=null){
            TimeType timeType = field.getAnnotation(TimeType.class);
            try {
                return LocalDate.parse(s,DateTimeFormatter.ofPattern(timeType.value()));
            } catch (Exception e) {
                throw new JsonConvertFailureError("Convert '"+s+"'"+" to LocalDate failure,the format can't be identified or parsed with pattern : "+timeType.value(),e);
            }
        }
        LocalDate parsed;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            parsed = LocalDate.parse(s,formatter);
        } catch (Exception e) {
            throw new JsonConvertFailureError("Convert '"+s+"'"+" to LocalDate failure,the format can't be identified or parsed with default pattern :yyyy-MM-dd'T'HH:mm:ss",e);
        }
        return parsed;
    }
}
