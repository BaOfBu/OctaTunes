package com.example.octatunes.Model;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.Date;

public class PlaylistLibrary_User {

    private int PlaylistID;
    private int UserID;
    private Date DateAdded;

    public PlaylistLibrary_User() {
    }

    public PlaylistLibrary_User(int playlistID, int userID, Date dateAdded) {
        PlaylistID = playlistID;
        UserID = userID;
        DateAdded = dateAdded;
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

    public Date getDateAdded() {
        return DateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        DateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
