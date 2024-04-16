package com.example.octatunes.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.*;
import com.example.octatunes.Model.ArtistModel;
import com.example.octatunes.Model.ArtistSectionModel;
import com.example.octatunes.Model.PlaylistDetailModel;
import com.example.octatunes.Model.PlaylistModel;
import com.example.octatunes.Model.PlaylistSectionModel;
import com.example.octatunes.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    ToggleButton toggleAll, toggleMusic, togglePodcasts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        setupToggleButtons();

        // Create playlist sections and items for demonstration
        List<PlaylistSectionModel> playlistSections = new ArrayList<>();
        playlistSections.add(new PlaylistSectionModel("Made For Bình Lê Tuấn", createPlaylistItems()));
        playlistSections.add(new PlaylistSectionModel("Made For Nguyễn Lê Quốc Khánh", createPlaylistItems()));

        // Add more sections as needed
        RecyclerView recyclerViewPlaylist = findViewById(R.id.playlistSection);
        PlaylistSectionAdapter adapterPlaylist = new PlaylistSectionAdapter(this, playlistSections);
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewPlaylist.setAdapter(adapterPlaylist);

        // Create artist sections and items for demonstration
        List<ArtistSectionModel> artistSection = new ArrayList<>();
        artistSection.add(new ArtistSectionModel("Ca sĩ nổi bật", createArtistListItem()));
        artistSection.add(new ArtistSectionModel("Ca sĩ trẻ bứt phá", createArtistListItem()));

        // Add more sections as needed
        RecyclerView recyclerViewArtist = findViewById(R.id.artistSection);
        ArtistSectionAdapter adapterArtist = new ArtistSectionAdapter(this, artistSection);
        recyclerViewArtist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewArtist.setAdapter(adapterArtist);

        // Add items to your list
        List<PlaylistDetailModel>  playlistDetailSection = new ArrayList<>();
        playlistDetailSection.add(new PlaylistDetailModel("Popular Single",
                new PlaylistModel("Playlist 1", "Description 1",R.drawable.ic_artist),
                R.drawable.playlist_bg));
        playlistDetailSection.add(new PlaylistDetailModel("Trending",
                new PlaylistModel("Playlist 2", "Description 2",R.drawable.ic_artist)
                , R.drawable.playlist_bg)); // Replace R.drawable.playlist_bg with your actual image resource
        // Add more items as needed
        RecyclerView recyclerViewPlaylistDetail = findViewById(R.id.playlistDetail);
        PlaylistDetailAdapter playlistDetailAdapter = new PlaylistDetailAdapter(this, playlistDetailSection);
        recyclerViewPlaylistDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewPlaylistDetail.setAdapter(playlistDetailAdapter);

    }
    private void setupToggleButtons() {
        toggleAll = findViewById(R.id.navigation_section).findViewById(R.id.tab_all);
        toggleMusic = findViewById(R.id.navigation_section).findViewById(R.id.tab_music);

        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            if (isChecked) {
                if (buttonView == toggleAll) {
                    toggleMusic.setChecked(false);
                    updateNestedScrollViewContent("all");
                } else if (buttonView == toggleMusic) {
                    toggleAll.setChecked(false);
                    updateNestedScrollViewContent("music");
                }
            }
        };

        toggleAll.setOnCheckedChangeListener(listener);
        toggleMusic.setOnCheckedChangeListener(listener);
    }
    private void updateNestedScrollViewContent(String category) {
        switch (category) {
            case "all":
                showAllContent();
                break;
            case "music":
                showMusicContent();
                break;
        }
    }
    private void showAllContent() {
        // Show all playlists and artists
        findViewById(R.id.playlistSection).setVisibility(View.VISIBLE);
        findViewById(R.id.artistSection).setVisibility(View.VISIBLE);
        findViewById(R.id.playlistDetail).setVisibility(View.VISIBLE);
    }
    void showMusicContent() {
        // Show only music related content
        findViewById(R.id.playlistSection).setVisibility(View.GONE);
        findViewById(R.id.artistSection).setVisibility(View.GONE);
        findViewById(R.id.playlistDetail).setVisibility(View.VISIBLE);
    }
    private List<ArtistModel> createArtistListItem(){
        List<ArtistModel> artisListItems = new ArrayList<>();
        artisListItems.add(new ArtistModel(R.drawable.ic_artist,"RPT MCK"));
        artisListItems.add(new ArtistModel(R.drawable.ic_artist,"RPT MCK"));
        artisListItems.add(new ArtistModel(R.drawable.ic_artist,"RPT MCK"));
        artisListItems.add(new ArtistModel(R.drawable.ic_artist,"RPT MCK"));
        return artisListItems;
    }

    // Method to create playlist items for demonstration
    private List<PlaylistModel> createPlaylistItems() {
        List<PlaylistModel> playlistItems = new ArrayList<>();
        playlistItems.add(new PlaylistModel("Playlist 1", "Description 1",R.drawable.ic_spotify));
        playlistItems.add(new PlaylistModel("Playlist 2", "Description 2",R.drawable.ic_spotify));
        playlistItems.add(new PlaylistModel("Playlist 3", "Description 2",R.drawable.ic_spotify));
        playlistItems.add(new PlaylistModel("Playlist 4", "Description 2",R.drawable.ic_spotify));
        return playlistItems;
    }
}