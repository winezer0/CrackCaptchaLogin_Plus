import ddddocr


def recognize_image_by_ddddocr(images_path):
    ocr = ddddocr.DdddOcr()
    with open(images_path, 'rb') as f:
        img_bytes = f.read()
    text = ocr.classification(img_bytes)
    return text


if __name__ == '__main__':
    images_path = r"yzm.jpg"
    text = recognize_image_by_ddddocr(images_path)
    print(f"验证码识别结果:{text}")

