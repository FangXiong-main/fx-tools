package com.fangxiong.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
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

    public static Map<String,String> getJSONKeysAndValuesWithPartlyMap(String s) {
        Map<String,String> mapKeysAndValues = new LinkedHashMap<>();
        StringBuilder sbKeys = new StringBuilder();
        StringBuilder sbValues = new StringBuilder();
        char[] ca = s.toCharArray();
        int quotationMarksCount = 0;
        boolean isReadingKey = false;boolean isReadingValue = false;boolean isEmptyEntity = false;int isEmptyEntityCount=0;
        boolean readValueFinished=false;boolean readKeyFinished=false;boolean valueIsNotString=false;
        for (int i = 0; i < ca.length; i++) {
            if(i<=ca.length-2&&ca[i+1]!='\"'&&ca[i]==':') {
                isReadingValue=true;isReadingKey=false;readKeyFinished=true;valueIsNotString=true;
            } else if (i<=ca.length-2&&ca[i+1]=='\"'&&ca[i]==':'){
                isReadingValue=true;isReadingKey=false;readKeyFinished=true;
            } else if (i>=1 && ca[i-1]==':' && (ca[i]=='{' || ca[i] == ']')) {
                sbValues.append(ca[i]);isEmptyEntity=true;isEmptyEntityCount++;
            } else if (isEmptyEntity &&isEmptyEntityCount!=0 && ( ca[i]=='}' || ca[i] == ']')) {
                isReadingValue = false; readValueFinished = true;sbValues.append(ca[i]);isEmptyEntity = false;isEmptyEntityCount=0;
            } else if (i>=1 && ca[i]=='\"' && !isReadingKey && (ca[i-1]==':'||ca[i-1]=='{'||ca[i-1]==',') && !valueIsNotString) {
                quotationMarksCount++;
            } else if (valueIsNotString && isReadingValue && (ca[i]==','||ca[i]=='}')) {
                readValueFinished=true;isReadingValue=false;valueIsNotString=false;i--;
            } else if (ca[i]=='\"' && (isReadingKey||isReadingValue)) {
                quotationMarksCount--;
            } else if (quotationMarksCount == 1 && !isReadingKey && !isReadingValue) {
                sbKeys.append(ca[i]);
                isReadingKey = true;
            } else if (quotationMarksCount == 0 && readKeyFinished && readValueFinished) {
                mapKeysAndValues.put(sbKeys.toString(),sbValues.toString());
                sbKeys.setLength(0);sbValues.setLength(0);
                isReadingKey = false;isReadingValue = false;
                readKeyFinished = false;readValueFinished = false;
                valueIsNotString = false;
            } else if (quotationMarksCount == 0 && isReadingKey) {
                isReadingKey = false;
                readKeyFinished = true;
            } else if (quotationMarksCount == 0 && isReadingValue && !valueIsNotString) {
                isReadingValue = false;
                readValueFinished = true;
                i--;
            } else if (isReadingKey) {
                sbKeys.append(ca[i]);
            } else if (isReadingValue) {
                sbValues.append(ca[i]);
            }
        }
        return mapKeysAndValues;
    }

    public static String getSplitMainJsonToPartly(String json){
        StringBuilder sb = new StringBuilder();
        StringBuilder sbMain = new StringBuilder();
        Map<String, String> splitMainJsonToPartlyMap = getSplitMainJsonToPartlyMap(sbMain,json);
        int totalCount = splitMainJsonToPartlyMap.size();int tempCount = 0;
        sb.append("{");
        for(String key : splitMainJsonToPartlyMap.keySet()){
            sb.append(key).append(":").append(splitMainJsonToPartlyMap.get(key));
            tempCount++;
            if(tempCount<totalCount){
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public static Map<String,String> getSplitMainJsonToPartlyMap(StringBuilder sbMain,String json){
        Map<String,String> tempPartMap = new LinkedHashMap<>();
        char[] ca = json.toCharArray();
        int tempPointer=1;int tempLeftPointer=0;int tempPartLeftPointer=0;boolean isNotFirst=false;
        int firstCharCounter=0;int noFieldNameInt=1;boolean isReadPart =false;boolean isMultiPart = false;
        StringBuilder sbPart = new StringBuilder();String tempFiledName = "";String tempSplitOut = "";
        for(int i=0;i<ca.length;i++){
            if(isReadPart){
                if(ca[i] == '{' || ca[i] == '['){
                    tempPartLeftPointer++;
                    isNotFirst = true;
                }else if (firstCharCounter==1) {
                    tempSplitOut = sbPart.substring(sbPart.length()-(tempPartLeftPointer));
                    tempFiledName = sbPart.substring(0,sbPart.length()-(tempPartLeftPointer+1));
                    sbPart.setLength(0);
                    sbPart.append(tempSplitOut);
                    firstCharCounter++;
                }else if(tempPartLeftPointer==0 && firstCharCounter==0 && ca[i] == ':' ){
                    firstCharCounter++;
                } else if(tempPartLeftPointer!=0 && (ca[i] == '}' || ca[i] == ']') ){
                    tempPartLeftPointer--;
                }else if(isNotFirst && tempPartLeftPointer==0){
                    sbMain.append(ca[i-1]);
                    tempFiledName = firstCharCounter==0 ? ""+noFieldNameInt : tempFiledName;
                    noFieldNameInt++;
                    tempPartMap.put(tempFiledName,sbPart.toString());
                    sbPart.setLength(0);
                    isReadPart = false;
                    isNotFirst = false;
                    tempLeftPointer--;
                    tempFiledName = "";
                    firstCharCounter=0;
                    i--;
                    continue;
                }
                sbPart.append(ca[i]);
            }else {
                if(ca[i] == ','){
                    tempPointer=i+1;
                    sbMain.append(ca[i]);
                } else if (tempLeftPointer!=2 &&(ca[i] == '{' || ca[i] == '[')) {
                    tempLeftPointer ++;
                    sbMain.append(ca[i]);
                } else if ((ca[i] == '}' || ca[i] == ']') && firstCharCounter!=0) {
                    sbMain.append(ca[i]);
                } else if (tempLeftPointer>1) {
                    isReadPart = true;
                    i = tempPointer-1;
                }else {
                    sbMain.append(ca[i]);
                }
            }
        }
        return tempPartMap;
    }

    public static ArrayList<String> getConvertJsonValueListToArr(String json){
        ArrayList<String> cacheArr = new ArrayList<>();
        char[] ca = json.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : ca) {
            if (c == ',' || c == ']') {
                cacheArr.add(sb.toString());
                sb.setLength(0);
            } else if(c != '[' && c!='"'){
                sb.append(c);
            }
        }
        return cacheArr;
    }
}
