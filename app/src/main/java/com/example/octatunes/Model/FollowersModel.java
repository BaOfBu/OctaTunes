package com.example.octatunes.Model;

public class FollowersModel {
    private int UserID;
    private int ArtistID;
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
}
