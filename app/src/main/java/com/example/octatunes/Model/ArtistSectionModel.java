package com.example.octatunes.Model;

import java.util.List;

public class ArtistSectionModel {
    private String title;
    private List<ArtistModel> artistItemList;

    public ArtistSectionModel(String title, List<ArtistModel> artistItemList) {
        this.title = title;
        this.artistItemList = artistItemList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ArtistModel> getArtistItemList() {
        return artistItemList;
    }

    public void setArtistItemList(List<ArtistModel> artistItemList) {
        this.artistItemList = artistItemList;
    }
}
