package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private Button bPlus;
    private Button bMin;
    private Button bMul;
    private Button bDiv;

    private char useOper;  //Operator that will be used in the computation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showNum = findViewById(R.id.show_calc);

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
        bPlus = findViewById(R.id.bp);
        bMin = findViewById(R.id.bmin);
        bMul = findViewById(R.id.bmul);
        bDiv = findViewById(R.id.bdiv);
        Button bEqual = findViewById(R.id.equal);
        Button bDeci = findViewById(R.id.bdeci);

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
        bDeci.setOnClickListener(this);

        //To clear the items with an equal sign long press
        bEqual.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                currVal = "";
                lastNum = "";
                firstValue = Double.NaN;
                showNum.setText("");
                Toast.makeText(MainActivity.this, "Cleared", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        showNum = findViewById(R.id.show_calc);

        //Each button provides a unique string
        switch (v.getId()) {
            case R.id.b0:
                resetHighlight();
                currVal += "0";
                showNum.setText(currVal);
                break;
            case R.id.b1:
                resetHighlight();
                currVal += "1";
                showNum.setText(currVal);
                break;
            case R.id.b2:
                resetHighlight();
                currVal += "2";
                showNum.setText(currVal);
                break;
            case R.id.b3:
                resetHighlight();
                currVal += "3";
                showNum.setText(currVal);
                break;
            case R.id.b4:
                resetHighlight();
                currVal += "4";
                showNum.setText(currVal);
                break;
            case R.id.b5:
                resetHighlight();
                currVal += "5";
                showNum.setText(currVal);
                break;
            case R.id.b6:
                resetHighlight();
                currVal += "6";
                showNum.setText(currVal);
                break;
            case R.id.b7:
                resetHighlight();
                currVal += "7";
                showNum.setText(currVal);
                break;
            case R.id.b8:
                resetHighlight();
                currVal += "8";
                showNum.setText(currVal);
                break;
            case R.id.b9:
                resetHighlight();
                currVal += "9";
                showNum.setText(currVal);
                break;
            case R.id.bdeci:
                resetHighlight();
                //Allows for only one decimal per value
                if (!currVal.contains(".")) {
                    currVal += ".";
                } else {
                    Toast.makeText(this, "Already used a decimal", Toast.LENGTH_SHORT).show();
                }
                showNum.setText(currVal);
                break;
            case R.id.bp:
                resetHighlight();
                bPlus.setBackgroundResource(R.drawable.button_highlight);
                makeComputation();
                useOper = add;
                break;
            case R.id.bmin:
                resetHighlight();
                bMin.setBackgroundResource(R.drawable.button_highlight);
                makeComputation();
                useOper = subtract;
                break;
            case R.id.bmul:
                resetHighlight();
                bMul.setBackgroundResource(R.drawable.button_highlight);
                makeComputation();
                useOper = multiply;
                break;
            case R.id.bdiv:
                resetHighlight();
                bDiv.setBackgroundResource(R.drawable.button_highlight);
                makeComputation();
                useOper = divide;
                break;
            case R.id.equal:
                resetHighlight();
                makeComputation();
                //Ensures that if a decimal followed by "." will still save last answer
                if (currVal.equals("") && !Double.isNaN(firstValue)) {
                    lastNum = String.valueOf(firstValue);
                    firstValue = Double.NaN;
                    useOper = 'e';     //Resets the operator
                }
                break;
            default:
                break;
        }
    }

    //Resets the operator button colors
    public void resetHighlight() {
        bPlus.setBackgroundResource(0);
        bMin.setBackgroundResource(0);
        bMul.setBackgroundResource(0);
        bDiv.setBackgroundResource(0);
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
        //Makes sure that a value is not just a decimal
        if (currVal.endsWith(".") && currVal.startsWith(".")) {
            Toast.makeText(this, "This is just a decimal", Toast.LENGTH_SHORT).show();
            return;
        }
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