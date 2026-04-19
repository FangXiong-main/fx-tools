package com.fangxiong.common;

import com.fangxiong.common.converters.LocalDateTimeJSONConverter;
import com.fangxiong.common.converters.MapConverter;
import com.fangxiong.common.converters.ObjectConverter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConverterFactory {
    private static final Map<Class<?>, JSONConverter> converterMap = new HashMap<>();

    static {
        converterMap.put(LocalDateTime.class,new LocalDateTimeJSONConverter());
        converterMap.put(Integer.class,(s,f)-> Integer.parseInt(s));
        converterMap.put(Long.class,(s,f)-> Long.parseLong(s));
        converterMap.put(String.class,(s,f)-> "\""+s+"\"");
        converterMap.put(int.class, (s,f)-> Integer.parseInt(s));
        converterMap.put(long.class,(s,f)-> Long.parseLong(s));
        converterMap.put(Map.class,new MapConverter());
    }

    public static JSONConverter addConverter(Class<?> clazz) {
        converterMap.put(clazz,new ObjectConverter());
        return converterMap.get(clazz);
    }

    public static JSONConverter getConverter(Class<?> clazz){
        if(!converterMap.containsKey(clazz)){
            return addConverter(clazz);
        }
        return converterMap.get(clazz);
    }

    public static String getUndecoratedJSONStr(String json){
        return json.replaceAll("\\s+","");
    }

    public static Map<String,String> getSplitMainEntityAndFieldEntity(StringBuilder sbMain,String json){
        Map<String,String> tempPartMap = new LinkedHashMap<>();
        char[] ca = json.toCharArray();
        int tempPointer=1;int tempLeftPointer=0;int tempPartLeftPointer=0;boolean isNotFirst=false;
        int firstCharCounter=0;int noFieldNameInt=1;boolean isReadPart =false;
        StringBuilder sbPart = new StringBuilder();String tempFiledName = "";
        for(int i=0;i<ca.length;i++){
            if(isReadPart){
                if(ca[i] == '{' || ca[i] == '['){
                    tempPartLeftPointer++;
                    isNotFirst = true;
                }else if(tempPartLeftPointer==0 && firstCharCounter==0 && ca[i] == ':' ){
                    firstCharCounter++;
                } else if (firstCharCounter==1) {
                    tempFiledName = sbPart.substring(0,sbPart.length()-2);
                    sbPart.setLength(0);
                    sbPart.append(ca[i-1]);
                    firstCharCounter++;
                } else if(tempPartLeftPointer!=0 && (ca[i] == '}' || ca[i] == ']') ){
                    tempPartLeftPointer--;
                }else if(isNotFirst && tempPartLeftPointer==0){
                    tempFiledName = firstCharCounter==0 ? ""+noFieldNameInt : tempFiledName;
                    noFieldNameInt++;
                    sbMain.append(ca[i-1]);
                    tempPartMap.put(tempFiledName,sbPart.toString());
                    sbPart.setLength(0);
                    isReadPart = false;
                    isNotFirst = false;
                    tempLeftPointer--;
                    tempFiledName = "";
                    firstCharCounter=0;
                    i--;
                    continue;
                }
                sbPart.append(ca[i]);
            }else {
                if(ca[i] == ','){
                    tempPointer=i+1;
                    sbMain.append(ca[i]);
                } else if (tempLeftPointer!=2 &&(ca[i] == '{' || ca[i] == '[')) {
                    tempLeftPointer ++;
                    sbMain.append(ca[i]);
                } else if (ca[i] == '}' || ca[i] == ']') {
                    sbMain.append(ca[i]);
                } else if (tempLeftPointer>1) {
                    isReadPart = true;
                    i = tempPointer-1;
                }else {
                    sbMain.append(ca[i]);
                }
            }
        }
        return tempPartMap;
    }

}
