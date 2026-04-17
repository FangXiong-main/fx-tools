package com.fangxiong.common;

import com.fangxiong.common.converters.LocalDateTimeJSONConverter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConverterFactory {
    private static final Map<Class<?>, JSONConverter> converterMap = new HashMap<>();

    static {
        converterMap.put(LocalDateTime.class,new LocalDateTimeJSONConverter());
        converterMap.put(Integer.class,(s,f)-> Integer.parseInt(s));
        converterMap.put(Long.class,(s,f)-> Long.parseLong(s));
        converterMap.put(String.class,(s,f)-> s);
        converterMap.put(int.class, (s,f)-> Integer.parseInt(s));
        converterMap.put(long.class,(s,f)-> Long.parseLong(s));
    }

    public static JSONConverter getConverter(Class<?> clazzType){
        return converterMap.get(clazzType);
    }

    public static String getValueStrFromJSON(String json,String fieldName){
        Pattern pattern = Pattern.compile("\"" + "\"\\s*:\\s*\"(.*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
             //return ConverterFactory.getConverter(f.getType()).convert(matcher.group(1), f);
        }
        return null;
    }

    public static String getUndecoratedJSONStr(String json){
        return json.replaceAll("\\s+","");
    }

    public static Map<String,String> getSplitMainEntityAndFieldEntity(StringBuilder sbMain,String json){
        Map<String,String> tempPartMap = new HashMap<>();
        char[] ca = json.toCharArray();
        int tempPointer=0;int tempLeftPointer=0;int tempPartLeftPointer=0;boolean isNotFirst=false;
        int firstCharCounter=0;boolean isReadPart =false;
        StringBuilder sbPart = new StringBuilder();String tempFiledName = "";
        for(int i=0;i<ca.length;i++){
            if(isReadPart){
                if(ca[i] == '{' || ca[i] == '['){
                    tempPartLeftPointer++;
                    isNotFirst = true;
                }else if(firstCharCounter==0 && ca[i] == ':' ){
                    firstCharCounter++;
                } else if (firstCharCounter==1) {
                    tempFiledName = sbPart.substring(0,sbPart.length()-2); //"2":{
                    firstCharCounter++;
                } else if(tempPartLeftPointer!=0 && (ca[i] == '}' || ca[i] == ']') ){
                    tempPartLeftPointer--;
                }else if(isNotFirst && tempPartLeftPointer==0){
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
                } else if (ca[i] == '{' || ca[i] == '[') {
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
