package com.fuping.CommonUtils;

import com.fuping.LoadDict.UserPassPair;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.util.StrUtil.isEmptyIfStr;
import static com.fuping.LoadDict.LoadDictUtils.excludeHistoryPairs;
import static com.fuping.LoadDict.LoadDictUtils.replaceUserMarkInPass;
import static com.fuping.PrintLog.PrintLog.print_debug;

public class Utils {

    public static UserPassPair[] processedUserPassHashSet(
            HashSet<UserPassPair> pairsHashSet,
            String historyFile,
            String separator,
            boolean exclude_history,
            String userMarkInPass){


        //替换密码中的用户名变量
        pairsHashSet = replaceUserMarkInPass(pairsHashSet, userMarkInPass);
        print_debug(String.format("Pairs Count After Replace Mark Str [%s]", pairsHashSet.size()));

        //读取 history 文件,排除历史扫描记录 ，
        if (exclude_history) {
            pairsHashSet = excludeHistoryPairs(pairsHashSet, historyFile, separator);
            print_debug(String.format("Pairs Count After Exclude History [%s] From [%s]", pairsHashSet.size(), historyFile));
        }

        //将账号密码字典格式从 HashSet 转为 数组,便于索引统计
        UserPassPair[] userPassPairsArray = pairsHashSet.toArray(new UserPassPair[0]);
        return userPassPairsArray;
    }


    public static String regexExtract(String string, String regex) {
        //忽略匹配空值
        if(isEmptyIfStr(string)) return null;
        if(isEmptyIfStr(regex)) return string;

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        // 查找匹配项
        if (matcher.find()) {
            // 返回匹配到的结果
            return matcher.group(0);
        } else {
            return null; // 没有匹配项
        }
    }

    public static boolean isNumber(String string){
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public static String urlRemoveQuery(String url) {
        // 获取 URL 的无参数部分
        int queryIndex = url.indexOf("?");
        return (queryIndex != -1) ? url.substring(0, queryIndex) : url;
    }

    public static String escapeString(Object Obj){
        if(null==Obj) return "null";
        String string = String.valueOf(Obj);
        return string.replace("\"","\\\"");
    }

}
