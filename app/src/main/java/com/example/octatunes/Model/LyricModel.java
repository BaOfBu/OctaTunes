package com.example.octatunes.Model;

import com.google.gson.Gson;

import java.io.Serializable;

public class LyricModel implements Serializable {
    private String lyric;
    private String trackId;
    public String getLyric() {
        return lyric;
    }
    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
    public String getTrackId() {
        return trackId;
    }
    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
