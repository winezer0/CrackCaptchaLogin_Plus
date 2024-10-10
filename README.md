# CrackCaptchaLogin

基于浏览器的、支持验证码识别的、登录爆破工具

## release版本下载

```
识别数据文件过大, 编译打包版本请关注 NOVASEC 公众号 回复【共享】获取网盘下载链接
NOVASEC 公众号 https://mp.weixin.qq.com/s/9ELFFr-y9ixMdnOXR97MHg
当前网盘 CrackCaptchaLogin release版本 为 3.7 20240108
```


## 功能介绍

参考CrackCaptchaLogin 的框架进行修改
```
仅保留了本地OCR识别功能,其他都已经进行重写。
新增功能较多，可通过配置文件分析所有支持的功能

CrackCaptcahLogin 1.0 小米范验证码登陆爆破工具
http://www.cnblogs.com/SEC-fsq/p/5712792.html


CrackCaptcahLogin 2.0 小米范验证码登陆爆破工具修改版
https://github.com/hkylin/CrackCaptcahLogin

CrackCaptcahLogin 2.0 更新：
1.	增加本地打码功能接口，可以自行添加验证码识别的方法。这样针对简单的验证码，不必使用云打码，使用自己的验证码识别功能即可。
2.	破解jxbrowser组件，重新整合代码，使得二次开发更方便。
```

##  工具使用 

使用命令`java -jar CrackCaptcahLogin.jar`打开工具

对于需要调用ocr识别的情况，需要先启动ocr api服务器.

考虑使用本地免费ddddocr api

https://github.com/winezer0/OcrApi/tree/main/ddddocr_api


基本使用：

根据 UI 进行手动配置


进阶使用： 

如需设置每个元素的默认值和其他选项，便于对项目进行重复爆破,请修改 config.prop 配置文件

自定义启动时的配置文件名称 

```
java -Dconfig=xxx.prop -jar CrackCaptchaLogin.jar
```


### 验证码识别配置

#### 本地识别 OCR使用方法

此步骤非必须，如果使用 本地 tess4j 识别验证码的话需要

在jar同级目录下创建 tessdata目录 存放，用于 tesseract-ocr 的训练识别验证码的库。

注意：本地识别验证码没有做什么处理，误报很高，需要自己指定自己的训练级。

#### 远程识别OCR使用方法

远程API OCR识别需要自己搭建ocr服务器, 

默认使用ddddocr_api的接口即可

#### 自定义远程识别OCR API服务器

本工具会通过post传输base64的图片数据到ocr识别接口

对于需要使用自己的web接口需求，需要配置 【远程识别设置】里的 api地址和相关响应提取方法

返回的数据可通过正则自定义提取, 默认提取所有响应内容作为验证码。

#### 验证码图片地址配置

常见的验证码地址有两种情况

1、验证码地址固定
```
例如：http://xxxx/new_captcha.html
```
2、验证码地址不固定
```
例如：http://xxxx/new_captcha.html?timestamp=时间戳
```

对于 场景1 可以直接设置 验证码地址为  http://xxxx/new_captcha.html

对于 场景2 需要设置为 正则模式 如 .*new_captcha.html.*

当验证码地址框填入的是URL的时候，可以点击 【识别测试】 按钮，从远程下载验证码进行识别测试.

因此，对于场景2想要测试识别验证码需要填一个有效验证码URL进行测试, 爆破前再改为正则模式.


#### 通用识别配置

通用识别配置用于指定 验证码识别结果的校验，对于不符合的格式的识别结果进行忽略

有两种方案：
1、正则匹配
2、验证码长度 

举例：

验证码长度固定是4 可以 在长度校验处填4

验证码时长度固定4位 并且都是数字字符 可以改为 [\w]{4}


## 工具缺陷

目前仅实现单线程及单进程爆破，由于jxBrowser版本问题, 没有办法进行太多的自定义配置。

如果需要多开爆破任务，就多开几个虚拟机操作






