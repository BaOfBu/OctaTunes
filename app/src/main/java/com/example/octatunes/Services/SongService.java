package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.SongModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
            }
        });
    }

    public void countSongWithArtistName(final String artistName, final OnSongCountListener listener) {
        // Query songs with artistName and playDate within the current month
        Query query = songRef.orderByChild("artist").equalTo(artistName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                listener.onSongCountRetrieved(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SongService", "Failed to count songs with artist name: " + artistName, databaseError.toException());
                listener.onSongCountFailed(databaseError.getMessage());
            }
        });
    }

    public interface OnSongCountListener {
        void onSongCountRetrieved(int count);
        void onSongCountFailed(String errorMessage);
    }
    public void getTopTitlesForSongIds(List<Integer> songIds, TitleListCallback titleListCallback) {
        Map<String, Integer> titleCounts = new HashMap<>();
        AtomicInteger processedCount = new AtomicInteger(0);

        // Loop through each song ID
        for (Integer songId : songIds) {
            songRef.orderByChild("songID").equalTo(songId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Process each song ID's data
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String title = snapshot.child("title").getValue(String.class);
                        if (title != null) {
                            // Increment the count for the title
                            titleCounts.put(title, titleCounts.getOrDefault(title, 0) + 1);
                        }
                    }
                    // Increment the processed count
                    int count = processedCount.incrementAndGet();

                    // After processing all song IDs, retrieve the top titles and pass them to the callback
                    if (count == songIds.size()) {
                        titleListCallback.onTitlesRetrieved(getTopTitles(titleCounts));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Error: " + databaseError.getMessage());
                    titleListCallback.onTitlesRetrieved(null);
                }
            });
        }
    }


    private List<String> getTopTitles(Map<String, Integer> titleCounts) {
        // Sort the titles by their counts in descending order
        List<Map.Entry<String, Integer>> sortedTitles = new ArrayList<>(titleCounts.entrySet());
        sortedTitles.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Get the top 76 titles
        List<String> topTitles = new ArrayList<>();
        for (int i = 0; i < Math.min(7, sortedTitles.size()); i++) {
            topTitles.add(sortedTitles.get(i).getKey());
        }
        return topTitles;
    }







    public interface TitleListCallback {
        void onTitlesRetrieved(List<String> titles);
    }
}
