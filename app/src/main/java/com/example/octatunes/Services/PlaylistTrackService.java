package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.Playlist_TracksModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.UsersModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PlaylistTrackService {
    private DatabaseReference playlistTracksRef;

    public PlaylistTrackService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        playlistTracksRef = database.getReference("playlist_track");
    }

    // Method to add a new playlist track entry
    public void addPlaylistTrack(Playlist_TracksModel playlistTrack) {
        // Fetch existing tracks for the same playlistID
        playlistTracksRef.orderByChild("playlistID").equalTo(playlistTrack.getPlaylistID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int maxOrder = 0;

                        // Determine the highest order value
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Playlist_TracksModel track = snapshot.getValue(Playlist_TracksModel.class);
                            if (track != null && track.getOrder() > maxOrder && track.getPlaylistID() == playlistTrack.getPlaylistID()) {
                                maxOrder = track.getOrder();
                            }
                        }

                        // Set the order for the new playlist track
                        playlistTrack.setOrder(maxOrder + 1);

                        // Generate a key for the playlist track entry from Firebase
                        String key = playlistTracksRef.push().getKey();

                        // Set the value with the key and playlistTrack object
                        playlistTracksRef.child(key).setValue(playlistTrack)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "Playlist track added successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("TAG", "Error adding playlist track", e);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("TAG", "Error fetching existing tracks", databaseError.toException());
                    }
                });
    }

    public void removePlaylistTrack(int playlistId, int trackId) {
        // Construct the DatabaseReference to the specific track
        Query query = playlistTracksRef.orderByChild("playlistID").equalTo(playlistId)
                .getRef().orderByChild("trackID").equalTo(trackId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "Playlist track removed successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("TAG", "Error removing playlist track", e);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "Error fetching existing tracks", databaseError.toException());
            }
        });
    }

    public void addLovePlaylistTrack(int userId,int trackId){

        PlaylistService playlistService = new PlaylistService();
        playlistService.getPlaylistModelLiked(userId, new PlaylistService.PlaylistCallback() {
            @Override
            public void onPlaylistRetrieved(PlaylistsModel likedPlaylist) {
                if (likedPlaylist != null) {
                    Playlist_TracksModel playlistTrack = new Playlist_TracksModel(likedPlaylist.getPlaylistID(), trackId, 0);
                    PlaylistTrackService playlistTrackService = new PlaylistTrackService();
                    playlistTrackService.addPlaylistTrack(playlistTrack);

                } else {
                    Log.e("addLovePlaylistTrack", "Liked Songs playlist not found for user: " + userId);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
                Log.e("addLovePlaylistTrack", errorMessage);
            }
        });
    }
    //public void addLovePlaylistTrack(int trackId) {
    //    UserService userService = new UserService();
    //    userService.getCurrentUserModel(new UserService.UserModelCallback() {
    //        @Override
    //        public void onUserModelRetrieved(UsersModel currentUser) {
    //            if (currentUser != null) {
    //                // Current user found, get user ID
    //                int userId = currentUser.getUserID();
    //
    //                // Get the "Liked Songs" playlist associated with the user
    //                PlaylistService playlistService = new PlaylistService();
    //                playlistService.getPlaylistModelLiked(userId, new PlaylistService.PlaylistCallback() {
    //                    @Override
    //                    public void onPlaylistRetrieved(PlaylistsModel likedPlaylist) {
    //                        if (likedPlaylist != null) {
    //                            // Playlist found, add the track to the playlist
    //                            Playlist_TracksModel playlistTrack = new Playlist_TracksModel(likedPlaylist.getPlaylistID(), trackId, 0);
    //                            PlaylistTrackService playlistTrackService = new PlaylistTrackService();
    //                            playlistTrackService.addPlaylistTrack(playlistTrack);
    //                        } else {
    //                            Log.e("addLovePlaylistTrack", "Liked Songs playlist not found for user: " + userId);
    //                        }
    //                    }
    //
    //                    @Override
    //                    public void onError(String errorMessage) {
    //                        // Handle error
    //                        Log.e("addLovePlaylistTrack", errorMessage);
    //                    }
    //                });
    //            } else {
    //                // Current user not found
    //                Log.e("addLovePlaylistTrack", "Current user not found");
    //            }
    //        }
    //    });
    //}
    public void addLovePlaylistTrack(int trackId) {
        UserService userService = new UserService();
        userService.getCurrentUserModel(new UserService.UserModelCallback() {
            @Override
            public void onUserModelRetrieved(UsersModel currentUser) {
                if (currentUser != null) {
                    // Current user found, get user ID
                    int userId = currentUser.getUserID();

                    // Get the "Liked Songs" playlist associated with the user
                    PlaylistService playlistService = new PlaylistService();
                    playlistService.getPlaylistModelLiked(userId, new PlaylistService.PlaylistCallback() {
                        @Override
                        public void onPlaylistRetrieved(PlaylistsModel likedPlaylist) {
                            if (likedPlaylist != null) {
                                // Playlist found, add the track to the playlist
                                Playlist_TracksModel playlistTrack = new Playlist_TracksModel(likedPlaylist.getPlaylistID(), trackId, 0);
                                PlaylistTrackService playlistTrackService = new PlaylistTrackService();
                                playlistTrackService.addPlaylistTrack(playlistTrack);
                            } else {
                                // Liked Songs playlist not found, add it and then add the track
                                addTrackToNewLikedPlaylist(userId, trackId);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Handle error
                            Log.e("addLovePlaylistTrack", errorMessage);
                        }
                    });
                } else {
                    // Current user not found
                    Log.e("addLovePlaylistTrack", "Current user not found");
                }
            }
        });
    }

    private void addTrackToNewLikedPlaylist(int userId, int trackId) {
        // Add Liked Songs playlist
        PlaylistService playlistService = new PlaylistService();
        playlistService.addPlaylistsLiked(userId, new PlaylistService.PlaylistAddedCallback() {
            @Override
            public void onPlaylistAdded(int newPlaylistId) {
                // Playlist added successfully, now add the track to it
                Playlist_TracksModel playlistTrack = new Playlist_TracksModel(newPlaylistId, trackId, 0);
                PlaylistTrackService playlistTrackService = new PlaylistTrackService();
                playlistTrackService.addPlaylistTrack(playlistTrack);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
                Log.e("addTrackToNewLikedPlaylist", errorMessage);
            }
        });
    }




}
