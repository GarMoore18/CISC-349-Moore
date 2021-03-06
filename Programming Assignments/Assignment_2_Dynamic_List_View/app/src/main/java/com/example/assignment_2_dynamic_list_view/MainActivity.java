package com.example.assignment_2_dynamic_list_view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<HolidaySongs> arrayOfSongs = new ArrayList<>(); //Create array of songs

        // Create the adapter to convert the array to views
        HolidaySongsAdapter adapter = new HolidaySongsAdapter(this, arrayOfSongs);

        // Attach the adapter to a ListView
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(adapter);

        //Using volley to request the information
        RequestQueue queue = Volley.newRequestQueue(this);

        //Creating image loader with LruCache
        imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String,
                    Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);

            }
        });

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                "https://setify.info:3000/holiday_songs_spotify", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObjectFromArray =
                                        response.getJSONObject(i);

                                HolidaySongs song = new HolidaySongs(jsonObjectFromArray.getString("album_img"),
                                        jsonObjectFromArray.getString("album_name"),
                                        jsonObjectFromArray.getString("artist_name"),
                                        jsonObjectFromArray.getDouble("danceability"),
                                        jsonObjectFromArray.getInt("duration_ms"),
                                        jsonObjectFromArray.getString("playlist_img"));

                                arrayOfSongs.add(song);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapter.notifyDataSetChanged();
                        //Log.d("JSONArray Response", "HEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE" + arrayOfSongs.size());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("JSONArray Error", "Error:" + error);
            }
        });
        queue.add(jsonArrayRequest);   //Add request to the queue

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewSongs.class);
                intent.putExtra("ALBUM_NAME", arrayOfSongs.get(position).getAlbum_name());
                intent.putExtra("PLAYLIST_IMG", arrayOfSongs.get(position).getPlaylist_img());

                startActivity(intent);
            }
        });
    }
}