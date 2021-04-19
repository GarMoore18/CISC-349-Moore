package com.example.finalprojectmoore.ui.platecalculator;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectmoore.R;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.Arrays;

public class PlatesFragment extends Fragment {

    private PlatesViewModel platesViewModel;
    private View root;
    private TableLayout plate_table;

    // For setting the weight column
    String[] plateStringValues = new String[] {"45", "35", "25", "10", "5", "2.5"};
    private TableRow row, tot_weight;
    private TextView pw, tw, rw;

    // For setting the weight toggles
    private Button wp, wm, bp, bm;
    private EditText wc, bc;
    private int weight_int = 0, barbell_int = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.plates_fragment, container, false);

        setToolbarIcon();

        // Find identified views
        rw = root.findViewById(R.id.text_plates);

        plate_table = root.findViewById(R.id.plate_table);  // Table found

        // Buttons and text for the user data
        wp = root.findViewById(R.id.weight_plus);
        wm = root.findViewById(R.id.weight_minus);
        wc = root.findViewById(R.id.weight_text);
        bp = root.findViewById(R.id.barbell_plus);
        bm = root.findViewById(R.id.barbell_minus);
        bc = root.findViewById(R.id.barbell_text);

        // The total weight will be the top toggle
        tot_weight = (TableRow) plate_table.getChildAt(0);
        setTotWeight();

        setPlatesViewModel();
        addPlatesTable();  // Properly fills the table with the plates

        // Watching button clicks
        weightOnClick();
        barbellOnClick();

        // Watching text changes
        weightTextWatcher();
        barbellTextWatcher();

        return root;
    }

    ////////////////////////////////////////////////
    //////////////// HELPER METHODS ////////////////
    ////////////////////////////////////////////////

    // Set total weight table title
    private void setTotWeight() {
        tw = (TextView) tot_weight.getChildAt(0);  // The plate_weight id column
        //Log.d("Total weight", String.valueOf(tw));
        tw.setText(String.format(getResources().getString(R.string.total_weight), weight_int));
    }

    // Ensure barbell weight is proper and weight is positive
    private boolean validWeight() {
        return barbell_int <= weight_int && weight_int >= 0;
    }

    // Plate count values added to the table
    private void addCountTable(int[] platesCount) {
        for (int i = 2; i < plate_table.getChildCount(); i++) {
            row = (TableRow) plate_table.getChildAt(i);  // Table row at child index
            //Log.d("Found row", String.valueOf(row));
            pw = (TextView) row.getChildAt(1);  // The plate_weight id column
            //Log.d("Found plate weight", String.valueOf(pw));
            pw.setText(String.valueOf(platesCount[i-2]));  // The string value for that row
            //Log.d("Plate weight text", (String) pw.getText());
        }
    }

    // Displays error message or runs calculations
    private void errorOrCalc() {
        if (validWeight()) {
            addCountTable(calcPlates(weight_int, barbell_int));
        } else {
            Toast.makeText(getActivity(),"Total weight must be more than barbell!",Toast.LENGTH_SHORT).show();
        }
    }

    // Will return how many of each plate needed per side
    private int[] calcPlates(int weight, int barbell) {
        int[] plateCount = new int[6];  // Starts with plate weights, ends with plate count
        double remainder;  //
        int plates;
        remainder = (double) (weight - barbell) / 2;

        int i;
        for (i = 0; i < plateStringValues.length; i++) {
            double plate_weight = Double.parseDouble(plateStringValues[i]);

            plates = (int) (remainder / plate_weight);  // Number of plates needed
            //Log.d("Plates", String.valueOf(plates));

            remainder = remainder % plate_weight; // Remaining weight when adding current plates
            //Log.d("Remainder", String.valueOf(remainder));

            plateCount[i] = plates;  // Store the num plates needed in array
            //Log.d("Plates", String.valueOf(plateArray[i]));

            if (remainder == 0) {
                i++;
                break; }  // Stop if there are no more plates needed
        }

        // Fills rest of array with 0
        for (int j = i; j < plateCount.length; j++) {
            plateCount[j] = 0;
        }

        // Show the desired weight and highest possible weight
        int actual_weight = (int) (weight_int - (remainder * 2));
        rw.setText(String.format(getResources().getString(R.string.calc_info), weight_int, actual_weight));
        //Log.d("Plate count", Arrays.toString(plateArray));

        return plateCount;
    }

    ////////////////////////////////////////////////
    ///////////////// MAIN METHODS /////////////////
    ////////////////////////////////////////////////

    // Watch the weight for a change to recalculate
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

                setTotWeight();  // Call to update the total weight row
                errorOrCalc();  // Call to try calculating
            }
        });
    }

    // Watch the barbell for a change to recalculate
    private void barbellTextWatcher() {
        // Shows the hint when the barbell weight is 0
        bc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    bc.setHint("");
                } else if (barbell_int == 0) {
                    bc.setHint(getResources().getString(R.string.barbell));
                }
            }
        });

        bc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                // Sets barbell weight to given weight if possible, else barbell is 0
                try {
                    barbell_int = Integer.parseInt(String.valueOf(bc.getText()));
                    bc.setHint("");
                } catch (NumberFormatException notInt) {
                    barbell_int = 0;
                    bc.setHint(getResources().getString(R.string.barbell));
                }

                errorOrCalc();  // Call to try calculating
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
    private void barbellOnClick() {
        bp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Attempt to get the int, unless a weight has not been used
                try {
                    barbell_int = Integer.parseInt(String.valueOf(bc.getText()));
                    //Log.d("b_int", String.valueOf(barbell_int));
                    bc.setText(String.valueOf(barbell_int + 1));
                    barbell_int = Integer.parseInt(String.valueOf(bc.getText()));
                } catch (NumberFormatException notInt) {
                    barbell_int = 1;
                    //Log.d("be_int", String.valueOf(barbell_int));
                    bc.setText(String.valueOf(barbell_int));
                }
            }
        });

        bm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barbell_int == 0) {
                    Toast.makeText(getActivity(),"No negative weight bro!",Toast.LENGTH_SHORT).show();
                } else {
                    barbell_int = Integer.parseInt(String.valueOf(bc.getText()));
                    //Log.d("b_int", String.valueOf(barbell_int));
                    bc.setText(String.valueOf(barbell_int - 1));
                    barbell_int = Integer.parseInt(String.valueOf(bc.getText()));
                }
            }
        });
    }

    // Plate weight values added to the table
    private void addPlatesTable() {
        // Start at non-title rows
        for (int i = 2; i < plate_table.getChildCount(); i++) {
            row = (TableRow) plate_table.getChildAt(i);  // Table row at child index
            //Log.d("Found row", String.valueOf(row));
            pw = (TextView) row.getChildAt(0);  // The plate_weight id column
            //Log.d("Found plate weight", String.valueOf(pw));
            pw.setText(plateStringValues[i-2]);  // The string value for that row
            //Log.d("Plate weight text", (String) pw.getText());
        }
    }

    // Setup for viewmodel
    private void setPlatesViewModel() {
        platesViewModel =
                new ViewModelProvider(this).get(PlatesViewModel.class);
        platesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                rw.setText(s);
            }
        });
    }

    // Sets correct image in the toolbar
    private void setToolbarIcon() {
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem icon = menu.findItem(R.id.icon_set);
        icon.setIcon(R.drawable.menu_plates);
        Drawable drawable = icon.getIcon();
        drawable.mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
    }
}