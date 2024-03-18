package com.example.octatunes.Model;

import java.util.List;

import java.util.List;

public class ArtistSectionModel {
    private String title;
    private List<ArtistItemModel> artistItemList;

    public ArtistSectionModel(String title, List<ArtistItemModel> artistItemList) {
        this.title = title;
        this.artistItemList = artistItemList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ArtistItemModel> getArtistItemList() {
        return artistItemList;
    }

    public void setArtistItemList(List<ArtistItemModel> artistItemList) {
        this.artistItemList = artistItemList;
    }
}
