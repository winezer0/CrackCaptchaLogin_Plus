package com.fuping.LoadConfig;

public class MyConst {
    // 私有静态成员变量，用于保存单例实例
    private static MyConst myConst;

    //设置配置参数名称
    public static String ProgramVersion = "NOVA SEC 2.2 20230904 00:46" ;

    //配置文件参数名
    public static String ProxyParam = "BrowserProxy";
    public static String ProxyValue = null;

    public static String ClearCookiesParam = "ClearCookie";
    public static boolean ClearCookiesValue = false;

    public MyConst(){
        ConfigReader configReader = ConfigReader.getInstance();
        //读取代理配置参数
        ProxyValue = configReader.getString(ProxyParam, null);
        //是否清理Cookie
        ClearCookiesValue = configReader.isTrue(ClearCookiesParam, false);
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
