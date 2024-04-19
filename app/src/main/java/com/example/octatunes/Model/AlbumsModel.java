package com.example.octatunes.Model;

import com.google.gson.Gson;

import java.util.Date;

public class AlbumsModel {
    private int AlbumID;
    private int ArtistID;
    private String Name;
    private Date ReleaseDate;
    private String Image;
    public AlbumsModel() {

    }
    public AlbumsModel(int albumID, int artistID, String name, Date releaseDate, String image) {
        AlbumID = albumID;
        ArtistID = artistID;
        Name = name;
        ReleaseDate = releaseDate;
        Image = image;
    }
    public int getAlbumID() {
        return AlbumID;
    }

    public void setAlbumID(int albumID) {
        AlbumID = albumID;
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

    public Date getReleaseDate() {
        return ReleaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        ReleaseDate = releaseDate;
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
