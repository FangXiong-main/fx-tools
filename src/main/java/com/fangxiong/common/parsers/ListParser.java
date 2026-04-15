package com.fangxiong.common.parsers;

import com.fangxiong.common.JSONParser;
import com.fangxiong.common.JSONUtils;
import com.fangxiong.common.ParserFactory;

import java.lang.reflect.Field;
import java.util.List;

public class ListParser implements JSONParser {
    @Override
    public String parse(Object o, Field f) {
        StringBuilder sb = new StringBuilder();
        List<?> l = (List<?>) o;
        int totalCount = l.size();
        int tempCount = 0;
        if(f!=null){
            sb.append("\"").append(f.getName()).append("\"  :  ");
        }
        sb.append("[");
        if(totalCount>0){
            sb.append("\n");
        }
        for(Object obj : l){
            sb.append("  ");
            if(obj == null){
                sb.append("null");
            }else{
                String parsed = ParserFactory.getParser(obj.getClass()).parse(obj, f);
                sb.append(parsed);
            }
            tempCount++;
            if(tempCount<totalCount){
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
