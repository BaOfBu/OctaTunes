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

public class ResultSearchOnlyPlaylistAdapter extends RecyclerView.Adapter<ResultSearchOnlyPlaylistAdapter.ViewHolder>{
    private ArrayList<TrackPreviewModel> playlistPreviewModelsLeft;
    private ArrayList<TrackPreviewModel> playlistPreviewModelsRight;
    private Context context;

    public ResultSearchOnlyPlaylistAdapter(ArrayList<TrackPreviewModel> playlistPreviewModelsLeft, ArrayList<TrackPreviewModel>  playlistPreviewModelsRight, Context context) {
        this.playlistPreviewModelsLeft = playlistPreviewModelsLeft;
        this.playlistPreviewModelsRight = playlistPreviewModelsRight;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView playlistImageLeft, playlistImageRight;
        private TextView playlistNameLeft;
        private TextView playlistNameRight;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImageLeft = itemView.findViewById(R.id.playlist_image_left);
            playlistNameLeft = itemView.findViewById(R.id.playlist_name_left);

            playlistImageRight = itemView.findViewById(R.id.playlist_image_right);
            playlistNameRight = itemView.findViewById(R.id.playlist_name_right);
        }
    }
    @NonNull
    @Override
    public ResultSearchOnlyPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_playlist_preview_2, parent, false);
        return new ResultSearchOnlyPlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultSearchOnlyPlaylistAdapter.ViewHolder holder, int position) {
        if(playlistPreviewModelsLeft.get(position) != null){
            TrackPreviewModel trackPreviewModelLeft = playlistPreviewModelsLeft.get(position);
            Picasso.get().load(trackPreviewModelLeft.getTrackImageId()).into(holder.playlistImageLeft);
            holder.playlistNameLeft.setText(trackPreviewModelLeft.getTrackName());
        }

        if(playlistPreviewModelsRight.get(position) != null){
            TrackPreviewModel trackPreviewModelRight = playlistPreviewModelsRight.get(position);
            Picasso.get().load(trackPreviewModelRight.getTrackImageId()).into(holder.playlistImageRight);
            holder.playlistNameRight.setText(trackPreviewModelRight.getTrackName());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.max(playlistPreviewModelsLeft.size(), playlistPreviewModelsRight.size());
    }
}