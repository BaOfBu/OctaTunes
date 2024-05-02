package com.example.octatunes;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FragmentListener {
    private static final String TAG = "MainActivity";
    private static final int UPDATE = 1;
    public ActivityMainBinding binding;
    public static List<SongModel> songList;
    private static int pos = -1;
    public static void setPos(int p){
        pos = p;
    }
    private MusicService.MusicBinder binder;
    private static boolean isServiceBound = false;
    private TrackService trackService;
    private AlbumService albumService;
    private ArtistService artistService;
    SongService songService;
    private static String from;
    private static String belong;
    public static Fragment lastFrag;
    public static Fragment trackFrag = null;
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
                lastFrag = new LibraryFragment();
                replaceFragment(lastFrag);
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

        binding.frameLayout.setVisibility(View.GONE);
    }

    public void Search(List<TracksModel> tracksModels, int trackID, int playlistID, int albumID, String from, String belong, String mode){
        setFrom(from);
        setBelong(belong);

        if(from.equals("PLAYING FROM ALBUM")){
            loadDataFromAlbum(albumID, trackID);
        }else if(from.equals("PLAYING FROM SEARCH")){
            loadDataFromAlbum(albumID, trackID);
        }else if(from.equals("PLAYING FROM PLAYLIST")){
            if(belong.equals("Liked Songs")){
                loadDataFromPlaylist(playlistID, trackID);
            }else{
                loadData(tracksModels, trackID);
            }
        }
    }

    private void loadDataFromAlbum(int albumID, int trackID){
        final boolean flag = !songList.isEmpty();
        if(!songList.isEmpty()) songList.clear();

        trackService.getTracksByAlbumId(albumID).thenAccept(tracksModels -> {
            albumService.findAlbumById(albumID).thenAccept(albumsModel ->{
               artistService.findArtistById(albumsModel.getArtistID()).thenAccept(artistsModel -> {
                   for(int i = 0; i < tracksModels.size(); i++){
                       TracksModel track = tracksModels.get(i);
                       if(track != null) {
                           Log.i("TRACK SERVICE IN MAIN", track.toString());
                           if(track.getTrackID() == trackID) pos = i;
                           SongModel songModel = new SongModel(track.getName(), artistsModel.getName(), albumsModel.getName(), artistsModel.getGenre(), albumsModel.getImage(), track.getFile(), track.getDuration());
                           songList.add(songModel);
                       }
                   }

                   myThread = new Thread(new MainActivity.MyThread());
                   myThread.start();

                   MusicService.setPos(pos);

                   intent = new Intent(MainActivity.this, MusicService.class);
                   startService(intent);

                   bindService(intent, connection, BIND_AUTO_CREATE);

                   pos = MusicService.getPos();
                   if(flag){
                       binder.setMediaPlayer(pos);
                   }
                   initNowPlayingBar();
               }) ;
            });
        });
    }
    private void loadData(List<TracksModel> tracksModels, int trackID){
        final boolean flag = !songList.isEmpty();
        if(!songList.isEmpty()) songList.clear();
        int totalTracks = tracksModels.size();
        AtomicInteger processedTracks = new AtomicInteger(0);
        for (int i = 0; i < tracksModels.size(); i++) {
            TracksModel track = tracksModels.get(i);
            SongModel songModel = new SongModel();
            if(track != null){
                Log.i("TRACK SERVICE IN MAIN", track.toString());
                songModel.setTitle(track.getName());
                songModel.setFile(track.getFile());
                songModel.setDuration(track.getDuration());
                songModel.setSongID(track.getTrackID());
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
                            Log.i(TAG, "Song List initial: " + songList.toString());

                            // All tracks have been processed
                            List<SongModel> songsTmp = new ArrayList<>(songList);
                            for(int j = 0; j < tracksModels.size(); j++){
                                for(int z = 0; z < songsTmp.size(); z++){
                                    if(songsTmp.get(z).getSongID() == tracksModels.get(j).getTrackID()){
                                        songList.set(j, songsTmp.get(z));
                                        if(songsTmp.get(z).getSongID() == trackID) {
                                            pos = j;
                                        }
                                        break;
                                    }
                                }
                            }

                            if(songList != null) Log.i("SONG LIST MAIN BEFORE", songList.toString());

                            myThread = new Thread(new MainActivity.MyThread());
                            myThread.start();

                            MusicService.setPos(pos);

                            isServiceBound = false;
                            intent = new Intent(MainActivity.this, MusicService.class);
                            startService(intent);
                            bindService(intent, connection, BIND_AUTO_CREATE);

                            Log.i(TAG, "ĐÃ VÔ HÀM LOAD DATA");
                            Log.i("POS SERVICE", String.valueOf(MusicService.getPos()));

                            pos = MusicService.getPos();

                            if(songList != null) Log.i("SONG LIST MAIN AFTER", songList.toString());

                            if(flag){
                                List<SongModel> newList = MusicService.loadSongQueue(pos);

                                MusicService.setSongList(newList);
                                MusicService.setPos(newList.size() - 1);

                                songList = newList;

                                setPos(newList.size() - 1);

                                binder.setMediaPlayer(pos);

                                initNowPlayingBar();
                                Log.i("FLAG", songList.toString());
                            }
                        }
                    });
                });
            }
        }
    }

    private void loadDataFromPlaylist(int playlistID, int trackID){
        trackService.getTracksByPlaylistId(playlistID).thenAccept(tracksModels -> {
            loadData(tracksModels, trackID);
        });
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.frame_layout){
            SongModel currentSong = songList.get(pos);
            Fragment fragment = new ListenToMusicActivity(from, belong, currentSong);
            trackFrag = fragment;
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
    public void onSignalReceived2(List<TracksModel> tracksModels, int trackID, String from, String belong, String mode) {
        Log.i("SIGNAL RECEIVED IN MAIN", "SUCCESS");
        if (myThread != null) {
            myThread.interrupt();
            myThread = null;
        }
        if (isServiceBound) {
            unbindService(connection);
            isServiceBound = false;
        }
        Search(tracksModels, trackID, -1, -1, from, belong, mode);
        if(binding.frameLayout.getVisibility() == View.GONE){
            binding.frameLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onSignalReceived3() {
        songList = MusicService.getSongList();
        setPos(MusicService.getPos());

        myThread = new Thread(new MyThread());
        myThread.start();

        isServiceBound = false;
        intent = new Intent(MainActivity.this, MusicService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
        binder.setMediaPlayer(pos);

        initNowPlayingBar();
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
        Search(null, trackID, playlistID, albumID, from, belong, mode);
        if(binding.frameLayout.getVisibility() == View.GONE){
            binding.frameLayout.setVisibility(View.VISIBLE);
        }
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
                    initNowPlayingBar();

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
        songList = MusicService.getSongList();
        Log.i("INIT NOW PLAYING BAR", songList.toString());
        if(songList!=null) {
            Glide.with(MainActivity.this).load(songList.get(pos).getImage()).into(binding.trackImage);
            binding.trackName.setText(songList.get(pos).getTitle());
            binding.trackArtist.setText(songList.get(pos).getArtist());

            int totalTime = songList.get(pos).getDuration() * 1000; //miliseconds

            binding.seekBar.setMax(totalTime);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        songList.clear();
        myThread.interrupt();
        if (isServiceBound) {
            unbindService(connection);
            stopService(intent);
            isServiceBound = false;
        }
    }
}
