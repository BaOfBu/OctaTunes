package com.example.octatunes.Model;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailModel {
    private String title;

    private PlaylistModel playlistModel;
    private int imageUrl;

    public PlaylistDetailModel(String title,PlaylistModel playlistModel, int imageUrl) {
        this.title = title;
        this.playlistModel = playlistModel;
        this.imageUrl = imageUrl;
    }
    public PlaylistModel getPlaylistModel(){
        return this.playlistModel;
    }
    public String getTitle() {
        return title;
    }

    public int getImageUrl() {
        return imageUrl;
    }
}

