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
                    SongManagerModel track = trackSnapshot.getValue(SongManagerModel.class);
                    track.setTrackID(Integer.parseInt(trackSnapshot.getKey()));

                    final AlbumsModel album = getAlbum(track.getAlbumID());
                    final ArtistsModel artist = getArtist(album.getArtistID());

                    SongManagerModel song = new SongManagerModel();

                    song.setTrackID(track.getTrackID());
                    song.setTrackName(track.getTrackName());
                    song.setAlbumID(album.getAlbumID());
                    song.setArtistName(artist.getName());
                    song.setFile(track.getFile());
                    song.setImage(album.getImage());

                    songs.add(song);
                }
                listener.onSuccess(songs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    private ArtistsModel getArtist(int artistID) {
        DatabaseReference artistRef = mDatabase.child("artists").child(String.valueOf(artistID));
        final ArtistsModel artist = new ArtistsModel();

        artistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArtistsModel artist = dataSnapshot.getValue(ArtistsModel.class);
                artist.setArtistID(Integer.parseInt(dataSnapshot.getKey()));
                // Cập nhật thông tin artist vào biến artist
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });

        return artist;
    }

    private AlbumsModel getAlbum(int albumID) {
        DatabaseReference albumRef = mDatabase.child("albums").child(String.valueOf(albumID));
        final AlbumsModel album = new AlbumsModel();

        albumRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AlbumsModel album = dataSnapshot.getValue(AlbumsModel.class);
                album.setAlbumID(Integer.parseInt(dataSnapshot.getKey()));
                // Cập nhật thông tin album vào biến album
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });

        return album;
    }

    public interface onGetDataListener<T> {
        void onSuccess(T data);

        void onFailure(Exception e);
    }
}
