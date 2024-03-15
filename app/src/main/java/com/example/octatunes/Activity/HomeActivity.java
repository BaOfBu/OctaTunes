package com.example.octatunes.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.*;
import com.example.octatunes.Model.PlaylistModel;
import com.example.octatunes.Model.PlaylistSectionModel;
import com.example.octatunes.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView playlistRecyclerView;
    private PlaylistAdapter playlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        // Create dummy playlist sections and items for demonstration
        List<PlaylistSectionModel> playlistSections = new ArrayList<>();
        playlistSections.add(new PlaylistSectionModel("Made For Bình Lê Tuấn", createPlaylistItems()));

        // Add more sections as needed
        RecyclerView recyclerView = findViewById(R.id.playlistSection);
        PlaylistSectionAdapter adapter = new PlaylistSectionAdapter(this, playlistSections);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    // Method to create dummy playlist items for demonstration
    private List<PlaylistModel> createPlaylistItems() {
        List<PlaylistModel> playlistItems = new ArrayList<>();
        playlistItems.add(new PlaylistModel("Playlist 1", "Description 1",R.drawable.ic_spotify));
        playlistItems.add(new PlaylistModel("Playlist 2", "Description 2",R.drawable.ic_spotify));
        playlistItems.add(new PlaylistModel("Playlist 3", "Description 2",R.drawable.ic_spotify));
        playlistItems.add(new PlaylistModel("Playlist 4", "Description 2",R.drawable.ic_spotify));
        // Add more items as needed
        return playlistItems;
    }
}
