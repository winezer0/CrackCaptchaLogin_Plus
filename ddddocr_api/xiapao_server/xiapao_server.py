#!/usr/bin/env python
# -*- conding:utf-8 -*-
from http.server import HTTPServer, BaseHTTPRequestHandler
import re, time, base64, os, requests
import json
import ddddocr
from urllib.parse import parse_qs
from PIL import ImageFile

ImageFile.LOAD_TRUNCATED_IMAGES = True


host = ('0.0.0.0', 8899)
count = 50  # 保存多少个验证码及结果


def send_request(url, data_package):
    # 判断是否为 GET 或 POST 请求
    if data_package.startswith("GET") or data_package.startswith("get"):
        method = "GET"
        data = None  # GET 请求通常没有请求体
        headers = {}
        body_start_index = data_package.find("\n\n")
        if body_start_index == -1:
            body_start_index = data_package.find("\r\n\r\n")
        if body_start_index == -1:
            body_start_index = len(data_package)

        headers_lines = data_package[:body_start_index].strip().split("\n")
        for line in headers_lines[1:]:
            key, value = line.split(": ", 1)
            headers[key] = value

    elif data_package.startswith("POST") or data_package.startswith("post"):
        method = "POST"

        # 解析 headers 和 body
        headers = {}
        body_start_index = data_package.find("\n\n")
        if body_start_index == -1:
            body_start_index = data_package.find("\r\n\r\n")
        if body_start_index == -1:
            body_start_index = len(data_package)

        headers_lines = data_package[:body_start_index].strip().split("\n")
        for line in headers_lines[1:]:
            key, value = line.split(": ", 1)
            headers[key] = value

        data = data_package[body_start_index + 2:].strip()

    else:
        raise ValueError("不支持的请求类型")

    # 根据方法发送请求
    if method == "GET":
        response = requests.get(url, headers=headers, timeout=3, verify=False)
    elif method == "POST":
        response = requests.post(url, headers=headers, data=data, timeout=3, verify=False)

    return response


def decode_b64(data):
    try:
        if len(data) > 0:
            data = base64.b64decode(data).decode("utf-8")
    except Exception as error:
        print(f"base64 decode [{data}] -> Error:{error}")
    return data


class Resquest(BaseHTTPRequestHandler):
    def handler(self):
        print("data:", self.rfile.readline().decode())
        self.wfile.write(self.rfile.readline())

    def do_GET(self):
        print(self.requestline)
        if self.path != '/':
            self.send_error(404, "Page not Found!")
            return
        with open('temp/log.txt', 'r') as f:
            content = f.read()
        data = '<title>xp_CAPTCHA</title><body style="text-align:center"><h1>验证码识别：xp_CAPTCHA V4.3</h1><a href="http://www.nmd5.com">author:算命縖子</a><p><TABLE style="BORDER-RIGHT: #ff6600 2px dotted; BORDER-TOP: #ff6600 2px dotted; BORDER-LEFT: #ff6600 2px dotted; BORDER-BOTTOM: #ff6600 2px dotted; BORDER-COLLAPSE: collapse" borderColor=#ff6600 height=40 cellPadding=1 align=center border=2><tr align=center><td>验证码</td><td>识别结果</td><td>时间</td><td>验证码模块</td></tr>%s</body>' % (
            content)
        self.send_response(200)
        self.send_header('Content-type', 'text/html; charset=UTF-8')
        self.end_headers()
        self.wfile.write(data.encode())

    def do_POST(self):
        # print(self.headers)
        # print(self.command)
        text = ''
        re_data = ""
        xp_url = ""
        xp_type = ""
        xp_cookie = ""
        xp_set_ranges = ""
        xp_complex_request = ""
        xp_rf = ""
        xp_re = ""
        xp_is_re_run = ""

        try:
            if self.path != '/base64' and self.path != '/imgurl':
                self.send_error(404, "Page not Found!")
                return

            if self.path == '/base64':
                # 预留接口
                self.send_error(404, "Page not Found!")
                return

            elif self.path == '/imgurl':
                img_name = time.time()
                req_datas = self.rfile.read(int(self.headers['content-length']))
                req_datas = req_datas.decode()
                # 转化为json格式
                json_req_datas = {k: v[0] for k, v in parse_qs(req_datas).items()}
                # print(f"json_req_datas: {json_req_datas}")
                # xp_url ： url（base64）
                # xp_type ：模式（1普通、2复杂）
                # xp_cookie：cookie（base64）
                # xp_set_ranges：验证码输出模式
                # xp_complex_request：复杂模式的验证码请求包（base64）
                # xp_rf：高级模式 - 数据来源（响应头1 / 体0）
                # xp_re：高级模式 - 正则（base64）
                # xp_is_re_run：高级模式 - 按钮（启动true / 关闭false）
                try:
                    xp_url = decode_b64(json_req_datas["xp_url"])
                    xp_type = json_req_datas["xp_type"]
                    xp_cookie = decode_b64(json_req_datas["xp_cookie"])
                    xp_set_ranges = json_req_datas["xp_set_ranges"]
                    xp_is_re_run = json_req_datas["xp_is_re_run"]

                    # print(f"xp_url:{xp_url}")
                    # print(f"xp_type:{xp_type}")
                    # print(f"xp_cookie:{xp_cookie}")
                    # print(f"xp_set_ranges:{xp_set_ranges}")
                    # print(f"xp_is_re_run:{xp_is_re_run}")
                except Exception as error:
                    print(f"[!] decode param occur error: {error}")

                try:
                    if xp_type == "1":

                        headers = {
                            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
                            "Referer": "https://www.baidu.com",
                            "Cookie": xp_cookie}
                        print("\n\n" + xp_url)
                        # print(headers)
                        request = requests.get(xp_url, headers=headers, timeout=3, verify=False)
                    elif xp_type == "2":
                        xp_complex_request = decode_b64(json_req_datas["xp_complex_request"])
                        print(f"xp_complex_request:{xp_complex_request}")
                        request = send_request(xp_url, xp_complex_request)

                    CAPTCHA = request.text  # 获取图片

                    print("图片地址响应码：", request.status_code)

                    if xp_is_re_run == "true":  # 判断是否启用高级模式
                        xp_re = decode_b64(json_req_datas["xp_re"])
                        print(f"xp_re:{xp_re}")
                        xp_rf = json_req_datas["xp_rf"]
                        print(f"xp_rf:{xp_rf}")
                        try:
                            if xp_rf == '0':
                                # 请求体数据
                                re_data = re.findall(xp_re, CAPTCHA)[0]
                                print("正则匹配结果：" + re_data)
                            elif xp_rf == '1':
                                # 请求头数据
                                rp_head = xp_re.split("|")
                                head_key = rp_head[0]
                                re_zz = xp_re[len(head_key) + 1:]
                                re_data = re.findall(re_zz, request.headers[head_key])[0]
                                print("正则匹配结果：" + re_data)
                        except:
                            re_data = " regex match error!!\n\n"

                    if xp_set_ranges == "8":  # 不识别验证码，验证码在返回包的情况
                        text += "0000|" + re_data
                        self.send_response(200)
                        self.send_header('Content-type', 'application/json')
                        self.end_headers()
                        self.wfile.write(text.encode('utf-8'))
                        return

                    # 判断验证码数据包是否为json格式
                    if re.findall('"\s*:\s*.?"', CAPTCHA):
                        print("json格式")
                        CAPTCHA = CAPTCHA.split('"')
                        CAPTCHA.sort(key=lambda i: len(i), reverse=True)  # 按照字符串长度排序
                        CAPTCHA = CAPTCHA[0].split(',')
                        CAPTCHA.sort(key=lambda i: len(i), reverse=True)  # 按照字符串长度排序
                        CAPTCHA_base64 = CAPTCHA[0]
                        text_img = False
                    elif re.findall('data:image/\D*;base64,', CAPTCHA):
                        print("base64格式")
                        CAPTCHA = CAPTCHA.split(',')
                        CAPTCHA.sort(key=lambda i: len(i), reverse=True)  # 按照字符串长度排序
                        CAPTCHA_base64 = CAPTCHA[0]
                        text_img = False
                    else:
                        print("图片格式")
                        text_img = True

                    if text_img:
                        # 图片格式直接保存
                        with open("temp/%s.png" % img_name, 'wb') as f:
                            f.write(request.content)
                            f.close()
                    else:
                        # base64需要解码保存
                        with open("temp/%s.png" % img_name, 'wb') as f:
                            f.write(base64.b64decode(CAPTCHA_base64))
                            f.close()

                except:
                    print("\n\n" + xp_url)
                    print("error:获取图片出错！")

            # 验证码识别 ddddocr
            ocr.set_ranges(int(xp_set_ranges))  # 设置输出格式
            with open(r"temp/%s.png" % img_name, "rb") as f:
                img_bytes = f.read()
            result_text = ocr.classification(img_bytes, probability=True)
            text = ""
            for i in result_text['probability']:
                text += result_text['charsets'][i.index(max(i))]
            print('\n' + text + '\n')  # 识别的结果

            # 保存最新count个的验证码及识别结果
            with open('temp/log.txt', 'r') as f:
                data = ""
                counts = 0
                content = f.read()
                pattern = re.compile(r'.*?\n')
                result1 = pattern.findall(content)
                for i in result1:
                    counts += 1
                    if counts >= count: break
                    data = data + i
            with open("temp/%s.png" % img_name, 'rb') as f:
                base64_img = base64.b64encode(f.read()).decode("utf-8")
            with open('temp/log.txt', 'w') as f:
                f.write(
                    '<tr align=center><td><img src="data:image/png;base64,%s"/></td><td>%s</td><td>%s</td><td>%s</td></tr>\n' % (
                    base64_img, text, time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(int(img_name))),
                    xp_type) + data)

            # 删除掉图片文件，以防占用太大的内存
            os.remove("temp/%s.png" % img_name)
        except Exception as e:
            print(e)
            text = '0000'
            print("\n\n" + xp_url)
            print('\nerror:识别失败！\n')

        if text == '':
            text = '0000'
            print('\n识别失败！\n')

        if xp_is_re_run == "true":
            text += "|" + re_data

        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        self.wfile.write(text.encode('utf-8'))


if __name__ == '__main__':
    print('正在加载中请稍后……')

    os.makedirs('temp', exist_ok=True)
    with open('temp/log.txt', 'w') as f:
        pass
    server = HTTPServer(host, Resquest)
    print("Starting server, listen at: %s:%s" % host)
    print('加载完成！请访问：http://127.0.0.1:%s' % host[1])
    print('github:https://github.com/smxiazi/NEW_xp_CAPTCHA\n\n')
    # ocr = ddddocr.DdddOcr()
    ocr = ddddocr.DdddOcr(beta=True)  # 切换为第二套ocr模型
    server.serve_forever()