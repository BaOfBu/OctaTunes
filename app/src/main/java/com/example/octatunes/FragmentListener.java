package com.example.octatunes;

public interface FragmentListener {
    void onSignalReceived(int trackID, int playlistID, int albumID, String from, String belong);
}
