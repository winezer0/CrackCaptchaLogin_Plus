package com.fuping.LoadConfig;


import java.io.*;
import java.util.Properties;

import static cn.hutool.core.io.CharsetDetector.detect;
import static cn.hutool.core.io.FileUtil.exist;
import static cn.hutool.core.io.FileUtil.touch;
import static cn.hutool.core.util.StrUtil.isEmptyIfStr;
import static com.fuping.LoadConfig.CommonUtils.print_error;
import static com.fuping.LoadConfig.CommonUtils.print_info;
import static com.fuping.LoadConfig.MyConst.ProxyParam;

public class ConfigReader {
    private Properties properties;
    private static ConfigReader instance;

    public ConfigReader(String configFile) {
        //properties = new Properties();
        properties = new Properties();
        //获取配置文件的物理路径
        String absolutePath = new File(configFile).getAbsolutePath();

        //判断文件是否存在 //不存在就进行创建
        if (!exist(absolutePath)) {
            print_error(String.format("Not Found File [%s]", absolutePath));
            print_info(String.format("Auto Touch File [%s]", touch(absolutePath).getAbsolutePath()));
        }

        try {
            //检测文件编码
            String encoding = detect(new File(absolutePath)).name();
            //读取文件内容
            InputStream input = new FileInputStream(absolutePath);
            InputStreamReader reader = new InputStreamReader(input, encoding);
            properties.load(reader);
        } catch (IOException e) {
            print_error(String.format("Unable Read File [%s]", absolutePath));
            e.printStackTrace();
        }
    }

    public static ConfigReader getInstance(String configFile) {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader(configFile);
                }
            }
        }
        return instance;
    }

    public static ConfigReader getInstance( ) {
        String configFile = "config.properties";
        return getInstance(configFile);
    }

    public String getPropString(String paramString) {
        String paramValue = properties.getProperty(paramString, null);
        return isEmptyIfStr(paramValue) ? null : paramValue.trim() ;
    }

    public String getSystemString(String paramString) {
        String paramValue = System.getProperty(paramString, null);
        return isEmptyIfStr(paramValue) ? null : paramValue.trim() ;
    }


    public String getString(String paramString, String defaultValue) {
        //先从系统参数中文件获取
        String ParamValue =  getSystemString(paramString);
        if (!isEmptyIfStr(ParamValue)){
            print_info(String.format("Get Param Value From System.Property: %s=%s", paramString, ParamValue));
            return ParamValue;
        }

        //再从配置文件中获取
        ParamValue = getPropString(paramString);
        if (!isEmptyIfStr(ParamValue)){
            print_info(String.format("Get Param Value From Config.Property: %s=%s", paramString, ParamValue));
            return ParamValue;
        }

        //两种情况都没有获取到,返回默认值
        return defaultValue;
    }


    public boolean isTrue(String key, boolean defaultValue) {
        return "true".equalsIgnoreCase(getString(key, String.valueOf(defaultValue))
        );
    }


    public int getNumber(String key, int defaultValue) {
        String value =  getString(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static void main(String[] args) {
        ConfigReader configReader = ConfigReader.getInstance();
        String browserProxy = configReader.getString(ProxyParam, null);
        System.out.println(String.format("%s: %s", ProxyParam, browserProxy));
    }
}
