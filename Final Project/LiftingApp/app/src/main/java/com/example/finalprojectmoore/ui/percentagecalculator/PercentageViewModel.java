package com.example.finalprojectmoore.ui.percentagecalculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PercentageViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PercentageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("*Calculated weight is rounded");
    }

    public LiveData<String> getText() {
        return mText;
    }
}