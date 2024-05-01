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

import com.example.octatunes.Model.HistoryModel;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Services.HistoryService;
import com.example.octatunes.Services.TrackService;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TrackPreviewAdapterForUser extends RecyclerView.Adapter<TrackPreviewAdapterForUser.ViewHolder> {
    private List<SongModel> songPreviewModels;
    private Context context;
    private String belong;
    private String mode = "sequencePlay";
    private FragmentListener fragmentListener;

    public TrackPreviewAdapterForUser(List<SongModel> songPreviewModels, Context context, FragmentListener fragmentListener, String belong) {
        removeDuplicateTitles(songPreviewModels);
        this.songPreviewModels = songPreviewModels;

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
    public TrackPreviewAdapterForUser.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_track_preview, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TrackPreviewAdapterForUser.ViewHolder holder, int position) {
        SongModel songModel = songPreviewModels.get(position);
        holder.trackArtist.setText(songModel.getArtist());
        Picasso.get().load(songModel.getImage()).into(holder.trackImage);
        holder.trackName.setText(songModel.getTitle());
        if(fragmentListener == null) {
            fragmentListener = (FragmentListener) context;
        }
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendSignalToMainActivity(songModel.get(), -1, tracksModel.getAlubumID(), "PLAYING FROM SEARCH", belong, mode);
//            }
//        });
    }

    public void removeDuplicateTitles(List<SongModel> songs) {
        Set<String> titlesSeen = new HashSet<>();
        Iterator<SongModel> iterator = songs.iterator();
        while (iterator.hasNext()) {
            SongModel song = iterator.next();
            if (titlesSeen.contains(song.getTitle())) {
                iterator.remove(); // Remove duplicates
            } else {
                titlesSeen.add(song.getTitle());
            }
        }
    }
    @Override
    public int getItemCount() {
        return songPreviewModels.size();
    }

}
