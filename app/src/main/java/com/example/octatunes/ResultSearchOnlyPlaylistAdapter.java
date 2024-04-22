package com.example.octatunes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.PlaylistsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ResultSearchOnlyPlaylistAdapter extends RecyclerView.Adapter<ResultSearchOnlyPlaylistAdapter.ViewHolder>{
    private ArrayList<PlaylistsModel> playlistPreviewModelsLeft;
    private ArrayList<PlaylistsModel> playlistPreviewModelsRight;
    private Context context;

    public ResultSearchOnlyPlaylistAdapter(List<PlaylistsModel> array, Context context) {
        playlistPreviewModelsLeft = new ArrayList<>();
        playlistPreviewModelsRight = new ArrayList<>();
        for(int i = 0; i < array.size(); i++){
            if(i % 2 == 0){
                playlistPreviewModelsLeft.add(array.get(i));
            } else {
                playlistPreviewModelsRight.add(array.get(i));
            }
        }
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
            PlaylistsModel trackPreviewModelLeft = playlistPreviewModelsLeft.get(position);
            Picasso.get().load(trackPreviewModelLeft.getImage()).into(holder.playlistImageLeft);
            holder.playlistNameLeft.setText(trackPreviewModelLeft.getName());
        }

        if(playlistPreviewModelsRight.size() <= position){
            setNullPlaylistRight(holder);
            return;
        }

        if(playlistPreviewModelsRight.get(position) != null){
            PlaylistsModel trackPreviewModelRight = playlistPreviewModelsRight.get(position);
            Picasso.get().load(trackPreviewModelRight.getImage()).into(holder.playlistImageRight);
            holder.playlistNameRight.setText(trackPreviewModelRight.getName());
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

    private void setNullPlaylistRight(ViewHolder holder){
        holder.playlistImageRight.setVisibility(View.INVISIBLE);
        holder.playlistNameRight.setVisibility(View.INVISIBLE);
    }
}