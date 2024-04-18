package com.example.octatunes.Model;

import com.google.gson.Gson;

public class ArtistsModel {
    private int ArtistID;
    private String Name;
    private String Genre;
    private String Image;
    public ArtistsModel(){

    }
    public ArtistsModel(int artistID, String name, String genre, String image) {
        ArtistID = artistID;
        Name = name;
        Genre = genre;
        Image = image;
    }
    public int getArtistID() {
        return ArtistID;
    }

    public void setArtistID(int artistID) {
        ArtistID = artistID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);

    }
}
