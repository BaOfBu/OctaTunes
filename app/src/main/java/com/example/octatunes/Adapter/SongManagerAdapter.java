package com.example.octatunes.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.SongManagerModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.TrackService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song_preview, parent, false);
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
        private TextView textViewName;
        private TextView textViewArtist;
        private ImageView btnDetele, btnEdit;
        private SongManagerModel song;
        private SongManagerAdapter adapter;
        private List<SongManagerModel> totalSongs;

        public SongViewHolder(@NonNull View itemView, SongManagerAdapter adapter, List<SongManagerModel> totalSongs) {
            super(itemView);
            this.adapter = adapter;
            this.totalSongs = totalSongs;

            imageViewSong = itemView.findViewById(R.id.imageViewSong);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewArtist = itemView.findViewById(R.id.textViewArtist);
            btnDetele = itemView.findViewById(R.id.btn_delete_song);
            btnEdit = itemView.findViewById(R.id.btn_edit_song);

            btnDetele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.btn_delete_song) {
                        int position = getAdapterPosition();
                        song = totalSongs.get(position);
                        if (position != RecyclerView.NO_POSITION && song != null) {
                            String trackId = String.valueOf(song.getTrackID());

                            // Delete the track from the Firebase database
                            DatabaseReference trackRef = FirebaseDatabase.getInstance().getReference("tracks").child(trackId);
                            trackRef.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Toast.makeText(itemView.getContext(), "Track đã được xóa.", Toast.LENGTH_SHORT).show();

                                }
                            });

                            // Delete the file from Firebase Storage
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(song.getFile());
                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(itemView.getContext(), "File đã được xóa.", Toast.LENGTH_SHORT).show();

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
            textViewName.setText(songManagerModel.getTrackName());
            textViewArtist.setText(songManagerModel.getArtistName());
        }
    }

    public void filter(List<SongManagerModel> filteredSongs) {
        songsList = new ArrayList<>();
        songsList.addAll(filteredSongs);
        notifyDataSetChanged();
    }
}