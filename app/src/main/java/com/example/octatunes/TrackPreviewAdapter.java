package com.example.octatunes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Services.TrackService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrackPreviewAdapter extends RecyclerView.Adapter<TrackPreviewAdapter.ViewHolder> {
    private List<TracksModel> trackPreviewModels;
    private Context context;

    public TrackPreviewAdapter(List<TracksModel> trackPreviewModels, Context context) {
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
        TracksModel tracksModel = trackPreviewModels.get(position);
        loadArtistName(tracksModel.getAlubumID(), holder.trackArtist);
        loadImageForTrack(tracksModel, holder.trackImage);
        holder.trackName.setText(tracksModel.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    private void loadImageForTrack(TracksModel track, ImageView imageView) {
        TrackService trackService = new TrackService();
        trackService.getImageForTrack(track, new TrackService.OnImageLoadedListener() {
            @Override
            public void onImageLoaded(String imageUrl) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(imageView);
                } else {
                }
            }
        });
    }
    private void loadArtistName(int albumId, TextView textView) {
        TrackService trackService = new TrackService();
        trackService.getArtistNameByAlbumId(albumId, new TrackService.OnArtistNameLoadedListener() {
            @Override
            public void onArtistNameLoaded(String artistName) {
                if (artistName != null) {
                    textView.setText(artistName);
                } else {
                    textView.setText("Unknown Artist");
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return trackPreviewModels.size();
    }

}
