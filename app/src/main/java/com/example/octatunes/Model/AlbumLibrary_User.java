package com.example.octatunes.Model;

import com.google.gson.Gson;

public class AlbumLibrary_User {

    private int AlbumID;
    private int UserID;
    public AlbumLibrary_User() {
    }

    public AlbumLibrary_User(int albumID, int userID) {
        AlbumID = albumID;
        UserID = userID;
    }



    public int getAlbumID() {
        return AlbumID;
    }

    public void setAlbumID(int albumID) {
        AlbumID = albumID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }



}
