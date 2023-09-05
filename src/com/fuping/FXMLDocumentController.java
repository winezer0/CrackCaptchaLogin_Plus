package com.fuping;

import com.fuping.BrowserUtils.MyDialogHandler;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static com.fuping.BrowserUtils.BrowserUitls.*;
import static com.fuping.CommonUtils.Utils.*;
import static com.fuping.LoadConfig.MyConst.*;
import static com.fuping.LoadDict.LoadDictUtils.userPassPairsHashSet;
import static com.fuping.PrintLog.PrintLog.print_info;

public class FXMLDocumentController implements Initializable {


    @FXML
    private TextField id_base_url_input;

    @FXML
    private Button id_crack;

    @FXML
    private RadioButton bro_id_user_by_id;

    @FXML
    private ToggleGroup bro_user_name_ele_group;

    @FXML
    private RadioButton bro_id_user_by_name;

    @FXML
    private TextField bro_id_user_ele_input;

    @FXML
    private RadioButton bro_id_pass_by_id;

    @FXML
    private ToggleGroup bro_id_password_ele_group;

    @FXML
    private RadioButton bro_id_pass_by_name;

    @FXML
    private TextField bro_id_pass_ele_input;

    @FXML
    private RadioButton bro_id_captcha_by_id;

    @FXML
    private ToggleGroup bro_id_captcha_ele_group;

    @FXML
    private RadioButton bro_id_captcha_by_name;

    @FXML
    private TextField bro_id_captcha_ele_input;

    @FXML
    private ToggleGroup captchagroup;

    @FXML
    private TextField bro_id_captcha_url_input;

    @FXML
    private TextField ys_soft_id;

    @FXML
    private TextField ys_soft_key;

    @FXML
    private ComboBox<Integer> ys_query_timeout;

    @FXML
    private Button ys_query_info;

    @FXML
    private TextField ys_username;

    @FXML
    private PasswordField ys_password;

    @FXML
    private TextField ys_type_id;

    @FXML
    private Hyperlink ys_type_help;

    @FXML
    private TextArea bro_id_output;

    @FXML
    private RadioButton bro_id_submit_by_id;

    @FXML
    private ToggleGroup bro_id_submit_ele_group;

    @FXML
    private RadioButton bro_id_submit_by_name;

    @FXML
    private TextField bro_id_submit_ele_input;

    @FXML
    private TextField bro_id_success_keyword;

    @FXML
    private ComboBox<Integer> bro_id_req_interval;

    @FXML
    private CheckBox bro_id_have_captcha;

    @FXML
    private HBox id_bro_id_captcha_ele;

    @FXML
    private HBox id_bro_id_captcha_url;

    @FXML
    private HBox id_ys_open;

    @FXML
    private HBox id_ys_info;

    @FXML
    private HBox id_ys_config;

    @FXML
    private RadioButton bro_id_user_by_class;

    @FXML
    private RadioButton bro_id_user_by_css;

    @FXML
    private RadioButton bro_id_user_by_xpath;

    @FXML
    private RadioButton bro_id_pass_by_class;

    @FXML
    private RadioButton bro_id_pass_by_css;

    @FXML
    private RadioButton bro_id_pass_by_xpath;

    @FXML
    private RadioButton bro_id_submit_by_class;

    @FXML
    private RadioButton bro_id_submit_by_css;

    @FXML
    private RadioButton bro_id_submit_by_xpath;

    @FXML
    private RadioButton bro_id_captcha_by_class;

    @FXML
    private RadioButton bro_id_captcha_by_css;

    @FXML
    private RadioButton bro_id_captcha_by_xpath;

    @FXML
    private CheckBox bro_id_show_browser;

    @FXML
    private TextArea nor_id_request_area;

    @FXML
    private HBox id_hbox221;

    @FXML
    private TextField nor_id_captcha_url_ele_input2;

    @FXML
    private TextField id_sucess_keyword2;

    @FXML
    private Button nor_id_set_username_pos;

    @FXML
    private Button nor_id_set_password_pos;

    @FXML
    private CheckBox nor_id_have_captcha;

    @FXML
    private Button nor_id_set_captcha_pos;

    @FXML
    private ComboBox<Integer> nor_id_threads;

    @FXML
    private ComboBox<Integer> nor_id_timeout;

    @FXML
    private TextArea nor_id_output_area;

    @FXML
    private TabPane id_tab_pane;

    @FXML
    private Tab id_browser_op_mode;

    @FXML
    private Tab id_normal_op_mode;

    @FXML
    private RadioButton bro_yzm_yunRadioBtn;

    @FXML
    private RadioButton bro_yzm_localRadioBtn;

    @FXML
    private ToggleGroup bro_yzm_group;

    private byte[] captcha_data;
    private Stage primaryStage;
    private LinkedBlockingQueue<UserPassPair> queue;
    private Boolean is_stop_send_crack;

    private String captchaText = null;

    private List<Cookie> cookies = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //初始化窗口内容设置
        this.nor_id_request_area.setPromptText(
                "POST /login.do\r\n" +
                        "Host: 192.168.0.123:8080\r\n" +
                        "User-Agent: User-Agent:Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36\r\n\r\n" +
                        "username=$username$&passwd=$$password&captcha=$captcha$"
        );

        ObservableList to = FXCollections.observableArrayList(new Integer[]{Integer.valueOf(30), Integer.valueOf(50), Integer.valueOf(60), Integer.valueOf(70), Integer.valueOf(80), Integer.valueOf(90), Integer.valueOf(100)});
        this.ys_query_timeout.setItems(to);

        ObservableList interval = FXCollections.observableArrayList(new Integer[]{Integer.valueOf(0), Integer.valueOf(500), Integer.valueOf(1000), Integer.valueOf(2000), Integer.valueOf(3000), Integer.valueOf(5000), Integer.valueOf(8000),
                Integer.valueOf(10000), Integer.valueOf(15000), Integer.valueOf(20000)});
        this.bro_id_req_interval.setItems(interval);

        ObservableList timeout2 = FXCollections.observableArrayList(new Integer[]{Integer.valueOf(1000), Integer.valueOf(2000), Integer.valueOf(3000), Integer.valueOf(4000), Integer.valueOf(5000), Integer.valueOf(8000), Integer.valueOf(10000)});

        this.nor_id_timeout.setItems(timeout2);

        ObservableList threads = FXCollections.observableArrayList(new Integer[]{Integer.valueOf(1), Integer.valueOf(3), Integer.valueOf(5), Integer.valueOf(10), Integer.valueOf(20), Integer.valueOf(30), Integer.valueOf(50), Integer.valueOf(60), Integer.valueOf(70), Integer.valueOf(80), Integer.valueOf(90),
                Integer.valueOf(100)});

        this.nor_id_threads.setItems(threads);
    }

    @FXML
    private void ys_query_info(ActionEvent event) {
        String result = "";
        result = YunSu.getInfo(this.ys_username.getText().trim(), this.ys_password.getText().trim());
        this.bro_id_output.appendText(result);
    }

    @FXML
    private void startCrack(ActionEvent event) {

        //读取登录 URL
        String login_url = this.id_base_url_input.getText().trim();
        //登陆 URL 检查
        if (login_url.equals("") || !login_url.startsWith("http")) {
            new Alert(Alert.AlertType.NONE, "请输入完整的登录页面URL", new ButtonType[]{ButtonType.CLOSE}).show();
            return;
        }

        //基于登录URL初始化日志文件配置
        //过滤历史字典记录,并转换为Array格式
        initDynamicFileBaseOnLoginUrl(login_url);
        UserPassPair[] userPassPairsArray = processedUserPassHashSet(userPassPairsHashSet);
        this.bro_id_output.appendText(String.format("[*] 动态加载账号密码对数量: [%s]\n\n", userPassPairsArray.length));

        //验证码输入URL
        String captcha_url_input = this.bro_id_captcha_url_input.getText().trim();

        //登录按钮内容
        String bro_submit_input = this.bro_id_submit_ele_input.getText().trim();

        //获取云速认证信息
        String ys_username = this.ys_username.getText().trim();
        String ys_password = this.ys_password.getText().trim();
        String ys_soft_id = this.ys_soft_id.getText().trim();
        String ys_soft_key = this.ys_soft_key.getText().trim();
        String ys_type_id = this.ys_type_id.getText().trim();
        Integer ys_query_timeout = this.ys_query_timeout.getValue();
        String ys_query_timeout2;

        if (ys_query_timeout == null)  ys_query_timeout2 = "60";
        else {  ys_query_timeout2 = ys_query_timeout.toString(); }

        YunSuConfig yunSuConfig = new YunSuConfig(ys_username, ys_password, ys_soft_id, ys_soft_key, ys_type_id, ys_query_timeout2);

        //浏览器操作模式模式
        if (this.id_browser_op_mode.isSelected()) {
            //存在验证码时监测云速账号密码是否为空//后续需要修改删除
           if(this.bro_id_have_captcha.isSelected()) {
                if ((ys_username.equals("")) || (ys_password.equals(""))) {
                    new Alert(Alert.AlertType.NONE, "云速账号密码不能为空", new ButtonType[]{ButtonType.CLOSE});
                    return;
                }
            }
            //获取用户名框框的内容
            String bro_user_input = this.bro_id_user_ele_input.getText().trim();
            if (bro_user_input.equals("")) {
                this.bro_id_user_ele_input.requestFocus();
                return;
            }
            //获取密码框元素的内容
            String bro_pass_input = this.bro_id_pass_ele_input.getText().trim();
            if (bro_pass_input.equals("")) {
                this.bro_id_pass_ele_input.requestFocus();
                return;
            }

            //获取验证码框元素的内容
            String bro_captcha_input = this.bro_id_captcha_ele_input.getText().trim();
            if (bro_captcha_input.equals("")) {
                this.bro_id_user_ele_input.requestFocus();
                return;
            }

            //获取验证码输入URL的内容
            String bro_captcha_url_input = this.bro_id_captcha_url_input.getText().trim();
            if (bro_captcha_url_input.equals("")) {
                this.bro_id_captcha_url_input.requestFocus();
                return;
            }

            //创建窗口对象 JavaFX的Stage类是JavaFX应用程序创建窗口的基础
            this.primaryStage = new Stage();
            //创建浏览器对象 轻量级对象
            //jxBrowser 支持两种渲染模式： BrowserType.LIGHTWEIGHT 轻量级渲染模式， BrowserType.HEAVYWEIGHT重量级渲染模式。
            //轻量级渲染模式是通过CPU来加速渲染的，速度更快，占用更少的内存。在轻量级渲染模式下，Chromium引擎会在后台使用CPU渲染网页，然后将网页的图像保存在共享内存中。
            //重量级渲染模式则使用GPU加速渲染，相对于轻量级模式来说，它需要占用更多的内存，但在某些场景下可能会有更好的性能和更高的渲染质量。

            Browser browser = new Browser(BrowserType.LIGHTWEIGHT);

            //浏览器代理设置
            if (BrowserProxySetting != null) {
                //参考 使用代理 https://www.kancloud.cn/neoman/ui/802531
                browser.getContext().getProxyService().setProxyConfig(getBrowserProxy());
            }

            //设置JxBrowser中网络委托的对象，以实现对浏览器的网络请求和响应的控制和处理。//不知道有啥用,可能是为了提前加载验证码
            browser.getContext().getNetworkService().setNetworkDelegate(new MyNetworkDelegate(captcha_url_input));

            jxrBrowserInitUi(browser);

            //开启一个新的线程进行爆破操作
            new Thread(new Runnable() {
                public void run() {
                    try {
                        //请求间隔设置
                        Integer req_interval = FXMLDocumentController.this.bro_id_req_interval.getValue();
                        //设置默认的请求间隔
                        if (FXMLDocumentController.this.bro_id_req_interval.getValue() == null) {
                            req_interval = Integer.valueOf(1500);
                        }

                        //遍历账号密码字典
                        for (int index = 0; index < userPassPairsArray.length; index++) {
                            UserPassPair userPassPair = userPassPairsArray[index];
                            print_info(String.format("Task Progress [%s/%s] <--> [%s]", index + 1, userPassPairsArray.length, userPassPair));

                            //清理所有Cookie //可能存在问题,比如验证码, 没有Cookie会怎么样呢?
                            AutoClearAllCookies(browser);

                            //清空上一次记录的的验证码数据
                            FXMLDocumentController.this.captcha_data = null;

                            //输出当前即将测试的数据
                            int finalIndex = index;
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    String appendText = String.format("[*] 当前进度 [%s/%s] <--> [%s]\n", finalIndex + 1, userPassPairsArray.length, userPassPair);
                                    FXMLDocumentController.this.bro_id_output.appendText(appendText);
                                }
                            });

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
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            FXMLDocumentController.this.bro_id_output.appendText("停止测试\n");
                                        }
                                    });
                                    break;
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        FXMLDocumentController.this.bro_id_output.appendText("访问超时\r\n");
                                    }
                                });
                                continue;
                            }

                            //进行线程延迟
                            if (req_interval.intValue() > 0) {
                                Thread.sleep(req_interval / 2);
                            }

                            //加载URl文档
                            DOMDocument document = browser.getDocument();

                            try {
                                //输入用户名元素
                               InputElement userElement = findElementByAny(document,
                                       bro_user_input,
                                       FXMLDocumentController.this.bro_id_user_by_id.isSelected(),
                                       FXMLDocumentController.this.bro_id_user_by_name.isSelected(),
                                       FXMLDocumentController.this.bro_id_user_by_class.isSelected(),
                                       FXMLDocumentController.this.bro_id_user_by_css.isSelected(),
                                       FXMLDocumentController.this.bro_id_user_by_xpath.isSelected()
                                );

                                userElement.setValue(userPassPair.getUsername());
                            } catch (IllegalStateException illegalStateException) {
                                String m = illegalStateException.getMessage();
                                System.out.println(m);
                                if (m.contains("Channel is already closed")) {
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            FXMLDocumentController.this.bro_id_output.appendText("IllegalStateException 异常,浏览器已经关闭, 停止测试\n");
                                        }
                                    });
                                    break;
                                }
                                illegalStateException.printStackTrace();
                            } catch (NullPointerException nullPointerException) {
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        FXMLDocumentController.this.bro_id_output.appendText("nullPointerException 异常, 定位元素失败,停止测试\n");
                                    }
                                });
                                break; //continue;
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                continue;
                            }


                            try {
                                //输入密码元素 //需要添加输入XPath|CSS元素
                                InputElement passElement = findElementByAny(document,
                                        bro_pass_input,
                                        FXMLDocumentController.this.bro_id_pass_by_id.isSelected(),
                                        FXMLDocumentController.this.bro_id_pass_by_name.isSelected(),
                                        FXMLDocumentController.this.bro_id_pass_by_class.isSelected(),
                                        FXMLDocumentController.this.bro_id_pass_by_css.isSelected(),
                                        FXMLDocumentController.this.bro_id_pass_by_xpath.isSelected()
                                );
                                passElement.setValue(userPassPair.getPassword());
                            } catch (IllegalStateException illegalStateException) {
                                String m = illegalStateException.getMessage();
                                System.out.println(m);
                                if (m.contains("Channel is already closed")) {
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            FXMLDocumentController.this.bro_id_output.appendText("IllegalStateException 异常,浏览器已经关闭, 停止测试\n");
                                        }
                                    });
                                    break;
                                }
                                illegalStateException.printStackTrace();
                            } catch (NullPointerException nullPointerException) {
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        FXMLDocumentController.this.bro_id_output.appendText("nullPointerException 异常, 定位元素失败,停止测试\n");
                                    }
                                });
                                break; //continue;
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                continue;
                            }


                            //获取验证码并进行识别
                            if (FXMLDocumentController.this.bro_id_have_captcha.isSelected()) {
                                //captcha_data 在
                                if (FXMLDocumentController.this.captcha_data == null) {
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            FXMLDocumentController.this.bro_id_output.appendText("获取验证码失败\n");
                                        }
                                    });
                                    continue;
                                }

                                //验证码识别 //云打码识别
                                if (bro_yzm_yunRadioBtn.isSelected()) {
                                    String result = YunSu.createByPost(ys_username, ys_password, ys_type_id, ys_query_timeout2, ys_soft_id, ys_soft_key, FXMLDocumentController.this.captcha_data);
                                    System.out.println("查询结果:" + result);
                                    // int k = result.indexOf("|");
                                    if (result.contains("Error_Code")) {
                                        Platform.runLater(new Runnable() {
                                            public void run() {
                                                FXMLDocumentController.this.bro_id_output.appendText("获取验证码失败\n");
                                            }
                                        });
                                        //continue;
                                        break;
                                    }
                                    captchaText = YunSu.getResult(result);
                                }
                                //验证码识别//本地识别
                                if (bro_yzm_localRadioBtn.isSelected()) {
                                    captchaText = YzmToText.getCode();
                                }

                                //输出已经识别的验证码记录
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        FXMLDocumentController.this.bro_id_output.appendText("已识别验证码为:" + captchaText);
                                    }
                                });

                                //定位验证码输入框并填写验证码
                                try {
                                    InputElement captchaElement = findElementByAny(document,
                                            bro_captcha_input,
                                            FXMLDocumentController.this.bro_id_captcha_by_id.isSelected(),
                                            FXMLDocumentController.this.bro_id_captcha_by_name.isSelected(),
                                            FXMLDocumentController.this.bro_id_captcha_by_class.isSelected(),
                                            FXMLDocumentController.this.bro_id_captcha_by_css.isSelected(),
                                            FXMLDocumentController.this.bro_id_captcha_by_xpath.isSelected()
                                    );

                                    captchaElement.setValue(captchaText);
                                } catch (IllegalStateException e) {
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            FXMLDocumentController.this.bro_id_output.appendText("IllegalStateException异常,无法获取登录按钮,停止测试\n");
                                        }
                                    });
                                }
                            }

                            //定位提交按钮, 并填写按钮
                            try {
                                InputElement submitElement = findElementByAny(document,
                                        bro_submit_input,
                                        FXMLDocumentController.this.bro_id_submit_by_id.isSelected(),
                                        FXMLDocumentController.this.bro_id_submit_by_name.isSelected(),
                                        FXMLDocumentController.this.bro_id_submit_by_class.isSelected(),
                                        FXMLDocumentController.this.bro_id_submit_by_css.isSelected(),
                                        FXMLDocumentController.this.bro_id_submit_by_xpath.isSelected()
                                );
                                submitElement.click();
                            } catch (Exception e) {
                                try {
                                    document.findElement(By.cssSelector("[type=submit]")).click();
                                } catch (IllegalStateException ee) {
                                    break;
                                }
                            }

                            browser.executeCommand(EditorCommand.INSERT_NEW_LINE);
                            if (req_interval.intValue() != 0) {
                                Thread.sleep(req_interval.intValue());
                            }
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    String cur_url = browser.getURL();
                                    String cur_title = browser.getTitle();
                                    int cur_length = browser.getHTML().length();

                                    //进行历史记录
                                    writeUserPassPairToFile(HistoryFilePath, ":", userPassPair);
                                    String title = "登录URL,账号,密码,跳转URL,网页标题,内容长度";
                                    writeTitleToFile(LogRecodeFilePath, title);
                                    String content = String.format("%s,%s,%s,%s,%s,%s", login_url, userPassPair.getUsername(), userPassPair.getPassword(), cur_url, cur_title, cur_length);
                                    writeLineToFile(LogRecodeFilePath, content);

                                    //窗口输出记录
                                    String appendText = String.format("登录URL%s,账号%s,密码%s,跳转URL%s,网页标题%s,内容长度%s\r\n\r\n", login_url, userPassPair.getUsername(), userPassPair.getPassword(), cur_url, cur_title, cur_length);
                                    FXMLDocumentController.this.bro_id_output.appendText(appendText);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        //普通爆破模式
        else if (this.id_normal_op_mode.isSelected()) {
            if ((this.nor_id_have_captcha.isSelected()) && ((ys_username.equals("")) || (ys_password.equals("")))) {
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

                    Integer thread = (Integer) FXMLDocumentController.this.nor_id_threads.getValue();
                    if (thread == null) {
                        thread = Integer.valueOf(1);
                    }
                    Integer timeout2 = (Integer) FXMLDocumentController.this.nor_id_timeout.getValue();
                    if (timeout2 == null) {
                        timeout2 = Integer.valueOf(3000);
                    }
                    CountDownLatch cdl = new CountDownLatch(thread.intValue());

                    String request = FXMLDocumentController.this.nor_id_request_area.getText();
                    if (request.equals("")) {
                        return;
                    }

                    String schema = login_url.startsWith("http") ? "http" : "https";
                    String keyword2 = FXMLDocumentController.this.id_sucess_keyword2.getText();
                    String captchaurlinput2 = FXMLDocumentController.this.nor_id_captcha_url_ele_input2.getText();

                    for (int i = 0; i < thread.intValue(); i++) {
                        new Thread(
                                new SendCrackThread(cdl, FXMLDocumentController.this.queue, request, schema, FXMLDocumentController.this.nor_id_output_area, keyword2,
                                        captchaurlinput2, timeout2, Boolean.valueOf(FXMLDocumentController.this.nor_id_have_captcha.isSelected()), yunSuConfig, login_url))
                                .start();
                    }
                    try {
                        for (int index = 0; index < userPassPairsArray.length; index++) {
                            UserPassPair userPassPair = userPassPairsArray[index];
                            print_info(String.format("Current Progress %s/%s <--> %s", index, userPassPairsArray.length, userPassPair.toString()));

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
                    System.out.println("爆破结束");
                    Platform.runLater(new Runnable() {
                        public void run() {
                            FXMLDocumentController.this.nor_id_output_area.appendText("爆破结束。\n");
                        }
                    });
                }
            }).start();
        }
    }

    @FXML
    private void ys_type_help(ActionEvent event) {
        String url = "http://www.ysdm.net/home/PriceType";
        OpenUrlWithLocalBrowser(url);
    }

    @FXML
    private void help(ActionEvent event) {
        String url = "http://www.cnblogs.com/SEC-fsq/p/5712792.html";
        OpenUrlWithLocalBrowser(url);
    }

    @FXML
    private void show_browser(ActionEvent event) {
        if (this.primaryStage == null) {
            return;
        }
        if (this.bro_id_show_browser.isSelected())
            this.primaryStage.show();
        else
            this.primaryStage.hide();
    }

    @FXML
    private void ydm_mode(ActionEvent event) {
        this.id_bro_id_captcha_ele.setDisable(false);
        this.id_bro_id_captcha_url.setDisable(false);
        this.id_ys_open.setDisable(false);
        this.id_ys_info.setDisable(false);
        this.id_ys_config.setDisable(false);
    }

    @FXML
    private void bro_local_dm(ActionEvent event) {
        this.id_ys_info.setDisable(true);
        this.id_ys_config.setDisable(true);
    }

    @FXML
    private void bro_check_have_captcha(ActionEvent event) {
        if (this.bro_id_have_captcha.isSelected()) {
            this.id_bro_id_captcha_ele.setDisable(false);
            this.id_bro_id_captcha_url.setDisable(false);
            this.id_ys_open.setDisable(false);
            this.id_ys_info.setDisable(false);
            this.id_ys_config.setDisable(false);
            this.bro_yzm_localRadioBtn.setDisable(false);
            this.bro_yzm_yunRadioBtn.setDisable(false);
        } else {
            this.id_bro_id_captcha_ele.setDisable(true);
            this.id_bro_id_captcha_url.setDisable(true);
            this.id_ys_open.setDisable(true);
            this.id_ys_info.setDisable(true);
            this.id_ys_config.setDisable(true);
            this.bro_yzm_localRadioBtn.setDisable(true);
            this.bro_yzm_yunRadioBtn.setDisable(true);
        }
    }

    @FXML
    private void nor_set_username_pos(ActionEvent event) {
        IndexRange selection = this.nor_id_request_area.getSelection();
        this.nor_id_request_area.replaceText(selection, "$username$");
    }

    @FXML
    private void nor_set_password_pos(ActionEvent event) {
        IndexRange selection = this.nor_id_request_area.getSelection();
        this.nor_id_request_area.replaceText(selection, "$password$");
    }

    @FXML
    private void nor_set_captcha_pos(ActionEvent event) {
        IndexRange selection = this.nor_id_request_area.getSelection();
        this.nor_id_request_area.replaceText(selection, "$captcha$");
    }

    @FXML
    private void nor_stop_send_crack(ActionEvent event) {
        this.is_stop_send_crack = Boolean.valueOf(true);
    }

    @FXML
    private void open_yun_su(ActionEvent event) {
        String url = "http://www.ysdm.net/";
        OpenUrlWithLocalBrowser(url);
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
            if (paramBeforeSendHeadersParams.getURL().startsWith(this.url)) {
                System.err.println("发起一次验证码请求。");
                paramBeforeSendHeadersParams.getHeadersEx().setHeader("Accept-Encoding", "");
                this.cap.clear();
            }
        }

        public void onDataReceived(DataReceivedParams paramDataReceivedParams) {
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
            if (charset.equals("")) {
                charset = "utf-8";
            }
            String x = null;
            try {
                x = new String(paramDataReceivedParams.getData(), charset);
                //System.out.println(paramDataReceivedParams.getURL() + "   Data Length: " + x.length());

                if (x.contains(FXMLDocumentController.this.bro_id_success_keyword.getText()))
                    Platform.runLater(new Runnable() {
                        public void run() {
                            FXMLDocumentController.this.bro_id_output.appendText(paramDataReceivedParams.getURL() + ":关键字匹配成功。\n");
                        }
                    });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public boolean onAuthRequired(AuthRequiredParams paramAuthRequiredParams) {
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
                    EventHandler okaction = new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent arg0) {
                            paramAuthRequiredParams.setUsername(user_field.getText());
                            paramAuthRequiredParams.setPassword(pass_field.getText());
                            MyNetworkDelegate.this.isCancelAuth = false;
                            MyNetworkDelegate.this.isCompleteAuth = true;
                            stage.close();
                        }
                    };
                    EventHandler cancelaction = new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent arg0) {
                            MyNetworkDelegate.this.isCancelAuth = true;
                            MyNetworkDelegate.this.isCompleteAuth = true;
                            stage.close();
                        }
                    };


                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            MyNetworkDelegate.this.isCancelAuth = true;
                            System.out.println("hehe");
                            MyNetworkDelegate.this.isCompleteAuth = true;
                        }
                    });

                    ok_button.setOnAction(okaction);
                    user_field.setOnAction(okaction);
                    pass_field.setOnAction(okaction);
                    cancel_button.setOnAction(cancelaction);
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

    private void jxrBrowserInitUi(Browser browser) {
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

        //显示浏览器框
        if (this.bro_id_show_browser.isSelected()) {
            this.primaryStage.show();
        }

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
                if (paramStartLoadingEvent.isMainFrame())
                    Platform.runLater(new Runnable() {
                        public void run() {
                            progressIndicator.setProgress(-1.0D);
                            String appendText = String.format("开始访问URL:%s 代理:[%s]\n", paramStartLoadingEvent.getValidatedURL(), BrowserProxySetting);
                            FXMLDocumentController.this.bro_id_output.appendText(appendText);
                        }
                    });
                super.onStartLoadingFrame(paramStartLoadingEvent);
            }

            public void onFailLoadingFrame(FailLoadingEvent paramFailLoadingEvent) {
                if (paramFailLoadingEvent.isMainFrame())
                    Platform.runLater(new Runnable() {
                        public void run() {
                            progressIndicator.setProgress(1.0D);
                        }
                    });
                super.onFailLoadingFrame(paramFailLoadingEvent);
            }

            public void onFinishLoadingFrame(FinishLoadingEvent paramFinishLoadingEvent) {
                if (paramFinishLoadingEvent.isMainFrame()) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            progressIndicator.setProgress(1.0D);
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
    }
}
