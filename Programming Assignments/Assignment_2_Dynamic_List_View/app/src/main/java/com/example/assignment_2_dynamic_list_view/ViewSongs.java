package com.example.assignment_2_dynamic_list_view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.logging.Logger;

public class ViewSongs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_songs);

        //Find intent and store passed data
        Intent i = getIntent();
        String album_name = i.getStringExtra("ALBUM_NAME");
        String playlist_img_url = i.getStringExtra("PLAYLIST_IMG");

        //Find the assets that need to be altered
        TextView big_album_name = findViewById(R.id.big_album_name);
        NetworkImageView playlist_img = findViewById(R.id.playlist_img);

        //Setting assets with correct information
        big_album_name.setText(album_name);
        playlist_img.setImageUrl(playlist_img_url, MainActivity.imageLoader);
    }
}