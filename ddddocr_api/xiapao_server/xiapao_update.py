#!/usr/bin/env python
# -*- coding: utf-8 -*-
import base64
import os
import re
import time
from collections import deque
from urllib.parse import parse_qs

import ddddocr
import requests
from PIL import ImageFile
from flask import Flask, request, jsonify, render_template_string

ImageFile.LOAD_TRUNCATED_IMAGES = True

app = Flask(__name__)

# ocr = ddddocr.DdddOcr()
ocr = ddddocr.DdddOcr(beta=True)

LOG_FILE = 'temp/log.txt'
LOG_COUNT = 50  # 保存多少个验证码及结果


@app.route('/')
def index():
    with open(LOG_FILE, 'r') as f:
        content = f.read()
    data = '''
    <title>xp_CAPTCHA</title>
    <body style="text-align:center">
        <h1>验证码识别：xp_CAPTCHA V4.3</h1>
        <a href="http://www.nmd5.com">author:算命縖子</a>
        <p>
            <TABLE style="BORDER-RIGHT: #ff6600 2px dotted; BORDER-TOP: #ff6600 2px dotted; BORDER-LEFT: #ff6600 2px dotted; BORDER-BOTTOM: #ff6600 2px dotted; BORDER-COLLAPSE: collapse" borderColor=#ff6600 height=40 cellPadding=1 align=center border=2>
                <tr align=center>
                    <td>验证码</td>
                    <td>识别结果</td>
                    <td>时间</td>
                    <td>验证码模块</td>
                </tr>
                {}
            </table>
        </p>
    </body>
    '''.format(content)
    return render_template_string(data)


@app.route('/imgurl', methods=['POST'])
def img_url_ocr():
    req_datas = request.data.decode()
    json_req_datas = {k: v[0] for k, v in parse_qs(req_datas).items()}

    xp_url = decode_b64(json_req_datas.get("xp_url", ""))
    xp_type = json_req_datas.get("xp_type", "")
    xp_cookie = decode_b64(json_req_datas.get("xp_cookie", ""))
    xp_set_ranges = json_req_datas.get("xp_set_ranges", "")
    xp_complex_request = decode_b64(json_req_datas.get("xp_complex_request", ""))
    xp_rf = json_req_datas.get("xp_rf", "")
    xp_re = decode_b64(json_req_datas.get("xp_re", ""))
    xp_is_re_run = json_req_datas.get("xp_is_re_run", "")

    print(f"xp_url: {xp_url}")
    print(f"xp_type: {xp_type}")
    print(f"xp_cookie: {xp_cookie}")
    print(f"xp_set_ranges: {xp_set_ranges}")
    print(f"xp_complex_request: {xp_complex_request}")
    print(f"xp_rf: {xp_rf}")
    print(f"xp_re: {xp_re}")
    print(f"xp_is_re_run: {xp_is_re_run}")

    try:
        CAPTCHA = None

        # 普通GET模式传递URL进行验证码获取
        if xp_type == "1":
            headers = {
                "User-Agent": "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36",
                # "Referer": "https://www.baidu.com",
                "Cookie": xp_cookie
            }
            response = requests.get(xp_url, headers=headers, timeout=3, verify=False)
            CAPTCHA = response.text  # 获取图片
            print("图片地址响应码：", response.status_code)

        # 自定义报文模式解析请求头+传递URL进行验证码获取
        if xp_type == "2":
            response = send_http_package(xp_url, xp_complex_request)
            CAPTCHA = response.text  # 获取图片
            print("图片地址响应码：", response.status_code)

        if CAPTCHA is None:
            print("[!] CAPTCHA数据获取失败!!!")
            return "CAPTCHA ERROR", 500

        # 开启高级RE提取模式
        if xp_is_re_run == "true" and xp_set_ranges == '8':
            try:
                if xp_rf == '0':
                    re_data = re.findall(xp_re, CAPTCHA)[0]
                    print(f"正则匹配结果：[{re_data}]")
                elif xp_rf == '1':
                    rp_head = xp_re.split("|")
                    head_key = rp_head[0]
                    re_zz = xp_re[len(head_key) + 1:]
                    re_data = re.findall(re_zz, response.headers[head_key])[0]
                    print(f"正则匹配结果：[{re_data}]")
            except Exception as error:
                print(f"正则匹配出错: xp_rf=>{xp_rf}  Error: {error}!!!")
                re_data = ""
            # 返回识别结果
            ocr_text = "0000|" + re_data
            return jsonify({"result": ocr_text})

        # 简单判断当前验证码数据格式
        img_is_bin, captcha_base64 = guess_captcha_format(CAPTCHA)

        # 保存验证码图片
        img_bytes = response.content if img_is_bin else base64.b64decode(captcha_base64)

        # 进行验证码识别
        img_time = time.time()
        ocr_text = ddddocr_ocr(img_bytes, xp_set_ranges)
        # 保存最新count个的验证码及识别结果
        save_latest_entries(img_bytes, ocr_text, img_time, xp_type, count=LOG_COUNT)
        return ocr_text, 200

    except Exception as error:
        print(f"Error: {error}")
        return error, 500


def guess_captcha_format(CAPTCHA):
    img_is_bin = True
    captcha_base64 = None
    if re.findall('"\s*:\s*.?"', CAPTCHA):
        print("img data is [base64 json] format")
        CAPTCHA = CAPTCHA.split('"')
        CAPTCHA.sort(key=lambda i: len(i), reverse=True)
        CAPTCHA = CAPTCHA[0].split(',')
        CAPTCHA.sort(key=lambda i: len(i), reverse=True)
        captcha_base64 = CAPTCHA[0]
        img_is_bin = False
    elif re.findall('data:image/\D*;base64,', CAPTCHA):
        print("img data is [base64] format")
        CAPTCHA = CAPTCHA.split(',')
        CAPTCHA.sort(key=lambda i: len(i), reverse=True)
        captcha_base64 = CAPTCHA[0]
        img_is_bin = False
    else:
        print("img data is [bin] format")
    return img_is_bin, captcha_base64


def ddddocr_ocr(img_bytes, xp_set_ranges):
    ocr.set_ranges(int(xp_set_ranges))
    ocr_result = ocr.classification(img_bytes, probability=True)
    ocr_text = "".join(ocr_result['charsets'][i.index(max(i))] for i in ocr_result['probability'])
    print(f'识别结果:{ocr_text}')
    return ocr_text


def save_latest_entries(img_bytes, ocr_text, img_time, xp_type, count=LOG_COUNT):
    log_entry_format = '<tr align=center><td><img src="data:image/png;base64,%s"/></td><td>%s</td><td>%s</td><td>%s</td></tr>\n'

    # 确保日志文件所在的目录存在
    os.makedirs(os.path.dirname(LOG_FILE), exist_ok=True)

    # 使用deque来保持最新的count个条目
    entries = deque(maxlen=count)

    # 尝试读取现有日志内容，并将每条记录加入到deque中
    if os.path.exists(LOG_FILE):
        with open(LOG_FILE, 'r') as f:
            for line in f:
                if line.strip():  # 忽略空行
                    entries.append(line)

    # 构建新的日志条目
    new_entry = log_entry_format % (
        base64.b64encode(img_bytes).decode("utf-8"),
        ocr_text,
        time.strftime("%Y-%m-%d %H:%M:%S", time.localtime(int(img_time))),
        xp_type
    )

    # 添加新条目到队列开头
    entries.appendleft(new_entry)

    # 写入更新后的日志文件
    with open(LOG_FILE, 'w') as f:
        f.writelines(entries)


def parse_http_package(http_package):
    def find_body_index(http_package_):
        # 假设空行（CRLF或LF）分隔headers和body
        empty_line_regex = r'\r\n\r\n|\n\n|\r\r'
        match_ = re.search(empty_line_regex, http_package_)
        return match_.start() if match_ else -1

    def parse_headers(http_package_, body_index_):
        # 提取header部分并解析为字典
        header_part = http_package_[:body_index_] if body_index_ >= 0 else http_package_
        lines = header_part.splitlines()
        headers_ = {}
        for line in lines[1:]:  # 忽略第一行，即请求行
            if ':' in line:
                key, value = line.split(':', 1)
                headers_[key.strip()] = value.strip()
        return headers_

    # 正则表达式匹配HTTP方法，不区分大小写
    match_method = re.match(r'^(?P<method>[A-Z]+)', http_package, re.IGNORECASE)
    if not match_method:
        raise ValueError("无法识别的请求类型")

    method = match_method.group('method').upper()
    body_index = find_body_index(http_package)
    headers = parse_headers(http_package, body_index)

    # 分离body部分，如果存在的话
    body = http_package[body_index + 2:].strip() if body_index >= 0 else None

    return method, headers, body


def decode_b64(data):
    try:
        if len(data) > 0:
            data = base64.b64decode(data).decode("utf-8")
    except Exception as error:
        print(f"base64 decode [{data}] -> Error:{error}")
    return data


def send_http_package(url, http_package):
    # method, headers, body = parse_http_package(http_package)
    method, headers, body = parse_http_package(http_package)
    # 使用method参数来指定请求方法，并且仅在需要的时候（如POST）提供data参数
    if method in ["GET", "POST"]:
        response = requests.request(method, url, headers=headers, data=body if body else None, timeout=3, verify=False)
        return response
    else:
        raise ValueError("暂不支持重放对应请求")


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8899)
