package com.example.finalprojectmoore;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    public static final String USERNAME = "username_from_db";
    private EditText username, password;
    private String username_txt, password_txt;
    private Button login_btn;

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
                    // Reset the text fields once submitted
                    username.setText("");
                    password.setText("");
                } else {
                    promptCheckFields();
                }
            }
        });
    }

    // Search for user in database
    private void loginRequest() {
        loginSuccess(); // Moves to MainActivity
    }

    // Start main activity
    private void loginSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USERNAME, username_txt);
        startActivity(intent);
    }

    private boolean allFieldsProvided() {
        return !username_txt.equals("") && !password_txt.equals("");
    }

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
}