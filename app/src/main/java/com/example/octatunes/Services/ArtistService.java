package com.example.octatunes.Services;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.text.Normalizer;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Utils.StringUtil;
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
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ArtistService {

    private static final String TAG = "ArtistService";

    private DatabaseReference albumsRef;
    private DatabaseReference tracksRef;

    private DatabaseReference artistsRef;

    private DatabaseReference playlistRef;

    private DatabaseReference playlistTrackRef;

    public ArtistService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        artistsRef = database.getReference("artists");
        albumsRef = database.getReference("albums");
        tracksRef = database.getReference("tracks");
        playlistRef=database.getReference("playlists");
        playlistTrackRef=database.getReference("playlist_track");
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
        String finalArtistName = StringUtil.removeAccents(query);
        Log.d(TAG, "Searching for artist: " + finalArtistName);
        getAllArtists(
                new OnSuccessListener<List<ArtistsModel>>() {
                    @Override
                    public void onSuccess(List<ArtistsModel> allArtists) {
                        List<ArtistsModel> foundArtists = new ArrayList<>();
                        for (ArtistsModel artist : allArtists) {
                            if (StringUtil.removeAccents(artist.getName()).toLowerCase().contains(finalArtistName.toLowerCase())) {
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
    //public void getRandomTracksByArtistId(int artistId, OnSuccessListener<List<TracksModel>> successListener, OnFailureListener failureListener) {
    //    List<TracksModel> tracksList = new ArrayList<>();
    //    Query query = albumsRef.orderByChild("artistID").equalTo(artistId);
    //    query.addListenerForSingleValueEvent(new ValueEventListener() {
    //        @Override
    //        public void onDataChange(DataSnapshot dataSnapshot) {
    //            if (dataSnapshot.exists()) {
    //                for (DataSnapshot albumSnapshot : dataSnapshot.getChildren()) {
    //                    int albumId = albumSnapshot.child("albumID").getValue(Integer.class);
    //                    Query trackQuery = tracksRef.orderByChild("alubumID").equalTo(albumId);
    //                    trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
    //                        @Override
    //                        public void onDataChange(DataSnapshot dataSnapshot) {
    //                            if (dataSnapshot.exists()) {
    //                                for (DataSnapshot trackSnapshot : dataSnapshot.getChildren()) {
    //                                    TracksModel track = trackSnapshot.getValue(TracksModel.class);
    //                                    if (track != null) {
    //                                        tracksList.add(track);
    //                                    }
    //                                }
    //
    //                                // Shuffle the list of tracks
    //                                Collections.shuffle(tracksList);
    //
    //                                // Select the first 5 tracks
    //                                List<TracksModel> randomTracks = tracksList.subList(0, Math.min(tracksList.size(), 5));
    //
    //                                successListener.onSuccess(randomTracks);
    //                            }
    //                            else {
    //                                successListener.onSuccess(tracksList); // Return empty list if no albums found
    //                            }
    //                        }
    //
    //                        @Override
    //                        public void onCancelled(DatabaseError databaseError) {
    //                            failureListener.onFailure(databaseError.toException());
    //                        }
    //                    });
    //                }
    //            }
    //            else {
    //                successListener.onSuccess(tracksList); // Return empty list if no albums found
    //            }
    //        }
    //
    //        @Override
    //        public void onCancelled(DatabaseError databaseError) {
    //            failureListener.onFailure(databaseError.toException());
    //        }
    //    });
    //}


    public CompletableFuture<List<TracksModel>> getRandomTracksByArtistId(int artistId) {
        CompletableFuture<List<TracksModel>> future = new CompletableFuture<>();
        List<TracksModel> tracksList = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);

        Query query = albumsRef.orderByChild("artistID").equalTo(artistId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int albumCount = (int) dataSnapshot.getChildrenCount();
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
                                }

                                // Increment the count of completed asynchronous tasks
                                int completedTasks = count.incrementAndGet();

                                // If all albums have been processed, resolve the CompletableFuture
                                if (completedTasks == albumCount) {
                                    // Shuffle the list of tracks
                                    Collections.shuffle(tracksList);

                                    // Select the first 5 tracks
                                    List<TracksModel> randomTracks = tracksList.subList(0, Math.min(tracksList.size(), 5));

                                    future.complete(randomTracks);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                future.completeExceptionally(databaseError.toException());
                            }
                        });
                    }
                } else {
                    // If no albums found, resolve the CompletableFuture with an empty list
                    future.complete(tracksList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
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


    public void getPlaylistsByArtistId(int artistId, OnSuccessListener<List<PlaylistsModel>> successListener, OnFailureListener failureListener) {
        Set<PlaylistsModel> playlistSet = new HashSet<>();
        Set<Integer> playlistIdSet = new HashSet<>();

        // Initialize latch with the initial count
        int initialCount = 1; // We'll increment it before each asynchronous call

        // Step 1: Find albums associated with the artistId
        Query albumQuery = albumsRef.orderByChild("artistID").equalTo(artistId);
        albumQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot albumSnapshot) {
                if (!albumSnapshot.exists()) {
                    successListener.onSuccess(new ArrayList<>(playlistSet));
                    return;
                }

                // Create a latch for each album
                CountDownLatch albumLatch = new CountDownLatch((int) albumSnapshot.getChildrenCount());

                for (DataSnapshot albumDataSnapshot : albumSnapshot.getChildren()) {
                    int albumId = albumDataSnapshot.child("albumID").getValue(Integer.class);
                    Query trackQuery = tracksRef.orderByChild("alubumID").equalTo(albumId);
                    albumLatch.countDown(); // Decrement the latch count for each album
                    trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot trackSnapshot) {
                            if (!trackSnapshot.exists()) {
                                albumLatch.countDown(); // Decrement the latch count if no tracks exist
                                return;
                            }

                            // Create a latch for each track
                            CountDownLatch trackLatch = new CountDownLatch((int) trackSnapshot.getChildrenCount());

                            for (DataSnapshot trackDataSnapshot : trackSnapshot.getChildren()) {
                                int trackId = trackDataSnapshot.child("trackID").getValue(Integer.class);
                                Query playlistTrackQuery = playlistTrackRef.orderByChild("trackID").equalTo(trackId);
                                trackLatch.countDown(); // Decrement the latch count for each track
                                playlistTrackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot playlistTrackSnapshot) {
                                        if (!playlistTrackSnapshot.exists()) {
                                            trackLatch.countDown(); // Decrement the latch count if no playlist tracks exist
                                            return;
                                        }

                                        // Create a latch for each playlist track
                                        CountDownLatch playlistLatch = new CountDownLatch((int) playlistTrackSnapshot.getChildrenCount());

                                        for (DataSnapshot playlistTrackDataSnapshot : playlistTrackSnapshot.getChildren()) {
                                            int playlistId = playlistTrackDataSnapshot.child("playlistID").getValue(Integer.class);
                                            Query playlistQuery = playlistRef.orderByChild("playlistID").equalTo(playlistId);
                                            playlistLatch.countDown(); // Decrement the latch count for each playlist track
                                            playlistQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot playlistSnapshot) {
                                                    playlistLatch.countDown(); // Decrement the latch count after playlist data retrieval
                                                    if (playlistSnapshot.exists()) {
                                                        for (DataSnapshot playlistDataSnapshot : playlistSnapshot.getChildren()) {
                                                            int playlistId = playlistDataSnapshot.child("playlistID").getValue(Integer.class);
                                                            String playlistName = playlistDataSnapshot.child("name").getValue(String.class);
                                                            if (!Objects.equals(playlistName, "Liked songs") && !Objects.equals(playlistName, "Made For You") ){
                                                                if (!playlistIdSet.contains(playlistId)) {
                                                                    playlistIdSet.add(playlistId);
                                                                    PlaylistsModel playlist = playlistDataSnapshot.getValue(PlaylistsModel.class);
                                                                    if (playlist != null) {
                                                                        playlistSet.add(playlist);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    checkAllDataRetrieved(albumLatch, successListener, playlistSet);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    failureListener.onFailure(databaseError.toException());
                                                    playlistLatch.countDown(); // Decrement the latch count if onCancelled is triggered
                                                }
                                            });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        failureListener.onFailure(databaseError.toException());
                                        trackLatch.countDown(); // Decrement the latch count if onCancelled is triggered
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            failureListener.onFailure(databaseError.toException());
                            albumLatch.countDown(); // Decrement the latch count if onCancelled is triggered
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                failureListener.onFailure(databaseError.toException());
            }
        });
    }

    private void checkAllDataRetrieved(CountDownLatch latch, OnSuccessListener<List<PlaylistsModel>> successListener, Set<PlaylistsModel> playlistSet) {
        try {
            latch.await(); // Wait for all asynchronous tasks to complete
            successListener.onSuccess(new ArrayList<>(playlistSet)); // Once all tasks complete, call successListener
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkAllDataRetrieved(OnSuccessListener<List<PlaylistsModel>> successListener, List<PlaylistsModel> playlistList) {
        successListener.onSuccess(playlistList);
    }


}