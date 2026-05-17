package com.fangxiong.mysqlUtilsCore.coreUtil;

import com.fangxiong.globalUtils.GlobalCustomizeClazzDetector;
import com.fangxiong.globalUtils.GlobalConverterCacheLib;
import com.fangxiong.mysqlUtilsCore.annotations.*;
import com.fangxiong.mysqlUtilsCore.converter.MysqlGenericConverterFactory;
import com.fangxiong.mysqlUtilsCore.converter.MysqlNonGenericConverterFactory;
import com.fangxiong.mysqlUtilsCore.exceptions.MysqlUtilsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlCoreUtils {
    private static final Pattern sqlValuePattern = Pattern.compile("#\\{(\\S+)}");
    private static final Pattern underscoreNamePattern = Pattern.compile("(\\S+)_(\\S+)");
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
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(proxy, args);
            }
            String originalSql = "";
            Method mapperMethod = mysqlMapperMethodCache.get(method.getName());  //interface's method
            Type returnType = mapperMethod.getGenericReturnType();
            Map<String, String> fieldValueMap = null;
            if (method.getParameterCount()==1) {
                Class<?> paramClass = method.getParameters()[0].getType();
                if (GlobalCustomizeClazzDetector.isCustomizeClazz(paramClass)) {
                    fieldValueMap = cacheEntityFieldValue(paramClass, args[0]);
                }
            }else {
                fieldValueMap = cacheAllArgs(args, mapperMethod.getParameters());
            }
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
            if(originalSql.isEmpty()){
                throw new MysqlUtilsException("No sql sequence detected!");
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
            if(!(annotation instanceof Select)){
                Statement statement = mysqlConnection.createStatement();
                int i = statement.executeUpdate(originalSql);
                statement.close();mysqlConnection.close();
                return i;
            }
            Statement statement = mysqlConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(originalSql);
            if(returnType instanceof ParameterizedType pt){
                Object converter = MysqlGenericConverterFactory.getConverter((Class<?>) pt.getRawType()).converter(resultSet, returnType);
                statement.close();mysqlConnection.close();
                return converter;
            }else {
                return MysqlNonGenericConverterFactory.getConverter((Class<?>) returnType).converter(resultSet,(Class<?>) returnType,null);
            }
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
            if(isDigitType(p.getType())){
                cacheMap.put(p.getAnnotation(ParamName.class).value(),args[cursor].toString());
            }else{
                String s = convertEscapeCharacterToStr(args[cursor].toString());
                cacheMap.put(p.getAnnotation(ParamName.class).value(),"'"+s+"'");
            }
            cursor++;
        }
        return cacheMap;
    }

    private static String convertEscapeCharacterToStr(String str){
        StringBuilder sb = new StringBuilder();
        char[] charArray = str.toCharArray();
        for(char c : charArray){
            if(c == '"'){
                sb.append("\\\\\"");
            } else if (c == '\'') {
                sb.append("\\\\'");
            } else if (c == '\\') {
                sb.append("\\\\\\");
            } else if (c == '\b') {
                sb.append("\\\\").append("b");
            } else if (c == '\f') {
                sb.append("\\\\").append("f");
            } else if (c == '\n') {
                sb.append("\\\\").append("n");
            } else if (c == '\r') {
                sb.append("\\\\").append("r");
            } else if (c == '\t') {
                sb.append("\\\\").append("t");
            } else if (c <= 31) {
                sb.append(String.format("\\\\u%04X",(int)c));
            }else {
                sb.append(c);
            }
        }
        return  sb.toString();
    }

    public static Boolean isDigitType(Class<?> clazz){
        return clazz == Integer.class || clazz == int.class || clazz == Float.class || clazz == float.class || clazz == Double.class || clazz == double.class || clazz == BigInteger.class || clazz == BigDecimal.class;
    }

    public static String convertColumnNameToCamelCase(String s){
        Matcher matcher = underscoreNamePattern.matcher(s);
        if(matcher.matches()){
            return matcher.group(1)+Character.toUpperCase(matcher.group(2).charAt(0))+matcher.group(2).substring(1);
        }
        return s;
    }
}
