package com.fuping.BrowserUtils;

import com.fuping.FXMLDocumentController;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.CustomProxyConfig;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.internal.InputElement;
import javafx.application.Platform;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.fuping.PrintLog.PrintLog.print_info;
import static com.fuping.LoadConfig.MyConst.ClearCookiesSetting;
import static com.fuping.LoadConfig.MyConst.BrowserProxySetting;

public class BrowserUitls {

    public static CustomProxyConfig getBrowserProxy(){
        //转换输入的代理格式
        BrowserProxySetting = BrowserProxySetting.replace("://","=");
        print_info(String.format("Proxy Will Setting [%s]", BrowserProxySetting));
        return new CustomProxyConfig(BrowserProxySetting);
    }

    public static void AutoClearAllCookies(Browser browser) {
        //清除cookie
        //参考 JxBrowser之五：清除cache和cookie以及SSL证书处理 https://www.yii666.com/article/677652.html
        if (ClearCookiesSetting){
            browser.getCookieStorage().deleteAll();
            List<Cookie> cookies = browser.getCookieStorage().getAllCookies();
            //print_info(String.format("Auto Clear Browser All Cookies ... %s", cookies.toString()));
        }
    }

    public static void OpenUrlWithLocalBrowser(String url) {
        try {
            URI uri = URI.create(url);
            Desktop dp = Desktop.getDesktop();
            if (dp.isSupported(Desktop.Action.BROWSE))
                dp.browse(uri);
        } catch (NullPointerException localNullPointerException) {
        } catch (Exception e) {
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }


}
