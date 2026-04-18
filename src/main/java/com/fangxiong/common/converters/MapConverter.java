package com.fangxiong.common.converters;

import com.fangxiong.common.ConverterFactory;
import com.fangxiong.common.JSONConverter;
import com.fangxiong.common.parsers.ObjectParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapConverter implements JSONConverter {

    private static final Pattern getKeyPattern = Pattern.compile("\"(.*)\":.+,?");
    private static final Pattern getValuePattern = Pattern.compile(":\"?(.*)\"?,?");
    private static final Pattern isIntegerPattern = Pattern.compile("-?(\\d+)");
    private static final Pattern isDicimalPattern = Pattern.compile("-?(\\d+\\.\\d+)");
    private static final ArrayList<String> mapKeys = new ArrayList<>();

    @Override
    public Object convert(String s, Class<?> clazz) {
        Map<String,Object> map = new HashMap<>();
        StringBuilder sbMain = new StringBuilder();
        if(clazz==null){
            Type rawType = ObjectConverter.pollConvertRawTypesDeque();
            Type[] actualTypes = ObjectConverter.pollConvertActualTypesDeque();
            if(actualTypes[1] instanceof ParameterizedType pt){
                Map<String, String> splitMainEntityAndFieldEntity = ConverterFactory.getSplitMainEntityAndFieldEntity(sbMain, s);
                Collection<String> values = splitMainEntityAndFieldEntity.values();
                ConverterFactory.getConverter((Class<?>) pt.getRawType()).convert(values.toString(),null);
            }else{
                ConverterFactory.getConverter((Class<?>) rawType).convert(s,(Class<?>) actualTypes[1]);
            }
        }else if (clazz == Map.class){
            getMapKeys(s);
            Matcher matcher = getKeyPattern.matcher(s);
            for (String key:mapKeys){
                if(matcher.find()){
                    map.put(key,getObjectValue(matcher.group(1)));
                }
            }
            mapKeys.clear();
        }else{
            getMapKeys(s);
            Matcher matcher = getKeyPattern.matcher(s);
            for (String key:mapKeys){
                if(matcher.find()){
                    map.put(key,ConverterFactory.getConverter(clazz).convert(matcher.group(1),clazz));
                }
            }
            mapKeys.clear();
        }
        return map;
    }

    private static void getMapKeys(String json) {
        Matcher matcher = getKeyPattern.matcher(json);
        while (matcher.find()) {
            mapKeys.add(matcher.group(1));
        }
    }

    private static Object getObjectValue(String value){
        Matcher matcher = isIntegerPattern.matcher(value);
        if (matcher.find()){
            return Integer.parseInt(matcher.group(1));
        }
        Matcher matcher2 = isDicimalPattern.matcher(value);
        if (matcher2.find()){
            return Double.parseDouble(matcher2.group(1));
        }
        return value;
    }
}
