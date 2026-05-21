package com.fangxiong.jsonUtilsCore.converters;

import com.fangxiong.jsonUtilsCore.coreUtil.JsonOperationUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NonGenericTypeConverterFactory {
    private static final Map<Class<?>, NonGenericTypeJsonConverter> converterMap = new HashMap<>();
    private static final Map<String,Object> normalTypesConverterCache = new HashMap<>();

    static {
        converterMap.put(LocalDateTime.class,new LocalDateTimeConverter());
        converterMap.put(Integer.class,(s,f,d)-> {
            if (s == null) {
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,Integer.class);
            return Integer.parseInt(s);
        });
        converterMap.put(Long.class,(s,f,d)->{
            if(s==null){
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,Long.class);
            return Long.parseLong(s);
        });
        converterMap.put(String.class,(s,f,d)-> s);
        converterMap.put(int.class, (s,f,d)->{
            if (s==null){
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,int.class);
            return Integer.parseInt(s);
        });
        converterMap.put(long.class,(s,f,d)-> {
            if(s==null){
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,long.class);
            return Long.parseLong(s);
        });
        converterMap.put(Boolean.class,(s,f,d)-> {
            if(s==null){
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,Boolean.class);
            if(s.equals("0")){
                return false;
            } else if (s.equals("1")) {
                return true;
            }
            return Boolean.parseBoolean(s);
        });
        converterMap.put(boolean.class,(s,f,d)-> {
            if(s==null){
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,Boolean.class);
            if(s.equals("0")||s.equals("false")){
                return false;
            } else{
                return true;
            }
        });
        converterMap.put(Double.class,(s,f,d)-> {
            if (s==null){
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,Double.class);
            return Double.parseDouble(s);
        });
        converterMap.put(Float.class,(s,f,d)-> {
            if(s==null){
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,Float.class);
            return Float.parseFloat(s);
        });
        converterMap.put(double.class,(s,f,d)-> {
            if (s == null) {
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,double.class);
            return Double.parseDouble(s);
        });
        converterMap.put(float.class,(s,f,d)-> {
            if (s == null) {
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s,float.class);
            return Float.parseFloat(s);
        });
        converterMap.put(BigDecimal.class,(s,f,d)->{
            if (s == null) {
                return null;
            }
            JsonOperationUtil.jsonValueValidationChecker(s, BigDecimal.class);
            return BigDecimal.valueOf(Double.parseDouble(s));
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
