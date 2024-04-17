package com.example.octatunes.Model;

import java.time.LocalDateTime;

public class LikesModel {
    private int UserID;
    private int TrackID;
    private LocalDateTime LikeDateTime;

    public LikesModel(int userID, int trackID, LocalDateTime likeDateTime) {
        UserID = userID;
        TrackID = trackID;
        LikeDateTime = likeDateTime;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getTrackID() {
        return TrackID;
    }

    public void setTrackID(int trackID) {
        TrackID = trackID;
    }

    public LocalDateTime getLikeDateTime() {
        return LikeDateTime;
    }

    public void setLikeDateTime(LocalDateTime likeDateTime) {
        LikeDateTime = likeDateTime;
    }
}
