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


    public interface AllAlbumLibraryUsersCallback {
        void onAllAlbumLibraryUsersRetrieved(List<AlbumsModel> albumLibraryUsers);
    }
}
