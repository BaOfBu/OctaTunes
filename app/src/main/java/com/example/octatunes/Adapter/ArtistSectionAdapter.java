package com.example.octatunes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;

import java.util.List;

public class ArtistSectionAdapter extends RecyclerView.Adapter<ArtistSectionAdapter.SectionViewHolder> {
    private List<String> sectionTitles;

    private List<List<ArtistsModel>> artistsBySection;
    private static Context context;

    public ArtistSectionAdapter(Context context, List<String> sectionTitles, List<List<ArtistsModel>> artistsBySection) {
        this.context = context;
        this.sectionTitles = sectionTitles;
        this.artistsBySection = artistsBySection;
    }



    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView artistHeader;
        public RecyclerView artistRecyclerView;
        public ListArtistAdapter artistAdapter;
        public SectionViewHolder(View itemView) {
            super(itemView);
            artistHeader = itemView.findViewById(R.id.artistHeader);
            artistRecyclerView = itemView.findViewById(R.id.artistRecyclerView);
            artistAdapter = new ListArtistAdapter(context);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            artistRecyclerView.setLayoutManager(layoutManager);
            artistRecyclerView.setAdapter(artistAdapter);
        }
    }
    @NonNull
    @Override
    public ArtistSectionAdapter.SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_artist_section, parent, false);
        return new ArtistSectionAdapter.SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistSectionAdapter.SectionViewHolder holder, int position) {
        // Bind section title
        holder.artistHeader.setText(sectionTitles.get(position));

        // Bind playlists for this section
        holder.artistAdapter.setArtistItems(artistsBySection.get(position));
    }

    @Override
    public int getItemCount() {
        return sectionTitles.size();
    }
}
