package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.AlbumsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AlbumService {
    private DatabaseReference albumsRef;

    public AlbumService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        albumsRef = database.getReference("albums");
    }

    // Method to add a new album
    public void addAlbum(final AlbumsModel album) {
        // Fetch the current maximum AlbumID from Firebase
        albumsRef.orderByChild("AlbumID").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int nextAlbumID = 0;
                if (dataSnapshot.exists()) {
                    // Get the highest AlbumID
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AlbumsModel lastAlbum = snapshot.getValue(AlbumsModel.class);
                        nextAlbumID = lastAlbum.getAlbumID() + 1;
                    }
                } else {
                    // If no albums exist yet, start with AlbumID = 1
                    nextAlbumID = 1;
                }

                // Set the calculated AlbumID and push the album to Firebase
                album.setAlbumID(nextAlbumID);
                albumsRef.push().setValue(album)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "Album added with ID: " + album.getAlbumID());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error adding album", e);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public CompletableFuture<List<AlbumsModel>> getAllAlbums(){
        CompletableFuture<List<AlbumsModel>> future = new CompletableFuture<>();
        albumsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<AlbumsModel> albums = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AlbumsModel album = snapshot.getValue(AlbumsModel.class);
                    if (album != null) {
                        albums.add(album);
                    }
                }
                future.complete(albums);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });
        return future;
    }

    public CompletableFuture<List<AlbumsModel>> findAlbumByName(String albumName){
        CompletableFuture<List<AlbumsModel>> future = new CompletableFuture<>();
        getAllAlbums().thenAccept(albums -> {
            List<AlbumsModel> foundAlbums = new ArrayList<>();
            for (AlbumsModel album : albums) {
                if (album.getName().toLowerCase().contains(albumName.toLowerCase())) {
                    foundAlbums.add(album);
                }
            }
            future.complete(foundAlbums);
        }).exceptionally(throwable -> {
            future.completeExceptionally(throwable);
            return null;
        });
        return future;
    }
}
