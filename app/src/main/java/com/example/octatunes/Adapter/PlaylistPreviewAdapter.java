package com.example.octatunes.Adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.NowPlayingBarFragment;
import com.example.octatunes.Activity.PlaylistSpotifyActivity;
import com.example.octatunes.Model.PlaylistLibrary_User;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.PlaylistLibraryUserService;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.Services.UserService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
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

    private ImageView playPlaylist = null;


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
        public ImageView add_button_preview_playlist;
        public ImageView play_button_home_playlist_preview;

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
            play_button_home_playlist_preview = itemView.findViewById(R.id.play_button_home_playlist_preview);
            add_button_preview_playlist = itemView.findViewById(R.id.add_button_preview_playlist);
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

        PlaylistLibraryUserService playlistLibraryUserService = new PlaylistLibraryUserService();

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
            PlaylistSpotifyActivity fragment = new PlaylistSpotifyActivity();
            Bundle bundle = new Bundle();
            bundle.putString("playlistItem", new Gson().toJson(playlist));
            fragment.setArguments(bundle);

            FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        TrackService trackService = new TrackService();
        final TracksModel[] tracksModel = {new TracksModel()};
        final String[] trackImg = new String[1];
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
                            tracksModel[0] = randomTrack;
                            holder.music_name.setText(randomTrack.getName());
                            holder.song_count.setText(String.valueOf(trackSize) + " Songs");
                        }
                        // Load image for random track using Picasso
                        trackService.getImageForTrack(randomTrack, new TrackService.OnImageLoadedListener() {
                            @Override
                            public void onImageLoaded(String imageUrl) {
                                if (imageUrl != null) {
                                    trackImg[0] = imageUrl;
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

        /* Play button */
        /*
        holder.play_button_home_playlist_preview.setTag(R.drawable.ic_play_circle);
        holder.play_button_home_playlist_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the resource IDs of the drawables
                int playDrawableId = R.drawable.ic_play_circle;
                int pauseDrawableId = R.drawable.baseline_pause_circle_24;

                // Get the current drawable resource ID of the button
                int currentDrawableId = (Integer) holder.play_button_home_playlist_preview.getTag();

                // Check if the current drawable is the play drawable
                if (currentDrawableId == playDrawableId) {
                    if (playPlaylist!=null){
                        playPlaylist.setImageResource(playDrawableId);
                        playPlaylist.setTag(playDrawableId);
                    }
                    holder.play_button_home_playlist_preview.setImageResource(pauseDrawableId);
                    holder.play_button_home_playlist_preview.setTag(pauseDrawableId);
                    playPlaylist = holder.play_button_home_playlist_preview;
                } else {
                    playPlaylist = null;
                    holder.play_button_home_playlist_preview.setImageResource(playDrawableId);
                    holder.play_button_home_playlist_preview.setTag(playDrawableId);
                }

                NowPlayingBarFragment nowPlayingBarFragment = NowPlayingBarFragment.newInstance("param1", "param2", tracksModel[0],trackImg[0]);
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FrameLayout frameLayout = ((AppCompatActivity) context).findViewById(R.id.frame_layout);
                frameLayout.setVisibility(View.VISIBLE);
                fragmentTransaction.replace(R.id.frame_layout, nowPlayingBarFragment);
                fragmentTransaction.commit();
            }
        });
        */

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

        /* Add button */
        // Create an instance of the UserService
        UserService userService = new UserService();
        // Fetch the userId using the UserService
        userService.getCurrentUserId(new UserService.UserIdCallback() {
            @Override
            public void onUserIdRetrieved(int userId) {
                // Use the userId obtained here
                playlistLibraryUserService.checkIfPlaylistExistsInLibrary(playlist.getPlaylistID(), userId, new PlaylistLibraryUserService.OnCheckCompleteListener() {
                    @Override
                    public void onCheckComplete(boolean exists) {
                        if (exists) {
                            holder.add_button_preview_playlist.setImageResource(R.drawable.baseline_check_circle_24);
                        } else {
                            holder.add_button_preview_playlist.setImageResource(R.drawable.add_to_library_button_a7a7a7);
                        }
                    }
                });

                holder.add_button_preview_playlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playlistLibraryUserService.checkIfPlaylistExistsInLibrary(playlist.getPlaylistID(), userId, new PlaylistLibraryUserService.OnCheckCompleteListener() {
                            @Override
                            public void onCheckComplete(boolean exists) {
                                if (exists) {
                                    holder.add_button_preview_playlist.setImageResource(R.drawable.add_to_library_button_a7a7a7);
                                    playlistLibraryUserService.removePlaylistLibraryUser(playlist.getPlaylistID(), userId);
                                    Toast.makeText(context, "Removed from your library", Toast.LENGTH_SHORT).show();
                                } else {
                                    holder.add_button_preview_playlist.setImageResource(R.drawable.baseline_check_circle_24);
                                    PlaylistLibrary_User playlistLibraryUser = new PlaylistLibrary_User(playlist.getPlaylistID(), userId, new Date());
                                    playlistLibraryUserService.addPlaylistLibraryUser(playlistLibraryUser);
                                    Toast.makeText(context, "Added to your library", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistPreviews.size();
    }
}
