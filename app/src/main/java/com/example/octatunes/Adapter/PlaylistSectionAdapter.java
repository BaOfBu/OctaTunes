package com.example.octatunes.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;

import java.util.List;

public class PlaylistSectionAdapter extends RecyclerView.Adapter<PlaylistSectionAdapter.SectionViewHolder> {

    private List<String> sectionTitles;
    private List<List<PlaylistsModel>> playlistsBySection;
    private static Context context;

    public PlaylistSectionAdapter(Context context, List<String> sectionTitles, List<List<PlaylistsModel>> playlistsBySection) {
        this.context = context;
        this.sectionTitles = sectionTitles;
        this.playlistsBySection = playlistsBySection;
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView playlistHeader;
        public RecyclerView playlistsRecyclerView;
        public ListPlaylistAdapter playlistAdapter;

        public SectionViewHolder(View itemView) {
            super(itemView);
            playlistHeader = itemView.findViewById(R.id.playlistHeader);
            playlistsRecyclerView = itemView.findViewById(R.id.playlistsRecyclerView);
            playlistAdapter = new ListPlaylistAdapter(context);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            playlistsRecyclerView.setLayoutManager(layoutManager);
            playlistsRecyclerView.setAdapter(playlistAdapter);
        }
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_playlist_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        // Bind section title
        holder.playlistHeader.setText(sectionTitles.get(position));

        // Bind playlists for this section
        holder.playlistAdapter.setPlaylistItems(playlistsBySection.get(position));
    }

    @Override
    public int getItemCount() {
        return sectionTitles.size();
    }
}

