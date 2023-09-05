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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.http.util.ByteArrayBuffer;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import static com.fuping.BrowserUtils.BrowserUitls.AutoClearAllCookies;
import static com.fuping.BrowserUtils.BrowserUitls.getBrowserProxy;
import static com.fuping.LoadConfig.MyConst.browserProxySetting;

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
    private HBox id_hbox11;

    @FXML
    private HBox id_hbox22;

    @FXML
    private HBox id_hbox33;

    @FXML
    private HBox id_hbox44;

    @FXML
    private HBox id_hbox55;

    @FXML
    private RadioButton bro_id_user_by_class;

    @FXML
    private RadioButton bro_id_pass_by_class;

    @FXML
    private RadioButton bro_id_submit_by_class;

    @FXML
    private RadioButton bro_id_captcha_by_class;

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


    private byte[] captchadata;
    private Stage primaryStage;
    private LinkedBlockingQueue<UserPassPair> queue;
    private Boolean isstopsendcrack;

    private String yzmText = null;

    private List<Cookie> cookies = null;

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
        //登陆URL检查
        String base_url = this.id_base_url_input.getText();
        //输入框检查
        if (base_url.equals("")) {
            new Alert(Alert.AlertType.NONE, "请输入登录页面URL", new ButtonType[]{ButtonType.CLOSE}).show();
            return;
        }
        //增加协议头
        if (!base_url.startsWith("http")) { base_url = String.format("http://%s", base_url); }
        String login_url = base_url;

        //验证码输入URL
        String captcha_url_input = this.bro_id_captcha_url_input.getText().trim();
        //登录按钮内容
        String submit_input = this.bro_id_submit_ele_input.getText().trim();

        //获取云速认证信息
        String ys_username = this.ys_username.getText().trim();
        String ys_password = this.ys_password.getText().trim();
        String ys_soft_id = this.ys_soft_id.getText().trim();
        String ys_soft_key = this.ys_soft_key.getText().trim();
        String ys_type_id = this.ys_type_id.getText().trim();
        Integer ys_query_timeout = this.ys_query_timeout.getValue();
        String ys_query_timeout2;
        if (ys_query_timeout == null)  ys_query_timeout2 = "60"; else {  ys_query_timeout2 = ys_query_timeout.toString(); }
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

            String bro_user_input = this.bro_id_user_ele_input.getText();
            if (bro_user_input.equals("")) {
                this.bro_id_user_ele_input.requestFocus();
                return;
            }
            String bro_pass_input = this.bro_id_pass_ele_input.getText();
            if (bro_pass_input.equals("")) {
                this.bro_id_pass_ele_input.requestFocus();
                return;
            }
            String bro_captcha_input = this.bro_id_captcha_ele_input.getText();
            if (bro_captcha_input.equals("")) {
                this.bro_id_user_ele_input.requestFocus();
                return;
            }
            String bro_captcha_url_input = this.bro_id_captcha_url_input.getText();
            if (bro_captcha_url_input.equals("")) {
                this.bro_id_captcha_url_input.requestFocus();
                return;
            }

            this.primaryStage = new Stage();
            Browser browser = new Browser(BrowserType.LIGHTWEIGHT);
            browser.getContext().getNetworkService().setNetworkDelegate(new MyNetworkDelegate(captcha_url_input));

            //浏览器代理设置
            if (browserProxySetting != null) {
                //参考 使用代理 https://www.kancloud.cn/neoman/ui/802531
                browser.getContext().getProxyService().setProxyConfig(getBrowserProxy());
            }

            BrowserView view = new BrowserView(browser);
            BorderPane borderPane = new BorderPane(view);

            ProgressIndicator progressIndicator = new ProgressIndicator(1.0D);
            progressIndicator.setPrefHeight(30.0D);
            progressIndicator.setPrefWidth(30.0D);
            TextField urlinput = new TextField();
            HBox Hbox = new HBox(2.0D, new Node[]{progressIndicator, urlinput});
            HBox.setHgrow(urlinput, Priority.ALWAYS);
            Hbox.setAlignment(Pos.CENTER_LEFT);

            borderPane.setTop(Hbox);

            Scene scene = new Scene(borderPane, 800.0D, 700.0D);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("请勿操作浏览器页面");
            if (this.bro_id_show_browser.isSelected()) {
                this.primaryStage.show();
            }

            browser.addLoadListener(new LoadAdapter() {
                public void onProvisionalLoadingFrame(ProvisionalLoadingEvent event) {
                    if (event.isMainFrame())
                        Platform.runLater(new Runnable() {
                            public void run() {
                                urlinput.setText(event.getURL());
                            }
                        });
                }

                public void onStartLoadingFrame(StartLoadingEvent paramStartLoadingEvent) {
                    if (paramStartLoadingEvent.isMainFrame())
                        Platform.runLater(new Runnable() {
                            public void run() {
                                progressIndicator.setProgress(-1.0D);
                                FXMLDocumentController.this.bro_id_output.appendText("开始访问URL:" + paramStartLoadingEvent.getValidatedURL() + "\n");
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
            PopupHandler popupHandler = new PopupHandler() {
                public PopupContainer handlePopup(PopupParams paramPopupParams) {
                    paramPopupParams.getParent().loadURL(paramPopupParams.getURL());
                    return null;
                }
            };
            browser.setPopupHandler(popupHandler);

            this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent event) {
                    browser.dispose();
                }
            });
            browser.setDialogHandler(new MyDialogHandler(view));

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Integer interval = (Integer) FXMLDocumentController.this.bro_id_req_interval.getValue();
                        if (FXMLDocumentController.this.bro_id_req_interval.getValue() == null) {
                            interval = Integer.valueOf(1500);
                        }
                        FileReader fruser = new FileReader("dict" + File.separator + "username.txt");
                        BufferedReader bruser = new BufferedReader(fruser);
                        String u;
                        while ((u = bruser.readLine()) != null) {
                            FileReader frpass = new FileReader("dict" + File.separator + "password.txt");
                            BufferedReader brpass = new BufferedReader(frpass);
                            String p;
                            while ((p = brpass.readLine()) != null) {
                                //清理所有Cookie //可能存在问题,比如验证码, 没有Cookie会怎么样呢?
                                AutoClearAllCookies(browser);

                                String uu = u;
                                String pp = p;
                                FXMLDocumentController.this.captchadata = null;
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        FXMLDocumentController.this.bro_id_output.appendText("正在测试用户名:" + uu + " 密码:" + pp + "\r\n");
                                    }
                                });


                                try {
                                    Browser.invokeAndWaitFinishLoadingMainFrame(browser, new Callback<Browser>() {
                                                public void invoke(Browser browser) {
                                                    browser.loadURL(login_url);
                                                }
                                            }
                                            , 120);
                                } catch (IllegalStateException e) {
                                    String m = e.getMessage();
                                    System.out.println(m);
                                    if (m.contains("Channel is already closed")) {
                                        Platform.runLater(new Runnable() {
                                            public void run() {
                                                FXMLDocumentController.this.bro_id_output.appendText("停止测试\n");
                                            }
                                        });
                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            FXMLDocumentController.this.bro_id_output.appendText("访问超时\r\n");
                                        }
                                    });
                                    continue;
                                }
                                if (interval.intValue() != 0) {
                                    Thread.sleep(interval.intValue() / 2);
                                }
                                DOMDocument doc = browser.getDocument();

                                try {
                                    if (FXMLDocumentController.this.bro_id_user_by_id.isSelected()) {
                                        InputElement ele = (InputElement) doc.findElement(By.id(bro_user_input));
                                        ele.setValue(u);
                                    } else if (FXMLDocumentController.this.bro_id_user_by_name.isSelected()) {
                                        InputElement ele = (InputElement) doc.findElement(By.name(bro_user_input));
                                        ele.setValue(u);
                                    } else {
                                        InputElement ele = (InputElement) doc.findElement(By.className(bro_user_input));
                                        ele.setValue(u);
                                    }

                                    if (FXMLDocumentController.this.bro_id_pass_by_id.isSelected()) {
                                        InputElement ele = (InputElement) doc.findElement(By.id(bro_pass_input));
                                        ele.setValue(p);
                                    } else if (FXMLDocumentController.this.bro_id_pass_by_name.isSelected()) {
                                        InputElement ele = (InputElement) doc.findElement(By.name(bro_pass_input));
                                        ele.setValue(p);
                                    } else {
                                        InputElement ele = (InputElement) doc.findElement(By.className(bro_pass_input));
                                        ele.setValue(p);
                                    }
                                } catch (IllegalStateException e) {
                                    String m = e.getMessage();
                                    System.out.println(m);
                                    if (m.contains("Channel is already closed")) {
                                        Platform.runLater(new Runnable() {
                                            public void run() {
                                                FXMLDocumentController.this.bro_id_output.appendText("IllegalStateException异常,无法获取用户名或密码输入框按钮,停止测试\n");
                                            }
                                        });
                                        break;
                                    }
                                    e.printStackTrace();
                                } catch (NullPointerException e) {
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            FXMLDocumentController.this.bro_id_output.appendText("识别用户名密码输入框失败\n");
                                        }
                                    });
                                    continue;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    continue;
                                }

                                if (FXMLDocumentController.this.bro_id_have_captcha.isSelected()) {
                                    if (FXMLDocumentController.this.captchadata == null) {
                                        Platform.runLater(new Runnable() {
                                            public void run() {
                                                FXMLDocumentController.this.bro_id_output.appendText("获取验证码失败\n");
                                            }
                                        });
                                        continue;
                                    }

                                    /*
                                    验证码识别
                                    * */

                                    if (bro_yzm_yunRadioBtn.isSelected()) {
                                        String result = YunSu.createByPost(ys_username, ys_password, ys_type_id, ys_query_timeout2, ys_soft_id,
                                                ys_soft_key, FXMLDocumentController.this.captchadata);
                                        System.out.println("查询结果:" + result);

//                                        int k = result.indexOf("|");
                                        if (result.contains("Error_Code")) {
                                            Platform.runLater(new Runnable() {
                                                public void run() {
                                                    FXMLDocumentController.this.bro_id_output.appendText("获取验证码失败\n");
                                                }
                                            });
//                                            continue;
                                            break;
                                        }
                                        yzmText = YunSu.getResult(result);
                                    }
                                    if (bro_yzm_localRadioBtn.isSelected()) {

                                        yzmText = YzmToText.getCode();

                                    }


                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            FXMLDocumentController.this.bro_id_output.appendText("已识别验证码为:" + yzmText);
                                        }
                                    });
                                    try {
                                        if (FXMLDocumentController.this.bro_id_captcha_by_id.isSelected()) {
                                            InputElement ele = (InputElement) doc.findElement(By.id(bro_captcha_input));
                                            ele.setValue(yzmText);
                                        } else if (FXMLDocumentController.this.bro_id_captcha_by_name.isSelected()) {
                                            InputElement ele = (InputElement) doc.findElement(By.name(bro_captcha_input));
                                            ele.setValue(yzmText);
                                        } else {
                                            InputElement ele = (InputElement) doc
                                                    .findElement(By.className(bro_captcha_input));
                                            ele.setValue(yzmText);
                                        }
                                    } catch (IllegalStateException e) {
                                        Platform.runLater(new Runnable() {
                                            public void run() {
                                                FXMLDocumentController.this.bro_id_output.appendText("IllegalStateException异常,无法获取登录按钮,停止测试\n");
                                            }
                                        });
                                    }
                                }

                                try {
                                    if (FXMLDocumentController.this.bro_id_submit_by_id.isSelected())
                                        doc.findElement(By.id(submit_input)).click();
                                    else if (FXMLDocumentController.this.bro_id_submit_by_name.isSelected())
                                        doc.findElement(By.name(submit_input)).click();
                                    else
                                        doc.findElement(By.className(submit_input)).click();
                                } catch (Exception e) {
                                    try {
                                        doc.findElement(By.cssSelector("[type=submit]")).click();
                                    } catch (IllegalStateException ee) {
                                        break;
                                    }
                                }


                                browser.executeCommand(EditorCommand.INSERT_NEW_LINE);
                                if (interval.intValue() != 0) {
                                    Thread.sleep(interval.intValue());
                                }
                                Platform.runLater(new Runnable() {
                                    public void run() {

                                        FXMLDocumentController.this.bro_id_output.appendText("点击登录后当前URL:"  +  browser.getURL()  + "---标题:" + browser.getTitle() + "---长度:" +  browser.getHTML().length() + "\r\n\r\n");
                                    }
                                });
                            }
                            brpass.close();
                        }
                        bruser.close();
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
                    FXMLDocumentController.this.isstopsendcrack = Boolean.valueOf(false);

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
                        FileReader fruser = new FileReader("dict" + File.separator + "username.txt");
                        BufferedReader bruser = new BufferedReader(fruser);
                        String username;
                        while ((username = bruser.readLine()) != null) {
                            FileReader frpass = new FileReader("dict" + File.separator + "password.txt");
                            BufferedReader brpass = new BufferedReader(frpass);
                            String password;
                            while ((password = brpass.readLine()) != null) {
                                if (FXMLDocumentController.this.isstopsendcrack.booleanValue()) {
                                    FXMLDocumentController.this.queue.clear();
                                    bruser.close();
                                    brpass.close();
                                    cdl.await();
                                    return;
                                }
                                do
                                    Thread.sleep(300L);
                                while (FXMLDocumentController.this.queue.size() > 8000);

                                FXMLDocumentController.this.queue.add(new UserPassPair(username, password));
                            }

                            brpass.close();
                        }

                        bruser.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        while (cdl.getCount() > 0L) {
                            if (FXMLDocumentController.this.isstopsendcrack.booleanValue()) {
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

    @FXML
    private void help(ActionEvent event) {
        String url = "http://www.cnblogs.com/SEC-fsq/p/5712792.html";
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
    private void ydm(ActionEvent event) {


        this.id_hbox11.setDisable(false);
        this.id_hbox22.setDisable(false);
        this.id_hbox33.setDisable(false);
        this.id_hbox44.setDisable(false);
        this.id_hbox55.setDisable(false);
    }

    @FXML
    private void bro_localdm(ActionEvent event) {


        this.id_hbox44.setDisable(true);
        this.id_hbox55.setDisable(true);
    }

    @FXML
    private void bro_check_have_captcha(ActionEvent event) {
        if (this.bro_id_have_captcha.isSelected()) {
            this.id_hbox11.setDisable(false);
            this.id_hbox22.setDisable(false);
            this.id_hbox33.setDisable(false);
            this.id_hbox44.setDisable(false);
            this.id_hbox55.setDisable(false);
            this.bro_yzm_localRadioBtn.setDisable(false);
            this.bro_yzm_yunRadioBtn.setDisable(false);
        } else {
            this.id_hbox11.setDisable(true);
            this.id_hbox22.setDisable(true);
            this.id_hbox33.setDisable(true);
            this.id_hbox44.setDisable(true);
            this.id_hbox55.setDisable(true);
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
        this.isstopsendcrack = Boolean.valueOf(true);
    }

    @FXML
    private void open_yun_su(ActionEvent event) {
        String url = "http://www.ysdm.net/";
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

    public class MyNetworkDelegate extends DefaultNetworkDelegate {
        private boolean isCompleteAuth;
        private boolean isCancelAuth;
        private String captchaurl;
        private String url;
        private long currentid;
        private ByteArrayBuffer cap = new ByteArrayBuffer(4096);

        public MyNetworkDelegate(String captchaurl) {
            this.captchaurl = captchaurl;
            int i = captchaurl.indexOf("?");
            if (i != -1)
                this.url = captchaurl.substring(0, i);
            else
                this.url = captchaurl;
        }

        public void onBeforeSendHeaders(BeforeSendHeadersParams paramBeforeSendHeadersParams) {
            if (paramBeforeSendHeadersParams.getURL().startsWith(this.url)) {
                System.err.println("发起一次验证码请求。");
                paramBeforeSendHeadersParams.getHeadersEx().setHeader("Accept-Encoding", "");
                this.cap.clear();
            }
        }

        public void onDataReceived(DataReceivedParams paramDataReceivedParams) {
            String currenturl = paramDataReceivedParams.getURL();
            FileOutputStream fos = null;
            if (currenturl.startsWith(this.url)) {
                try {
                    System.out.println("已获取验证码数据:" + currenturl);
                    FXMLDocumentController.this.captchadata = paramDataReceivedParams.getData();
                    this.cap.append(FXMLDocumentController.this.captchadata, 0, FXMLDocumentController.this.captchadata.length);
                    FXMLDocumentController.this.captchadata = this.cap.toByteArray();

                    fos = new FileOutputStream(new File("tmp\\yzm.jpg"));
                    fos.write(FXMLDocumentController.this.captchadata);
//                    ImageIO.write(ImageIO.read(new File("tmp\\yzm.jpg")),"JPG",new File("tmp\\yzm2.jpg"));

                    fos.flush();
                    fos.close();


//                    System.out.println("验证码数据:" + new String(FXMLDocumentController.this.captchadata).substring(0, 100));
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
                    TextField userfield = new TextField();
                    TextField passfield = new TextField();
                    userfield.setPromptText("用户名");
                    passfield.setPromptText("密码");

                    Button okbutton = new Button("确定");
                    Button cancelbutton = new Button("取消");

                    HBox hbox = new HBox(50.0D);
                    hbox.getChildren().addAll(new Node[]{okbutton, cancelbutton});

                    VBox vbox = new VBox(20.0D, new Node[]{userfield, passfield, hbox});
                    vbox.setPadding(new Insets(30.0D, 30.0D, 30.0D, 30.0D));

                    vbox.setAlignment(Pos.CENTER);
                    hbox.setAlignment(Pos.CENTER);

                    Scene scene = new Scene(vbox);
                    stage.setScene(scene);
                    stage.setTitle("请输入用户名密码");
                    stage.sizeToScene();
                    userfield.requestFocus();
                    EventHandler okaction = new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent arg0) {
                            paramAuthRequiredParams.setUsername(userfield.getText());
                            paramAuthRequiredParams.setPassword(passfield.getText());
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

                    okbutton.setOnAction(okaction);
                    userfield.setOnAction(okaction);
                    passfield.setOnAction(okaction);
                    cancelbutton.setOnAction(cancelaction);
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

}
