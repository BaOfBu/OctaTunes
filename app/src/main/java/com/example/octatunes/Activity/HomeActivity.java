package com.example.octatunes.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.ArtistSectionAdapter;
import com.example.octatunes.Adapter.ListPlaylistAdapter;
import com.example.octatunes.Adapter.PlaylistSectionAdapter;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Model.UsersModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.PlaylistService;
import com.example.octatunes.Services.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    ToggleButton toggleAll, toggleMusic;

    List<List<PlaylistsModel>> playlistsBySection = new ArrayList<>();

    List<List<ArtistsModel>> artistsBySection = new ArrayList<>();

    private PlaylistService playlistService = new PlaylistService();

    private ArtistService artistService = new ArtistService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        setupToggleButtons();

        /* Playlist section */
        List<String> playlistSectionTitles = new ArrayList<>();
        playlistSectionTitles.add("Featured Playlists");
        playlistSectionTitles.add("Recommended Playlists");

        // Fetch and display featured playlists
        getFeaturedPlaylists(playlistSectionTitles);

        // Fetch and display recommended playlists
        getRecommendedPlaylists(playlistSectionTitles);

        //List<PlaylistsModel> playlistsModels = new ArrayList<>();
        //playlistsModels.add(new PlaylistsModel(
        //        1,
        //        1,
        //        "Playlist Name",
        //        "Image",
        //        "Description"));
        //playlistService.addPlaylists(playlistsModels);

        /* Artist section */
        List<String> artistSectionTitles = new ArrayList<>();
        artistSectionTitles.add("Popular artists");
        getArtists(artistSectionTitles);

        //artistService.addArtist(new ArtistsModel(
        //    0,
        //    "Phuc Du",
        //    "viet chill rap",
        //    "https://firebasestorage.googleapis.com/v0/b/octatunes-495d2.appspot.com/o" +
        //            "/images%2Fartists%2F008.jpg?alt=media&token=32639bc8-023e-42e5-9ca3-18a5bcca1ec2"
        //));

    }
    private void setupArtistSectionAdapter(List<String> sectionTitles) {
        ArtistSectionAdapter adapter = new ArtistSectionAdapter(this,sectionTitles, artistsBySection);
        RecyclerView artistSectionRecyclerView = findViewById(R.id.artistSection);
        artistSectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        artistSectionRecyclerView.setAdapter(adapter);
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
    private void getArtists(List<String> sectionTitles) {
        artistService.getRandomArtists(6,
                new OnSuccessListener<List<ArtistsModel>>() {
                    @Override
                    public void onSuccess(List<ArtistsModel> featuredArtists) {
                        artistsBySection.add(featuredArtists);
                        setupArtistSectionAdapter(sectionTitles);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to fetch featured artists
                        Log.e("TAG", "Failed to fetch featured artists: " + e.getMessage());
                    }
                }
        );
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