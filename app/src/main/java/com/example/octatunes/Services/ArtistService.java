package com.example.octatunes.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.TracksModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class ArtistService {

    private static final String TAG = "ArtistService";

    private DatabaseReference albumsRef;
    private DatabaseReference tracksRef;

    private DatabaseReference artistsRef;

    public ArtistService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        artistsRef = database.getReference("artists");
        albumsRef = database.getReference("albums");
        tracksRef = database.getReference("tracks");
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
    public void getTracksByArtistId(int artistId, OnSuccessListener<List<TracksModel>> successListener, OnFailureListener failureListener) {
        List<TracksModel> tracksList = new ArrayList<>();
        Query query = albumsRef.orderByChild("artistID").equalTo(artistId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot albumSnapshot : dataSnapshot.getChildren()) {
                        int albumId = albumSnapshot.child("albumID").getValue(Integer.class);
                        Query trackQuery = tracksRef.orderByChild("alubumID").equalTo(albumId);
                        trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot trackSnapshot : dataSnapshot.getChildren()) {
                                        TracksModel track = trackSnapshot.getValue(TracksModel.class);
                                        if (track != null) {
                                            tracksList.add(track);
                                        }
                                    }
                                    successListener.onSuccess(tracksList);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                failureListener.onFailure(databaseError.toException());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                failureListener.onFailure(databaseError.toException());
            }
        });
    }

    public void getRandomTracksByArtistId(int artistId, OnSuccessListener<List<TracksModel>> successListener, OnFailureListener failureListener) {
        List<TracksModel> tracksList = new ArrayList<>();
        Query query = albumsRef.orderByChild("artistID").equalTo(artistId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot albumSnapshot : dataSnapshot.getChildren()) {
                        int albumId = albumSnapshot.child("albumID").getValue(Integer.class);
                        Query trackQuery = tracksRef.orderByChild("alubumID").equalTo(albumId);
                        trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot trackSnapshot : dataSnapshot.getChildren()) {
                                        TracksModel track = trackSnapshot.getValue(TracksModel.class);
                                        if (track != null) {
                                            tracksList.add(track);
                                        }
                                    }

                                    // Shuffle the list of tracks
                                    Collections.shuffle(tracksList);

                                    // Select the first 5 tracks
                                    List<TracksModel> randomTracks = tracksList.subList(0, Math.min(tracksList.size(), 5));

                                    successListener.onSuccess(randomTracks);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                failureListener.onFailure(databaseError.toException());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                failureListener.onFailure(databaseError.toException());
            }
        });
    }

    public void getRandomAlbumByArtistId(int artistId, OnSuccessListener<List<AlbumsModel>> successListener, OnFailureListener failureListener) {
        List<AlbumsModel> albumsList = new ArrayList<>();
        Query query = albumsRef.orderByChild("artistID").equalTo(artistId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot albumSnapshot : dataSnapshot.getChildren()) {
                        AlbumsModel album = albumSnapshot.getValue(AlbumsModel.class);
                        if (album != null) {
                            albumsList.add(album);
                        }
                    }

                    // Sort albums by release date
                    Collections.sort(albumsList, new Comparator<AlbumsModel>() {
                        @Override
                        public int compare(AlbumsModel album1, AlbumsModel album2) {
                            // Compare release dates in reverse order (newest to oldest)
                            return album2.getReleaseDate().compareTo(album1.getReleaseDate());
                        }
                    });

                    // Select the first 5 albums or fewer if the list is shorter
                    List<AlbumsModel> firstFiveAlbums = albumsList.subList(0, Math.min(albumsList.size(), 5));

                    successListener.onSuccess(firstFiveAlbums);
                } else {
                    successListener.onSuccess(albumsList); // Return empty list if no albums found
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                failureListener.onFailure(databaseError.toException());
            }
        });
    }


}
