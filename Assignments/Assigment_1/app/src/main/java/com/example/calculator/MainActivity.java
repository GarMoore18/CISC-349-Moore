package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

/* This class implements the OnClickListener so there is no need to rewrite a new
onClickListener for every button */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView showNum;
    //At any one time, there will only be two values
    private double firstValue = Double.NaN;
    private double secondValue;

    //The calculator can only do these computations
    private static final char add = '+';
    private static final char subtract = '-';
    private static final char multiply = '*';
    private static final char divide = '/';

    //Need a way to store what the next computation that will be done
    private char nextComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bZero = findViewById(R.id.b0);
        Button bOne = findViewById(R.id.b1);
        Button bTwo = findViewById(R.id.b2);
        Button bThree = findViewById(R.id.b3);
        Button bFour = findViewById(R.id.b4);
        Button bFive = findViewById(R.id.b5);
        Button bSix = findViewById(R.id.b6);
        Button bSeven = findViewById(R.id.b7);
        Button bEight = findViewById(R.id.b8);
        Button bNine = findViewById(R.id.b9);
        Button bPlus = findViewById(R.id.bp);
        Button bMin = findViewById(R.id.bmin);
        Button bMul = findViewById(R.id.bmul);
        Button bDiv = findViewById(R.id.bdiv);

        //These are a call to the onClick method
        bZero.setOnClickListener(this);
        bOne.setOnClickListener(this);
        bTwo.setOnClickListener(this);
        bThree.setOnClickListener(this);
        bFour.setOnClickListener(this);
        bFive.setOnClickListener(this);
        bSix.setOnClickListener(this);
        bSeven.setOnClickListener(this);
        bEight.setOnClickListener(this);
        bNine.setOnClickListener(this);
        bPlus.setOnClickListener(this);
        bMin.setOnClickListener(this);
        bMul.setOnClickListener(this);
        bDiv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        showNum = findViewById(R.id.show_calc);
        String currText = showNum.getText().toString();

        //Each button provides a unique string
        switch (v.getId()) {
            case R.id.b0:
                showNum.setText("0");
                break;
            case R.id.b1:
                showNum.setText("1");
                break;
            case R.id.b2:
                showNum.setText("2");
                break;
            case R.id.b3:
                showNum.setText("3");
                break;
            case R.id.b4:
                showNum.setText("4");
                break;
            case R.id.b5:
                showNum.setText("5");
                break;
            case R.id.b6:
                showNum.setText("6");
                break;
            case R.id.b7:
                showNum.setText("7");
                break;
            case R.id.b8:
                showNum.setText("8");
                break;
            case R.id.b9:
                showNum.setText("9");
                break;
            case R.id.bp:
                makeComputation();
                nextComp = add;
                showNum.setText(String.valueOf(firstValue));
                break;
            case R.id.bmin:
                makeComputation();
                nextComp = subtract;
                showNum.setText(String.valueOf(firstValue));
                break;
            case R.id.bmul:
                makeComputation();
                nextComp = multiply;
                showNum.setText(String.valueOf(firstValue));
                break;
            case R.id.bdiv:
                makeComputation();
                nextComp = divide;
                showNum.setText(String.valueOf(firstValue));
                break;
            default:
                break;
        }

    }

    private void makeComputation() {
        if (Double.isNaN(firstValue)) {
            firstValue = Double.parseDouble(showNum.getText().toString());

        } else {
            secondValue = Double.parseDouble(showNum.getText().toString());
            if (nextComp == add) {
                firstValue = this.firstValue + secondValue;
            } else if (nextComp == subtract) {
                firstValue = this.firstValue - secondValue;
            } else if (nextComp == multiply) {
                firstValue = this.firstValue * secondValue;
            } else if (nextComp == divide) {
                firstValue = this.firstValue / secondValue;
            }
        }
    }
}