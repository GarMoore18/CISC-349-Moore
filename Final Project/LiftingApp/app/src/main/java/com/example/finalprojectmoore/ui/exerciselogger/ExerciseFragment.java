package com.example.finalprojectmoore.ui.exerciselogger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalprojectmoore.MainActivity;
import com.example.finalprojectmoore.R;
import com.example.finalprojectmoore.SpinnerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ExerciseFragment extends Fragment {

    private static final String url = "http://10.0.0.133:5000/add_set";

    private ExerciseViewModel exerciseViewModel;
    private View root;
    private TextView notifyCap;

    private Spinner exercise_spin;
    private SpinnerAdapter spinnerAdapter;
    private final String[] exercise_names = {"Choice", "Bench", "Deadlift", "Squat"};
    private final int[] exercise_imgs = {R.drawable.dropdown, R.drawable.spinner_bench, R.drawable.spinner_deadlift, R.drawable.spinner_squat};
    private final String[] exercise_name_less = {"Bench", "Deadlift", "Squat"};
    private final int[] exercise_imgs_less = {R.drawable.spinner_bench, R.drawable.spinner_deadlift, R.drawable.spinner_squat};

    // For setting the weight toggles
    private Button wp, wm, rp, rm;
    private EditText wc, rc;
    private int weight_int = 0, rep_int = 0, one_rep_max = -1;

    private Button submit;

    private int spinner_position;
    private boolean added;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.exercise_fragment, container, false);

        setToolbarIcon();

        notifyCap = root.findViewById(R.id.text_exercise);

        // Buttons and text for the user data
        wp = root.findViewById(R.id.weight_plus);
        wm = root.findViewById(R.id.weight_minus);
        wc = root.findViewById(R.id.weight_text);
        rp = root.findViewById(R.id.reps_plus);
        rm = root.findViewById(R.id.reps_minus);
        rc = root.findViewById(R.id.reps_text);

        submit = root.findViewById(R.id.add_exercise);

        setGraphsViewModel();

        // Find and set spinner
        exercise_spin = root.findViewById(R.id.exercise_spinner);
        spinnerSetup();
        spinnerChange();

        // Watching button clicks
        weightOnClick();
        repsOnClick();
        submitOnClick();

        // Watching text changes
        weightTextWatcher();
        repsTextWatcher();

        return root;
    }

    private int oneRepMaxBrzycki(int weight, int reps) {
        double oneRepMax = weight / (1.0278 - (0.0278 * reps));
        return (int) Math.round(oneRepMax);
    }

    private void setRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        if (!(rep_int > 10)) {
            one_rep_max = oneRepMaxBrzycki(weight_int, rep_int);
        }

        // Creating the JSON object to POST to flask
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", ((MainActivity) getActivity()).user_id);
            jsonObject.put("exercise_id", spinner_position);
            jsonObject.put("weight", weight_int);
            jsonObject.put("reps", rep_int);
            jsonObject.put("1_rep_max", one_rep_max);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Object", String.valueOf(jsonObject));
        // Creating the volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", String.valueOf(response));
                        //Log.d("Response", String.valueOf(response.length()));

                        // Get the set added response
                        try {
                            added = response.getBoolean("added");
                            Log.d("Added", String.valueOf(added));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Display correct message for added boolean
                        if (added) {
                            submitSuccessDialog();
                            one_rep_max = -1;
                        } else {
                            submitFailureDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", String.valueOf(error));
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

        requestQueue.add(jsonObjectRequest);   //Add request to the queue
    }

    // Adds a hyperlink to the
    private void setHyperLink() {
        String linkText = "Set has more than 10 reps." +
                "<br>The set will be excluded from 1RM." +
                "<br>Check out the <a href='http://www.weightrainer.net/training/coefficients.html'>Brzycki Formula</a>";
        notifyCap.setText(Html.fromHtml(linkText, Html.FROM_HTML_MODE_LEGACY));
        notifyCap.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private boolean checkRepCap() {
        return rep_int > 10;
    }

    private void weightTextWatcher() {
        // Shows the hint when the weight is 0
        wc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
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
                    wc.setHint("");
                } catch (NumberFormatException notInt) {
                    weight_int = 0;
                    wc.setHint(getResources().getString(R.string.weight));
                }
            }
        });
    }

    // Watch the barbell for a change to recalculate
    private void repsTextWatcher() {
        // Shows the hint when the barbell weight is 0
        rc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    rc.setHint("");
                } else if (rep_int == 0) {
                    rc.setHint(getResources().getString(R.string.reps_caps));
                }
            }
        });

        rc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                // Sets barbell weight to given weight if possible, else barbell is 0
                try {
                    rep_int = Integer.parseInt(String.valueOf(rc.getText()));
                    rc.setHint("");
                } catch (NumberFormatException notInt) {
                    rep_int = 0;
                    rc.setHint(getResources().getString(R.string.reps_caps));
                }
                if (checkRepCap()) {
                    setHyperLink();  // Sets the issues and link to learn more
                } else {
                    notifyCap.setText(getResources().getString(R.string.add_set_info));
                }
            }
        });
    }

    // Click listeners for changing weight
    private void weightOnClick() {
        wp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    weight_int = Integer.parseInt(String.valueOf(wc.getText()));
                    //Log.d("w_int", String.valueOf(weight_int));
                    wc.setText(String.valueOf(weight_int + 1));
                    weight_int = Integer.parseInt(String.valueOf(wc.getText()));
                } catch (NumberFormatException notInt) {
                    weight_int = 1;
                    //Log.d("we_int", String.valueOf(weight_int));
                    wc.setText(String.valueOf(weight_int));
                }
            }
        });

        wm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weight_int == 0) {
                    Toast.makeText(getActivity(),"No negative weight bro!",Toast.LENGTH_SHORT).show();
                } else {
                    weight_int = Integer.parseInt(String.valueOf(wc.getText()));
                    //Log.d("w_int", String.valueOf(weight_int));
                    wc.setText(String.valueOf(weight_int - 1));
                    weight_int = Integer.parseInt(String.valueOf(wc.getText()));
                }
            }
        });
    }

    // Click listeners for changing barbell
    private void repsOnClick() {
        rp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Attempt to get the int, unless a weight has not been used
                try {
                    rep_int = Integer.parseInt(String.valueOf(rc.getText()));
                    //Log.d("b_int", String.valueOf(barbell_int));
                    rc.setText(String.valueOf(rep_int + 1));
                    rep_int = Integer.parseInt(String.valueOf(rc.getText()));
                } catch (NumberFormatException notInt) {
                    rep_int = 1;
                    //Log.d("be_int", String.valueOf(barbell_int));
                    rc.setText(String.valueOf(rep_int));
                }
            }
        });

        rm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rep_int == 0) {
                    Toast.makeText(getActivity(),"No negative reps bro!",Toast.LENGTH_SHORT).show();
                } else {
                    rep_int = Integer.parseInt(String.valueOf(rc.getText()));
                    //Log.d("b_int", String.valueOf(barbell_int));
                    rc.setText(String.valueOf(rep_int - 1));
                    rep_int = Integer.parseInt(String.valueOf(rc.getText()));
                }
            }
        });
    }

    private void submitOnClick() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFieldsProvided()) {
                    setRequest();
                } else {
                    promptCheckFields();
                }
            }
        });
    }

    private boolean allFieldsProvided() {
        return !spinnerAdapter.getNames()[0].equals("Choice") && !(weight_int == 0) && !(rep_int == 0);
    }

    // TODO: Call this on success in volley
    private void submitSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(false);
        builder.setTitle("Good lift!");
        builder.setMessage("The set was added.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Reset the fields for the set
                weight_int = 0;
                rep_int = 0;
                wc.setText("");
                rc.setText("");
                spinnerSetup();  // Set the original spinner adapter
            }
        });
        builder.show();
    }

    // TODO: Call this on volley error
    private void submitFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(false);
        builder.setTitle("Bad spot!");
        builder.setMessage("Sorry, the set was not added.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void promptCheckFields() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(false);
        builder.setTitle("Confused?");
        builder.setMessage("Choose an exercise.\nAdd weight more than 0.\nAdd reps more than 0.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    // Initialize the spinner items
    private void newSpinnerSetup() {
        spinnerAdapter = new SpinnerAdapter(getActivity(), exercise_imgs_less, exercise_name_less);
        exercise_spin.setAdapter(spinnerAdapter);
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
                // The spinner still contains the choice and is has not been changed from default
                if (position != 0 && spinnerAdapter.getNames()[0].equals("Choice")) {
                    newSpinnerSetup();  // Remove the base option from the spinner
                    exercise_spin.setSelection(position-1);

                } else if (!spinnerAdapter.getNames()[0].equals("Choice")) {
                    exercise_spin.setSelection(position);
                }
                spinner_position = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    // Setup for viewmodel
    private void setGraphsViewModel() {
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        exerciseViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                notifyCap.setText(s);
            }
        });
    }

    // Sets correct image in the toolbar
    private void setToolbarIcon() {
        // Since this loads with activity ingnore the null pointer
        try {
            Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
            Menu menu = toolbar.getMenu();
            MenuItem icon = menu.findItem(R.id.icon_set);
            icon.setIcon(R.drawable.menu_exercise);
            Drawable drawable = icon.getIcon();
            drawable.mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        } catch (NullPointerException ignored) { }
    }
}