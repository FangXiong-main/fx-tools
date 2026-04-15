package com.fangxiong.common.parsers;

import com.fangxiong.common.JSONParser;
import com.fangxiong.common.ParserFactory;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Set;

public class MapParser implements JSONParser {

    @Override
    public String parse(Object o, Field f) {
        StringBuilder sb = new StringBuilder();
        Map<?,?> map = (Map<?,?>) o;
        int totalCount = map.size();
        int tempCount = 0;
        Set<?> keySet = map.keySet();
        if (f!=null){
            sb.append("  \"").append(f.getName()).append("\"  :  ");
        }
        sb.append("{");
        if (totalCount>0){
            sb.append("\n");
        }
        for (Object key : keySet) {
            sb.append("  \"");
            sb.append(key.toString()).append("\"  :  ");
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
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
