package com.fangxiong.utils.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {
    private static final Pattern isNotBlankPattern = Pattern.compile(".*\\S+.*");
    private static final Pattern jsonIsNotBlankPattern = Pattern.compile("\\{.*\\S+.*}|\\[.*\\S+.*]");
    private static final Pattern jsonIsBlankMapPattern = Pattern.compile("\\{\\s*}");
    private static final Pattern jsonIsBlankListPattern = Pattern.compile("\\[\\s*]");
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

    public static Boolean jsonIsBlankMap(String s){
        if(s == null){
            return false;
        }else {
            Matcher matcher = jsonIsBlankMapPattern.matcher(s);
            return matcher.matches();
        }
    }

    public static Boolean jsonIsBlankList(String s){
        if(s == null){
            return false;
        }else {
            Matcher matcher = jsonIsBlankListPattern.matcher(s);
            return matcher.matches();
        }
    }

    public static Map<String,String> getKeysAndValuesMapWithJsonStr(String s) {
        Map<String,String> mapKeysAndValues = new LinkedHashMap<>();
        StringBuilder sbKeys = new StringBuilder();
        StringBuilder sbValues = new StringBuilder();
        char[] ca = s.toCharArray();
        int quotationMarksCount = 0;
        boolean isReadingKey = false;boolean isReadingValue = false;boolean isEmptyEntity = false;int isEmptyEntityCount=0;int noKeyListInt=1;boolean isListEntity=false;boolean isReadingListValue=false;
        boolean readValueFinished=false;boolean readKeyFinished=false;boolean valueIsNotString=false;boolean isListOrMapValue = false;int tempListOrMapCount=0;int listDeepCount=0;
        for (int i = 0; i < ca.length; i++) {
            if(!isListEntity&&!isListOrMapValue&&i<=ca.length-2&&ca[i+1]!='\"'&&ca[i+1]!='['&&ca[i+1]!='{'&&ca[i]==':'&&!isReadingValue) {
                isReadingValue=true;isReadingKey=false;readKeyFinished=true;valueIsNotString=true;
            } else if (ca[0] == '[' && i==0) {
                isListEntity=true;
                listDeepCount++;
            } else if (isListEntity&&(ca[i]=='['||ca[i]=='{')) {
                listDeepCount++;sbValues.append(ca[i]);
            } else if (isListEntity&&(ca[i]==']'||ca[i]=='}')&&listDeepCount!=1) {
                listDeepCount--;sbValues.append(ca[i]);
            } else if (isListEntity&&(ca[i] == '['||ca[i]=='{')&& listDeepCount==1) {
                noKeyListInt++;sbValues.append(ca[i]);
            } else if (isListEntity&&ca[i]==','&& listDeepCount==1) {
                mapKeysAndValues.put(String.valueOf(noKeyListInt),sbValues.toString());noKeyListInt++;sbValues.setLength(0);
            } else if (isListEntity&&(ca[i]==']'||ca[i]=='}')&&listDeepCount==1&&!sbValues.isEmpty()) {
                mapKeysAndValues.put(String.valueOf(noKeyListInt),sbValues.toString());
            } else if (isListEntity) {
                sbValues.append(ca[i]);
            } else if (!isReadingValue&&i<=ca.length-2&&(ca[i+1]=='[' || ca[i+1]=='{')&&ca[i]==':') {
                isListOrMapValue=true;isReadingKey=false;readKeyFinished=true;isReadingValue=true;
            } else if (isListOrMapValue && isReadingValue && (ca[i]=='['||ca[i]=='{')) {
                sbValues.append(ca[i]);tempListOrMapCount++;
            } else if (!isListOrMapValue&&i<=ca.length-2&&ca[i+1]=='\"'&&ca[i]==':'){
                isReadingValue=true;isReadingKey=false;readKeyFinished=true;
            } else if (isReadingValue&&isListOrMapValue&&i<=ca.length-2&&ca[i+1]=='\"'&&ca[i]==':') {
                isReadingKey=false;readKeyFinished=true;sbValues.append(ca[i]);
            } else if (i>=1 && ca[i-1]==':' && (ca[i]=='}' || ca[i] == ']')) {
                sbValues.append(ca[i]);isEmptyEntity=true;isEmptyEntityCount++;
            } else if (isEmptyEntity &&isEmptyEntityCount!=0 && ( ca[i]=='}' || ca[i] == ']')) {
                isReadingValue = false; readValueFinished = true;sbValues.append(ca[i]);isEmptyEntity = false;isEmptyEntityCount=0;
            } else if ( !isListOrMapValue&&i>=1 && ca[i]=='\"' && !isReadingKey && (ca[i-1]==':'||ca[i-1]=='{'||ca[i-1]==','||ca[i-1]=='[') && !valueIsNotString) {
                quotationMarksCount++;
            } else if (isListOrMapValue&&i>=1 && ca[i]=='\"' && !isReadingKey && (ca[i-1]==':'||ca[i-1]=='{'||ca[i-1]==','||ca[i-1]=='[') && !valueIsNotString) {
                quotationMarksCount++;sbValues.append(ca[i]);
            } else if (valueIsNotString && isReadingValue && (ca[i]==','||ca[i]=='}')) {
                readValueFinished=true;isReadingValue=false;valueIsNotString=false;i--;
            } else if (!isListOrMapValue&&ca[i]=='\"' && (isReadingKey||isReadingValue)) {
                quotationMarksCount--;
            } else if (isListOrMapValue&&ca[i]=='\"' && (isReadingKey||isReadingValue)) {
                quotationMarksCount--;sbValues.append(ca[i]);
            } else if (quotationMarksCount == 1 && !isReadingKey && !isReadingValue) {
                sbKeys.append(ca[i]);
                isReadingKey = true;
            } else if (tempListOrMapCount!=0&&isListOrMapValue && (ca[i]==']'||ca[i]=='}')) {
                tempListOrMapCount--;
                sbValues.append(ca[i]);
            } else if (isListOrMapValue&&tempListOrMapCount==0) {
                mapKeysAndValues.put(sbKeys.toString(),sbValues.toString());
                sbKeys.setLength(0);sbValues.setLength(0);
                isReadingKey = false;isReadingValue = false;
                readKeyFinished = false;readValueFinished = false;
                valueIsNotString = false;isListOrMapValue = false;
            } else if (quotationMarksCount == 0 && readKeyFinished && readValueFinished) {
                mapKeysAndValues.put(sbKeys.toString(),sbValues.toString());
                sbKeys.setLength(0);sbValues.setLength(0);
                isReadingKey = false;isReadingValue = false;
                readKeyFinished = false;readValueFinished = false;
                valueIsNotString = false;isListOrMapValue = false;
                tempListOrMapCount=0;
            } else if (quotationMarksCount == 0 && isReadingKey) {
                isReadingKey = false;
                readKeyFinished = true;
            } else if (quotationMarksCount == 0 && isReadingValue && !valueIsNotString && !isListOrMapValue) {
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

//    public static Map<String,String> getKeysAndValuesMapWithJsonStr(String s) {
//        Map<String,String> mapKeysAndValues = new LinkedHashMap<>();
//        StringBuilder sbKeys = new StringBuilder();
//        StringBuilder sbValues = new StringBuilder();
//        char[] ca = s.toCharArray();
//        int quotationMarksCount = 0;
//        boolean isReadingKey = false;boolean isReadingValue = false;boolean isEmptyEntity = false;int isEmptyEntityCount=0;int noKeyListInt=0;boolean isList=false;
//        boolean readValueFinished=false;boolean readKeyFinished=false;boolean valueIsNotString=false;boolean isListOrMapValue = false;int tempListOrMapCount=0;
//        for (int i = 0; i < ca.length; i++) {
//            if(!isListOrMapValue&&i<=ca.length-2&&ca[i+1]!='\"'&&ca[i+1]!='['&&ca[i+1]!='{'&&ca[i]==':'&&!isReadingValue) {
//                isReadingValue=true;isReadingKey=false;readKeyFinished=true;valueIsNotString=true;
//            } else if (!isReadingValue&&i<=ca.length-2&&(ca[i+1]=='[' || ca[i+1]=='{')&&ca[i]==':') {
//                isListOrMapValue=true;isReadingKey=false;readKeyFinished=true;isReadingValue=true;
//            } else if (isListOrMapValue && isReadingValue && (ca[i]=='['||ca[i]=='{')) {
//                sbValues.append(ca[i]);tempListOrMapCount++;
//            } else if (!isListOrMapValue&&i<=ca.length-2&&ca[i+1]=='\"'&&ca[i]==':'){
//                isReadingValue=true;isReadingKey=false;readKeyFinished=true;
//            } else if (isReadingValue&&isListOrMapValue&&i<=ca.length-2&&ca[i+1]=='\"'&&ca[i]==':') {
//                isReadingKey=false;readKeyFinished=true;sbValues.append(ca[i]);
//            } else if (i>=1 && ca[i-1]==':' && (ca[i]=='}' || ca[i] == ']')) {
//                sbValues.append(ca[i]);isEmptyEntity=true;isEmptyEntityCount++;
//            } else if (isEmptyEntity &&isEmptyEntityCount!=0 && ( ca[i]=='}' || ca[i] == ']')) {
//                isReadingValue = false; readValueFinished = true;sbValues.append(ca[i]);isEmptyEntity = false;isEmptyEntityCount=0;
//            } else if ( !isListOrMapValue&&i>=1 && ca[i]=='\"' && !isReadingKey && (ca[i-1]==':'||ca[i-1]=='{'||ca[i-1]==','||ca[i-1]=='[') && !valueIsNotString) {
//                quotationMarksCount++;
//            } else if (isListOrMapValue&&i>=1 && ca[i]=='\"' && !isReadingKey && (ca[i-1]==':'||ca[i-1]=='{'||ca[i-1]==','||ca[i-1]=='[') && !valueIsNotString) {
//                quotationMarksCount++;sbValues.append(ca[i]);
//            } else if (valueIsNotString && isReadingValue && (ca[i]==','||ca[i]=='}')) {
//                readValueFinished=true;isReadingValue=false;valueIsNotString=false;i--;
//            } else if (!isListOrMapValue&&ca[i]=='\"' && (isReadingKey||isReadingValue)) {
//                quotationMarksCount--;
//            } else if (isListOrMapValue&&ca[i]=='\"' && (isReadingKey||isReadingValue)) {
//                quotationMarksCount--;sbValues.append(ca[i]);
//            } else if (quotationMarksCount == 1 && !isReadingKey && !isReadingValue) {
//                sbKeys.append(ca[i]);
//                isReadingKey = true;
//            } else if (tempListOrMapCount!=0&&isListOrMapValue && (ca[i]==']'||ca[i]=='}')) {
//                tempListOrMapCount--;
//                sbValues.append(ca[i]);
//            } else if (isListOrMapValue&&tempListOrMapCount==0) {
//                mapKeysAndValues.put(sbKeys.toString(),sbValues.toString());
//                sbKeys.setLength(0);sbValues.setLength(0);
//                isReadingKey = false;isReadingValue = false;
//                readKeyFinished = false;readValueFinished = false;
//                valueIsNotString = false;isListOrMapValue = false;
//            } else if (quotationMarksCount == 0 && readKeyFinished && readValueFinished) {
//                mapKeysAndValues.put(sbKeys.toString(),sbValues.toString());
//                sbKeys.setLength(0);sbValues.setLength(0);
//                isReadingKey = false;isReadingValue = false;
//                readKeyFinished = false;readValueFinished = false;
//                valueIsNotString = false;isListOrMapValue = false;
//                tempListOrMapCount=0;
//            } else if (quotationMarksCount == 0 && isReadingKey) {
//                isReadingKey = false;
//                readKeyFinished = true;
//            } else if (quotationMarksCount == 0 && isReadingValue && !valueIsNotString && !isListOrMapValue) {
//                isReadingValue = false;
//                readValueFinished = true;
//                i--;
//            } else if (isReadingKey) {
//                sbKeys.append(ca[i]);
//            } else if (isReadingValue) {
//                sbValues.append(ca[i]);
//            }
//        }
//        return mapKeysAndValues;
//    }

    public static Map<String,String> getSplitMainJsonToPartlyMap(StringBuilder sbMain,String json){
        Map<String,String> tempPartMap = new LinkedHashMap<>();
        char[] ca = json.toCharArray();
        int tempPointer=1;int tempLeftPointer=0;int tempPartLeftPointer=0;boolean isNotFirst=false;
        int firstCharCounter=0;int noFieldNameInt=1;boolean isReadPart =false;
        StringBuilder sbPart = new StringBuilder();String tempFiledName = "";String tempSplitOut;
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
        boolean isReadingMap=false;boolean isReadingList =false;int detListCount=0;int readingNormalValue =0;
        for(int i=0;i<ca.length;i++){
            if (ca[i] == '[' && detListCount>=1) {
                isReadingList=true;
                sb.append(ca[i]);
            } else if(ca[i] == '['){
                detListCount++;
            } else if (ca[i] =='{') {
                isReadingMap=true;
                sb.append(ca[i]);
            } else if (ca[i]=='}') {
                isReadingMap=false;
                sb.append(ca[i]);
                i++;
                cacheArr.add(sb.toString());
                sb.setLength(0);
            } else if (ca[i] == ']' && isReadingList) {
                sb.append(ca[i]);
                cacheArr.add(sb.toString());
                sb.setLength(0);
                i++;
                isReadingList=false;
            } else if (isReadingMap) {
                sb.append(ca[i]);
            } else if (isReadingList&&ca[i]!='"') {
                sb.append(ca[i]);
            } else if (i==ca.length-1 && !sb.isEmpty()) {
                cacheArr.add(sb.toString());
            } else if (ca[i]==',' && readingNormalValue==0) {
                cacheArr.add(sb.toString()); // ["T,T"]
                sb.setLength(0);
            } else if(ca[i]!='"'){
                sb.append(ca[i]);
            } else if (ca[i]=='"' && !isReadingList && readingNormalValue==0) {
                readingNormalValue++;
            } else if (ca[i]=='"' && !isReadingList && readingNormalValue>=1) {
                readingNormalValue--;
            }
        }
        return cacheArr;
    }

    public static String getUndecoratedJSONStr(String json){
        return json.replaceAll("\\s+","");
    }
}