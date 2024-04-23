package com.example.octatunes.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import com.example.octatunes.Activity.ListenToMusicActivity;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        this.songList= MainActivity.getSongList();

        if(!MainActivity.isServiceBound()){
            musicBinder.setMediaPlayer(pos);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

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