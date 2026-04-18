package com.fangxiong.common.converters;

import com.fangxiong.common.ConverterFactory;
import com.fangxiong.common.JSONConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapConverter implements JSONConverter {

    private static final Pattern getKeyPattern = Pattern.compile("\"(.*?)\":.+,?}");
    private static final ArrayList<String> mapKeys = new ArrayList<>();

    @Override
    public Object convert(String s, Class<?> clazz) {
        getMapKeys(s);
        Map<String,Object> map = new HashMap<>();
        for(String mapKey : mapKeys) {
            Matcher matcher = getKeyPattern.matcher(s);
            if (matcher.find()){
                //map.put(mapKey,ConverterFactory.getConverter());
            }
        }
        return null;
    }

    private static void getMapKeys(String json) {
        Matcher matcher = getKeyPattern.matcher(json);
        while (matcher.find()) {
            mapKeys.add(matcher.group(1));
        }
    }
}
