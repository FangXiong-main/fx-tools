package com.fangxiong.mysqlUtilsCore.converter;

import java.lang.reflect.Type;
import java.sql.ResultSet;

@FunctionalInterface
public interface MysqlGenericConverter {
    Object converter (ResultSet resultSet, Type type);
}
