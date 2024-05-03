package com.example.octatunes.Services;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.octatunes.Activity.ListenToMusicActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;
import com.google.firebase.database.*;

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
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String songTitle = intent.getStringExtra("SONG_TITLE");
        String songFile = intent.getStringExtra("SONG_FILE");
        downloadFile(songTitle, songFile);
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadFile(String title, String file) {
        String filename = getFilenameFromUrl(file);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(file));

        // Set title and description for the download notification (optional)
        request.setTitle(title);
        request.setDescription("Downloading audio file...");

        // Set destination directory and file name for the downloaded file
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

        // Enqueue the download and get a download ID
        downloadId = downloadManager.enqueue(request);
    }

    private String getFilenameFromUrl(String Url){
        // Split the URL by '/'
        String[] parts = Url.split("/");

        // Get the last part which contains the filename
        String filenameWithToken = parts[parts.length - 1];

        // Remove the token part
        String[] filenameParts = filenameWithToken.split("\\?");

        // The filename is the first part before the '?'
        String filename = filenameParts[0];
        return filename;
    }

    @Override
    public void onDestroy() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.remove(downloadId);
        super.onDestroy();
    }
}
