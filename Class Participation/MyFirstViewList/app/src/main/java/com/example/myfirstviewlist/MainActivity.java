package com.example.myfirstviewlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ListView listView;
    TextView textView;
    String[] listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);
        textView = findViewById(R.id.textView);
        listItem = getResources().getStringArray(R.array.array_technology);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_list_layout, listItem);
        listView.setAdapter(adapter);
        /*for (String s : listItem) {
            Log.d(TAG, s);
        }*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, adapter.getItem(position));
                String lang = adapter.getItem(position);
                Toast.makeText(MainActivity.this, lang, Toast.LENGTH_SHORT).show();
            }
        });

        }
    }