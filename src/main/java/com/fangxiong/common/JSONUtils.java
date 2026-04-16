package com.fangxiong.common;

import com.fangxiong.common.parsers.ObjectParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONUtils {
    public static String toJSONStr(Object o) {
        Class<?> c = o.getClass();
        return decorateJSONStr(ParserFactory.getParser(c).parse(o, null));
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

    public static String convertEscapeCharacterToStr(String str){
        StringBuilder sb = new StringBuilder();
        char[] charArray = str.toCharArray();
        for(char c : charArray){
            if(c == '"'){
                sb.append("\\\"");
            } else if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '\b') {
                sb.append("\\").append("b");
            } else if (c == '\f') {
                sb.append("\\").append("f");
            } else if (c == '\n') {
                sb.append("\\").append("n");
            } else if (c == 'r') {
                sb.append("\\").append("r");
            } else if (c == '\t') {
                sb.append("\\").append("t");
            } else if (c <= 31) {
                sb.append(String.format("\\u%04X",(int)c));
            }else {
                sb.append(c);
            }
        }
        return  sb.toString();
    }

    public static String decorateJSONStr(String jsonStr){
        StringBuilder sb = new StringBuilder();
        int tempCount=0;
        char[] jsonStrCharArray = jsonStr.toCharArray();
        char previousChar = ' ';
        for(char c : jsonStrCharArray){
            if(c=='{' || c=='['){
                tempCount+=2;
                sb.append(c).append("\n").append(" ".repeat(tempCount));
            } else if (c == ':' && previousChar == '\"') {
                sb.append(c).append(" ");
            } else if (c == ',') {
                sb.append(c).append("\n").append(" ".repeat(tempCount));
            } else if (c == '}' || c==']') {
                tempCount-=2;
                sb.append("\n").append(" ".repeat(tempCount)).append(c);
            }else {
                sb.append(c);
            }
            previousChar = c;
        }
        return sb.toString();
    }
}
