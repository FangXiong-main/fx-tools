package com.fangxiong.utils.mysql;


import com.fangxiong.mysqlUtilsCore.coreUtil.MysqlCoreUtils;
import com.fangxiong.mysqlUtilsCore.exceptions.MysqlUtilsException;

import java.sql.Connection;

public class MysqlUtils {

    public static <T> T useMapper (Connection connection, Class<T> mapper){
        if (!MysqlCoreUtils.setMysqlConnection(connection)) {
            throw new MysqlUtilsException("Set mysql connection failed!");
        }
        return MysqlCoreUtils.getMapperEntity(mapper,null);
    }

}
