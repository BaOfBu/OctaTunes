package com.example.octatunes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.R;

import java.util.List;

public class ArtistSectionAdapter extends RecyclerView.Adapter<ArtistSectionAdapter.ViewHolder> {

    private Context context;
    private List<ArtistsModel> artistSectionList;

    public ArtistSectionAdapter(Context context, List<ArtistsModel> artistSectionList) {
        this.context = context;
        this.artistSectionList = artistSectionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_home_artist_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //ArtistSectionModel artistSection = artistSectionList.get(position);
        //holder.titleTextView.setText(artistSection.getTitle());
        //
        //// Create an adapter for the artist items in this section
        //ArtistItemAdapter itemAdapter = new ArtistItemAdapter(artistSection.getArtistItemList());
        //holder.artistRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        //holder.artistRecyclerView.setAdapter(itemAdapter);
    }

    @Override
    public int getItemCount() {
        return artistSectionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        RecyclerView artistRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.artistHeader);
            artistRecyclerView = itemView.findViewById(R.id.artistRecyclerView);
        }
    }
}
