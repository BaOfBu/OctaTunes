package com.example.octatunes.Model;

import java.util.Date;

public class HistoryModel {
    private int UserID;
    private int TrackID;
    private Date HistoryDateTime;

    public HistoryModel() {
    }

    public HistoryModel(int userID, int trackID, Date historyDateTime) {
        UserID = userID;
        TrackID = trackID;
        HistoryDateTime = historyDateTime;
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

    public Date getHistoryDateTime() {
        return HistoryDateTime;
    }

    public void setHistoryDateTime(Date historyDateTime) {
        HistoryDateTime = historyDateTime;
    }

    @Override
    public String toString() {
        return "HistoryModel{" +
                "UserID=" + UserID +
                ", TrackID=" + TrackID +
                ", HistoryDateTime='" + HistoryDateTime + '\'' +
                '}';
    }
}
