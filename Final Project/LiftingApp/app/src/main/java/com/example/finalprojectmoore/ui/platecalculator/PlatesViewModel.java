package com.example.finalprojectmoore.ui.platecalculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlatesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PlatesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is plates fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}