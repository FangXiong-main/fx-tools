package com.fangxiong.common.converters;

import com.fangxiong.common.ConverterFactory;
import com.fangxiong.common.JSONConverter;
import com.fangxiong.common.ParserFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.fangxiong.redis.SystemConstants.SET;

public class ObjectConverter implements JSONConverter {

    @Override
    public Object convert(String s, Class<?> clazz) {
        StringBuilder sbMain = new StringBuilder();
        Map<String,String> tempPartMap = ConverterFactory.getSplitMainEntityAndFieldEntity(sbMain,s);
        Field[] df = clazz.getDeclaredFields();
        for (Field f : df){
            try {
                Method dm = clazz.getDeclaredMethod(SET + f.getName(), f.getType());

            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
