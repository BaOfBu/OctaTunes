package com.example.octatunes.Activity;

import android.os.Bundle;
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

        //// Create playlist sections and items for demonstration
        List<String> sectionTitles = new ArrayList<>();
        sectionTitles.add("Featured Playlists");
        sectionTitles.add("Recommended Playlists");

        List<List<PlaylistsModel>> playlistsBySection = new ArrayList<>();

        // Add your playlists for each section
        List<PlaylistsModel> featuredPlaylists = new ArrayList<>();
        // Add featured playlists...
        playlistsBySection.add(featuredPlaylists);

        List<PlaylistsModel> recommendedPlaylists = new ArrayList<>();
        // Add recommended playlists...
        playlistsBySection.add(recommendedPlaylists);

        PlaylistSectionAdapter adapter = new PlaylistSectionAdapter(sectionTitles, playlistsBySection);
        RecyclerView playlistSectionRecyclerView = findViewById(R.id.playlistSection);
        playlistSectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlistSectionRecyclerView.setAdapter(adapter);

        //// Create artist sections and items for demonstration
        //List<ArtistSectionModel> artistSection = new ArrayList<>();
        //artistSection.add(new ArtistSectionModel("Ca sĩ nổi bật", createArtistListItem()));
        //artistSection.add(new ArtistSectionModel("Ca sĩ trẻ bứt phá", createArtistListItem()));
        //
        //// Add more sections as needed
        //RecyclerView recyclerViewArtist = findViewById(R.id.artistSection);
        //ArtistSectionAdapter adapterArtist = new ArtistSectionAdapter(this, artistSection);
        //recyclerViewArtist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //recyclerViewArtist.setAdapter(adapterArtist);
        //
        //// Add items to your list
        //List<PlaylistDetailModel>  playlistDetailSection = new ArrayList<>();
        //
        //playlistDetailSection.add(new PlaylistDetailModel("Popular Single",
        //        new PlaylistModel("Playlist 1", "Description 1",R.drawable.ic_artist,createSongListItems()),
        //        R.drawable.playlist_bg));
        //
        //playlistDetailSection.add(new PlaylistDetailModel("Trending",
        //        new PlaylistModel("Playlist 2", "Description 2",R.drawable.ic_artist,createSongListItems())
        //        , R.drawable.playlist_bg));
        //
        //playlistDetailSection.add(new PlaylistDetailModel("Trending",
        //        new PlaylistModel("Playlist 3", "Description 3",R.drawable.ic_artist,createSongListItems())
        //        , R.drawable.playlist_bg));
        //
        //RecyclerView recyclerViewPlaylistDetail = findViewById(R.id.playlistDetail);
        //PlaylistDetailAdapter playlistDetailAdapter = new PlaylistDetailAdapter(this, playlistDetailSection);
        //recyclerViewPlaylistDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //recyclerViewPlaylistDetail.setAdapter(playlistDetailAdapter);

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

    private List<ArtistsModel> createArtistListItem(){
        List<ArtistsModel> artisListItems = new ArrayList<>();
        artisListItems.add(new ArtistsModel(1,"RPT-MCK","",""));
        artisListItems.add(new ArtistsModel(1,"RPT-MCK","",""));
        return artisListItems;
    }
    private List<PlaylistsModel> createPlaylistItems() {
        List<PlaylistsModel> playlistItems = new ArrayList<>();
        playlistItems.add(new PlaylistsModel(1,1,"Tên Playlist 1",""));
        playlistItems.add(new PlaylistsModel(1,1,"Tên Playlist 1",""));
        playlistItems.add(new PlaylistsModel(1,1,"Tên Playlist 1",""));
        playlistItems.add(new PlaylistsModel(1,1,"Tên Playlist 1",""));
        playlistItems.add(new PlaylistsModel(1,1,"Tên Playlist 1",""));
        playlistItems.add(new PlaylistsModel(1,1,"Tên Playlist 1",""));
        playlistItems.add(new PlaylistsModel(1,1,"Tên Playlist 1",""));
        return playlistItems;
    }
    private List<TracksModel> createSongListItems(){
        List<TracksModel> songLists = new ArrayList<>();
        songLists.add(new TracksModel(1,1,"Chúng ta của tương lai", 310, "",""));
        songLists.add(new TracksModel(1,1,"Chúng ta của tương lai", 310, "",""));
        return songLists;
    }
}