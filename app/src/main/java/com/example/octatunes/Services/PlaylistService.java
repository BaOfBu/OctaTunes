package com.example.octatunes.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.UsersModel;
import com.example.octatunes.Utils.StringUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
                int count_current = 0;
                int index = 0;
                while(count_current<count && index<indexes.size()){
                    if (playlists.get(indexes.get(index)).getUserID()==1){
                        randomPlaylists.add(playlists.get(indexes.get(index)));
                        index++;
                        count_current++;
                    }
                   else{
                        index++;
                    }
                }
                //for (int i = 0; i < count; i++) {
                //    randomPlaylists.add(playlists.get(indexes.get(i)));
                //}
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
                if (StringUtil.removeAccents(playlist.getName()).toLowerCase().contains(StringUtil.removeAccents(playlistName).toLowerCase())) {
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
    public void getPlaylistModelLiked(int userId, PlaylistCallback callback) {
        String playlistName = "Liked songs";
        playlistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot playlistSnapshot : dataSnapshot.getChildren()) {
                    PlaylistsModel playlistModel = playlistSnapshot.getValue(PlaylistsModel.class);
                    if (playlistModel != null && playlistModel.getUserID() == userId && playlistModel.getName().equals(playlistName)) {
                        // Found the matching playlist
                        callback.onPlaylistRetrieved(playlistModel);
                        return;
                    }
                }
                callback.onPlaylistRetrieved(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                callback.onError(databaseError.getMessage());
            }
        });
    }

    // Define PlaylistCallback interface
    public interface PlaylistCallback {
        void onPlaylistRetrieved(PlaylistsModel playlistModel);
        void onError(String errorMessage);
    }

    // Method to add a new playlist
    public void addPlaylists(int userId, String name) {
        // Get the current maximum playlistId from the database
        playlistsRef.orderByChild("playlistId").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maxPlaylistId = 0; // Default if no playlist exists yet

                // Check if there are playlists in the database
                if (dataSnapshot.exists()) {
                    // Get the maximum playlistId
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PlaylistsModel playlist = snapshot.getValue(PlaylistsModel.class);
                        maxPlaylistId = playlist.getPlaylistID();
                    }
                }

                // Increment the maxPlaylistId to generate a new playlistId
                int newPlaylistId = maxPlaylistId + 1;

                // Generate a new unique random key for the node
                String playlistNodeId = playlistsRef.push().getKey();

                // Create a new PlaylistModel object with the provided playlistId, userId, and name
                PlaylistsModel newPlaylist = new PlaylistsModel(newPlaylistId, userId, name,"","");

                // Set the new playlist under the generated key directly under the 'playlists' node
                playlistsRef.child(playlistNodeId).setValue(newPlaylist)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Playlist added successfully
                                Log.d(TAG, "Playlist added successfully.");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to add playlist
                                Log.e(TAG, "Failed to add playlist.", e);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    public void addPlaylistsLiked(int userId, PlaylistAddedCallback callback) {
        String name = "Liked songs";

        // Get the current maximum playlistId from the database
        playlistsRef.orderByChild("playlistId").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maxPlaylistId = 0; // Default if no playlist exists yet

                // Check if there are playlists in the database
                if (dataSnapshot.exists()) {
                    // Get the maximum playlistId
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PlaylistsModel playlist = snapshot.getValue(PlaylistsModel.class);
                        maxPlaylistId = playlist.getPlaylistID();
                    }
                }

                // Increment the maxPlaylistId to generate a new playlistId
                int newPlaylistId = maxPlaylistId + 1;

                // Generate a new unique random key for the node
                String playlistNodeId = playlistsRef.push().getKey();

                // Create a new PlaylistModel object with the provided playlistId, userId, and name
                PlaylistsModel newPlaylist = new PlaylistsModel(newPlaylistId, userId, name,"","");

                // Set the new playlist under the generated key directly under the 'playlists' node
                playlistsRef.child(playlistNodeId).setValue(newPlaylist)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Playlist added successfully
                                Log.d(TAG, "Playlist added successfully.");
                                if(callback != null) {
                                    callback.onPlaylistAdded(newPlaylistId);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to add playlist
                                Log.e(TAG, "Failed to add playlist.", e);
                                if(callback != null) {
                                    callback.onError(e.getMessage());
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                if(callback != null) {
                    callback.onError(databaseError.getMessage());
                }
            }
        });
    }

    // Callback interface for notifying when playlist is added or an error occurs
    public interface PlaylistAddedCallback {
        void onPlaylistAdded(int newPlaylistId);
        void onError(String errorMessage);
    }


}
