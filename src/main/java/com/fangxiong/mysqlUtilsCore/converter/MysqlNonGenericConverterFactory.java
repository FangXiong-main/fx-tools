package com.fangxiong.mysqlUtilsCore.converter;

import com.fangxiong.mysqlUtilsCore.enums.EnableCamelCaseToUnderscore;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class MysqlNonGenericConverterFactory {
    private static EnableCamelCaseToUnderscore camelCaseToUnderscoreEnum = EnableCamelCaseToUnderscore.ENABLE;
    private static final Map<Class<?>,MysqlNonGenericConverter> mysqlNonGenericConverterMap = new HashMap<>();
    static{
        mysqlNonGenericConverterMap.put(String.class,(r,c,n)-> {
            if(n==null){
                return r.getString(1);
            }
            return r.getString(n);
        });
        mysqlNonGenericConverterMap.put(int.class,(r,c,n)-> {
            if(n == null){
                return r.getInt(1);
            }
            return r.getInt(n);
        });
        mysqlNonGenericConverterMap.put(Integer.class,(r,c,n)-> {
            if (n == null) {
                return r.getInt(1);
            }
            return r.getInt(n);
        });
        mysqlNonGenericConverterMap.put(BigInteger.class,(r,c,n)-> {
            if (n == null){
                return r.getInt(1);
            }
            return r.getInt(n);
        });
        mysqlNonGenericConverterMap.put(Float.class,(r,c,n)-> {
            if(n== null){
                return r.getFloat(1);
            }
            return r.getFloat(n);
        });
        mysqlNonGenericConverterMap.put(Double.class,(r,c,n)-> {
            if(n == null){
                return r.getDouble(1);
            }
            return r.getDouble(n);
        });
        mysqlNonGenericConverterMap.put(BigDecimal.class,(r, c, n)-> {
            if(n == null){
                return r.getBigDecimal(1);
            }
            return r.getBigDecimal(n);
        });
        mysqlNonGenericConverterMap.put(char.class,(r, c, n)-> {
            if(n == null){
                return r.getString(1);
            }
            return r.getString(n);
        });
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

    public static void setCamelCaseToUnderscoreEnumStatus(EnableCamelCaseToUnderscore enableCamelCaseToUnderscore){
        camelCaseToUnderscoreEnum = enableCamelCaseToUnderscore;
    }

    public static EnableCamelCaseToUnderscore getCamelCaseToUnderscoreStatus(){
        return camelCaseToUnderscoreEnum;
    }
}