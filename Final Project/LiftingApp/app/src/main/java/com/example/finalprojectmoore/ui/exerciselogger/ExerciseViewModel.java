package com.example.finalprojectmoore.ui.exerciselogger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExerciseViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ExerciseViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("*Add exercise, weight, and reps.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}