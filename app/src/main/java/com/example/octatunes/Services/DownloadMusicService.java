package com.example.octatunes.Services;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.octatunes.Activity.ListenToMusicActivity;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;

public class DownloadMusicService extends Service {
    private SongModel song;
    private long downloadId;
    private BroadcastReceiver downloadReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        downloadFile();
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (downloadId != -1) {
                        // Display a notification
                        Toast.makeText(getApplicationContext(), "Download finish", Toast.LENGTH_SHORT).show();
                        showNotification(context, "Download completed", "Your file has been downloaded.");
                    }
                }
            }
        };
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.remove(downloadId);
        if (downloadReceiver != null) {
            unregisterReceiver(downloadReceiver);
        }
        super.onDestroy();
    }

    private void showNotification(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "DownloadMusic")
                .setSmallIcon(R.drawable.ic_spotify_white)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(123, builder.build());
    }

    private void downloadFile() {
        SongModel songModel = ListenToMusicActivity.currentSong;
        String downloadUrl = songModel.getFile();
        String songTitle = songModel.getTitle();
        String filename = getFilenameFromUrl(downloadUrl);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        // Set title and description for the download notification (optional)
        request.setTitle(songTitle);
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
}
