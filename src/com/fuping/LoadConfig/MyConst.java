package com.fuping.LoadConfig;

public class MyConst {
    // 私有静态成员变量，用于保存单例实例
    private static MyConst MyConstInstance;

    //存储默认历史记录文件
    public static String HistoryFilePath = null ;
    public static String LogRecodeFilePath = null ;

    //设置配置参数名称
    public static String ProgramVersion = "NOVA SEC 2.2 20230904 00:46" ;

    //JxBrowser相关配置参数
    public static String BrowserProxySetting = null;
    public static boolean ClearCookiesSetting = false;

    //字典文件相关参数
    public static String UserNameFile = null;
    public static String PassWordFile = null;
    public static boolean PitchforkMode = false;

    public static String UserPassFile = null;
    public static String PairSeparator = null;
    public static boolean UserPassMode = false;
    public static String UserMarkInPass = "%USER%";

    //登录配置参数
    public static String DefaultLoginUrl = null;

    public static String DefaultNameEleValue = null;
    public static String NameEleType = null;

    public static String DefaultPassEleValue = null;
    public static String PassEleType = null;

    public static String DefaultSubmitEleValue = null;
    public static String SubmitEleType = null;

    public static boolean DefaultShowBrowser = false;
    public static int DefaultLoadTimeSleep = 1500;

    public static String DefaultSuccessKey = null;
    public static String DefaultFailureKey = null;

    public static boolean DefaultIdentCaptcha = false;
    public static boolean DefaultLocalIdentify = false;

    public static String DefaultCaptchaUrl = null;
    public static String DefaultCaptchaEleValue = null;
    public static String DefaultCaptchaEleType = null;


    public MyConst(){
        ConfigReader configReader = ConfigReader.getInstance();
        //读取代理配置参数
        BrowserProxySetting = configReader.getString("browser_proxy", null);
        //是否清理Cookie
        ClearCookiesSetting = configReader.isTrue("clear_cookies", false);

        //加载账号密码、文件路径
        UserNameFile = configReader.getString("user_name_file", null);
        PassWordFile = configReader.getString("pass_word_file", null);
        PitchforkMode = configReader.isTrue("pitchfork_mode", false);
        //加载账号密码对文件路径
        UserPassFile = configReader.getString("user_pass_file", null);
        PairSeparator = configReader.getString("pair_separator", ":");
        UserPassMode = configReader.isTrue("user_pass_mode", false);

        UserMarkInPass = configReader.getString("user_mark_in_pass", "%USER%");

        //加载默认的登录框配置
        DefaultLoginUrl = configReader.getString("login_url", "http://127.0.0.1/demo/index.php/Home/Login/login.html");
        DefaultNameEleValue = configReader.getString("name_ele_value", "username");
        NameEleType = configReader.getString("name_ele_type", "id");

        DefaultPassEleValue = configReader.getString("pass_ele_value", "password");
        PassEleType = configReader.getString("pass_ele_type", "id");

        DefaultSubmitEleValue = configReader.getString("submit_ele_value", "login");
        SubmitEleType = configReader.getString("button_ele_type", "id");

        DefaultShowBrowser = configReader.isTrue("show_browser", false);
        DefaultLoadTimeSleep = Integer.parseInt(configReader.getString("load_time_sleep", "1500"));

        DefaultSuccessKey = configReader.getString("success_keyword", "welcome");
        DefaultFailureKey = configReader.getString("failure_keyword", "登录失败");

        DefaultIdentCaptcha = configReader.isTrue("ident_captcha", false);
        DefaultLocalIdentify = configReader.isTrue("local_Identify", false);

        DefaultCaptchaUrl = configReader.getString("captcha_url", "http://127.0.0.1/demo/index.php/Home/Login/verify.html");
        DefaultCaptchaEleValue = configReader.getString("captcha_ele_value", "verify");
        DefaultCaptchaEleType = configReader.getString("captcha_ele_type", "id");
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
