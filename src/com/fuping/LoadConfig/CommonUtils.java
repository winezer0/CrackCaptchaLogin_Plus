package com.fuping.LoadConfig;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.CustomProxyConfig;

import java.util.List;

import static cn.hutool.core.util.StrUtil.isEmptyIfStr;
import static com.fuping.LoadConfig.ConstString.SettingClearCookie;
import static com.fuping.LoadConfig.ConstString.SettingProxyString;

public class CommonUtils {

    public static final String scrub(String var0) {
        return var0 == null ? null : var0.replace('\u001b', '.');
    }

    public static final void print_info(String var0) {
        System.out.println("[*] " + scrub(var0));
    }

    public static final void print_error(String var0) {
        System.err.println("[-] " + scrub(var0));
    }

    public static CustomProxyConfig getBrowserProxy(){
        String browserProxy = ConfigReader.getInstance().getString(SettingProxyString, null);
        if(!isEmptyIfStr(browserProxy)){
            //转换输入的代理格式
            browserProxy = browserProxy.replace("://","=");
            print_info(String.format("Proxy Will Setting [%s]", browserProxy));
            return new CustomProxyConfig(browserProxy);
        } else {
            print_info("Proxy Setting Not Enable");
            print_info("Actual Proxy String Should Like (http://foo:80;https://foo:80;ftp://foo:80;socks://foo:80)");
            print_info("Proxy String Should Like (http=foo:80;https=foo:80;ftp=foo:80;socks=foo:80) On jxbrowser 6.1.5");
            return null;
        }
    }

    public static void AutoClearAllCookies(Browser browser) {
        //清除cookie
        //参考 JxBrowser之五：清除cache和cookie以及SSL证书处理 https://www.yii666.com/article/677652.html
        if (ConfigReader.getInstance().isTrue(SettingClearCookie, false)){
            browser.getCookieStorage().deleteAll();
            List<Cookie> cookies = browser.getCookieStorage().getAllCookies();
            print_info(String.format("Auto Clear Browser All Cookies ... %s", cookies.toString()));
        }
    }
}



