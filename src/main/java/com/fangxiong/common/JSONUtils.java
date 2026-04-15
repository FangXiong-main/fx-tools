package com.fangxiong.common;

import com.fangxiong.common.parsers.ObjectParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONUtils {
    public static String toJSONStr(Object o) {
        Class<?> c = o.getClass();
        StringBuilder sb = new StringBuilder();
        if(CustomizeClazzDetector.isCustomizeClazz(c)){
            sb.append(ParserFactory.getParser(c).parse(o, null));
        }else if(CustomizeClazzDetector.isDigitalClazz(c)){
            sb.append("{\n").append("  \"").append(ParserFactory.getParser(c).parse(o,null)).append("\"\n}");
        }else{
            sb.append(ParserFactory.getParser(c).parse(o, null));
        }
        return sb.toString();
    }
    public static <T> T jsonToBean(String jsonString, Class<T> clazz) {
        T t;
        Object converted = null;
        Field[] df = clazz.getDeclaredFields();
        try {
            Constructor<T> dc = clazz.getDeclaredConstructor();
            t = dc.newInstance();
            for (Field f : df){
                Pattern pattern = Pattern.compile("\"" + f.getName() + "\"\\s*:\\s*\"(.*)\"");
                Matcher matcher = pattern.matcher(jsonString);
                if (matcher.find()) {
                    converted = ConverterFactory.getConverter(f.getType()).convert(matcher.group(1), f);
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
