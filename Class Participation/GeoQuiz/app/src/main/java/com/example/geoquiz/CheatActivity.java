package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class CheatActivity extends AppCompatActivity {

    private Button mShowAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        Logger.addLogAdapter(new AndroidLogAdapter());
        mShowAnswer = findViewById(R.id.show_answer_button);

        Intent i = getIntent();
        String answer = i.getStringExtra(MainActivity.CHEAT_ANSWER);
        Logger.d(answer);

        //String message = i.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //Logger.d(message);

        mShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Toast.makeText(CheatActivity.this, "Correct answer is " + answer, Toast.LENGTH_SHORT).show();
                i.putExtra("result", "Hello from Cheat Activity");
                setResult(Activity.RESULT_CANCELED, i);
                finish();
            }
        });


    }
}