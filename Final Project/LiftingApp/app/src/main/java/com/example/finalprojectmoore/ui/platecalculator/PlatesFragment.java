package com.example.finalprojectmoore.ui.platecalculator;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.finalprojectmoore.R;

public class PlatesFragment extends Fragment {

    private PlatesViewModel platesViewModel;
    private View root;
    private TextView textView;

    private TableRow p45, p35, p25, p10, p5, p2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.plates_fragment, container, false);

        // Find identified views
        textView = root.findViewById(R.id.text_plates);

        setPlatesViewModel();

        return root;
    }

    // Setup for viewmodel
    private void setPlatesViewModel() {
        platesViewModel =
                new ViewModelProvider(this).get(PlatesViewModel.class);
        platesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
    }

    private boolean validWeight(int weight, int barbell) {
        return barbell <= weight && weight >= 0;
    }

    // TODO: CAN LEAVE A REMAINDER OF 1 OCCASIONALLY
    // Will return how many of each plate needed per side
    private double[] calcPlates(int weight, int barbell) {
        double[] plateArray = new double[] {45, 35, 25, 10, 5, 2.5};  // Starts with plate weights, ends with plate count
        double remainder = 0;  //
        double plates;
        weight = (weight - barbell) / 2;

        int i;
        for (i = 0; i < plateArray.length; i++) {
            plates = remainder / plateArray[i];  // Number of plates needed

            remainder = weight % plateArray[i]; // Remaining weight when adding current plates

            plateArray[i] = plates;  // Store the num plates needed in array

            if (remainder == 0) { break; }  // Stop if there are no more plates needed
        }

        // Fills rest of array with 0
        for (int j = i; j < plateArray.length; j++) {
            plateArray[j] = 0;
        }

        return plateArray;
    }
}