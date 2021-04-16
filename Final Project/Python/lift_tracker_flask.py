from flask import Flask, request, render_template, abort, jsonify, redirect, url_for
from flask_mysqldb import MySQL
import json
import bcrypt
import datetime
import os

app = Flask(__name__)   #current module

app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'phoneDevFinal'
app.config['MYSQL_PASSWORD'] = 'phoneDevFinal2021'
app.config['MYSQL_DB'] = 'lifttracker'
app.config['MYSQL_CURSORCLASS'] = 'DictCursor'

mysql = MySQL(app)

#FOR REGISTERING USERS
# Create hased password from the provided password
def hashed_pass(password):
    return bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())

@app.route('/')
def welcome():
    return jsonify("Welcome to Lift Tracker Connection")

@app.route('/login', methods=["POST"])
def login():
    details = request.get_json()
    username = details['username']
    att_password = details['password']

    cur = mysql.connection.cursor()  # Open connection
    cur.execute("SELECT id, password FROM login WHERE username = %s", (username,))  # Attempt to find user
    
    # There is a user
    row = cur.fetchone()
    if row:
        # Check the encrypted password from database against attempted password
        if bcrypt.checkpw(att_password.encode('utf-8'), row['password'].encode('utf-8')):
            cur.execute("SELECT * FROM users WHERE login_id = 1")
            results = cur.fetchone()
        else:
            results = {}  # Encrypted password and non-encrypted do not match
    else:
        results = {}  # Inform app there is not a user

    cur.close()  # Close the connection

    return jsonify(results)  # Return the JSON of the result

@app.route('/register', methods=["POST"])
def register():
    # Use hashed_pass() here
    pass

@app.route('/add_set', methods=["POST"])
def addSet():
    details = request.get_json()
    user = details['user_id']
    exercise = details['exercise_id']
    weight = details['weight']
    reps = details['reps']
    orm = details['1_rep_max']

    cur = mysql.connection.cursor()  # Open connection
    cur.execute("INSERT INTO sets (user_id, exercise_id, weight, reps, 1_rep_max) VALUES (%s, %s, %s, %s, %s)", (user, exercise, weight, reps, orm))  # Execute the query

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

    cur.execute("SELECT exercise_id, 1_rep_max, timestamp FROM sets WHERE 1_rep_max > -1 AND user_id = %s ORDER BY `timestamp` ASC", (user,))  # Execute the query
    #cur.execute("SELECT exercise_id, weight, reps, timestamp FROM sets WHERE user_id = %s ORDER BY timestamp ASC", (user,))  # Execute the query

    f = '%Y-%m-%d %H:%M:%S'
    results = cur.fetchall()
    for row in results:
        row['timestamp'] = row['timestamp'].strftime(f)
    
    #print(results)

    cur.close()  # Close the connection

    return jsonify(results)  # Return the JSON of the result

@app.route('/get_maxes', methods=["GET"])
def getMaxes():
    cur = mysql.connection.cursor()  # Open connection
    
    cur.execute("SELECT exercise_id, MAX(1_rep_max) as '1_rep_max' FROM sets WHERE exercise_id = 0 UNION SELECT exercise_id, MAX(1_rep_max) as '1_rep_max' FROM sets WHERE exercise_id = 1 UNION SELECT exercise_id, MAX(`1_rep_max`) as '1_rep_max' FROM sets WHERE exercise_id = 2")

    results = cur.fetchall()

    cur.close()  # Close the connection

    return jsonify(results)  # Return the JSON of the result


if __name__ == '__main__':
    #app.run()   #runs app on local dev server
    app.debug = True
    app.run(host = '0.0.0.0')   #runs app for network users