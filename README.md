# CrackCaptcahLogin

数据文件过大, 编译打包版本请参考 文章  https://mp.weixin.qq.com/s/9ELFFr-y9ixMdnOXR97MHg  索引 到 公众号网盘下载

参考CrackCaptchaLogin 的框架进行修改,  仅保留了本地OCR识别功能,其他都已经进行重写。


新增功能较多，可通过配置文件分析所有支持的功能



```
CrackCaptcahLogin 1.0 小米范验证码登陆爆破工具
http://www.cnblogs.com/SEC-fsq/p/5712792.html


CrackCaptcahLogin 2.0 小米范验证码登陆爆破工具修改版
https://github.com/hkylin/CrackCaptcahLogin

CrackCaptcahLogin 2.0 更新：
1.	增加本地打码功能接口，可以自行添加验证码识别的方法。这样针对简单的验证码，不必使用云打码，使用自己的验证码识别功能即可。
2.	破解jxbrowser组件，重新整合代码，使得二次开发更方便。
```





## 本地OCR使用方法

此步骤非必须，如果使用 本地 tess4j 识别验证码的话需要

在jar同级目录下创建 tessdata目录 存放，用于 tesseract-ocr 的训练识别验证码的库。



注意：本地识别验证码没有做什么处理，误报很高，需要自己指定自己的训练级。



## 远程OCR使用方法



远程API OCR识别需要自己搭建ocr服务器,  

本工具会通过post传输base64的图片数据，

返回的数据可通过正则自定义提取, 默认提取所有响应内容作为验证码。



##  工具使用 


使用命令`java -jar CrackCaptcahLogin.jar`打开工具



基本使用：

根据 UI 进行手动配置



进阶使用： 

如需设置每个元素的默认值和其他选项，便于对项目进行重复爆破,请修改 config.prop 配置文件

自定义启动时的配置文件名称 

```
java -Dconfig=xxx.prop -jar CrackCaptchaLogin.jar
```



## 工具缺陷

目前仅实现单线程及单进程爆破，由于jxBrowser版本问题, 没有办法进行太多的自定义配置。

如果需要多开爆破任务，就多开几个虚拟机操作






