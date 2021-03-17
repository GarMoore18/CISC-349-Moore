from flask import Flask, request
from flask import jsonify
import sys
import base64
from PIL import Image
from datetime import datetime

app = Flask(__name__)

@app.route("/", methods=["GET"])
def hello():
    return "Hello, World"

@app.route("/image", methods=["GET", "POST"])
def image():
    print(request.is_json)
    content = request.get_json()
    print(content['Info'])
    
    image = content['image']
    now = datetime.now()
    image_name = 'images-' + now.strftime('%m-%d-%Y-%H-%M-%S') + '.jpg'
    with open(image_name, 'wb') as f:
        f.write(base64.decodebytes(image.encode()))

    data = {'a': 1, 'b': 2}
    return jsonify(data)


if __name__ == '__main__':
    app.run("10.0.0.133")