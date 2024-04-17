package com.example.octatunes.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.SongAdapter;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSpotifyActivity extends AppCompatActivity {
    private List<TracksModel> songList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_playlist_spotify);

        songList = new ArrayList<>();
        songList.add(new TracksModel(1,1,"Chúng ta của tương lai", 310, ""));
        songList.add(new TracksModel(1,1,"Chúng ta của tương lai", 310, ""));
        songList.add(new TracksModel(1,1,"Chúng ta của tương lai", 310, ""));
        songList.add(new TracksModel(1,1,"Chúng ta của tương lai", 310, ""));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SongAdapter adapter = new SongAdapter(this,songList);
        recyclerView.setAdapter(adapter);

        ImageView moreOptions = findViewById(R.id.more_info);
        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating the BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(PlaylistSpotifyActivity.this);
                bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_playlist_spotify);

                // Setup listeners for each item if necessary
                View listenAdFreeView = bottomSheetDialog.findViewById(R.id.action_listen_ad_free);
                listenAdFreeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle click event
                    }
                });

                // ...setup other item listeners...

                bottomSheetDialog.show();
            }
        });
    }
}
