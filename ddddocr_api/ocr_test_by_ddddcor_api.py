import requests
import base64


def image_2_base64(images_path):
    str_base64_data = None
    # 图片转base64  https://blog.csdn.net/lly1122334/article/details/120200906
    images_data = open(images_path, 'rb').read()
    byte_base64_data = base64.b64encode(images_data)
    str_base64_data = byte_base64_data.decode()
    return str_base64_data


def recognize_image_by_ddddocr_api(images_path):
    """
    # 发送POST请求并接受返回结果
    当前开放接口 http://0.0.0.0:1111/api
    发送post  url=data:image/png;base64,iVBORw0KGgoAAAANSxxxxxxxx
    """
    str_base64_data = image_2_base64(images_path)
    # print(str_base64_data)
    ocr_api = "http://127.0.0.1:5000/"
    headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36"}
    data = str_base64_data
    response = requests.post(ocr_api, headers=headers, data=data, timeout=1)
    print(f"验证码识别结果:{response.text}")
    return response.text


if __name__ == '__main__':
    images_path = r"yzm.jpg"
    recognize_image_by_ddddocr_api(images_path)
