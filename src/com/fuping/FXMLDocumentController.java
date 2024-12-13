package com.fuping;

import com.fuping.BrowserUtils.MyDialogHandler;
import com.fuping.CommonUtils.ElementUtils;
import com.fuping.CommonUtils.MyFileUtils;
import com.fuping.CommonUtils.SystemUtilization;
import com.fuping.LoadDict.UserPassPair;
import com.teamdev.jxbrowser.chromium.Callback;
import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMDocument;
import com.teamdev.jxbrowser.chromium.dom.internal.Element;
import com.teamdev.jxbrowser.chromium.dom.internal.InputElement;
import com.teamdev.jxbrowser.chromium.events.*;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import com.teamdev.jxbrowser.chromium.javafx.DefaultNetworkDelegate;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.http.util.ByteArrayBuffer;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.util.StrUtil.isEmptyIfStr;
import static com.fuping.BrowserUtils.BrowserUtils.*;
import static com.fuping.CaptchaIdentify.CaptchaUtils.LoadImageToFile;
import static com.fuping.CaptchaIdentify.RemoteApiIdent.remoteIndentCaptcha;
import static com.fuping.CaptchaIdentify.TesseractsLocaleIdent.localeIdentCaptcha;
import static com.fuping.CommonUtils.Utils.escapeString;
import static com.fuping.CommonUtils.Utils.urlRemoveQuery;
import static com.fuping.LoadConfig.MyConst.*;
import static com.fuping.LoadConfig.MyConst.ActionStatus.*;
import static com.fuping.LoadDict.LoadDictUtils.*;
import static com.fuping.PrintLog.PrintLog.*;

public class FXMLDocumentController implements Initializable {
    @FXML  //识别结果判断条件
    public TextField bro_id_ident_format_length_text;
    @FXML  //识别结果格式筛选
    public TextField bro_id_ident_format_regex_text;
    @FXML  //识别超时配置
    public ComboBox<Integer> bro_id_ident_time_out_combo;
    @FXML //远程识别接口地址
    public TextField bro_id_remote_ident_url_text;
    @FXML  //识别结果判断条件
    public TextField bro_id_remote_resp_is_ok_status_text;
    @FXML  //识别结果判断条件
    public TextField bro_id_remote_resp_is_ok_keywords_text;
    @FXML  //识别结果内容提取
    public TextField bro_id_remote_ident_result_extract_regex_text;

    //登录相关元素
    @FXML
    private TextField id_login_url_text;
    @FXML
    private TextField bro_id_user_ele_text;
    @FXML
    private TextField bro_id_pass_ele_text;
    @FXML
    private TextField bro_id_submit_ele_text;
    @FXML
    private ComboBox<String> bro_id_user_ele_type_combo;
    @FXML
    private ComboBox<String> bro_id_pass_ele_type_combo;
    @FXML
    private ComboBox<String> bro_id_submit_ele_type_combo;
    @FXML
    private TextField bro_id_success_regex_text;
    @FXML
    private TextField bro_id_failure_regex_text;

    //浏览器设置相关
    @FXML
    private ComboBox<Integer> bro_id_login_page_wait_time_combo;
    @FXML
    private ComboBox<Integer> bro_id_submit_fixed_wait_time_combo;
    @FXML
    public CheckBox bro_id_submit_auto_wait_check;

    @FXML
    public CheckBox bro_id_default_js_mode_check;

    @FXML //设置字典组合模式
    private ComboBox<String> bro_id_dict_compo_mode_combo;
    @FXML
    private CheckBox bro_id_show_browser_check;
    @FXML
    private CheckBox bro_id_exclude_history_check;

    //验证码元素相关
    @FXML
    public VBox bro_id_captcha_set_vbox;
    @FXML
    private CheckBox bro_id_captcha_switch_check;
    @FXML
    private CheckBox bro_id_store_unknown_status_check;
    @FXML
    private CheckBox bro_id_use_browser_proxy;
    @FXML
    private TextField bro_id_captcha_url_text;
    @FXML
    private ComboBox<String> bro_id_captcha_ele_type_combo;
    @FXML
    private TextField bro_id_captcha_ele_text;
    @FXML
    private RadioButton bro_id_yzm_remote_ident_radio;
    @FXML
    private RadioButton bro_id_locale_ident_flag_radio;
    @FXML
    private TextField bro_id_captcha_regex_text;

    //输出相关
    @FXML
    private TextArea bro_id_output_text_area;

    private Stage primaryStage;
    private byte[] captcha_data;

    //记录当前页面加载状态
    private String loading_status;
    private final String LOADING_START = "loading_start";
    private final String LOADING_FINISH = "loading_finish";
    private final String LOADING_FAILED = "loading_failed";
    private final String LOADING_UNKNOWN = "loading_unknown"; //在没有获取到状态时使用

    private String crack_status;
    private final String LOGIN_SUCCESS = "login_success";  //登录成功
    private final String LOGIN_FAILURE = "login_failure";  //登录失败
    private final String ERROR_CAPTCHA = "error_captcha";  //验证码错误常量

    private String base_login_url = null;  //设置当前登录url的全局变量用于后续调用
    private List<String> login_about_urls = null;  //存储当前URL相关的多个URl

    private String base_captcha_url = null;  //设置当前登录验证码url用于后续调用
    private boolean captcha_ident_was_error = false; //设置当前验证码识别错误状态

    private Browser browser = null;

    private String login_url_protocol = null; //记录当前登录URL的协议类型,用于后续http/https的纠正使用

    //元素查找方法
    private boolean executeJavaScriptMode = false;

    //一些工具类方法
    public void setWithCheck(Object eleObj, Object Value) {
        if (Value != null) {
            if (eleObj instanceof TextField) {
                //输入文本框的类型
                TextField textField = (TextField) eleObj;
                textField.setText((String) Value);
            } else if (eleObj instanceof CheckBox){
                //勾选框的类型
                CheckBox checkBox = (CheckBox) eleObj;
                checkBox.setSelected((Boolean) Value);
            }  else if (eleObj instanceof ComboBox) {
                // 组合框 下拉列表框的类型
                ComboBox comboBox = (ComboBox) eleObj;
                comboBox.setValue(Value);
            } else if (eleObj instanceof RadioButton) {
                //单选按钮
                RadioButton radioButton = (RadioButton) eleObj;
                radioButton.setSelected((Boolean) Value);
            } else {
                print_error(String.format("The element type is not supported yet [%s] -> [%s]",eleObj,Value));
            }
        }
    }

    private void printlnDebugOnUIAndConsole(String appendTextToUI) {
        print_debug(appendTextToUI);
/*
        Platform.runLater(new Runnable() {
            public void run() {
                FXMLDocumentController.this.bro_id_output_text_area.appendText(String.format("[*] %s\n", appendTextToUI));
            }
        });
*/
    }

    private void printlnInfoOnUIAndConsole(String appendTextToUI) {
        print_info(appendTextToUI);
        Platform.runLater(new Runnable() {
            public void run() {
                FXMLDocumentController.this.bro_id_output_text_area.appendText(String.format("[+] %s\n", appendTextToUI));
            }
        });
    }

    private void printlnErrorOnUIAndConsole(String appendText) {
        print_error(appendText);
        Platform.runLater(new Runnable() {
            public void run() {
                FXMLDocumentController.this.bro_id_output_text_area.appendText(String.format("[-] %s\n", appendText));
            }
        });
    }

    /**
     * 查找元素并输入
     * @ browser_close_action 浏览器关闭时的异常动作
     * @ find_ele_illegal_action 页面中元素操作异常的动作
     * @ find_ele_null_action 页面中没有找到元素的动作
     * @ find_ele_exception_action 页面中元素操作其他异常的动作
     * @param document 页面的文档对象
     * @param locate_info 定位信息
     * @param selectedOption 定位选项
     * @param input_string 输入值
     * @return
     */
    private ActionStatus findElementAndInput(DOMDocument document, String locate_info, String selectedOption, String input_string) {
        ActionStatus action_string = SUCCESS;
        try {
            InputElement findElement = findInputElementByOption(document, locate_info, selectedOption);
            Map<String, String> attributes = findElement.getAttributes();
            //findElement.click(); // 尝试前后新增 .click() 解决部分场景内容输入后提示没有内容的问题 无效果
            findElement.setValue(input_string);
            // for (String attrName : attributes.keySet()) { System.out.println(attrName + " = " + attributes.get(attrName)); }
        }
        catch (IllegalStateException illegalStateException) {
            String eMessage = illegalStateException.getMessage();
            System.out.println(eMessage);
            if (eMessage.contains("Channel is already closed")) {
                action_string = ActionStatus.fromString(BROWSER_CLOSE_ACTION);
                printlnErrorOnUIAndConsole(String.format("浏览器已关闭 (IllegalStateException) 动作:[%s]", action_string));
            }else {
                illegalStateException.printStackTrace();
                action_string = ActionStatus.fromString(FIND_ELE_ILLEGAL_ACTION);
                printlnErrorOnUIAndConsole(String.format("illegal State Exception 动作:[%s]", action_string));
            }
        } catch (NullPointerException nullPointerException) {
            action_string = ActionStatus.fromString(FIND_ELE_NULL_ACTION);
            printlnErrorOnUIAndConsole(String.format("定位元素失败 (nullPointerException) 动作:[%s]", action_string));
        } catch (Exception exception) {
            exception.printStackTrace();
            action_string = ActionStatus.fromString(FIND_ELE_EXCEPTION_ACTION);
            printlnErrorOnUIAndConsole(String.format("未知定位异常 (unknown exception) 动作:[%s]", action_string));
        }
        return action_string;
    }


    /**
     * 通过浏览器JS代码执行器来输入元素操作
     * @param browser
     * @param locateInfo
     * @param locateType
     * @param inputText
     * @return
     */
    public ActionStatus setInputValueByJS(Browser browser, String locateInfo, String locateType, String inputText) {
        ActionStatus action_string;
        String jsCode = null;
        switch (locateType.toLowerCase()) {
            case "css":
                // JavaScript code to find an element by CSS selector and set its value.
                jsCode = "function setInputValueByCSS(cssSelector, value) {" +
                        "   try {" +
                        "       var node = document.querySelector(cssSelector);" +
                        "       if (node && node instanceof HTMLElement) {" +
                        "           node.value = value;" +
                        "           var event = new Event('input', { 'bubbles': true, 'cancelable': true });" +
                        "           node.dispatchEvent(event);" +
                        "           return { success: true, message: 'Input successful.' };" +
                        "       } else {" +
                        "           return { success: false, message: 'Element not found or not an HTML element.' };" +
                        "       }" +
                        "   } catch (error) {" +
                        "       return { success: false, message: error.message };" +
                        "   }" +
                        "}" +
                        "setInputValueByCSS('" + locateInfo.replace("'", "\\'") + "', '" + inputText.replace("'", "\\'") + "');";
                break;
            case "xpath":
                // JavaScript code to find an element by XPath and set its value.
                jsCode = "function setInputValueByXPath(xpath, value) {" +
                        "   try {" +
                        "       var result = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null);" +
                        "       var node = result.singleNodeValue;" +
                        "       if (node && node instanceof HTMLElement) {" +
                        "           node.value = value;" +
                        "           var event = new Event('input', { 'bubbles': true, 'cancelable': true });" +
                        "           node.dispatchEvent(event);" +
                        "           return { success: true, message: 'Input successful.' };" +
                        "       } else {" +
                        "           return { success: false, message: 'Element not found or not an HTML element.' };" +
                        "       }" +
                        "   } catch (error) {" +
                        "       return { success: false, message: error.message };" +
                        "   }" +
                        "}" +
                        "setInputValueByXPath('" + locateInfo.replace("'", "\\'") + "', '" + inputText.replace("'", "\\'") + "');";
                break;
            case "id":
            case "name":
            case "class":
            default:
                printlnErrorOnUIAndConsole("Js Mode Not Support [id|name|class] Mode!!! Please Input [CSS] or [XPATH] Locate Info Again.");
                break;
        }

        if (jsCode == null){
            printlnErrorOnUIAndConsole(String.format("Js Mode Only Support [css|xpath] Mode!!! You Input Mode is [%s]", locateType));
            return BREAK;
        }

        try {
            // Execute the JavaScript code in the context of the currently loaded web page and get the return value.
            JSValue jsValue = browser.executeJavaScriptAndReturnValue(jsCode);
            // Check if the returned JSValue is an object and contains expected properties.
            if (jsValue.isObject()) {
                JSValue success = jsValue.asObject().getProperty("success");
                JSValue message = jsValue.asObject().getProperty("message");
                if (success.isBoolean() && message.isString()) {
                    JSBoolean isSuccess = success.asBoolean();
                    // Print the message for debugging purposes.
                    // System.out.println(isSuccess.getValue(), msg);
                    if (isSuccess.getValue()) {
                        //定位并输入元素成功
                        action_string = SUCCESS;
                    } else {
                        action_string = fromString(FIND_ELE_NULL_ACTION);
                        String msg = message.asString().getValue();
                        printlnErrorOnUIAndConsole(String.format("定位元素失败 (影响结果false) 动作:[%s] MSG[%s]", action_string, msg));
                    }
                } else {
                    action_string = fromString(FIND_ELE_NULL_ACTION);
                    String msg = message.asString().getValue();
                    printlnErrorOnUIAndConsole(String.format("定位元素失败 (响应格式非预期) 动作:[%s] MSG[%s]", action_string, msg));
                }
                return action_string;
            }

            // If we reach here, something unexpected happened.
            action_string = ActionStatus.fromString(FIND_ELE_NULL_ACTION);
            printlnErrorOnUIAndConsole(String.format("未知定位异常 (JS执行结果格式未知) 动作:[%s]", action_string));
        } catch (Exception e){
            // If we reach here, something unexpected happened.
            action_string = CONTINUE;
            printlnErrorOnUIAndConsole(String.format("未知定位异常 (JS执行发生未知错误) 动作:[%s] ERROR:[%s]", action_string, e.getMessage()));
        }
        return action_string;
    }


    /***
     * 支持重试的元素查找方案
     * maxRetries 尝试次数
     * retryInterval 重试间隔时间，单位：毫秒
     */
    private ActionStatus findElementAndInputWithRetries(DOMDocument document, String locateInfo, String selectedOption, String inputString, int maxRetries, long retryInterval) {

        int retries = 0;
        ActionStatus action_status = ActionStatus.FAILURE;

        while (!SUCCESS.equals(action_status) && retries < maxRetries) {
            action_status = findElementAndInput(document, locateInfo, selectedOption, inputString);
            if (SUCCESS.equals(action_status)) { break; }

            // 延迟500毫秒后重试
            try {
                TimeUnit.MILLISECONDS.sleep(retryInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted during sleep.");
            }

            retries++;
        }
        return action_status;
    }

    //识别验证码的函数, 便于合并
    private String identCaptcha(boolean remoteIdent, String imagePath, byte[] imageBytes){
        //获取验证码筛选条件
        String ident_format_length = this.bro_id_ident_format_length_text.getText(); //期望长度
        String ident_format_regex = this.bro_id_ident_format_regex_text.getText(); //期望格式
        Integer ident_time_out = this.bro_id_ident_time_out_combo.getValue();

        //记录验证码识别结果
        String captcha_indent_text = null;
        //远程识别模式
        if(remoteIdent){
            //获取是被接口信息接口URL
            String remote_ident_url_text = this.bro_id_remote_ident_url_text.getText().trim();
            if (isEmptyIfStr(remote_ident_url_text)){
                this.bro_id_remote_ident_url_text.requestFocus();
                return null;
            }
            //获取响应判断条件
            String remote_resp_is_ok_status = this.bro_id_remote_resp_is_ok_status_text.getText(); //期望的 远程OCR接口  正常响应状态码 如 200
            String remote_resp_is_ok_keywords = this.bro_id_remote_resp_is_ok_keywords_text.getText(); //期望的 远程OCR接口 正常响应关键字 默认空

            //响应结果正则提取
            String ident_result_extract_regex = this.bro_id_remote_ident_result_extract_regex_text.getText(); //从响应中提取验证码结果的正则 默认获取全部

            try {
                //识别图片地址
                if (ElementUtils.isNotEmptyObj(imagePath)){
                    captcha_indent_text = remoteIndentCaptcha(
                            imagePath,
                            remote_ident_url_text,
                            remote_resp_is_ok_status,
                            remote_resp_is_ok_keywords,
                            ident_result_extract_regex,
                            ident_format_regex,
                            ident_format_length,
                            ident_time_out
                    );
                } else {
                    captcha_indent_text = remoteIndentCaptcha(imageBytes,
                            remote_ident_url_text,
                            remote_resp_is_ok_status,
                            remote_resp_is_ok_keywords,
                            ident_result_extract_regex,
                            ident_format_regex,
                            ident_format_length,
                            ident_time_out
                    );
                }
                printlnInfoOnUIAndConsole(String.format("验证码 远程识别结果: [%s]", captcha_indent_text));
            }catch (Exception e){
                printlnErrorOnUIAndConsole(String.format("验证码 远程识别错误: [%s]", e.getMessage()));
                e.printStackTrace();
            }
        } else {
            try {
                if (ElementUtils.isNotEmptyObj(imagePath)){
                    captcha_indent_text = localeIdentCaptcha(imagePath, ident_format_regex, ident_format_length, globalLocaleTessDataName);
                } else {
                    captcha_indent_text = localeIdentCaptcha(imageBytes, ident_format_regex, ident_format_length, globalLocaleTessDataName);
                }
                printlnDebugOnUIAndConsole(String.format("验证码 本地识别结果: [%s]", captcha_indent_text));
            } catch (Exception e) {
                printlnErrorOnUIAndConsole(String.format("验证码 本地识别出错: [%s]", e.getMessage()));
                e.printStackTrace();
            }
        }
        return captcha_indent_text;
    }

    //浏览器动作配置
    private Browser initJxBrowserInstance() {
        //创建窗口对象 JavaFX的Stage类是JavaFX应用程序创建窗口的基础
        this.primaryStage = new Stage();

        //在创建任何浏览器实例之前，只能修改一次用户代理字符串。
        // 可以使用BrowserPreferences.setUserAgent（String userAgent）方法
        // 或通过 jxbrowser.chromium.user-agent Java System属性提供用户代理字符串：
        BrowserPreferences.setUserAgent(globalBrowserUserAgent);

        //创建浏览器对象 轻量级对象 BrowserType.LIGHTWEIGHT 轻量级渲染模式， BrowserType.HEAVYWEIGHT重量级渲染模式。
        //轻量级渲染模式是通过CPU来加速渲染的，速度更快，占用更少的内存。在轻量级渲染模式下，Chromium引擎会在后台使用CPU渲染网页，然后将网页的图像保存在共享内存中。
        //重量级渲染模式则使用GPU加速渲染，相对于轻量级模式来说，它需要占用更多的内存，但在某些场景下可能会有更好的性能和更高的渲染质量。
        Browser browser = new Browser(BrowserType.LIGHTWEIGHT);

        ////浏览器首选项设置
        //BrowserPreferences preferences = browser.getPreferences();
        //preferences.setImagesEnabled(false);
        //preferences.setJavaScriptEnabled(false);
        //browser.setPreferences(preferences);

        //着浏览器视图将占据布局容器的中心区域，并自动适应大小。
        BrowserView view = new BrowserView(browser); //将 JxBrowser 的浏览器引擎 browser 嵌入到 JavaFX 中
        BorderPane borderPane = new BorderPane(view); //着浏览器视图将占据布局容器的中心区域，并自动适应大小。

        //创建了一个进度指示器，该指示器可以用于显示任务的进度状态，并通过设置首选高度和宽度来调整其在界面中的大小
        ProgressIndicator progressIndicator = new ProgressIndicator(1.0D);
        progressIndicator.setPrefHeight(30.0D);
        progressIndicator.setPrefWidth(30.0D);

        //创建一个文本输入框 用于输入URL的输入框，用户可以在其中键入网址。
        TextField url_input = new TextField();
        //进度指示器  和 文本输入框 。这两个子节点之间的水平间隔为 2.0D。
        HBox Hbox = new HBox(2.0D, new Node[]{progressIndicator, url_input});
        //文本输入框将始终尝试占据额外的水平空间，以充分利用可用的宽度
        HBox.setHgrow(url_input, Priority.ALWAYS);
        //子节点的水平对齐方式被设置为 Pos.CENTER_LEFT，这表示子节点在容器内水平居左对齐。
        Hbox.setAlignment(Pos.CENTER_LEFT);
        //将 进度指示器和URL输入框 放在 用户界面的顶部部分
        borderPane.setTop(Hbox);

        //创建了一个场景（Scene）对象 宽度为 800.0 像素，高度为 700.0 像素
        Scene scene = new Scene(borderPane, 800.0D, 700.0D);
        //primaryStage 是 JavaFX 应用程序的顶层窗口，指定要在主窗口中显示的用户界面内容。
        this.primaryStage.setScene(scene);
        //设置了主舞台的标题，标题将显示在窗口的标题栏上。
        this.primaryStage.setTitle("请勿操作浏览器页面");

        //是否显示浏览器框//应该放在后面处理
        if (this.bro_id_show_browser_check.isSelected()) { this.primaryStage.show(); }

        //在浏览器中处理弹出窗口的情况，将弹出窗口的内容加载到父级容器中。这对于控制浏览器行为或执行自动化测试时可能会很有用。
        PopupHandler popupHandler = new PopupHandler() {
            public PopupContainer handlePopup(PopupParams paramPopupParams) {
                paramPopupParams.getParent().loadURL(paramPopupParams.getURL());
                return null;
            }
        };
        browser.setPopupHandler(popupHandler);

        //确保在关闭应用程序时释放浏览器资源，并设置了自定义的对话框处理程序，以控制网页中对话框的行为。
        this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                browser.dispose();
            }
        });
        browser.setDialogHandler(new MyDialogHandler(view));

        //添加页面加载监听事件
        browser.addLoadListener(new LoadAdapter() {
            @Override
            //预加载触发事件||表示加载即将开始
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
                //页面首次加载|| 页面中的iframe或其他嵌入式框架开始加载新内容||用户点击链接、提交表单等操作导致页面或其部分重新加载。
                if (event.isMainFrame())
                    Platform.runLater(new Runnable() {
                        public void run() {
                            url_input.setText(event.getURL());
                        }
                    });
            }

            @Override
            //加载中触发事件||在实际接收到数据时触发
            public void onStartLoadingFrame(StartLoadingEvent event) {
                //在浏览器开始接收到框架的数据时被调用，意味着网络连接已经建立，并且服务器已经开始发送响应数据。
                if (event.isMainFrame()){
                    Platform.runLater(new Runnable() {
                        public void run() {
                            //输出加载中记录输出两次 UI重复,不是错误,是浏览器实际进行了主页和登录请求两次
                            String validatedURL = event.getValidatedURL();
                            loading_status = String.format("%s<->%s", LOADING_START, validatedURL);
                            printlnDebugOnUIAndConsole(String.format("Loading Status: %s %s", LOADING_START, validatedURL));
                            progressIndicator.setProgress(-1.0D);
                        }
                    });
                }
                super.onStartLoadingFrame(event);
            }

            @Override
            //加载失败触发事件
            public void onFailLoadingFrame(FailLoadingEvent event) {
                //尝试加载某个框架的内容但未能成功时
                if (event.isMainFrame())
                    Platform.runLater(new Runnable() {
                        public void run() {
                            String validatedURL = event.getValidatedURL();
                            loading_status = String.format("%s<->%s", LOADING_FAILED, validatedURL);
                            printlnErrorOnUIAndConsole(String.format("Loading Status: %s %s", LOADING_FAILED, validatedURL));
                            progressIndicator.setProgress(1.0D);
                        }
                    });
                super.onFailLoadingFrame(event);
            }

            @Override
            //加载完成触发事件
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                //当浏览器成功加载完某个框架的所有内容时，包括HTML、CSS、JavaScript等资源时触发
                if (event.isMainFrame()) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            String validatedURL = event.getValidatedURL();
                            loading_status = String.format("%s<->%s", LOADING_FINISH, validatedURL);
                            printlnDebugOnUIAndConsole(String.format("Loading Status: %s %s", LOADING_FINISH, validatedURL));
                            progressIndicator.setProgress(1.0D);
                        }
                    });
                }
                super.onFinishLoadingFrame(event);
            }

            @Override
            //主框架和子框架加载完成调用事件
            public void onDocumentLoadedInFrame(FrameLoadEvent event) {
                //在页面中任何框架（包括主框架和嵌套的子框架如 iframe）的 DOM 文档加载完成后被调用。
                printlnInfoOnUIAndConsole("Frame document is loaded.");
            }

            @Override
            //主框架加载完成调用事件
            public void onDocumentLoadedInMainFrame(LoadEvent event) {
                //仅在主框架（即整个页面）的DOM文档加载完成后被调用，而不考虑其他嵌套的子框架。
                printlnInfoOnUIAndConsole("Main frame document is loaded.");
            }

        });

        //addStatusListener状态监听器 允许开发者监听浏览器的各种状态变化、可以捕获到诸如导航开始、导航完成、加载进度更新等事件，
        //这对于实现自定义的用户界面反馈（如显示加载进度条）或执行特定逻辑（如在页面加载完成后执行某些操作）非常有用。
        //addStatusListener状态监听器 // 没有获取到任何数据 //放弃使用//

        return browser;
    }

    //定义是否停止所有爆破动作
    private boolean stopCrackStatus = false;
    @FXML //停止爆破动作
    public void stopCrack(ActionEvent actionEvent) {
        stopCrackStatus=true;
        printlnInfoOnUIAndConsole("已点击停止按钮,请等待停止信号传递...");
    }

    //修改浏览器代理配置
    @FXML
    public void change_browser_proxy_action(ActionEvent actionEvent) {
        printlnInfoOnUIAndConsole("已点击修改浏览器代理配置,请等待修改信号传递...");
        setBrowserProxyMode(browser, this.bro_id_use_browser_proxy.isSelected(), globalBrowserProxyStr, login_url_protocol);
    }

    @FXML
    public void change_js_mode_action(ActionEvent actionEvent) {
        printlnInfoOnUIAndConsole("已点击修改元素查找模式,请等待修改信号传递...");
        executeJavaScriptMode = this.bro_id_default_js_mode_check.isSelected();
    }

    @FXML
    public void change_submit_auto_wait_action(ActionEvent actionEvent) {
        //点击了自动等待就关闭手动设置等待的功能
        this.bro_id_submit_fixed_wait_time_combo.setDisable(bro_id_submit_auto_wait_check.isSelected());
    }

    public class MyNetworkDelegate extends DefaultNetworkDelegate {
        private boolean isCompleteAuth;
        private boolean isCancelAuth;
        private String base_url;
        //private long current_id;
        private ByteArrayBuffer captchaBytes = new ByteArrayBuffer(4096);

        public MyNetworkDelegate(String captcha_url) {
            int i = captcha_url.indexOf("?");
            if (i != -1)
                this.base_url = captcha_url.substring(0, i);
            else
                this.base_url = captcha_url;
        }

//        @Override
//        public void onBeforeURLRequest(BeforeURLRequestParams params) {
//            printlnInfoOnUIAndConsole(String.format("[%s] [%s]",params.getRequestId(), params.getMethod(), params.getURL()));
//        }

        @Override
        public void onBeforeSendHeaders(BeforeSendHeadersParams paramBeforeSendHeadersParams) {
            //发送请求前动作
            String getReqURL = paramBeforeSendHeadersParams.getURL();
            //需要判断那think php的情况，baseurl都是一样的，不能作为验证码图片URL
            if (base_captcha_url != null && ElementUtils.isSimilarLink(getReqURL, base_captcha_url)) {
                this.captchaBytes.clear();
                paramBeforeSendHeadersParams.getHeadersEx().setHeader("Accept-Encoding", "");
                print_debug(String.format("正在发起验证码请求 BeforeSendHeaders:%s", getReqURL));
            }
        }
        @Override
        public void onDataReceived(DataReceivedParams paramDataReceivedParams) {
            //接收到数据时候的操作

            //存储验证码数据
            String getReqURL = paramDataReceivedParams.getURL();
            if (base_captcha_url != null && ElementUtils.isSimilarLink(getReqURL, base_captcha_url)) {
                try {
                    FXMLDocumentController.this.captcha_data = paramDataReceivedParams.getData();
                    this.captchaBytes.append(FXMLDocumentController.this.captcha_data, 0, FXMLDocumentController.this.captcha_data.length);
                    FXMLDocumentController.this.captcha_data = this.captchaBytes.toByteArray();
                    print_debug(String.format("获取验证码数据 onDataReceived:[%s] From [%s]", FXMLDocumentController.this.captcha_data.length, getReqURL));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(this.captchaBytes.isEmpty()){
                    print_debug(String.format("获取验证码数据失败 onDataReceived From [%s]", getReqURL));
                }
            }

            //检查登录关键字匹配状态
            String charset = paramDataReceivedParams.getCharset();
            if (isEmptyIfStr(charset)) { charset = "utf-8"; }
            try {
                String receive = new String(paramDataReceivedParams.getData(), charset);
                //print_info(String.format("receive data size: %s from %s", receive.length(), paramDataReceivedParams.getURL()));

                String login_success_key = FXMLDocumentController.this.bro_id_success_regex_text.getText();
                String foundStrForLoginSuccess = ElementUtils.FoundContainSubString(receive, login_success_key);
                if(foundStrForLoginSuccess != null){
                    crack_status = String.format("%s<->%s", LOGIN_SUCCESS, paramDataReceivedParams.getURL());
                    printlnInfoOnUIAndConsole(String.format("响应内容匹配: 登录成功 %s [匹配:%s]", crack_status, foundStrForLoginSuccess));

                    //自动指定当前页面加载状态为已完成
                    loading_status = LOADING_FINISH;
                }

                String login_failure_key = FXMLDocumentController.this.bro_id_failure_regex_text.getText();
                String foundStrForLoginFailure = ElementUtils.FoundContainSubString(receive, login_failure_key);
                if(foundStrForLoginFailure != null){
                    crack_status = String.format("%s<->%s", LOGIN_FAILURE, paramDataReceivedParams.getURL());
                    printlnErrorOnUIAndConsole(String.format("响应内容匹配: 登录失败 %s [匹配:%s]", crack_status, foundStrForLoginFailure));

                    //自动指定当前页面加载状态为已完成
                    loading_status = LOADING_FINISH;
                }

                String captcha_fail_key = FXMLDocumentController.this.bro_id_captcha_regex_text.getText();
                String foundStrForCaptchaFail = ElementUtils.FoundContainSubString(receive, captcha_fail_key);
                if(foundStrForCaptchaFail != null){
                    crack_status = String.format("%s<->%s", ERROR_CAPTCHA, paramDataReceivedParams.getURL());
                    printlnErrorOnUIAndConsole(String.format("响应内容匹配: 验证码错误 %s [匹配:%s]", crack_status, foundStrForCaptchaFail));

                    //自动指定当前页面加载状态为已完成
                    loading_status = LOADING_FINISH;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        @Override
        public boolean onAuthRequired(AuthRequiredParams paramAuthRequiredParams) {
            //提示需要认证
            System.out.println("需要认证");
            this.isCompleteAuth = false;
            this.isCancelAuth = false;

            Platform.runLater(new Runnable() {
                public void run() {
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    TextField user_field = new TextField();
                    TextField pass_field = new TextField();
                    user_field.setPromptText("用户名");
                    pass_field.setPromptText("密码");

                    Button ok_button = new Button("确定");
                    Button cancel_button = new Button("取消");

                    HBox hbox = new HBox(50.0D);
                    hbox.getChildren().addAll(new Node[]{ok_button, cancel_button});

                    VBox vbox = new VBox(20.0D, new Node[]{user_field, pass_field, hbox});
                    vbox.setPadding(new Insets(30.0D, 30.0D, 30.0D, 30.0D));

                    vbox.setAlignment(Pos.CENTER);
                    hbox.setAlignment(Pos.CENTER);

                    Scene scene = new Scene(vbox);
                    stage.setScene(scene);
                    stage.setTitle("请输入用户名密码");
                    stage.sizeToScene();
                    user_field.requestFocus();
                    EventHandler okAction = new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent arg0) {
                            paramAuthRequiredParams.setUsername(user_field.getText());
                            paramAuthRequiredParams.setPassword(pass_field.getText());
                            MyNetworkDelegate.this.isCancelAuth = false;
                            MyNetworkDelegate.this.isCompleteAuth = true;
                            stage.close();
                        }
                    };
                    EventHandler cancelAction = new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent arg0) {
                            MyNetworkDelegate.this.isCancelAuth = true;
                            MyNetworkDelegate.this.isCompleteAuth = true;
                            stage.close();
                        }
                    };
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        //关闭请求时的动作
                        @Override
                        public void handle(WindowEvent event) {
                            MyNetworkDelegate.this.isCancelAuth = true;
                            System.out.println("hehe");
                            MyNetworkDelegate.this.isCompleteAuth = true;
                        }
                    });
                    ok_button.setOnAction(okAction);
                    user_field.setOnAction(okAction);
                    pass_field.setOnAction(okAction);
                    cancel_button.setOnAction(cancelAction);
                    stage.showAndWait();
                }
            });
            while (!this.isCompleteAuth) {
                System.out.println("等待输入账号密码");
            }
            System.out.println(this.isCancelAuth);
            return this.isCancelAuth;
        }
    }

    @Override //UI加载时的初始化操作
    public void initialize(URL url, ResourceBundle rb) {
        //通过代码修改复选框设置参考，已修改到FXML文件里面配置
        //ObservableList threads = FXCollections.observableArrayList(new Integer[]{Integer.valueOf(1), Integer.valueOf(3), Integer.valueOf(5), Integer.valueOf(10), Integer.valueOf(20), Integer.valueOf(30), Integer.valueOf(50), Integer.valueOf(60), Integer.valueOf(70), Integer.valueOf(80), Integer.valueOf(90), Integer.valueOf(100)});
        //this.nor_id_threads.setItems(threads);

        //初始化窗口1的内容设置
        //设置登录URL
        setWithCheck(this.id_login_url_text, default_login_url);
        //设置登录框
        setWithCheck(this.bro_id_user_ele_text, default_name_ele_value);
        setWithCheck(this.bro_id_user_ele_type_combo, default_name_ele_type);
        this.bro_id_user_ele_text.setTooltip(new Tooltip("账号框元素定位方式和对应值"));
        this.bro_id_user_ele_type_combo.setTooltip(new Tooltip("账号框元素定位方式和对应值"));

        //设置密码框
        setWithCheck(this.bro_id_pass_ele_text, default_pass_ele_value);
        setWithCheck(this.bro_id_pass_ele_type_combo, default_pass_ele_type);
        this.bro_id_pass_ele_text.setTooltip(new Tooltip("密码框元素定位方式和对应值"));
        this.bro_id_pass_ele_type_combo.setTooltip(new Tooltip("密码框元素定位方式和对应值"));

        //设置提交按钮
        setWithCheck(this.bro_id_submit_ele_text, default_submit_ele_value);
        setWithCheck(this.bro_id_submit_ele_type_combo, default_submit_ele_type);
        this.bro_id_submit_ele_text.setTooltip(new Tooltip("提交按钮元素定位方式和对应值"));
        this.bro_id_submit_ele_type_combo.setTooltip(new Tooltip("提交按钮元素定位方式和对应值"));

        //设置浏览器选项
        setWithCheck(this.bro_id_show_browser_check, default_show_browser_switch);
        this.bro_id_exclude_history_check.setTooltip(new Tooltip("显示浏览器到窗口"));
        setWithCheck(this.bro_id_exclude_history_check, globalExcludeHistorySwitch);
        this.bro_id_exclude_history_check.setTooltip(new Tooltip("排除已测试的历史账号密码"));

        //设置是否保存默认状态
        setWithCheck(this.bro_id_store_unknown_status_check, default_store_unknown_load_status);
        this.bro_id_store_unknown_status_check.setTooltip(new Tooltip("保存响应状态未知（程序无法确定登录结果）的结果"));

        //设置是否使用浏览器代理
        setWithCheck(this.bro_id_use_browser_proxy, true);
        this.bro_id_use_browser_proxy.setTooltip(new Tooltip("启用浏览器代理用于访问调试"));

        setWithCheck(this.bro_id_login_page_wait_time_combo, default_login_page_wait_time);
        this.bro_id_login_page_wait_time_combo.setTooltip(new Tooltip("请求间隔时间（毫秒）"));

        setWithCheck(this.bro_id_submit_fixed_wait_time_combo, default_submit_fixed_wait_time);
        this.bro_id_submit_fixed_wait_time_combo.setTooltip(new Tooltip("提交等待时间（毫秒）"));

        setWithCheck(this.bro_id_submit_auto_wait_check, default_submit_auto_wait_switch);
        this.bro_id_submit_auto_wait_check.setTooltip(new Tooltip("自动等待页面加载完成"));
        this.change_submit_auto_wait_action(null);

        setWithCheck(this.bro_id_default_js_mode_check, default_js_mode_switch);
        this.bro_id_default_js_mode_check.setTooltip(new Tooltip("使用JS执行模式进行元素查找和输入 仅实现XPATH和CSS元素选择器"));

        setWithCheck(this.bro_id_dict_compo_mode_combo, default_dict_compo_mode);
        this.bro_id_dict_compo_mode_combo.setTooltip(new Tooltip("字典组合方式"));

        //设置关键字匹配
        setWithCheck(this.bro_id_success_regex_text, default_resp_key_success_regex);
        setWithCheck(this.bro_id_failure_regex_text, default_resp_key_failure_regex);
        setWithCheck(this.bro_id_captcha_regex_text, default_resp_key_captcha_regex);
        //设置验证码识别开关
        setWithCheck(this.bro_id_captcha_switch_check, default_ident_captcha_switch);
        this.bro_id_captcha_switch_check.setTooltip(new Tooltip("开启验证码识别功能"));
        //设置验证码识别方式
        setWithCheck(default_locale_identify_switch ? this.bro_id_locale_ident_flag_radio : this.bro_id_yzm_remote_ident_radio, true);
        //设置验证码属性
        setWithCheck(this.bro_id_captcha_url_text, default_captcha_url);
        setWithCheck(this.bro_id_captcha_ele_text, default_captcha_ele_value);
        setWithCheck(this.bro_id_captcha_ele_type_combo, default_captcha_ele_type);
        this.bro_id_captcha_ele_text.setTooltip(new Tooltip("验证码输入框元素定位方式和对应值"));
        this.bro_id_captcha_ele_type_combo.setTooltip(new Tooltip("验证码输入框元素定位方式和对应值"));

        //设置验证码配置细节
        setWithCheck(this.bro_id_ident_time_out_combo, default_ident_time_out);
        this.bro_id_ident_time_out_combo.setTooltip(new Tooltip("验证码识别超时（毫秒）"));

        setWithCheck(this.bro_id_ident_format_regex_text, default_ident_format_regex);
        setWithCheck(this.bro_id_ident_format_length_text, default_ident_format_length);
        setWithCheck(this.bro_id_remote_ident_url_text, default_remote_ident_url);
        setWithCheck(this.bro_id_remote_ident_result_extract_regex_text, default_remote_extract_regex);
        setWithCheck(this.bro_id_remote_resp_is_ok_status_text, default_remote_expected_status);
        setWithCheck(this.bro_id_remote_resp_is_ok_keywords_text, default_remote_expected_keywords);

        //模拟禁用动作
        this.captcha_identify_action(null);

    }

    @FXML  //点击 验证码识别开关需要 禁用|开启 的按钮
    private void captcha_identify_action(ActionEvent event) {
        if (this.bro_id_captcha_switch_check.isSelected()) {
            this.bro_id_captcha_set_vbox.setDisable(false);
        } else {
            this.bro_id_captcha_set_vbox.setDisable(true);
        }
    }

    @FXML //测试验证码识别功能是否正常
    public void bro_id_remote_ident_test_run(ActionEvent event) {
        //获取识别方式
        boolean remoteIdent = this.bro_id_yzm_remote_ident_radio.isSelected();

        //获取验证码输入URL的内容
        String bro_captcha_url_text = this.bro_id_captcha_url_text.getText().trim();
        if (isEmptyIfStr(bro_captcha_url_text)) {
            this.bro_id_captcha_url_text.requestFocus();
            return;
        }

        //另外开一个线程处理,以免UI卡顿
        new Thread(new Runnable() {
            public void run() {
                String imagePath = LoadImageToFile(bro_captcha_url_text, "TestRemote.jpg");
                printlnDebugOnUIAndConsole(String.format("Stored Image [%s] To [%s]", bro_captcha_url_text, imagePath));
                identCaptcha(remoteIdent, imagePath, null);
            }
        }).start();
    }

    @FXML  //浏览器窗口显示设置,不需要管理
    private void show_browser_action(ActionEvent event) {
        if (this.primaryStage == null) {
            return;
        }
        if (this.bro_id_show_browser_check.isSelected())
            this.primaryStage.show();
        else
            this.primaryStage.hide();
    }

    //主要爆破函数的修改
    @FXML
    private void startCrack(ActionEvent actionEvent) {
        //初始化停止爆破状态为 false,以免上次的状态影响本次的操作
        stopCrackStatus = false;

        Platform.runLater(() -> {
            if(stopCrackStatus){
                return;
            }

            //读取登录 URL
            base_login_url = this.id_login_url_text.getText().trim();

            //登陆 URL 检查
            if (isEmptyIfStr(base_login_url) || !base_login_url.startsWith("http")) {
                new Alert(Alert.AlertType.NONE, "请输入完整的登录页面URL", new ButtonType[]{ButtonType.CLOSE}).show();
                return;
            }

            //支持在登录URL处输入多个URL,第一个URL用于登录访问,其他URL用于判断
            if (base_login_url.contains("||")){
                login_about_urls = Arrays.asList(base_login_url.split("\\|\\|"));
                base_login_url = login_about_urls.get(0);
                printlnDebugOnUIAndConsole(String.format("指定登录访问URL:%s 相关跳转URL为:%s", login_about_urls.get(0), base_login_url));
            } else{
                login_about_urls = Collections.singletonList(base_login_url);
            }

            //基于登录URL初始化|URL更新|日志文件配置
            MyFileUtils.initBaseOnLoginUrlFile(base_login_url);

            //检查是否存在关键按钮信息修改,(都需要更新到全局变量做记录),并且重新更新加载字典
            boolean isModifiedAuthFile = MyFileUtils.isModifiedAuthFile(); //字典文件是否修改
            //print_info(String.format("isModifiedAuthFile %s", isModifiedAuthFile));

            boolean isModifiedLoginUrl = MyFileUtils.isModifiedLoginUrl(base_login_url); //登录URL是否修改
            //print_info(String.format("isModifiedLoginUrl %s", isModifiedLoginUrl));

            boolean isModifiedDictMode = MyFileUtils.isModifiedDictMode(this.bro_id_dict_compo_mode_combo.getValue()); //字典模式是否修改
            //print_info(String.format("isModifiedDictMode %s", isModifiedDictMode));

            boolean isModifiedExcludeHistory = MyFileUtils.isModifiedExcludeHistory(this.bro_id_exclude_history_check.isSelected());//排除历史状态是否修改
            //print_info(String.format("isModifiedExcludeHistory %s", isModifiedExcludeHistory));

            if(globalExcludeHistorySwitch ||isModifiedAuthFile||isModifiedLoginUrl||isModifiedDictMode||isModifiedExcludeHistory){
                //当登录URL或账号密码文件修改后,就需要重新更新
                printlnDebugOnUIAndConsole("加载账号密码文件开始...");
                //点击登录后加载字典文件
                HashSet<UserPassPair> UserPassPairsHashSet = loadUserPassFile(globalUserNameFile, globalPassWordFile, globalUserPassFile, globalPairSeparator, default_dict_compo_mode);

                //替换密码中的用户名变量
                UserPassPairsHashSet = replaceUserMarkInPass(UserPassPairsHashSet, globalUserMarkInPass);
                print_debug(String.format("Pairs Count After Replace Mark Str [%s]", UserPassPairsHashSet.size()));

                //读取 history 文件,排除历史扫描记录 ，
                if (globalExcludeHistorySwitch) {
                    UserPassPairsHashSet = excludeHistoryPairs(UserPassPairsHashSet, globalCrackHistoryFilePath, globalPairSeparator);
                    print_debug(String.format("Pairs Count After Exclude History [%s] From [%s]", UserPassPairsHashSet.size(), globalCrackHistoryFilePath));
                }

                //将账号密码字典格式从 HashSet 转为 数组,便于索引统计
                globalUserPassPairsArray = UserPassPairsHashSet.toArray(new UserPassPair[0]);
            }

            //判断字典列表数量是否大于0
            if(globalUserPassPairsArray.length > 0){
                printlnDebugOnUIAndConsole(String.format("加载账号密码文件完成 当前账号:密码数量[%s], 开始爆破操作...", globalUserPassPairsArray.length));
            } else {
                printlnErrorOnUIAndConsole(String.format("加载账号密码文件完成 当前账号:密码数量[%s], 跳过爆破操作...", globalUserPassPairsArray.length));
                return;
            }


            //获取用户名框框的内容
            String bro_user_ele_text = this.bro_id_user_ele_text.getText().trim();
            String bro_user_ele_type = this.bro_id_user_ele_type_combo.getValue();
            if (isEmptyIfStr(bro_user_ele_text)) { this.bro_id_user_ele_text.requestFocus(); return;}

            //获取密码框元素的内容
            String bro_pass_ele_text = this.bro_id_pass_ele_text.getText().trim();
            String bro_pass_ele_type = this.bro_id_pass_ele_type_combo.getValue();
            if (isEmptyIfStr(bro_pass_ele_text)) { this.bro_id_pass_ele_text.requestFocus(); return;}

            //登录按钮内容
            String bro_submit_ele_text = this.bro_id_submit_ele_text.getText().trim();
            String bro_id_submit_ele_type = this.bro_id_submit_ele_type_combo.getValue();
            if (isEmptyIfStr(bro_submit_ele_text)) { this.bro_id_submit_ele_text.requestFocus(); return;}

            //检查验证码输入URL的内容
            if (this.bro_id_captcha_switch_check.isSelected() && isEmptyIfStr(this.bro_id_captcha_url_text.getText().trim())) {
                this.bro_id_captcha_url_text.requestFocus();
                return;
            }

            //初始化浏览器 //尝试将 Browser 设置为全局时,将导致无法停止
            browser = initJxBrowserInstance();

            //浏览器代理设置
            login_url_protocol = base_login_url.toLowerCase().startsWith("http://") ? "http" : "https";
            setBrowserProxyMode(browser, this.bro_id_use_browser_proxy.isSelected(), globalBrowserProxyStr, login_url_protocol);

            //设置JxBrowser中网络委托的对象，以实现对浏览器的网络请求和响应的控制和处理。 //更详细的请求和响应处理,含保存验证码图片
            if (this.bro_id_captcha_switch_check.isSelected()){
                base_captcha_url = this.bro_id_captcha_url_text.getText().trim();
                browser.getContext().getNetworkService().setNetworkDelegate(new MyNetworkDelegate(base_captcha_url));
            }

            //初始化获取当前的元素选择模式
            executeJavaScriptMode = this.bro_id_default_js_mode_check.isSelected();

            //开启一个新的线程进行爆破操作
            new Thread(new Runnable() {
                public void run() {
                    try {
                        //提交等待时间
                        Integer bro_submit_fixed_wait_time = FXMLDocumentController.this.bro_id_submit_fixed_wait_time_combo.getValue();

                        //遍历账号密码字典
                        for (int index = 0; index < globalUserPassPairsArray.length;) {
                            // 记录程序开始时间
                            long startTime = System.currentTimeMillis();

                            UserPassPair userPassPair = globalUserPassPairsArray[index];
                            String cur_user = userPassPair.getUsername();
                            String cur_pass = userPassPair.getPassword();

                            //判断停止按钮是否点击,是的话就跳出循环
                            if(stopCrackStatus) {
                                printlnErrorOnUIAndConsole("发现已点击停止按钮, 停止爆破动作");
                                break;
                            }

                            //输出当前即将测试的数据
                            printlnInfoOnUIAndConsole(String.format("当前进度 [%s/%s] <--> [%s] [%s]", index+1, globalUserPassPairsArray.length, userPassPair, base_login_url));

                            //请求间隔设置
                            Integer bro_login_page_wait_time = FXMLDocumentController.this.bro_id_login_page_wait_time_combo.getValue();

                            //设置初始化Cookies字符串 用于满足Cookie不存在时不能直接访问登录页面的情况
                            if (index==0 && globalBrowserInitCookies != null && !globalBrowserInitCookies.trim().isEmpty()){
                                setBrowserCookies(browser, base_login_url, globalBrowserInitCookies);
                            }

                            //清理所有Cookie //可能存在问题,比如验证码, 没有Cookie会怎么样呢?
                            if (index>0 && globalClearCookiesSwitch){clearCookieStorage(browser); }

                            //清空上一次记录的的验证码数据 //清空的话会导致没有加载页面的时候验证码图片没有值
                            //FXMLDocumentController.this.captcha_data = null;


                            //加载登录URL
                            //判断当前页面是不是登录页面 当前页面不是登录页[或登录相关]时重新加载登录页面
                            //当用户指定了 global_login_page_reload_per_time 时，重新加载页面
                            //当验证码是错误的时候也需要重新加载页面，不然总是重新识别不出来,死循环
                            if(global_login_page_reload_per_time ||!ElementUtils.isEqualsOneKey(browser.getURL(),login_about_urls)|| captcha_ident_was_error){
                                try {
                                    printlnDebugOnUIAndConsole("等待加载登录页面 By global_login_page_reload_per_time || !base_login_url.equals(browser.getURL()) || captcha_ident_was_error");
                                    Browser.invokeAndWaitFinishLoadingMainFrame(browser, new Callback<Browser>() {
                                        public void invoke(Browser browser) {
                                            browser.loadURL(base_login_url);
                                        }
                                    }, global_login_page_load_time);
                                } catch (IllegalStateException illegalStateException) {
                                    String illegalStateExceptionMessage = illegalStateException.getMessage();

                                    //状态异常是否重新开始
                                    if (illegalStateExceptionMessage.contains("Channel is already closed")) {
                                        printlnErrorOnUIAndConsole("停止测试, 请点击按钮重新开始");
                                        break;
                                    } else {
                                        printlnErrorOnUIAndConsole(String.format("重新测试, %s", illegalStateExceptionMessage));
                                        continue;
                                    }

                                } catch (Exception exception) {
                                    exception.printStackTrace();

                                    //登录页面加载超时是否重头再来
                                    if(global_login_page_load_timeout_rework) {
                                        printlnErrorOnUIAndConsole("访问超时-重新测试, 请检查网络设置状态");
                                        continue;
                                    } else {
                                        printlnErrorOnUIAndConsole("访问超时-停止测试, 请检查网络设置状态");
                                        break;
                                    }
                                }

                                //进行线程延迟 //等待页面加载完毕//原则上是可以不需要的
                                Thread.sleep(bro_login_page_wait_time);
                            }else {
                                //进行线程延迟
                                Thread.sleep(bro_login_page_wait_time + 1000);
                            }

                            //加载URl文档
                            DOMDocument document = browser.getDocument();
                            //输入用户名
                            ActionStatus action_status;
                            if (executeJavaScriptMode){
                                action_status = setInputValueByJS(browser, bro_user_ele_text, bro_user_ele_type,  cur_user);
                            } else {
                                action_status = findElementAndInputWithRetries(document, bro_user_ele_text, bro_user_ele_type, cur_user, globalFindEleRetryTimes, globalFindEleDelayTime);
                            }
                            //处理资源寻找状态
                            if(!SUCCESS.equals(action_status)){
                                printlnErrorOnUIAndConsole(String.format("Error For Location [USERNAME] [%s] <--> Action: [%s]", bro_user_ele_text, action_status));

                                //查找元素错误时的处理 继续还是中断
                                if(BREAK.equals(action_status)) {break;} else if(CONTINUE.equals(action_status)){continue;} else {continue;}
                            }else{
                                print_debug("find [USERNAME] Element And Input Success ...");
                            }

                            //查找密码输入框
                            if (executeJavaScriptMode){
                                action_status = setInputValueByJS(browser, bro_pass_ele_text, bro_pass_ele_type,  cur_pass);
                            } else {
                                action_status = findElementAndInputWithRetries(document, bro_pass_ele_text, bro_pass_ele_type, cur_pass, globalFindEleRetryTimes, globalFindEleDelayTime);
                            }
                            //处理资源寻找状态
                            if(!SUCCESS.equals(action_status)){
                                printlnErrorOnUIAndConsole(String.format("Error For Location [PASSWORD] [%s] <--> Action: [%s]", bro_pass_ele_text, action_status));
                                //查找元素错误时的处理 继续还是中断
                                if(BREAK.equals(action_status)) {break;} else if(CONTINUE.equals(action_status)){continue;} else {continue;}
                            }else{
                                print_debug("find [PASSWORD] Element And Input Success ...");
                            }

                            //获取验证码并进行识别
                            if (FXMLDocumentController.this.bro_id_captcha_switch_check.isSelected()) {
                                //captcha_data不存在
                                if (FXMLDocumentController.this.captcha_data == null) {
                                    printlnErrorOnUIAndConsole("获取验证码失败 (数据为空) 重新测试...");
                                    captcha_ident_was_error = true;
                                    continue;
                                }

                                //获取输入的验证码元素定位信息
                                String bro_captcha_ele_text = FXMLDocumentController.this.bro_id_captcha_ele_text.getText().trim();
                                String bro_captcha_ele_type = FXMLDocumentController.this.bro_id_captcha_ele_type_combo.getValue();
                                if (isEmptyIfStr(bro_captcha_ele_text)) {
                                    printlnErrorOnUIAndConsole("验证码定位元素表单内容为空 请输入...");
                                    FXMLDocumentController.this.bro_id_user_ele_text.requestFocus();
                                    return;
                                }

                                //开始验证码识别
                                String captchaText = identCaptcha(bro_id_yzm_remote_ident_radio.isSelected(), null, FXMLDocumentController.this.captcha_data);
                                //判断验证码 是否是否正确
                                if(isEmptyIfStr(captchaText)){
                                    printlnErrorOnUIAndConsole(String.format("识别验证码失败 (结果为空) 重新测试...", captchaText));
                                    captcha_ident_was_error = true;
                                    continue;
                                }

                                //输入验证码元素 并检查输入状态
                                if (executeJavaScriptMode){
                                    action_status = setInputValueByJS(browser, bro_captcha_ele_text, bro_captcha_ele_type,  captchaText);
                                } else {
                                    action_status = findElementAndInputWithRetries(document,bro_captcha_ele_text, bro_captcha_ele_type, captchaText, globalFindEleRetryTimes, globalFindEleDelayTime);
                                }
                                //处理资源寻找状态
                                if(!SUCCESS.equals(action_status)){
                                    printlnErrorOnUIAndConsole(String.format("Error For Location [CAPTCHA] [%s] <--> Action: [%s]", bro_captcha_ele_text, action_status));
                                    //查找元素错误时的处理 继续还是中断
                                    if(BREAK.equals(action_status)) {break;} else if(CONTINUE.equals(action_status)){continue;} else {continue;}
                                }else{
                                    print_debug("find [CAPTCHA] Element And Input Success ...");
                                }
                                captcha_ident_was_error = false;
                            }

                            //定位提交按钮, 并填写按钮
                            ActionStatus submit_status = SUCCESS;
                            try {
                                Element submitElement = findElementByOption(document, bro_submit_ele_text, bro_id_submit_ele_type);
                                submitElement.click();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                printlnErrorOnUIAndConsole(String.format("Error For Location:[%s] <--> Action:[%s] <--> Error:[%s]", bro_submit_ele_text, bro_id_submit_ele_type, exception.getMessage()));
                                try {
                                    document.findElement(By.cssSelector("[type=submit]")).click();
                                } catch (IllegalStateException illegalStateException) {
                                    illegalStateException.printStackTrace();
                                    printlnErrorOnUIAndConsole("Error For document.findElement(By.cssSelector(\"[type=submit]\")).click()");
                                    submit_status = ActionStatus.fromString(FIND_ELE_NULL_ACTION);
                                }
                            } finally {
                                //处理按钮点击状态
                                if(!SUCCESS.equals(submit_status)){
                                    printlnErrorOnUIAndConsole(String.format("Error For Location [SUBMIT] [%s] <--> Action: [%s]", bro_submit_ele_text, submit_status));
                                    //查找元素错误时的处理 继续还是中断
                                    if(BREAK.equals(action_status)) {break;} else if(CONTINUE.equals(action_status)){continue;} else {continue;}
                                }else{
                                    print_debug("find [SUBMIT] Element And Input Success ...");
                                }
                            }

                            //在当前编辑区域（可能是文本框或富文本编辑器等）的光标位置插入一个新的空行，类似于按下回车键创建一个新行。
                            //browser.executeCommand(EditorCommand.INSERT_NEW_LINE);

                            //点击按钮前先重置页面加载状态
                            loading_status ="";
                            crack_status ="";

                            //需要等待页面加载完毕
                            if (bro_id_submit_auto_wait_check.isSelected()){
                                Thread.sleep(global_submit_auto_wait_interval);
                                long wait_start_time = System.currentTimeMillis();
                                while (isEmptyIfStr(loading_status) || loading_status.contains(LOADING_START)) {
                                    //输出检查状态
                                    printlnDebugOnUIAndConsole(String.format("checking status: [%s]", loading_status));
                                    // 检查是否超时
                                    if (System.currentTimeMillis() - wait_start_time > global_submit_auto_wait_limit) {
                                        printlnDebugOnUIAndConsole("等待超时，退出循环");
                                        break;
                                    }
                                    //继续等待
                                    Thread.sleep(global_submit_auto_wait_interval);
                                }
                            } else {
                                Thread.sleep((bro_submit_fixed_wait_time>0)?bro_submit_fixed_wait_time:2000);
                            }


                            //设置 loading_status 为 const_loading_unknown
                            if(isEmptyIfStr(loading_status)|| LOADING_UNKNOWN.equalsIgnoreCase(loading_status)) {
                                printlnErrorOnUIAndConsole(String.format("最终页面状态异常: [%s] 保留: [%s]", loading_status, bro_id_store_unknown_status_check.isSelected()));
                                loading_status = LOADING_UNKNOWN;
                            }

                            //输出加载状态
                            String cur_url = browser.getURL();
                            String cur_title = browser.getTitle();
                            int cur_length = browser.getHTML().length();

                            //判断是否跳转
                            boolean isPageForward = !urlRemoveQuery(base_login_url).equalsIgnoreCase(urlRemoveQuery(cur_url));
                            //进行日志记录
                            String title = "是否跳转,登录URL,测试账号,测试密码,跳转URL,网页标题,内容长度,爆破状态,加载状态";
                            MyFileUtils.writeTitleToFile(globalCrackLogRecodeFilePath, title);

                            String content = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                                    escapeString(isPageForward),
                                    escapeString(base_login_url),
                                    escapeString(cur_user),
                                    escapeString(cur_pass),
                                    escapeString(cur_url),
                                    escapeString(cur_title),
                                    escapeString(cur_length),
                                    escapeString(crack_status),
                                    escapeString(loading_status)
                            );
                            MyFileUtils.writeLineToFile(globalCrackLogRecodeFilePath, content);

                            print_debug(String.format("本次 Crack Login 状态: %s", crack_status));

                            //添加一个条件, 动态判断对于 const_loading_unknown 状态的是响应是否保存到结果中
                            if(loading_status.contains(LOADING_FINISH) || (bro_id_store_unknown_status_check.isSelected() && loading_status.contains(LOADING_UNKNOWN))){
                                //判断登录状态是否时验证码码错误,是的话,就不能记录到爆破历史中
                                if(crack_status.contains(ERROR_CAPTCHA)){
                                    captcha_ident_was_error = true;
                                    MyFileUtils.writeUserPassPairToFile(globalErrorCaptchaFilePath, globalPairSeparator, userPassPair);
                                    printlnErrorOnUIAndConsole(String.format("重新测试|||账号:密码【%s:%s】\n" +
                                                    "跳转情况:%s -> %s->%s\n" +
                                                    "网页标题:%s -> 长度:%s\n",
                                            cur_user, cur_pass, isPageForward, base_login_url, cur_url, cur_title, cur_length));
                                } else {
                                    //进行爆破历史记录
                                    MyFileUtils.writeUserPassPairToFile(globalCrackHistoryFilePath, globalPairSeparator, userPassPair);
                                    if(crack_status.contains(LOGIN_SUCCESS)){
                                        MyFileUtils.writeUserPassPairToFile(globalLoginSuccessFilePath, globalPairSeparator, userPassPair);
                                        printlnInfoOnUIAndConsole(String.format("登录成功|||账号:密码【%s:%s】\n" +
                                                        "跳转情况:%s -> %s->%s\n" +
                                                        "网页标题:%s -> 长度:%s\n",
                                                cur_user, cur_pass, isPageForward, base_login_url, cur_url, cur_title, cur_length));
                                    } else if(crack_status.contains(LOGIN_FAILURE)){
                                        MyFileUtils.writeUserPassPairToFile(globalLoginFailureFilePath, globalPairSeparator, userPassPair);
                                        printlnErrorOnUIAndConsole(String.format("登录失败|||账号:密码【%s:%s】\n" +
                                                        "跳转情况:%s -> %s->%s\n" +
                                                        "网页标题:%s -> 长度:%s\n",
                                                cur_user, cur_pass, isPageForward, base_login_url, cur_url, cur_title, cur_length));
                                    } else {
                                        printlnInfoOnUIAndConsole(String.format("未知状态|||账号:密码【%s:%s】\n" +
                                                        "跳转情况:%s -> %s->%s\n" +
                                                        "网页标题:%s -> 长度:%s\n",
                                                cur_user, cur_pass, isPageForward, base_login_url, cur_url, cur_title, cur_length));
                                    }

                                    //对统计计数进行增加
                                    index ++;
                                }
                            } else {
                                printlnErrorOnUIAndConsole(String.format("加载失败|||账号:密码【%s:%s】\n" +
                                                "跳转情况:%s -> %s->%s\n" +
                                                "网页标题:%s -> 长度:%s\n",
                                        cur_user, cur_pass, isPageForward, base_login_url, cur_url, cur_title, cur_length));

                                //判断当前是不是固定加载模式,是的话就自动添加一点加载时间
                                if(!bro_id_submit_auto_wait_check.isSelected() && bro_submit_fixed_wait_time < global_submit_auto_wait_limit) {
                                    bro_submit_fixed_wait_time += 1000;
                                    printlnInfoOnUIAndConsole(String.format("等待超时|||自动更新等待时间至[%s]", bro_submit_fixed_wait_time));
                                }

                                //对于未知加载状态的数据进行跳过，但是不保存，防止死循环的发生
                                index ++;
                            }

                            // 输出预计剩余运行时间
                            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; //单位 秒
                            long residueTime = (globalUserPassPairsArray.length - index - 1) * elapsedTime / 60; //单位 分
                            print_debug(String.format("本次运行时间:[%s]秒, 预计剩余时间:[%s]分钟 ==> [%s]小时...\n", elapsedTime, residueTime, residueTime/60));
                        }

                        //停止所有请求,防止影响到下一次的使用 //6.15版本没有办法处理关闭浏览器
                        printlnInfoOnUIAndConsole("所有爆破任务正常结束...");
                        stopCrackStatus=true;
                    } catch (Exception e) {
                        // 处理特定的 IllegalStateException
                        if (e.getMessage().contains("stream was closed") || e.getMessage().contains("Failed to send message")) {
                            printlnDebugOnUIAndConsole("发生已知异常|即将重试: Channel stream was closed !!!");
                            stopCrackStatus=false;
                        } else {
                            // 其他类型的 IllegalStateException
                            e.printStackTrace();
                            printlnErrorOnUIAndConsole("发生未知异常|即将停止: " + e.getMessage());
                            stopCrackStatus=true;
                        }
                    } finally {
                        //开始进行重新测试
                        if (!stopCrackStatus && !SystemUtilization.isSystemOverloaded()){
                            startCrack(actionEvent);
                        } else{
                            //关闭当前浏览器,后续需要优化为连Chrome进程一起关闭
                            browser.dispose();
                        }
                    }
                }
            }).start();
        });

    }


}
