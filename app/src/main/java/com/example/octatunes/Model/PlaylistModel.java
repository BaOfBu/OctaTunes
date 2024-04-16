package com.example.octatunes.Model;

import java.util.List;

public class PlaylistModel {
    private String title;
    private String description;
    private int coverImageResId;
    private List<SongModel> songs;  // List to store songs

    // Constructor to initialize the PlaylistModel object
    public PlaylistModel(String title, String description, int coverImageResId, List<SongModel> songs) {
        this.title = title;
        this.description = description;
        this.coverImageResId = coverImageResId;
        this.songs = songs;
    }

    // Getters and Setters for the title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getters and Setters for the description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getters and Setters for the cover image resource ID
    public int getCoverImageResId() {
        return coverImageResId;
    }

    public void setCoverImageResId(int coverImageResId) {
        this.coverImageResId = coverImageResId;
    }

    // Getters and Setters for the list of songs
    public List<SongModel> getSongs() {
        return songs;
    }

    public void setSongs(List<SongModel> songs) {
        this.songs = songs;
    }
}
