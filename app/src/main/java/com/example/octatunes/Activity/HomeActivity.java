package com.example.octatunes.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.ArtistSectionAdapter;
import com.example.octatunes.Adapter.ListPlaylistAdapter;
import com.example.octatunes.Adapter.PlaylistPreviewAdapter;
import com.example.octatunes.Adapter.PlaylistSectionAdapter;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.Playlist_TracksModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Model.UsersModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.AlbumService;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.PlaylistService;
import com.example.octatunes.Services.PlaylistTrackService;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.Services.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HomeActivity extends Fragment {
    ToggleButton toggleAll, toggleMusic;

    List<List<PlaylistsModel>> playlistsBySection = new ArrayList<>();

    List<List<ArtistsModel>> artistsBySection = new ArrayList<>();

    List<PlaylistsModel> playlistPreviewBySection = new ArrayList<>();

    private PlaylistService playlistService = new PlaylistService();

    private ArtistService artistService = new ArtistService();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_home, container, false);

        setupToggleButtons(rootView);

        setupUI(rootView);

        return rootView;

    }
    private void setupArtistSectionAdapter(List<String> sectionTitles) {
        ArtistSectionAdapter adapter = new ArtistSectionAdapter(getContext(),sectionTitles, artistsBySection);
        RecyclerView artistSectionRecyclerView = getView().findViewById(R.id.artistSection);
        artistSectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artistSectionRecyclerView.setAdapter(adapter);
    }
    private void setupPlaylistSectionAdapter(List<String> sectionTitles) {
        PlaylistSectionAdapter adapter = new PlaylistSectionAdapter(getContext(),sectionTitles, playlistsBySection);
        RecyclerView playlistSectionRecyclerView =  getView().findViewById(R.id.playlistSection);
        playlistSectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistSectionRecyclerView.setAdapter(adapter);
    }
    private void setupPlaylistPreviewSectionAdapter(List<String> sectionTitles, List<Integer> playlistPreviewIcon) {
        PlaylistPreviewAdapter adapter = new PlaylistPreviewAdapter(getContext(),sectionTitles, playlistPreviewBySection,playlistPreviewIcon);
        RecyclerView playlistSectionRecyclerView =  getView().findViewById(R.id.playlistDetail);
        playlistSectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                        Log.e("TAG", "Failed to fetch featured artists: " + e.getMessage());
                    }
                }
        );
    }
    private void getPlaylistMusicPreview(List<String> sectionTitles,List<Integer> playlistPreviewIcon){
        playlistService.getRandomPlaylists(sectionTitles.size()).thenAccept(playlists -> {
            for (PlaylistsModel playlist : playlists) {
                playlistPreviewBySection.add(playlist);
            }
            setupPlaylistPreviewSectionAdapter(sectionTitles,playlistPreviewIcon);
        }).exceptionally(throwable -> {
            Log.e("TAG", "Failed to fetch recommended playlists: " + throwable.getMessage());
            return null;
        });
    }
    private void setupToggleButtons(View rootView) {
        toggleAll = rootView.findViewById(R.id.navigation_section).findViewById(R.id.tab_all);
        toggleMusic = rootView.findViewById(R.id.navigation_section).findViewById(R.id.tab_music);
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
        getView().findViewById(R.id.playlistSection).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.artistSection).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.playlistDetail).setVisibility(View.VISIBLE);
        NestedScrollView scrollView = getView().findViewById(R.id.homeScroll);
        scrollView.scrollTo(0, 0);
    }
    private void showMusicContent() {
        // Show only music-related content
        getView().findViewById(R.id.playlistSection).setVisibility(View.GONE);
        getView().findViewById(R.id.artistSection).setVisibility(View.GONE);
        getView().findViewById(R.id.playlistDetail).setVisibility(View.VISIBLE);
        NestedScrollView scrollView = getView().findViewById(R.id.homeScroll);
        scrollView.scrollTo(0, 0);
    }
    private void setupUI(View rootView) {
        /* Playlist section */
        List<String> playlistSectionTitles = new ArrayList<>();
        playlistSectionTitles.add("Featured Playlists");
        playlistSectionTitles.add("Recommended Playlists");

        // Fetch and display featured playlists
        getFeaturedPlaylists(playlistSectionTitles);

        // Fetch and display recommended playlists
        getRecommendedPlaylists(playlistSectionTitles);

        /* Artist section */
        List<String> artistSectionTitles = new ArrayList<>();
        artistSectionTitles.add("Popular artists");
        getArtists(artistSectionTitles);

        /* Playlist Detail Section */
        List<String> playlistPreviews = new ArrayList<>();
        playlistPreviews.add("Trending");
        playlistPreviews.add("Popular Playlist");
        playlistPreviews.add("Newest Playlist");
        playlistPreviews.add("Popular Album");

        List<Integer> playlistPreviewIcon = new ArrayList<>();
        playlistPreviewIcon.add(R.drawable.baseline_trending_up_24);
        playlistPreviewIcon.add(R.drawable.baseline_favorite_border_24);
        playlistPreviewIcon.add(R.drawable.baseline_perm_identity_24);
        playlistPreviewIcon.add(R.drawable.baseline_diamond_24);
        getPlaylistMusicPreview(playlistPreviews, playlistPreviewIcon);
    }
}