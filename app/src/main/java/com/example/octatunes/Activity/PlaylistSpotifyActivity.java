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

import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
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

        if (playlistsModelJson != null) {
            // Deserialize the PlaylistsModel from JSON
            PlaylistsModel playlistsModel = new Gson().fromJson(playlistsModelJson, PlaylistsModel.class);

            TextView description = view.findViewById(R.id.playlistDescriptionSpotify);
            ImageView imageView = view.findViewById(R.id.playlist_cover_image_spotify);

            if (!playlistsModel.getImage().isEmpty()) {
                Picasso.get().load(playlistsModel.getImage()).into(imageView);
            }

            description.setText(playlistsModel.getDescription());
        }

        songList = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        //SongAdapter adapter = new SongAdapter(requireContext(), songList);
        //recyclerView.setAdapter(adapter);

        ImageView moreOptions = view.findViewById(R.id.more_info);

        ImageView backIcon = view.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

        moreOptions.setOnClickListener(v -> {
            // Creating the BottomSheetDialog
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_playlist_spotify);

            // Setup listeners for each item if necessary
            View listenAdFreeView = bottomSheetDialog.findViewById(R.id.action_listen_ad_free);
            listenAdFreeView.setOnClickListener(v1 -> {
                // Handle click event
            });

            bottomSheetDialog.show();
        });

        return view;
    }
}
