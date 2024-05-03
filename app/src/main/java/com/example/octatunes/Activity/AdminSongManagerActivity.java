package com.example.octatunes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.SongManagerAdapter;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.SongManagerModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.SongManagerService;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.Utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdminSongManagerActivity extends AppCompatActivity {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private TextView _textTitleAdmin, _textTotalSongs;
    private EditText _editTextSearch;
    private Button _btnAddSong;
    private ImageView _btnRemoveSearch;
    private RecyclerView _recyclerViewSongList;
    private ImageButton _btn_menu;
    private LayoutInflater _inflater;
    private PopupWindow _popupWindow;
    private View _toolbar;
    boolean isPopupMenuShow = false;

    private SongManagerAdapter adapter;
    private List<SongManagerModel> songs = new ArrayList<>();
    private TrackService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_songmanager);
        linktoLayout();

        _textTitleAdmin.setText("Quản lý bài hát");

        adapter = new SongManagerAdapter();
        _recyclerViewSongList.setLayoutManager(new LinearLayoutManager(this));
        _recyclerViewSongList.setAdapter(adapter);

        service = new TrackService();
        getTracks();

        _btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePopupMenu();
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút xóa nội dung tìm kiếm
        _btnRemoveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearch();
            }
        });

        _btnAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSongManagerActivity.this, AdminAddSongActivity.class);
                intent.putExtra("TOTAL_TRACKS_CUR", String.valueOf(adapter.getTotal()));
                startActivity(intent);
            }
        });

        // Add listener to the search EditText
        _editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Trigger search when text changes
                String searchText = s.toString().trim();
                if (!searchText.isEmpty()) {
                    searchSongs(searchText);
                } else {
                    // If search text is empty, show all tracks
                    getTracks();
                }
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
                Intent intent = new Intent(AdminSongManagerActivity.this, AdminDashboardActivity.class);
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
        _btn_menu = findViewById(R.id.menu_admin);
        _editTextSearch = findViewById(R.id.edit_text_search);
        _btnRemoveSearch = findViewById(R.id.btn_remove);
        _recyclerViewSongList = findViewById(R.id.song_list);
        _btnAddSong = findViewById(R.id.btn_add_song);
        _textTotalSongs = findViewById(R.id.text_total_songs);
    }

    private void getTracks() {
        List<SongManagerModel> songs = new ArrayList<>();
        service.getTracksRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot trackSnapshot : dataSnapshot.getChildren()) {
                    TracksModel track = trackSnapshot.getValue(TracksModel.class);
                    SongManagerModel song = new SongManagerModel();

                    if (track != null) {
                        song.setTrackID(track.getTrackID());
                        song.setTrackName(track.getName());
                        song.setAlbumID(track.getAlubumID());
                        song.setFile(track.getFile());
                        service.getImageForTrack(track, new TrackService.OnImageLoadedListener() {
                            @Override
                            public void onImageLoaded(String imageUrl) {
                                song.setImage(imageUrl);
                            }
                        });
                        service.getArtistNameByAlbumId(track.getAlubumID(), new TrackService.OnArtistNameLoadedListener() {
                            @Override
                            public void onArtistNameLoaded(String artistName) {
                                song.setArtistName(artistName);
                            }
                        });

                        songs.add(song);
                    }
                    updateRecyclerView(songs);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchSongs(final String searchText) {
        final String searchTextWithoutAccents = StringUtil.removeAccents(searchText).toLowerCase();
        service.getTracksRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SongManagerModel> searchResults = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TracksModel track = snapshot.getValue(TracksModel.class);
                    SongManagerModel song = new SongManagerModel();
                    // Kiểm tra xem tiêu đề hoặc tên ca sĩ (không dấu) có chứa từ khóa tìm kiếm không
                    String titleWithoutAccents = StringUtil.removeAccents(track.getName()).toLowerCase();
                    if (titleWithoutAccents.contains(searchTextWithoutAccents)) {
                        if (track != null) {
                            song.setTrackID(track.getTrackID());
                            song.setTrackName(track.getName());
                            song.setAlbumID(track.getAlubumID());
                            song.setFile(track.getFile());
                            service.getImageForTrack(track, new TrackService.OnImageLoadedListener() {
                                @Override
                                public void onImageLoaded(String imageUrl) {
                                    song.setImage(imageUrl);
                                }
                            });
                            service.getArtistNameByAlbumId(track.getAlubumID(), new TrackService.OnArtistNameLoadedListener() {
                                @Override
                                public void onArtistNameLoaded(String artistName) {
                                    song.setArtistName(artistName);
                                }
                            });

                            searchResults.add(song);
                        }
                    }
                }
                updateRecyclerView(searchResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AdminSongManager", "Failed to search songs.", databaseError.toException());
            }
        });
    }

    private void updateRecyclerView(List<SongManagerModel> songs) {
        adapter.setSongs(songs);
        _textTotalSongs.setText("Tổng số bài hát: " + adapter.getItemCount());
    }

    private void clearSearch() {
        _editTextSearch.setText("");
        getTracks();
    }
}