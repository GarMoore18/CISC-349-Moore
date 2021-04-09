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
import android.widget.TextView;

import com.example.finalprojectmoore.R;

public class PlatesFragment extends Fragment {

    private PlatesViewModel platesViewModel;
    private View root;
    private TextView textView;

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
}