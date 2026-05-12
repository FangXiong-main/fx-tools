package com.fangxiong.jsonUtilsCore.parsers;

import com.fangxiong.globalUtils.GlobalConverterCacheLib;
import com.fangxiong.jsonUtilsCore.exceptions.JsonParserFailureError;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.fangxiong.constants.SystemConstants.GET;

public class ObjectParser implements JsonParser {
    private static final Map<Class<?>,Field[]> fieldCache = new HashMap<>();
    private static final Map<Class<?>,Map<String,Method>> methodCache = new HashMap<>();
    @Override
    public String parse(Object o, Field f) {
        StringBuilder sb = new StringBuilder();
        try {
            Class<?> clazz = o.getClass();
            Field[] df = fieldCache.get(clazz)!=null ? fieldCache.get(clazz) : getFieldCache(clazz);
            Map<Field,Method> methods = GlobalConverterCacheLib.getConverterGetMethodCache(clazz);
            int totalCount = df.length;
            int tempCount = 0;
            sb.append("{");
            for(Field fd : df){
                Method md = methods.get(fd);
                sb.append("\"").append(fd.getName()).append("\":");
                String parsed;
                if(md.invoke(o)!=null){
                    parsed = ParserFactory.getParser(fd.getType()).parse(md.invoke(o), fd);
                }else {
                    parsed = null;
                }
                sb.append(parsed);
                tempCount++;
                if(tempCount<totalCount){
                    sb.append(",");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sb.append("}");
        return sb.toString();
    }
    private static Field[] getFieldCache(Class<?> clazz) {
        Field[] df = clazz.getDeclaredFields();
        fieldCache.put(clazz, df);
        return df;
    }

}
