package com.example.geoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mPrevButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    public static final String EXTRA_MESSAGE = "com.example.geoquiz.MESSAGE";
    private static final int REQUEST_CODE_CHEAT = 0;

    private static final String TAG = "MainActivity";
    private static final String KEY_INDEX = "index";

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d(requestCode);
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (resultCode == Activity.RESULT_OK) {
                Logger.d("Result okay and code is 0.");
            } else {
                Logger.d("Result cancelled");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Set UI for user

        Logger.addLogAdapter(new AndroidLogAdapter());

        //Finding the items in the layout
        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mPrevButton = findViewById(R.id.prev_button);
        mNextButton = findViewById(R.id.next_button);
        mCheatButton = findViewById(R.id.cheat_button);

        mQuestionTextView = findViewById(R.id.question_text);

        //Used to ensure orientation change does not change screen data
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        updateQuestion();   //call to update question, initially index 0

        //click event for the true button and does something
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        //click event for the false button and does something
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        //click event for the next button and moves through questions
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex == 0) {
                    mCurrentIndex = mQuestionBank.length - 1;
                } else {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                }
                updateQuestion();

            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("Cheating");
                Intent i = new Intent(MainActivity.this, CheatActivity.class);
                String message = "Hello from Main Activity";
                i.putExtra(EXTRA_MESSAGE, message);
                startActivityForResult(i, REQUEST_CODE_CHEAT);
            }
        });

        //click event for the next button and moves through questions
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d("onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("onResume");
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");

        // Save the state of item position
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    //to update which question that we are currently seeing
    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getmTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userAnswer) {
        boolean answer = mQuestionBank[mCurrentIndex].ismAnswerTrue();
        String message = "";
        if (userAnswer == answer) {
            message = "You Win!";
        } else {
            message = "You Lost!";
        }
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        /*
        Toast testToast = Toast.makeText(MainActivity.this, R.string.incorrect_toast, Toast.LENGTH_SHORT);
        testToast.setGravity(Gravity.TOP, 0, 0);
        testToast.show(); */
    }
}