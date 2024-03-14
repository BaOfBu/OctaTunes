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

public class ResultSearchByBandNameAdapter extends RecyclerView.Adapter<ResultSearchByBandNameAdapter.ViewHolder>{
    private UserProfileModel artistProfileModel;
    private ArrayList<TrackPreviewModel> albumPreviewModels;
    private ArrayList<TrackPreviewModel> trackPreviewModels;
    private Context context;

    public ResultSearchByBandNameAdapter(UserProfileModel artistProfileModel, ArrayList<TrackPreviewModel> albumPreviewModels, ArrayList<TrackPreviewModel> trackPreviewModels, Context context) {
        this.artistProfileModel = artistProfileModel;
        this.albumPreviewModels = albumPreviewModels;
        this.trackPreviewModels = trackPreviewModels;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView artistImage;
        private TextView artistName;
        private RecyclerView albumRecyclerView;
        private RecyclerView trackRecyclerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.artist_image);
            artistName = itemView.findViewById(R.id.artist_name);

            albumRecyclerView = itemView.findViewById(R.id.recyclerview_album_preview); // Initialize the new RecyclerView
            trackRecyclerView = itemView.findViewById(R.id.recyclerview_track_preview); // Initialize the new RecyclerView

        }
    }
    @NonNull
    @Override
    public ResultSearchByBandNameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_search_band, parent, false);
        return new ResultSearchByBandNameAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultSearchByBandNameAdapter.ViewHolder holder, int position) {
        Picasso.get().load(artistProfileModel.getUserImageId()).into(holder.artistImage);
        holder.artistName.setText(artistProfileModel.getFullName());

        AlbumPreviewAdapter albumPreviewAdapter = new AlbumPreviewAdapter(albumPreviewModels, context);
        holder.albumRecyclerView.setAdapter(albumPreviewAdapter);

        TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(trackPreviewModels, context);
        holder.trackRecyclerView.setAdapter(trackPreviewAdapter);
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
