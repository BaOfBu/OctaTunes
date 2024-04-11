package com.example.octatunes.Model;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailModel {
    private String title;

    private List<PlaylistModel> playlistModel = new ArrayList<>();
    private int imageUrl;

    public PlaylistDetailModel(String title,List<PlaylistModel> playlistModel, int imageUrl) {
        this.title = title;
        this.playlistModel = playlistModel;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getImageUrl() {
        return imageUrl;
    }
}

