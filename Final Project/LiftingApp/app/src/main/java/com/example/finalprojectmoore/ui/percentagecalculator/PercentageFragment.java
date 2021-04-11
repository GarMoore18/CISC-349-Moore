package com.example.finalprojectmoore.ui.percentagecalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalprojectmoore.R;
import com.example.finalprojectmoore.SpinnerAdapter;
import com.example.finalprojectmoore.ui.platecalculator.PlatesViewModel;

public class PercentageFragment extends Fragment {

    // TODO: REQUEST WHEN CHANGE TO FRAGMENT --> BENCH, DEADLIFT, SQUAT MAX

    private PercentageViewModel percentageViewModel;
    private View root;
    private TextView textView;

    private Spinner exercise_spin;
    private String[] exercise_names = {"Bench", "Deadlift", "Squat"};
    private int[] exercise_imgs = {R.drawable.spinner_bench, R.drawable.spinner_deadlift, R.drawable.spinner_squat};

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.percentage_fragment, container, false);

        // Find identified views
        textView = root.findViewById(R.id.text_percentage);

        setPercentageViewModel();

        // Find and set spinner
        exercise_spin = root.findViewById(R.id.exercise_spinner);
        spinnerSetup();

        return root;
    }

    private void spinnerSetup() {
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), exercise_imgs, exercise_names);
        exercise_spin.setAdapter(spinnerAdapter);
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