package com.fangxiong.common.converters;

import com.fangxiong.common.ConverterFactory;
import com.fangxiong.common.JSONConverter;
import com.fangxiong.common.ParserFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.fangxiong.redis.SystemConstants.SET;

public class ObjectConverter implements JSONConverter {
    private static final Deque<Type> convertRawTypesDeque = new ArrayDeque<>();
    private static final Deque<Type[]> convertActualTypesDeque = new ArrayDeque<>();

    @Override
    public Object convert(String s, Class<?> clazz) {
        StringBuilder sbMain = new StringBuilder();
        Map<String,String> tempPartMap = ConverterFactory.getSplitMainEntityAndFieldEntity(sbMain,s);
        Field[] df = clazz.getDeclaredFields();Class<?> rawType;
        try {
            Object convertedObj = clazz.getDeclaredConstructor().newInstance();
            for (Field f : df) {
                Method setMd = clazz.getDeclaredMethod(SET + f.getName(), f.getType());
                if(f.getGenericType() instanceof ParameterizedType pt){
                    rawType = (Class<?>) pt.getRawType();
                    while(true){
                        Type[] actualTypeArguments = pt.getActualTypeArguments();
                        if(actualTypeArguments.length==2){
                            convertRawTypesDeque.add(pt.getRawType());
                            convertActualTypesDeque.add(pt.getActualTypeArguments());
                            if(!(actualTypeArguments[1] instanceof ParameterizedType)){
                                break;
                            }else {
                                pt = (ParameterizedType) pt.getActualTypeArguments()[1];
                            }
                        }else{
                            convertRawTypesDeque.add(pt.getRawType());
                            convertActualTypesDeque.add(pt.getActualTypeArguments());
                            if(!(actualTypeArguments[0] instanceof ParameterizedType)){
                                break;
                            }else {
                                pt = (ParameterizedType) pt.getActualTypeArguments()[0];
                            }
                        }
                    }
                    ConverterFactory.getConverter(rawType).convert(tempPartMap.get("\""+f.getName()+"\""),null);
                }else{
                    setMd.invoke(convertedObj,ConverterFactory.getConverter(f.getType()).convert(tempPartMap.get(f.getName()),f.getType()));
                }
                convertRawTypesDeque.clear();
                convertActualTypesDeque.clear();
            }
            return convertedObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Type pollConvertRawTypesDeque(){
        return convertRawTypesDeque.poll();
    }

    public static Type[] pollConvertActualTypesDeque(){
        return convertActualTypesDeque.poll();
    }

}
