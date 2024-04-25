package com.example.octatunes.Model;

import com.google.gson.Gson;

import java.io.Serializable;

public class LyricModel implements Serializable {
    private String lyric;
    private Integer trackId;
    private String title;

    public String getLyric() {
        return lyric;
    }
    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
    public Integer getTrackId() {
        return trackId;
    }
    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
