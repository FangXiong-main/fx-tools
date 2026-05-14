package com.fangxiong.mysqlUtilsCore.coreUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CurrentColumnTypeDetector {
    private final Map<Integer,Class<?>> mysqlColumnTypeMap = new HashMap<>();

    public CurrentColumnTypeDetector(){
        mysqlColumnTypeMap.put(Types.CHAR,String.class);
        mysqlColumnTypeMap.put(Types.INTEGER,Integer.class);
        mysqlColumnTypeMap.put(Types.BOOLEAN,Boolean.class);
        mysqlColumnTypeMap.put(Types.BIGINT, BigInteger.class);
        mysqlColumnTypeMap.put(Types.DECIMAL, BigDecimal.class);
        mysqlColumnTypeMap.put(Types.DOUBLE,Double.class);
        mysqlColumnTypeMap.put(Types.FLOAT,Float.class);
        mysqlColumnTypeMap.put(Types.DATE, Date.class);
        mysqlColumnTypeMap.put(Types.VARCHAR, String.class);
        mysqlColumnTypeMap.put(Types.TINYINT, Integer.class);
    }

    public Class<?> getMysqlColumnTypeMap(Integer typeInt) {
        return mysqlColumnTypeMap.get(typeInt);
    }
}
