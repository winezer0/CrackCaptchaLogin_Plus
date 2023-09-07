package com.fuping;

import com.fuping.BaseCrack.SendCrackThread;
import com.fuping.BrowserUtils.MyDialogHandler;
import com.fuping.CaptchaIdentify.YunSu;
import com.fuping.CaptchaIdentify.YunSuConfig;
import com.fuping.CaptchaIdentify.YzmToText;
import com.fuping.LoadDict.UserPassPair;
import com.teamdev.jxbrowser.chromium.Callback;
import com.teamdev.jxbrowser.chromium.*;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMDocument;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static com.fuping.BrowserUtils.BrowserUitls.*;
import static com.fuping.CommonUtils.Utils.*;
import static com.fuping.LoadConfig.MyConst.*;
import static com.fuping.LoadDict.LoadDictUtils.loadUserPassFile;
import static com.fuping.PrintLog.PrintLog.print_error;
import static com.fuping.PrintLog.PrintLog.print_info;

public class FXMLDocumentController implements Initializable {

    //操作模式选择
    @FXML
    private Tab id_browser_op_mode_tab;
    @FXML
    private Tab id_normal_op_mode_tab;


    //第1页元素选择

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
    private ComboBox<Integer> bro_id_load_sleep_combo;
    @FXML //设置字典组合模式
    private ComboBox<String> bro_id_dict_compo_mode_combo;
    @FXML
    private CheckBox bro_id_show_browser_check;
    @FXML
    private CheckBox bro_id_exclude_history_check;

    //验证码元素相关
    @FXML
    public VBox bro_id_all_captcha_set_vbox;
    @FXML
    private CheckBox bro_id_captcha_switch_check;
    @FXML
    private TextField bro_id_captcha_url_text;
    @FXML
    private ComboBox<String> bro_id_captcha_ele_type_combo;
    @FXML
    private TextField bro_id_captcha_ele_text;
    @FXML
    private RadioButton bro_id_yzm_remote_ident_radio;
    @FXML
    private RadioButton bro_id_yzm_local_ident_radio;
    @FXML
    private TextField bro_id_captcha_regex_text;

    //输出相关
    @FXML
    private TextArea bro_id_output_text_area;

    //云速账号相关
    @FXML  //YS整体设置,待删除
    public VBox ys_id_all_yunsu_set_vbox;
    @FXML
    private ComboBox<Integer> yzm_query_timeout_combo;
    @FXML  //YS账号设置,待删除
    private TextField ys_soft_id_text;
    @FXML  //YS账号设置,待删除
    private TextField ys_soft_key_text;
    @FXML  //YS账号设置,待删除
    private TextField ys_username_text;
    @FXML  //YS账号设置,待删除
    private PasswordField ys_password_text;
    @FXML  //YS账号设置,待删除
    private TextField ys_type_id_text;




    //第2页的元素
    @FXML
    private TextArea nor_id_request_text_area;
    @FXML
    private TextField nor_id_captcha_url_text;
    @FXML
    private TextField nor_id_success_keys_text;

    @FXML
    private CheckBox nor_id_captcha_ident_check;

    @FXML
    private ComboBox<Integer> nor_id_threads_combo;
    @FXML
    private ComboBox<Integer> nor_id_timeout_combo;
    @FXML
    private TextArea nor_id_output_text_area;


    private Stage primaryStage;
    private byte[] captcha_data;
    private LinkedBlockingQueue<UserPassPair> queue;
    private Boolean is_stop_send_crack;
    private String captchaText = null;
    private List<Cookie> cookies = null;

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
    private String findElementAndInput(DOMDocument document, String locate_info, String selectedOption, String input_string) {
        String action_string = "success";
        try {
            InputElement findElement = findElementByOption(document, locate_info, selectedOption);
            findElement.setValue(input_string);
        }
        catch (IllegalStateException illegalStateException) {
            String eMessage = illegalStateException.getMessage();
            System.out.println(eMessage);
            if (eMessage.contains("Channel is already closed")) {
                printlnErrorOnUIAndConsole("浏览器已关闭 (IllegalStateException) 停止测试...");
            }
            illegalStateException.printStackTrace();
            action_string = "break";
        }
        catch (NullPointerException nullPointerException) {
            printlnErrorOnUIAndConsole("定位元素失败 (nullPointerException) 停止测试...");
            action_string = "break";
        } catch (Exception exception) {
            exception.printStackTrace();
            action_string = "continue";
        }
        return action_string;
    }

    //浏览器动作配置
    private Browser initJxBrowserInstance() {
        //创建窗口对象 JavaFX的Stage类是JavaFX应用程序创建窗口的基础
        this.primaryStage = new Stage();

        //创建浏览器对象 轻量级对象 BrowserType.LIGHTWEIGHT 轻量级渲染模式， BrowserType.HEAVYWEIGHT重量级渲染模式。
        //轻量级渲染模式是通过CPU来加速渲染的，速度更快，占用更少的内存。在轻量级渲染模式下，Chromium引擎会在后台使用CPU渲染网页，然后将网页的图像保存在共享内存中。
        //重量级渲染模式则使用GPU加速渲染，相对于轻量级模式来说，它需要占用更多的内存，但在某些场景下可能会有更好的性能和更高的渲染质量。
        Browser browser = new Browser(BrowserType.LIGHTWEIGHT);
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

        //添加监听事件
        browser.addLoadListener(new LoadAdapter() {
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
                if (event.isMainFrame())
                    Platform.runLater(new Runnable() {
                        public void run() {
                            url_input.setText(event.getURL());
                        }
                    });
            }

            public void onStartLoadingFrame(StartLoadingEvent paramStartLoadingEvent) {
                if (paramStartLoadingEvent.isMainFrame()){
                    Platform.runLater(new Runnable() {
                        public void run() {
                            progressIndicator.setProgress(-1.0D);
                            //输出加载中记录输出两次 UI重复 //暂时无法解决,不在UI输出
                            print_info(String.format("Start Loading: [%s] Proxy: [%s]", paramStartLoadingEvent.getValidatedURL(), BrowserProxySetting));
                        }
                    });
                }
                super.onStartLoadingFrame(paramStartLoadingEvent);
            }

            public void onFailLoadingFrame(FailLoadingEvent paramFailLoadingEvent) {
                if (paramFailLoadingEvent.isMainFrame())
                    Platform.runLater(new Runnable() {
                        public void run() {
                            progressIndicator.setProgress(1.0D);
                            //输出加载中记录输出两次 UI重复 //暂时无法解决,不在UI输出
                            print_error(String.format("Fail Loading: [%s] Proxy: [%s]", paramFailLoadingEvent.getValidatedURL(), BrowserProxySetting));
                        }
                    });
                super.onFailLoadingFrame(paramFailLoadingEvent);
            }
            public void onFinishLoadingFrame(FinishLoadingEvent paramFinishLoadingEvent) {
                if (paramFinishLoadingEvent.isMainFrame()) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            progressIndicator.setProgress(1.0D);
                            //输出加载中记录输出两次 UI重复 //暂时无法解决,不在UI输出
                            print_info(String.format("Finish Loading: [%s] Proxy:[%s]", paramFinishLoadingEvent.getValidatedURL(), BrowserProxySetting));
                        }
                    });
                }
                super.onFinishLoadingFrame(paramFinishLoadingEvent);
            }
        });

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

        return browser;
    }
    public class MyNetworkDelegate extends DefaultNetworkDelegate {
        private boolean isCompleteAuth;
        private boolean isCancelAuth;
        private String captcha_url;
        private String url;
        private long current_id;
        private ByteArrayBuffer cap = new ByteArrayBuffer(4096);

        public MyNetworkDelegate(String captcha_url) {
            this.captcha_url = captcha_url;
            int i = captcha_url.indexOf("?");
            if (i != -1)
                this.url = captcha_url.substring(0, i);
            else
                this.url = captcha_url;
        }

        public void onBeforeSendHeaders(BeforeSendHeadersParams paramBeforeSendHeadersParams) {
            //发送请求前动作
            if (paramBeforeSendHeadersParams.getURL().startsWith(this.url)) {
                System.err.println("发起一次验证码请求。");
                paramBeforeSendHeadersParams.getHeadersEx().setHeader("Accept-Encoding", "");
                this.cap.clear();
            }
        }

        public void onDataReceived(DataReceivedParams paramDataReceivedParams) {
            //接收到数据时候的操作
            String current_url = paramDataReceivedParams.getURL();
            FileOutputStream yzm_fos = null;
            if (current_url.startsWith(this.url)) {
                try {
                    System.out.println("已获取验证码数据:" + current_url);
                    FXMLDocumentController.this.captcha_data = paramDataReceivedParams.getData();
                    this.cap.append(FXMLDocumentController.this.captcha_data, 0, FXMLDocumentController.this.captcha_data.length);
                    FXMLDocumentController.this.captcha_data = this.cap.toByteArray();

                    yzm_fos = new FileOutputStream(new File("tmp\\yzm.jpg"));
                    yzm_fos.write(FXMLDocumentController.this.captcha_data);
                    //ImageIO.write(ImageIO.read(new File("tmp\\yzm.jpg")),"JPG",new File("tmp\\yzm2.jpg"));

                    yzm_fos.flush();
                    yzm_fos.close();
                    //System.out.println("验证码数据:" + new String(FXMLDocumentController.this.captchadata).substring(0, 100));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            String charset = paramDataReceivedParams.getCharset();
            if (charset.equals("")) { charset = "utf-8"; }
            try {
                String receive = new String(paramDataReceivedParams.getData(), charset);
                String success_key = FXMLDocumentController.this.bro_id_success_regex_text.getText();
                if(containsMatchingSubString(receive, success_key)){
                    printlnInfoOnUIAndConsole(String.format("%s 页面存在登录成功关键字 [%s]", paramDataReceivedParams.getURL(), success_key));
                }

                String failure_key = FXMLDocumentController.this.bro_id_failure_regex_text.getText();
                if(containsMatchingSubString(receive, failure_key)){
                    printlnInfoOnUIAndConsole(String.format("%s 页面存在登录失败关键字 [%s]", paramDataReceivedParams.getURL(), failure_key));
                }

                String captcha_fail = FXMLDocumentController.this.bro_id_captcha_regex_text.getText();
                if(containsMatchingSubString(receive, captcha_fail)){
                    printlnInfoOnUIAndConsole(String.format("%s 页面存在验证码失败关键字 [%s]", paramDataReceivedParams.getURL(), captcha_fail));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

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
        setWithCheck(this.id_login_url_text, DefaultLoginUrl);
        //设置登录框
        setWithCheck(this.bro_id_user_ele_text, DefaultNameEleValue);
        setWithCheck(this.bro_id_user_ele_type_combo, DefaultNameEleType);
        //设置密码框
        setWithCheck(this.bro_id_pass_ele_text, DefaultPassEleValue);
        setWithCheck(this.bro_id_pass_ele_type_combo, DefaultPassEleType);
        //设置提交按钮
        setWithCheck(this.bro_id_submit_ele_text, DefaultSubmitEleValue);
        setWithCheck(this.bro_id_submit_ele_type_combo, DefaultSubmitEleType);
        //设置浏览器选项
        setWithCheck(this.bro_id_show_browser_check, DefaultShowBrowser);
        setWithCheck(this.bro_id_exclude_history_check, ExcludeHistory);
        setWithCheck(this.bro_id_load_sleep_combo, DefaultLoadTimeSleep);
        setWithCheck(this.bro_id_dict_compo_mode_combo, DictCompoMode);
        //设置关键字匹配
        setWithCheck(this.bro_id_success_regex_text, DefaultSuccessRegex);
        setWithCheck(this.bro_id_failure_regex_text, DefaultFailureRegex);
        setWithCheck(this.bro_id_captcha_regex_text, DefaultCaptchaRegex);
        //设置验证码识别开关
        setWithCheck(this.bro_id_captcha_switch_check, DefaultCaptchaSwitch);
        //设置验证码识别方式
        setWithCheck(DefaultLocalIdentify ? this.bro_id_yzm_local_ident_radio : this.bro_id_yzm_remote_ident_radio, true);
        //设置验证码属性
        setWithCheck(this.bro_id_captcha_url_text, DefaultCaptchaUrl);
        setWithCheck(this.bro_id_captcha_ele_text, DefaultCaptchaEleValue);
        setWithCheck(this.bro_id_captcha_ele_type_combo, DefaultCaptchaEleType);

        //模拟禁用动作
        this.bro_id_captcha_identify_action(null);


        //初始化窗口2的内容设置
        this.nor_id_request_text_area.setPromptText("POST /login.do\r\n" +
                "Host: 192.168.0.123:8080\r\n" +
                "User-Agent: User-Agent:Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36\r\n\r\n" +
                "username=$username$&passwd=$$password&captcha=$captcha$"
        );
    }
    @FXML  //程序帮助文档
    private void program_help(ActionEvent event) {
        String url = "http://www.cnblogs.com/SEC-fsq/p/5712792.html";
        OpenUrlWithLocalBrowser(url);
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
    @FXML  //查询YS信息,需要修改|删除
    private void ys_query_info_action(ActionEvent event) {
        String query_info_result = "";
        query_info_result = YunSu.getInfo(this.ys_username_text.getText().trim(), this.ys_password_text.getText().trim());
        this.bro_id_output_text_area.appendText(query_info_result);
    }
    @FXML  //点击 验证码识别开关需要 禁用|开启 的按钮
    private void bro_id_captcha_identify_action(ActionEvent event) {
        if (this.bro_id_captcha_switch_check.isSelected()) {

            this.bro_id_all_captcha_set_vbox.setDisable(false);
            this.ys_id_all_yunsu_set_vbox.setDisable(false);
        }
        else {
            this.bro_id_all_captcha_set_vbox.setDisable(true);

            this.ys_id_all_yunsu_set_vbox.setDisable(true);
        }
    }

    //第二页的图标设置
    @FXML
    private void nor_id_set_username_pos_action(ActionEvent event) {
        IndexRange selection = this.nor_id_request_text_area.getSelection();
        this.nor_id_request_text_area.replaceText(selection, "$username$");
    }
    @FXML
    private void nor_id_set_password_pos_action(ActionEvent event) {
        IndexRange selection = this.nor_id_request_text_area.getSelection();
        this.nor_id_request_text_area.replaceText(selection, "$password$");
    }
    @FXML
    private void nor_id_set_captcha_pos_action(ActionEvent event) {
        IndexRange selection = this.nor_id_request_text_area.getSelection();
        this.nor_id_request_text_area.replaceText(selection, "$captcha$");
    }
    @FXML
    private void nor_stop_send_crack(ActionEvent event) {
        this.is_stop_send_crack = Boolean.valueOf(true);
    }


    //主要爆破函数的修改
    @FXML
    private void startCrack(ActionEvent event) {
        //读取登录 URL
        String login_url = this.id_login_url_text.getText().trim();
        //登陆 URL 检查
        if (login_url.equals("") || !login_url.startsWith("http")) {
            new Alert(Alert.AlertType.NONE, "请输入完整的登录页面URL", new ButtonType[]{ButtonType.CLOSE}).show();
            return;
        }
        //基于登录URL初始化|URL更新|日志文件配置
        initBaseOnLoginUrl(login_url);

        //检查是否存在关键按钮信息修改,修改了的需要更新到全局变量,并且重新更新加载字典
        boolean isModifiedAuthFile = isModifiedAuthFile();
        boolean isModifiedLoginUrl = isModifiedLoginUrl(login_url);
        boolean isModifiedDictMode = isModifiedDictMode(this.bro_id_dict_compo_mode_combo.getValue());
        boolean isModifiedExcludeHistory = isModifiedExcludeHistory(this.bro_id_exclude_history_check.isSelected());
        if(isModifiedAuthFile||isModifiedLoginUrl||isModifiedDictMode||isModifiedExcludeHistory){
            //当登录URL或账号密码文件修改后,就需要重新更新
            printlnInfoOnUIAndConsole(String.format("加载账号密码文件开始..."));
            //点击登录后加载字典文件
            HashSet<UserPassPair> UserPassPairsHashSet = loadUserPassFile(UserNameFile, PassWordFile, UserPassFile, PairSeparator, DictCompoMode);
            //过滤历史字典记录,并转换为Array格式
            UserPassPairsArray = processedUserPassHashSet(UserPassPairsHashSet, HistoryFilePath, ExcludeHistory, UserMarkInPass);
        }
        //判断字典列表数量是否大于0
        if(UserPassPairsArray.length <= 0){
            printlnErrorOnUIAndConsole(String.format("加载账号密码文件完成 当前账号:密码数量[%s], 跳过爆破操作...", UserPassPairsArray.length));
            return;
        } else {
            printlnInfoOnUIAndConsole(String.format("加载账号密码文件完成 当前账号:密码数量[%s], 开始爆破操作...", UserPassPairsArray.length));
        }

        //获取云速认证信息//可以删除,慢慢来
        String ys_username = this.ys_username_text.getText().trim();
        String ys_password = this.ys_password_text.getText().trim();
        String ys_soft_id = this.ys_soft_id_text.getText().trim();
        String ys_soft_key = this.ys_soft_key_text.getText().trim();
        String ys_type_id = this.ys_type_id_text.getText().trim();
        Integer yzm_query_timeout = this.yzm_query_timeout_combo.getValue();
        YunSuConfig yunSuConfig = new YunSuConfig(ys_username, ys_password, ys_soft_id, ys_soft_key, ys_type_id, yzm_query_timeout.toString());

        //验证码输入URL
        String captcha_url_input = this.bro_id_captcha_url_text.getText().trim();

        //浏览器操作模式模式
        if (this.id_browser_op_mode_tab.isSelected()) {
            //存在验证码时监测云速账号密码是否为空//后续需要修改删除
            if(this.bro_id_captcha_switch_check.isSelected()) {
                if ((ys_username.equals("")) || (ys_password.equals(""))) {
                    new Alert(Alert.AlertType.NONE, "云速账号密码不能为空", new ButtonType[]{ButtonType.CLOSE});
                    return;
                }
            }

            //获取用户名框框的内容
            String bro_user_input = this.bro_id_user_ele_text.getText().trim();
            String bro_user_option = this.bro_id_user_ele_type_combo.getValue();
            if (bro_user_input.equals("")) {
                this.bro_id_user_ele_text.requestFocus();
                return;
            }

            //获取密码框元素的内容
            String bro_pass_input = this.bro_id_pass_ele_text.getText().trim();
            String bro_pass_option = this.bro_id_pass_ele_type_combo.getValue();
            if (bro_pass_input.equals("")) {
                this.bro_id_pass_ele_text.requestFocus();
                return;
            }

            //获取验证码框元素的内容
            String bro_captcha_input = this.bro_id_captcha_ele_text.getText().trim();
            String bro_captcha_option = this.bro_id_captcha_ele_type_combo.getValue();
            if (bro_captcha_input.equals("")) {
                this.bro_id_user_ele_text.requestFocus();
                return;
            }

            //获取验证码输入URL的内容
            String bro_captcha_url_input = this.bro_id_captcha_url_text.getText().trim();
            if (bro_captcha_url_input.equals("")) {
                this.bro_id_captcha_url_text.requestFocus();
                return;
            }

            //登录按钮内容
            String bro_submit_input = this.bro_id_submit_ele_text.getText().trim();
            String bro_submit_option = this.bro_id_submit_ele_type_combo.getValue();

            //初始化浏览器
            Browser browser = initJxBrowserInstance();

            //浏览器代理设置
            if (BrowserProxySetting != null) {
                //参考 使用代理 https://www.kancloud.cn/neoman/ui/802531
                browser.getContext().getProxyService().setProxyConfig(getBrowserProxy());
            }

            //设置JxBrowser中网络委托的对象，以实现对浏览器的网络请求和响应的控制和处理。//不知道有啥用,可能是为了提前加载验证码
            browser.getContext().getNetworkService().setNetworkDelegate(new MyNetworkDelegate(captcha_url_input));

            //开启一个新的线程进行爆破操作
            new Thread(new Runnable() {
                public void run() {
                    try {
                        //请求间隔设置
                        Integer req_interval = FXMLDocumentController.this.bro_id_load_sleep_combo.getValue();

                        //遍历账号密码字典
                        for (int index = 0; index < UserPassPairsArray.length; index++) {
                            UserPassPair userPassPair = UserPassPairsArray[index];
                            print_info(String.format("Task Progress [%s/%s] <--> [%s]", index + 1, UserPassPairsArray.length, userPassPair));

                            //清理所有Cookie //可能存在问题,比如验证码, 没有Cookie会怎么样呢?
                            AutoClearAllCookies(browser);

                            //清空上一次记录的的验证码数据
                            FXMLDocumentController.this.captcha_data = null;

                            //输出当前即将测试的数据
                            printlnInfoOnUIAndConsole(String.format("当前进度 [%s/%s] <--> [%s] [%s]", index+1, UserPassPairsArray.length, userPassPair, login_url));

                            try {
                                Browser.invokeAndWaitFinishLoadingMainFrame(browser, new Callback<Browser>() {
                                            public void invoke(Browser browser) {
                                                browser.loadURL(login_url);
                                            }
                                        }
                                        , 120);
                            } catch (IllegalStateException illegalStateException) {
                                String illegalStateExceptionMessage = illegalStateException.getMessage();
                                System.out.println(illegalStateExceptionMessage);
                                if (illegalStateExceptionMessage.contains("Channel is already closed")) {
                                    printlnErrorOnUIAndConsole("停止测试");
                                    break;
                                }
                            } catch (Exception exception) {
                                printlnErrorOnUIAndConsole("访问超时");
                                exception.printStackTrace();
                                continue;
                            }

                            //进行线程延迟
                            if (req_interval.intValue() > 0) {Thread.sleep(req_interval / 2); }

                            //加载URl文档
                            DOMDocument document = browser.getDocument();

                            String result_action = null;
                            //输入用户名
                            result_action = findElementAndInput(document, bro_user_input, bro_user_option, userPassPair.getUsername());

                            //处理资源寻找状态
                            if(!"success".equals(result_action)){
                                printlnErrorOnUIAndConsole(String.format("Error For Location [%s] <--> Action: [%s]", bro_pass_input, result_action));
                                if("break".equals(result_action)) break; else if("continue".equals(result_action)) continue;
                            }

                            result_action = findElementAndInput(document,bro_pass_input, bro_pass_option, userPassPair.getPassword()
                            );

                            //处理资源寻找状态
                            if(!"success".equals(result_action)){
                                printlnErrorOnUIAndConsole(String.format("Error For Location [%s] <--> Action: [%s]", bro_pass_input, result_action));
                                if("break".equals(result_action)) break; else if("continue".equals(result_action)) continue;
                            }

                            //获取验证码并进行识别
                            if (FXMLDocumentController.this.bro_id_captcha_switch_check.isSelected()) {
                                //captcha_data 在
                                if (FXMLDocumentController.this.captcha_data == null) {
                                    printlnErrorOnUIAndConsole("获取验证码失败 (captcha数据为空)");
                                    continue;
                                }

                                //验证码识别 //云打码识别
                                if (bro_id_yzm_remote_ident_radio.isSelected()) {
                                    String result = YunSu.createByPost(ys_username, ys_password, ys_type_id, yzm_query_timeout.toString(), ys_soft_id, ys_soft_key, FXMLDocumentController.this.captcha_data);
                                    // int k = result.indexOf("|");
                                    if (result.contains("Error_Code")) {
                                        printlnErrorOnUIAndConsole(String.format("获取验证码失败 (查询结果%s)", result));
                                        break; //continue;
                                    }
                                    captchaText = YunSu.getResult(result);
                                }
                                //验证码识别//本地识别
                                if (bro_id_yzm_local_ident_radio.isSelected()) {
                                    captchaText = YzmToText.getCode();
                                }

                                //输出已经识别的验证码记录
                                printlnInfoOnUIAndConsole(String.format("已识别验证码为:%s", captchaText));

                                //定位验证码输入框并填写验证码
                                String action_captcha = findElementAndInput(document,bro_captcha_input, bro_captcha_option, captchaText);
                            }

                            //处理资源寻找状态
                            if(!"success".equals(result_action)){
                                printlnErrorOnUIAndConsole(String.format("Error For Location [%s] <--> Action: [%s]", bro_pass_input, result_action));
                                if("break".equals(result_action)) break; else if("continue".equals(result_action)) continue;
                            }

                            //定位提交按钮, 并填写按钮
                            try {
                                InputElement submitElement = findElementByOption(document, bro_submit_input, bro_submit_option);
                                submitElement.click();
                            } catch (Exception e) {
                                try {
                                    document.findElement(By.cssSelector("[type=submit]")).click();
                                } catch (IllegalStateException ee) {
                                    break;
                                }
                            }

                            browser.executeCommand(EditorCommand.INSERT_NEW_LINE);

                            //进行线程延迟
                            if (req_interval.intValue() > 0) {Thread.sleep(req_interval / 2); }

                            String cur_url = browser.getURL();
                            String cur_title = browser.getTitle();
                            int cur_length = browser.getHTML().length();

                            //判断是否跳转
                            boolean isPageForward = !urlRemoveQuery(login_url).equals(urlRemoveQuery(cur_url));
                            //进行历史记录
                            writeUserPassPairToFile(HistoryFilePath, ":", userPassPair);
                            String title = "是否跳转,登录URL,测试账号,测试密码,跳转URL,网页标题,内容长度";
                            writeTitleToFile(LogRecodeFilePath, title);
                            String content = String.format("%s,%s,%s,%s,%s,%s,%s", isPageForward, login_url, userPassPair.getUsername(), userPassPair.getPassword(), cur_url, cur_title, cur_length);
                            writeLineToFile(LogRecodeFilePath, content);
                            printlnInfoOnUIAndConsole(String.format("登录URL: %s\n是否跳转: %s\n测试账号: %s\n测试密码: %s\n跳转URL: %s\n网页标题: %s\n内容长度: %s\n",
                                    login_url, isPageForward, userPassPair.getUsername(), userPassPair.getPassword(), cur_url, cur_title, cur_length));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        browser.dispose();
                        printlnInfoOnUIAndConsole("所有任务爆破结束");
                    }
                }
            }).start();
        }
        //普通爆破模式
        else if (this.id_normal_op_mode_tab.isSelected()) {
            if ((this.nor_id_captcha_ident_check.isSelected()) && ((ys_username.equals("")) || (ys_password.equals("")))) {
                new Alert(Alert.AlertType.NONE, "云速账号密码不能为空", new ButtonType[]{ButtonType.CLOSE});
                return;
            }
            new Thread(new Runnable() {
                public void run() {
                    FXMLDocumentController.this.is_stop_send_crack = Boolean.valueOf(false);

                    if (FXMLDocumentController.this.queue == null)
                        FXMLDocumentController.this.queue = new LinkedBlockingQueue(8100);
                    else {
                        FXMLDocumentController.this.queue.clear();
                    }

                    Integer thread = FXMLDocumentController.this.nor_id_threads_combo.getValue();
                    if (thread == null) {
                        thread = Integer.valueOf(1);
                    }
                    Integer timeout2 = (Integer) FXMLDocumentController.this.nor_id_timeout_combo.getValue();
                    if (timeout2 == null) {
                        timeout2 = Integer.valueOf(3000);
                    }
                    CountDownLatch cdl = new CountDownLatch(thread.intValue());

                    String request = FXMLDocumentController.this.nor_id_request_text_area.getText();
                    if (request.equals("")) {
                        return;
                    }

                    String schema = login_url.startsWith("http") ? "http" : "https";
                    String keyword2 = FXMLDocumentController.this.nor_id_success_keys_text.getText();
                    String captchaurlinput2 = FXMLDocumentController.this.nor_id_captcha_url_text.getText();

                    for (int i = 0; i < thread.intValue(); i++) {
                        new Thread(
                                new SendCrackThread(cdl, FXMLDocumentController.this.queue, request, schema, FXMLDocumentController.this.nor_id_output_text_area, keyword2,
                                        captchaurlinput2, timeout2, Boolean.valueOf(FXMLDocumentController.this.nor_id_captcha_ident_check.isSelected()), yunSuConfig, login_url))
                                .start();
                    }
                    try {
                        for (int index = 0; index < UserPassPairsArray.length; index++) {
                            UserPassPair userPassPair = UserPassPairsArray[index];
                            print_info(String.format("Current Progress %s/%s <--> %s", index, UserPassPairsArray.length, userPassPair.toString()));

                            if (FXMLDocumentController.this.is_stop_send_crack.booleanValue()) {
                                FXMLDocumentController.this.queue.clear();
                                cdl.await();
                                return;
                            }
                            do
                                Thread.sleep(300L);
                            while (FXMLDocumentController.this.queue.size() > 8000);

                            FXMLDocumentController.this.queue.add(userPassPair);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        while (cdl.getCount() > 0L) {
                            if (FXMLDocumentController.this.is_stop_send_crack.booleanValue()) {
                                FXMLDocumentController.this.queue.clear();
                                break;
                            }
                            Thread.sleep(3000L);
                        }
                        cdl.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    printlnInfoOnUIAndConsole("所有任务爆破结束");
                }
            }).start();
        }
    }

}
