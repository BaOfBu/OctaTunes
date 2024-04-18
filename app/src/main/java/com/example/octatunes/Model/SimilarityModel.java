package com.example.octatunes.Model;

import com.google.gson.Gson;

public class SimilarityModel {
    private int UserID;
    private int SongID;
    private float SimilarityScore;

    public SimilarityModel() {

    }

    public SimilarityModel(int userID, int songID, float similarityScore) {
        UserID = userID;
        SongID = songID;
        SimilarityScore = similarityScore;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getSongID() {
        return SongID;
    }

    public void setSongID(int songID) {
        SongID = songID;
    }

    public float getSimilarityScore() {
        return SimilarityScore;
    }

    public void setSimilarityScore(float similarityScore) {
        SimilarityScore = similarityScore;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);

    }
}
