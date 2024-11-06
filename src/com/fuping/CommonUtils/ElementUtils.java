package com.fuping.CommonUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.util.StrUtil.isEmptyIfStr;

public class ElementUtils {
    public static boolean isNotEmptyIfStr(String string) {
        //判断字符串是否不为空
        return !isEmptyIfStr(string);
    }

    private static boolean isContainOneKeyByEach(String stringFormat, List<String> elementsFormat) {
        for (String element : elementsFormat) {
            if (element.length()>0 && stringFormat.contains(element)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串 是否 包含 列表中的任意元素
     *
     * @param string 单个字符串。
     * @param elementsString 允许的字符串，用'|'分隔。
     * @param defaultBool 当 elementsString 为空时应该返回的响应码
     * @return 如果 elementStrings 的任意子元素 在 string 内 则返回true，否则返回false。
     */
    public static boolean isContainOneKeyByEach(String string, String elementsString, boolean defaultBool) {
        //当元素为空时,返回默认值
        if (isNotEmptyIfStr(string) || isNotEmptyIfStr(elementsString)) return defaultBool;

        //预先格式化处理
        String stringFormat = string.toLowerCase();
        String[] elementsFormat = elementsString.toLowerCase().split("\\|");

        return isContainOneKeyByEach(stringFormat, Arrays.asList(elementsFormat));
    }

    public static boolean isContainOneKeyByRegex(String receive, String keyRegex) {
        if(isEmptyIfStr(keyRegex)){return false;}
        // 编译正则表达式 //忽略大小写
        Pattern pattern = Pattern.compile(keyRegex, Pattern.CASE_INSENSITIVE);
        // 创建匹配器对象
        Matcher matcher = pattern.matcher(receive);
        // 查找匹配的子字符串
        return matcher.find();
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
}
