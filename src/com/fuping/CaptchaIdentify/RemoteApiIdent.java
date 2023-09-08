package com.fuping.CaptchaIdentify;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import static cn.hutool.core.util.StrUtil.isEmptyIfStr;
import static com.fuping.CommonUtils.Utils.containsMatchingSubString;

public class RemoteApiIdent {
    public static String imageToBase64(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            FileInputStream imageInputStream = new FileInputStream(imageFile);
            byte[] imageBytes = new byte[(int) imageFile.length()];
            imageInputStream.read(imageBytes);
            imageInputStream.close();

            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 处理异常，返回null或其他错误信息
        }
    }

    public static String remoteIdentCommon(String Url, String base64Image,  String ExpectedStatus, String ExpectedKeywords){
        // 创建 HTTP 请求对象
        HttpRequest request = HttpUtil.createPost(Url);
        // 设置请求体，这里将 Base64 图片数据作为请求体
        request.body(base64Image);
        //设置超市时间等参数
        request.timeout(5000);//超时，毫秒

        try{
            // 发送 POST 请求并获取响应
            HttpResponse response = request.execute();
            // 获取响应状态码
            int statusCode = response.getStatus();
            String responseBody = response.body();

            //当前 ExpectedStatus 不为空时, 判断响应状态码是否包含关键字正则
            if (!isEmptyIfStr(ExpectedStatus) && !containsMatchingSubString(String.valueOf(statusCode), ExpectedStatus)) {
                System.err.println(String.format("非预期响应状态:[%s] 不包含 [%s]", statusCode, ExpectedStatus));
                return null;
            }
            //当前 ExpectedKeywords 不为空时, 判断响应体是否包含关键字正则
            if (!isEmptyIfStr(ExpectedKeywords) && !containsMatchingSubString(responseBody, ExpectedKeywords)) {
                System.err.println(String.format("非预期响应内容:[%s] 不包含 [%s]", responseBody, ExpectedKeywords));
                return null;
            }

            System.out.println(String.format("response status:[%s] content: [%s]", statusCode, responseBody));
            return responseBody;
        } catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        //输入图片地址 图片格式转换
        String imagePath = "tmp/yzm2.jpg";
        String base64Image = imageToBase64(imagePath);
        if (base64Image == null) {return;  }
        //System.out.println(String.format("Base64编码:%s", base64Image));

        //WEB请求测试
        String url = "http://127.0.0.1:5000/base64ocr"; // POST 请求的 URL
        //期望的状体码
        String captcha = remoteIdentCommon(url, base64Image, "200", null);
        System.out.println(String.format("识别结果: %s", captcha));
    }
}
