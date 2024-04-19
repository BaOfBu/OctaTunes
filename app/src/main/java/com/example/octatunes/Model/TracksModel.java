package com.example.octatunes.Model;

import com.google.gson.Gson;

public class TracksModel {
    private int TrackID;
    private int AlbumID;
    private String Name;
    private int Duration;
    private String File;

    public TracksModel() {

    }

    public TracksModel(int trackID, int albumID, String name, int duration, String file) {
        TrackID = trackID;
        AlbumID = albumID;
        Name = name;
        Duration = duration;
        File = file;
    }

    public int getTrackID() {
        return TrackID;
    }

    public void setTrackID(int trackID) {
        TrackID = trackID;
    }

    public int getAlbumID() {
        return AlbumID;
    }

    public void setAlubumID(int albumID) {
        AlbumID = albumID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public String getFile() {
        return File;
    }

    public void setFile(String file) {
        File = file;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);

    }
}
