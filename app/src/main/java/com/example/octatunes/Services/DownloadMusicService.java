package com.example.octatunes.Services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.octatunes.Activity.ListenToMusicActivity;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;

public class DownloadMusicService extends Service {
    private SongModel song;
    private long downloadId;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        song = ListenToMusicActivity.currentSong;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId){
        downloadFile(song);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy(){
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.remove(downloadId);
        super.onDestroy();
    }
    public void downloadFile(SongModel songModel) {
        String downloadUrl = songModel.getFile();


        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        // Set title and description for the download notification (optional)
        request.setTitle("Downloading Audio");
        request.setDescription("Downloading audio file...");

        // Set destination directory and file name for the downloaded file
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloaded_audio.mp3");

        // Enqueue the download and get a download ID
        downloadId = downloadManager.enqueue(request);

        Toast.makeText(getApplicationContext(), "File download enqueued with ID: " + downloadId, Toast.LENGTH_SHORT).show();
    }
}
