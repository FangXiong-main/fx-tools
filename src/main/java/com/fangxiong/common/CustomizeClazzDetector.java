package com.fangxiong.common;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CustomizeClazzDetector {
    private static final ArrayList<Class<?>> clazzList = new ArrayList<>();
    private static final ArrayList<Class<?>> digitalClazzList = new ArrayList<>();
    static {
        //DigitalClazz
        digitalClazzList.add(Integer.class);
        digitalClazzList.add(Long.class);
        digitalClazzList.add(Short.class);
        digitalClazzList.add(Double.class);
        digitalClazzList.add(Float.class);
        digitalClazzList.add(int.class);
        digitalClazzList.add(long.class);
        digitalClazzList.add(short.class);
        digitalClazzList.add(double.class);
        digitalClazzList.add(float.class);


        clazzList.add(Integer.class);
        clazzList.add(Long.class);
        clazzList.add(Short.class);
        clazzList.add(Byte.class);
        clazzList.add(Double.class);
        clazzList.add(Float.class);
        clazzList.add(Boolean.class);
        clazzList.add(Character.class);
        clazzList.add(String.class);
        clazzList.add(BigDecimal.class);
        clazzList.add(BigInteger.class);
        clazzList.add(LocalDate.class);
        clazzList.add(LocalTime.class);
        clazzList.add(LocalDateTime.class);
        clazzList.add(Date.class);
        clazzList.add(Timestamp.class);
        clazzList.add(List.class);
        clazzList.add(Map.class);
        clazzList.add(Set.class);
        clazzList.add(Object.class);
        clazzList.add(Class.class);
        clazzList.add(int.class);
        clazzList.add(long.class);
        clazzList.add(short.class);
        clazzList.add(byte.class);
        clazzList.add(double.class);
        clazzList.add(float.class);
        clazzList.add(boolean.class);
        clazzList.add(char.class);
    }

    public static Boolean isCustomizeClazz(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> i : interfaces) {
            if (clazzList.contains(i)) {
                return false;
            }
        }
        return !clazzList.contains(clazz);
    }

    public static Boolean isDigitalClazz(Class<?> clazz){
        return digitalClazzList.contains(clazz);
    }
}
