package com.example.octatunes.Services;
import android.util.Log;

import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.FollowersModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FollowerService {

    private DatabaseReference databaseReference;
    private DatabaseReference artistsReference;
    private FirebaseAuth firebaseAuth;

    UserService userService = new UserService();

    public FollowerService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference("followers");
        artistsReference = firebaseDatabase.getReference("artists");
    }

    public void addFollowedArtist(int userId, int artistId) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userFollowingRef = databaseReference.push(); // Generate random key

            // Create a new FollowersModel object
            FollowersModel follower = new FollowersModel(userId, artistId);

            // Add the follower to the database under the generated key
            userFollowingRef.setValue(follower)
                    .addOnSuccessListener(aVoid -> {
                        // Follower added successfully
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });

            // Also, add the follower to the artists' followers list
            DatabaseReference artistFollowersRef = artistsReference.child(String.valueOf(artistId)).child("followers");
            artistFollowersRef.child(String.valueOf(userId)).setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        // Follower added successfully to artist's followers list
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }

    public void addFollowedArtistForCurrentUser(int artistId) {
        userService.getCurrentUserId(new UserService.UserIdCallback() {
            @Override
            public void onUserIdRetrieved(int userId) {
                if (userId != -1) {
                    // Current user ID retrieved successfully
                    addFollowedArtist(userId, artistId);
                } else {
                    // Failed to retrieve current user ID
                    Log.e("FollowerService", "Failed to retrieve current user ID");
                }
            }
        });
    }

    public void isArtistFollowedByCurrentUser(int artistId, FollowCheckCallback callback) {

        userService.getCurrentUserId(new UserService.UserIdCallback() {
            @Override
            public void onUserIdRetrieved(int userId) {
                if (userId != -1) {
                    // Once the current user ID is retrieved, check if the artist is followed by this user
                    DatabaseReference userFollowingRef = databaseReference.child(String.valueOf(userId));
                    userFollowingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean isFollowed = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                FollowersModel follower = snapshot.getValue(FollowersModel.class);
                                if (follower != null && follower.getArtistID() == artistId) {
                                    // Artist is already followed by the current user
                                    isFollowed = true;
                                    break;
                                }
                            }
                            callback.onFollowChecked(isFollowed);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle onCancelled event
                            callback.onFollowChecked(false);
                        }
                    });
                } else {
                    // Unable to retrieve current user ID
                    callback.onFollowChecked(false);
                }
            }
        });
    }

    public interface FollowCheckCallback {
        void onFollowChecked(boolean isFollowed);
    }

    public void getAllArtistsFollowedByCurrentUser(ArtistListCallback callback) {
        userService.getCurrentUserId(new UserService.UserIdCallback() {
            @Override
            public void onUserIdRetrieved(int userId) {
                if (userId != -1) {
                    // Once the current user ID is retrieved, fetch all artists followed by this user
                    databaseReference.orderByChild("userID").equalTo(userId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Integer> artistIds = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                FollowersModel follower = snapshot.getValue(FollowersModel.class);
                                if (follower != null) {
                                    artistIds.add(follower.getArtistID());
                                }
                            }
                            // Fetch the corresponding ArtistModel objects
                            fetchArtists(artistIds, callback);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle onCancelled event
                            callback.onArtistsRetrieved(Collections.emptyList());
                        }
                    });
                } else {
                    // Unable to retrieve current user ID
                    callback.onArtistsRetrieved(Collections.emptyList());
                }
            }
        });
    }

    private void fetchArtists(List<Integer> artistIds, ArtistListCallback callback) {
        DatabaseReference artistsRef = FirebaseDatabase.getInstance().getReference("artists");
        artistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ArtistsModel> artists = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArtistsModel artist = snapshot.getValue(ArtistsModel.class);
                    if (artist != null && artistIds.contains(artist.getArtistID())) {
                        artists.add(artist);
                    }
                }
                callback.onArtistsRetrieved(artists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onArtistsRetrieved(Collections.emptyList());
            }
        });
    }

    public interface ArtistListCallback {
        void onArtistsRetrieved(List<ArtistsModel> artists);
    }


}
