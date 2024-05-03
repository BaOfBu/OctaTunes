package com.example.octatunes.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerControlView;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AdminAddSongActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private EditText _edtTxtSongName, _edtTxtSongDuration;
    private TextView _txtPath;
    private Spinner _albumSpinner;
    private Button _btnChooseFile, _btnSave;
    private ImageView _btnBack, _btnNext, _btnPlay, _btnPrev;
    private Uri _audio, _audioPath;
    private List<String> albumNames;
    private int _nextID, _albumID;
    private PlayerControlView _playerControlView;
    private ExoPlayer _player;
    private SeekBar _seekBar;
    private int REQUEST_CODE = 1;
    private final Handler handler = new Handler();
    private boolean isUserSeeking = false;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @UnstableApi
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    _btnSave.setEnabled(true);
                    _audio = result.getData().getData();

                    // Lấy thời lượng của file audio
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(getApplicationContext(), _audio);
                    String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    int durationInMillis = Integer.parseInt(duration);

                    // Chuyển thời lượng từ milliseconds sang giây
                    int durationInSeconds = durationInMillis / 1000;
                    _edtTxtSongDuration.setText(String.valueOf(durationInSeconds));

                    _txtPath.setText(_audio.getPath());

                    // Trình phát nhạc
                    initPlayer();
                }
            }
        }
    });

    @UnstableApi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_addsong);
        linktolayout();
        Intent intent = getIntent();
        _nextID = Integer.parseInt(intent.getStringExtra("TOTAL_TRACKS_CUR")) + 1;
        albumNames = new ArrayList<>();
        _player = new ExoPlayer.Builder(this).build();

        // Đặt sự kiện click cho các nút điều khiển
        _btnPlay.setOnClickListener(view -> {
            if (_player.isPlaying()) {
                _player.pause();
                _btnPlay.setImageResource(R.drawable.ic_circle_play); // Đặt biểu tượng cho play khi tạm dừng
            } else {
                if (_player.getCurrentPosition() >= _player.getDuration()) {
                    // Nếu đã nghe hết, quay lại đầu và phát lại
                    _player.seekTo(0);
                    _btnPlay.setImageResource(R.drawable.ic_circle_play);
                }
                _player.play();
                _btnPlay.setImageResource(R.drawable.ic_circle_pause); // Đặt biểu tượng cho tạm dừng khi phát
            }
        });

        _btnPrev.setOnClickListener(view -> {
            // Xử lý sự kiện tua lại
            _player.seekTo(_player.getCurrentPosition() - 10000); // tua lại 10 giây
        });

        _btnNext.setOnClickListener(view -> {
            // Xử lý sự kiện tua tới
            _player.seekTo(_player.getCurrentPosition() + 10000); // tua tới 10 giây
        });

        // Đặt trình nghe sự kiện cho thanh seekbar
        _player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                updateSeekBar();
            }
        });

        _seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    isUserSeeking = true;
                    _player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
            }
        });

        // Khởi tạo firebase storage
        FirebaseApp.initializeApp(AdminAddSongActivity.this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference.child("albums").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                AlbumsModel album = snapshot.getValue(AlbumsModel.class);
                if (album != null)
                    albumNames.add(album.getName());

                // Tạo Adapter cho Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminAddSongActivity.this,
                        R.layout.layout_spinner_item, albumNames);
                adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
                // Cài Adapter cho Spinner
                _albumSpinner.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        _albumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String albumName = parent.getItemAtPosition(position).toString();
                Query albumRef = databaseReference.child("albums").orderByChild("name").equalTo(albumName);
                albumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot albumSnapshot : snapshot.getChildren()) {
                                AlbumsModel album = albumSnapshot.getValue(AlbumsModel.class);
                                _albumID = album.getAlbumID(); // Sử dụng albumId tại đây break; // Thêm dòng này để thoát khỏi vòng lặp sau khi tìm thấy albumID } } }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        _btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _player.release();
                Intent intent = new Intent(AdminAddSongActivity.this, AdminSongManagerActivity.class);
                startActivity(intent);
            }
        });

        _btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                activityResultLauncher.launch(intent);
            }
        });

        _btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songName = _edtTxtSongName.getText().toString();
                int songDuration = Integer.parseInt(_edtTxtSongDuration.getText().toString());

                uploadAudio(_audio, songName, songDuration);

                Intent intent = new Intent(AdminAddSongActivity.this, AdminSongManagerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng tài nguyên khi không cần thiết
        _player.release();
    }

    private void updateSeekBar() {
        _seekBar.setMax((int) _player.getDuration());
        _seekBar.setProgress((int) _player.getCurrentPosition());

        if (!isUserSeeking) {
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    @UnstableApi
    private void initPlayer() {
        // Tạo MediaItem từ URI của file audio
        MediaItem mediaItem = MediaItem.fromUri(_audio);

        // Set MediaItem cho ExoPlayer
        _player.setMediaItem(mediaItem);

        // Liên kết ExoPlayer với PlayerControlView
        _playerControlView.setPlayer(_player);

        // Bắt đầu phát âm nhạc
        _player.prepare();

        // Đặt trình nghe sự kiện để theo dõi sự thay đổi trong trạng thái phát nhạc

    }

    private void uploadAudio(Uri audio, String SongName, int songDuration) {
        StorageReference reference = storageReference.child("musics/" + String.format("%03d", _nextID) + "_" + Normalizer.normalize(SongName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replace(" ", "") + ".mp3");

        reference.putFile(audio)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    urlTask.addOnSuccessListener(downloadUrl -> {
                        _audioPath = downloadUrl;

                        TracksModel newTrack = new TracksModel(_nextID, _albumID, SongName, songDuration, _audioPath.toString());

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference NewTrackRef = database.getReference("tracks");
                        NewTrackRef.push().setValue(newTrack)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AdminAddSongActivity.this, "Audio upload successfully!!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AdminAddSongActivity.this, "Failed to save the track to the database", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminAddSongActivity.this, "Audio upload error!!!", Toast.LENGTH_SHORT).show();
                });
    }

    private void linktolayout() {
        _edtTxtSongName = findViewById(R.id.songNameEditText);
        _albumSpinner = findViewById(R.id.albumNameSpinner);
        _edtTxtSongDuration = findViewById(R.id.songDurationEditText);
        _txtPath = findViewById(R.id.textPath);
        _btnChooseFile = findViewById(R.id.btn_choose_file);
        _btnSave = findViewById(R.id.btn_save);
        _btnBack = findViewById(R.id.btn_back);
        _playerControlView = findViewById(R.id.exo_controller);
        _btnNext = findViewById(R.id.btn_next);
        _btnPlay = findViewById(R.id.btn_play);
        _btnPrev = findViewById(R.id.btn_prev);
        _seekBar = findViewById(R.id.seekBar);
    }
}