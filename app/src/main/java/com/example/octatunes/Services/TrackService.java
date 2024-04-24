package com.example.octatunes.Services;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.Playlist_TracksModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Model.UserSongModel;
import com.example.octatunes.TrackPreviewAdapter;
import com.example.octatunes.TrackPreviewModel;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TrackService {
    private DatabaseReference playlistTracksRef;
    private DatabaseReference tracksRef;
    private DatabaseReference albumsRef;
    private DatabaseReference artistRef;


    public TrackService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        playlistTracksRef = database.getReference("playlist_track");
        tracksRef = database.getReference("tracks");
        albumsRef = database.getReference("albums");
        artistRef = database.getReference("artists");
    }

    // Method to add a new track
    public void addTrack(final TracksModel track) {
        tracksRef.orderByChild("TrackID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maxTrackID = 0;
                if (dataSnapshot.exists()) {
                    // Get the highest TrackID
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TracksModel lastTrack = snapshot.getValue(TracksModel.class);
                        maxTrackID = lastTrack.getTrackID();
                    }
                }
                // Increment the TrackID for the new track
                int nextTrackID = maxTrackID + 1;

                // Set the TrackID for the track and push it to Firebase
                track.setTrackID(nextTrackID);
                String key = tracksRef.push().getKey();
                tracksRef.child(key).setValue(track)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "Track added successfully");
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
    public void getTracksByPlaylistId(final Integer playlistId, final OnTracksLoadedListener listener) {
        Query playlistTrackQuery = playlistTracksRef.orderByChild("playlistID").equalTo(playlistId);
        Log.i("TrackService", "Query playlistTrackQuery success!");
        playlistTrackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("TrackService", "OnDataChange success");
                final List<Integer> trackIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Playlist_TracksModel playlistTrack = snapshot.getValue(Playlist_TracksModel.class);
                    // Add each TrackID associated with the playlistId to the list
                    trackIds.add(playlistTrack.getTrackID());
                }

                // Query tracks using the list of TrackIDs
                final AtomicInteger count = new AtomicInteger(trackIds.size());
                final List<TracksModel> tracks = new ArrayList<>();
                for (Integer trackId : trackIds) {
                    Query trackQuery = tracksRef.orderByChild("trackID").equalTo(trackId);
                    trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                TracksModel track = snapshot.getValue(TracksModel.class);
                                tracks.add(track);
                            }
                            if (count.decrementAndGet() == 0) {
                                listener.onTracksLoaded(tracks);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void getImageForTrack(final TracksModel track, final OnImageLoadedListener listener) {
        final int albumId = track.getAlubumID();
        Query query = albumsRef.orderByChild("albumID").equalTo(albumId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String imageUrl = snapshot.child("image").getValue(String.class);
                        listener.onImageLoaded(imageUrl);
                        return;
                    }
                }
                listener.onImageLoaded(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void getArtistNameByAlbumId(int albumId, final OnArtistNameLoadedListener listener) {
        Query query = albumsRef.orderByChild("albumID").equalTo(albumId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int artistId = snapshot.child("artistID").getValue(int.class);
                        getArtistNameById(artistId, listener);
                        return;
                    }
                }
                listener.onArtistNameLoaded(null); // No artist found
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
    private void getArtistNameById(int artistId, final OnArtistNameLoadedListener listener) {
        Query query = artistRef.orderByChild("artistID").equalTo(artistId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String artistName = snapshot.child("name").getValue(String.class);
                        listener.onArtistNameLoaded(artistName);
                        return;
                    }
                }
                listener.onArtistNameLoaded(null); // No artist found
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
    public void findTrackByName(final String trackName, final OnTracksLoadedListener listener) {
        Query trackQuery = tracksRef.orderByChild("name");
        trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Integer> trackIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(Objects.requireNonNull(snapshot.child("name").getValue(String.class)).toLowerCase().contains(trackName.toLowerCase())){
                        trackIds.add(snapshot.child("trackID").getValue(Integer.class));
                    }
                }

                // Query tracks using the list of TrackIDs
                final AtomicInteger count = new AtomicInteger(trackIds.size());
                final List<TracksModel> tracks = new ArrayList<>();
                for (Integer trackId : trackIds) {
                    Query trackQuery = tracksRef.orderByChild("trackID").equalTo(trackId);
                    trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                TracksModel track = snapshot.getValue(TracksModel.class);
                                tracks.add(track);
                            }
                            if (count.decrementAndGet() == 0) {
                                listener.onTracksLoaded(tracks);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void findTrackByID(final List<UserSongModel> trackIDs, final OnTracksLoadedListener listener) {
        Query trackQuery = tracksRef.orderByChild("trackID");
        trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Integer> trackIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (UserSongModel trackID : trackIDs) {
                        if (snapshot.child("trackID").getValue(Integer.class).equals(trackID.getSongID())) {
                            trackIds.add(snapshot.child("trackID").getValue(Integer.class));
                            break;
                        }
                    }
                }

                // Query tracks using the list of TrackIDs
                final AtomicInteger count = new AtomicInteger(trackIds.size());
                final List<TracksModel> tracks = new ArrayList<>();
                for (Integer trackId : trackIds) {
                    Query trackQuery = tracksRef.orderByChild("trackID").equalTo(trackId);
                    trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                TracksModel track = snapshot.getValue(TracksModel.class);
                                tracks.add(track);
                            }
                            if (count.decrementAndGet() == 0) {
                                listener.onTracksLoaded(tracks);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public CompletableFuture<List<TracksModel>> getTracksByPlaylistId(int playlistId){
        CompletableFuture<List<TracksModel>> future = new CompletableFuture<>();

        Query playlistTrackQuery = playlistTracksRef.orderByChild("playlistID").equalTo(playlistId);
        playlistTrackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("TrackService", "OnDataChange success");
                final List<Integer> trackIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Playlist_TracksModel playlistTrack = snapshot.getValue(Playlist_TracksModel.class);
                    // Add each TrackID associated with the playlistId to the list
                    trackIds.add(playlistTrack.getTrackID());
                }

                // Query tracks using the list of TrackIDs
                final AtomicInteger count = new AtomicInteger(trackIds.size());
                final List<TracksModel> tracks = new ArrayList<>();
                for (Integer trackId : trackIds) {
                    Query trackQuery = tracksRef.orderByChild("trackID").equalTo(trackId);
                    trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                TracksModel track = snapshot.getValue(TracksModel.class);
                                tracks.add(track);
                            }
                            if (count.decrementAndGet() == 0) {
                                future.complete(tracks);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return future;
    }
    public CompletableFuture<List<TracksModel>> getTracksByAlbumId(int albumId){
        CompletableFuture<List<TracksModel>> future = new CompletableFuture<>();

        Query trackQuery = tracksRef.orderByChild("alubumID").equalTo(albumId);
        trackQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("TrackService", "OnDataChange success");
                final List<TracksModel> tracks = new ArrayList<>();
                final AtomicInteger count = new AtomicInteger((int) dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TracksModel track = snapshot.getValue(TracksModel.class);
                    tracks.add(track);
                    if (count.decrementAndGet() == 0) {
                        future.complete(tracks);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.complete(null);
            }
        });
        return future;
    }
    public interface OnArtistNameLoadedListener {
        void onArtistNameLoaded(String artistName);
    }
    public interface OnTracksLoadedListener {
        void onTracksLoaded(List<TracksModel> tracks);
    }
    public interface OnImageLoadedListener {
        void onImageLoaded(String imageUrl);
    }
}
