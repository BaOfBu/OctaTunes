package com.example.octatunes.Services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.SongManagerModel;
import com.example.octatunes.Model.TracksModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.StartupTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SongManagerService {
    private DatabaseReference mDatabase;

    public SongManagerService() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void getSongs(final onGetDataListener<List<SongManagerModel>> listener) {
        mDatabase.child("tracks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SongManagerModel> songs = new ArrayList<>();
                for (DataSnapshot trackSnapshot : dataSnapshot.getChildren()) {
                    TracksModel track = trackSnapshot.getValue(TracksModel.class);

                    final AlbumsModel album = new AlbumsModel();
                    album.setAlbumID(track.getAlubumID());

                    DatabaseReference albumRef = mDatabase.child("albums").child(Integer.toString(track.getAlubumID()));
                    albumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            AlbumsModel albumsModel = dataSnapshot.getValue(AlbumsModel.class);
                            // Cập nhật thông tin album vào biến album

                            final ArtistsModel artist = new ArtistsModel();
                            artist.setArtistID(albumsModel.getArtistID());

                            DatabaseReference artistRef = mDatabase.child("artists").child(Integer.toString(albumsModel.getArtistID()));
                            artistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ArtistsModel artistsModel = dataSnapshot.getValue(ArtistsModel.class);
                                    // Cập nhật thông tin artist vào biến artist

                                    SongManagerModel song = new SongManagerModel();

                                    song.setTrackID(track.getTrackID());
                                    song.setTrackName(track.getName());
                                    song.setAlbumID(album.getAlbumID());
                                    song.setArtistName(artistsModel.getName());
                                    song.setFile(track.getFile());
                                    song.setImage(album.getImage());

                                    songs.add(song);

                                    if (songs.size() == dataSnapshot.getChildrenCount()) {
                                        listener.onSuccess(songs);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Xử lý lỗi
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Xử lý lỗi
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    private ArtistsModel getArtist(int artistID) {
        final ArtistsModel artist = new ArtistsModel();
        return artist;
    }

    private AlbumsModel getAlbum(int albumID) {
        final AlbumsModel album = new AlbumsModel();
        return album;
    }

    public interface onGetDataListener<T> {
        void onSuccess(T data);

        void onFailure(Exception e);
    }
}
