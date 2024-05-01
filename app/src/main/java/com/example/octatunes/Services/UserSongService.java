package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.UserSongModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserSongService {
    private DatabaseReference userSongRef;
    private TrackService trackService = new TrackService();
    public UserSongService() {
        // Initialize Firebase database reference
        userSongRef = FirebaseDatabase.getInstance().getReference("userSongs");
    }
    public void addUserSong(final UserSongModel userSong) {
        // Fetch the current maximum AlbumID from Firebase
        userSongRef.orderByChild("UserID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserSongModel last = dataSnapshot.getValue(UserSongModel.class);
                if(last != null && (last.getSongID() != userSong.getSongID())){
                    userSongRef.push().setValue(userSong)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "UserID: " + userSong.getUserID()+ " and SongID: " + userSong.getSongID());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("TAG", "Error adding album", e);
                                }
                            });
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public CompletableFuture<List<UserSongModel>> getAllUserSongs(){
        CompletableFuture<List<UserSongModel>> future = new CompletableFuture<>();
        userSongRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserSongModel> userSongModels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserSongModel userSong = snapshot.getValue(UserSongModel.class);
                    if (userSong != null) {
                        userSongModels.add(userSong);
                    }
                }
                future.complete(userSongModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    public CompletableFuture<List<UserSongModel>> getHistory(Integer userID){
        CompletableFuture<List<UserSongModel>> future = new CompletableFuture<>();
        getAllUserSongs().thenAccept(albums -> {
            List<UserSongModel> foundUserSongs = new ArrayList<>();
            for (UserSongModel userSong : albums) {
                if (userSong.getUserID() == userID && !containsSong(foundUserSongs, userSong)) {
                    foundUserSongs.add(userSong);
                }
            }
            // Sort by newest date
            foundUserSongs.sort((o1, o2) -> o2.getPlayDate().compareTo(o1.getPlayDate()));
            future.complete(foundUserSongs.subList(0, Math.min(20, foundUserSongs.size())));
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });
        return future;
    }

    private boolean containsSong(List<UserSongModel> userSongs, UserSongModel userSong) {
        for (UserSongModel song : userSongs) {
            if (song.getSongID() == userSong.getSongID()) {
                return true;
            }
        }
        return false;
//        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("songs");
//        final boolean[] exists = {false};
//        songRef.child(String.valueOf(userSong.getSongID())).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot1) {
//                for (UserSongModel song : userSongs) {
//                    songRef.child(String.valueOf(song.getSongID())).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
//                            if (snapshot2.exists()) {
//                                SongModel songModel = snapshot2.getValue(SongModel.class);
//                                SongModel userSongModel = snapshot1.getValue(SongModel.class);
//                                if (songModel != null && userSongModel != null) {
//                                    if (songModel.getTitle() == userSongModel.getTitle()) {
//                                        exists[0] = true;
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.e("UserSongService", "Error checking if song exists", error.toException());
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("UserSongService", "Error checking if song exists", error.toException());
//            }
//        });
//
//        return exists[0];
    }

    public void addUserSongTBIN(UserSongModel userSong) {
        // Set play date to current date if not provided
        if (userSong.getPlayDate() == null) {
            userSong.setPlayDate(new Date());
        }

        String key = userSongRef.push().getKey();
        userSongRef.child(key).setValue(userSong)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("UserSongService", "User song added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UserSongService", "Error adding user song", e);
                    }
                });
    }

    public void getAllSongIdsForUser(SongIdListCallback songIdListCallback) {
        UserService userService = new UserService();
        userService.getCurrentUserId(new UserService.UserIdCallback() {
            @Override
            public void onUserIdRetrieved(int userID) {
                Query query = userSongRef.orderByChild("userID").equalTo(userID);

                // Add a listener to retrieve the data
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Integer> songIds = new ArrayList<>();
                        for (DataSnapshot userSongSnapshot : dataSnapshot.getChildren()) {
                            int songID = userSongSnapshot.child("songID").getValue(Integer.class);
                            songIds.add(songID);
                        }
                        songIdListCallback.onSongIdsRetrieved(songIds);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                        System.out.println("Error: " + databaseError.getMessage());
                        songIdListCallback.onSongIdsRetrieved(null);
                    }
                });
            }
        });
    }
    public interface SongIdListCallback {
        void onSongIdsRetrieved(List<Integer> songIds);
    }

}
