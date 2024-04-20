package com.example.octatunes.Model;

import com.google.gson.Gson;

import java.util.Date;

public class AlbumLibrary_User {

    private int albumID;
    private int userID;
    private Date dateAdded;

    public AlbumLibrary_User() {
    }

    public AlbumLibrary_User(int albumID, int userID, Date dateAdded) {
        this.albumID = albumID;
        this.userID = userID;
        this.dateAdded = dateAdded;
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
