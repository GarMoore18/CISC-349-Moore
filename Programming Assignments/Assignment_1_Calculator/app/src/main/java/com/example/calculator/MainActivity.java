package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.text.DecimalFormat;

/* This class implements the OnClickListener so there is no need to rewrite a new
onClickListener for every button */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView showNum;
    private String currVal = "";    //Used for storing a current value
    private String lastNum = "";    //Used for storing previous answer

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

        //Stores the buttons in variables
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
                lastNum = String.valueOf(firstValue);
                firstValue = Double.NaN;
                useOper = 'e';     //Resets the operator
                break;
            default:
                break;
        }
    }

    /*
    Method that defines which way the operations should be performed
    This is important for calculations such as 1 + 2 = + 3.
    */
    private void correctWay(double partOne, double partTwo) {
        //Will execute if there is not a value stored yet
        if (Double.isNaN(firstValue)) {
            firstValue = Double.parseDouble(showNum.getText().toString());

        //Will execute an operation if there are two values stored
        } else {
            if (useOper == add) {
                firstValue = partOne + partTwo;
            } else if (useOper == subtract) {
                firstValue = partOne - partTwo;
            } else if (useOper == multiply) {
                firstValue = partOne * partTwo;
            } else if (useOper == divide) {
                firstValue = partOne / partTwo;
            }
            showNum.setText(String.valueOf(firstValue));    //Will show the computed value
        }
    }

    /*
    Method that defines what the values are for the operation and
    then makes a call to decide which way the values should be
    operated on.
    */
    private void makeComputation() {
        //Will execute if the equal sign and then a operator is selected
        if (!lastNum.equals("") && currVal.equals("")) {
            double secondValue = Double.parseDouble(lastNum);
            correctWay(secondValue, firstValue);
            lastNum = "";   //Resets the last number selecting equals more than once does nothing

        //Will execute if there was a value already stored
        } else if (!currVal.equals("")) {
            double secondValue = Double.parseDouble(showNum.getText().toString());
            correctWay(firstValue, secondValue);
        }
        currVal = "";   //Reset the value that the user wants
    }
}