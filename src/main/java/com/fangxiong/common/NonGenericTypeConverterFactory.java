package com.fangxiong.common;

import com.fangxiong.common.converters.LocalDateTimeConverter;
import com.fangxiong.common.converters.ObjectConverter;
import com.fangxiong.utils.json.StrUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NonGenericTypeConverterFactory {
    private static final Map<Class<?>, NonGenericTypeJsonConverter> converterMap = new HashMap<>();

    static {
        converterMap.put(LocalDateTime.class,new LocalDateTimeConverter());
        converterMap.put(Integer.class,(s,f)-> {
            if (s == null) {
                return null;
            }
            StrUtils.jsonValueValidationChecker(s,Integer.class);
            return Integer.parseInt(s);
        });
        converterMap.put(Long.class,(s,f)->{
            if(s==null){
                return null;
            }
            StrUtils.jsonValueValidationChecker(s,Long.class);
            return Long.parseLong(s);
        });
        converterMap.put(String.class,(s,f)-> s);
        converterMap.put(int.class, (s,f)->{
            if (s==null){
                return null;
            }
            StrUtils.jsonValueValidationChecker(s,int.class);
            return Integer.parseInt(s);
        });
        converterMap.put(long.class,(s,f)-> {
            if(s==null){
                return null;
            }
            StrUtils.jsonValueValidationChecker(s,long.class);
            return Long.parseLong(s);
        });
        converterMap.put(Boolean.class,(s,f)-> {
            if(s==null){
                return null;
            }
            StrUtils.jsonValueValidationChecker(s,Boolean.class);
            return Boolean.parseBoolean(s);
        });
        converterMap.put(Double.class,(s,f)-> {
            if (s==null){
                return null;
            }
            StrUtils.jsonValueValidationChecker(s,Double.class);
            return Double.parseDouble(s);
        });
        converterMap.put(Float.class,(s,f)-> {
            if(s==null){
                return null;
            }
            StrUtils.jsonValueValidationChecker(s,Float.class);
            return Float.parseFloat(s);
        });
        converterMap.put(double.class,(s,f)-> {
            if (s == null) {
                return null;
            }
            StrUtils.jsonValueValidationChecker(s,double.class);
            return Double.parseDouble(s);
        });
        converterMap.put(float.class,(s,f)-> {
            if (s == null) {
                return null;
            }
            StrUtils.jsonValueValidationChecker(s,float.class);
            return Float.parseFloat(s);
        });
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
