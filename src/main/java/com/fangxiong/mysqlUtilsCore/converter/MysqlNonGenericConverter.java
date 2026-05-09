package com.fangxiong.mysqlUtilsCore.converter;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface MysqlNonGenericConverter {
    Object converter (ResultSet resultSet,Class<?> clazz,String columName) throws SQLException;
}
