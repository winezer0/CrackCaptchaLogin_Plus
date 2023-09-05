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
    }


    public static MyConst initialize() {
        // 如果实例为null，则创建一个新的实例
        if (MyConstInstance == null) MyConstInstance = new MyConst();
        return MyConstInstance;
    }

    public static void main(String[] args) {
        // 获取单例实例
        MyConst MyConst1 = MyConst.initialize();
        MyConst MyConst2 = MyConst.initialize();
        // 检查是否是同一个实例
        System.out.println(MyConst1 == MyConst2); // 应该输出 true
    }

}
