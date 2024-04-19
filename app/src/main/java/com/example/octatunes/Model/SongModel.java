package com.example.octatunes.Model;

import com.google.gson.Gson;

public class SongModel {

    private int SongID;
    private String Title;
    private String Artist;
    private String Album;
    private String Genre;

    public SongModel() {
    }

    public SongModel(int songID, String title, String artist, String album, String genre) {
        SongID = songID;
        Title = title;
        Artist = artist;
        Album = album;
        Genre = genre;
    }

    public int getSongID() {
        return SongID;
    }

    public void setSongID(int songID) {
        SongID = songID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);

    }
}
