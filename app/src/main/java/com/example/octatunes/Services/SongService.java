package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SongService {
    private DatabaseReference songRef;
    public SongService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        songRef = database.getReference("songs");
    }
    public void addSong(final SongModel song) {
        songRef.orderByChild("SongID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maxSongID = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SongModel lastTrack = snapshot.getValue(SongModel.class);
                        maxSongID = lastTrack.getSongID();
                    }
                }
                // Increment the SongID for the new track
                int nextTrackID = maxSongID + 1;

                // Set the SongID for the song and push it to Firebase
                song.setSongID(nextTrackID);
                String key = songRef.push().getKey();
                songRef.child(key).setValue(song)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "Song added successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error adding track", e);
                            }
                        });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public interface OnSongLoadedListener {
        void onSongLoaded();
    }
    public void countSongsWithTitle(final String trackName, final OnSongCountListener listener) {
        songRef.orderByChild("title").equalTo(trackName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                listener.onSongCountRetrieved(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("SongService", "Failed to count songs with title: " + trackName, databaseError.toException());
                listener.onSongCountFailed(databaseError.getMessage());
            }
        });
    }
    public interface OnSongCountListener {
        void onSongCountRetrieved(int count);
        void onSongCountFailed(String errorMessage);
    }
}
