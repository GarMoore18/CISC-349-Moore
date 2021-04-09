package com.example.finalprojectmoore.ui.percentagecalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalprojectmoore.R;
import com.example.finalprojectmoore.ui.platecalculator.PlatesViewModel;

public class PercentageFragment extends Fragment {

    private PercentageViewModel percentageViewModel;
    private View root;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.percentage_fragment, container, false);

        // Find identified views
        textView = root.findViewById(R.id.text_percentage);

        setPercentageViewModel();

        return root;
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