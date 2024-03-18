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

public class TrackPreviewAdapter extends RecyclerView.Adapter<TrackPreviewAdapter.ViewHolder> {
    private ArrayList<TrackPreviewModel> trackPreviewModels;
    private Context context;

    public TrackPreviewAdapter(ArrayList<TrackPreviewModel> trackPreviewModels, Context context) {
        this.trackPreviewModels = trackPreviewModels;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView trackImage;
        private TextView trackName, type, trackArtist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackImage = itemView.findViewById(R.id.track_image);
            trackName = itemView.findViewById(R.id.track_name);
            type = itemView.findViewById(R.id.type);
            trackArtist = itemView.findViewById(R.id.track_artist);
        }
    }
    @NonNull
    @Override
    public TrackPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_track_preview, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TrackPreviewAdapter.ViewHolder holder, int position) {
        TrackPreviewModel trackPreviewModel = trackPreviewModels.get(position);
        Picasso.get().load(trackPreviewModel.getTrackImageId()).into(holder.trackImage);
        holder.trackName.setText(trackPreviewModel.getTrackName());
        holder.trackArtist.setText(trackPreviewModel.getTrackArtist());
        holder.type.setText(trackPreviewModel.getType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    @Override
    public int getItemCount() {
        return trackPreviewModels.size();
    }

}
