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
    public static String LoginUrl = null;

    public static String LoginNameEleValue = null;
    public static String LoginNameEleType = null;

    public static String LoginPassEleValue = null;
    public static String LoginPassEleType = null;

    public static String LoginButtonEleValue = null;
    public static String LoginButtonEleType = null;

    public static boolean ShowBrowser = false;
    public static int LoadTimeSleep = 1500;

    public static String LoginSuccessKeywords = null;
    public static String LoginFailureKeywords = null;

    public static boolean IdentCaptcha = false;
    public static boolean LocalIdentify = false;

    public static String LoginCaptchaUrl = null;
    public static String LoginCaptchaEleValue = null;
    public static String LoginCaptchaEleType = null;


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
        LoginUrl = configReader.getString("login_url", "http://127.0.0.1/demo/index.php/Home/Login/login.html");
        LoginNameEleValue = configReader.getString("login_name_ele_value", "username");
        LoginNameEleType = configReader.getString("login_name_ele_type", "id");

        LoginPassEleValue = configReader.getString("login_pass_ele_value", "password");
        LoginPassEleType = configReader.getString("login_pass_ele_type", "id");

        LoginButtonEleValue = configReader.getString("login_button_ele_value", "login");
        LoginButtonEleType = configReader.getString("login_button_ele_type", "id");

        ShowBrowser = configReader.isTrue("show_browser", false);
        LoadTimeSleep = Integer.parseInt(configReader.getString("load_time_sleep", "1500"));

        LoginSuccessKeywords = configReader.getString("login_success_keywords", "welcome");
        LoginFailureKeywords = configReader.getString("login_failure_keywords", "登录失败");

        IdentCaptcha = configReader.isTrue("ident_captcha", false);
        LocalIdentify = configReader.isTrue("local_Identify", false);

        LoginCaptchaUrl = configReader.getString("login_captcha_url", "http://127.0.0.1/demo/index.php/Home/Login/verify.html");
        LoginCaptchaEleValue = configReader.getString("login_captcha_ele_value", "verify");
        LoginCaptchaEleType = configReader.getString("login_captcha_ele_type", "id");
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
