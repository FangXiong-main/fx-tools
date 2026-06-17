package com.fangxiong.mysqlUtilsCore.converter;

import com.fangxiong.mysqlUtilsCore.enums.EnableCamelCaseToUnderscore;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MysqlNonGenericConverterFactory {
    private static EnableCamelCaseToUnderscore camelCaseToUnderscoreEnum = EnableCamelCaseToUnderscore.ENABLE;
    private static final Map<Class<?>,MysqlNonGenericConverter> mysqlNonGenericConverterMap = new HashMap<>();
    static{
        //(r,c,n) -> resultSet,clazz,columName
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
        mysqlNonGenericConverterMap.put(Short.class,(r,c,n)->{
            if (n == null){
                return r.getShort(1);
            }
            return r.getShort(n);
        });
        mysqlNonGenericConverterMap.put(BigInteger.class,(r,c,n)-> {
            if (n == null){
                return r.getLong(1);
            }
            return r.getLong(n);
        });
        mysqlNonGenericConverterMap.put(Long.class,(r,c,n)->{
            if (n == null){
                return r.getLong(1);
            }
            return r.getLong(n);
        });
        mysqlNonGenericConverterMap.put(long.class,(r,c,n)->{
            if (n == null){
                return r.getLong(1);
            }
            return r.getLong(n);
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
        mysqlNonGenericConverterMap.put(LocalDate.class,(r,c,n)->{
            if (n == null){
               return  r.getDate(1).toLocalDate();
            }
            return  r.getDate(n).toLocalDate();
        });
        mysqlNonGenericConverterMap.put(LocalDateTime.class,new MysqlLocalDateTimeConverter());
        mysqlNonGenericConverterMap.put(Date.class,(r,c,n)->{
            if (n == null){
                return r.getDate(1);
            }
            return r.getDate(n);
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