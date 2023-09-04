package com.fuping.LoadDict;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static cn.hutool.core.util.StrUtil.isEmptyIfStr;
import static com.fuping.CommonUtils.Utils.*;
import static com.fuping.PrintLog.PrintLog.print_error;
import static com.fuping.PrintLog.PrintLog.print_info;

public class LoadDictUtils {

    public static List<String> readDictFile(String filePath) {
        // 读取文件内容到列表
        String absolutePath = getFileStrAbsolutePath(filePath);

        //判断文件是否存在
        if (isEmptyFile(absolutePath)){
            print_error(String.format("File Not Found Or Read Empty From [%s]", absolutePath));
            System.exit(0);
            return null;
        }

        //检查文件编码
        String checkEncode = checkFileEncode(absolutePath, "UTF-8");
        // 读取文件内容到列表
        List<String> baseLines = FileUtil.readLines(absolutePath, checkEncode);
        List<String> newLines = new ArrayList<>();
        for (String line : baseLines) {
            //去除空行和首尾空格
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty()) {
                // 添加非空行到 processedLines
                newLines.add(trimmedLine);
            }
        }

        print_info(String.format("Read Lines [%s] From File [%s]", newLines.size(), absolutePath));
        return newLines;
    }


    public static LinkedHashSet<UserPassPair> createPitchforkUserPassPairs(List<String> usernames, List<String> passwords) {
        //创建 pitchfork 模式的用户密码对
        int minSize = Math.min(usernames.size(), passwords.size());
        LinkedHashSet<UserPassPair> userPassPairs = new LinkedHashSet<>();
        for (int i = 0; i < minSize; i++) {
            String username = usernames.get(i);
            String password = passwords.get(i);
            userPassPairs.add(new UserPassPair(username.trim(), password.trim()));
        }
        print_info(String.format("Create Pitchfork User Pass Pairs [%s]", userPassPairs.size()));
        return userPassPairs;
    }

    public static LinkedHashSet<UserPassPair> createCartesianUserPassPairs(List<String> usernames, List<String> passwords) {
        //创建 笛卡尔积 模式的用户密码对
        LinkedHashSet<UserPassPair> userPassPairs = new LinkedHashSet<>();

        for (String username : usernames) {
            for (String password : passwords) {
                userPassPairs.add(new UserPassPair(username.trim(), password.trim()));
            }
        }
        print_info(String.format("Create Cartesian User Pass Pairs [%s]", userPassPairs.size()));
        return userPassPairs;
    }

    public static LinkedHashSet<UserPassPair> splitAndCreatUserPassPairs(List<String> pairStringList, String pair_separator) {
        //拆分账号密钥对文件 到用户名密码字典
        LinkedHashSet<UserPassPair> userPassPairs = new LinkedHashSet<>();
        for (String str : pairStringList) {
            // 使用 split 方法按冒号分割字符串
            String[] parts = str.split(pair_separator, 2);
            if (parts.length == 2) {
                String username = parts[0];
                String password = parts[1];
                userPassPairs.add(new UserPassPair(username.trim(), password.trim()));
            }
        }
        print_info(String.format("Split And Creat User Pass Pairs [%s]", userPassPairs.size()));
        return userPassPairs;
    }

    public static LinkedHashSet<UserPassPair> loadUserPassFile(String userNameFile, String passWordFile,
                                                               boolean pitchforkMode,
                                                               String userPassFile, String pair_separator,
                                                               boolean userPassMode,
                                                               String userMarkInPass
    ){
        //判断是加载账号密码对字典还是加载账号字典
        LinkedHashSet<UserPassPair> userPassPairs = new LinkedHashSet<>();

        if(userPassMode){
            if (userPassFile != null){
                //处理账号密码对文件
                List<String> userPassPairList =  readDictFile(userPassFile);
                userPassPairs = splitAndCreatUserPassPairs(userPassPairList, pair_separator);
            }
        }else {
            if (userNameFile != null && passWordFile != null){
                //处理账号密码文件
                List<String> userNameList =  readDictFile(userNameFile);
                List<String> passWordList =  readDictFile(passWordFile);
                //判断是否使用 pitchfork 模式
                if(pitchforkMode){
                    userPassPairs = createPitchforkUserPassPairs(userNameList, passWordList);
                }else {
                    userPassPairs = createCartesianUserPassPairs(userNameList, passWordList);
                }
            }
        }

        print_info(String.format("Pairs Count After Exclude History [%s]", userPassPairs.size()));
        replaceUserMarkInPass(userPassPairs, userMarkInPass);
        print_info(String.format("Pairs Count After Replace Mark Str [%s]", userPassPairs.size()));
        return userPassPairs;
    }

    public static LinkedHashSet<UserPassPair> excludeHistoryUserPassPairs(LinkedHashSet<UserPassPair> inputUserPassPairs, String historyFile, String separator) {
        LinkedHashSet<UserPassPair> userPassPairs = inputUserPassPairs;
        //处理历史账号密码对文件
        if (isNotEmptyFile(historyFile)){
            List<String> hisUserPassPairList =  readDictFile(historyFile);
            LinkedHashSet<UserPassPair> hisUserPassPairs = splitAndCreatUserPassPairs(hisUserPassPairList, separator);
            userPassPairs = subtractHashSet(inputUserPassPairs, hisUserPassPairs);
        }
        return userPassPairs;
    }

    private static LinkedHashSet<UserPassPair> subtractHashSet(LinkedHashSet<UserPassPair> inputUserPassPairs, LinkedHashSet<UserPassPair> hisUserPassPairs) {
        //将两个Hashset相减
        LinkedHashSet<UserPassPair> userPassPairs = new LinkedHashSet<>(inputUserPassPairs);
        for (UserPassPair pairToRemove : hisUserPassPairs) {
            userPassPairs.remove(pairToRemove); // 从结果中移除与 set2 中相同的元素
        }
        return userPassPairs;
    }

    public static void showUserPassPairs(LinkedHashSet<UserPassPair> userPassPairs){
        //循环输出
        //for (UserPassPair pair : userPassPairs) { System.out.println(pair.getUsername() + " <--> " + pair.getPassword());}
        // 使用 ArrayList 转换为数组后输出
        UserPassPair[] userPassArray = userPassPairs.toArray(new UserPassPair[0]);
        // 使用 Arrays.toString() 输出数组 //需要先重写userPass的toString方法哦
        System.out.println(Arrays.toString(userPassArray));
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

    public static void replaceUserMarkInPass(LinkedHashSet<UserPassPair> userPassPairs, String userMark) {
        //仅处理存在的情况
        if(isEmptyIfStr(userMark) || userPassPairs.size()< 1){
            return;
        }

        //替换密码中的用户名标记符号为用户名
        for (UserPassPair pair : userPassPairs) {
            String newPassword = pair.getPassword().replace(userMark, pair.getUsername());
            pair.setPassword(newPassword);
        }
    }

    public static void main(String[] args) {
        String usernamePath = "dict" + File.separator + "username.txt";
        String passwordPath = "dict" + File.separator + "password.txt";
        String userPassPath = "dict" + File.separator + "user_pass.txt";
        loadUserPassFile(usernamePath, passwordPath, false, null,null , false, "%USER%");
        loadUserPassFile(usernamePath, passwordPath, true, null,null , false, "%USER%");
        LinkedHashSet<UserPassPair> inputUserPassPairs = loadUserPassFile(null, null, false, userPassPath, ":", true, "%USER%");

        String hisUserPassPath = "dict" + File.separator + "history.txt";
        LinkedHashSet<UserPassPair> userPassPairs = excludeHistoryUserPassPairs(inputUserPassPairs, hisUserPassPath, ":");
        showUserPassPairs(userPassPairs);

        //替换用户名元素中的变量
        replaceUserMarkInPass(userPassPairs, "%USER%");

        for (UserPassPair userPassPair: userPassPairs){
            print_info(String.format("writed %s", userPassPair.toString(":")));
            writeUserPassPairToFile(hisUserPassPath, ":", userPassPair);
        }
    }
}
