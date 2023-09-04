package com.fuping.LoadDict;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.fuping.CommonUtils.Utils.*;
import static com.fuping.PrintLog.PrintLog.print_error;
import static com.fuping.PrintLog.PrintLog.print_info;

public class LoadDictUtils {

    public static List<String> readDictFile(String filePath) {
        // 读取文件内容到列表
        String absolutePath = getFileStrAbsolutePath(filePath);

        //判断文件是否存在
        if (!isNotEmptyFile(absolutePath)){
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


    public static HashSet<UserPassPair> createPitchforkUserPassPairs(List<String> usernames, List<String> passwords) {
        //创建 pitchfork 模式的用户密码对
        int minSize = Math.min(usernames.size(), passwords.size());
        HashSet<UserPassPair> userPassPairs = new HashSet<>();
        for (int i = 0; i < minSize; i++) {
            String username = usernames.get(i);
            String password = passwords.get(i);
            userPassPairs.add(new UserPassPair(username.trim(), password.trim()));
        }
        print_info(String.format("Create Pitchfork User Pass Pairs [%s]", userPassPairs.size()));
        return userPassPairs;
    }

    public static HashSet<UserPassPair> createCartesianUserPassPairs(List<String> usernames, List<String> passwords) {
        //创建 笛卡尔积 模式的用户密码对
        HashSet<UserPassPair> userPassPairs = new HashSet<>();

        for (String username : usernames) {
            for (String password : passwords) {
                userPassPairs.add(new UserPassPair(username.trim(), password.trim()));
            }
        }
        print_info(String.format("Create Cartesian User Pass Pairs [%s]", userPassPairs.size()));
        return userPassPairs;
    }

    public static HashSet<UserPassPair> splitAndCreatUserPassPairs(List<String> stringList, String separator) {
        //拆分账号密钥对文件 到用户名密码字典
        HashSet<UserPassPair> userPassPairs = new HashSet<>();
        for (String str : stringList) {
            // 使用 split 方法按冒号分割字符串
            String[] parts = str.split(separator, 2);
            if (parts.length == 2) {
                String username = parts[0];
                String password = parts[1];
                userPassPairs.add(new UserPassPair(username.trim(), password.trim()));
            }
        }
        print_info(String.format("Split And Creat User Pass Pairs [%s]", userPassPairs.size()));
        return userPassPairs;
    }

    public static HashSet<UserPassPair> loadUserPassFile(String userNameFile, String passWordFile, boolean pitchforkMode, String userPassFile, String pair_separator, boolean userPassMode){
        //判断是加载账号密码对字典还是加载账号字典
        HashSet<UserPassPair> userPassPairs = new HashSet<>();

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
        return userPassPairs;
    }

    public static void main(String[] args) {
        String usernamePath = "dict" + File.separator + "username.txt";
        String passnamePath = "dict" + File.separator + "password.txt";
        String userPassPath = "dict" + File.separator + "user_pass.txt";
        loadUserPassFile(usernamePath, passnamePath, false, null,null , false);
        loadUserPassFile(usernamePath, passnamePath, true, null,null , false);
        loadUserPassFile(null, null, false, userPassPath,":" , true);
    }
}
