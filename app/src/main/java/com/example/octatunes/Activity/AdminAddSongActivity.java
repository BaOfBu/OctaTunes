package com.example.octatunes.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.octatunes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

public class AdminAddSongActivity extends AppCompatActivity {

    private EditText _edtTxtSongName, _edtTxtAlbumName, _edtTxtSongDuration;
    private Button _btnChooseFile, _btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_addsong);
        linktolayout();

        _btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent, 1);
            }
        });

        _btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songName = _edtTxtSongName.getText().toString();
                String albumName = _edtTxtAlbumName.getText().toString();
                String songDuration = _edtTxtSongDuration.getText().toString();

                // Save song details to database or file
                //...

                // Show a toast message to confirm that the song has been saved
                Toast.makeText(AdminAddSongActivity.this, "Song saved successfully!", Toast.LENGTH_SHORT).show();

                // Finish the activity
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data!= null) {
            Uri selectedFileUri = data.getData();

            // Get the file path from the URI
            String filePath = getFilePathFromUri(selectedFileUri);

            // Upload the file to Firebase Storage
            uploadFileToFirebaseStorage(filePath);
        }
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor!= null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

    private void uploadFileToFirebaseStorage(String filePath) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference songsRef = storageRef.child("tracks");
        StorageReference songFileRef = songsRef.child(UUID.randomUUID().toString() + ".mp3");

        UploadTask uploadTask = songFileRef.putFile(Uri.fromFile(new File(filePath)));

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful upload
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful upload
                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        // Save the download URL to a database or file
                    }
                });
            }
        });
    }

    private void linktolayout(){
        _edtTxtSongName = findViewById(R.id.songNameEditText);
        _edtTxtAlbumName = findViewById(R.id.albumNameEditText);
        _edtTxtSongDuration = findViewById(R.id.songDurationEditText);
        _btnChooseFile = findViewById(R.id.btn_choose_file);
        _btnSave = findViewById(R.id.btn_save);
    }
}