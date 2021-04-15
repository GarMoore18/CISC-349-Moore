from flask import Flask, request, render_template, abort, jsonify, redirect, url_for
from flask_mysqldb import MySQL
import json
import hashlib, binascii
import datetime
import os

app = Flask(__name__)   #current module

app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root'
app.config['MYSQL_PASSWORD'] = 'qwerty!123456'
app.config['MYSQL_DB'] = 'lifttracker'
app.config['MYSQL_CURSORCLASS'] = 'DictCursor'

mysql = MySQL(app)

"""def hashed_pass(password):
    salt = os.urandom(32)
    key = hashlib.pbkdf2_hmac(
                'sha256', # The hash digest algorithm for HMAC
                password.encode('utf-8'), # Convert the password to bytes
                salt, # Provide the salt
                100000 # It is recommended to use at least 100,000 iterations of SHA-256 
                )

    storage = binascii.hexlify(key + salt)
    return storage"""

@app.route('/')
def welcome():
    return jsonify("Welcome to Lift Tracker Connection")

@app.route('/login', methods=["POST"])
def login():
    details = request.get_json()
    username = details['username']
    password = details['password']

    cur = mysql.connection.cursor()  # Open connection
    cur.execute("SELECT id FROM login WHERE username = %s and password = %s", (username, password))  # Execute the query

    # If there is a match return the user information
    if cur.fetchone():
        cur.execute("SELECT * FROM users WHERE login_id = 1")
        results = cur.fetchone()
    else:
        results = {}  # Inform app there is not a user

    cur.close()  # Close the connection

    return jsonify(results)  # Return the JSON of the result

@app.route('/add_set', methods=["POST"])
def addSet():
    details = request.get_json()
    user = details['user_id']
    exercise = details['exercise_id']
    weight = details['weight']
    reps = details['reps']

    cur = mysql.connection.cursor()  # Open connection
    cur.execute("INSERT INTO sets (user_id, exercise_id, weight, reps) VALUES (%s, %s, %s, %s)", (user, exercise, weight, reps))  # Execute the query

    mysql.connection.commit()

    cur.close()  # Close the connection

    if cur.rowcount > 0:
        result = {"added": True} 
    else:
        result = {"added": False} 

    return jsonify(result)  # Return the JSON of the result

@app.route('/get_set', methods=["POST"])
def getSet():
    details = request.get_json()
    user = details['user_id']

    cur = mysql.connection.cursor()  # Open connection

    cur.execute("SELECT exercise_id, weight, reps, timestamp FROM sets WHERE user_id = %s ORDER BY timestamp ASC", (user,))  # Execute the query


    f = '%Y-%m-%d %H:%M:%S'
    results = cur.fetchall()
    for row in results:
        row['timestamp'] = row['timestamp'].strftime(f)
    
    #print(results)

    cur.close()  # Close the connection

    return jsonify(results)  # Return the JSON of the result

if __name__ == '__main__':
    #app.run()   #runs app on local dev server
    app.debug = True
    app.run(host = '0.0.0.0')   #runs app for network users