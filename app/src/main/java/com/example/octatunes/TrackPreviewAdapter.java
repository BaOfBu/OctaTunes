package com.example.octatunes;

import android.content.Context;
import android.util.Log;
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
    private String belong;
    private String mode = "sequencePlay";
    private FragmentListener fragmentListener;

    public TrackPreviewAdapter(List<TracksModel> trackPreviewModels, Context context, FragmentListener fragmentListener, String belong) {
        this.trackPreviewModels = trackPreviewModels;
        this.context = context;
        this.fragmentListener = fragmentListener;
        this.belong = belong;
    }

    private void sendSignalToMainActivity(int trackID, int playlistID, int albumID, String from, String belong, String mode) {
        if (fragmentListener != null) {
            fragmentListener.onSignalReceived(trackID, playlistID, albumID, from, belong, mode);
        }
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
        if(fragmentListener == null) {
            fragmentListener = (FragmentListener) context;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("TRACK ADAPTER", String.valueOf(tracksModel.getTrackID()));
                Log.i("TRACK ADAPTER", String.valueOf(tracksModel.getAlubumID()));
                sendSignalToMainActivity(tracksModel.getTrackID(), -1, tracksModel.getAlubumID(), "PLAYING FROM SEARCH", "Track", mode);
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
