package com.example.octatunes.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.octatunes.NonScrollLinearLayoutManager;

import com.example.octatunes.Adapter.*;
import com.example.octatunes.Model.ArtistItemModel;
import com.example.octatunes.Model.ArtistSectionModel;
import com.example.octatunes.Model.PlaylistModel;
import com.example.octatunes.Model.PlaylistSectionModel;
import com.example.octatunes.NonScrollLinearLayoutManager;
import com.example.octatunes.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView playlistRecyclerView;
    private PlaylistAdapter playlistAdapter;

    private  ArtistItemAdapter artistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        // Create playlist sections and items for demonstration
        List<PlaylistSectionModel> playlistSections = new ArrayList<>();
        playlistSections.add(new PlaylistSectionModel("Made For Bình Lê Tuấn", createPlaylistItems()));
        playlistSections.add(new PlaylistSectionModel("Made For Nguyễn Lê Quốc Khánh", createPlaylistItems()));

        // Add more sections as needed
        RecyclerView recyclerViewPlaylist = findViewById(R.id.playlistSection);
        PlaylistSectionAdapter adapterPlaylist = new PlaylistSectionAdapter(this, playlistSections);
        recyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // recyclerViewPlaylist.setLayoutManager(new NonScrollLinearLayoutManager(this));
        recyclerViewPlaylist.setAdapter(adapterPlaylist);

        // Create artist sections and items for demonstration
        List<ArtistSectionModel> artistSection = new ArrayList<>();
        artistSection.add(new ArtistSectionModel("Ca sĩ nổi bật", createArtistListItem()));
        artistSection.add(new ArtistSectionModel("Ca sĩ trẻ bứt phá", createArtistListItem()));

        // Add more sections as needed
        RecyclerView recyclerViewArtist = findViewById(R.id.artistSection);
        ArtistSectionAdapter adapterArtist = new ArtistSectionAdapter(this, artistSection);
        recyclerViewArtist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // .setLayoutManager(new NonScrollLinearLayoutManager(this));

        recyclerViewArtist.setAdapter(adapterArtist);
    }

    private List<ArtistItemModel> createArtistListItem(){
        List<ArtistItemModel> artisListItems = new ArrayList<>();
        artisListItems.add(new ArtistItemModel(R.drawable.ic_artist,"RPT MCK"));
        artisListItems.add(new ArtistItemModel(R.drawable.ic_artist,"RPT MCK"));
        artisListItems.add(new ArtistItemModel(R.drawable.ic_artist,"RPT MCK"));
        artisListItems.add(new ArtistItemModel(R.drawable.ic_artist,"RPT MCK"));
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
