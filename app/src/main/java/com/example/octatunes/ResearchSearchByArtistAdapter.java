package com.example.octatunes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ResearchSearchByArtistAdapter extends RecyclerView.Adapter<ResearchSearchByArtistAdapter.ViewHolder>{
    private UserProfileModel artistProfileModel;
    private ArrayList<TrackPreviewModel> trackPreviewModels;
    private Context context;

    public ResearchSearchByArtistAdapter(UserProfileModel artistProfileModel, ArrayList<TrackPreviewModel> trackPreviewModels, Context context) {
        this.artistProfileModel = artistProfileModel;
        this.trackPreviewModels = trackPreviewModels;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView artistImage;
        private TextView artistName;
        private RecyclerView trackRecyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.artist_image);
            artistName = itemView.findViewById(R.id.artist_name);

            trackRecyclerView = itemView.findViewById(R.id.recyclerview_track_preview); // Initialize the new RecyclerView

        }
    }
    @NonNull
    @Override
    public ResearchSearchByArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_search_artist, parent, false);
        return new ResearchSearchByArtistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResearchSearchByArtistAdapter.ViewHolder holder, int position) {
        Picasso.get().load(artistProfileModel.getUserImageId()).into(holder.artistImage);
        holder.artistName.setText(artistProfileModel.getFullName());

//        TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(trackPreviewModels, context);
//        holder.trackRecyclerView.setAdapter(trackPreviewAdapter);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}