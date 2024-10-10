import base64
import ddddocr
from flask import Flask, request
from datetime import datetime

app = Flask(__name__)
app.config['DEBUG'] = True
ocr = ddddocr.DdddOcr()


@app.route('/', methods=["POST"])
@app.route('/base64ocr', methods=["POST"])
def getCode():
    # 获取当前请求的时间
    start_time = datetime.now()
    print(f"Request At: {start_time}")

    # 接受请求数据
    img_b64 = request.get_data()

    # 输出请求数据
    # print(f"Base64 Img Data: {str(img_b64[:20] if len(img_b64) > 20 else img_b64)} ...")
    print(f"Base64 Img Data Length: {len(img_b64)}.")

    if not img_b64:
        print(f"Base64 Img Data Is Null !!!")
        return ""

    # 解码 base64 图像数据
    try:
        img_bin = base64.b64decode(img_b64.strip())
        print(f"Base64 decoded success.")
    except Exception as e:
        # 当传入的数据不是base64的时候直接当作图片二进制处理
        print(f"Error decoding base64: {e}, POST Possible Img Binary...")
        img_bin = img_b64

    # 使用 OCR 进行识别
    try:
        ocr_result = ocr.classification(img_bin)
        print(f"OCR result: {ocr_result}")
    except Exception as e:
        print(f"OCR error: {e}")
        return ""

    # 计算请求处理时间
    end_time = datetime.now()
    processing_time = (end_time - start_time).total_seconds() * 1000

    # 输出信息
    print(f"Processed in {processing_time:.2f}ms")
    return ocr_result


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
