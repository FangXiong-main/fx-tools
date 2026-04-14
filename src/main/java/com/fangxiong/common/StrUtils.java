package com.fangxiong.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {
    public static Boolean strIsNotBlank(String s){
        int length;
        if(s!=null){
            length = s.length();
            if(length == 0){
                return false;
            }
            Pattern pattern = Pattern.compile(".*\\S+.*");
            Matcher matcher = pattern.matcher(s);
            return matcher.matches();
        }else{
            return false;
        }
    }
}
