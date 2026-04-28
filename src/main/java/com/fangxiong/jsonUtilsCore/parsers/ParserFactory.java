package com.fangxiong.jsonUtilsCore.parsers;

import com.fangxiong.jsonUtilsCore.converters.JsonOperationFactory;

import java.time.LocalDateTime;
import java.util.*;

public class ParserFactory {
    private static final Map<Class<?>, JsonParser> parserMap = new HashMap<>();

    static{
        parserMap.put(String.class,(o,f)->{
            if(o==null || o.equals("null")) {
                return null;
            } else if (JsonOperationFactory.strIsNotBlank(o.toString())) {
                return "\""+ParserFactory.convertEscapeCharacterToStr(o.toString())+"\"";
            }else {
                return "\"\"";
            }
        });
        parserMap.put(int.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(long.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(Integer.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(Long.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(LocalDateTime.class,new LocalDateTimeParser());
        parserMap.put(HashMap.class,new MapParser());
        parserMap.put(Map.class,new MapParser());
        parserMap.put(Boolean.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(Double.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(Float.class,(o,f)->o==null ? null : o.toString());
        parserMap.put(ArrayList.class,new ListParser());
        parserMap.put(List.class,new ListParser());
        parserMap.put(Set.class,new SetParser());
        parserMap.put(HashSet.class,new SetParser());
    }

    private static JsonParser addParser(Class<?> clazz) {
        parserMap.put(clazz,new ObjectParser());
        return parserMap.get(clazz);
    }

    public static JsonParser getParser(Class<?> clazz){
        if (!parserMap.containsKey(clazz)){
            return addParser(clazz);
        }
        return parserMap.get(clazz);
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
            } else if (c == '\r') {
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
