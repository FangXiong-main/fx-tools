package com.fangxiong.common;

import com.fangxiong.annotations.TimeType;
import org.springframework.boot.json.JacksonJsonParser;

import java.lang.invoke.CallSite;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONUtils {
    public static String toJSONString(Object o) {
        Class<?> c = o.getClass();
        Field[] df = c.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        int tempCounter = 0;
        try {
            sb.append("{").append("\n");
            for (Field f : df) {
                f.setAccessible(true);
                TimeType annotation = f.getAnnotation(TimeType.class);
                sb.append("  ").append("\"").append(f.getName()).append("\" : ").append("\"");
                if (annotation!=null){
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(annotation.value());
                    sb.append(dateTimeFormatter.format((LocalDateTime)f.get(o)));
                }else{
                    sb.append(f.get(o).toString());
                }
                sb.append("\"");
                tempCounter++;
                if (df.length > 1 && tempCounter!=df.length) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    public static <T> T jsonToBean(String jsonString, Class<T> clazz) {
        T t;
        Object converted = null;String valueString;
        Field[] df = clazz.getDeclaredFields();
        try {
            Constructor<T> dc = clazz.getDeclaredConstructor();
            t = dc.newInstance();
            for (Field f : df){
                Pattern pattern = Pattern.compile("\"" + f.getName() + "\"\\s*:\\s*\"(.*?)\"");
                Matcher matcher = pattern.matcher(jsonString);
                if (matcher.find()) {
                    valueString = matcher.group(1);
                   if (f.getType()!=LocalDateTime.class){
                       converted = ConverterFactory.getConverter(f.getType()).convert(valueString);
                   }else{
                       converted = LocalDateTime.parse(valueString,DateTimeFormatter.ofPattern(f.getAnnotation(TimeType.class).value()));
                   }
                }
                f.setAccessible(true);
                if(converted!=null){
                    f.set(t,converted);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return t;
    }
}
