package com.example.octatunes.Model;

public class PlaylistModel {
    private String title;
    private String description;
    private int coverImageResId; // Resource ID for the cover image, you can replace it with a URL if using online images

    public PlaylistModel(String title, String description, int coverImageResId) {
        this.title = title;
        this.description = description;
        this.coverImageResId = coverImageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCoverImageResId() {
        return coverImageResId;
    }

    public void setCoverImageResId(int coverImageResId) {
        this.coverImageResId = coverImageResId;
    }
}
