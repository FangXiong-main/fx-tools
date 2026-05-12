package com.fangxiong.mysqlUtilsCore.coreUtil;

import com.fangxiong.globalUtils.CustomizeClazzDetector;
import com.fangxiong.globalUtils.GlobalConverterCacheLib;
import com.fangxiong.mysqlUtilsCore.annotations.*;
import com.fangxiong.mysqlUtilsCore.exceptions.MysqlUtilsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlCoreUtils {

    private static final Pattern sqlValuePattern = Pattern.compile("#\\{(\\S+)}");
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
        InvocationHandler invocationHandler = (proxy, method, args) -> {
            String originalSql = "";
            Method mapperMethod = mysqlMapperMethodCache.get(method.getName());  //interface's method
            Map<String, String> fieldValueMap = null;
            if (method.getParameterCount()==1) {
                Class<?> paramClass = method.getParameters()[0].getType();
                if (CustomizeClazzDetector.isCustomizeClazz(paramClass)) {
                    fieldValueMap = cacheEntityFieldValue(paramClass, args[0]);
                }
            }else {
                fieldValueMap = cacheAllArgs(args, mapperMethod.getParameters());
            }
            Map<String, Integer> mysqlParamIndexCache = GlobalConverterCacheLib.getMysqlParamIndexCache(mapperMethod);
            Statement statement = mysqlConnection.createStatement();
            Annotation[] declaredAnnotations = mapperMethod.getDeclaredAnnotations();
            if(declaredAnnotations.length != 1){
                throw new MysqlUtilsException("Expect single annotation,but find 0 or more than one.");
            }
            Annotation annotation = declaredAnnotations[0];
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
                String tempParamName = matcher.group(1);
                String tempRegex = "#\\{"+tempParamName+"}";
                if (fieldValueMap==null) {
                    originalSql = originalSql.replaceFirst(tempRegex,args[0].toString());
                }else {
                    originalSql = originalSql.replaceFirst(tempRegex,fieldValueMap.get(tempParamName));
                }
            }
            System.out.println(originalSql);
            return null;
        };
        return (T) Proxy.newProxyInstance(mapper.getClassLoader(), new Class[]{mapper},invocationHandler);
    }

    private static Map<String,String> cacheEntityFieldValue(Class<?> clazz,Object entity){
        Field[] fields = GlobalConverterCacheLib.getConverterFieldCache(clazz);
        Map<Field, Method> converterGetMethodCache = GlobalConverterCacheLib.getConverterGetMethodCache(clazz);
        Map<String,String> filedValueCache = new HashMap<>();
        String tempMethodName = "";
        try {
            for(Field f : fields){
                Method method = converterGetMethodCache.get(f);
                tempMethodName = method.getName();
                filedValueCache.put(f.getName(), method.invoke(entity).toString());
            }
        } catch (Exception e) {
            throw new MysqlUtilsException("Can't find get method '"+tempMethodName+"' in entity '"+clazz.getName()+"'.");
        }
        return filedValueCache;
    }

    private static Map<String,String> cacheAllArgs(Object[] args, Parameter[] parameters){
        Map<String,String> cacheMap = new HashMap<>();
        int cursor = 0;
        for(Parameter p : parameters){
            cacheMap.put(p.getAnnotation(ParamName.class).value(),args[cursor].toString());
            cursor++;
        }
        return cacheMap;
    }
}
