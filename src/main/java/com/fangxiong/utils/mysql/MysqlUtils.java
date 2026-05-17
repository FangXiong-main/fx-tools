package com.fangxiong.utils.mysql;


import com.fangxiong.mysqlUtilsCore.converter.MysqlNonGenericConverterFactory;
import com.fangxiong.mysqlUtilsCore.coreUtil.MysqlCoreUtils;
import com.fangxiong.mysqlUtilsCore.enums.EnableCamelCaseToUnderscore;
import com.fangxiong.mysqlUtilsCore.exceptions.MysqlUtilsException;

import java.sql.Connection;

public class MysqlUtils {

    public static <T> T useMapper (Connection connection, Class<T> mapper){
        if (!MysqlCoreUtils.setMysqlConnection(connection)) {
            throw new MysqlUtilsException("Set mysql connection failed!");
        }
        return MysqlCoreUtils.getMapperEntity(mapper,null);
    }

    public static <T> T useMapper (Connection connection, Class<T> mapper, EnableCamelCaseToUnderscore enableCamelCaseToUnderscore){
        MysqlNonGenericConverterFactory.setCamelCaseToUnderscoreEnumStatus(enableCamelCaseToUnderscore);
        return useMapper(connection,mapper);
    }

}
