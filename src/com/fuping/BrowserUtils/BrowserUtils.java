package com.fuping.BrowserUtils;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.CookieStorage;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMDocument;
import com.teamdev.jxbrowser.chromium.dom.internal.Element;
import com.teamdev.jxbrowser.chromium.dom.internal.InputElement;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrowserUtils {
    //清除浏览器当前 Cookies
    public static void clearCookieStorage(Browser browser) {
        //清除cookie
        //参考 JxBrowser 之五：清除cache和cookie以及SSL证书处理 https://www.yii666.com/article/677652.html
        CookieStorage cookieStorage = browser.getCookieStorage();
        if (!cookieStorage.getAllCookies().isEmpty()){
            cookieStorage.deleteAll();
            cookieStorage.save();
        }
    }

    /**
     * 解析 cookie 字符串到一个 Map。
     * @param cookiesStr 待解析的 cookie 字符串。
     * @return 包含 cookie 名称和值的 Map。
     */
    public static Map<String, String> parseCookiesStr(String cookiesStr) {
        Map<String, String> cookies = new HashMap<>();
        Pattern pattern = Pattern.compile("[^; ]+=[^; ]+");
        Matcher matcher = pattern.matcher(cookiesStr);

        while (matcher.find()) {
            String cookieEntry = matcher.group();
            String[] keyValue = cookieEntry.split("=", 2); // 最多分割一次
            if (keyValue.length == 2) {
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            } else {
                // 处理没有值的情况（例如：SessionID=; Path=/）
                cookies.put(cookieEntry.trim(), "");
            }
        }
        return cookies;
    }

    /**
     * 从给定的 URL 中提取域名，并构造新的域名字符串。
     * @param originalUrl 原始 URL 字符串。
     * @param withHttpPrefix 是否在新域名前加上 "http://"。
     * @return 构造的新域名字符串。
     */
    public static String extractDomain(String originalUrl, boolean withHttpPrefix) {
        try {
            URL url = new URL(originalUrl);
            String protocol = url.getProtocol();
            String host = url.getHost();
            String portPart = url.getPort() > -1 ? ":" + url.getPort() : "";
            String domain = host + portPart;

            //需要考虑 80 443 端口，这两个情况应该不需要配置
            if (url.getPort() == 80 || url.getPort() == 443){
                domain = host;
            }

            // 构造新的域名
            if (withHttpPrefix) {
                return protocol + "://" + domain;
            } else {
                return domain;
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format", e);
        }
    }

    //设置浏览器当前Cookies 有些场景下需要先设置cookie才能访问登录页面
    public static void setBrowserCookies(Browser browser, String urlStr, String cookiesStr) {
        // Cookie 操作 ： https://teamdev.com/jxbrowser/docs/6/guides/cookies/

//        urlStr = "http://34.96.183.238:81/admin/public/doLogin.html";
//        cookiesStr = "Cookie: thinkphp_show_page_trace=0|0; PHPSESSID=cde568e4057972b8f1f1e7afae4baad1";

        if (urlStr == null|| cookiesStr == null  || urlStr.trim().isEmpty() || cookiesStr.trim().isEmpty()){
            System.out.println("未输入有效URL和Cookies字符串...");
            return;
        }

        //获取URL相关数据
        String target = extractDomain(urlStr,true);
        String domain = extractDomain(urlStr,false);

        if (domain==null||target==null||domain.trim().isEmpty()||target.trim().isEmpty()){
            System.out.println(String.format("解析 URL [%s] 结果为空!!!", urlStr));
            return;
        }

        //解析请求cookie数据
        Map<String, String> cookies = parseCookiesStr(cookiesStr);
        if (cookies == null || cookies.isEmpty()){
            System.out.println(String.format("解析 Cookies [%s] 结果为空!!!", cookiesStr));
            return;
        }

        // 设置Cookie
        CookieStorage cookieStorage = browser.getCookieStorage();
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            String cookieName = cookie.getKey();
            String cookieValue = cookie.getValue();
            System.out.println(String.format("Setting %s <-> %s  <-> %s:%s", target,domain,cookieName,cookieValue));

            //设置持久化Cookie
            cookieStorage.setCookie(target, cookieName, cookieValue, domain,"/", (System.currentTimeMillis() +  7 * 24 * 60 * 60) * 1000, false, false);
            //设置会话Cookie 设置失败
            //cookieStorage.setSessionCookie(target, cookieName, cookieValue, domain, "/", false, false);
        }
        //保存cookie设置
        cookieStorage.save();
        //输出Cookie设置
        //List<Cookie> allCookies = browser.getCookieStorage().getAllCookies(target);
        //输出指定URL的Cookies
        List<Cookie>  allCookies = browser.getCookieStorage().getAllCookies(target);
        System.out.println(String.format("New Cookies: size:%s <-> %s", allCookies.size(), cookiesToString(allCookies)));
    }

    //转换当前Cookies到字符串
    public static String cookiesToString(List<Cookie> cookies ) {
        StringBuilder cookiesStringBuilder = new StringBuilder();
        for (Cookie cookie:cookies){
//            String cookieString = String.format(
//                    "Cookie{name='%s', value='%s', domain='%s', path='%s', creationTime=%d, unixCreationTime=%d,
//                    expirationTime=%d, unixExpirationTime=%d, secure=%b, httpOnly=%b, session=%b}",
//                    cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(),
//                    cookie.getCreationTime(), cookie.getUnixCreationTime(), cookie.getExpirationTime(),
//                    cookie.getUnixExpirationTime(), cookie.isSecure(), cookie.isHTTPOnly(), cookie.isSession()
//            );
            String cookieString = String.format(
                    "Cookie{name='%s', value='%s', domain='%s', path='%s'",
                    cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath()
            );
            // 将每个 Cookie 的字符串添加到 StringBuilder 中，并在每个 Cookie 之间添加换行符
            cookiesStringBuilder.append(cookieString).append(System.lineSeparator());
        }
        // 返回拼接后的字符串
        return cookiesStringBuilder.toString();
    }

    public static void OpenUrlWithLocalBrowser(String url) {
        //使用系统浏览器打开网页
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

    public static InputElement findInputElementByOption(DOMDocument doc, String elementValue, String selectOption ) {
        //输入用户名元素 //需要添加输入XPath|css元素
        InputElement inputElement;
        switch (selectOption.toLowerCase()) {
            case "id":
                inputElement = (InputElement) doc.findElement(By.id(elementValue));
                break;
            case "name":
                inputElement = (InputElement) doc.findElement(By.name(elementValue));
                break;
            case "class":
                inputElement = (InputElement) doc.findElement(By.className(elementValue));
                break;
            case "css":
                inputElement = (InputElement) doc.findElement(By.cssSelector(elementValue));
                break;
            case "xpath":
            default:
                inputElement = (InputElement) doc.findElement(By.xpath(elementValue));
        }
        return inputElement;
    }


    public static Element findElementByOption(DOMDocument doc, String elementValue, String selectOption ) {
        //输入用户名元素 //需要添加输入XPath|css元素
        Element element;
        switch (selectOption.toLowerCase()) {
            case "id":
                element = (Element) doc.findElement(By.id(elementValue));
                break;
            case "name":
                element = (Element) doc.findElement(By.name(elementValue));
                break;
            case "class":
                element = (Element) doc.findElement(By.className(elementValue));
                break;
            case "css":
                element = (Element) doc.findElement(By.cssSelector(elementValue));
                break;
            case "xpath":
            default:
                element = (Element) doc.findElement(By.xpath(elementValue));
        }
        return element;
    }

}
