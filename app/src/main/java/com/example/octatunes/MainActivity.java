package com.example.octatunes;

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
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.octatunes.Activity.HomeActivity;
import com.example.octatunes.Activity.ListenToMusicActivity;
import com.example.octatunes.Activity.LibraryFragment;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Services.AlbumService;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.MusicService;
import com.example.octatunes.Services.SongService;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FragmentListener {
    private static final String TAG = "MainActivity";
    private static final int UPDATE = 1;
    public ActivityMainBinding binding;
    public static List<SongModel> songList;
    private int pos = -1;
    private MusicService.MusicBinder binder;
    private static boolean isServiceBound = false;
    private TrackService trackService;
    private AlbumService albumService;
    private ArtistService artistService;
    SongService songService;
    private static String from;
    private static String belong;
    public static Fragment lastFrag;
    private Thread myThread;
    private Intent intent;
    public static List<SongModel> getSongList(){
        return songList;
    }
    public static boolean isServiceBound(){
        return isServiceBound;
    }
    public static void setFrom(String f){
        from = f;
    }
    public static void setBelong(String b){
        belong = b;
    }
    public ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MusicService.MusicBinder) service;
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        songList = new ArrayList<>();
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        lastFrag = new HomeActivity();
        replaceFragment(lastFrag);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemID = item.getItemId();
            if(itemID == R.id.home){
                lastFrag = new HomeActivity();
                replaceFragment(lastFrag);
            }else if(itemID == R.id.search){
                lastFrag = new SearchingActivity();
                replaceFragment(lastFrag);
            }else if(itemID == R.id.library){
                replaceFragment(new LibraryFragment());
            }else if(itemID == R.id.Premium){

            }
            return true;
        });
        trackService = new TrackService();
        songService = new SongService();
        artistService = new ArtistService();
        albumService = new AlbumService();

        binding.frameLayout.setOnClickListener(this);
        binding.trackPlayPause.setOnClickListener(this);

        Search(26, 11, 4, "PLAYING FROM SEARCH", "\"Như+ngày+hôm+qua\" in Search", null);
    }

    public void Search(int trackID, int playlistID, int albumID, String from, String belong, String mode){
        setFrom(from);
        setBelong(belong);

//        if (mode != null && mode.equals("shuffle")){
//            binder.setRandomPlay();
//        }

        if(!Objects.equals(from, "PLAYING FROM ALBUM")){
            loadData(playlistID, trackID);
        }else{
            loadDataFromAlbum(albumID, trackID);
        }
    }

    private void loadDataFromAlbum(int albumID, int trackID){
        trackService.getTracksByAlbumId(albumID).thenAccept(tracksModels -> {
            albumService.findAlbumById(albumID).thenAccept(albumsModel ->{
               artistService.findArtistById(albumsModel.getArtistID()).thenAccept(artistsModel -> {
                   if(songList != null) songList.clear();
                   for(int i = 0; i < tracksModels.size(); i++){
                       TracksModel track = tracksModels.get(i);
                       Log.i("TRACK SERVICE IN MAIN", track.toString());
                       if(track.getTrackID() == trackID) pos = i;
                       SongModel songModel = new SongModel(track.getName(), artistsModel.getName(), albumsModel.getName(), artistsModel.getGenre(), albumsModel.getImage(), track.getFile(), track.getDuration());
                       songList.add(songModel);
                   }
                   myThread=new Thread(new MainActivity.MyThread());
                   myThread.start();

                   if(intent == null) {
                       intent = new Intent(MainActivity.this, MusicService.class);
                       bindService(intent, connection, BIND_AUTO_CREATE);
                   }else{
                       connection = new ServiceConnection() {
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
                       startService(intent);
                       bindService(intent, connection, BIND_AUTO_CREATE);
                   }

                   MusicService.setPos(pos);
                   initNowPlayingBar();
               }) ;
            });
        });
    }
    private void loadData(int playlistID, int trackID){
        trackService.getTracksByPlaylistId(playlistID).thenAccept(tracksModels -> {
            int totalTracks = tracksModels.size();
            AtomicInteger processedTracks = new AtomicInteger(0);
            for (int i = 0; i < tracksModels.size(); i++) {
                TracksModel track = tracksModels.get(i);
                Log.i("TRACK SERVICE IN MAIN", track.toString());

                if(track.getTrackID() == trackID) pos = i;

                SongModel songModel = new SongModel();
                songModel.setTitle(track.getName());
                songModel.setFile(track.getFile());
                songModel.setDuration(track.getDuration());
                if(songList != null) songList.clear();

                // Fetch album asynchronously and set properties of songModel
                albumService.findAlbumById(track.getAlubumID()).thenAccept(albumsModel -> {
                    songModel.setAlbum(albumsModel.getName());
                    songModel.setImage(albumsModel.getImage());

                    Log.i("ALBUM SERVICE IN MAIN", "Fetch album asynchronously and set properties of songModel");
                    // Fetch artist asynchronously and set properties of songModel
                    artistService.findArtistById(albumsModel.getArtistID()).thenAccept(artistsModel -> {
                        songModel.setArtist(artistsModel.getName());
                        songModel.setGenre(artistsModel.getGenre());

                        // Add songModel to MainActivity.songList after all data is loaded
                        songList.add(songModel);
                        int processed = processedTracks.incrementAndGet();
                        if (processed == totalTracks) {
                            // All tracks have been processed
                            Log.i(TAG, songList.toString());

                            myThread=new Thread(new MainActivity.MyThread());
                            myThread.start();

                            if(intent == null) {
                                intent = new Intent(MainActivity.this, MusicService.class);
                                startService(intent);
                                bindService(intent, connection, BIND_AUTO_CREATE);
                            }else{
                                connection = new ServiceConnection() {
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
                                bindService(intent, connection, BIND_AUTO_CREATE);
                            }

                            MusicService.setPos(pos);

                            initNowPlayingBar();
                        }
                    });
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.frame_layout){
            SongModel currentSong = songList.get(pos);
            Fragment fragment = new ListenToMusicActivity(from, belong, currentSong);
            replaceFragment(fragment);
            binding.frameLayout.setVisibility(View.GONE);
            binding.bottomNavigation.setVisibility(View.GONE);
        }else if(id == R.id.track_play_pause){
            binder.playMusic();
            if(MusicService.mediaPlayer.isPlaying()){
                binding.trackPlayPause.setImageResource(R.drawable.ic_pause_white_24);
            }else{
                binding.trackPlayPause.setImageResource(R.drawable.ic_play_white_24);
            }
        }
    }

    @Override
    public void onSignalReceived(int trackID, int playlistID, int albumID, String from, String belong, String mode) {
        Log.i("SIGNAL RECEIVED IN MAIN", "SUCCESS");
        if (myThread != null) {
            myThread.interrupt();
            myThread = null;
        }
        if (isServiceBound) {
            unbindService(connection);
            isServiceBound = false;
        }
        Search(trackID, playlistID, albumID, from, belong, mode);
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
                        initNowPlayingBar();
                    }
                    if(MusicService.mediaPlayer.isPlaying()){
                        binding.trackPlayPause.setImageResource(R.drawable.ic_pause_white_24);
                    }else{
                        binding.trackPlayPause.setImageResource(R.drawable.ic_play_white_24);
                    }
                    binding.seekBar.setProgress(MusicService.mediaPlayer.getCurrentPosition());
                    break;
                default:
            }
        }
    };
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
    public void initNowPlayingBar(){
        if(songList!=null) {
            Glide.with(MainActivity.this).load(songList.get(pos).getImage()).into(binding.trackImage);
            binding.trackName.setText(songList.get(pos).getTitle());
            binding.trackArtist.setText(songList.get(pos).getArtist());

            int totalTime = songList.get(pos).getDuration() * 1000; //miliseconds

            binding.seekBar.setMax(totalTime);
        }
    }
}
