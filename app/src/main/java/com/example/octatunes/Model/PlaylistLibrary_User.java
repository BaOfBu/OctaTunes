package com.example.octatunes.Model;

import com.google.gson.Gson;

public class PlaylistLibrary_User {

    private int PlaylistID;
    private int UserID;
    public PlaylistLibrary_User() {
    }

    public PlaylistLibrary_User(int playlistID, int userID) {
        PlaylistID = playlistID;
        UserID = userID;
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
