package com.example.finalprojectmoore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String url = "http://10.0.0.133:5000/login";
    public static final String USERNAME = "username_from_db";
    public static final String USERID = "userid_from_db";
    private EditText username, password;
    private String username_txt, password_txt;
    private Button login_btn;

    private String user_first_name;
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find necessary views
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login);

        loginListener();

    }

    // Login button listener
    private void loginListener() {
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store the entered data
                username_txt = username.getText().toString().trim();
                password_txt = password.getText().toString().trim();

                //Log.d("Testing", username + " " + password);

                if (allFieldsProvided()) {
                    loginRequest(); // Request database check
                } else {
                    promptCheckFields();
                }
            }
        });
    }

    // Search for user in database
    private void loginRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Creating the JSON object to POST to flask
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username_txt);
            jsonObject.put("password", password_txt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Creating the volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("Response", String.valueOf(response));
                //Log.d("Response", String.valueOf(response.length()));
                if (response.length() > 0) {
                    try {
                        user_first_name = response.getString("first_name");
                        user_id = response.getInt("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loginSuccess(); // Moves to MainActivity
                    // Reset the text fields once submitted
                    username.setText("");
                    password.setText("");
                } else {
                    promptCheckCredentials();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", String.valueOf(error));
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");  // Need this to talk to Flask
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);   //Add request to the queue
    }

    // Start main activity
    private void loginSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USERNAME, user_first_name);
        intent.putExtra(USERID, user_id);
        startActivity(intent);
    }

    // Checks for a username and password
    private boolean allFieldsProvided() {
        return !username_txt.equals("") && !password_txt.equals("");
    }

    // Alert to fill in all of the fields
    private void promptCheckFields() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("Provide Credentials");
        builder.setMessage("Please provide both a username and password.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    // Alert to check the user credentials
    private void promptCheckCredentials() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("Login Invalid");
        builder.setMessage("The credentials provided do not match any users.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}