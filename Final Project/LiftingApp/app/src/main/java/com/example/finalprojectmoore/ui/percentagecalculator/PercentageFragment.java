package com.example.finalprojectmoore.ui.percentagecalculator;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalprojectmoore.R;
import com.example.finalprojectmoore.SpinnerAdapter;

import java.text.DecimalFormat;

public class PercentageFragment extends Fragment {

    private PercentageViewModel percentageViewModel;
    private View root;
    private TextView textView;
    private TableLayout percent_table;
    private LinearLayout main_percentage;

    private Spinner exercise_spin;
    private SpinnerAdapter spinnerAdapter;
    private final String[] exercise_names = {"Choice", "Bench", "Deadlift", "Squat"};
    private final int[] exercise_imgs = {R.drawable.dropdown, R.drawable.spinner_bench, R.drawable.spinner_deadlift, R.drawable.spinner_squat};
    private final String[] exercise_name_less = {"Bench", "Deadlift", "Squat"};
    private final int[] exercise_imgs_less = {R.drawable.spinner_bench, R.drawable.spinner_deadlift, R.drawable.spinner_squat};


    private int[] max_weight = {205, 275, 260};  // TODO: Fill with maxes from database
    private TableRow row;
    private TextView pc, wr, rc;

    private Button wp, wm;
    private EditText wc;
    private int weight_int = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.percentage_fragment, container, false);

        // Find identified views
        textView = root.findViewById(R.id.text_percentage);

        percent_table = root.findViewById(R.id.percent_table);
        main_percentage = root.findViewById(R.id.main_percentage);

        // Buttons and text for the user data
        wp = root.findViewById(R.id.weight_plus);
        wm = root.findViewById(R.id.weight_minus);
        wc = root.findViewById(R.id.weight_text);

        setPercentageViewModel();
        setStaticTable();

        // Watching button clicks
        weightOnClick();

        // Watching text changes
        weightTextWatcher();

        // Find and set spinner
        exercise_spin = root.findViewById(R.id.exercise_spinner);
        spinnerSetup();
        spinnerChange();

        return root;
    }

    ////////////////////////////////////////////////
    //////////////// HELPER METHODS ////////////////
    ////////////////////////////////////////////////
    // Adds static percent and reps
    private void setStaticTable() {
        double top_per = 95;  // Change top percent to get lower or higher percent column
        for (int i = 1; i < percent_table.getChildCount(); i++) {
            row = (TableRow) percent_table.getChildAt(i);  // Table row at child index
            //Log.d("Found row", String.valueOf(row));
            pc = (TextView) row.getChildAt(0);  // The percentage id column
            //Log.d("Found plate percent", String.valueOf(pc));
            DecimalFormat decimalFormat = new DecimalFormat("0.#");  // Remove trailing 0
            pc.setText(decimalFormat.format(top_per));  // The string value for that row
            //Log.d("Plate weight text", (String) pc.getText());
            top_per -= 2.5;  // Decrement the percent value to properly fill the table
            rc = (TextView) row.getChildAt(2);  // The rep id column
            rc.setText(String.valueOf(i+1));  // The string value for that column
        }
    }

    // Return weight from row percentage
    private String calcWeightPercent(String percent) {
        double percent_double = Double.parseDouble(percent) / 100;
        return String.valueOf((int) Math.round(percent_double * weight_int));
    }

    // Sets the weight column to proper values
    private void calcSetRows(boolean validInt) {
        if (validInt) {
            for (int i = 1; i < percent_table.getChildCount(); i++) {
                row = (TableRow) percent_table.getChildAt(i);  // Table row at child index
                //Log.d("Found row", String.valueOf(row));
                pc = (TextView) row.getChildAt(0);  // The percentage id column
                wr = (TextView) row.getChildAt(1);  // The percentage id column
                //Log.d("Found plate percent", String.valueOf(pc));

                wr.setText(calcWeightPercent((String) pc.getText()));  // The string value for that row
                //Log.d("Plate weight text", (String) pc.getText());
            }
        } else {
            for (int i = 1; i < percent_table.getChildCount(); i++) {
                row = (TableRow) percent_table.getChildAt(i);  // Table row at child index
                //Log.d("Found row", String.valueOf(row));
                wr = (TextView) row.getChildAt(1);  // The percentage id column
                //Log.d("Found plate percent", String.valueOf(pc));

                wr.setText(getResources().getString(R.string.plate_hold));  // The string value for that row
                //Log.d("Plate weight text", (String) pc.getText());
            }
        }
    }

    // Initialize the spinner items
    private void newSpinnerSetup() {
        spinnerAdapter = new SpinnerAdapter(getActivity(), exercise_imgs_less, exercise_name_less);
        exercise_spin.setAdapter(spinnerAdapter);
    }

    ////////////////////////////////////////////////
    ///////////////// MAIN METHODS /////////////////
    ////////////////////////////////////////////////
    // Volley request the maxes
    private void requestMaxes() {
        // TODO: Create volley request to fill max_weight (done once on create)
    }

    // Watch the weight for a change to recalculate
    private void weightTextWatcher() {
        // Shows the hint when the weight is 0
        wc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Reset table if value has changed from focus
                    if (!wc.getText().toString().equals(String.valueOf(weight_int))) {
                        calcSetRows(false);
                    }
                    spinnerSetup();  // Reset spinner choice if user enters weight
                    wc.setHint("");
                } else if (weight_int == 0) {
                    wc.setHint(getResources().getString(R.string.weight));
                }
            }
        });

        wc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                // Sets weight to given weight if possible, else weight is 0
                try {
                    weight_int = Integer.parseInt(String.valueOf(wc.getText()));
                    exercise_spin.setSelection(0);  // Reset spinner choice if user enters weight
                    //Log.d("hey_int", String.valueOf(weight_int));
                    calcSetRows(true);
                    wc.setHint("");
                } catch (NumberFormatException notInt) {
                    weight_int = 0;
                    //Log.d("ENTERED?", String.valueOf(notInt));
                    calcSetRows(false);
                    wc.setHint(getResources().getString(R.string.weight));
                }
            }
        });
    }

    // Click listeners for changing weight
    private void weightOnClick() {
        wp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_percentage.requestFocus();
                try {
                    spinnerSetup();  // Reset spinner choice if user enters weight
                    weight_int = Integer.parseInt(String.valueOf(wc.getText()));
                    Log.d("w_int", String.valueOf(weight_int));
                    wc.setText(String.valueOf(weight_int + 1));
                    weight_int = Integer.parseInt(String.valueOf(wc.getText()));
                    calcSetRows(true);
                } catch (NumberFormatException notInt) {
                    weight_int = 1;
                    Log.d("we_int", String.valueOf(weight_int));
                    wc.setText(String.valueOf(weight_int));
                    calcSetRows(true);
                }
            }
        });

        wm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weight_int == 0 || String.valueOf(wc.getText()).equals("")) {
                    Toast.makeText(getActivity(),"No negative weight bro!",Toast.LENGTH_SHORT).show();
                } else {
                    main_percentage.requestFocus();
                    weight_int = Integer.parseInt(String.valueOf(wc.getText()));
                    //Log.d("w_int", String.valueOf(weight_int));
                    wc.setText(String.valueOf(weight_int - 1));
                    weight_int = Integer.parseInt(String.valueOf(wc.getText()));
                }
            }
        });
    }

    // Initialize the spinner items
    private void spinnerSetup() {
        spinnerAdapter = new SpinnerAdapter(getActivity(), exercise_imgs, exercise_names);
        exercise_spin.setAdapter(spinnerAdapter);
    }

    // Removes the entered weight if an exercise is selected
    private void spinnerChange() {
        exercise_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // The spinner has been changed so the max array positions are different
                if (!spinnerAdapter.getNames()[0].equals("Choice")) {
                    main_percentage.requestFocus();  // Remove focus from edit weight
                    wc.setText("");  // Edit weight text needs reset
                    wc.setHint(getResources().getString(R.string.weight));  // Reset the edit weight hint
                    Log.d("p", String.valueOf(position));
                    weight_int = max_weight[position];  // This will get the max weight for selected exercise
                    calcSetRows(true);
                }
                // The spinner still contains the choice and is has not been changed from default
                if (position != 0 && spinnerAdapter.getNames()[0].equals("Choice")) {
                    newSpinnerSetup();  // Remove the base option from the spinner
                    exercise_spin.setSelection(position-1);

                    main_percentage.requestFocus();
                    wc.setText("");  // Edit weight text needs reset
                    wc.setHint(getResources().getString(R.string.weight));  // Reset the edit weight hint

                    weight_int = max_weight[position-1];
                    calcSetRows(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    // Setup for viewmodel
    private void setPercentageViewModel() {
        percentageViewModel =
                new ViewModelProvider(this).get(PercentageViewModel.class);
        percentageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
    }
}