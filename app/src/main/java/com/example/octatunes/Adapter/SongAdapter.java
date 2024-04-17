package com.example.octatunes.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<SongModel> songs;

    public SongAdapter(List<SongModel> songs) {
        this.songs = songs;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_playlist_spotify_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        SongModel song = songs.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        holder.image.setImageResource(song.getImageUrl());
        holder.itemNumber.setText(position + 1 + "");
        // Additional setup for other views and click listeners
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        ImageView image;

        TextView itemNumber;

        public SongViewHolder(View itemView) {
            super(itemView);
            itemNumber = itemView.findViewById(R.id.item_number);
            title = itemView.findViewById(R.id.item_title);
            artist = itemView.findViewById(R.id.item_artist);
            image = itemView.findViewById(R.id.item_image);
        }
    }
}
