package com.example.octatunes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
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
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.TrackService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PlaylistPreviewAdapter extends RecyclerView.Adapter<PlaylistPreviewAdapter.ViewHolder> {
    private List<String> playlistPreviews;
    private List<PlaylistsModel> playlistsModels;
    private List<Integer> iconResourceIds;
    private Context context;
    private Handler handler = new Handler();
    private int focusedPosition = RecyclerView.NO_POSITION;

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
        public TextView song_count;
        public ImageView tbin_homepage_playlist_detail_menu_button;

        public ViewHolder(View itemView) {
            super(itemView);
            playlistDetailIcon = itemView.findViewById(R.id.text_title_container_icon);
            music_name = itemView.findViewById(R.id.music_name);
            text_title_container_text = itemView.findViewById(R.id.text_title_container_text);
            playlistImage = itemView.findViewById(R.id.playlist_detail_background_image);
            artistPlaylistImage = itemView.findViewById(R.id.playlist).findViewById(R.id.imageView_playlist_artist);
            playlistTitle = itemView.findViewById(R.id.playlist).findViewById(R.id.title);
            playlistDescription = itemView.findViewById(R.id.playlist).findViewById(R.id.description);
            song_count = itemView.findViewById(R.id.tbin_song_count);
            tbin_homepage_playlist_detail_menu_button=itemView.findViewById(R.id.tbin_homepage_playlist_detail_menu_button);
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
        boolean isFocused = position == focusedPosition;

        holder.playlistDetailIcon.setImageResource(iconResourceId);
        int color = ContextCompat.getColor(context, R.color.dark_gray);
        holder.playlistDetailIcon.setColorFilter(color);

        holder.text_title_container_text.setText(preview);
        holder.playlistTitle.setText(playlist.getName());
        holder.playlistDescription.setText(playlist.getDescription());
        if (!Objects.equals(playlist.getImage(), "")){
            Picasso.get().load(playlist.getImage()).into(holder.artistPlaylistImage);
        }

        /* Image changw */
        holder.itemView.setOnClickListener(v -> {
            if (playlist.getUserID()==1){
                // Open PlayListSpotifyActivity on item click
                Intent intent = new Intent(context, PlaylistSpotifyActivity.class);
                intent.putExtra("playlistItem", new Gson().toJson(playlist));
                context.startActivity(intent);
            }
        });

        TrackService trackService = new TrackService();
        Runnable updateTrackRunnable = new Runnable() {
            @Override
            public void run() {
                trackService.getTracksByPlaylistId(playlist.getPlaylistID(), new TrackService.OnTracksLoadedListener() {
                    @Override
                    public void onTracksLoaded(List<TracksModel> tracks) {
                        int trackSize = tracks.size();
                        TracksModel randomTrack = new TracksModel();
                        if (trackSize > 0) {
                            int randomIndex = new Random().nextInt(trackSize);
                            randomTrack = tracks.get(randomIndex);
                            // Set random track name
                            holder.music_name.setText(randomTrack.getName());
                        }
                        // Load image for random track using Picasso
                        trackService.getImageForTrack(randomTrack, new TrackService.OnImageLoadedListener() {
                            @Override
                            public void onImageLoaded(String imageUrl) {
                                if (imageUrl != null) {
                                    Picasso.get().load(imageUrl).into(holder.playlistImage);
                                }
                            }
                        });
                    }
                });

                // Schedule the next update after 30 seconds
                handler.postDelayed(this, 30000);
            }
        };

        // Initial execution
        handler.post(updateTrackRunnable);

        /* More Info button */
        holder.tbin_homepage_playlist_detail_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_playlist_spotify);
                ImageView bottomSheetImage = bottomSheetDialog.findViewById(R.id.tbin_playlist_bottom_sheet_image);
                TextView bottomSheetTitle = bottomSheetDialog.findViewById(R.id.tbin_playlist_bottom_sheet_title);
                if (!Objects.equals(playlist.getImage(), "")){
                    Picasso.get().load(playlist.getImage()).into(bottomSheetImage);
                }
                bottomSheetTitle.setText(playlist.getName());
                View listenAdFreeView = bottomSheetDialog.findViewById(R.id.action_listen_ad_free);
                listenAdFreeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                bottomSheetDialog.show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return playlistPreviews.size();
    }
}
