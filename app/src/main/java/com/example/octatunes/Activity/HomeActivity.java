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

public class HomeActivity extends AppCompatActivity {
    ToggleButton toggleAll, toggleMusic;

    List<List<PlaylistsModel>> playlistsBySection = new ArrayList<>();

    List<List<ArtistsModel>> artistsBySection = new ArrayList<>();

    List<PlaylistsModel> playlistPreviewBySection = new ArrayList<>();

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
        getPlaylistMusicPreview(playlistPreviews,playlistPreviewIcon);

        /* Album */
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //
        //Date releaseDate = null;
        //try {
        //    releaseDate = dateFormat.parse("2024-02-02");
        //} catch (ParseException e) {
        //    e.printStackTrace();
        //}
        //
        //AlbumService albumService = new AlbumService();
        //
        //AlbumsModel newAlbum = new AlbumsModel(
        //    1,2,"Sớm Mai",releaseDate,
        //    "https://firebasestorage.googleapis.com/v0/" +
        //            "b/octatunes-495d2.appspot.com/o/images%2Falbums%2F038.jpg?alt=media&token=95f765d3-eedc-47f8-afa6-5eb15e7aec6d"
        //);
        //
        //albumService.addAlbum(newAlbum);

        /* Playlist_Tracks */
        //PlaylistTrackService playlistTrackService = new PlaylistTrackService();
        //
        //// Define the range of TrackIDs
        //int minTrackID = 1;
        //int maxTrackID = 58;
        //
        //// Create a list to store the track IDs within the specified range
        //List<Integer> trackIDs = new ArrayList<>();
        //for (int i = minTrackID; i <= maxTrackID; i++) {
        //    trackIDs.add(i);
        //}
        //
        //// Shuffle the list to randomize the order of track IDs
        //Collections.shuffle(trackIDs);
        //
        //// Select the first 10 track IDs from the shuffled list
        //List<Integer> selectedTrackIDs = trackIDs.subList(0, 18);
        //
        //// Add each selected track to the playlist
        //for (Integer trackID : selectedTrackIDs) {
        //    Playlist_TracksModel newPlaylistTrack = new Playlist_TracksModel(10, trackID, 0);
        //    playlistTrackService.addPlaylistTrack(newPlaylistTrack);
        //}

        /* Tracks */
        //TrackService trackService = new TrackService();
        //TracksModel newTrack = new TracksModel();
        //newTrack.setTrackID(1);
        //newTrack.setAlubumID(28);
        //newTrack.setName("Sớm Mai");
        //newTrack.setDuration(180);
        //newTrack.setFile("https://firebasestorage.googleapis.com/v0/b/octatunes-495d2.appspot.com/o/musics%2F054_SomMai_Ronboogz.mp3?alt=media&token=bd524d07-ae8d-4ab5-b447-b895e5c020fa");
        //
        //trackService.addTrack(newTrack);
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
    private void setupPlaylistPreviewSectionAdapter(List<String> sectionTitles, List<Integer> playlistPreviewIcon) {
        PlaylistPreviewAdapter adapter = new PlaylistPreviewAdapter(this,sectionTitles, playlistPreviewBySection,playlistPreviewIcon);
        RecyclerView playlistSectionRecyclerView = findViewById(R.id.playlistDetail);
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