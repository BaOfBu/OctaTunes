package com.example.octatunes.Activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.AlbumService;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.MusicService;
import com.example.octatunes.Services.SongService;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.Utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
public class ListenToMusicActivity extends AppCompatActivity implements View.OnClickListener {
    int trackID;
    int playlistID;
    int albumID;
    String from;
    String belong;
    TextView track_from;
    TextView track_belong;
    private ImageView imageView;
    private TextView songName;
    private TextView singer;
    private ImageButton play;
    private List<SongModel> songList = new ArrayList<>();
    private static final int UPDATE = 0;
    private MusicService.MusicBinder binder;
    private Thread myThread;
    private int pos = -1;
    TrackService trackService;
    AlbumService albumService;
    ArtistService artistService;
    SongService songService;
    public SeekBar seekBar;
    private TextView playTime;
    private TextView duration;
    private static boolean isServiceBound = false;
    public static boolean isServiceBound(){
        return isServiceBound;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_track_view);

        Intent i = getIntent();
        if (i != null) {
            Bundle extras = i.getExtras();
            if (extras != null) {
                trackID = extras.getInt("trackID");
                playlistID = extras.getInt("playlistID");
                albumID = extras.getInt("albumID");
                from = extras.getString("from");
                belong = extras.getString("belong");
            }
        }

        track_from = findViewById(R.id.track_from);
        track_belong = findViewById(R.id.track_belong);
        imageView = findViewById(R.id.imageViewAlbumArt);
        songName = findViewById(R.id.textViewTrackTitle);
        singer = findViewById(R.id.textViewArtistName);
        play = findViewById(R.id.imageButtonPlayPause);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        playTime=(TextView) findViewById(R.id.elapsedTime);
        duration = (TextView) findViewById(R.id.remainingTime);

        trackService = new TrackService();
        songService = new SongService();
        artistService = new ArtistService();
        albumService = new AlbumService();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    MusicService.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MusicService.mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.mediaPlayer.start();
            }
        });

        track_from.setText(from);
        track_belong.setText(belong);

        play.setOnClickListener(this);

        loadData();
    }
    private class MyThread implements Runnable{
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                if(MusicService.mediaPlayer!= null ) {
                    Message message = new Message();
                    message.what = UPDATE;
                    message.arg1 = MusicService.getPos();
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE:
                    if(pos!=msg.arg1){
                        pos=msg.arg1;
                        initMediaPlayer();
                    }
                    playTime.setText(MusicUtils.formatTime(MusicService.mediaPlayer.getCurrentPosition()));
                    seekBar.setProgress(MusicService.mediaPlayer.getCurrentPosition());
                    break;
                default:
            }
        }
    };

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder=(MusicService.MusicBinder) service;
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.imageButtonPlayPause){
            binder.playMusic();
            if(MusicService.mediaPlayer.isPlaying()){
                play.setImageResource(R.drawable.ic_circle_pause_white_70);
            }else {
                play.setImageResource(R.drawable.ic_circle_play_white_70);
            }
        }
    }
    private void loadData(){
        trackService.getTracksByPlaylistId(playlistID).thenAccept(tracksModels -> {
            int totalTracks = tracksModels.size();
            AtomicInteger processedTracks = new AtomicInteger(0);
            for (int i = 0; i < tracksModels.size(); i++) {
                TracksModel track = tracksModels.get(i);
                Log.i("TRACK SERVICE", track.toString());

                if(track.getTrackID() == trackID) pos = i;

                SongModel songModel = new SongModel();
                songModel.setTitle(track.getName());
                songModel.setFile(track.getFile());
                songModel.setDuration(track.getDuration());

                // Fetch album asynchronously and set properties of songModel
                albumService.findAlbumById(track.getAlubumID()).thenAccept(albumsModel -> {
                    songModel.setAlbum(albumsModel.getName());
                    songModel.setImage(albumsModel.getImage());

                    Log.i("ALBUM SERVICE", "Fetch album asynchronously and set properties of songModel");
                    // Fetch artist asynchronously and set properties of songModel
                    artistService.findArtistById(albumsModel.getArtistID()).thenAccept(artistsModel -> {
                        songModel.setArtist(artistsModel.getName());
                        songModel.setGenre(artistsModel.getGenre());

                        // Add songModel to MainActivity.songList after all data is loaded
                        MainActivity.songList.add(songModel);
                        int processed = processedTracks.incrementAndGet();
                        if (processed == totalTracks) {
                            // All tracks have been processed
                            Log.i("MAIN", MainActivity.songList.toString());

                            myThread=new Thread(new MyThread());
                            myThread.start();

                            songList = MainActivity.getSongList();

                            Intent intent=new Intent(ListenToMusicActivity.this, MusicService.class);
                            bindService(intent,connection,BIND_AUTO_CREATE);

                            MusicService.setPos(pos);

                            initMediaPlayer();
                        }
                    });
                });
            }
        });
    }

    private void initMediaPlayer(){
        Glide.with(ListenToMusicActivity.this).load(songList.get(pos).getImage()).into(imageView);
        songName.setText(songList.get(pos).getTitle());
        singer.setText(songList.get(pos).getArtist());
        int totalTime = songList.get(pos).getDuration() * 1000; //miliseconds

        String time = MusicUtils.formatTime(totalTime);
        duration.setText(time);

        seekBar.setMax(totalTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        myThread.interrupt();
    }
}
