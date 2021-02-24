package com.example.assignment_2_dynamic_list_view;

public class HolidaySongs {

    private final String album_img;
    private final String album_name;
    private final String artist_name;
    private final Double danceability;
    private final Integer duration_ms; //should be in minutes and seconds
    private final String playlist_img;

    public HolidaySongs(String album_img, String album_name, String artist_name, Double danceability, Integer duration_ms, String playlist_img) {
        this.album_img = album_img;
        this.album_name = album_name;
        this.artist_name = artist_name;
        this.danceability = danceability;
        this.duration_ms = duration_ms;
        this.playlist_img = playlist_img;
    }

    public String getAlbum_img() {
        return album_img;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public Double getDanceability() {
        return danceability;
    }

    public Integer getDuration_ms() {
        return duration_ms;
    }

    public String getPlaylist_img() {
        return playlist_img;
    }
}
