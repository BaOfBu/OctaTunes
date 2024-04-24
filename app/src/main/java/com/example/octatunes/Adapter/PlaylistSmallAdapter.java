package com.example.octatunes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaylistSmallAdapter extends RecyclerView.Adapter<PlaylistSmallAdapter.ViewHolder> {

    private List<PlaylistsModel> playlistList;
    private Context context;

    public PlaylistSmallAdapter(Context context, List<PlaylistsModel> playlistList) {
        this.context = context;
        this.playlistList = playlistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_playlist_item_artist_small, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistsModel playlist = playlistList.get(position);
        Picasso.get().load(playlist.getImage()).into(holder.playlistImage);
        holder.playlistName.setText(playlist.getName());
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView playlistImage;
        TextView playlistName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.playlist_image_small);
            playlistName = itemView.findViewById(R.id.playlist_title_small);
        }
    }
}
