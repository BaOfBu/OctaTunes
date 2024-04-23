package com.example.octatunes.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class ArtistService {

    private static final String TAG = "ArtistService";

    private DatabaseReference artistsRef;

    public ArtistService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        artistsRef = database.getReference("artists");
    }

    public void addArtist(ArtistsModel artist) {
        // Get the current max ArtistID
        artistsRef.orderByChild("artistID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maxArtistID = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArtistsModel model = snapshot.getValue(ArtistsModel.class);
                    if (model != null && model.getArtistID() > maxArtistID) {
                        maxArtistID = model.getArtistID();
                    }
                }

                // Calculate the next ArtistID
                int nextArtistID = maxArtistID + 1;

                // Use push() to generate a unique key
                String key = artistsRef.push().getKey();

                if (key != null) {
                    // Set the generated key as the Firebase key
                    artist.setArtistID(nextArtistID);

                    // Add the artist to Firebase with the generated key
                    artistsRef.child(key).setValue(artist)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Artist added with ID: " + nextArtistID);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error adding artist", e);
                                }
                            });
                } else {
                    Log.e(TAG, "Failed to get key for artist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
    public void getAllArtists(OnSuccessListener<List<ArtistsModel>> successListener, OnFailureListener failureListener) {
        artistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ArtistsModel> artists = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArtistsModel artist = snapshot.getValue(ArtistsModel.class);
                    if (artist != null) {
                        artists.add(artist);
                    }
                }
                successListener.onSuccess(artists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                failureListener.onFailure(databaseError.toException());
            }
        });
    }
    public void getRandomArtists(int count, OnSuccessListener<List<ArtistsModel>> successListener, OnFailureListener failureListener) {
        getAllArtists(
                new OnSuccessListener<List<ArtistsModel>>() {
                    @Override
                    public void onSuccess(List<ArtistsModel> allArtists) {
                        List<ArtistsModel> randomArtists = new ArrayList<>();
                        if (allArtists.size() <= count) {
                            // If there are fewer artists than the requested count, return all artists
                            randomArtists.addAll(allArtists);
                        } else {
                            // Randomly select 'count' number of artists
                            List<Integer> indices = new ArrayList<>();
                            Random random = new Random();
                            while (randomArtists.size() < count) {
                                int index = random.nextInt(allArtists.size());
                                if (!indices.contains(index)) {
                                    indices.add(index);
                                    randomArtists.add(allArtists.get(index));
                                }
                            }
                        }
                        successListener.onSuccess(randomArtists);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        failureListener.onFailure(e);
                    }
                }
        );
    }
    public CompletableFuture<ArtistsModel> findArtistById(int artistId){
        CompletableFuture<ArtistsModel> future = new CompletableFuture<>();
        Query query = artistsRef.orderByChild("artistID").equalTo(artistId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ArtistsModel artistsModel = snapshot.getValue(ArtistsModel.class);
                        future.complete(artistsModel);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    public void findArtistByName(String query, OnSuccessListener<List<ArtistsModel>> successListener, OnFailureListener failureListener) {
        getAllArtists(
                new OnSuccessListener<List<ArtistsModel>>() {
                    @Override
                    public void onSuccess(List<ArtistsModel> allArtists) {
                        List<ArtistsModel> foundArtists = new ArrayList<>();
                        for (ArtistsModel artist : allArtists) {
                            if (artist.getName().toLowerCase().contains(query.toLowerCase())) {
                                foundArtists.add(artist);
                            }
                        }
                        successListener.onSuccess(foundArtists);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        failureListener.onFailure(e);
                    }
                }
        );
    }
}
