package com.fangxiong.mysqlUtilsCore.converter;

import java.sql.ResultSet;

public interface MysqlConverter {
    Object converter (ResultSet resultSet,Class<?> clazz);
}
