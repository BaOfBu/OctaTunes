package com.example.octatunes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.TracksModel;
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

    private PlaylistService playlistService = new PlaylistService();
    private ArtistService artistService = new ArtistService();
    private TrackService trackService = new TrackService();

    private static ToggleButton selectedButton = null;
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

        if (listSearchResultRecyclerView.getParent() != null) {
            ((ViewGroup) listSearchResultRecyclerView.getParent()).removeView(listSearchResultRecyclerView);
        }

        //listSearchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        listSearchResultLayout.addView(listSearchResultRecyclerView);
        if(getArguments()!=null){
            searchQuery = getArguments().getString("searchQuery");
        }
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
        return rootView;
    }

    private ArrayList<TrackPreviewModel> getDataTracksFake(){
        int []song_images = {R.drawable.song_chungtacuatuonglai,
                R.drawable.song_theresnooneatall,
                R.drawable.song_muonroisaomacon,
                R.drawable.song_makingmyway,
                R.drawable.song_haytraochoanh,
                R.drawable.song_chungtacuahientai,
                R.drawable.song_noinaycoanh,
                R.drawable.song_chacaidoseve,
                R.drawable.song_emcuangayhomqua,
                R.drawable.song_nangamxadan
        };

        ArrayList<TrackPreviewModel> trackPreviewModels = new ArrayList<>();
        trackPreviewModels.add(new TrackPreviewModel(song_images[0], "Chúng ta của tương lai", "Bài hát", "Sơn Tùng MTP", 2024));
        trackPreviewModels.add(new TrackPreviewModel(song_images[1], "There's no one a tall", "Bài hát", "Sơn Tùng MTP", 2022));
        trackPreviewModels.add(new TrackPreviewModel(song_images[2], "Muộn rồi sao mà còn", "Bài hát", "Sơn Tùng MTP", 2021));
        trackPreviewModels.add(new TrackPreviewModel(song_images[3], "Making my way", "Bài hát", "Sơn Tùng MTP", 2023));
        trackPreviewModels.add(new TrackPreviewModel(song_images[4], "Hãy trao cho anh", "Bài hát", "Sơn Tùng MTP", 2019));
        trackPreviewModels.add(new TrackPreviewModel(song_images[5], "Chúng ta của hiện tại", "Bài hát", "Sơn Tùng MTP", 2020));
        trackPreviewModels.add(new TrackPreviewModel(song_images[6], "Nơi này có anh", "Bài hát", "Sơn Tùng MTP", 2017));
        trackPreviewModels.add(new TrackPreviewModel(song_images[7], "Chắc ai đó sẽ về", "Bài hát", "Sơn Tùng MTP", 2017));
        trackPreviewModels.add(new TrackPreviewModel(song_images[8], "Em của ngày hôm qua", "Bài hát", "Sơn Tùng MTP", 2014));
        trackPreviewModels.add(new TrackPreviewModel(song_images[9], "Nắng ấm xa dần", "Bài hát", "Sơn Tùng MTP", 2014));
        return trackPreviewModels;
    }
    private ArrayList<UserProfileModel> getDataUserProfileFake(){
        int id_default_image = R.drawable.ic_spotify;

        int []userprofile_images = {
            R.drawable.userprofile_sontung1,
            R.drawable.userprofile_sontung2,
            R.drawable.userprofile_sontung3,
            R.drawable.userprofile_sontung4,
            R.drawable.userprofile_sontung5,
            R.drawable.userprofile_sontung6,
            R.drawable.userprofile_sontung7,
            R.drawable.userprofile_sontung8,
            R.drawable.userprofile_sontung9,
            R.drawable.userprofile_sontung10
        };

        ArrayList<UserProfileModel> userProfileModels = new ArrayList<>();
        userProfileModels.add(new UserProfileModel(userprofile_images[0], "Sơn Tùng"));
        userProfileModels.add(new UserProfileModel(userprofile_images[1], "Sơn Tùng"));
        userProfileModels.add(new UserProfileModel(userprofile_images[2], "Sơn Tùng"));
        userProfileModels.add(new UserProfileModel(userprofile_images[3], "Sơn Tùng"));
        userProfileModels.add(new UserProfileModel(userprofile_images[4], "Sơn Tùng"));
        userProfileModels.add(new UserProfileModel(userprofile_images[5], "Sơn Tùng"));
        userProfileModels.add(new UserProfileModel(userprofile_images[6], "Sơn Tùng"));
        userProfileModels.add(new UserProfileModel(userprofile_images[7], "Sơn Tùng"));
        userProfileModels.add(new UserProfileModel(userprofile_images[8], "Sơn Tùng"));
        userProfileModels.add(new UserProfileModel(userprofile_images[9], "Sơn Tùng"));
        return userProfileModels;
    }

    private ArrayList<UserProfileModel> getDataArtistFake(){
        int id_default_image = R.drawable.ic_spotify;

        int []artist_images = {
            R.drawable.artist_sontungmtp,
            R.drawable.artist_mono,
            R.drawable.artist_jack97,
            R.drawable.artist_bichphuong,
            R.drawable.artist_sontuyen
        };

        ArrayList<UserProfileModel> userProfileModels = new ArrayList<>();
        userProfileModels.add(new UserProfileModel(artist_images[0], "Sơn Tùng MTP"));
        userProfileModels.add(new UserProfileModel(artist_images[1], "Mono"));
        userProfileModels.add(new UserProfileModel(artist_images[2], "Jack J97"));
        userProfileModels.add(new UserProfileModel(artist_images[3], "Bích Phương"));
        userProfileModels.add(new UserProfileModel(artist_images[4], "Sơn Tuyền"));
//        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 6"));
//        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 7"));
//        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 8"));
//        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 9"));
//        userProfileModels.add(new UserProfileModel(id_default_image, "Tên nghệ sĩ 10"));
        return userProfileModels;
    }
    private ArrayList<TrackPreviewModel> getDataAlbumsFakeLeft(){

        int []album_images = {
                R.drawable.album_chungtacuatuonglai,
                R.drawable.album_makingmyway,
                R.drawable.album_noinaycoanh,
                R.drawable.album_cochacyeuladay,
                R.drawable.album_haytraochoanh,
                R.drawable.album_chungtacuahientai,
                R.drawable.album_mtp,
                R.drawable.album_nangamxadan,
                R.drawable.album_emcuangayhomqua,
                R.drawable.album_khongphaidangvuadau,
        };


        ArrayList<TrackPreviewModel> albumPreviewModels = new ArrayList<>();
        albumPreviewModels.add(new TrackPreviewModel(album_images[0], "Chúng ta của tương lai", "Album", "Sơn Tùng MTP", 2024));
        albumPreviewModels.add(new TrackPreviewModel(album_images[1], "Making my way", "Album", "Sơn Tùng MTP", 2023));
        albumPreviewModels.add(new TrackPreviewModel(album_images[2], "Nơi này có anh", "Album", "Sơn Tùng MTP", 2017));
        albumPreviewModels.add(new TrackPreviewModel(album_images[3], "Có chắc yêu là đây", "Album", "Sơn Tùng MTP", 2020));
        albumPreviewModels.add(new TrackPreviewModel(album_images[4], "Hãy trao cho anh", "Album", "Sơn Tùng MTP", 2019));
        albumPreviewModels.add(new TrackPreviewModel(album_images[5], "Chúng ta của hiện tại", "Album", "Sơn Tùng MTP", 2020));
        albumPreviewModels.add(new TrackPreviewModel(album_images[6], "mtp M-TP", "Album", "Sơn Tùng MTP", 2017));
        albumPreviewModels.add(new TrackPreviewModel(album_images[7], "Nắng ấm xa dần", "Album", "Sơn Tùng MTP", 2014));
        albumPreviewModels.add(new TrackPreviewModel(album_images[8], "Em của ngày hôm qua", "Album", "Sơn Tùng MTP", 2014));
        albumPreviewModels.add(new TrackPreviewModel(album_images[9], "Không phải dạng vừa đâu", "Album", "Sơn Tùng MTP", 2015));
        return albumPreviewModels;
    }
    private ArrayList<TrackPreviewModel> getDataAlbumsFakeRight(){
        int []album_images = {
                R.drawable.album_theresononeatall,
                R.drawable.album_skydecade,
                R.drawable.album_muonroimasaocon,
                R.drawable.album_chayngaydi,
                R.drawable.album_nangamxadan,
                R.drawable.album_amthambenem,
                R.drawable.album_lactroi,
                R.drawable.album_khuonmatdangthuong,
                R.drawable.album_rememberme,
                R.drawable.album_annutnhothagiacmo
        };


        ArrayList<TrackPreviewModel> albumPreviewModels = new ArrayList<>();
        albumPreviewModels.add(new TrackPreviewModel(album_images[0], "There's no one at all", "Album", "Sơn Tùng MTP", 2022));
        albumPreviewModels.add(new TrackPreviewModel(album_images[1], "Sky decade", "Album", "Sơn Tùng MTP", 2022));
        albumPreviewModels.add(new TrackPreviewModel(album_images[2], "Muộn rồi mà sao còn", "Album", "Sơn Tùng MTP", 2021));
        albumPreviewModels.add(new TrackPreviewModel(album_images[3], "Chạy ngay đi", "Album", "Sơn Tùng MTP", 2018));
        albumPreviewModels.add(new TrackPreviewModel(album_images[4], "Nắng ấm xa dần", "Album", "Sơn Tùng MTP", 2019));
        albumPreviewModels.add(new TrackPreviewModel(album_images[5], "Âm thầm bên em", "Album", "Sơn Tùng MTP", 2015));
        albumPreviewModels.add(new TrackPreviewModel(album_images[6], "Lạc trôi", "Album", "Sơn Tùng MTP", 2016));
        albumPreviewModels.add(new TrackPreviewModel(album_images[7], "Khuôn mặt đáng thương", "Album", "Sơn Tùng MTP", 2015));
        albumPreviewModels.add(new TrackPreviewModel(album_images[8], "Remember me", "Album", "Sơn Tùng MTP", 2021));
        albumPreviewModels.add(new TrackPreviewModel(album_images[9], "Ấn nút nhớ thả giấc mơ", "Album", "Sơn Tùng MTP", 2015));
        return albumPreviewModels;
    }
    private ArrayList<TrackPreviewModel> getDataPlaylistsFakeLeft(){
        int []playlist_images = {
                R.drawable.playlist_dungvetrenha,
                R.drawable.playlist_maiyeusontungmtp,
                R.drawable.playlist_sontungmtp,
                R.drawable.playlist_sontungmtpmix,
                R.drawable.playlist_sontungremix
        };

        ArrayList<TrackPreviewModel> albumPreviewModels = new ArrayList<>();
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[0], "Đừng về trễ nha", "Playlist", "Sơn Tùng MTP", 2024));
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[1], "Mãi yêu sơn tùng mtp", "Playlist", "Sơn Tùng MTP", 2024));
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[2], "Sơn Tùng MTP", "Playlist", "Sơn Tùng MTP", 2024));
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[3], "Sơn Tùng MTP Mix", "Playlist", "Sơn Tùng MTP", 2024));
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[4], "Sơn Tùng Remix", "Playlist", "Sơn Tùng MTP", 2024));
        return albumPreviewModels;
    }
    private ArrayList<TrackPreviewModel> getDataPlaylistsFakeRight(){
        int []playlist_images = {
                R.drawable.playlist_dungvetrenhasontungmtp,
                R.drawable.playlist_nhacsontungmtp,
                R.drawable.playlist_sontungmtplist,
                R.drawable.playlist_sontungmtpplaylist,
                R.drawable.playlist_tuyentapcuasontungmtp
        };

        ArrayList<TrackPreviewModel> albumPreviewModels = new ArrayList<>();
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[0], "Đừng về trễ nhà - Sơn Tùng MTP", "Playlist", "Sơn Tùng MTP", 2024));
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[1], "Nhạc Sơn Tùng MTP", "Playlist", "Sơn Tùng MTP", 2024));
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[2], "Sơn Tùng MTP List", "Playlist", "Sơn Tùng MTP", 2024));
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[3], "Sơn Tùng MTP Play list", "Playlist", "Sơn Tùng MTP", 2024));
        albumPreviewModels.add(new TrackPreviewModel(playlist_images[4], "Tuyển tập của Sơn Tùng MTP", "Playlist", "Sơn Tùng MTP", 2024));
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
        
        ListCategoriesButtonAdapter listCategoriesButtonAdapter = new ListCategoriesButtonAdapter(listCategories, this.getContext(), this);
        listCategoriesButtonRecyclerView.setAdapter(listCategoriesButtonAdapter);
        listCategoriesButtonAdapter.notifyDataSetChanged();
    }
    private void getTracks(String searchQuery) {
//        ArrayList<TrackPreviewModel> trackPreviewModels = getDataTracksFake();
//        TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(trackPreviewModels, this.getContext());
//        listSearchResultRecyclerView.setAdapter(trackPreviewAdapter);
//        trackPreviewAdapter.notifyDataSetChanged();
        trackService.findTrackByName(searchQuery,
                new OnSuccessListener<List<TracksModel>>() {
                    @Override
                    public void onSuccess(List<TracksModel> trackPreviewModels) {
                        TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(trackPreviewModels, getContext());
                        listSearchResultRecyclerView.setAdapter(trackPreviewAdapter);
                        trackPreviewAdapter.notifyDataSetChanged();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }
    private void getListUserProfile(String searchQuery) {
        ArrayList<UserProfileModel> userProfileModels = getDataUserProfileFake();
        UserProfileAdapter userProfileAdapter = new UserProfileAdapter(userProfileModels, this.getContext());
        listSearchResultRecyclerView.setAdapter(userProfileAdapter);
        userProfileAdapter.notifyDataSetChanged();
    }

    private void getListArtist(String searchQuery) {
        //ArrayList<UserProfileModel> userProfileModels = getDataArtistFake();
        Context context = this.getContext();
        artistService.findArtistByName(searchQuery,
                new OnSuccessListener<List<ArtistsModel>>() {
                    @Override
                    public void onSuccess(List<ArtistsModel> artistsModels) {
                        ResearchSearchOnlyArtistAdapter researchSearchOnlyArtistAdapter = new ResearchSearchOnlyArtistAdapter(artistsModels, context);
                        listSearchResultRecyclerView.setAdapter(researchSearchOnlyArtistAdapter);
                        researchSearchOnlyArtistAdapter.notifyDataSetChanged();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
//        ResearchSearchOnlyArtistAdapter researchSearchOnlyArtistAdapter = new ResearchSearchOnlyArtistAdapter(userProfileModels, this.getContext());
//        listSearchResultRecyclerView.setAdapter(researchSearchOnlyArtistAdapter);
//        researchSearchOnlyArtistAdapter.notifyDataSetChanged();
    }
    private void getAlbums(String searchQuery) {
        ArrayList<TrackPreviewModel> albumPreviewModelsLeft = getDataAlbumsFakeLeft();
        ArrayList<TrackPreviewModel> albumPreviewModelsRight = getDataAlbumsFakeRight();
        ResultSearchOnlyAlbumAdapter resultSearchOnlyAlbumAdapter = new ResultSearchOnlyAlbumAdapter(albumPreviewModelsLeft, albumPreviewModelsRight, this.getContext());
        listSearchResultRecyclerView.setAdapter(resultSearchOnlyAlbumAdapter);
        resultSearchOnlyAlbumAdapter.notifyDataSetChanged();
    }
    private void getPlaylists(String searchQuery) {
        ArrayList<TrackPreviewModel> playlistPreviewModelsLeft = getDataPlaylistsFakeLeft();
        ArrayList<TrackPreviewModel> playlistPreviewModelsRight = getDataPlaylistsFakeRight();
        ResultSearchOnlyPlaylistAdapter resultSearchOnlyPlaylistAdapter = new ResultSearchOnlyPlaylistAdapter(playlistPreviewModelsLeft, playlistPreviewModelsRight, this.getContext());
        listSearchResultRecyclerView.setAdapter(resultSearchOnlyPlaylistAdapter);
        resultSearchOnlyPlaylistAdapter.notifyDataSetChanged();
    }

    private void getBestMatchResults(String searchQuery, String category){
        if(category.equals("Track")){
            getTracks(searchQuery);
        }else{
            UserProfileModel artistModel = new UserProfileModel(R.drawable.artist_sontungmtp, "Tên nhóm nhạc");

            ArrayList<TrackPreviewModel> trackPreviewModels = getDataTracksFake();

            if(category.equals("Band")){
                ArrayList<TrackPreviewModel> albumPreviewModels = getDataAlbumsFakeLeft();
                ResultSearchByBandNameAdapter resultSearchByBandNameAdapter = new ResultSearchByBandNameAdapter(artistModel, albumPreviewModels, trackPreviewModels, this.getContext());
                listSearchResultRecyclerView.setAdapter(resultSearchByBandNameAdapter);
                resultSearchByBandNameAdapter.notifyDataSetChanged();
            }else if(category.equals("Artist")){
                artistModel.setFullName("Sơn Tùng MTP");
                ResearchSearchByArtistAdapter researchSearchByArtistAdapter = new ResearchSearchByArtistAdapter(artistModel, trackPreviewModels, this.getContext());
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
