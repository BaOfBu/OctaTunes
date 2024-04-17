package com.example.octatunes.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.ListPlaylistAdapter;
import com.example.octatunes.Adapter.PlaylistSectionAdapter;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Model.UsersModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.PlaylistService;
import com.example.octatunes.Services.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    ToggleButton toggleAll, toggleMusic;

    List<List<PlaylistsModel>> playlistsBySection = new ArrayList<>();

    private PlaylistService playlistService = new PlaylistService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        setupToggleButtons();

        /* Playlist section */
        List<String> sectionTitles = new ArrayList<>();
        sectionTitles.add("Featured Playlists");
        sectionTitles.add("Recommended Playlists");

        // Fetch and display featured playlists
        getFeaturedPlaylists(sectionTitles);

        // Fetch and display recommended playlists
        getRecommendedPlaylists(sectionTitles);

        /* Artist section */
    }
    private void setupPlaylistSectionAdapter(List<String> sectionTitles) {
        PlaylistSectionAdapter adapter = new PlaylistSectionAdapter(this,sectionTitles, playlistsBySection);
        RecyclerView playlistSectionRecyclerView = findViewById(R.id.playlistSection);
        playlistSectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlistSectionRecyclerView.setAdapter(adapter);
    }
    private void getFeaturedPlaylists(List<String> sectionTitles) {
        playlistService.getRandomPlaylists(6).thenAccept(playlists -> {
            List<PlaylistsModel> featuredPlaylists = new ArrayList<>();
            for (PlaylistsModel playlist : playlists) {
                featuredPlaylists.add(playlist);
            }
            playlistsBySection.add(featuredPlaylists);
            setupPlaylistSectionAdapter(sectionTitles);
        }).exceptionally(throwable -> {
            // Handle exceptions if playlist retrieval fails
            Log.e("TAG", "Failed to fetch featured playlists: " + throwable.getMessage());
            return null;
        });
    }
    private void getRecommendedPlaylists(List<String> sectionTitles) {
        playlistService.getRandomPlaylists(5).thenAccept(playlists -> {
            List<PlaylistsModel> recommendedPlaylists = new ArrayList<>();
            for (PlaylistsModel playlist : playlists) {
                recommendedPlaylists.add(playlist);
            }
            playlistsBySection.add(recommendedPlaylists);
            setupPlaylistSectionAdapter(sectionTitles);
        }).exceptionally(throwable -> {
            // Handle exceptions if playlist retrieval fails
            Log.e("TAG", "Failed to fetch recommended playlists: " + throwable.getMessage());
            return null;
        });
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
        findViewById(R.id.playlistSection).setVisibility(View.GONE);
        findViewById(R.id.artistSection).setVisibility(View.GONE);
        findViewById(R.id.playlistDetail).setVisibility(View.VISIBLE);
    }

}