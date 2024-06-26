package com.example.octatunes;

public class TrackPreviewModel {
    private String trackImageId;
    private String trackName;
    private String type;
    private String trackArtist;
    private int publishedYear;
    public String getTrackImageId() { return trackImageId; }
    public void setTrackImageId(String trackImageId) { this.trackImageId = trackImageId; }
    public String getTrackName() {
        return trackName;
    }
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTrackArtist() {
        return trackArtist;
    }
    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }
    public int getPublishedYear(){
        return publishedYear;
    }
    public void setPublishedYear(int publishedYear){
        this.publishedYear = publishedYear;
    }
    public TrackPreviewModel(String trackImageId, String trackName, String type, String trackArtist, int publishedYear) {
        this.trackImageId = trackImageId;
        this.trackName = trackName;
        this.type = type;
        this.trackArtist = trackArtist;
        this.publishedYear = publishedYear;
    }
}
