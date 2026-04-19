package com.fangxiong.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {
    private static final Pattern isNotBlankPattern = Pattern.compile(".*\\S+.*");
    private static final Pattern jsonIsNotBlankPattern = Pattern.compile("\\{.*\\S+.*\\}|\\[.*\\S+.*\\]");
    public static Boolean strIsNotBlank(String s){
        if (s == null){
            return false;
        }else {
            Matcher matcher = isNotBlankPattern.matcher(s);
            return matcher.matches();
        }
    }

    public static Boolean jsonIsNotBlank(String s){
        if(s == null){
            return false;
        }else {
            Matcher matcher = jsonIsNotBlankPattern.matcher(s);
            return matcher.matches();
        }
    }
}
