package com.fuping.CaptchaIdentify;

import com.fuping.CommonUtils.MyFileUtils;
import com.fuping.CommonUtils.ElementUtils;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.fuping.CaptchaIdentify.CaptchaUtils.writeBytesToFile;
import static com.fuping.CommonUtils.Utils.*;
import static com.fuping.PrintLog.PrintLog.print_error;
import static com.fuping.PrintLog.PrintLog.print_info;

public class TesseractsLocaleIdent {
    public static String localeIdentCaptcha(byte[] captcha_data, String expectedRegex, String expectedLength, String tessDataName) {
        String imagePath = MyFileUtils.getFileStrAbsolutePath("captcha.png");
        imagePath = writeBytesToFile(imagePath, captcha_data);
        return localeIdentCaptcha(imagePath, expectedRegex, expectedLength, tessDataName);
    }

    public static String localeIdentCaptcha(String pngImagePath, String expectedRegex, String expectedLength, String tessDataName) {
        pngImagePath = MyFileUtils.getFileStrAbsolutePath(pngImagePath);
        //将保存的图片转换为jpg
        try {
            BufferedImage img = ImageIO.read(new File(pngImagePath));
            String jpgImagePath = MyFileUtils.getFileStrAbsolutePath("captcha.jpg");
            ImageIO.write(img, "JPG", new File(jpgImagePath));
            img = ImageIO.read(new File(jpgImagePath));
            //创建 TesseractsOcr 实例
            ITesseract tesseracts = new Tesseract();

            //设置识别数据集的路径
            if(ElementUtils.isNotEmptyObj(tessDataName)){
                String dataAbsolutePat = MyFileUtils.getFileStrAbsolutePath(String.format("tessdata%s%s.traineddata", File.separator, tessDataName));
                if( MyFileUtils.isNotEmptyFile(dataAbsolutePat)){
                    //tesseracts.setDatapath(tessDataPath);  //存在依赖,提示要设置环境变量, 弃用
                    tesseracts.setLanguage(tessDataName); //直接设置语言前缀
                    print_info(String.format("Use Found TessData From Name:[%s] On Path:[%s]", tessDataName, dataAbsolutePat));
                }else {
                    print_error(String.format("Not Found TessData From Name:[%s]!!! Please Move [%s.raineddata] To [%s]",tessDataName, tessDataName, dataAbsolutePat));
                }
            }

            String captchaResult = tesseracts.doOCR(img).replace(" ", "").replace("\n", "");
            //当前 ExpectedRegex 不为空时, 判断验证码是否符合正则
            if (ElementUtils.isNotEmptyObj(expectedRegex) && !ElementUtils.isContainOneKeyByRegex(captchaResult, expectedRegex)) {
                print_error(String.format("格式错误: [%s] <--> [%s]", expectedRegex, captchaResult));
                return null;
            }

            //当前 captchaResult 不为空时, 判断验证码长度是否正确
            if (isNumber(expectedLength) && Integer.parseInt(expectedLength) !=  captchaResult.length()) {
                print_error(String.format("识别错误: 结果[%s] <--> 长度[%s] <--> 期望长度:[%s]",captchaResult, captchaResult.length(), expectedLength));
                return null;
            }
            return captchaResult;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static BufferedImage gray(BufferedImage srcImage) {
//        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
//        ColorConvertOp op = new ColorConvertOp(cs, null);
//        srcImage = op.filter(srcImage, null);
//        return srcImage;
//    }

//    public static String getOcr(BufferedImage img2) throws Exception {
//        BufferedImage img = gray(img2);
//        List<BufferedImage> listImg = splitImage(img);
//        Map<BufferedImage, String> map = loadTrainData();
//        String result = "";
//        for (BufferedImage bi : listImg) {
//            result += getSingleCharOcr(bi, map);
//        }
//        return result;
//    }

//    public static List<BufferedImage> splitImage(BufferedImage img) throws Exception {
//        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
//        subImgs.add(img.getSubimage(16, 11, 13, 19));
//        subImgs.add(img.getSubimage(32, 11, 13, 19));
//        subImgs.add(img.getSubimage(48, 11, 13, 19));
//        subImgs.add(img.getSubimage(64, 11, 13, 19));
//        return subImgs;
//    }

//    public static Map<BufferedImage, String> loadTrainData() throws Exception {
//        Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
//        File dir = new File("train");
//        File[] files = dir.listFiles();
//        for (File file : files) {
//            map.put(ImageIO.read(file), file.getName().charAt(0) + "");
//        }
//        return map;
//    }

//    public static String getSingleCharOcr(BufferedImage img, Map<BufferedImage, String> map) {
//        String result = "";
//        int width = img.getWidth();
//        int height = img.getHeight();
//        int min = width * height;
//        for (BufferedImage bi : map.keySet()) {
//            int count = 0;
//            Label1:
//            for (int x = 0; x < width; ++x) {
//                for (int y = 0; y < height; ++y) {
//                    if (isWhite(img.getRGB(x, y)) != isWhite(bi.getRGB(x, y))) {
//                        count++;
//                        if (count >= min)
//                            break Label1;
//                    }
//                }
//            }
//            if (count < min) {
//                min = count;
//                result = map.get(bi);
//            }
//        }
//        return result;
//    }

//    public static int isWhite(int colorInt) {
//        Color color = new Color(colorInt);
//        if (color.getRed() + color.getGreen() + color.getBlue() > 100) {
//            return 1;
//        }
//        return 0;
//    }
//    public static int isBlack(int colorInt) {
//        Color color = new Color(colorInt);
//        if (color.getRed() + color.getGreen() + color.getBlue() <= 100) {
//            return 1;
//        }
//        return 0;
//    }
//    public static BufferedImage removeBackground(BufferedImage img) throws Exception {
//        //BufferedImage img = ImageIO.read(new File(fileName));
//        //获取图片的高宽
//        int width = img.getWidth();
//        int height = img.getHeight();
//
//        //循环执行除去干扰像素
//        for (int i = 1; i < width; i++) {
//            Color colorFirst = new Color(img.getRGB(i, 1));
//            int numFirstGet = colorFirst.getRed() + colorFirst.getGreen() + colorFirst.getBlue();
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    Color color = new Color(img.getRGB(x, y));
//                    //System.out.println("red:"+color.getRed()+" | green:"+color.getGreen()+" | blue:"+color.getBlue());
//                    int num = color.getRed() + color.getGreen() + color.getBlue();
//                    if (num >= numFirstGet) {
//                        img.setRGB(x, y, Color.WHITE.getRGB());
//                    }
//                }
//            }
//        }
//
//        //图片背景变黑色
//        for (int i = 1; i < width; i++) {
//            Color color1 = new Color(img.getRGB(i, 1));
//            int num1 = color1.getRed() + color1.getGreen() + color1.getBlue();
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    Color color = new Color(img.getRGB(x, y));
//                    // System.out.println("red:"+color.getRed()+" | green:"+color.getGreen()+" | blue:"+color.getBlue());
//                    int num = color.getRed() + color.getGreen() + color.getBlue();
//                    if (num == num1) {
//                        img.setRGB(x, y, Color.WHITE.getRGB());
//                    } else {
//                        img.setRGB(x, y, Color.BLACK.getRGB());
//                    }
//                }
//            }
//        }
//        return img;
//    }

    public static void main(String args[]) {
        System.out.println(localeIdentCaptcha("TestRemote.jpg","", "", ""));
    }

}
