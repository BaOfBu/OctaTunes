package com.example.octatunes.Model;

public class TracksModel {
    private int TrackID;
    private int AlubumID;
    private String Name;
    private int Duration;
    private String File;

    public TracksModel(int trackID, int alubumID, String name, int duration, String file) {
        TrackID = trackID;
        AlubumID = alubumID;
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

    public int getAlubumID() {
        return AlubumID;
    }

    public void setAlubumID(int alubumID) {
        AlubumID = alubumID;
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
}
