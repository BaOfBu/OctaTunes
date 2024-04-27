package com.example.octatunes;

import com.example.octatunes.Model.TracksModel;

import java.util.List;

public interface FragmentListener {
    void onSignalReceived(List<TracksModel> tracksModels, int trackID, int albumID, String from, String belong, String mode);

    void onSignalReceived(List<TracksModel> tracksModelList, int trackID, String albumID, String from, String belong, String mode);
}
