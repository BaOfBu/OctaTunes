package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.HistoryModel;
import com.example.octatunes.Model.HistoryModel;
import com.example.octatunes.Model.UserSongModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HistoryService {
    private DatabaseReference historyRef;

    public HistoryService() {
        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        historyRef = database.getReference("history");
    }
    
    public void addHistory(HistoryModel historyModel) {
        historyRef.push().setValue(historyModel);
//        historyRef.orderByChild("UserID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                HistoryModel last = dataSnapshot.getValue(HistoryModel.class);
//                if(last != null && (last.getTrackID() != historyModel.getTrackID())){
//                    historyRef.push().setValue(historyModel)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d("TAG", "UserID: " + historyModel.getUserID()+ " and TrackID: " + historyModel.getTrackID());
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.e("TAG", "Error adding album", e);
//                                }
//                            });
//                }
//
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    public CompletableFuture<List<HistoryModel>> getAllUserSongs(){
        CompletableFuture<List<HistoryModel>> future = new CompletableFuture<>();
        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<HistoryModel> historyModels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HistoryModel userSong = snapshot.getValue(HistoryModel.class);
                    if (userSong != null) {
                        historyModels.add(userSong);
                    }
                }
                future.complete(historyModels);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    public CompletableFuture<List<HistoryModel>> getHistory(Integer userID){
        CompletableFuture<List<HistoryModel>> future = new CompletableFuture<>();
        getAllUserSongs().thenAccept(albums -> {
            List<HistoryModel> foundHistory = new ArrayList<>();
            for (HistoryModel history : albums) {
                if (history.getUserID() == userID) {
                    foundHistory.add(history);
                }
            }
            future.complete(foundHistory);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });
        return future;
    }
}
