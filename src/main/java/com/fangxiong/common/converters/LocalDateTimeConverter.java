package com.fangxiong.common.converters;

import com.fangxiong.annotations.TimeType;
import com.fangxiong.common.Converter;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter implements Converter {

    @Override
    public Object convert(String s, Field field) {
        TimeType timeType = field.getAnnotation(TimeType.class);
        return LocalDateTime.parse(s,DateTimeFormatter.ofPattern(timeType.value()));
    }
}
