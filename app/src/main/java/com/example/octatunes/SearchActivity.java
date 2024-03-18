package com.example.octatunes;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements ListCategoriesButtonAdapter.OnCategorySelectedListener{
    String searchQuery = "";
    private EditText searchEditText;
    RelativeLayout listSearchResultLayout;
    static RecyclerView listSearchResultRecyclerView;
    RecyclerView listCategoriesButtonRecyclerView;

    private static ToggleButton selectedButton = null;
    private String searchType = "Band";

    public static ToggleButton getSelectedButton(){
        return selectedButton;
    }

    public static void setSelectedButton(ToggleButton button){
        selectedButton = button;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_result_search);

        searchEditText = findViewById(R.id.search_bar_edit_text);
        listSearchResultLayout = findViewById(R.id.list_search_result);

        View recyclerViewLayout = getLayoutInflater().inflate(R.layout.layout_result_search_track, null);
        listSearchResultRecyclerView = recyclerViewLayout.findViewById(R.id.recyclerview_track_preview_only_track);
        listCategoriesButtonRecyclerView = findViewById(R.id.recyclerview_categories_search);

        if (listSearchResultRecyclerView.getParent() != null) {
            ((ViewGroup) listSearchResultRecyclerView.getParent()).removeView(listSearchResultRecyclerView);
        }

        listSearchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        listSearchResultLayout.addView(listSearchResultRecyclerView);

        searchQuery = getIntent().getStringExtra("searchQuery");
        searchEditText.setText(searchQuery);
        searchEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getTracks(searchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        getListCategoriesButton();
        getBestMatchResults(searchQuery, searchType);
    }

    private ArrayList<TrackPreviewModel> getDataTracksFake(){
        int id_default_image = R.drawable.ic_spotify;

        ArrayList<TrackPreviewModel> trackPreviewModels = new ArrayList<>();
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 1", "Bài hát", "Tên nghệ sĩ 1", 2024));
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 2", "Bài hát", "Tên nghệ sĩ 2", 2024));
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 3", "Bài hát", "Tên nghệ sĩ 3", 2024));
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 4", "Bài hát", "Tên nghệ sĩ 4", 2024));
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 5", "Bài hát", "Tên nghệ sĩ 5", 2024));
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 6", "Bài hát", "Tên nghệ sĩ 6", 2024));
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 7", "Bài hát", "Tên nghệ sĩ 7", 2024));
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 8", "Bài hát", "Tên nghệ sĩ 8", 2024));
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 9", "Bài hát", "Tên nghệ sĩ 9", 2024));
        trackPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên bài hát 10", "Bài hát", "Tên nghệ sĩ 10", 2024));
        return trackPreviewModels;
    }
    private ArrayList<UserProfileModel> getDataUserProfileFake(){
        int id_default_image = R.drawable.ic_spotify;

        ArrayList<UserProfileModel> userProfileModels = new ArrayList<>();
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 1"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 2"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 3"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 4"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 5"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 6"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 7"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 8"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 9"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên người dùng 10"));
        return userProfileModels;
    }

    private ArrayList<UserProfileModel> getDataArtistFake(){
        int id_default_image = R.drawable.ic_spotify;

        ArrayList<UserProfileModel> userProfileModels = new ArrayList<>();
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 1"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 2"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 3"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 4"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 5"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 6"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 7"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 8"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 9"));
        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 10"));
        return userProfileModels;
    }
    private ArrayList<TrackPreviewModel> getDataAlbumsFakeLeft(){
        int id_default_image = R.drawable.ic_spotify;

        ArrayList<TrackPreviewModel> albumPreviewModels = new ArrayList<>();
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 1", "Album", "Tên nghệ sĩ 1", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 3", "Album", "Tên nghệ sĩ 2", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 5", "Album", "Tên nghệ sĩ 3", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 7", "Album", "Tên nghệ sĩ 4", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 9", "Album", "Tên nghệ sĩ 5", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 11", "Album", "Tên nghệ sĩ 6", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 13", "Album", "Tên nghệ sĩ 7", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 15", "Album", "Tên nghệ sĩ 8", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 17", "Album", "Tên nghệ sĩ 9", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 19", "Album", "Tên nghệ sĩ 10", 2024));
        return albumPreviewModels;
    }
    private ArrayList<TrackPreviewModel> getDataAlbumsFakeRight(){
        int id_default_image = R.drawable.ic_spotify;

        ArrayList<TrackPreviewModel> albumPreviewModels = new ArrayList<>();
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 2", "Album", "Tên nghệ sĩ 1", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 4", "Album", "Tên nghệ sĩ 2", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 6", "Album", "Tên nghệ sĩ 3", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 8", "Album", "Tên nghệ sĩ 4", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 10", "Album", "Tên nghệ sĩ 5", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 12", "Album", "Tên nghệ sĩ 6", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 14", "Album", "Tên nghệ sĩ 7", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 16", "Album", "Tên nghệ sĩ 8", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 18", "Album", "Tên nghệ sĩ 9", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên album 20", "Album", "Tên nghệ sĩ 10", 2024));
        return albumPreviewModels;
    }
    private ArrayList<TrackPreviewModel> getDataPlaylistsFakeLeft(){
        int id_default_image = R.drawable.ic_spotify;

        ArrayList<TrackPreviewModel> albumPreviewModels = new ArrayList<>();
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 1", "Album", "Tên nghệ sĩ 1", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 3", "Album", "Tên nghệ sĩ 2", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 5", "Album", "Tên nghệ sĩ 3", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 7", "Album", "Tên nghệ sĩ 4", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 9", "Album", "Tên nghệ sĩ 5", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 11", "Album", "Tên nghệ sĩ 6", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 13", "Album", "Tên nghệ sĩ 7", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 15", "Album", "Tên nghệ sĩ 8", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 17", "Album", "Tên nghệ sĩ 9", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 19", "Album", "Tên nghệ sĩ 10", 2024));
        return albumPreviewModels;
    }
    private ArrayList<TrackPreviewModel> getDataPlaylistsFakeRight(){
        int id_default_image = R.drawable.ic_spotify;

        ArrayList<TrackPreviewModel> albumPreviewModels = new ArrayList<>();
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 2", "Album", "Tên nghệ sĩ 1", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 4", "Album", "Tên nghệ sĩ 2", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 6", "Album", "Tên nghệ sĩ 3", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 8", "Album", "Tên nghệ sĩ 4", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 10", "Album", "Tên nghệ sĩ 5", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 12", "Album", "Tên nghệ sĩ 6", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 14", "Album", "Tên nghệ sĩ 7", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 16", "Album", "Tên nghệ sĩ 8", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 18", "Album", "Tên nghệ sĩ 9", 2024));
        albumPreviewModels.add(new TrackPreviewModel(id_default_image, "Tên playlist 20", "Album", "Tên nghệ sĩ 10", 2024));
        return albumPreviewModels;
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
        
        ListCategoriesButtonAdapter listCategoriesButtonAdapter = new ListCategoriesButtonAdapter(listCategories, this, this);
        listCategoriesButtonRecyclerView.setAdapter(listCategoriesButtonAdapter);
        listCategoriesButtonAdapter.notifyDataSetChanged();
    }
    private void getTracks(String searchQuery) {
        ArrayList<TrackPreviewModel> trackPreviewModels = getDataTracksFake();
        TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(trackPreviewModels, this);
        listSearchResultRecyclerView.setAdapter(trackPreviewAdapter);
        trackPreviewAdapter.notifyDataSetChanged();
    }
    private void getListUserProfile(String searchQuery) {
        ArrayList<UserProfileModel> userProfileModels = getDataUserProfileFake();
        UserProfileAdapter userProfileAdapter = new UserProfileAdapter(userProfileModels, this);
        listSearchResultRecyclerView.setAdapter(userProfileAdapter);
        userProfileAdapter.notifyDataSetChanged();
    }

    private void getListArtist(String searchQuery) {
        ArrayList<UserProfileModel> userProfileModels = getDataArtistFake();
        ResearchSearchOnlyArtistAdapter researchSearchOnlyArtistAdapter = new ResearchSearchOnlyArtistAdapter(userProfileModels, this);
        listSearchResultRecyclerView.setAdapter(researchSearchOnlyArtistAdapter);
        researchSearchOnlyArtistAdapter.notifyDataSetChanged();
    }
    private void getAlbums(String searchQuery) {
        ArrayList<TrackPreviewModel> albumPreviewModelsLeft = getDataAlbumsFakeLeft();
        ArrayList<TrackPreviewModel> albumPreviewModelsRight = getDataAlbumsFakeRight();
        ResultSearchOnlyAlbumAdapter resultSearchOnlyAlbumAdapter = new ResultSearchOnlyAlbumAdapter(albumPreviewModelsLeft, albumPreviewModelsRight, this);
        listSearchResultRecyclerView.setAdapter(resultSearchOnlyAlbumAdapter);
        resultSearchOnlyAlbumAdapter.notifyDataSetChanged();
    }
    private void getPlaylists(String searchQuery) {
        ArrayList<TrackPreviewModel> playlistPreviewModelsLeft = getDataPlaylistsFakeLeft();
        ArrayList<TrackPreviewModel> playlistPreviewModelsRight = getDataPlaylistsFakeRight();
        ResultSearchOnlyPlaylistAdapter resultSearchOnlyPlaylistAdapter = new ResultSearchOnlyPlaylistAdapter(playlistPreviewModelsLeft, playlistPreviewModelsRight, this);
        listSearchResultRecyclerView.setAdapter(resultSearchOnlyPlaylistAdapter);
        resultSearchOnlyPlaylistAdapter.notifyDataSetChanged();
    }

    private void getBestMatchResults(String searchQuery, String category){
        if(category.equals("Track")){
            getTracks(searchQuery);
        }else{
            UserProfileModel artistModel = new UserProfileModel(R.drawable.ic_spotify, "Tên nhóm nhạc");

            ArrayList<TrackPreviewModel> trackPreviewModels = getDataTracksFake();

            if(category.equals("Band")){
                ArrayList<TrackPreviewModel> albumPreviewModels = getDataAlbumsFakeLeft();
                ResultSearchByBandNameAdapter resultSearchByBandNameAdapter = new ResultSearchByBandNameAdapter(artistModel, albumPreviewModels, trackPreviewModels, this);
                listSearchResultRecyclerView.setAdapter(resultSearchByBandNameAdapter);
                resultSearchByBandNameAdapter.notifyDataSetChanged();
            }else if(category.equals("Artist")){
                artistModel.setFullName("Tên nghệ sĩ");
                ResearchSearchByArtistAdapter researchSearchByArtistAdapter = new ResearchSearchByArtistAdapter(artistModel, trackPreviewModels, this);
                listSearchResultRecyclerView.setAdapter(researchSearchByArtistAdapter);
                researchSearchByArtistAdapter.notifyDataSetChanged();
            }
        }

    }
    @Override
    public void onCategorySelected(String category) {
        switch (category){
            case "Kết quả phù hợp nhất": {
                getBestMatchResults(searchQuery, searchType);
                break;
            }
            case "Bài hát": {
                getTracks(searchQuery);
                break;
            }
            case "Nghệ sĩ": {
                getListArtist(searchQuery);
                break;
            }
            case "Danh sách phát": {
                getPlaylists(searchQuery);
                break;
            }
            case "Albums": {
                getAlbums(searchQuery);
                break;
            }
            case "Hồ sơ": {
                getListUserProfile(searchQuery);
                break;
            }
            default: {
                getBestMatchResults(searchQuery, searchType);
            }
        }
    }
}
