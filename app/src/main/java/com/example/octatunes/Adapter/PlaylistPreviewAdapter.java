package com.example.octatunes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.PlaylistSpotifyActivity;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class PlaylistPreviewAdapter extends RecyclerView.Adapter<PlaylistPreviewAdapter.ViewHolder> {
    private List<String> playlistPreviews;
    private List<PlaylistsModel> playlistsModels;
    private List<Integer> iconResourceIds; // List of icon resource IDs
    private Context context;

    public PlaylistPreviewAdapter(Context context, List<String> playlistPreviews, List<PlaylistsModel> playlistsModels, List<Integer> iconResourceIds) {
        this.context = context;
        this.playlistPreviews = playlistPreviews;
        this.playlistsModels = playlistsModels;
        this.iconResourceIds = iconResourceIds;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView playlistDetailIcon;
        public TextView text_title_container_text;
        public ImageView playlistImage;
        public ImageView artistPlaylistImage;
        public TextView playlistTitle;
        public TextView playlistDescription;
        public TextView music_name;

        public ViewHolder(View itemView) {
            super(itemView);
            playlistDetailIcon = itemView.findViewById(R.id.text_title_container_icon);
            music_name = itemView.findViewById(R.id.music_name);
            text_title_container_text = itemView.findViewById(R.id.text_title_container_text);
            playlistImage = itemView.findViewById(R.id.playlist_detail_background_image);
            artistPlaylistImage = itemView.findViewById(R.id.playlist).findViewById(R.id.imageView_playlist_artist);
            playlistTitle = itemView.findViewById(R.id.playlist).findViewById(R.id.title);
            playlistDescription = itemView.findViewById(R.id.playlist).findViewById(R.id.description);
        }
    }

    @NonNull
    @Override
    public PlaylistPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_playlist_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistPreviewAdapter.ViewHolder holder, int position) {
        String preview = playlistPreviews.get(position);
        PlaylistsModel playlist = playlistsModels.get(position);
        int iconResourceId = iconResourceIds.get(position);

        holder.playlistDetailIcon.setImageResource(iconResourceId);
        int color = ContextCompat.getColor(context, R.color.dark_gray);
        holder.playlistDetailIcon.setColorFilter(color);

        holder.text_title_container_text.setText(preview);
        holder.playlistTitle.setText(playlist.getName());
        holder.playlistDescription.setText(playlist.getDescription());
        if (!Objects.equals(playlist.getImage(), "")){
            Picasso.get().load(playlist.getImage()).into(holder.artistPlaylistImage);
        }

        holder.itemView.setOnClickListener(v -> {
            if (playlist.getUserID()==1){
                // Open PlayListSpotifyActivity on item click
                Intent intent = new Intent(context, PlaylistSpotifyActivity.class);
                intent.putExtra("playlistItem", new Gson().toJson(playlist));
                context.startActivity(intent);
            }

        });

    }

    @Override
    public int getItemCount() {
        return playlistPreviews.size();
    }
}
