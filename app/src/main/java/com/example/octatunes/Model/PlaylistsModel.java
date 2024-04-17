package com.example.octatunes.Model;

import com.google.firebase.database.PropertyName;
import com.google.gson.Gson;

import java.io.Serializable;

public class PlaylistsModel implements Serializable {
    @PropertyName("playlistID")
    private int PlaylistID;
    @PropertyName("userId")
    private int UserID;
    @PropertyName("name")
    private String Name;

    @PropertyName("description")
    private String Description;
    @PropertyName("image")
    private String Image;

    public PlaylistsModel() {

    }

    public PlaylistsModel(int playlistID, int userID, String name, String image, String description) {
        PlaylistID = playlistID;
        UserID = userID;
        Name = name;
        Image = image;
        Description = description;
    }

    public String getDescription () {
        return Description;
    }

    public void setDescription (String description){
        Description = description;

    }

    public int getPlaylistID() {
        return PlaylistID;
    }

    public void setPlaylistID(int playlistID) {
        PlaylistID = playlistID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
