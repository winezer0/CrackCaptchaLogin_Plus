package com.fuping.CaptchaIdentify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class CaptchaUtils {

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

    public static String LoadImageToFile(String imageUrl, String imagePath) {
        //从网页加载 验证码 内容，并保存到文件


        return imagePath;
    }

}
