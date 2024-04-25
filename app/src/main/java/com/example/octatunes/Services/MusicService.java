package com.example.octatunes.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.octatunes.Activity.ListenToMusicActivity;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.UserSongModel;
import com.example.octatunes.Model.UsersModel;
import com.example.octatunes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    public  static MediaPlayer mediaPlayer;
    private MusicBinder musicBinder = new MusicBinder();
    private static List<SongModel> songList = new ArrayList<>();
    private static int pos;
    public static int getPos() {
        return pos;
    }
    public static void setPos(int currentPos) {
        pos = currentPos;
    }
    public static List<SongModel> getSongList(){
        return songList;
    }
    private boolean singlePlay = false;
    private boolean randomPlay = false;
    private boolean sequencePlay = false;
    private RemoteViews views;
    private Notification notification;
    private NotificationManager notificationManager;
    private BroadcastReceiver musicReceiver;
    final private int notificationId = 88;
    private SongService songService;
    public class MusicBinder extends Binder {
        public void setSinglePlay(){
            singlePlay = true;
            randomPlay = false;
            sequencePlay = false;
        }

        public void setRandomPlay(){
            singlePlay = false;
            randomPlay = true;
            sequencePlay = false;
        }

        public void setSequencePlay(){
            singlePlay = false;
            randomPlay = false;
            sequencePlay = true;
        }
        public boolean isSinglePlay(){
            return singlePlay;
        }
        public boolean isRandomPlay(){
            return randomPlay;
        }
        public boolean isSequencePlay(){
            return sequencePlay;
        }
        public void setMediaPlayer(int position){
            if (songList != null && !songList.isEmpty() && position >= 0 && position < songList.size()) {
                try {
                    pos = position;

                    SongModel songCurrent = songList.get(pos);
                    songService.addSong(songCurrent);

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {
                        String uid = user.getUid();
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UsersModel userModel = dataSnapshot.getValue(UsersModel.class);
                                    if (userModel != null) {
                                        int userId = userModel.getUserID();
                                        UserSongModel userSongModel = new UserSongModel(userId, songCurrent.getSongID(), new Date());
                                        UserSongService userSongService = new UserSongService();
                                        userSongService.addUserSong(userSongModel);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                            }
                        });
                    }

                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(songList.get(pos).getFile());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if(sequencePlay) {
                                nextMusic();
                            }else if(singlePlay){
                                setMediaPlayer(pos);
                            }else if(randomPlay){
                                Random random = new Random();
                                int i = random.nextInt(songList.size());
                                setMediaPlayer(i);
                            }else {
                                nextMusic();
                            }
                            updateTrackView();
                        }
                    });

                    updateNotification();
                } catch (IOException e) {
                    Log.i(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }
        }

        public void playMusic(){
            if(mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
                updateNotification();

            }
        }
        public void previousMusic(){
            if(mediaPlayer!=null) {
                if(randomPlay) {
                    if(pos != 0){
                        pos--;
                    }
                }else{
                    if (pos == 0) {
                        pos = songList.size() - 1;
                    } else {
                        pos--;
                    }
                }
                setMediaPlayer(pos);
                updateNotification();

            }
        }
        public void nextMusic(){
            if(mediaPlayer!=null) {
                if(randomPlay) {
                    Random random = new Random();
                    int i = random.nextInt(songList.size());
                    setMediaPlayer(i);
                } else if (singlePlay) {
                    ListenToMusicActivity.repeat.setImageResource(R.drawable.ic_repeat_clicked_one_green_24);
                    ListenToMusicActivity.chosenRepeatOneSong = false;
                    if(ListenToMusicActivity.chosenShuffle){
                        setRandomPlay();
                    }else{
                        setSequencePlay();
                    }
                } else {
                    if (pos == songList.size() - 1) {
                        pos = 0;
                    } else {
                        pos++;
                    }
                    setMediaPlayer(pos);
                }
                updateNotification();

            }
        }

        private void updateTrackView(){
            ListenToMusicActivity.currentSong = songList.get(pos);
            ListenToMusicActivity.initMediaPlayer();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();

        songList = MainActivity.getSongList();

        if (songList == null) {
            Log.e(TAG, "Song list is null");
        } else {
            if (!MainActivity.isServiceBound()) {
                songService = new SongService();
                musicBinder.setMediaPlayer(pos);
                Log.i(TAG, "ONCREATE");
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint({"RemoteViewLayout", "ForegroundServiceType"})
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Intent clickIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0, clickIntent, PendingIntent.FLAG_IMMUTABLE);

        views = new RemoteViews(this.getPackageName(), R.layout.layout_notification);
        Intent intentPrevious = new Intent("previousMusic");
        PendingIntent previousPi = PendingIntent.getBroadcast(this,1, intentPrevious, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.imageButtonPrevious,previousPi);

        Intent intentPlay = new Intent("playMusic");
        PendingIntent playPi = PendingIntent.getBroadcast(this,2, intentPlay, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.imageButtonPlayPause,playPi);

        Intent intentNext = new Intent("nextMusic");
        PendingIntent nextPi = PendingIntent.getBroadcast(this,3, intentNext, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.imageButtonNext,nextPi);

        Intent intentShuffle = new Intent("shuffleMusic");
        PendingIntent shufflePi = PendingIntent.getBroadcast(this,4, intentShuffle, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.imageButtonShuffle, shufflePi);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String CHANNEL_ID = "MusicPlayer";
            String CHANNEL_NAME = "OctaTunes";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);

            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_spotify_white)
                    .setCustomBigContentView(views)
                    .setCustomContentView(views)
                    .setContentIntent(pi)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .build();

            Log.i("MUSIC SERVICE", "SUCCESS CREATE NOTIFICATION");
            updateNotification();
            views.setImageViewResource(R.id.imageButtonPlayPause, R.drawable.ic_circle_pause_white_70);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        }

        musicReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Received: " + intent.getAction());
                switch (Objects.requireNonNull(intent.getAction())){
                    case "previousMusic":
                        views.setImageViewResource(R.id.imageButtonPlayPause, R.drawable.ic_circle_pause_white_70);
                        musicBinder.previousMusic();
                        break;
                    case "playMusic":
                        if(mediaPlayer.isPlaying()){
                            views.setImageViewResource(R.id.imageButtonPlayPause, R.drawable.ic_circle_play_white_70);
                        }else {
                            views.setImageViewResource(R.id.imageButtonPlayPause, R.drawable.ic_circle_pause_white_70);
                        }
                        musicBinder.playMusic();
                        break;
                    case "nextMusic":
                        views.setImageViewResource(R.id.imageButtonPlayPause, R.drawable.ic_circle_pause_white_70);
                        musicBinder.nextMusic();
                        break;
                    case "shuffleMusic":

                        if(!musicBinder.isRandomPlay()){
                            musicBinder.setRandomPlay();
                        }else {
                            musicBinder.setSequencePlay();
                        }
                        if(musicBinder.isRandomPlay()){
                            views.setImageViewResource(R.id.imageButtonShuffle, R.drawable.ic_shuffle_white_24);
                        }else{
                            views.setImageViewResource(R.id.imageButtonShuffle, R.drawable.ic_shuffle_clicked_green_24);
                        }

                        break;
                    default:
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("previousMusic");
        intentFilter.addAction("playMusic");
        intentFilter.addAction("nextMusic");
        intentFilter.addAction("shuffleMusic");
        registerReceiver(musicReceiver,intentFilter, Context.RECEIVER_EXPORTED);
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateNotification(){
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if(notification != null){
            if(views!=null){
                if(songList != null && !songList.isEmpty() && pos >= 0 && pos < songList.size()){
                    views.setTextViewText(R.id.textView_trackTitle,songList.get(pos).getTitle());
                    views.setTextViewText(R.id.textView_artistName,songList.get(pos).getArtist());

                    Glide.with(this)
                            .asBitmap()
                            .load(songList.get(pos).getImage())
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    // Đặt Bitmap vào ImageView trong RemoteViews
                                    views.setImageViewBitmap(R.id.imageAlbum, resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Handle case where Glide clears the Bitmap
                                }
                            });
                }
            }
            notificationManager.notify(notificationId, notification);
        }else {
            // Handle the case where notification is null
            Log.e(TAG, "Notification is null");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        stopForeground(true);
        if(notificationManager!=null){
            notificationManager.cancel(notificationId);
        }
        if(musicReceiver!=null) {
            unregisterReceiver(musicReceiver);
        }
    }
}