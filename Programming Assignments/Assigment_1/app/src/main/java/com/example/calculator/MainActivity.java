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
    private String currVal = "";    //Used for creating a current value

    private double firstValue = Double.NaN;     //First value will be NaN until changed

    //The calculator can only do these computations
    private static final char add = '+';
    private static final char subtract = '-';
    private static final char multiply = '*';
    private static final char divide = '/';

    private char useOper;  //Operator that will be used in the computation

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
        Button bEqual = findViewById(R.id.equal);

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
        bEqual.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        showNum = findViewById(R.id.show_calc);

        //Each button provides a unique string
        switch (v.getId()) {
            case R.id.b0:
                currVal += "0";
                showNum.setText(currVal);
                break;
            case R.id.b1:
                currVal += "1";
                showNum.setText(currVal);
                break;
            case R.id.b2:
                currVal += "2";
                showNum.setText(currVal);
                break;
            case R.id.b3:
                currVal += "3";
                showNum.setText(currVal);
                break;
            case R.id.b4:
                currVal += "4";
                showNum.setText(currVal);
                break;
            case R.id.b5:
                currVal += "5";
                showNum.setText(currVal);
                break;
            case R.id.b6:
                currVal += "6";
                showNum.setText(currVal);
                break;
            case R.id.b7:
                currVal += "7";
                showNum.setText(currVal);
                break;
            case R.id.b8:
                currVal += "8";
                showNum.setText(currVal);
                break;
            case R.id.b9:
                currVal += "9";
                showNum.setText(currVal);
                break;
            case R.id.bp:
                makeComputation();
                useOper = add;
                currVal = "";
                break;
            case R.id.bmin:
                makeComputation();
                useOper = subtract;
                break;
            case R.id.bmul:
                makeComputation();
                useOper = multiply;
                break;
            case R.id.bdiv:
                makeComputation();
                useOper = divide;
                break;
            case R.id.equal:
                makeComputation();
                firstValue = Double.NaN;
                useOper = '0';     //Resets the operator
                break;
            default:
                break;
        }
    }

    //Method that performs the correct operation if possible
    private void makeComputation() {
        //Denies the user from using an operator before adding a second value
        if (!currVal.equals("")) {

            //Assigns the first value in the computation if there is not one
            if (Double.isNaN(firstValue)) {
                firstValue = Double.parseDouble(showNum.getText().toString());

            //There was a first value, so perform the correct operation
            } else {
                double secondValue = Double.parseDouble(showNum.getText().toString());

                if (useOper == add) {
                    firstValue = this.firstValue + secondValue;
                } else if (useOper == subtract) {
                    firstValue = this.firstValue - secondValue;
                } else if (useOper == multiply) {
                    firstValue = this.firstValue * secondValue;
                } else if (useOper == divide) {
                    firstValue = this.firstValue / secondValue;
                }
                showNum.setText(String.valueOf(firstValue));    //Will show the computed value
            }

            currVal = "";   //Resets the value that is currently being entered
        }
    }
}