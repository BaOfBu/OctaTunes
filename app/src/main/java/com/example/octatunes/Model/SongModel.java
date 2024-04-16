package com.example.octatunes.Model;

public class SongModel {
    // Attributes of the song
    private String title;
    private String artist;
    private String album;
    private int duration;
    private int imageUrl;
    private String streamUrl;

    public SongModel(String title, String artist, String album, int duration, int imageUrl, String streamUrl) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.imageUrl = imageUrl;
        this.streamUrl = streamUrl;
    }

    // Getters and setters for each attribute
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
}
