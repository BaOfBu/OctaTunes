package com.example.octatunes.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.octatunes.Model.SongManagerModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.TrackService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SongManagerAdapter extends RecyclerView.Adapter<SongManagerAdapter.SongViewHolder> {
    private List<SongManagerModel> songsList = new ArrayList<>();
    private List<SongManagerModel> totalSongs = new ArrayList<>();

    public SongManagerAdapter() {

    }

    public void setSongs(List<SongManagerModel> songsList) {
        this.songsList = songsList;
        this.totalSongs = songsList;
        filter(songsList);
    }

    public void removeTrack(String trackId, int position, TrackService.OnTrackRemovedListener listener) {
        songsList.remove(position);
        totalSongs.remove(position);

        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song_preview_admin, parent, false);
        return new SongViewHolder(view, this, totalSongs);
    }

    @Override

    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongManagerModel song = songsList.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public int getTotal() {
        return totalSongs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewSong;
        private TextView textViewSongName;
        private TextView textViewSongArtistName;
        private ImageView btnDetele;
        private SongManagerModel song;
        private SongManagerAdapter adapter;
        private List<SongManagerModel> totalSongs;

        public SongViewHolder(@NonNull View itemView, SongManagerAdapter adapter, List<SongManagerModel> totalSongs) {
            super(itemView);
            this.adapter = adapter;
            this.totalSongs = totalSongs;

            imageViewSong = itemView.findViewById(R.id.imageViewSong);
            textViewSongName = itemView.findViewById(R.id.textViewSongName);
            textViewSongArtistName = itemView.findViewById(R.id.textViewSongArtistName);
            btnDetele = itemView.findViewById(R.id.btn_delete_song);

            btnDetele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.btn_delete_song) {
                        int position = getAdapterPosition();
                        song = totalSongs.get(position);
                        if (position != RecyclerView.NO_POSITION && song != null) {
                            String trackId = String.valueOf(song.getTrackID());

                            // Khởi tạo đường dẫn
                            DatabaseReference Ref = FirebaseDatabase.getInstance().getReference();

                            // Xóa thông tin track trong tracks
                            Query queryTrack = Ref.child("tracks").orderByChild("trackID").equalTo(Integer.parseInt(trackId));

                            queryTrack.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot trackSnapshot) {
                                    if (trackSnapshot.exists()) {
                                        for (DataSnapshot Tracksnapshot : trackSnapshot.getChildren()) {
                                            Tracksnapshot.getRef().removeValue() // Remove the node from Firebase Realtime Database
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Thành công xóa tracks
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Thất bại xóa tracks
                                                        Toast.makeText(itemView.getContext(), "Lỗi không thể xóa thông tin trong tracks", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle any errors
                                    Toast.makeText(itemView.getContext(), "Lỗi khi thực hiện truy vấn", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Xóa thông tin track trong playlist_track
                            Query queryPlaylist = Ref.child("playlist_track").orderByChild("trackID").equalTo(Integer.parseInt(trackId));

                            queryPlaylist.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot playlistSnapshot) {
                                    if (playlistSnapshot.exists()) {
                                        for (DataSnapshot Playlistsnapshot : playlistSnapshot.getChildren()) {
                                            Playlistsnapshot.getRef().removeValue()
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Thành công xóa playlist


                                                    }).addOnFailureListener(e -> {
                                                        // Thất bại xóa playlist
                                                        Toast.makeText(itemView.getContext(), "Lỗi không thể xóa thông tin trong playlist", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            // Xóa thông tin track trong history
                            Query queryHistory = Ref.child("history").orderByChild("trackID").equalTo(Integer.parseInt(trackId));

                            queryHistory.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot historySnapshot) {
                                    if (historySnapshot.exists()) {
                                        for (DataSnapshot Historysnapshot : historySnapshot.getChildren()) {
                                            Historysnapshot.getRef().removeValue()
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Thành công xóa history

                                                    }).addOnFailureListener(e -> {
                                                        // Thất bại xóa history
                                                        Toast.makeText(itemView.getContext(), "Lỗi không thể xóa thông tin trong history", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            // Xóa thông tin track trong lyrics
                            Query queryLyric = Ref.child("lyrics").orderByChild("trackId").equalTo(Integer.parseInt(trackId));

                            queryLyric.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot lyricSnapshot) {
                                    if(lyricSnapshot.exists()){
                                        for (DataSnapshot Lyricsnapshot : lyricSnapshot.getChildren()){
                                            String lyricURL = Lyricsnapshot.child("lyric").getValue(String.class);

                                            Lyricsnapshot.getRef().removeValue()
                                                    .addOnSuccessListener(aVoid->{
                                                        // Thành công xóa lyrics
                                                        // Xóa file lyric
                                                        StorageReference lyricStorage = FirebaseStorage.getInstance().getReferenceFromUrl(lyricURL);

                                                        lyricStorage.delete()
                                                                .addOnSuccessListener(aVoid1->{
                                                                    // Thành công xóa file
                                                                    Toast.makeText(itemView.getContext(), "Xóa track thành công", Toast.LENGTH_SHORT).show();
                                                                }).addOnFailureListener(e->{
                                                                    // Thất bại xóa file
                                                                    Toast.makeText(itemView.getContext(), "Lỗi không thể xóa file lyric", Toast.LENGTH_SHORT).show();
                                                                });
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Thất bại xóa lyrics
                                                        Toast.makeText(itemView.getContext(), "Lỗi không thể xóa thông tin trong lyrics", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            // Xóa file mp3 của track
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(song.getFile());
                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(itemView.getContext(), "Xóa track thành công", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(itemView.getContext(), "Lỗi khi xóa file.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });
        }

        public void bind(final SongManagerModel songManagerModel) {
            Glide.with(itemView.getContext()).load(songManagerModel.getImage()).into(imageViewSong);
            textViewSongName.setText(songManagerModel.getTrackName());
            textViewSongArtistName.setText(songManagerModel.getArtistName());
        }
    }

    public void filter(List<SongManagerModel> filteredSongs) {
        songsList = new ArrayList<>();
        songsList.addAll(filteredSongs);
        notifyDataSetChanged();
    }
}