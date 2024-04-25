package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Model.UserSongModel;
import com.example.octatunes.TrackPreviewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserSongService {
    private DatabaseReference userSongRef;
    private TrackService trackService = new TrackService();
    public UserSongService() {
        // Initialize Firebase database reference
        userSongRef = FirebaseDatabase.getInstance().getReference("userSongs");
    }
    public void addAlbum(final UserSongModel userSong) {
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
                if (userSong.getUserID() == userID) {
                    foundUserSongs.add(userSong);
                }
            }
            future.complete(foundUserSongs);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });
        return future;
    }

}
