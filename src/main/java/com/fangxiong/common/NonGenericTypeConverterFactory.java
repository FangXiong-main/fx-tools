package com.fangxiong.common;

import com.fangxiong.common.converters.ListConverter;
import com.fangxiong.common.converters.LocalDateTimeConverter;
import com.fangxiong.common.converters.ObjectConverter;
import com.fangxiong.common.parsers.ListParser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NonGenericTypeConverterFactory {
    private static final Map<Class<?>, NonGenericTypeJsonConverter> converterMap = new HashMap<>();

    static {
        converterMap.put(LocalDateTime.class,new LocalDateTimeConverter());
        converterMap.put(Integer.class,(s,f)-> Integer.parseInt(s));
        converterMap.put(Long.class,(s,f)-> Long.parseLong(s));
        converterMap.put(String.class,(s,f)-> s);
        converterMap.put(int.class, (s,f)-> Integer.parseInt(s));
        converterMap.put(long.class,(s,f)-> Long.parseLong(s));
        converterMap.put(Boolean.class,(s,f)-> Boolean.parseBoolean(s));
        converterMap.put(Double.class,(s,f)-> Double.parseDouble(s));
        converterMap.put(Float.class,(s,f)-> Float.parseFloat(s));
        converterMap.put(double.class,(s,f)-> Double.parseDouble(s));
        converterMap.put(float.class,(s,f)-> Float.parseFloat(s));
    }

    private static NonGenericTypeJsonConverter addConverter(Class<?> clazz) {
        converterMap.put(clazz,new ObjectConverter());
        return converterMap.get(clazz);
    }

    public static NonGenericTypeJsonConverter getConverter(Class<?> clazz){
        if(!converterMap.containsKey(clazz)){
            return addConverter(clazz);
        }
        return converterMap.get(clazz);
    }

    public static String getUndecoratedJSONStr(String json){
        return json.replaceAll("\\s+","");
    }



    public static String concatMapValuesToStr(Map<String,String> map){
        StringBuilder sb =new StringBuilder();
        Collection<String> values = map.values();
        int tempCount=0;int totalCount = values.size();
        for (String s:values){
            sb.append(s);
            tempCount++;
            if (tempCount<totalCount){
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
