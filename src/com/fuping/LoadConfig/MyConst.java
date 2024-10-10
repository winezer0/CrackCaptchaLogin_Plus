package com.fuping.LoadConfig;

import com.fuping.LoadDict.UserPassPair;

import static com.fuping.PrintLog.PrintLog.print_info;

public class MyConst {
    // 私有静态成员变量，用于保存单例实例
    private static MyConst MyConstInstance;

    //本次全局变量命名规范,设置在UI上做初始值的加init或default,完整的全局变量加global
    public static UserPassPair[] globalUserPassPairsArray = null;

    //存储默认历史记录文件
    public static String globalCrackHistoryFilePath = null;   //记录成功爆破的历史
    public static String globalCrackLogRecodeFilePath = null;   //记录爆破记录

    public static String globalLoginSuccessFilePath = null;   //记录密码正确的记录
    public static String globalLoginFailureFilePath = null;   //记录密码错误的记录
    public static String globalErrorCaptchaFilePath = null;   //记录验证错误的记录

    //设置配置参数名称
    public static String globalProgramVersion = "NOVASEC 3.8.3 20241010" ;

    //JxBrowser相关配置参数
    public static String globalBrowserProxy = null;
    public static String globalBrowserUserAgent = null;
    public static boolean globalClearCookiesSwitch = false;

    //字典文件相关参数
    public static String globalUserNameFile = null;
    public static String globalPassWordFile = null;

    public static String globalUserPassFile = null;
    public static String globalPairSeparator = null;

    public static String default_dict_compo_mode = null;      //指定字典组合方式 cartesian  pitchfork pair_file

    public static final String const_pair_file = "pair_file"; //账号密码对文件
    public static final String const_pitchfork = "pitchfork"; //账号密码对应模式，要求账号和密码文件数量相同，一般不用
    public static final String const_cartesian = "cartesian"; //默认 交叉模式 常用

    public static String globalUserMarkInPass = "%USER%";  //密码内的用户名变量
    public static boolean globalExcludeHistorySwitch = false;    //是否排除历史爆破记录

    //记录账号密码文件是否修改过,如果没有修改的话,就考虑不重新读取
    public static long globalUserFileLastModified = 0;
    public static long globalPassFileLastModified = 0;
    public static long globalPairFileLastModified = 0;

    //登录配置参数
    public static String default_login_url = null;

    public static String default_name_ele_value = null;
    public static String default_name_ele_type = null;

    public static String default_pass_ele_value = null;
    public static String default_pass_ele_type = null;

    public static String default_submit_ele_value = null;
    public static String default_submit_ele_type = null;

    public static boolean default_show_browser_switch = false;

    public static boolean global_login_page_reload_per_time = true; //是否每次都重新加载登录页面
    public static int global_login_page_load_time = 30;  //登录页面加载超时时间
    public static boolean global_login_page_load_timeout_rework = true; //登录页面加载超时是否重头再来


    public static int default_login_page_wait_time = 1000;  //登录页面加载后的等待时间
    //登录按钮点击后的等待时间
    public static boolean default_submit_auto_wait_switch = true; //是否自动等待模式常量
    public static int default_submit_fixed_wait_time = 2000; //常规模式下点击提交按钮后的 固定的等待时间
    public static long global_submit_auto_wait_limit = 5000; //自动等待模式下的 超时等待时间设置
    public static long global_submit_auto_wait_interval = 500; //自动等待模式下的 每次等待时间

    //是否保存无法识别当前链接状态的情况
    public static boolean default_store_unknown_load_status = false;

    //响应判断的关键字正则
    public static String default_resp_key_success_regex = null;
    public static String default_resp_key_failure_regex = null;
    public static String default_resp_key_captcha_regex = null;


    public static String default_captcha_url = null;
    public static String default_captcha_ele_value = null;
    public static String default_captcha_ele_type = null;

    public static boolean default_ident_captcha_switch = false;
    public static boolean default_locale_identify_switch = false;

    //验证码识别相关配置变量
    public static int default_ident_time_out=1000;  //验证码识别超时毫秒

    public static String default_ident_format_regex=null;  //验证码格式正则校验
    public static String default_ident_format_length=null; //验证码格式长度校验

    public static String default_remote_ident_url =null;  //远程模式 默认的API地址
    public static String default_remote_extract_regex =null; //远程模式 从响应中提取验证码的正则
    public static String default_remote_expected_status =null; //访问成功状态正则匹配
    public static String default_remote_expected_keywords =null;  //访问成功响应正则匹配

    public static String globalLocaleTessDataName =null; //默认调用的数据集名称, 实际上就是tessdata目录下的文件名前缀


    //定义查找元素失败后的操作
    public static String const_browser_close_action;     //浏览器关闭后的动作  break
    public static String const_find_Ele_illegal_action;  //查找到不合法的动作时 continue
    public static String const_find_Ele_null_action;     //没有找到元素对应的操作 continue
    public static String const_find_Ele_exception_action; //发生其他异常时的动作 continue


    public MyConst(){
        ConfigReader configReader = ConfigReader.getInstance();
        //读取代理配置参数
        globalBrowserProxy = configReader.getString("browser_proxy", null);
        //浏览器UA设置
        globalBrowserUserAgent = configReader.getString("browser_ua", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        //是否清理Cookie
        globalClearCookiesSwitch = configReader.isTrue("clear_cookies", false);
        //加载账号密码、文件路径
        globalUserNameFile = configReader.getString("user_name_file", null);
        globalPassWordFile = configReader.getString("pass_word_file", null);
        //加载账号密码对文件路径
        globalUserPassFile = configReader.getString("user_pass_file", null);
        globalPairSeparator = configReader.getString("pair_separator", ":");
        //字典模式选择
        default_dict_compo_mode = configReader.getString("dict_compo_mode", const_cartesian);

        globalUserMarkInPass = configReader.getString("user_mark_in_pass", "%USER%");
        globalExcludeHistorySwitch = configReader.isTrue("exclude_history", false);

        //加载默认的登录框配置
        default_login_url = configReader.getString("login_url", "http://127.0.0.1/demo/index.php/Home/Login/login.html");
        default_name_ele_value = configReader.getString("name_ele_value", "username");
        default_name_ele_type = configReader.getString("name_ele_type", null);

        default_pass_ele_value = configReader.getString("pass_ele_value", "password");
        default_pass_ele_type = configReader.getString("pass_ele_type", null);

        default_submit_ele_value = configReader.getString("submit_ele_value", "login");
        default_submit_ele_type = configReader.getString("submit_ele_type", null);

        default_show_browser_switch = configReader.isTrue("browser_show", false);

        global_login_page_reload_per_time = configReader.isTrue("login_page_reload_per_time", true);
        global_login_page_load_time = Integer.parseInt(configReader.getString("login_page_load_time", "30"));
        global_login_page_load_timeout_rework = configReader.isTrue("login_page_load_timeout_rework", true);

        default_login_page_wait_time = Integer.parseInt(configReader.getString("login_page_wait_time", "1000"));
        default_submit_auto_wait_switch = configReader.isTrue("submit_auto_wait", true);
        default_submit_fixed_wait_time = Integer.parseInt(configReader.getString("submit_fixed_wait_limit", "2000"));
        global_submit_auto_wait_limit = Integer.parseInt(configReader.getString("submit_auto_wait_limit", "5000"));
        global_submit_auto_wait_interval = Integer.parseInt(configReader.getString("submit_auto_wait_interval", "500"));

        default_store_unknown_load_status = configReader.isTrue("store_unknown_load_status", false);

        default_resp_key_success_regex = configReader.getString("success_regex", "welcome");
        default_resp_key_failure_regex = configReader.getString("failure_regex", "登录失败");
        default_resp_key_captcha_regex = configReader.getString("captcha_regex", "验证码错误");

        default_ident_captcha_switch = configReader.isTrue("captcha_switch", false);
        default_locale_identify_switch = configReader.isTrue("locale_Identify", false);

        default_captcha_url = configReader.getString("captcha_url", null);
        default_captcha_ele_value = configReader.getString("captcha_ele_value", null);
        default_captcha_ele_type = configReader.getString("captcha_ele_type", null);

        default_ident_time_out = Integer.parseInt(configReader.getString("ident_time_out", "1000"));
        default_ident_format_regex = configReader.getString("ident_format_regex", null);
        default_ident_format_length = configReader.getString("ident_format_length", null);
        default_remote_ident_url = configReader.getString("remote_ident_url", null);
        default_remote_extract_regex = configReader.getString("remote_extract_regex", null);
        default_remote_expected_status = configReader.getString("remote_expected_status", null);
        default_remote_expected_keywords = configReader.getString("remote_expected_keywords", null);
        //指定默认本地识别数据集路径
        globalLocaleTessDataName = configReader.getString("locale_tess_data_name", null);

        //指定对应的操作
        const_browser_close_action = configReader.getString("browser_close_action", "break");
        const_find_Ele_illegal_action = configReader.getString("find_ele_illegal_action", "continue");
        const_find_Ele_null_action = configReader.getString("find_ele_null_action", "continue");
        const_find_Ele_exception_action = configReader.getString("find_ele_exception_action", "continue");

        print_info("Loaded Config Finish...");
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
