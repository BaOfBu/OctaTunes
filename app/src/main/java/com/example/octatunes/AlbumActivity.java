package com.example.octatunes;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.PlaylistAdapter;
import com.example.octatunes.Model.PlaylistModel;

import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity{
    RelativeLayout _layoutPlaylistInformation;
    LinearLayout _layoutPlaylistSongs;
    LinearLayout _layoutPlaylistYouMayLike;
    ImageView _imagePlaylistCover;
    TextView _textPlaylistName, _textLikes, _textUsername, _textTotalTime;
    ImageButton _buttonPlay, _buttonAdd, _buttonDownload, _buttonSetting;
    RecyclerView _recyclerViewSongs, _recyclerViewYouMayLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_playlist);
        _layoutPlaylistSongs = findViewById(R.id.playlistSongs);
        _layoutPlaylistYouMayLike = findViewById(R.id.playlistYouMayLike);
        _layoutPlaylistInformation = findViewById(R.id.playlistInformation);
        _imagePlaylistCover = findViewById(R.id.playlistCover);
        _textPlaylistName = findViewById(R.id.playlist_name);
        _textLikes = findViewById(R.id.likes);
        _textUsername = findViewById(R.id.username);
        _textTotalTime = findViewById(R.id.total_times);
        _buttonPlay = findViewById(R.id.playButton);
        _buttonAdd = findViewById(R.id.addToLibButton);
        _buttonDownload = findViewById(R.id.downloadButton);
        _buttonSetting = findViewById(R.id.settingButton);
        _recyclerViewSongs = findViewById(R.id.songsRecyclerView);
        _recyclerViewYouMayLike = findViewById(R.id.youMayLikeRecyclerView);
        createFakeData();
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
    private ArrayList<PlaylistModel> getDataPlaylistsFake(){
        int id_default_image = R.drawable.ic_spotify;

        ArrayList<PlaylistModel> albumPreviewModels = new ArrayList<>();
        albumPreviewModels.add(new PlaylistModel( "Tên playlist 1", "Tên người tạo 1", id_default_image,null));
        albumPreviewModels.add(new PlaylistModel( "Tên playlist 2", "Tên người tạo 2", id_default_image,null));
        albumPreviewModels.add(new PlaylistModel( "Tên playlist 3", "Tên người tạo 3", id_default_image,null));
        albumPreviewModels.add(new PlaylistModel( "Tên playlist 4", "Tên người tạo 4", id_default_image,null));
        return albumPreviewModels;
    }
    private void createFakeData() {
        _textPlaylistName.setText("Chúng ta tương lai");
        _textLikes.setText("Album");
        _textUsername.setText("Username");
        _textTotalTime.setText("2024");
        ArrayList<TrackPreviewModel> listSongs = getDataTracksFake();
        TrackPreviewAdapter listSongsAdapter = new TrackPreviewAdapter(listSongs, this);
        _recyclerViewSongs.setAdapter(listSongsAdapter);

        ArrayList<PlaylistModel> listYouMayLike = getDataPlaylistsFake();
        PlaylistAdapter listYouMayLikeAdapter = new PlaylistAdapter(listYouMayLike);
        _recyclerViewYouMayLike.setAdapter(listYouMayLikeAdapter);

    }
}
