package com.example.octatunes.Services;

import android.util.Log;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.LyricModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class LyricService {
    private DatabaseReference lyricRef;
    public LyricService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        lyricRef = database.getReference("lyrics");
    }

    public void saveLyric(LyricModel lyricModel) {
        // Save lyric to database
        lyricRef.push().setValue(lyricModel);
    }

    public CompletableFuture<LyricModel> getLyricFile(String titleQuery) {
        // Get lyric file from database
        CompletableFuture<LyricModel> future = new CompletableFuture<>();
        Query query = lyricRef.orderByChild("title").equalTo(titleQuery);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LyricModel lyricModel = snapshot.getValue(LyricModel.class);
                    //Log.d("LyricService", "Tìm được url tải: " + lyricModel.getLyric());
                    future.complete(lyricModel);
                    return;
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }
}
