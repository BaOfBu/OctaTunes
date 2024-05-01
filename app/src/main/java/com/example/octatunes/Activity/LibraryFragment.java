package com.example.octatunes.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.LibraryAdapter;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.AlbumLibraryUserService;
import com.example.octatunes.Services.FollowerService;
import com.example.octatunes.Services.PlaylistLibraryUserService;
import com.example.octatunes.Services.UserService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        MainActivity.lastFrag=this;

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

        // Liked Song click
        setUpLikedSong(rootView);
        setUpDeviceSong(rootView);

        return rootView;
    }

    private void setUpLikedSong(View rootView) {
        LinearLayout library_liked_song=rootView.findViewById(R.id.library_liked_song);
        library_liked_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikedSongFragment fragment = new LikedSongFragment();
                Bundle bundle = new Bundle();
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void setUpDeviceSong(View rootView){
        LinearLayout library_device_song = rootView.findViewById(R.id.library_device_song);
        library_device_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceSongFragment fragment = new DeviceSongFragment();
                Bundle bundle = new Bundle();
                fragment.setArguments(bundle);

                FragmentTransaction transaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void setupAdapter() {
        adapter = new LibraryAdapter(getActivity(), items);
        recyclerView.setAdapter(adapter);
    }

    private void getAllPlaylists() {
        clearAndReload();
        PlaylistLibraryUserService playlistLibraryUserService = new PlaylistLibraryUserService();
        playlistLibraryUserService.getAllPlaylistsForCurrentUser(new PlaylistLibraryUserService.PlaylistCallback() {
            @Override
            public void onPlaylistsRetrieved(List<PlaylistsModel> playlists) {
                Random random = new Random();

                for (PlaylistsModel playlist : playlists) {
                    // Generate a random index within the range of the list size
                    int randomIndex = random.nextInt(items.size() + 1);

                    // Insert the artist at the random index
                    items.add(randomIndex, playlist);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllAlbums() {
        clearAndReload();
        AlbumLibraryUserService albumLibraryUserService = new AlbumLibraryUserService();
        albumLibraryUserService.getAllAlbumsForCurrentUser(new AlbumLibraryUserService.AlbumCallback() {
            @Override
            public void onAlbumsRetrieved(List<AlbumsModel> albums) {
                Random random = new Random();

                for (AlbumsModel album : albums) {
                    // Generate a random index within the range of the list size
                    int randomIndex = random.nextInt(items.size() + 1);

                    // Insert the artist at the random index
                    items.add(randomIndex, album);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllArtist() {
        clearAndReload();
        FollowerService followerService = new FollowerService();
        followerService.getAllArtistsFollowedByCurrentUser(new FollowerService.ArtistListCallback() {
            @Override
            public void onArtistsRetrieved(List<ArtistsModel> artists) {
                Random random = new Random();

                for (ArtistsModel artist : artists) {
                    // Generate a random index within the range of the list size
                    int randomIndex = random.nextInt(items.size() + 1);

                    // Insert the artist at the random index
                    items.add(randomIndex, artist);
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getAllItems() {
        clearAndReload();
        // Get all playlists
        PlaylistLibraryUserService playlistLibraryUserService = new PlaylistLibraryUserService();
        playlistLibraryUserService.getAllPlaylistsForCurrentUser(new PlaylistLibraryUserService.PlaylistCallback() {
            @Override
            public void onPlaylistsRetrieved(List<PlaylistsModel> playlists) {
                Random random = new Random();

                for (PlaylistsModel playlist : playlists) {
                    // Generate a random index within the range of the list size
                    int randomIndex = random.nextInt(items.size() + 1);

                    // Insert the artist at the random index
                    items.add(randomIndex, playlist);
                }

                adapter.notifyDataSetChanged();
            }
        });

        // Get all albums
        AlbumLibraryUserService albumLibraryUserService = new AlbumLibraryUserService();
        albumLibraryUserService.getAllAlbumsForCurrentUser(new AlbumLibraryUserService.AlbumCallback() {
            @Override
            public void onAlbumsRetrieved(List<AlbumsModel> albums) {
                Random random = new Random();

                for (AlbumsModel album : albums) {
                    // Generate a random index within the range of the list size
                    int randomIndex = random.nextInt(items.size() + 1);

                    // Insert the artist at the random index
                    items.add(randomIndex, album);
                }

                adapter.notifyDataSetChanged();
            }
        });

        // Get all artists
        FollowerService followerService = new FollowerService();
        followerService.getAllArtistsFollowedByCurrentUser(new FollowerService.ArtistListCallback() {
            @Override
            public void onArtistsRetrieved(List<ArtistsModel> artists) {
                Random random = new Random();

                for (ArtistsModel artist : artists) {
                    // Generate a random index within the range of the list size
                    int randomIndex = random.nextInt(items.size() + 1);

                    // Insert the artist at the random index
                    items.add(randomIndex, artist);
                }

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
                boolean anyToggleButtonChecked = false; // Track if any toggle button is checked

                if (isChecked) {
                    anyToggleButtonChecked = true; // Set the flag if any button is checked
                    clearAndReload();
                    handleToggleButtonSelection(toggleButton);
                } else {
                    // Check if any toggle button is still checked
                    if (tabPlaylistLibrary.isChecked() || tabAlbumsLibrary.isChecked() || tabArtistsLibrary.isChecked()) {
                        anyToggleButtonChecked = true;
                    }

                    if (!anyToggleButtonChecked) {
                        clearAndReload();
                        getAllItems();
                    }
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
