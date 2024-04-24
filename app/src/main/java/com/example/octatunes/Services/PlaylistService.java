package com.example.octatunes.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.UsersModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlaylistService {

    private static final String TAG = "PlaylistService";

    private DatabaseReference playlistsRef;

    private DatabaseReference usersRef;

    public PlaylistService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        playlistsRef = database.getReference("playlists");
        usersRef = database.getReference("users");
    }
    public void addPlaylists(List<PlaylistsModel> playlists) {
        playlistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maxPlaylistID = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlaylistsModel model = snapshot.getValue(PlaylistsModel.class);
                    if (model != null && model.getPlaylistID() > maxPlaylistID) {
                        maxPlaylistID = model.getPlaylistID();
                    }
                }
                int nextPlaylistID = maxPlaylistID + 1;
                for (PlaylistsModel playlist : playlists) {
                    String key = playlistsRef.push().getKey();
                    if (key != null) {
                        playlist.setPlaylistID(nextPlaylistID);
                        playlistsRef.child(key).setValue(playlist)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Playlist added with ID: " + key);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error adding playlist", e);
                                });
                        nextPlaylistID++; // Increment for the next playlist
                    } else {
                        Log.e(TAG, "Failed to get key for playlist");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }
    public CompletableFuture<List<PlaylistsModel>> getAllPlaylists() {
        CompletableFuture<List<PlaylistsModel>> future = new CompletableFuture<>();
        playlistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<PlaylistsModel> playlists = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlaylistsModel playlist = snapshot.getValue(PlaylistsModel.class);
                    if (playlist != null) {
                        playlists.add(playlist);
                    }
                }
                future.complete(playlists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }
    public CompletableFuture<List<PlaylistsModel>> getRandomPlaylists(int count) {
        CompletableFuture<List<PlaylistsModel>> future = new CompletableFuture<>();
        getAllPlaylists().thenAccept(playlists -> {
            if (playlists.size() <= count) {
                future.complete(playlists);
            } else {
                List<PlaylistsModel> randomPlaylists = new ArrayList<>();
                List<Integer> indexes = new ArrayList<>();
                for (int i = 0; i < playlists.size(); i++) {
                    indexes.add(i);
                }
                Collections.shuffle(indexes);
                for (int i = 0; i < count; i++) {
                    randomPlaylists.add(playlists.get(indexes.get(i)));
                }
                future.complete(randomPlaylists);
            }
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });
        return future;
    }

    public CompletableFuture<List<PlaylistsModel>> getPlaylistByName(String playlistName){
        CompletableFuture<List<PlaylistsModel>> future = new CompletableFuture<>();
        getAllPlaylists().thenAccept(playlists -> {
            List<PlaylistsModel> playlistsByName = new ArrayList<>();
            for (PlaylistsModel playlist : playlists) {
                if (playlist.getName().toLowerCase().contains(playlistName.toLowerCase())) {
                    playlistsByName.add(playlist);
                }
            }
            future.complete(playlistsByName);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });
        return future;
    }
    // Method to get usernames of playlist creators
    public CompletableFuture<String> getUsernameOfPlaylistCreator(PlaylistsModel playlist) {
        CompletableFuture<String> future = new CompletableFuture<>();
        int userId = playlist.getUserID();

        usersRef.orderByChild("userID").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UsersModel userModel = snapshot.getValue(UsersModel.class);
                        if (userModel != null) {
                            String username = userModel.getName();
                            future.complete(username);
                            return;
                        }
                    }
                    future.completeExceptionally(new Exception("Username not found for userID: " + userId));
                } else {
                    future.completeExceptionally(new Exception("User with userID " + userId + " not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException()); // Complete the future exceptionally in case of error
            }
        });

        return future;
    }

}
