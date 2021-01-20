package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class MainActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Set UI for user
        Logger.addLogAdapter(new AndroidLogAdapter());

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);

        //click event for the true button and does something
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = mQuestionBank[0].getmTextResId();
                boolean truth = mQuestionBank[0].ismAnswerTrue();
                Logger.d("The ID is " + id + " and the answer is:" + truth);

                //displays correct text when true button is clicked
                Toast correct = Toast.makeText(MainActivity.this, R.string.correct_toast, Toast.LENGTH_SHORT);
                correct.setGravity(Gravity.TOP, 0, 0);
                correct.show();
            }
        });

        //click event for the false button and does something
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //displays incorrect text when true button is clicked
                Toast incorrect = Toast.makeText(MainActivity.this, R.string.incorrect_toast, Toast.LENGTH_SHORT);
                incorrect.setGravity(Gravity.TOP, 0, 0);
                incorrect.show();
            }
        });
    }
}