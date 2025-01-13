## 为ddddocr提供web接口

> 环境：python 3.8.5
>
> 依赖：flask, ddddocr （均可通过pip安装）


### 说明脚本
```
ocr_test_by_ddddcor_local.py  用于本地调用ddddocr进行识别测试

ocr_test_by_ddddcor_api.py   用于调用api接口进行本地图片识别，需要先启动api识别服务器

ddddocr_api_simple.py，启动flask web服务 用于接受图片数据进行验证码识别
```

### 使用方法
```
配好环境，安装依赖。
执行 python3 ddddocr_api_simple.py，启动flask web服务。
```

+ 接口URL就是web服务的地址，默认是`http://127.0.0.1:5000`

+ 请求接口地址当前为 POST / 或者 /base64ocr

+ 请求模板如下：

```http
POST / HTTP/1.1

iVBORw0KGgoAAAANSUhEUgAAAFAAAAArCAIAAABglpj4AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAABApSURBVGhDXdl36Pbj

POST /base64ocr HTTP/1.1

iVBORw0KGgoAAAANSUhEUgAAAFAAAAArCAIAAABglpj4AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAABApSURBVGhDXdl36Pbj

返回文本即为验证码
```
