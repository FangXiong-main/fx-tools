package com.fangxiong.mysqlUtilsCore.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class MysqlNonGenericConverterFactory {
    private static final Map<Class<?>,MysqlNonGenericConverter> mysqlNonGenericConverterMap = new HashMap<>();
    static{
        mysqlNonGenericConverterMap.put(String.class,(r,c,n)-> r.getString(n));
        mysqlNonGenericConverterMap.put(int.class,(r,c,n)-> r.getInt(n));
        mysqlNonGenericConverterMap.put(Integer.class,(r,c,n)-> r.getInt(n));
        mysqlNonGenericConverterMap.put(BigInteger.class,(r,c,n)-> r.getInt(n));
        mysqlNonGenericConverterMap.put(Float.class,(r,c,n)-> r.getFloat(n));
        mysqlNonGenericConverterMap.put(Double.class,(r,c,n)-> r.getDouble(n));
        mysqlNonGenericConverterMap.put(BigDecimal.class,(r, c, n)-> r.getBigDecimal(n));
        mysqlNonGenericConverterMap.put(char.class,(r, c, n)-> r.getString(n));
    }

    private static MysqlNonGenericConverter addConverter(Class<?> clazz){
        mysqlNonGenericConverterMap.put(clazz, new MysqlObjectConverter());
        return new MysqlObjectConverter();
    }

    public static MysqlNonGenericConverter getConverter(Class<?> clazz){
        MysqlNonGenericConverter mysqlNonGenericConverter = mysqlNonGenericConverterMap.get(clazz);
        if (mysqlNonGenericConverter == null){
            return addConverter(clazz);
        }
        return mysqlNonGenericConverter;
    }
}