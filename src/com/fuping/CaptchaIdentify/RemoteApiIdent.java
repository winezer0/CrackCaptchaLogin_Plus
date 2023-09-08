package com.fuping.CaptchaIdentify;
import java.io.*;
import java.util.Base64;

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

    public static void main(String[] args) {

        String imagePath = "tmp/yzm.jpg";
        String base64Image = imageToBase64(imagePath);
        if (base64Image != null) {
            System.out.println(String.format("Base64编码:%s", base64Image));
        } else {
            System.out.println("无法读取图片或转换为Base64编码。");
        }
        //输入图片地址 图片格式转换
        //发送请求到 识别接口
        //验证码识别 //云打码识别
    }
}
