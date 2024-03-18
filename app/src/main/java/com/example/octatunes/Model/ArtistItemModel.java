package com.example.octatunes.Model;

public class ArtistItemModel {
    private int imageResource;
    private String artistName;

    public ArtistItemModel(int imageResource, String artistName) {
        this.imageResource = imageResource;
        this.artistName = artistName;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
