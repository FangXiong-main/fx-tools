package com.fangxiong.mysqlUtilsCore.coreUtil;

import com.fangxiong.mysqlUtilsCore.annotations.Select;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class CurrentOperationAnnoDetector {
    private static final Map<String,Class<?>> mysqlOperationAnnoCache = new HashMap<>();

}
