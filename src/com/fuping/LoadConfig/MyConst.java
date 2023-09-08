package com.fuping.LoadConfig;

import com.fuping.LoadDict.UserPassPair;

public class MyConst {
    // 私有静态成员变量，用于保存单例实例
    private static MyConst MyConstInstance;

    public static UserPassPair[] UserPassPairsArray = null;

    //存储默认历史记录文件
    public static String HistoryFilePath = null ;
    public static String LogRecodeFilePath = null ;

    //设置配置参数名称
    public static String ProgramVersion = null ;

    //JxBrowser相关配置参数
    public static String BrowserProxySetting = null;
    public static boolean ClearCookiesSetting = false;
    public static String BrowserUserAgent = null;

    //字典文件相关参数
    public static String UserNameFile = null;
    public static String PassWordFile = null;

    public static String UserPassFile = null;
    public static String PairSeparator = null;
    public static String dict_compo_mode = null;      //指定字典组合方式 cartesian  pitchfork pair_file
    public static String str_pair_file = "pair_file";
    public static String str_pitchfork = "pitchfork";
    public static String str_cartesian = "cartesian"; //默认

    public static String UserMarkInPass = "%USER%";  //密码内的用户名变量
    public static boolean default_exclude_history = false;    //是否排除历史爆破记录

    public static long UserFileLastModified = 0;  //记录账号密码文件是否修改过,如果没有修改的话,就不重新读取
    public static long PassFileLastModified = 0;
    public static long PairFileLastModified = 0;

    //登录配置参数
    public static String default_login_url = null;

    public static String default_name_ele_value = null;
    public static String default_name_ele_type = null;

    public static String default_pass_ele_value = null;
    public static String default_pass_ele_type = null;

    public static String default_submit_ele_value = null;
    public static String default_submit_ele_type = null;

    public static boolean default_show_browser = false;

    public static int login_page_wait_time = 1000;  //登录页面加载后的等待时间

    //登录按钮点击后的等待时间
    public static boolean submit_auto_wait = true; //是否自动等待模式常量
    public static int submit_fixed_wait_time = 2000; //常规模式下点击提交按钮后的 固定的等待时间
    public static long SubmitAutoWaitLimit = 5000; //自动等待模式下的 超时等待时间设置
    public static long SubmitAutoWaitInterval = 500; //自动等待模式下的 每次等待时间

    public static String default_success_regex = null;
    public static String default_failure_regex = null;
    public static String default_captcha_regex = null;


    public static String default_captcha_url = null;
    public static String default_captcha_ele_value = null;
    public static String default_captcha_ele_type = null;

    public static boolean default_captcha_switch = false;
    public static boolean default_local_identify = false;

    //验证码识别相关配置变量
    public static String default_ident_time_out =null;  //验证码识别超时毫秒

    public static String default_ident_format_regex =null;  //验证码格式正则校验
    public static String default_ident_format_length =null; //验证码格式长度校验

    public static String default_remote_ident_url =null;  //远程模式 默认的API地址
    public static String default_remote_extract_regex =null; //远程模式 从响应中提取验证码的正则
    public static String default_remote_expected_status =null; //访问成功状态正则匹配
    public static String default_remote_expected_keywords =null;  //访问成功响应正则匹配


    public MyConst(){
        ConfigReader configReader = ConfigReader.getInstance();
        //读取版本号信息
        ProgramVersion = configReader.getString("program_version", "Unknown");

        //读取代理配置参数
        BrowserProxySetting = configReader.getString("browser_proxy", null);
        //浏览器UA设置
        BrowserUserAgent = configReader.getString("browser_ua", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        //是否清理Cookie
        ClearCookiesSetting = configReader.isTrue("clear_cookies", false);
        //加载账号密码、文件路径
        UserNameFile = configReader.getString("user_name_file", null);
        PassWordFile = configReader.getString("pass_word_file", null);
        //加载账号密码对文件路径
        UserPassFile = configReader.getString("user_pass_file", null);
        PairSeparator = configReader.getString("pair_separator", ":");
        //字典模式选择
        dict_compo_mode = configReader.getString("dict_compo_mode", str_cartesian);

        UserMarkInPass = configReader.getString("user_mark_in_pass", "%USER%");
        default_exclude_history = configReader.isTrue("exclude_history", false);

        //加载默认的登录框配置
        default_login_url = configReader.getString("login_url", "http://127.0.0.1/demo/index.php/Home/Login/login.html");
        default_name_ele_value = configReader.getString("name_ele_value", "username");
        default_name_ele_type = configReader.getString("name_ele_type", null);

        default_pass_ele_value = configReader.getString("pass_ele_value", "password");
        default_pass_ele_type = configReader.getString("pass_ele_type", null);

        default_submit_ele_value = configReader.getString("submit_ele_value", "login");
        default_submit_ele_type = configReader.getString("submit_ele_type", null);

        default_show_browser = configReader.isTrue("browser_show", false);

        login_page_wait_time = Integer.parseInt(configReader.getString("login_page_wait_time", "1000"));
        submit_auto_wait = configReader.isTrue("submit_auto_wait", true);
        submit_fixed_wait_time = Integer.parseInt(configReader.getString("submit_fixed_wait_limit", "2000"));
        SubmitAutoWaitLimit = Integer.parseInt(configReader.getString("submit_auto_wait_limit", "5000"));
        SubmitAutoWaitInterval = Integer.parseInt(configReader.getString("submit_auto_wait_interval", "500"));

        default_success_regex = configReader.getString("success_regex", "welcome");
        default_failure_regex = configReader.getString("failure_regex", "登录失败");
        default_captcha_regex = configReader.getString("captcha_regex", "验证码错误");

        default_captcha_switch = configReader.isTrue("captcha_switch", false);
        default_local_identify = configReader.isTrue("local_Identify", false);

        default_captcha_url = configReader.getString("captcha_url", null);
        default_captcha_ele_value = configReader.getString("captcha_ele_value", null);
        default_captcha_ele_type = configReader.getString("captcha_ele_type", null);

        default_ident_time_out = configReader.getString("ident_time_out", null);
        default_ident_format_regex = configReader.getString("ident_format_regex", null);
        default_ident_format_length = configReader.getString("ident_format_length", null);
        default_remote_ident_url = configReader.getString("remote_ident_url", null);
        default_remote_extract_regex = configReader.getString("remote_extract_regex", null);
        default_remote_expected_status = configReader.getString("remote_expected_status", null);
        default_remote_expected_keywords = configReader.getString("remote_expected_keywords", null);

    }


    public static MyConst initialize() {
        // 如果实例为null，则创建一个新的实例
        if (MyConstInstance == null) MyConstInstance = new MyConst();
        return MyConstInstance;
    }

    public static void main(String[] args) {
       MyConst.initialize();
    }

}
