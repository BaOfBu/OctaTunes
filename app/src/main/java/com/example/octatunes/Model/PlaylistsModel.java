package com.example.octatunes.Model;

public class PlaylistsModel {
    private int PlaylistID;
    private int UserID;
    private String Name;
    private String Image;
    private String Description;

    public PlaylistsModel(int playlistID, int userID, String name, String image, String description) {
        PlaylistID = playlistID;
        UserID = userID;
        Name = name;
        Image = image;
        Description = description;
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

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
