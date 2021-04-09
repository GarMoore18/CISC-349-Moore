package com.example.finalprojectmoore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

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

                loginRequest(); // Request database check

                // Reset the text fields once submitted
                username.setText("");
                password.setText("");
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
        startActivity(intent);
    }
}