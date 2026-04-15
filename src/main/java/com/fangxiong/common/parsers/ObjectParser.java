package com.fangxiong.common.parsers;

import com.fangxiong.common.JSONParser;
import com.fangxiong.common.ParserFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ObjectParser implements JSONParser {

    @Override
    public String parse(Object o, Field f) {
        StringBuilder sb = new StringBuilder();
        Field[] df = o.getClass().getDeclaredFields();
        sb.append("{\n");
        try {
            for(Field fd : df){
                fd.setAccessible(true);
                ParserFactory.getParser(fd.getType()).parse(fd.get(o), fd);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
