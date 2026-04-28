package com.fangxiong.jsonUtilsCore.parsers;

import java.lang.reflect.Field;
import java.util.Set;

public class SetParser implements JsonParser {
    @Override
    public String parse(Object o, Field f) {
        StringBuilder sb = new StringBuilder();
        Set<?> s = (Set<?>) o;
        if(s==null){
            sb.append("null");
            return sb.toString();
        }
        int totalCount = s.size();
        int tempCount = 0;
        sb.append("[");
        for(Object obj : s){
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
