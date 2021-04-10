package com.example.finalprojectmoore.ui.graphsdisplay;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GraphsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GraphsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is graphs fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}