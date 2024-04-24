package com.example.octatunes;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.KeyEvent;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.ListenToMusicActivity;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Services.AlbumService;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.PlaylistService;
import com.example.octatunes.Services.TrackService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchActivity extends Fragment implements ListCategoriesButtonAdapter.OnCategorySelectedListener{
    String searchQuery = "";
    private EditText searchEditText;
    RelativeLayout listSearchResultLayout;
    static RecyclerView listSearchResultRecyclerView;
    RecyclerView listCategoriesButtonRecyclerView;
    TextView emptyTextView;

    private PlaylistService playlistService = new PlaylistService();
    private ArtistService artistService = new ArtistService();
    private TrackService trackService = new TrackService();
    private AlbumService albumService = new AlbumService();

    private static ToggleButton selectedButton = null;
    private TextView cancelSearchButton;

    private String selectedCategory = "Kết quả phù hợp nhất";
    private String searchType = "Track";

    public static ToggleButton getSelectedButton(){
        return selectedButton;
    }

    public static void setSelectedButton(ToggleButton button){
        selectedButton = button;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.layout_result_search, container, false);
        searchEditText = rootView.findViewById(R.id.search_bar_edit_text);
        searchEditText = rootView.findViewById(R.id.search_bar_edit_text);
        listSearchResultLayout = rootView.findViewById(R.id.list_search_result);

        View recyclerViewLayout = getLayoutInflater().inflate(R.layout.layout_result_search_track, null);
        listSearchResultRecyclerView = recyclerViewLayout.findViewById(R.id.recyclerview_track_preview_only_track);
        listCategoriesButtonRecyclerView = rootView.findViewById(R.id.recyclerview_categories_search);
        emptyTextView = rootView.findViewById(R.id.emptyListText);
        cancelSearchButton = rootView.findViewById(R.id.HuyButton);

        if (listSearchResultRecyclerView.getParent() != null) {
            ((ViewGroup) listSearchResultRecyclerView.getParent()).removeView(listSearchResultRecyclerView);
        }

        listSearchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        listSearchResultLayout.addView(listSearchResultRecyclerView);
        if(getArguments()!=null){
            searchQuery = getArguments().getString("searchQuery");
        }
        searchEditText.setText(searchQuery);

        cancelSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        //uncomment this for instant search when type in search box
//        searchEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // This method is called before the text is changed
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // This method is called when the text is changed
//                searchQuery = searchEditText.getText().toString();
//                switch (selectedCategory) {
//                    case "Kết quả phù hợp nhất":
//                        getTracks();
//                        break;
//                    case "Bài hát":
//                        getTracks();
//                        break;
//                    case "Nghệ sĩ":
//                        getListArtist();
//                        break;
//                    case "Danh sách phát":
//                        getPlaylists();
//                        break;
//                    case "Albums":
//                        getAlbums();
//                        break;
//                    case "Hồ sơ":
//                        getListUserProfile(searchQuery);
//                        break;
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // This method is called after the text is changed
//            }
//        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    if(selectedButton == null)
//                        Log.e("SearchActivity", "button get text: " + selectedButton.getText().toString());
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                    //listSearchResultRecyclerView.setAdapter(null);
                    searchQuery = searchEditText.getText().toString();
                    switch (selectedCategory) {
                        case "Kết quả phù hợp nhất":
                            getBestMatchResults();
                            break;
                        case "Bài hát":
                            getTracks();
                            break;
                        case "Nghệ sĩ":
                            getListArtist();
                            break;
                        case "Danh sách phát":
                            getPlaylists();
                            break;
                        case "Albums":
                            getAlbums();
                            break;
                        case "Hồ sơ":
                            getListUserProfile(searchQuery);
                            break;
                    }

                    return true;
                }
                return false;
            }
        });

        getListCategoriesButton();
        getBestMatchResults();
        return rootView;
    }

    private ArrayList<String> getListCategories(){
        ArrayList<String> listCategories = new ArrayList<>();

        listCategories.add("Kết quả phù hợp nhất");
        listCategories.add("Bài hát");
        listCategories.add("Nghệ sĩ");
        listCategories.add("Danh sách phát");
        listCategories.add("Albums");
        listCategories.add("Hồ sơ");

        return  listCategories;
    }

    private void getListCategoriesButton(){
        ArrayList<String> listCategories = getListCategories();

        ListCategoriesButtonAdapter listCategoriesButtonAdapter = new ListCategoriesButtonAdapter(listCategories, this.getContext(), this);
        listCategoriesButtonRecyclerView.setAdapter(listCategoriesButtonAdapter);
        listCategoriesButtonAdapter.notifyDataSetChanged();
    }
    private void getTracks() {
//        ArrayList<TrackPreviewModel> trackPreviewModels = getDataTracksFake();
//        TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(trackPreviewModels, this.getContext());
//        listSearchResultRecyclerView.setAdapter(trackPreviewAdapter);
//        trackPreviewAdapter.notifyDataSetChanged();
        listSearchResultRecyclerView.setAdapter(null);
        List<TracksModel> tempTracks = new ArrayList<>();
        trackService.findTrackByName(searchQuery, new TrackService.OnTracksLoadedListener() {
            @Override
            public void onTracksLoaded(List<TracksModel> tracks) {
                Log.e("SearchActivity", "Tracks: " + tracks.size());
                if(tracks.size() == 0){
                    emptyTextView.setVisibility(View.VISIBLE);
                    listSearchResultRecyclerView.setVisibility(View.GONE);
                    return;
                }else{
                    emptyTextView.setVisibility(View.GONE);
                    listSearchResultRecyclerView.setVisibility(View.VISIBLE);
                }
                TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(tracks, getContext());
                listSearchResultRecyclerView.setAdapter(trackPreviewAdapter);
                trackPreviewAdapter.notifyDataSetChanged();
            }
        });

    }
    private void getListUserProfile(String searchQuery) {
//        ArrayList<UserProfileModel> userProfileModels = getDataUserProfileFake();
//        UserProfileAdapter userProfileAdapter = new UserProfileAdapter(userProfileModels, this.getContext());
//        listSearchResultRecyclerView.setAdapter(userProfileAdapter);
//        userProfileAdapter.notifyDataSetChanged();
    }

    private void getListArtist() {
        //ArrayList<UserProfileModel> userProfileModels = getDataArtistFake();
        Context context = this.getContext();
        artistService.findArtistByName(searchQuery,
                new OnSuccessListener<List<ArtistsModel>>() {
                    @Override
                    public void onSuccess(List<ArtistsModel> artistsModels) {
                        Log.e("SearchActivity", "ArtisModels: " + artistsModels.size());
                        if(artistsModels.size() == 0){
                            emptyTextView.setVisibility(View.VISIBLE);
                            listSearchResultRecyclerView.setVisibility(View.GONE);
                            return;
                        }else{
                            emptyTextView.setVisibility(View.GONE);
                            listSearchResultRecyclerView.setVisibility(View.VISIBLE);
                        }

                        ResearchSearchOnlyArtistAdapter researchSearchOnlyArtistAdapter = new ResearchSearchOnlyArtistAdapter(artistsModels, context);
                        listSearchResultRecyclerView.setAdapter(researchSearchOnlyArtistAdapter);
                        researchSearchOnlyArtistAdapter.notifyDataSetChanged();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Log.e("SearchActivity", "Error getting artists", e);
                    }
                });
//        ResearchSearchOnlyArtistAdapter researchSearchOnlyArtistAdapter = new ResearchSearchOnlyArtistAdapter(userProfileModels, this.getContext());
//        listSearchResultRecyclerView.setAdapter(researchSearchOnlyArtistAdapter);
//        researchSearchOnlyArtistAdapter.notifyDataSetChanged();
    }
    private void getAlbums() {
//        ArrayList<TrackPreviewModel> albumPreviewModelsLeft = getDataAlbumsFakeLeft();
//        ArrayList<TrackPreviewModel> albumPreviewModelsRight = getDataAlbumsFakeRight();
//        ResultSearchOnlyAlbumAdapter resultSearchOnlyAlbumAdapter = new ResultSearchOnlyAlbumAdapter(albumPreviewModelsLeft, albumPreviewModelsRight, this);
//        listSearchResultRecyclerView.setAdapter(resultSearchOnlyAlbumAdapter);
//        resultSearchOnlyAlbumAdapter.notifyDataSetChanged();
        albumService.findAlbumByName(searchQuery).thenAccept(albums -> {
            Log.e("SearchActivity", "ALbums: " + albums.size());
            if(albums.size() == 0){
                emptyTextView.setVisibility(View.VISIBLE);
                listSearchResultRecyclerView.setVisibility(View.GONE);
                return;
            }else{
                emptyTextView.setVisibility(View.GONE);
                listSearchResultRecyclerView.setVisibility(View.VISIBLE);
            }
            List<AlbumsModel> albumHolders = new ArrayList<>();
            for(AlbumsModel album : albums){
                albumHolders.add(album);
            }
            ResultSearchOnlyAlbumAdapter resultSearchOnlyAlbumAdapter = new ResultSearchOnlyAlbumAdapter(albumHolders, this.getContext());
            listSearchResultRecyclerView.setAdapter(resultSearchOnlyAlbumAdapter);
            resultSearchOnlyAlbumAdapter.notifyDataSetChanged();
        }).exceptionally(throwable -> {
            Log.e("SearchActivity", "Error getting albums", throwable);
            return null;
        });
    }
    private void getPlaylists() {
//        ArrayList<TrackPreviewModel> playlistPreviewModelsLeft = getDataPlaylistsFakeLeft();
//        ArrayList<TrackPreviewModel> playlistPreviewModelsRight = getDataPlaylistsFakeRight();
//        ResultSearchOnlyPlaylistAdapter resultSearchOnlyPlaylistAdapter = new ResultSearchOnlyPlaylistAdapter(playlistPreviewModelsLeft, playlistPreviewModelsRight, this);
//        listSearchResultRecyclerView.setAdapter(resultSearchOnlyPlaylistAdapter);
//        resultSearchOnlyPlaylistAdapter.notifyDataSetChanged();
        playlistService.getPlaylistByName(searchQuery).thenAccept(playlists -> {
            Log.e("SearchActivity", "Playlists: " + playlists.size());
            if(playlists.size() == 0){
                emptyTextView.setVisibility(View.VISIBLE);
                listSearchResultRecyclerView.setVisibility(View.GONE);
                return;
            }else{
                emptyTextView.setVisibility(View.GONE);
                listSearchResultRecyclerView.setVisibility(View.VISIBLE);
            }
            List<PlaylistsModel> playlistModels = new ArrayList<>();
            for(PlaylistsModel playlist : playlists){
                playlistModels.add(playlist);
            }
            ResultSearchOnlyPlaylistAdapter resultSearchOnlyPlaylistAdapter = new ResultSearchOnlyPlaylistAdapter(playlistModels, this.getContext());
            listSearchResultRecyclerView.setAdapter(resultSearchOnlyPlaylistAdapter);
            resultSearchOnlyPlaylistAdapter.notifyDataSetChanged();
        }).exceptionally(throwable -> {
            Log.e("SearchActivity", "Error getting playlists", throwable);
            return null;
        });
    }

    private void getBestMatchResults(){
        List<AlbumsModel> listAlbums = new ArrayList<>();
        List<ArtistsModel> artist = new ArrayList<>();
        // Search for albums
        albumService.findAlbumByName(searchQuery).thenAccept(albums -> {
            Log.e("SearchActivity", "ALbums: " + albums.size());
            for(AlbumsModel album : albums){
                listAlbums.add(album);
            }
        }).exceptionally(throwable -> {
            Log.e("SearchActivity", "Error getting albums", throwable);
            return null;
        });

        // Search for artists

        artistService.findArtistByName(searchQuery, new OnSuccessListener<List<ArtistsModel>>() {
            @Override
            public void onSuccess(List<ArtistsModel> artists) {
                Log.e("SearchActivity", "Artists: " + artists.size());
                if(artists.size() == 0)
                    return;
                artist.addAll(artists);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("SearchActivity", "Error getting artists", e);
            }
        });
        // Search for tracks
        trackService.findTrackByName(searchQuery, new TrackService.OnTracksLoadedListener() {
            @Override
            public void onTracksLoaded(List<TracksModel> tracks) {
                Log.e("SearchActivity", "Tracks: " + tracks.size());
                ResultSearchByBandNameAdapter resultSearchByBandNameAdapter = new ResultSearchByBandNameAdapter(artist, listAlbums, tracks, getContext());
                listSearchResultRecyclerView.setAdapter(resultSearchByBandNameAdapter);
                resultSearchByBandNameAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onCategorySelected(String category) {
        switch (category){
            case "Kết quả phù hợp nhất": {
                selectedCategory = "Kết quả phù hợp nhất";
                getBestMatchResults();
                break;
            }
            case "Bài hát": {
                selectedCategory = "Bài hát";
                getTracks();
                break;
            }
            case "Nghệ sĩ": {
                selectedCategory = "Nghệ sĩ";
                getListArtist();
                break;
            }
            case "Danh sách phát": {
                selectedCategory = "Danh sách phát";
                getPlaylists();
                break;
            }
            case "Albums": {
                selectedCategory = "Albums";
                getAlbums();
                break;
            }
            case "Hồ sơ": {
                getListUserProfile(searchQuery);
                break;
            }
            default: {
                getBestMatchResults();
            }
        }
    }
}