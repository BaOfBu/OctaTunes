package com.example.octatunes;

import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;

import java.util.List;

public interface FragmentListener {
    void onSignalReceived(int trackID, int playlistID, int albumID, String from, String belong, String mode);
    void onSignalReceived2(List<TracksModel> tracksModels, int trackID, String from, String belong, String mode);
    void onSignalReceived3(List<SongModel> songModels, int pos, String from, String belong, String mode);
    void onSignalReceived4();
}
