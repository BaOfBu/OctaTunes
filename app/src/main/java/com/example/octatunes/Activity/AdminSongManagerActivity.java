package com.example.octatunes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.SongManagerAdapter;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongManagerModel;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.TrackService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminSongManagerActivity extends AppCompatActivity {
    private TextView _textTitleAdmin, _textTotalSongs;
    private EditText _editTextSearch;
    private Button _btnSearch, _btnAddSong;
    private ImageView _btnRemoveSearch, _btnEditSong, _btnDeleteSong;
    private RecyclerView _recyclerViewSongList;
    private ImageButton _btn_menu;
    private LayoutInflater _inflater;
    private PopupWindow _popupWindow;
    private View _toolbar;
    boolean isPopupMenuShow = false;

    private SongManagerAdapter adapter;
    private List<TracksModel> tracks;
    private TrackService trackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_songmanager);
        linktoLayout();

        _textTitleAdmin.setText("Quản lý bài hát");

        adapter = new SongManagerAdapter();
        _recyclerViewSongList.setLayoutManager(new LinearLayoutManager(this));
        _recyclerViewSongList.setAdapter(adapter);

        trackService = new TrackService();
        loadSongs();

        _btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePopupMenu();
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút tìm kiếm
        _btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút xóa nội dung tìm kiếm
        _btnRemoveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void loadSongs() {
        trackService.getTracksRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SongManagerModel> songsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SongManagerModel song = new SongManagerModel();
                    TracksModel track = snapshot.getValue(TracksModel.class);
                    if (track != null) {
                        // Lấy ID Track
                        song.setTrackID(track.getTrackID());
                        // Lấy Name Track
                        song.setTrackName(track.getName());
                        // Lấy Artist Name của Track
                        trackService.getArtistNameByAlbumId(track.getAlubumID(), new TrackService.OnArtistNameLoadedListener() {
                            @Override
                            public void onArtistNameLoaded(String artistName) {
                                if (artistName != null) {
                                    song.setArtistName(artistName);
                                }
                            }
                        });
                        // Lấy Image cho Track
                        trackService.getImageForTrack(track, new TrackService.OnImageLoadedListener() {
                            @Override
                            public void onImageLoaded(String imageUrl) {
                                if (imageUrl != null) {
                                    song.setImageUrl(imageUrl);
                                }
                            }
                        });
                        // Lấy File cho Track
                        song.setFile(track.getFile());
                    }
                    songsList.add(song);
                }
                adapter.setSongsList(songsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void togglePopupMenu() {
        if (isPopupMenuShow) _popupWindow.dismiss();
        else showPopupMenu(_btn_menu);
    }

    private void showPopupMenu(View view) {
        _inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = _inflater.inflate(R.layout.layout_admin_menu, null);

        int width = getResources().getDisplayMetrics().widthPixels / 2;
        int height = getResources().getDisplayMetrics().heightPixels;

        boolean focus = true;

        _toolbar = findViewById(R.id.toolbar);
        int _toolbarHeight = _toolbar.getHeight();

        _popupWindow = new PopupWindow(popupView, width, height - _toolbarHeight, focus);
        _popupWindow.showAtLocation(view, Gravity.START | Gravity.BOTTOM, 0, 0);

        TextView dashboard = popupView.findViewById(R.id.dashboard);
        TextView userManager = popupView.findViewById(R.id.user_manager);
        TextView musicManager = popupView.findViewById(R.id.music_manager);

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSongManagerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        userManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSongManagerActivity.this, AdminUserManagerActivity.class);
                startActivity(intent);
            }
        });

        musicManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSongManagerActivity.this, AdminSongManagerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void linktoLayout() {
        _textTitleAdmin = findViewById(R.id.text_title_admin);
        _editTextSearch = findViewById(R.id.edit_text_search);
        _btnSearch = findViewById(R.id.btn_search);
        _recyclerViewSongList = findViewById(R.id.song_list);
        _btnAddSong = findViewById(R.id.btn_add_song);
        _btnRemoveSearch = findViewById(R.id.btn_remove);
        _btn_menu = findViewById(R.id.menu_admin);
        _textTotalSongs = findViewById(R.id.text_total_songs);
        _btnEditSong = findViewById(R.id.btn_edit_song);
        _btnDeleteSong = findViewById(R.id.btn_delete_song);
    }
}