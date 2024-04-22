package com.example.octatunes.Services;

import com.example.octatunes.Model.PlaylistLibrary_User;
import com.example.octatunes.Model.PlaylistsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistLibraryUserService {

    private DatabaseReference playlistLibraryUserRef;

    public PlaylistLibraryUserService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        playlistLibraryUserRef = database.getReference("playlist_library_user");
    }

    // Method to add a new PlaylistLibrary_User
    public void addPlaylistLibraryUser(PlaylistLibrary_User playlistLibraryUser) {
        String key = playlistLibraryUserRef.push().getKey();
        if (key != null) {
            playlistLibraryUserRef.child(key).setValue(playlistLibraryUser)
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }
    public void removePlaylistLibraryUser(int playlistID, int userID) {
        playlistLibraryUserRef.orderByChild("playlistID").equalTo(playlistID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            PlaylistLibrary_User playlistLibraryUser = snapshot.getValue(PlaylistLibrary_User.class);
                            if (playlistLibraryUser != null && playlistLibraryUser.getUserID() == userID) {
                                snapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                        })
                                        .addOnFailureListener(e -> {
                                        });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
    public void checkIfPlaylistExistsInLibrary(int playlistID, int userID, OnCheckCompleteListener listener) {
        playlistLibraryUserRef.orderByChild("playlistID").equalTo(playlistID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean exists = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            PlaylistLibrary_User playlistLibraryUser = snapshot.getValue(PlaylistLibrary_User.class);
                            if (playlistLibraryUser != null && playlistLibraryUser.getUserID() == userID) {
                                exists = true;
                                break;
                            }
                        }
                        listener.onCheckComplete(exists);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onCheckComplete(false);
                    }
                });
    }

    public interface OnCheckCompleteListener {
        void onCheckComplete(boolean exists);
    }
    public void getAllPlaylistLibraryUsers(AllPlaylistLibraryUsersCallback callback) {
        playlistLibraryUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PlaylistLibrary_User> playlistLibraryUsers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlaylistLibrary_User playlistLibraryUser = snapshot.getValue(PlaylistLibrary_User.class);
                    if (playlistLibraryUser != null) {
                        playlistLibraryUsers.add(playlistLibraryUser);
                    }
                }

                // Once all PlaylistLibrary_User objects are retrieved, fetch the corresponding PlaylistModel objects
                List<PlaylistsModel> playlists = new ArrayList<>();
                DatabaseReference playlistsRef = FirebaseDatabase.getInstance().getReference("playlists");
                for (PlaylistLibrary_User user : playlistLibraryUsers) {
                    playlistsRef.orderByChild("playlistID").equalTo(user.getPlaylistID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot playlistSnapshot : dataSnapshot.getChildren()) {
                                PlaylistsModel playlistModel = playlistSnapshot.getValue(PlaylistsModel.class);
                                if (playlistModel != null) {
                                    playlists.add(playlistModel);
                                    break; // Stop loop after finding a match
                                }
                            }

                            // Check if all PlaylistLibrary_User objects have been processed
                            if (playlists.size() == playlistLibraryUsers.size()) {
                                callback.onAllPlaylistLibraryUsersRetrieved(playlists);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle onCancelled event
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onAllPlaylistLibraryUsersRetrieved(Collections.emptyList());
            }
        });
    }


    public interface AllPlaylistLibraryUsersCallback {
        void onAllPlaylistLibraryUsersRetrieved(List<PlaylistsModel> playlistLibraryUsers);
    }

}
