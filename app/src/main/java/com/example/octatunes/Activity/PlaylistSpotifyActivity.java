package com.example.octatunes.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.SongAdapter;
import com.example.octatunes.Model.PlaylistLibrary_User;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.PlaylistLibraryUserService;
import com.example.octatunes.Services.TrackService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.time.LocalDateTime;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlaylistSpotifyActivity extends Fragment {
    private List<TracksModel> songList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_playlist_spotify, container, false);

        String playlistsModelJson = getArguments().getString("playlistItem");

        ImageView play_button_playlist_display=view.findViewById(R.id.play_button_playlist_display);
        play_button_playlist_display.setOnClickListener(v -> {
            // Create NowPlayingBarFragment instance
            NowPlayingBarFragment nowPlayingBarFragment = new NowPlayingBarFragment();

            // Get FragmentManager
            FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();

            // Begin transaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Find the FrameLayout using the activity's findViewById()
            FrameLayout frameLayout = ((AppCompatActivity) getContext()).findViewById(R.id.frame_layout);
            frameLayout.setVisibility(View.VISIBLE);

            // Replace fragment_container with NowPlayingBarFragment
            fragmentTransaction.replace(R.id.frame_layout, nowPlayingBarFragment);

            // Commit transaction
            fragmentTransaction.commit();
        });

        if (playlistsModelJson != null && getContext() != null) {
            PlaylistsModel playlistsModel = new Gson().fromJson(playlistsModelJson, PlaylistsModel.class);

            TextView description = view.findViewById(R.id.playlistDescriptionSpotify);
            ImageView imageView = view.findViewById(R.id.playlist_cover_image_spotify);

            if (!playlistsModel.getImage().isEmpty()) {
                Picasso.get().load(playlistsModel.getImage()).into(imageView);
            }

            description.setText(playlistsModel.getDescription());

            TrackService trackService = new TrackService();

            // Get tracks by playlist ID
            int playlistId = playlistsModel.getPlaylistID();
            trackService.getTracksByPlaylistId(playlistId, new TrackService.OnTracksLoadedListener() {
                @Override
                public void onTracksLoaded(List<TracksModel> tracks) {
                    if (getContext() != null) {
                        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSong);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        SongAdapter adapter = new SongAdapter(getContext(), tracks);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });

            ImageView backIcon = view.findViewById(R.id.back_icon);
            backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

            /* More option */
            ImageView moreOptions = view.findViewById(R.id.more_info);
            moreOptions.setOnClickListener(v -> {
                // Creating the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_playlist_spotify);

                ImageView tbin_playlist_bottom_sheet_image=bottomSheetDialog.findViewById(R.id.tbin_playlist_bottom_sheet_image);
                TextView tbin_playlist_bottom_sheet_title = bottomSheetDialog.findViewById(R.id.tbin_playlist_bottom_sheet_title);

                tbin_playlist_bottom_sheet_image.setImageDrawable(imageView.getDrawable());
                tbin_playlist_bottom_sheet_title.setText(playlistsModel.getName());

                bottomSheetDialog.show();
            });

            /* Download option */
            ImageView download = view.findViewById(R.id.download_playlist_spotify);
            download.setOnClickListener(v ->{
                // Creating the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_playlist_spotify_download);
                ImageView tbin_playlist_bottom_sheet_image=bottomSheetDialog.findViewById(R.id.download_bottom_sheet_image);
                TextView tbin_playlist_bottom_sheet_title = bottomSheetDialog.findViewById(R.id.download_bottom_sheet_title);

                tbin_playlist_bottom_sheet_image.setImageDrawable(imageView.getDrawable());
                tbin_playlist_bottom_sheet_title.setText("Want to download " + playlistsModel.getName());

                bottomSheetDialog.show();
            });

            /* Add option */
            ImageView add = view.findViewById(R.id.playlist_spotify_add_to_library);
            PlaylistLibraryUserService playlistLibraryUserService = new PlaylistLibraryUserService();
            playlistLibraryUserService.checkIfPlaylistExistsInLibrary(playlistsModel.getPlaylistID(), 2, new PlaylistLibraryUserService.OnCheckCompleteListener() {
                @Override
                public void onCheckComplete(boolean exists) {
                    if (exists) {
                        add.setImageResource(R.drawable.baseline_check_circle_24);
                    } else {
                        add.setImageResource(R.drawable.add_to_library_button_a7a7a7);
                    }
                }
            });
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playlistLibraryUserService.checkIfPlaylistExistsInLibrary(playlistsModel.getPlaylistID(), 2, new PlaylistLibraryUserService.OnCheckCompleteListener() {
                        @Override
                        public void onCheckComplete(boolean exists) {
                            if (exists) {
                                add.setImageResource(R.drawable.add_to_library_button_a7a7a7);
                                playlistLibraryUserService.removePlaylistLibraryUser(playlistsModel.getPlaylistID(), 2);
                                Toast.makeText(getContext(), "Removed from your library", Toast.LENGTH_SHORT).show();
                            } else {
                                add.setImageResource(R.drawable.baseline_check_circle_24);
                                PlaylistLibrary_User playlistLibraryUser = new PlaylistLibrary_User(playlistsModel.getPlaylistID(), 2, new Date());
                                playlistLibraryUserService.addPlaylistLibraryUser(playlistLibraryUser);
                                Toast.makeText(getContext(), "Added to your library", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        return view;
    }

}
