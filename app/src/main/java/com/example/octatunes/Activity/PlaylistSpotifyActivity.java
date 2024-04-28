package com.example.octatunes.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
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
import com.example.octatunes.FragmentListener;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.PlaylistLibrary_User;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.PlaylistLibraryUserService;
import com.example.octatunes.Services.PlaylistService;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.Services.UserService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.time.LocalDateTime;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlaylistSpotifyActivity extends Fragment {
    private List<TracksModel> songList;

    private FragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }
    private void sendSignalToMainActivity(int trackID, int playlistID, int albumID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived(trackID, playlistID, albumID, from, belong, mode);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        MainActivity.lastFrag=this;

        View view = inflater.inflate(R.layout.layout_playlist_spotify, container, false);

        String playlistsModelJson = getArguments().getString("playlistItem");

        if (playlistsModelJson != null && getContext() != null) {
            PlaylistsModel playlistsModel = new Gson().fromJson(playlistsModelJson, PlaylistsModel.class);

            TextView description = view.findViewById(R.id.playlistDescriptionSpotify);
            ImageView imageView = view.findViewById(R.id.playlist_cover_image_spotify);
            TextView creator = view.findViewById(R.id.creator_name);

            if (!playlistsModel.getImage().isEmpty()) {
                Picasso.get().load(playlistsModel.getImage()).into(imageView);
            }

            description.setText(playlistsModel.getDescription());

            PlaylistService playlistService = new PlaylistService();
            playlistService.getUsernameOfPlaylistCreator(playlistsModel)
                    .thenAccept(username -> {
                        if (username != null) {
                            if (username.equals("Spotify")) {
                                int randomNumber = (int) (Math.random() * 100) + 1;
                                if (randomNumber % 2 == 0) {
                                    String madeByText = "<font color='#a7a7a7'>Made for </font>" + "Binh Le Tuan";
                                    creator.setText(Html.fromHtml(madeByText));
                                } else {
                                    creator.setText(username);
                                }

                            } else {
                                String madeByText = "<font color='#a7a7a7'>Made by </font>" + username;
                                creator.setText(Html.fromHtml(madeByText));
                            }
                        } else {
                            Log.e("Error", "Username is null");
                        }
                    })
                    .exceptionally(throwable -> {
                        // Handle exceptions here
                        Log.e("Error", "Failed to get username", throwable);
                        return null;
                    });


            TrackService trackService = new TrackService();
            List<TracksModel> allTracks = new ArrayList<>();

            // Get tracks by playlist ID
            int playlistId = playlistsModel.getPlaylistID();
            trackService.getTracksByPlaylistId(playlistId).thenAccept(tracks -> {
                String mode = "sequencePlay";
                int trackFirstId = tracks.get(0).getTrackID();
                int albumId = -1;
                String from =  "PLAYING FROM PLAYLIST";
                String belong = playlistsModel.getName();
                ImageView playButton = view.findViewById(R.id.play_button_playlist_display);
                playButton.setOnClickListener(v -> {
                    sendSignalToMainActivity(trackFirstId, playlistId, albumId, from, belong, mode);
                });

                /* Adapter for track */
                allTracks.addAll(tracks);
                if (getContext() != null) {
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSong);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    SongAdapter adapter = new SongAdapter(getContext(), tracks,listener,playlistsModel);
                    recyclerView.setAdapter(adapter);
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

                ImageView tbin_playlist_bottom_sheet_image = bottomSheetDialog.findViewById(R.id.tbin_playlist_bottom_sheet_image);
                TextView tbin_playlist_bottom_sheet_title = bottomSheetDialog.findViewById(R.id.tbin_playlist_bottom_sheet_title);

                tbin_playlist_bottom_sheet_image.setImageDrawable(imageView.getDrawable());
                tbin_playlist_bottom_sheet_title.setText(playlistsModel.getName());

                bottomSheetDialog.show();
            });

            /* Download option */
            ImageView download = view.findViewById(R.id.download_playlist_spotify);
            download.setOnClickListener(v -> {
                // Creating the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_playlist_spotify_download);
                ImageView tbin_playlist_bottom_sheet_image = bottomSheetDialog.findViewById(R.id.download_bottom_sheet_image);
                TextView tbin_playlist_bottom_sheet_title = bottomSheetDialog.findViewById(R.id.download_bottom_sheet_title);

                tbin_playlist_bottom_sheet_image.setImageDrawable(imageView.getDrawable());
                tbin_playlist_bottom_sheet_title.setText("Want to download " + playlistsModel.getName());

                bottomSheetDialog.show();
            });

            /* Add option */
            ImageView add = view.findViewById(R.id.playlist_spotify_add_to_library);
            PlaylistLibraryUserService playlistLibraryUserService = new PlaylistLibraryUserService();

            // Create an instance of the UserService
            UserService userService = new UserService();

            // Fetch the userId using the UserService
            userService.getCurrentUserId(new UserService.UserIdCallback() {
                @Override
                public void onUserIdRetrieved(int userId) {
                    // Use the userId obtained here
                    playlistLibraryUserService.checkIfPlaylistExistsInLibrary(playlistsModel.getPlaylistID(), userId, new PlaylistLibraryUserService.OnCheckCompleteListener() {
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
                            playlistLibraryUserService.checkIfPlaylistExistsInLibrary(playlistsModel.getPlaylistID(), userId, new PlaylistLibraryUserService.OnCheckCompleteListener() {
                                @Override
                                public void onCheckComplete(boolean exists) {
                                    if (exists) {
                                        add.setImageResource(R.drawable.add_to_library_button_a7a7a7);
                                        playlistLibraryUserService.removePlaylistLibraryUser(playlistsModel.getPlaylistID(), userId);
                                        Toast.makeText(getContext(), "Removed from your library", Toast.LENGTH_SHORT).show();
                                    } else {
                                        add.setImageResource(R.drawable.baseline_check_circle_24);
                                        PlaylistLibrary_User playlistLibraryUser = new PlaylistLibrary_User(playlistsModel.getPlaylistID(), userId, new Date());
                                        playlistLibraryUserService.addPlaylistLibraryUser(playlistLibraryUser);
                                        Toast.makeText(getContext(), "Added to your library", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });

        }
        return view;
    }
}
