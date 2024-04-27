package com.example.octatunes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    private Button _btnSearch, _btnAddSong;
    private ImageView _btnRemoveSearch, _btnEditSong, _btnDeleteSong;
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

        // Xử lý sự kiện khi người dùng nhấn vào nút tìm kiếm
        _btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = _editTextSearch.getText().toString();
                searchSongs(keyword);
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
                startActivity(intent);
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

    private void removeTrack() {
        int position = _recyclerViewSongList.getChildAdapterPosition(_btnDeleteSong);
        String trackId = String.valueOf(songs.get(position).getTrackID());
        service.removeTrack(trackId, new TrackService.OnTrackRemovedListener() {
            @Override
            public void onTrackRemoved() {
                Toast.makeText(AdminSongManagerActivity.this, "Track đã được xóa.", Toast.LENGTH_SHORT).show();
                songs.remove(position);
                adapter.notifyItemRemoved(position);
                _textTotalSongs.setText("Tổng số bài hát: " + adapter.getItemCount());
            }
        });
    }
}