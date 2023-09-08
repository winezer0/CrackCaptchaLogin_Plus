package com.fuping.CaptchaIdentify;

public class RemoteIdentTest {
    public static void main(String[] args) {

        //输入图片地址
        //图片格式转换
        //发送请求到 识别接口
        //验证码识别 //云打码识别
        String result = YunSu.createByPost(ys_username, ys_password, ys_type_id, yzm_query_timeout.toString(), ys_soft_id, ys_soft_key, FXMLDocumentController.this.captcha_data);

    }


}
