package com.example.assignment_2_dynamic_list_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HolidaySongsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HolidaySongs> arrayList;

    public HolidaySongsAdapter(Context context, ArrayList<HolidaySongs> arrayList) {
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
        convertView = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false );
        TextView album_img = convertView.findViewById(R.id.album_img);
        TextView album_name = convertView.findViewById(R.id.album_name);
        TextView artist_name = convertView.findViewById(R.id.artist_name);
        TextView danceability = convertView.findViewById(R.id.danceability);
        TextView duration = convertView.findViewById(R.id.duration);
        //TextView playlist_img = convertView.findViewById(R.id.playlist_img);


        album_img.setText(arrayList.get(position).getAlbum_img());
        album_name.setText(arrayList.get(position).getAlbum_name());
        artist_name.setText(arrayList.get(position).getArtist_name());
        danceability.setText(String.valueOf(arrayList.get(position).getDanceability()));

        //Convert ms to min:sec
        int duration_before = arrayList.get(position).getDuration_ms();
        @SuppressLint("DefaultLocale")
        String duration_after = String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(duration_before),
                TimeUnit.MILLISECONDS.toSeconds(duration_before) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration_before))
        );
        duration.setText(duration_after);

        //playlist_img.setText(arrayList.get(position).getPlaylist_img());

        return convertView;
    }
}
