package com.example.octatunes.Services;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private BroadcastReceiver musicReceiver;

    public class MusicBinder extends Binder {

        public void setMediaPlayer(int position){
            try {
                pos = position;
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

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void playMusic(){
            if(mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        }
        public void previousMusic(){
            if(mediaPlayer!=null) {
                if (pos == 0) {
                    pos = songList.size() - 1;
                } else {
                    pos--;
                }
                setMediaPlayer(pos);
            }
        }
        public void nextMusic(){
            if(mediaPlayer!=null) {
                if (pos == songList.size() - 1) {
                    pos = 0;
                } else {
                    pos++;
                }
                setMediaPlayer(pos);
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }
    @SuppressLint("RemoteViewLayout")
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();

        this.songList= MainActivity.getSongList();

        if(!MainActivity.isServiceBound()){
            musicBinder.setMediaPlayer(pos);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Intent clickIntent=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,clickIntent, PendingIntent.FLAG_IMMUTABLE);

        musicReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i(TAG, "Received broadcast with action: " + action);
                if(action.equals("previousMusic")){
                    musicBinder.previousMusic();
                }else if(action.equals("playMusic")){
                    musicBinder.playMusic();
                }else if(action.equals("nextMusic")){
                    musicBinder.nextMusic();
                }else if (action.equals("quit")) {
                    stopForeground(true);
                    stopSelf();
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("previousMusic");
        intentFilter.addAction("playMusic");
        intentFilter.addAction("nextMusic");
        intentFilter.addAction("quit");
        registerReceiver(musicReceiver,intentFilter, Context.RECEIVER_NOT_EXPORTED);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}