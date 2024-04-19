package com.example.octatunes.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.SongAdapter;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.TrackService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSpotifyActivity extends Fragment {
    private List<TracksModel> songList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_playlist_spotify, container, false);

        String playlistsModelJson = getArguments().getString("playlistItem");

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

            ImageView moreOptions = view.findViewById(R.id.more_info);

            ImageView backIcon = view.findViewById(R.id.back_icon);
            backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

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
        }
        return view;
    }

}
