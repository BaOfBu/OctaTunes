package com.example.octatunes.Services;
import com.example.octatunes.Model.AlbumLibrary_User;
import com.example.octatunes.Model.AlbumsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumLibraryUserService {

    private DatabaseReference albumLibraryUserRef;

    public AlbumLibraryUserService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        albumLibraryUserRef = database.getReference("album_library_user");
    }

    public void getAllAlbumLibraryUsers(AllAlbumLibraryUsersCallback callback) {
        albumLibraryUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AlbumLibrary_User> albumLibraryUsers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AlbumLibrary_User albumLibraryUser = snapshot.getValue(AlbumLibrary_User.class);
                    if (albumLibraryUser != null) {
                        albumLibraryUsers.add(albumLibraryUser);
                    }
                }
                // Once all AlbumLibrary_User objects are retrieved, fetch the corresponding AlbumsModel objects
                List<AlbumsModel> albums = new ArrayList<>();
                DatabaseReference albumsRef = FirebaseDatabase.getInstance().getReference("albums");
                for (AlbumLibrary_User user : albumLibraryUsers) {
                    albumsRef.orderByChild("albumID").equalTo(user.getAlbumID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            AlbumsModel albumModel = dataSnapshot.getValue(AlbumsModel.class);
                            if (albumModel != null) {
                                albums.add(albumModel);
                            }

                            if (albums.size() == albumLibraryUsers.size()) {
                                callback.onAllAlbumLibraryUsersRetrieved(albums);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle onCancelled
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    // Method to get all albums of the current user
    public void getAllAlbumsForCurrentUser(AlbumCallback callback) {
        UserService userService = new UserService();
        userService.getCurrentUserId(new UserService.UserIdCallback() {
            @Override
            public void onUserIdRetrieved(int userId) {
                if (userId != -1) {
                    // User ID retrieved successfully
                    // Now fetch albums for this user ID
                    fetchAlbumsForUser(userId, callback);
                } else {
                    // Failed to retrieve user ID
                    callback.onAlbumsRetrieved(Collections.emptyList());
                }
            }
        });
    }

    // Helper method to fetch albums for a user ID
    private void fetchAlbumsForUser(int userId, AlbumCallback callback) {
        albumLibraryUserRef.orderByChild("userID").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Integer> albumIds = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            AlbumLibrary_User albumLibraryUser = snapshot.getValue(AlbumLibrary_User.class);
                            if (albumLibraryUser != null) {
                                albumIds.add(albumLibraryUser.getAlbumID());
                            }
                        }

                        // Now we have a list of album IDs, let's fetch the corresponding albums
                        fetchAlbums(albumIds, callback);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onAlbumsRetrieved(Collections.emptyList());
                    }
                });
    }

    // Helper method to fetch albums based on their IDs
    private void fetchAlbums(List<Integer> albumIds, AlbumCallback callback) {
        DatabaseReference albumsRef = FirebaseDatabase.getInstance().getReference("albums");
        albumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AlbumsModel> albums = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AlbumsModel album = snapshot.getValue(AlbumsModel.class);
                    if (album != null && albumIds.contains(album.getAlbumID())) {
                        albums.add(album);
                    }
                }
                callback.onAlbumsRetrieved(albums);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onAlbumsRetrieved(Collections.emptyList());
            }
        });
    }

    // Callback interface for returning the retrieved albums
    public interface AlbumCallback {
        void onAlbumsRetrieved(List<AlbumsModel> albums);
    }


    public interface AllAlbumLibraryUsersCallback {
        void onAllAlbumLibraryUsersRetrieved(List<AlbumsModel> albumLibraryUsers);
    }
}
