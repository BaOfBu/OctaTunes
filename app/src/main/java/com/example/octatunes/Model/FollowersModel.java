package com.example.octatunes.Model;

import com.google.gson.Gson;

public class FollowersModel {
    private int UserID;
    private int ArtistID;
    public FollowersModel() {

    }
    public FollowersModel(int userID, int artistID) {
        UserID = userID;
        ArtistID = artistID;
    }
    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getArtistID() {
        return ArtistID;
    }

    public void setArtistID(int artistID) {
        ArtistID = artistID;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);

    }
}
