package com.example.octatunes.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ImageView _btnBack;
    private Uri _audio, _audioPath;
    private List<String> albumNames;
    private int _nextID, _albumID;

    private int REQUEST_CODE = 1;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    _btnSave.setEnabled(true);
                    _audio = result.getData().getData();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_addsong);
        linktolayout();
        Intent intent = getIntent();
        _nextID = Integer.parseInt(intent.getStringExtra("TOTAL_TRACKS_CUR")) + 1;
        albumNames = new ArrayList<>();

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

    private void uploadAudio(Uri audio, String SongName, int songDuration) {
        StorageReference reference = storageReference.child("musics/" + String.format("%03d", _nextID) + "_" + Normalizer.normalize(SongName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replace(" ","") + ".mp3");

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
    }
}