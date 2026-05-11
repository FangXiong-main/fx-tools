package com.fangxiong.utils.mysql;

import com.fangxiong.globalUtils.CustomizeClazzDetector;
import com.fangxiong.globalUtils.GlobalConverterCacheLib;
import com.fangxiong.mysqlUtilsCore.annotations.Delete;
import com.fangxiong.mysqlUtilsCore.annotations.Insert;
import com.fangxiong.mysqlUtilsCore.annotations.Select;
import com.fangxiong.mysqlUtilsCore.annotations.Update;
import com.fangxiong.mysqlUtilsCore.converter.MysqlNonGenericConverterFactory;
import com.fangxiong.mysqlUtilsCore.enums.EnableCamelCaseToUnderscore;
import com.fangxiong.mysqlUtilsCore.converter.MysqlGenericConverterFactory;
import com.fangxiong.mysqlUtilsCore.exceptions.MysqlUtilsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MysqlUtils {
    private static final Pattern sqlValuePattern = Pattern.compile("\\{\\S+}");
    private static Connection mysqlConnection = null;
    public static Boolean setMysqlConnection(Connection connection){
        mysqlConnection = connection;
        return mysqlConnection != null;
    }

    public static <T,E> T getMapperEntity(Class<T> mapper,Class<E> entityClass){
        if(mysqlConnection==null){
            throw new MysqlUtilsException("Please set Mysql Connection first! Cause by mysql connection is null.");
        }
        Map<String, Method> mysqlMapperMethodCache = GlobalConverterCacheLib.getMysqlMapperMethodCache(mapper);
        return (T)Proxy.newProxyInstance(mapper.getClassLoader(), new Class[]{mapper}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String originalSql = "";
                Method mapperMethod = mysqlMapperMethodCache.get(method.getName());
                if (method.getParameterCount()==1) {
                    Class<? extends Parameter> paramClass = method.getParameters()[0].getClass();
                    if (CustomizeClazzDetector.isCustomizeClazz(paramClass)) {
                        // TODO This part
                    }
                }
                Map<String, Integer> mysqlParamIndexCache = GlobalConverterCacheLib.getMysqlParamIndexCache(mapperMethod);
                Statement statement = mysqlConnection.createStatement();
                Annotation[] declaredAnnotations = mapperMethod.getDeclaredAnnotations();
                if(declaredAnnotations.length>1 || declaredAnnotations.length ==0){
                    throw new MysqlUtilsException("Expect single annotation,but find 0 or more than one.");
                }
                Annotation annotation = mapperMethod.getAnnotation(declaredAnnotations[0].getClass());
                if(annotation instanceof Select){
                    originalSql = mapperMethod.getAnnotation(Select.class).value();
                } else if (annotation instanceof Delete) {
                    originalSql = mapperMethod.getAnnotation(Delete.class).value();
                } else if (annotation instanceof Update) {
                    originalSql = mapperMethod.getAnnotation(Update.class).value();
                } else if (annotation instanceof Insert) {
                    originalSql = mapperMethod.getAnnotation(Insert.class).value();
                }
                Matcher matcher = sqlValuePattern.matcher(originalSql);
                while(matcher.find()){
                    Integer i = mysqlParamIndexCache.get(matcher.group());
                    originalSql.replaceFirst("\\{"++"}");
                }
                return null;
            }
        });
    }

}
