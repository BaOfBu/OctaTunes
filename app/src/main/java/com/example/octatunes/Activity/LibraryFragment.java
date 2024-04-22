package com.example.octatunes.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.LibraryAdapter;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.AlbumLibraryUserService;
import com.example.octatunes.Services.FollowerService;
import com.example.octatunes.Services.PlaylistLibraryUserService;
import com.example.octatunes.Services.UserService;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private ToggleButton tabPlaylistLibrary;
    private ToggleButton tabAlbumsLibrary;
    private ToggleButton tabArtistsLibrary;

    private RecyclerView recyclerView;

    private List<Object> items;

    private LibraryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.layout_library, container, false);

        FollowerService userService = new FollowerService();

        // Initialize ToggleButtons
        tabPlaylistLibrary = rootView.findViewById(R.id.tab_playlist_library);
        tabAlbumsLibrary = rootView.findViewById(R.id.tab_albums_library);
        tabArtistsLibrary = rootView.findViewById(R.id.tab_artists_library);

        setupToggleButtons();

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        items = new ArrayList<>();

        setupAdapter();
        getAllItems();

        return rootView;
    }

    private void setupAdapter() {
        adapter = new LibraryAdapter(getActivity(), items);
        recyclerView.setAdapter(adapter);
    }

    private void getAllPlaylists() {
        items.clear();
        PlaylistLibraryUserService playlistLibraryUserService = new PlaylistLibraryUserService();
        playlistLibraryUserService.getAllPlaylistsForCurrentUser(new PlaylistLibraryUserService.PlaylistCallback() {
            @Override
            public void onPlaylistsRetrieved(List<PlaylistsModel> playlists) {
                items.addAll(playlists);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllAlbums() {
        items.clear();
        AlbumLibraryUserService albumLibraryUserService = new AlbumLibraryUserService();
        albumLibraryUserService.getAllAlbumsForCurrentUser(new AlbumLibraryUserService.AlbumCallback() {
            @Override
            public void onAlbumsRetrieved(List<AlbumsModel> albums) {
                items.addAll(albums);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllArtist() {
        items.clear();
        FollowerService followerService = new FollowerService();
        followerService.getAllArtistsFollowedByCurrentUser(new FollowerService.ArtistListCallback() {
            @Override
            public void onArtistsRetrieved(List<ArtistsModel> artists) {
                items.addAll(artists);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllItems() {
        items.clear();
        // Get all playlists
        PlaylistLibraryUserService playlistLibraryUserService = new PlaylistLibraryUserService();
        playlistLibraryUserService.getAllPlaylistsForCurrentUser(new PlaylistLibraryUserService.PlaylistCallback() {
            @Override
            public void onPlaylistsRetrieved(List<PlaylistsModel> playlists) {
                items.addAll(playlists);
                adapter.notifyDataSetChanged();
            }
        });

        // Get all albums
        AlbumLibraryUserService albumLibraryUserService = new AlbumLibraryUserService();
        albumLibraryUserService.getAllAlbumsForCurrentUser(new AlbumLibraryUserService.AlbumCallback() {
            @Override
            public void onAlbumsRetrieved(List<AlbumsModel> albums) {
                items.addAll(albums);
                adapter.notifyDataSetChanged();
            }
        });

        // Get all artists
        FollowerService followerService = new FollowerService();
        followerService.getAllArtistsFollowedByCurrentUser(new FollowerService.ArtistListCallback() {
            @Override
            public void onArtistsRetrieved(List<ArtistsModel> artists) {
                items.addAll(artists);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setupToggleButtons() {
        setupToggleButton(tabPlaylistLibrary);
        setupToggleButton(tabAlbumsLibrary);
        setupToggleButton(tabArtistsLibrary);
    }

    private void setupToggleButton(ToggleButton toggleButton) {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    handleToggleButtonSelection(toggleButton);
                } else {
                    clearAndReload();
                    getAllItems();
                }
            }
        });
    }




    private void handleToggleButtonSelection(ToggleButton selectedButton) {
        boolean anyButtonChecked = tabPlaylistLibrary.isChecked() || tabAlbumsLibrary.isChecked() || tabArtistsLibrary.isChecked();
        if (!anyButtonChecked) {
            clearAndReload();
            getAllItems();
            return;
        }
        if (selectedButton == tabPlaylistLibrary) {
            uncheckOtherButtons(tabAlbumsLibrary, tabArtistsLibrary);
            clearAndReload();
            getAllPlaylists();
        } else if (selectedButton == tabAlbumsLibrary) {
            uncheckOtherButtons(tabPlaylistLibrary, tabArtistsLibrary);
            clearAndReload();
            getAllAlbums();
        } else if (selectedButton == tabArtistsLibrary) {
            uncheckOtherButtons(tabPlaylistLibrary, tabAlbumsLibrary);
            clearAndReload();
            getAllArtist();
        }
    }


    private void uncheckOtherButtons(ToggleButton... buttons) {
        for (ToggleButton button : buttons) {
            if (button.isChecked()) {
                button.setChecked(false);
            }
        }
    }

    private void clearAndReload() {
        items.clear();
        adapter.notifyDataSetChanged();
    }
}
