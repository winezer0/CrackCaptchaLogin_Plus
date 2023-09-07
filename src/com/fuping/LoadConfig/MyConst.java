package com.fuping.LoadConfig;

import com.fuping.LoadDict.UserPassPair;

public class MyConst {
    // 私有静态成员变量，用于保存单例实例
    private static MyConst MyConstInstance;

    public static UserPassPair[] UserPassPairsArray = null;

    //常量
    public static String str_pair_file = "pair_file";
    public static String str_pitchfork = "pitchfork";
    public static String str_cartesian = "cartesian"; //默认

    //存储默认历史记录文件
    public static String HistoryFilePath = null ;
    public static String LogRecodeFilePath = null ;

    //设置配置参数名称
    public static String ProgramVersion = null ;

    //JxBrowser相关配置参数
    public static String BrowserProxySetting = null;
    public static boolean ClearCookiesSetting = false;

    //字典文件相关参数
    public static String UserNameFile = null;
    public static String PassWordFile = null;
//    public static boolean PitchforkMode = false;

    public static String UserPassFile = null;
    public static String PairSeparator = null;
    public static String DictCompoMode = null;      //指定字典组合方式 cartesian  pitchfork pair_file
    public static String UserMarkInPass = "%USER%";  //密码内的用户名变量
    public static boolean ExcludeHistory = false;    //是否排除历史爆破记录

    public static long UserFileLastModified = 0;  //记录账号密码文件是否修改过,如果没有修改的话,就不重新读取
    public static long PassFileLastModified = 0;
    public static long PairFileLastModified = 0;

    //登录配置参数
    public static String DefaultLoginUrl = null;

    public static String DefaultNameEleValue = null;
    public static String DefaultNameEleType = null;

    public static String DefaultPassEleValue = null;
    public static String DefaultPassEleType = null;

    public static String DefaultSubmitEleValue = null;
    public static String DefaultSubmitEleType = null;

    public static boolean DefaultShowBrowser = false;
    public static int DefaultLoadTimeSleep = 1500;

    public static String DefaultSuccessRegex = null;
    public static String DefaultFailureRegex = null;
    public static String DefaultCaptchaRegex = null;

    public static boolean DefaultCaptchaSwitch = false;
    public static boolean DefaultLocalIdentify = false;

    public static String DefaultCaptchaUrl = null;
    public static String DefaultCaptchaEleValue = null;
    public static String DefaultCaptchaEleType = null;


    public MyConst(){
        ConfigReader configReader = ConfigReader.getInstance();
        //读取版本号信息
        ProgramVersion = configReader.getString("program_version", "Unknown");

        //读取代理配置参数
        BrowserProxySetting = configReader.getString("browser_proxy", null);
        //是否清理Cookie
        ClearCookiesSetting = configReader.isTrue("clear_cookies", false);

        //加载账号密码、文件路径
        UserNameFile = configReader.getString("user_name_file", null);
        PassWordFile = configReader.getString("pass_word_file", null);
        //加载账号密码对文件路径
        UserPassFile = configReader.getString("user_pass_file", null);
        PairSeparator = configReader.getString("pair_separator", ":");
        //字典模式选择
        DictCompoMode = configReader.getString("dict_compo_mode", str_cartesian);

        UserMarkInPass = configReader.getString("user_mark_in_pass", "%USER%");
        ExcludeHistory = configReader.isTrue("exclude_history", false);

        //加载默认的登录框配置
        DefaultLoginUrl = configReader.getString("login_url", "http://127.0.0.1/demo/index.php/Home/Login/login.html");
        DefaultNameEleValue = configReader.getString("name_ele_value", "username");
        DefaultNameEleType = configReader.getString("name_ele_type", null);

        DefaultPassEleValue = configReader.getString("pass_ele_value", "password");
        DefaultPassEleType = configReader.getString("pass_ele_type", null);

        DefaultSubmitEleValue = configReader.getString("submit_ele_value", "login");
        DefaultSubmitEleType = configReader.getString("submit_ele_type", null);

        DefaultShowBrowser = configReader.isTrue("browser_show", false);
        DefaultLoadTimeSleep = Integer.parseInt(configReader.getString("browser_sleep", "1500"));

        DefaultSuccessRegex = configReader.getString("success_regex", "welcome");
        DefaultFailureRegex = configReader.getString("failure_regex", "登录失败");
        DefaultCaptchaRegex = configReader.getString("captcha_regex", "验证码错误");

        DefaultCaptchaSwitch = configReader.isTrue("captcha_switch", false);
        DefaultLocalIdentify = configReader.isTrue("local_Identify", false);

        DefaultCaptchaUrl = configReader.getString("captcha_url", "http://127.0.0.1/demo/index.php/Home/Login/verify.html");
        DefaultCaptchaEleValue = configReader.getString("captcha_ele_value", "verify");
        DefaultCaptchaEleType = configReader.getString("captcha_ele_type", null);
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
