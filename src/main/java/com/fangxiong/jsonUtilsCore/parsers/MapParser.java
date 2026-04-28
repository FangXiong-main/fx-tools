package com.fangxiong.jsonUtilsCore.parsers;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class MapParser implements JsonParser {

    @Override
    public String parse(Object o, Field f) {
        StringBuilder sb = new StringBuilder();
        Map<?,?> map = (Map<?,?>) o;
        if(map==null){
            sb.append("null");
            return sb.toString();
        }
        int totalCount = map.size();
        int tempCount = 0;
        Set<?> keySet = map.keySet();
        sb.append("{");
        for (Object key : keySet) {
            if(key!=null){
                sb.append("\"");
                sb.append(key);
                sb.append("\":");
            }
            Object o1 = map.get(key);
            if (o1==null){
                sb.append("null");
            }else{
                String parsed = ParserFactory.getParser(o1.getClass()).parse(o1, null);
                sb.append(parsed);
            }
            tempCount++;
            if(tempCount<totalCount){
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
