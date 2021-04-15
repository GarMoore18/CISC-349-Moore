package com.example.finalprojectmoore.ui.percentagecalculator;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalprojectmoore.MainActivity;
import com.example.finalprojectmoore.R;
import com.example.finalprojectmoore.SetInformation;
import com.example.finalprojectmoore.SpinnerAdapter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PercentageFragment extends Fragment {

    private static final String url = "http://10.0.0.133:5000/get_maxes";

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

    private int[] max_weight = new int[3];  // TODO: Fill with maxes from database
    private TableRow row;
    private TextView pc, wr, rc;

    private Button wp, wm;
    private EditText wc;
    private int weight_int = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.percentage_fragment, container, false);

        setToolbarIcon();

        requestMaxes();

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // Creating the volley request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject exercise_max = response.getJSONObject(i);
                                try {
                                    int exercise_id = exercise_max.getInt("exercise_id");
                                    int one_rep_max = exercise_max.getInt("1_rep_max");
                                    max_weight[exercise_id] = one_rep_max;
                                } catch (JSONException noMax) {
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred

                    }
                }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");  // Need this to talk to Flask
                return params;
            }
        };

        requestQueue.add(jsonArrayRequest);   //Add request to the queue
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
                    //Log.d("we_int", String.valueOf(weight_int));
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
                    //Log.d("p", String.valueOf(position));
                    weight_int = max_weight[position];  // This will get the max weight for selected exercise
                    calcSetRows(true);

                    if (weight_int == 0) {
                        Toast.makeText(getActivity(), "There were no sets recorded for " + exercise_name_less[position], Toast.LENGTH_SHORT).show();
                    }
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

                    if (weight_int == 0) {
                        Toast.makeText(getActivity(), "There were no sets recorded for " + exercise_name_less[position-1], Toast.LENGTH_SHORT).show();
                    }
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

    // Sets correct image in the toolbar
    private void setToolbarIcon() {
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem icon = menu.findItem(R.id.icon_set);
        icon.setIcon(R.drawable.menu_percentage);
        Drawable drawable = icon.getIcon();
        drawable.mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
    }
}