package com.fuping.LoadConfig;

public class MyConst {
    // 私有静态成员变量，用于保存单例实例
    private static MyConst myConst;

    //设置配置参数名称
    public static String ProgramVersion = "NOVA SEC 2.2 20230904 00:46" ;

    //JxBrowser相关配置参数
    public static String browserProxySetting = null;
    public static boolean clearCookiesSetting = false;

    //字典文件相关参数
    public static String userNameFile = null;
    public static String passWordFile = null;
    public static boolean pitchforkMode = false;

    public static String userPassFile = null;
    public static String pairSeparator = null;
    public static boolean userPassMode = false;

    public static String userMarkInPass = "%USER%";

    public MyConst(){
        ConfigReader configReader = ConfigReader.getInstance();
        //读取代理配置参数
        browserProxySetting = configReader.getString("browser_proxy", null);
        //是否清理Cookie
        clearCookiesSetting = configReader.isTrue("clear_cookies", false);

        //加载账号密码、文件路径
        userNameFile = configReader.getString("user_name_file", null);
        passWordFile = configReader.getString("pass_word_file", null);
        pitchforkMode = configReader.isTrue("pitchfork_mode", false);
        //加载账号密码对文件路径
        userPassFile = configReader.getString("user_pass_file", null);
        pairSeparator = configReader.getString("pair_separator", ":");
        userPassMode = configReader.isTrue("user_pass_mode", false);

        userMarkInPass = configReader.getString("user_mark_in_pass", "%USER%");
    }


    public static MyConst initialize() {
        // 如果实例为null，则创建一个新的实例
        if (myConst == null) {  myConst = new MyConst();  }
        return myConst;
    }

    public static void main(String[] args) {
        // 获取单例实例
        MyConst MyConst1 = MyConst.initialize();
        MyConst MyConst2 = MyConst.initialize();
        // 检查是否是同一个实例
        System.out.println(MyConst1 == MyConst2); // 应该输出 true
    }

}
