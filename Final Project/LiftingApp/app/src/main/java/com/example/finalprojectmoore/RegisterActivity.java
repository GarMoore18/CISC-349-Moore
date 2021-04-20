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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String url = "http://10.0.0.133:5000/register";

    private EditText username, password, f_name, l_name;
    private String username_txt, password_txt, f_name_txt, l_name_txt;
    private Button submit_btn;

    private int added;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find necessary views
        f_name = findViewById(R.id.first_name);
        l_name = findViewById(R.id.last_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit_btn = findViewById(R.id.register);

        submitListener();
    }

    ////////////////////////////////////////////////
    //////////////// HELPER METHODS ////////////////
    ////////////////////////////////////////////////

    // Checks for a username and password
    private boolean allFieldsProvided() {
        return !username_txt.equals("") && !password_txt.equals("") && !f_name_txt.equals("") && !l_name_txt.equals("");
    }

    // Search for user in database
    private void registerRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Creating the JSON object to POST to flask
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("f_name", f_name_txt);
            jsonObject.put("l_name", l_name_txt);
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
                        Log.d("Response", String.valueOf(response));
                        //Log.d("Response", String.valueOf(response.length()));

                        // Get the set added response
                        try {
                            added = response.getInt("added");
                            Log.d("Added", String.valueOf(added));
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                        // Display correct message for added boolean
                        if (added == 2) {
                            registerFailureDialog();

                        } else {
                            registerSuccessDialog();
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

    // Dialog for successful register
    private void registerSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("Welcome new user!");
        builder.setMessage("Thanks for registering! You will now need to login.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnToLogin();
            }
        });
        builder.show();
    }

    // Dialog for failed register
    private void registerFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("Username Taken");
        builder.setMessage("Sorry, another user has already selected that username. Please use another username.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    // Alert to fill in all of the fields
    private void promptCheckFields() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("Provide Information");
        builder.setMessage("Please provide all four fields.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    // Return to login and do not allow back button
    private void returnToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    ////////////////////////////////////////////////
    ///////////////// MAIN METHODS /////////////////
    ////////////////////////////////////////////////

    // Submit button listener
    private void submitListener() {
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store the entered data
                f_name_txt = f_name.getText().toString().trim();
                l_name_txt = l_name.getText().toString().trim();
                username_txt = username.getText().toString().trim();
                password_txt = password.getText().toString().trim();

                //Log.d("Testing", username + " " + password);

                if (allFieldsProvided()) {
                    registerRequest(); // Request database check
                } else {
                    promptCheckFields();
                }
            }
        });
    }


}