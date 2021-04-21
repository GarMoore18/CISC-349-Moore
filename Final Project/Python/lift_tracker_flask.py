from flask import Flask, request, render_template, abort, jsonify, redirect, url_for
from flask_mysqldb import MySQL
import MySQLdb
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
def hashedPass(password):
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
    use_id = row['id']
    if row:
        # Check the encrypted password from database against attempted password
        if bcrypt.checkpw(att_password.encode('utf-8'), row['password'].encode('utf-8')):
            cur.execute("SELECT * FROM users WHERE login_id = %s", (use_id,))
            results = cur.fetchone()
        else:
            results = {}  # Encrypted password and non-encrypted do not match
    else:
        results = {}  # Inform app there is not a user

    cur.close()  # Close the connection

    return jsonify(results)  # Return the JSON of the result

@app.route('/register', methods=["POST"])
def register():
    details = request.get_json()
    username = details['username']
    provided_pass = details['password']

    hashed_pass = hashedPass(provided_pass)

    # Check for unique username
    try:
        cur = mysql.connection.cursor()
        cur.execute("INSERT INTO login (username, password) VALUES (%s, %s)", (username, hashed_pass))

        mysql.connection.commit()

    except MySQLdb.Error as e:
        return jsonify({"added": 2})

    first_name = details['f_name']
    last_name = details['l_name']
    login_id = cur.lastrowid
    print(login_id)

    cur = mysql.connection.cursor()
    cur.execute("INSERT INTO users (login_id, first_name, last_name) VALUES (%s, %s, %s)", (login_id, first_name, last_name))

    mysql.connection.commit()

    if cur.rowcount > 0:
        result = {"added": 0} 
    else:
        result = {"added": 1} 

    return jsonify(result)  # Return the JSON of the result

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

@app.route('/get_maxes', methods=["POST"])
def getMaxes():
    cur = mysql.connection.cursor()  # Open connection
    details = request.get_json()
    user = details['user_id']
    
    cur.execute("SELECT exercise_id, MAX(1_rep_max) as '1_rep_max' FROM sets WHERE exercise_id = 0 AND user_id = %s UNION SELECT exercise_id, MAX(1_rep_max) as '1_rep_max' FROM sets WHERE exercise_id = 1 AND user_id = %s UNION SELECT exercise_id, MAX(`1_rep_max`) as '1_rep_max' FROM sets WHERE exercise_id = 2 AND user_id = %s", (user, user, user))

    results = cur.fetchall()

    cur.close()  # Close the connection

    return jsonify(results)  # Return the JSON of the result


if __name__ == '__main__':
    #app.run()   #runs app on local dev server
    app.debug = True
    app.run(host = '0.0.0.0')   #runs app for network users