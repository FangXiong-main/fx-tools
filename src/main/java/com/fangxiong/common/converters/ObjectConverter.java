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
    private static final Deque<Map<Class<?>,Class<?>[]>> convertRawTypesDeque = new ArrayDeque<>();
    private static final ArrayList<Class<?>> convertActualTypes = new ArrayList<Class<?>>();
    private static final Map<Class<?>,Class<?>[]> tempTypeMap = new HashMap<>();

    @Override
    public Object convert(String s, Class<?> clazz) {
        StringBuilder sbMain = new StringBuilder();
        Map<String,String> tempPartMap = ConverterFactory.getSplitMainEntityAndFieldEntity(sbMain,s);
        Field[] df = clazz.getDeclaredFields();
        try {
            Object convertedObj = clazz.getDeclaredConstructor().newInstance();
            for (Field f : df) {
                Method setMd = clazz.getDeclaredMethod(SET + f.getName(), f.getType());
                if(f.getGenericType() instanceof ParameterizedType pt){
                    Type tempType = null;
                    while(true){
                        tempType = pt.getRawType();
                        if(pt.getActualTypeArguments().length==2&&(pt.getActualTypeArguments()[1] instanceof ParameterizedType tempPt)){
                            //tempTypeMap.put(tempType,)
                            pt = (ParameterizedType) tempPt.getActualTypeArguments()[1];
                        }else if(pt.getActualTypeArguments().length==1&&(pt.getActualTypeArguments()[0] instanceof ParameterizedType tempPt)){
                            pt = (ParameterizedType) tempPt.getActualTypeArguments()[0];
                        }else if(pt.getActualTypeArguments().length==2){
                            Class<?>[] tempClazz = {(Class<?>) pt.getActualTypeArguments()[1]};
                            tempTypeMap.put((Class<?>) pt.getRawType(),tempClazz);
                            convertRawTypesDeque.push(tempTypeMap);
                            tempTypeMap.clear();
                        }
                    }
                }else{
                    setMd.invoke(convertedObj,ConverterFactory.getConverter(f.getType()).convert(tempPartMap.get(f.getName()),f.getType()));
                }
            }
            return convertedObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
