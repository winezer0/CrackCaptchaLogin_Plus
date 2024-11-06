package com.fuping.CommonUtils;

import cn.hutool.core.io.FileUtil;
import com.fuping.LoadDict.UserPassPair;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.io.CharsetDetector.detect;
import static cn.hutool.core.util.StrUtil.isEmptyIfStr;
import static com.fuping.LoadConfig.MyConst.*;
import static com.fuping.LoadDict.LoadDictUtils.excludeHistoryPairs;
import static com.fuping.LoadDict.LoadDictUtils.replaceUserMarkInPass;
import static com.fuping.PrintLog.PrintLog.print_info;

public class Utils {
    public static String getFileStrAbsolutePath(String fileStr) {
        //获取文件的物理路径
        return new File(fileStr).getAbsolutePath();
    }

    public static boolean isEmptyFile(String fileStr) {
        //判断文件是否为空
        String absolutePath = getFileStrAbsolutePath(fileStr);
        return FileUtil.isEmpty(new File(absolutePath));
    }

    public static boolean isNotEmptyFile(String fileStr) {
        //判断文件是否不为空
        return !isEmptyFile(fileStr);
    }

    public static boolean isNotEmptyIfStr(String string) {
        //判断字符串是否不为空
        return !isEmptyIfStr(string);
    }

    public static String checkFileEncode(String absolutePath, String defaultEncode){
        //检测文件编码
        String encoding = defaultEncode;
        if (isNotEmptyFile(absolutePath)){
            try {
                encoding = detect(new File(absolutePath)).name();
                print_info(String.format("Detect File Encoding [%s] From [%s]", encoding, absolutePath));
            } catch (Exception e){
                encoding = defaultEncode;
            }
        }
        return encoding;
    }

    public static String genFileNameByUrl(String urlString, String defaultPath, String suffix, boolean useAbsolutePath){
        String filename = defaultPath;

        if (isNotEmptyIfStr(urlString)){
            filename = urlString.split("[\\\\\"*?<>|%'#]")[0]; // 按照 # 或 ? 进行切割
            filename = filename.replaceAll("[/:]", "_"); // 对URL的特殊字符进行替换
            filename = String.format("%s%s", filename, suffix);
        }

        return  useAbsolutePath ? getFileStrAbsolutePath(filename): filename;
    }

    public static boolean writeUserPassPairToFile(String historyFile, String separator, UserPassPair userPassPair){
        //写入账号密码对文件到历史记录文件
        try {
            historyFile = getFileStrAbsolutePath(historyFile);
            String content = String.format("%s%s%s",userPassPair.getUsername(), separator, userPassPair.getPassword());
            // 使用 Hutool 写入字符串到文件
            FileUtil.appendString(String.format("%s\n", content), historyFile, "UTF-8");
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeTitleToFile(String historyFile, String content){
        //写入账号密码对文件到历史记录文件
        try {
            historyFile = getFileStrAbsolutePath(historyFile);
            if(isEmptyFile(historyFile)){
                FileUtil.appendString(String.format("%s\n", content), historyFile, "UTF-8");
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeLineToFile(String historyFile, String content){
        //写入账号密码对文件到历史记录文件
        try {
            historyFile = getFileStrAbsolutePath(historyFile);
            FileUtil.appendString(String.format("%s\n", content), historyFile, "UTF-8");
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void initBaseOnLoginUrlFile(String login_url) {
        //根据当前登录URL生成 history 文件名称
        globalCrackHistoryFilePath = genFileNameByUrl(login_url, "dict/CrackHistory.log", ".CrackHistory.log", true);
        globalCrackLogRecodeFilePath = genFileNameByUrl(login_url, "dict/CrackLogRecode.csv", ".CrackLogRecode.csv", true);
        globalLoginSuccessFilePath = genFileNameByUrl(login_url, "dict/LoginSuccess.log", ".LoginSuccess.log", true);
        globalLoginFailureFilePath = genFileNameByUrl(login_url, "dict/LoginFailure.log", ".LoginFailure.log", true);
        globalErrorCaptchaFilePath = genFileNameByUrl(login_url, "dict/ErrorCaptcha.log", ".ErrorCaptcha.log", true);
    }

    public static UserPassPair[] processedUserPassHashSet(
            HashSet<UserPassPair> pairsHashSet,
            String historyFile,
            String separator,
            boolean exclude_history,
            String userMarkInPass){
        //读取 history 文件,排除历史扫描记录 ，
        if (exclude_history) {
            pairsHashSet = excludeHistoryPairs(pairsHashSet, historyFile, separator);
            print_info(String.format("Pairs Count After Exclude History [%s] From [%s]", pairsHashSet.size(), historyFile));
        }

        //替换密码中的用户名变量
        replaceUserMarkInPass(pairsHashSet, userMarkInPass);
        print_info(String.format("Pairs Count After Replace Mark Str [%s]", pairsHashSet.size()));

        //将账号密码字典格式从 HashSet 转为 数组,便于索引统计
        UserPassPair[] userPassPairsArray = pairsHashSet.toArray(new UserPassPair[0]);
        return userPassPairsArray;
    }

    public static boolean containsMatchingSubString(String receive, String keyRegex) {
        if(isEmptyIfStr(keyRegex)){return false;}
        // 编译正则表达式 //忽略大小写
        Pattern pattern = Pattern.compile(keyRegex, Pattern.CASE_INSENSITIVE);
        // 创建匹配器对象
        Matcher matcher = pattern.matcher(receive);
        // 查找匹配的子字符串
        return matcher.find();
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

    public static long getFileModified(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {return -1;}
        return file.lastModified();
    }

    public static boolean isModifiedAuthFile() {
        long pairFileTime = getFileModified(globalUserPassFile);
        long nameFileTime = getFileModified(globalUserNameFile);
        long passFileTime = getFileModified(globalPassWordFile);

        if(nameFileTime != globalUserFileLastModified || passFileTime != globalPassFileLastModified || pairFileTime != globalPairFileLastModified){
            globalPairFileLastModified = pairFileTime;
            globalUserFileLastModified = nameFileTime;
            globalPassFileLastModified = passFileTime;
            return true;
        }

        return false;
    }

    public static boolean isModifiedLoginUrl(String login_url){
        //检查登录URL是否更新, 更新了就重新赋值
        if(login_url.equalsIgnoreCase(default_login_url)) {
            return false;
        } else{
            print_info(String.format("The login URL has been modified from [%s] to [%s]", default_login_url, login_url));
            default_login_url = login_url;
            return true;
        }
    }

    public static boolean isModifiedDictMode(String dict_mode){
        //检查登录URL是否更新, 更新了就重新赋值
        if(dict_mode.equalsIgnoreCase(default_dict_compo_mode)) {
            return false;
        } else{
            print_info(String.format("The dict mode has been modified from [%s] to [%s]", default_dict_compo_mode, dict_mode));
            default_dict_compo_mode = dict_mode;
            return true;
        }
    }

    public static boolean isModifiedExcludeHistory(boolean exclude_history){
        //检查登录URL是否更新, 更新了就重新赋值
        if(exclude_history == globalExcludeHistorySwitch){
            return false;
        } else {
            print_info(String.format("The Exclude History has been modified from [%s] to [%s]", globalExcludeHistorySwitch, exclude_history));
            globalExcludeHistorySwitch = exclude_history;
            return true;
        }
    }

    public static String escapeString(Object Obj){
        if(null==Obj) return "null";
        String string = String.valueOf(Obj);
        return string.replace("\"","\\\"");
    }

    public static boolean isSimilarLink(String cur_captcha_url, String raw_captcha_url){
        //使用字符串匹配
        if(raw_captcha_url.equalsIgnoreCase(cur_captcha_url)){
            return true;
        }

        //使用正则匹配
        Pattern pattern = Pattern.compile(raw_captcha_url);
        Matcher matcher = pattern.matcher(cur_captcha_url);
        if (matcher.matches()) {
            //System.out.println(String.format("Pattern Match found! %s", cur_captcha_url));
            return true;
        }

        //匹配失败
        return false;
    }

    public static boolean isEqualsOneKey(String stringFormat, List<String> elementsFormat) {
        return elementsFormat.stream().anyMatch(stringFormat::equals);
    }

    public static void main(String[] args) {
        String urlString = "https://www.example.com/login.jsp?session=1";
        System.out.println(genFileNameByUrl(urlString, "dict/history.txt",".history.txt",true));
    }
}
