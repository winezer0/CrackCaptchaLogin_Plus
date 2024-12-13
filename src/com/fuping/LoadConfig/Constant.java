package com.fuping.LoadConfig;

public class Constant {

//    public static final String LOADING_START = "loading_start";
//    public static final String LOADING_FINISH = "loading_finish";
//    public static final String LOADING_FAILED = "loading_failed";
//    public static final String LOADING_UNKNOWN = "loading_unknown"; //在没有获取到状态时使用


    // 定义 ActionStatus 枚举
    public enum LoadStatus {
        LOADING_START, LOADING_FINISH, LOADING_FAILED, LOADING_UNKNOWN
    }

//    public static final String LOGIN_SUCCESS = "login_success";  //登录成功
//    public static final String LOGIN_FAILURE = "login_failure";  //登录失败
//    public static final String ERROR_CAPTCHA = "error_captcha";  //验证码错误常量

    // 定义 ActionStatus 枚举
    public enum LoginStatus {
        LOGIN_SUCCESS, LOGIN_FAILURE, ERROR_CAPTCHA
    }
    // 定义 DictMode 枚举
    public enum DictMode {
        // "pair_file"; //账号密码对文件
        // "pitchfork"; //账号密码对应模式，要求账号和密码文件数量相同，一般不用
        // "cartesian"; //默认 交叉模式 常用

        PAIR_FILE, PITCHFORK, CARTESIAN;

        // 自定义从字符串创建枚举的方法，处理大小写不敏感的情况
        public static DictMode fromString(String text) {
            if (text != null) {
                for (DictMode b : DictMode.values()) {
                    if (text.equalsIgnoreCase(b.name())) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No matching enum found for text: " + text);
        }
    }

    // 定义 ActionStatus 枚举
    public enum EleFoundStatus {
        SUCCESS, FAILURE, BREAK, CONTINUE;
        // 自定义从字符串创建枚举的方法，处理大小写不敏感的情况
        public static EleFoundStatus fromString(String text) {
            if (text != null) {
                for (EleFoundStatus b : EleFoundStatus.values()) {
                    if (text.equalsIgnoreCase(b.name())) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("No matching enum found for text: " + text);
        }
    }
}
