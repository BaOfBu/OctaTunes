package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.TracksModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class TrackService {
    private DatabaseReference tracksRef;

    public TrackService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tracksRef = database.getReference("tracks");
    }

    // Method to add a new track
    public void addTrack(final TracksModel track) {
        tracksRef.orderByChild("TrackID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maxTrackID = 0;
                if (dataSnapshot.exists()) {
                    // Get the highest TrackID
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TracksModel lastTrack = snapshot.getValue(TracksModel.class);
                        maxTrackID = lastTrack.getTrackID();
                    }
                }
                // Increment the TrackID for the new track
                int nextTrackID = maxTrackID + 1;

                // Set the TrackID for the track and push it to Firebase
                track.setTrackID(nextTrackID);
                String key = tracksRef.push().getKey();
                tracksRef.child(key).setValue(track)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "Track added successfully");
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
}
