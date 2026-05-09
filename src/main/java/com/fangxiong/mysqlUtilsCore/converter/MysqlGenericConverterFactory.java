package com.fangxiong.mysqlUtilsCore.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlGenericConverterFactory {
    private static final Map<Class<?>, MysqlNonGenericConverter> mysqlGenericConverterMap = new HashMap<>();

    static{
        mysqlGenericConverterMap.put(Map.class,null);
        mysqlGenericConverterMap.put(List.class,null);
    }

    private static MysqlNonGenericConverter addConverter(Class<?> clazz){
        MysqlNonGenericConverter mysqlNonGenericConverter = new MysqlObjectNonGenericConverter();
        mysqlGenericConverterMap.put(clazz, mysqlNonGenericConverter);
        return mysqlNonGenericConverter;
    }

    public static MysqlNonGenericConverter getConverter(Class<?> clazz){
        MysqlNonGenericConverter mysqlNonGenericConverter = mysqlGenericConverterMap.get(clazz);
        if(mysqlNonGenericConverter == null){
            return addConverter(clazz);
        }
        return mysqlNonGenericConverter;
    }
}
