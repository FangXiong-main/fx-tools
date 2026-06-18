package com.fangxiong.jsonUtilsCore.converters;

import com.fangxiong.globalAnnotations.TimeType;
import com.fangxiong.jsonUtilsCore.exceptions.JsonConvertFailureError;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class DateConverter implements NonGenericTypeJsonConverter{
    @Override
    public Object convert(String s, Class<?> clazz, Field field) {
        if (s == null){
            return null;
        }
        if(field.getAnnotation(TimeType.class)!=null){
            TimeType timeType = field.getAnnotation(TimeType.class);
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeType.value());
                return simpleDateFormat.parse(s);
            } catch (Exception e) {
                throw new JsonConvertFailureError("Convert '"+s+"'"+" to LocalDate failure,the format can't be identified or parsed with pattern : "+timeType.value(),e);
            }
        }
        Date parsed;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            parsed = simpleDateFormat.parse(s);
        } catch (Exception e) {
            throw new JsonConvertFailureError("Convert '"+s+"'"+" to LocalDate failure,the format can't be identified or parsed with default pattern :yyyy-MM-dd'T'HH:mm:ss",e);
        }
        return parsed;
    }
}
