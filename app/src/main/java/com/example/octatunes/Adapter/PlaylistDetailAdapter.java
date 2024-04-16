package com.example.octatunes.Adapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.PlaylistDetailModel;
import com.example.octatunes.R;

import java.util.List;

public class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.ViewHolder> {

    private Context context;
    private List<PlaylistDetailModel> details;

    public PlaylistDetailAdapter(Context context, List<PlaylistDetailModel> details) {
        this.context = context;
        this.details = details;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_home_playlist_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistDetailModel item = details.get(position);
        holder.title.setText(item.getTitle());
        holder.imageView.setImageResource(item.getImageUrl());
        holder.titlePlaylist.setText(item.getPlaylistModel().getTitle());
        holder.decscription.setText(item.getPlaylistModel().getDescription());
        holder.imageViewPlaylist.setImageResource(item.getPlaylistModel().getCoverImageResId());
        holder.musicName.setText(item.getPlaylistModel().getSongs().get(0).getTitle());
        holder.numberOfMusic.setText(item.getPlaylistModel().getSongs().size() + " Songs");

        // Ensure that VideoView and MediaController are initialized correctly
        //MediaController mediaController = new MediaController(holder.itemView.getContext());
        //mediaController.setAnchorView(holder.videoMusic);
        //holder.videoMusic.setMediaController(mediaController);
        //
        //// Optional: Prepare VideoView for playing, assuming you have a valid URL
        //// You should handle this carefully, especially if loading URLs directly
        //holder.videoMusic.setVideoPath(item.getPlaylistModel().getSongs().get(0).getStreamUrl());
        //holder.videoMusic.requestFocus();
        //holder.videoMusic.start();  // You might not
        // Set additional properties or handle nested RecyclerView for PlaylistModel if needed
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;

        TextView titlePlaylist;

        ImageView imageViewPlaylist;

        TextView decscription;

        TextView musicName;
        TextView numberOfMusic;

        VideoView videoMusic;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title_container_text);
            imageView = itemView.findViewById(R.id.background_image);
            titlePlaylist = itemView.findViewById(R.id.playlist).findViewById(R.id.title);
            decscription = itemView.findViewById((R.id.playlist)).findViewById(R.id.description);
            imageViewPlaylist = itemView.findViewById(R.id.playlist).findViewById(R.id.imageView_playlist);
            musicName = itemView.findViewById(R.id.tab_layout).findViewById(R.id.music_name);
            numberOfMusic = itemView.findViewById(R.id.toolbar).findViewById(R.id.song_count);
            //videoMusic = itemView.findViewById(R.id.video_view);
        }
    }
}