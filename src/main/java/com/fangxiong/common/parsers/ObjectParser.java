package com.fangxiong.common.parsers;

import com.fangxiong.common.CustomizeClazzDetector;
import com.fangxiong.common.JSONParser;
import com.fangxiong.common.ParserFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ObjectParser implements JSONParser {

    @Override
    public String parse(Object o, Field f) {
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = o.getClass();
        Field[] df = clazz.getDeclaredFields();
        int totalCount = df.length;
        int tempCount = 0;
        sb.append("{");
        try {
            for(Field fd : df){
                Method dm = clazz.getDeclaredMethod("get" + Character.toUpperCase(fd.getName().charAt(0)) + fd.getName().substring(1));
                sb.append("\"").append(fd.getName()).append("\":");
                String parsed = ParserFactory.getParser(fd.getType()).parse(dm.invoke(o), fd);
                sb.append(parsed);
                tempCount++;
                if(tempCount<totalCount){
                    sb.append(",");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sb.append("}");
        return sb.toString();
    }


}
