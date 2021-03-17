from flask import Flask, request
from flask import jsonify
import sys
import base64
from PIL import Image

app = Flask(__name__)

@app.route("/", methods=["GET"])
def hello():
    return "Hello, World"

@app.route("/image", methods=["GET", "POST"])
def image():
    data = {'name': 'Garrett', 'ID': 1234}

    return jsonify(data)

if __name__ == '__main__':
    app.run("10.0.0.133")