package com.example.finalprojectmoore.ui.percentagecalculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PercentageViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PercentageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is percentage fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}