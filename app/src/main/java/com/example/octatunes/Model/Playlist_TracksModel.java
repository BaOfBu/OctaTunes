package com.example.octatunes.Model;

public class Playlist_TracksModel {
    private int PlaylistID;
    private int TrackID;
    private int Order;

    public Playlist_TracksModel(int playlistID, int trackID, int order) {
        PlaylistID = playlistID;
        TrackID = trackID;
        Order = order;
    }

    public int getPlaylistID() {
        return PlaylistID;
    }

    public void setPlaylistID(int playlistID) {
        PlaylistID = playlistID;
    }

    public int getTrackID() {
        return TrackID;
    }

    public void setTrackID(int trackID) {
        TrackID = trackID;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }
}
