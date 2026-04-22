package com.fangxiong.common.parsers;

import com.fangxiong.common.CustomizeClazzDetector;
import com.fangxiong.common.JSONParser;
import com.fangxiong.common.JSONUtils;
import com.fangxiong.common.ParserFactory;
import com.fangxiong.common.converters.ObjectConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public class ListParser implements JSONParser {


    @Override
    public String parse(Object o, Field f) {
        StringBuilder sb = new StringBuilder();
        List<?> l = (List<?>) o;
        if(l==null){
            sb.append("null");
            return sb.toString();
        }
        int totalCount = l.size();
        int tempCount = 0;
        sb.append("[");
        for(Object obj : l){
            if(obj == null){
                sb.append("null");
            }else{
                sb.append(ParserFactory.getParser(obj.getClass()).parse(obj, f));
            }
            tempCount++;
            if(tempCount<totalCount){
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
