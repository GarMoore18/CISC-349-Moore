package com.example.customadaptertest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> arrayList;
    //private TextView name, phone;

    public UserAdapter(Context context, ArrayList<User> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false );
        TextView name = convertView.findViewById(R.id.name);
        TextView phone = convertView.findViewById(R.id.phone);

        name.setText(arrayList.get(position).getName());
        phone.setText(arrayList.get(position).getPhone());

        return convertView;
    }
}
