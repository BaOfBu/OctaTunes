package com.example.octatunes.Model;

import com.google.gson.Gson;

import java.util.Date;

public class UserSongModel {
    private int UserID;
    private int SongID;
    private Date PlayDate;

    public UserSongModel() {

    }

    public UserSongModel(int userID, int songID, Date playDate) {
        UserID = userID;
        SongID = songID;
        PlayDate = playDate;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getSongID() {
        return SongID;
    }

    public void setSongID(int songID) {
        SongID = songID;
    }

    public Date getPlayDate() {
        return PlayDate;
    }

    public void setPlayDate(Date playDate) {
        PlayDate = playDate;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);

    }
}
