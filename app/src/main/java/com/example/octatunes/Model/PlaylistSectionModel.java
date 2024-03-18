package com.example.octatunes.Model;

import java.util.List;

public class PlaylistSectionModel {
    private String title;
    private List<PlaylistModel> playlistItems;

    public PlaylistSectionModel(String title, List<PlaylistModel> playlistItems) {
        this.title = title;
        this.playlistItems = playlistItems;
    }

    public String getTitle() {
        return title;
    }

    public List<PlaylistModel> getPlaylistItems() {
        return playlistItems;
    }
}
