package com.example.finalprojectmoore.ui.platecalculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlatesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PlatesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("*Calculated weight may be lower than desired");
    }

    public LiveData<String> getText() {
        return mText;
    }
}