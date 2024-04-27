package com.example.octatunes.Model;

public class SongManagerModel {
    private int trackID;
    private String trackName;
    private int albumID;
    private String artistName;
    private String file;
    private String image;



    public int getTrackID() {
        return trackID;
    }

    public void setTrackID(int TrackID) {
        trackID = TrackID;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String TrackName) {
        trackName = TrackName;
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int AlbumID) {
        albumID = AlbumID;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String ArtistName) {
        artistName = ArtistName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String File) {
        file = File;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String Image) {
        image = Image;
    }
}
