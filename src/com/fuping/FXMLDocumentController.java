package com.fuping;

import com.fuping.BrowserUtils.MyDialogHandler;
import com.fuping.CommonUtils.Utils;
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
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;

import static cn.hutool.core.util.StrUtil.isEmptyIfStr;
import static com.fuping.BrowserUtils.BrowserUtils.*;
import static com.fuping.CaptchaIdentify.CaptchaUtils.LoadImageToFile;
import static com.fuping.CaptchaIdentify.RemoteApiIdent.remoteIndentCaptcha;
import static com.fuping.CaptchaIdentify.TesseractsLocaleIdent.localeIdentCaptcha;
import static com.fuping.CommonUtils.Utils.*;
import static com.fuping.LoadConfig.MyConst.*;
import static com.fuping.LoadDict.LoadDictUtils.loadUserPassFile;
import static com.fuping.PrintLog.PrintLog.print_error;
import static com.fuping.PrintLog.PrintLog.print_info;

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
    public TextField bro_id_remote_expected_status_text;
    @FXML  //识别结果判断条件
    public TextField bro_id_remote_expected_keywords_text;
    @FXML  //识别结果内容提取
    public TextField bro_id_remote_extract_regex_text;

    //操作模式选择
    @FXML
    private Tab id_browser_op_mode_tab;
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

    //远程验证码识别功能相关
    @FXML
    public VBox bro_id_remote_index_set_vbox;

    private Stage primaryStage;
    private byte[] captcha_data;

    //记录当前页面加载状态
    private String loading_status;
    private final String const_loading_start = "loading_start";
    private final String const_loading_finish = "loading_finish";
    private final String const_loading_failed = "loading_failed";
    private final String const_loading_unknown = "loading_unknown"; //在没有获取到状态时使用

    private String crack_status;
    private final String const_login_success = "login_success";  //登录成功
    private final String const_login_failure = "login_failure";  //登录失败
    private final String const_error_captcha = "error_captcha";  //验证码错误常量

    private String base_login_url = null;  //设置当前登录url的全局变量用于后续调用
    private String base_captcha_url = null;  //设置当前登录验证码url用于后续调用
    private boolean captcha_is_error = false; //设置当前验证码识别错误状态

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
    private void printlnInfoOnUIAndConsole(String appendTextToUI) {
        print_info(appendTextToUI);
        Platform.runLater(new Runnable() {
            public void run() {
                FXMLDocumentController.this.bro_id_output_text_area.appendText(String.format("[*] %s\n", appendTextToUI));
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
    //查找元素并输入
    private String findElementAndInput(DOMDocument document, String locate_info, String selectedOption, String input_string) {
        String action_string = "success";
        try {
            InputElement findElement = findInputElementByOption(document, locate_info, selectedOption);
            Map<String, String> attributes = findElement.getAttributes();

            findElement.setValue(input_string);
            // for (String attrName : attributes.keySet()) { System.out.println(attrName + " = " + attributes.get(attrName)); }
        }
        catch (IllegalStateException illegalStateException) {
            String eMessage = illegalStateException.getMessage();
            System.out.println(eMessage);
            if (eMessage.contains("Channel is already closed")) {
                printlnErrorOnUIAndConsole("浏览器已关闭 (IllegalStateException) 停止测试...");
                action_string = const_browser_close_action;
            }else {
                illegalStateException.printStackTrace();
                action_string = const_find_Ele_illegal_action ;
            }
        } catch (NullPointerException nullPointerException) {
            printlnErrorOnUIAndConsole("定位元素失败 (nullPointerException) 停止测试...");
            action_string = const_find_Ele_null_action;
        } catch (Exception exception) {
            exception.printStackTrace();
            action_string = const_find_Ele_exception_action;
        }
        return action_string;
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
            String ident_expected_status = this.bro_id_remote_expected_status_text.getText(); //期望状态码
            String ident_expected_keywords = this.bro_id_remote_expected_keywords_text.getText(); //期望响应
            //响应结果正则提取
            String ident_extract_regex = this.bro_id_remote_extract_regex_text.getText(); //提取数据

            try {
                //识别图片地址
                if (isNotEmptyIfStr(imagePath)){
                    captcha_indent_text = remoteIndentCaptcha(
                            imagePath,
                            remote_ident_url_text,
                            ident_expected_status,
                            ident_expected_keywords,
                            ident_extract_regex,
                            ident_format_length,
                            ident_time_out
                    );
                } else {
                    captcha_indent_text = remoteIndentCaptcha(imageBytes,
                            remote_ident_url_text,
                            ident_expected_status,
                            ident_expected_keywords,
                            ident_extract_regex,
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
                if (isNotEmptyIfStr(imagePath)){
                    captcha_indent_text = localeIdentCaptcha(imagePath, ident_format_regex, ident_format_length, globalLocaleTessDataName);
                } else {
                    captcha_indent_text = localeIdentCaptcha(imageBytes, ident_format_regex, ident_format_length, globalLocaleTessDataName);
                }
                printlnInfoOnUIAndConsole(String.format("验证码 本地识别结果: [%s]", captcha_indent_text));
            } catch (Exception e) {
                printlnErrorOnUIAndConsole(String.format("验证码 本地识别出错: [%s]", e.getMessage()));
                e.printStackTrace();
            }
        }
        return captcha_indent_text;
    }

    //浏览器动作配置
    private Browser initJxBrowserInstance(String browserProxyString) {
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
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
                if (event.isMainFrame())
                    Platform.runLater(new Runnable() {
                        public void run() {
                            url_input.setText(event.getURL());
                        }
                    });
            }
            @Override
            public void onStartLoadingFrame(StartLoadingEvent event) {
                if (event.isMainFrame()){
                    Platform.runLater(new Runnable() {
                        public void run() {
                            //输出加载中记录输出两次 UI重复,不是错误,是浏览器实际进行了主页和登录请求两次
                            String validatedURL = event.getValidatedURL();
                            loading_status= String.format("%s<->%s", const_loading_start, validatedURL);
                            printlnInfoOnUIAndConsole(String.format("Loading Status: %s %s", const_loading_start, validatedURL));
                            progressIndicator.setProgress(-1.0D);
                        }
                    });
                }
                super.onStartLoadingFrame(event);
            }
            @Override
            public void onFailLoadingFrame(FailLoadingEvent event) {
                if (event.isMainFrame())
                    Platform.runLater(new Runnable() {
                        public void run() {
                            String validatedURL = event.getValidatedURL();
                            loading_status= String.format("%s<->%s", const_loading_failed, validatedURL);
                            printlnErrorOnUIAndConsole(String.format("Loading Status: %s %s", const_loading_failed, validatedURL));
                            progressIndicator.setProgress(1.0D);
                        }
                    });
                super.onFailLoadingFrame(event);
            }
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            String validatedURL = event.getValidatedURL();
                            loading_status= String.format("%s<->%s", const_loading_finish, validatedURL);
                            printlnInfoOnUIAndConsole(String.format("Loading Status: %s %s", const_loading_finish, validatedURL));
                            progressIndicator.setProgress(1.0D);
                        }
                    });
                }
                super.onFinishLoadingFrame(event);
            }
//            @Override
//            public void onDocumentLoadedInFrame(FrameLoadEvent event) {
//                printlnInfoOnUIAndConsole("Frame document is loaded.");
//            }
//            @Override
//            public void onDocumentLoadedInMainFrame(LoadEvent event) {
//                printlnInfoOnUIAndConsole("Main frame document is loaded.");
//            }
        });
       //添加响应这状态码监听事件 //addStatusListener 没有获取到任何数据 //放弃使用

        //浏览器代理设置
        if (isNotEmptyIfStr(browserProxyString)) {
            //参考 使用代理 https://www.kancloud.cn/neoman/ui/802531
            //转换输入的代理格式
            browserProxyString = browserProxyString.replace("://","=");
            browser.getContext().getProxyService().setProxyConfig(new CustomProxyConfig(browserProxyString));
            print_info(String.format("Browser Proxy Was Configured [%s]", browserProxyString));
        }else {
            print_info("Browser Proxy Not Configured ...");
        }
        return browser;
    }

    //定义是否停止所有爆破动作
    public static boolean stopCrackStatus = false;

    @FXML //停止爆破动作
    public void stopCrack(ActionEvent actionEvent) {
        stopCrackStatus = true;
        printlnErrorOnUIAndConsole("已点击停止按钮,请等待停止信号传递...");
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
            if (base_captcha_url != null && Utils.isSimilarLink(getReqURL, base_captcha_url)) {
                this.captchaBytes.clear();
                paramBeforeSendHeadersParams.getHeadersEx().setHeader("Accept-Encoding", "");
                print_info(String.format("正在发起验证码请求 BeforeSendHeaders:%s", getReqURL));
            }
        }
        @Override
        public void onDataReceived(DataReceivedParams paramDataReceivedParams) {
            //接收到数据时候的操作

            //存储验证码数据
            String getReqURL = paramDataReceivedParams.getURL();
            if (base_captcha_url != null && Utils.isSimilarLink(getReqURL, base_captcha_url)) {
                try {
                    FXMLDocumentController.this.captcha_data = paramDataReceivedParams.getData();
                    this.captchaBytes.append(FXMLDocumentController.this.captcha_data, 0, FXMLDocumentController.this.captcha_data.length);
                    FXMLDocumentController.this.captcha_data = this.captchaBytes.toByteArray();
                    print_info(String.format("获取验证码数据 onDataReceived:[%s] From [%s]", FXMLDocumentController.this.captcha_data.length, getReqURL));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //检查登录关键字匹配状态
            String charset = paramDataReceivedParams.getCharset();
            if (isEmptyIfStr(charset)) { charset = "utf-8"; }
            try {
                String receive = new String(paramDataReceivedParams.getData(), charset);
                String success_key = FXMLDocumentController.this.bro_id_success_regex_text.getText();
                if(containsMatchingSubString(receive, success_key)){
                    crack_status = String.format("%s<->%s", const_login_success, paramDataReceivedParams.getURL());
                    printlnInfoOnUIAndConsole(String.format("响应内容匹配: 登录成功 %s [Find:%s]",crack_status, success_key));
                }

                String failure_key = FXMLDocumentController.this.bro_id_failure_regex_text.getText();
                if(containsMatchingSubString(receive, failure_key)){
                    crack_status = String.format("%s<->%s", const_login_failure, paramDataReceivedParams.getURL());
                    printlnErrorOnUIAndConsole(String.format("响应内容匹配: 登录失败 %s [Find:%s]",crack_status, failure_key));
                }

                String captcha_fail = FXMLDocumentController.this.bro_id_captcha_regex_text.getText();
                if(containsMatchingSubString(receive, captcha_fail)){
                    crack_status = String.format("%s<->%s", const_error_captcha, paramDataReceivedParams.getURL());
                    printlnErrorOnUIAndConsole(String.format("响应内容匹配: 验证码错误 %s [Find:%s]",crack_status, captcha_fail));
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
        //设置密码框
        setWithCheck(this.bro_id_pass_ele_text, default_pass_ele_value);
        setWithCheck(this.bro_id_pass_ele_type_combo, default_pass_ele_type);
        //设置提交按钮
        setWithCheck(this.bro_id_submit_ele_text, default_submit_ele_value);
        setWithCheck(this.bro_id_submit_ele_type_combo, default_submit_ele_type);
        //设置浏览器选项
        setWithCheck(this.bro_id_show_browser_check, default_show_browser_switch);
        setWithCheck(this.bro_id_exclude_history_check, globalExcludeHistorySwitch);

        //设置是否保存默认状态
        setWithCheck(this.bro_id_store_unknown_status_check, default_store_unknown_load_status);

        setWithCheck(this.bro_id_login_page_wait_time_combo, default_login_page_wait_time);
        setWithCheck(this.bro_id_submit_fixed_wait_time_combo, default_submit_fixed_wait_time);
        setWithCheck(this.bro_id_submit_auto_wait_check, default_submit_auto_wait_switch);

        setWithCheck(this.bro_id_dict_compo_mode_combo, default_dict_compo_mode);
        //设置关键字匹配
        setWithCheck(this.bro_id_success_regex_text, default_resp_key_success_regex);
        setWithCheck(this.bro_id_failure_regex_text, default_resp_key_failure_regex);
        setWithCheck(this.bro_id_captcha_regex_text, default_resp_key_captcha_regex);
        //设置验证码识别开关
        setWithCheck(this.bro_id_captcha_switch_check, default_ident_captcha_switch);
        //设置验证码识别方式
        setWithCheck(default_locale_identify_switch ? this.bro_id_locale_ident_flag_radio : this.bro_id_yzm_remote_ident_radio, true);
        //设置验证码属性
        setWithCheck(this.bro_id_captcha_url_text, default_captcha_url);
        setWithCheck(this.bro_id_captcha_ele_text, default_captcha_ele_value);
        setWithCheck(this.bro_id_captcha_ele_type_combo, default_captcha_ele_type);

        //设置验证码配置细节
        setWithCheck(this.bro_id_ident_time_out_combo, default_ident_time_out);
        setWithCheck(this.bro_id_ident_format_regex_text, default_ident_format_regex);
        setWithCheck(this.bro_id_ident_format_length_text, default_ident_format_length);
        setWithCheck(this.bro_id_remote_ident_url_text, default_remote_ident_url);
        setWithCheck(this.bro_id_remote_extract_regex_text, default_remote_extract_regex);
        setWithCheck(this.bro_id_remote_expected_status_text, default_remote_expected_status);
        setWithCheck(this.bro_id_remote_expected_keywords_text, default_remote_expected_keywords);

        //模拟禁用动作
        this.bro_id_captcha_identify_action(null);

    }

    @FXML  //浏览器窗口显示设置,不需要管理
    private void bro_id_show_browser_action(ActionEvent event) {
        if (this.primaryStage == null) {
            return;
        }
        if (this.bro_id_show_browser_check.isSelected())
            this.primaryStage.show();
        else
            this.primaryStage.hide();
    }

    @FXML  //点击 验证码识别开关需要 禁用|开启 的按钮
    private void bro_id_captcha_identify_action(ActionEvent event) {
        if (this.bro_id_captcha_switch_check.isSelected()) {
            this.bro_id_captcha_set_vbox.setDisable(false);
            this.bro_id_remote_index_set_vbox.setDisable(false);
        } else {
            this.bro_id_captcha_set_vbox.setDisable(true);
            this.bro_id_remote_index_set_vbox.setDisable(true);
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
                printlnInfoOnUIAndConsole(String.format("Stored Image [%s] To [%s]", bro_captcha_url_text, imagePath));
                identCaptcha(remoteIdent, imagePath, null);
            }
        }).start();
    }


    //主要爆破函数的修改
    @FXML
    private void startCrack(ActionEvent event) {
        //初始化停止爆破状态为 false,以免上次的状态影响本次的操作
        stopCrackStatus = false;

        //读取登录 URL
        base_login_url = this.id_login_url_text.getText().trim();
        //登陆 URL 检查
        if (isEmptyIfStr(base_login_url) || !base_login_url.startsWith("http")) {
            new Alert(Alert.AlertType.NONE, "请输入完整的登录页面URL", new ButtonType[]{ButtonType.CLOSE}).show();
            return;
        }
        //基于登录URL初始化|URL更新|日志文件配置
        initBaseOnLoginUrlFile(base_login_url);

        //检查是否存在关键按钮信息修改,(都需要更新到全局变量做记录),并且重新更新加载字典
        boolean isModifiedAuthFile = isModifiedAuthFile(); //字典文件是否修改
        //print_info(String.format("isModifiedAuthFile %s", isModifiedAuthFile));

        boolean isModifiedLoginUrl = isModifiedLoginUrl(base_login_url); //登录URL是否修改
        //print_info(String.format("isModifiedLoginUrl %s", isModifiedLoginUrl));

        boolean isModifiedDictMode = isModifiedDictMode(this.bro_id_dict_compo_mode_combo.getValue()); //字典模式是否修改
        //print_info(String.format("isModifiedDictMode %s", isModifiedDictMode));

        boolean isModifiedExcludeHistory = isModifiedExcludeHistory(this.bro_id_exclude_history_check.isSelected());//排除历史状态是否修改
        //print_info(String.format("isModifiedExcludeHistory %s", isModifiedExcludeHistory));

        if(globalExcludeHistorySwitch ||isModifiedAuthFile||isModifiedLoginUrl||isModifiedDictMode||isModifiedExcludeHistory){
            //当登录URL或账号密码文件修改后,就需要重新更新
            printlnInfoOnUIAndConsole("加载账号密码文件开始...");
            //点击登录后加载字典文件
            HashSet<UserPassPair> UserPassPairsHashSet = loadUserPassFile(globalUserNameFile, globalPassWordFile, globalUserPassFile, globalPairSeparator, default_dict_compo_mode);
            //过滤历史字典记录,并转换为Array格式
            globalUserPassPairsArray = processedUserPassHashSet(UserPassPairsHashSet, globalCrackHistoryFilePath, globalPairSeparator, globalExcludeHistorySwitch, globalUserMarkInPass);
        }
        //判断字典列表数量是否大于0
        if(globalUserPassPairsArray.length > 0){
            printlnInfoOnUIAndConsole(String.format("加载账号密码文件完成 当前账号:密码数量[%s], 开始爆破操作...", globalUserPassPairsArray.length));
        } else {
            printlnErrorOnUIAndConsole(String.format("加载账号密码文件完成 当前账号:密码数量[%s], 跳过爆破操作...", globalUserPassPairsArray.length));
            return;
        }

        //浏览器操作模式模式
        if (this.id_browser_op_mode_tab.isSelected()) {
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

            //初始化浏览器
            Browser browser = initJxBrowserInstance(globalBrowserProxy);

            //设置JxBrowser中网络委托的对象，以实现对浏览器的网络请求和响应的控制和处理。 //更详细的请求和响应处理,含保存验证码图片
            if (this.bro_id_captcha_switch_check.isSelected()){
                base_captcha_url = this.bro_id_captcha_url_text.getText().trim();
                browser.getContext().getNetworkService().setNetworkDelegate(new MyNetworkDelegate(base_captcha_url));
            }

            //开启一个新的线程进行爆破操作
            new Thread(new Runnable() {
                public void run() {
                    try {
                        //请求间隔设置
                        Integer bro_login_page_wait_time = FXMLDocumentController.this.bro_id_login_page_wait_time_combo.getValue();
                        Integer bro_submit_fixed_wait_time = FXMLDocumentController.this.bro_id_submit_fixed_wait_time_combo.getValue();

                        //遍历账号密码字典
                        for (int index = 0; index < globalUserPassPairsArray.length; ) {
                            UserPassPair userPassPair = globalUserPassPairsArray[index];

                            //判断停止按钮是否点击,是的话就跳出循环
                            if(stopCrackStatus) {
                                printlnErrorOnUIAndConsole("发现已点击停止按钮, 停止爆破动作");
                                break;
                            }

                            //输出当前即将测试的数据
                            printlnInfoOnUIAndConsole(String.format("当前进度 [%s/%s] <--> [%s] [%s]", index+1, globalUserPassPairsArray.length, userPassPair, base_login_url));

                            //清理所有Cookie //可能存在问题,比如验证码, 没有Cookie会怎么样呢?
                            AutoClearAllCookies(browser);

                            //清空上一次记录的的验证码数据 //清空的话会导致没有加载页面的时候验证码图片没有值
                            //FXMLDocumentController.this.captcha_data = null;


                            //加载登录URL
                            //判断当前页面是不是登录页面 当前页面不是登录页时重新加载登录页面
                            //当用户指定了 global_login_page_reload_per_time 时，重新加载页面
                            //当验证码是错误的时候也需要重新加载页面，不然总是重新识别不出来,死循环
                            if(global_login_page_reload_per_time || !base_login_url.equals(browser.getURL()) || captcha_is_error){
                                try {
                                    Browser.invokeAndWaitFinishLoadingMainFrame(browser, new Callback<Browser>() {
                                        public void invoke(Browser browser) {
                                            browser.loadURL(base_login_url);
                                        }
                                    }, global_login_page_load_time);
                                } catch (IllegalStateException illegalStateException) {
                                    String illegalStateExceptionMessage = illegalStateException.getMessage();
                                    System.out.println(illegalStateExceptionMessage);
                                    if (illegalStateExceptionMessage.contains("Channel is already closed")) {
                                        printlnErrorOnUIAndConsole("停止测试, 请点击按钮重新开始");
                                        break;
                                    }
                                } catch (Exception exception) {
                                    printlnErrorOnUIAndConsole("访问超时, 请检查网络设置状态");
                                    exception.printStackTrace();
                                    if(global_login_page_load_timeout_rework) continue;
                                }

                                //进行线程延迟 //等待页面加载完毕//原则上是可以不需要的
                                Thread.sleep((bro_login_page_wait_time>0)?bro_login_page_wait_time:0);
                            }

                            //加载URl文档
                            DOMDocument document = browser.getDocument();
                            //输入用户名
                            String action_status;
                            action_status = findElementAndInput(document, bro_user_ele_text, bro_user_ele_type, userPassPair.getUsername());
                            //处理资源寻找状态
                            if(!"success".equalsIgnoreCase(action_status)){
                                printlnErrorOnUIAndConsole(String.format("Error For Location [USERNAME] [%s] <--> Action: [%s]", bro_user_ele_text, action_status));
                                if("break".equalsIgnoreCase(action_status)) break; else if("continue".equalsIgnoreCase(action_status)) continue;
                            }else{
                                print_info("find [USERNAME] Element And Input Success ...");
                            }

                            //查找密码输入框
                            action_status = findElementAndInput(document, bro_pass_ele_text, bro_pass_ele_type, userPassPair.getPassword());
                            //处理资源寻找状态
                            if(!"success".equalsIgnoreCase(action_status)){
                                printlnErrorOnUIAndConsole(String.format("Error For Location [PASSWORD] [%s] <--> Action: [%s]", bro_pass_ele_text, action_status));
                                if("break".equalsIgnoreCase(action_status)) break; else if("continue".equalsIgnoreCase(action_status)) continue;
                            }else{
                                print_info("find [PASSWORD] Element And Input Success ...");
                            }

                            //获取验证码并进行识别
                            if (FXMLDocumentController.this.bro_id_captcha_switch_check.isSelected()) {
                                //captcha_data不存在
                                if (FXMLDocumentController.this.captcha_data == null) {
                                    printlnErrorOnUIAndConsole("获取验证码失败 (captcha数据为空)");
                                    captcha_is_error = true;
                                    continue;
                                }

                                //获取输入的验证码元素定位信息
                                String bro_captcha_ele_text = FXMLDocumentController.this.bro_id_captcha_ele_text.getText().trim();
                                String bro_captcha_ele_type = FXMLDocumentController.this.bro_id_captcha_ele_type_combo.getValue();
                                if (isEmptyIfStr(bro_captcha_ele_text)) {
                                    FXMLDocumentController.this.bro_id_user_ele_text.requestFocus();
                                    return;
                                }

                                //开始验证码识别
                                String captchaText = identCaptcha(bro_id_yzm_remote_ident_radio.isSelected(), null, FXMLDocumentController.this.captcha_data);
                                //判断验证码 是否是否正确
                                if(isEmptyIfStr(captchaText)){
                                    printlnErrorOnUIAndConsole(String.format("跳过操作 验证码识别错误...", captchaText));
                                    captcha_is_error = true;
                                    continue;
                                }

                                //输入验证码元素 并检查输入状态
                                action_status = findElementAndInput(document,bro_captcha_ele_text, bro_captcha_ele_type, captchaText);
                                //处理资源寻找状态
                                if(!"success".equalsIgnoreCase(action_status)){
                                    printlnErrorOnUIAndConsole(String.format("Error For Location [CAPTCHA] [%s] <--> Action: [%s]", bro_captcha_ele_text, action_status));
                                    if("break".equalsIgnoreCase(action_status)) break; else if("continue".equalsIgnoreCase(action_status)) continue;
                                }else{
                                    print_info("find [CAPTCHA] Element And Input Success ...");
                                }

                                captcha_is_error = false;
                            }

                            //定位提交按钮, 并填写按钮
                            String submit_status = "success";
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
                                    submit_status = const_find_Ele_null_action;
                                }
                            } finally {
                                //处理按钮点击状态
                                if(!"success".equalsIgnoreCase(submit_status)){
                                    printlnErrorOnUIAndConsole(String.format("Error For Location [SUBMIT] [%s] <--> Action: [%s]", bro_submit_ele_text, submit_status));
                                    if("break".equalsIgnoreCase(submit_status)) break; else if("continue".equalsIgnoreCase(submit_status)) continue;
                                }else{
                                    print_info("find [SUBMIT] Element And Input Success ...");
                                }
                            }

                            //在当前编辑区域（可能是文本框或富文本编辑器等）的光标位置插入一个新的空行，类似于按下回车键创建一个新行。
                            //browser.executeCommand(EditorCommand.INSERT_NEW_LINE);

                            //点击按钮前先重置页面加载状态
                            loading_status="";
                            crack_status="";

                            //需要等待页面加载完毕
                            if (bro_id_submit_auto_wait_check.isSelected()){
                                Thread.sleep(global_submit_auto_wait_interval);
                                long wait_start_time = System.currentTimeMillis();
                                while (isEmptyIfStr(loading_status) || loading_status.contains(const_loading_start)) {
                                    //输出检查状态
                                    printlnInfoOnUIAndConsole(String.format("checking status: [%s]", loading_status));
                                    // 检查是否超时
                                    if (System.currentTimeMillis() - wait_start_time > global_submit_auto_wait_limit) {
                                        printlnInfoOnUIAndConsole("等待超时，退出循环");
                                        break;
                                    }
                                    //继续等待
                                    Thread.sleep(global_submit_auto_wait_interval);
                                }
                            } else {Thread.sleep((bro_submit_fixed_wait_time>0)?bro_submit_fixed_wait_time:2000);}

                            //设置 loading_status 为 const_loading_unknown
                            if(isEmptyIfStr(loading_status)) loading_status = const_loading_unknown;

                            //输出加载状态
                            String cur_url = browser.getURL();
                            String cur_title = browser.getTitle();
                            int cur_length = browser.getHTML().length();

                            //判断是否跳转
                            boolean isPageForward = !urlRemoveQuery(base_login_url).equalsIgnoreCase(urlRemoveQuery(cur_url));
                            //进行日志记录
                            String title = "是否跳转,登录URL,测试账号,测试密码,跳转URL,网页标题,内容长度,爆破状态,加载状态";
                            writeTitleToFile(globalCrackLogRecodeFilePath, title);

                            String content = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                                    escapeString(isPageForward),
                                    escapeString(base_login_url),
                                    escapeString(userPassPair.getUsername()),
                                    escapeString(userPassPair.getPassword()),
                                    escapeString(cur_url),
                                    escapeString(cur_title),
                                    escapeString(cur_length),
                                    escapeString(crack_status),
                                    escapeString(loading_status)
                            );
                            writeLineToFile(globalCrackLogRecodeFilePath, content);

                            print_info(String.format("crack_status:%s", crack_status));

                            //添加一个条件, 动态判断对于 const_loading_unknown 状态的是响应是否保存到结果中
                            if(loading_status.contains(const_loading_finish) || (bro_id_store_unknown_status_check.isSelected() && loading_status.contains(const_loading_unknown))){
                                //判断登录状态是否时验证码码错误,是的话,就不能记录到爆破历史中
                                if(crack_status.contains(const_error_captcha)){
                                    writeUserPassPairToFile(globalErrorCaptchaFilePath, globalPairSeparator, userPassPair);
                                    printlnErrorOnUIAndConsole(String.format("验证码错误|||登录URL: %s\n是否跳转: %s\n测试账号: %s\n测试密码: %s\n跳转URL: %s\n网页标题: %s\n内容长度: %s\n", base_login_url, isPageForward, userPassPair.getUsername(), userPassPair.getPassword(), cur_url, cur_title, cur_length));
                                }else {
                                    //进行爆破历史记录
                                    writeUserPassPairToFile(globalCrackHistoryFilePath, globalPairSeparator, userPassPair);
                                    if(crack_status.contains(const_login_success)){
                                        writeUserPassPairToFile(globalLoginSuccessFilePath, globalPairSeparator, userPassPair);
                                        printlnInfoOnUIAndConsole(String.format("登录成功|||登录URL: %s\n是否跳转: %s\n测试账号: %s\n测试密码: %s\n跳转URL: %s\n网页标题: %s\n内容长度: %s\n", base_login_url, isPageForward, userPassPair.getUsername(), userPassPair.getPassword(), cur_url, cur_title, cur_length));
                                    } else if(crack_status.contains(const_login_failure)){
                                        writeUserPassPairToFile(globalLoginFailureFilePath, globalPairSeparator, userPassPair);
                                        printlnErrorOnUIAndConsole(String.format("登录失败|||登录URL: %s\n是否跳转: %s\n测试账号: %s\n测试密码: %s\n跳转URL: %s\n网页标题: %s\n内容长度: %s\n", base_login_url, isPageForward, userPassPair.getUsername(), userPassPair.getPassword(), cur_url, cur_title, cur_length));
                                    } else {
                                        printlnInfoOnUIAndConsole(String.format("未知状态|||登录URL: %s\n是否跳转: %s\n测试账号: %s\n测试密码: %s\n跳转URL: %s\n网页标题: %s\n内容长度: %s\n", base_login_url, isPageForward, userPassPair.getUsername(), userPassPair.getPassword(), cur_url, cur_title, cur_length));
                                    }

                                    //对统计计数进行增加
                                    index ++;
                                }
                           }else {
                                printlnErrorOnUIAndConsole(String.format("加载失败|||登录URL: %s\n是否跳转: %s\n测试账号: %s\n测试密码: %s\n跳转URL: %s\n网页标题: %s\n内容长度: %s\n", base_login_url, isPageForward, userPassPair.getUsername(), userPassPair.getPassword(), cur_url, cur_title, cur_length));
                                //判断当前是不是固定加载模式,是的话就自动添加一点加载时间
                                if(!bro_id_submit_auto_wait_check.isSelected() && bro_submit_fixed_wait_time < global_submit_auto_wait_limit) {
                                    bro_submit_fixed_wait_time += 1000;
                                    printlnInfoOnUIAndConsole(String.format("等待超时|||自动更新等待时间至[%s]", bro_submit_fixed_wait_time));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //停止所有请求,防止影响到下一次的使用 //6.15版本没有办法处理关闭浏览器
                        browser.dispose();
                        printlnInfoOnUIAndConsole("所有任务爆破结束");
                    }
                }
            }).start();
        }
    }
}
