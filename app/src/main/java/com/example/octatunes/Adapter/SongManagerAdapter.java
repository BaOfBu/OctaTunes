package com.example.octatunes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.SongManagerModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.TrackService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SongManagerAdapter extends RecyclerView.Adapter<SongManagerAdapter.SongViewHolder> {
    private List<SongManagerModel> songsList = new ArrayList<>();

    public void setSongs(List<SongManagerModel> songsList) {
        this.songsList = songsList;
        filter(songsList);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song_preview, parent, false);
        return new SongViewHolder(view);
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

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewSong;
        private TextView textViewName;
        private TextView textViewArtist;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSong = itemView.findViewById(R.id.imageViewSong);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewArtist = itemView.findViewById(R.id.textViewArtist);
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