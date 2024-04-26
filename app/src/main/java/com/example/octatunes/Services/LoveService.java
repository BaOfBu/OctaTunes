package com.example.octatunes.Services;

// LoveService.java

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoveService {
    private DatabaseReference favoritesRef;

    public LoveService() {
        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        favoritesRef = database.getReference("favorites");
    }

    // Check if a song is already loved by the user
    public void isLoved(int userId, int trackId, final LoveCheckCallback callback) {
        DatabaseReference userFavoritesRef = favoritesRef.child(String.valueOf(userId)).child(String.valueOf(trackId));

        userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isLoved = dataSnapshot.exists();
                callback.onLoveChecked(isLoved);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("LoveService", "Error checking if track is loved: " + databaseError.getMessage());
                callback.onLoveChecked(false); // Default to false in case of error
            }
        });
    }

    // Add a track to the user's list of loved tracks
    public void addLove(int userId, int trackId) {
        DatabaseReference userFavoritesRef = favoritesRef.child(String.valueOf(userId)).child(String.valueOf(trackId));

        userFavoritesRef.setValue(true)
                .addOnSuccessListener(aVoid -> Log.d("LoveService", "Track added to loved list successfully"))
                .addOnFailureListener(e -> Log.e("LoveService", "Error adding track to loved list: " + e.getMessage()));
    }

    // Callback interface for love check operation
    public interface LoveCheckCallback {
        void onLoveChecked(boolean isLoved);
    }
}
