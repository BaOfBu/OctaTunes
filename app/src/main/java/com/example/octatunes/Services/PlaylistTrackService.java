package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.Playlist_TracksModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlaylistTrackService {
    private DatabaseReference playlistTracksRef;

    public PlaylistTrackService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        playlistTracksRef = database.getReference("playlist_track");
    }

    // Method to add a new playlist track entry
    public void addPlaylistTrack(Playlist_TracksModel playlistTrack) {
        // Generate a key for the playlist track entry from Firebase
        String key = playlistTracksRef.push().getKey();
        playlistTracksRef.child(key).setValue(playlistTrack)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Playlist track added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Error adding playlist track", e);
                    }
                });
    }

}
