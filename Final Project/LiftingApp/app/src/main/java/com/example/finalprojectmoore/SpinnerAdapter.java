package com.example.finalprojectmoore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter {
    Context context;
    int exercise_imgs[];
    String[] exercise_names;
    LayoutInflater layoutInflater;

    public SpinnerAdapter(Context applicationContext, int[] exercise_imgs, String[] exercise_names) {
        this.context = applicationContext;
        this.exercise_imgs = exercise_imgs;
        this.exercise_names = exercise_names;
        layoutInflater = (LayoutInflater.from(applicationContext));
    }

    public String[] getNames() {
        return exercise_names;
    }

    @Override
    public int getCount() {
        return exercise_imgs.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.custom_exercise_spinner, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        icon.setImageResource(exercise_imgs[i]);
        names.setText(exercise_names[i]);
        return view;
    }
}
