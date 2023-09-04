package com.fuping.BrowserUtils;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.CustomProxyConfig;

import java.util.List;

import static com.fuping.PrintLog.PrintLog.print_info;
import static com.fuping.LoadConfig.MyConst.clearCookiesSetting;
import static com.fuping.LoadConfig.MyConst.browserProxySetting;

public class BrowserUitls {

    public static CustomProxyConfig getBrowserProxy(){
        //转换输入的代理格式
        browserProxySetting = browserProxySetting.replace("://","=");
        print_info(String.format("Proxy Will Setting [%s]", browserProxySetting));
        return new CustomProxyConfig(browserProxySetting);
    }

    public static void AutoClearAllCookies(Browser browser) {
        //清除cookie
        //参考 JxBrowser之五：清除cache和cookie以及SSL证书处理 https://www.yii666.com/article/677652.html
        if (clearCookiesSetting){
            browser.getCookieStorage().deleteAll();
            List<Cookie> cookies = browser.getCookieStorage().getAllCookies();
            print_info(String.format("Auto Clear Browser All Cookies ... %s", cookies.toString()));
        }
    }
}
