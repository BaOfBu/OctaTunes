package com.example.octatunes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;

import java.util.List;

public class PlaylistSectionAdapter extends RecyclerView.Adapter<PlaylistSectionAdapter.ViewHolder> {

    private Context context;
    private List<PlaylistsModel> playlistSections;

    public PlaylistSectionAdapter(Context context, List<PlaylistsModel> playlistSections) {
        this.context = context;
        this.playlistSections = playlistSections;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_home_playlist_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //PlaylistSectionModel playlistSection = playlistSections.get(position);
        //holder.titleTextView.setText(playlistSection.getTitle());
        //
        //// Create an adapter for the playlist items in this section
        //PlaylistAdapter itemAdapter = new PlaylistAdapter(playlistSection.getPlaylistItems());
        //holder.playlistRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        //holder.playlistRecyclerView.setAdapter(itemAdapter);
    }



    @Override
    public int getItemCount() {
        return playlistSections.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        RecyclerView playlistRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.playlistHeader);
            playlistRecyclerView = itemView.findViewById(R.id.playlistsRecyclerView);
        }
    }
}
