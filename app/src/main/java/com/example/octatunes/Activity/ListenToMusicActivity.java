package com.example.octatunes.Activity;

import static android.content.Context.BIND_AUTO_CREATE;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.octatunes.FragmentListener;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.LyricService;
import com.example.octatunes.Services.MusicService;
import com.example.octatunes.Utils.FileUtils;
import com.example.octatunes.Utils.MusicUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import me.zhengken.lyricview.LyricView;


public class ListenToMusicActivity extends Fragment implements View.OnClickListener {
    String from;
    String belong;
    public static SongModel currentSong;
    TextView track_from;
    TextView track_belong;
    @SuppressLint("StaticFieldLeak")
    private static ImageView imageView;
    @SuppressLint("StaticFieldLeak")
    private static TextView songName;
    @SuppressLint("StaticFieldLeak")
    private static TextView singer;
    private ImageButton previous;
    private ImageButton play;
    private ImageButton next;
    private static final int UPDATE = 0;
    private MusicService.MusicBinder binder;
    private Thread myThread;
    private Thread lyricThread;
    private int pos = -1;
    @SuppressLint("StaticFieldLeak")
    public static SeekBar seekBar;
    private TextView playTime;
    @SuppressLint("StaticFieldLeak")
    private static TextView duration;
    private ImageButton show_options;
    private ImageButton track_minimize;
    LyricView mLyricView;
    @SuppressLint("StaticFieldLeak")
    private static View rootView;
    private FragmentListener listener;
    private Handler handlerLyric;
    @SuppressLint("StaticFieldLeak")
    public static ImageButton repeat;
    private View repeat_dot;
    private ImageButton shuffle;
    private View shuffle_dot;

    LyricService lyricService = new LyricService();
    private boolean chosenSequence = false;
    public static boolean chosenRepeatOneSong = false;
    public static boolean chosenShuffle = false;
    private StorageReference storageRef;
    public ListenToMusicActivity(String from, String belong, SongModel song){
        this.from = from;
        this.belong = belong;
        currentSong = song;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_track_view, container, false);

        if (getArguments() != null) {
            from = getArguments().getString("from");
            belong = getArguments().getString("belong");
            currentSong = (SongModel) getArguments().getSerializable("current_song");
        }

        track_from = rootView.findViewById(R.id.track_from);
        track_belong = rootView.findViewById(R.id.track_belong);
        imageView = rootView.findViewById(R.id.imageViewAlbumArt);
        songName = rootView.findViewById(R.id.textViewTrackTitle);
        singer = rootView.findViewById(R.id.textViewArtistName);
        previous = rootView.findViewById(R.id.imageButtonPrevious);
        play = rootView.findViewById(R.id.imageButtonPlayPause);
        next = rootView.findViewById(R.id.imageButtonNext);
        seekBar = rootView.findViewById(R.id.seekBar);
        playTime = rootView.findViewById(R.id.elapsedTime);
        duration = rootView.findViewById(R.id.remainingTime);
        show_options = rootView.findViewById(R.id.show_options);
        track_minimize = rootView.findViewById(R.id.track_minimize);
        repeat= rootView.findViewById(R.id.imageButtonRepeat);
        repeat_dot= rootView.findViewById(R.id.repeat_dot);
        shuffle= rootView.findViewById(R.id.imageButtonShuffle);
        shuffle_dot= rootView.findViewById(R.id.shuffle_dot);

        track_from.setText(from);
        track_belong.setText(belong);
        if(binder != null && binder.isRandomPlay()){
            shuffle.setImageResource(R.drawable.ic_shuffle_clicked_green_24);
            shuffle_dot.setVisibility(View.VISIBLE);
        }else{
            shuffle.setImageResource(R.drawable.ic_shuffle_white_24);
            shuffle_dot.setVisibility(View.INVISIBLE);
        }
        initMediaPlayer();
    //test lyric for local file
        mLyricView = (LyricView)rootView.findViewById(R.id.custom_lyric_view);
        mLyricView.reset();
        File fileLyric = null;
        try {
            fileLyric = FileUtils.createFileFromRaw(getContext(), R.raw.thudocypher, "thudocypher.lrc");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mLyricView.setLyricFile(fileLyric);
        mLyricView.setCurrentTimeMillis(0);
        mLyricView.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
            @Override
            public void onPlayerClicked(long progress, String content) {
                MusicService.mediaPlayer.seekTo((int) progress);
            }
        });
//        lyricService.getLyricFile(currentSong.getTitle()).thenAccept(lyricModel -> {
//            mLyricView = (LyricView)rootView.findViewById(R.id.custom_lyric_view);
//            mLyricView.reset();
//            File fileLyric = null;
//            try {
//                fileLyric = FileUtils.createFileFromRaw(getContext(), lyricModel.getLyricFile(), lyricModel.getTitle());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            mLyricView.setLyricFile(fileLyric);
//            mLyricView.setCurrentTimeMillis(0);
//            mLyricView.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
//                @Override
//                public void onPlayerClicked(long progress, String content) {
//                    MusicService.mediaPlayer.seekTo((int) progress);
//                }
//            });
//        });

        track_minimize.setOnClickListener(this);
        show_options.setOnClickListener(this);
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    MusicService.mediaPlayer.seekTo(progress);
                    mLyricView.setCurrentTimeMillis(progress);
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

        myThread=new Thread(new MyThread());
        myThread.start();

        handlerLyric = new Handler();
        // Update progress bar and lyrics every second
        handlerLyric.postDelayed(updateProgress, 1000);

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, connection, BIND_AUTO_CREATE);

        if(MusicService.mediaPlayer.isPlaying()){
            play.setImageResource(R.drawable.ic_circle_pause_white_70);
        }else {
            play.setImageResource(R.drawable.ic_circle_play_white_70);
        }



        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }
//    private void downloadFile(String filePathInStorage, String localFileName) {
//        // Create a reference to the file you want to download
//        StorageReference fileRef = storageRef.child(filePathInStorage);
//
//        // Create a local file to store the downloaded file
//        File localFile = new File(getExternalFilesDir(null), localFileName);
//
//        // Download the file to the local file
//        fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                // File downloaded successfully
//                Log.d(TAG, "File downloaded to " + localFile.getAbsolutePath());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//                Log.e(TAG, "Error downloading file: " + exception.getMessage());
//            }
//        });
//    }
    private void sendSignalToMainActivity(int trackID, int playlistID, int albumID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived(trackID, playlistID, albumID, from, belong, mode);
        }
    }

    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (MusicService.mediaPlayer != null && MusicService.mediaPlayer.isPlaying()) {
                int currentPosition = MusicService.mediaPlayer.getCurrentPosition();

                // Call method to update lyrics progress
                mLyricView.setCurrentTimeMillis(currentPosition);

                // Call this runnable again after 1 second
                handler.postDelayed(this, 1000);
            }
        }
    };

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
                    if(MusicService.mediaPlayer.isPlaying()){
                        play.setImageResource(R.drawable.ic_circle_pause_white_70);
                    }else{
                        play.setImageResource(R.drawable.ic_circle_play_white_70);
                    }
                    playTime.setText(MusicUtils.formatTime(MusicService.mediaPlayer.getCurrentPosition()));
                    seekBar.setProgress(MusicService.mediaPlayer.getCurrentPosition());
                    //mLyricView.setCurrentTimeMillis(MusicService.mediaPlayer.getCurrentPosition());
                    break;
                default:
            }
        }
    };

    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MusicService.MusicBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

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
        }else if(id == R.id.imageButtonPrevious){
            binder.previousMusic();
            play.setImageResource(R.drawable.ic_circle_pause_white_70);
            pos = MusicService.getPos();
            currentSong = MusicService.getSongList().get(MusicService.getPos());
            initMediaPlayer();
        }else if(id == R.id.imageButtonNext){
            binder.nextMusic();
            play.setImageResource(R.drawable.ic_circle_pause_white_70);
            pos = MusicService.getPos();
            currentSong = MusicService.getSongList().get(MusicService.getPos());
            initMediaPlayer();
        }else if (id == R.id.show_options){
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_track_view);
            bottomSheetDialog.show();

            ImageView item_image = bottomSheetDialog.findViewById(R.id.item_image);
            Glide.with(bottomSheetDialog.getContext()).load(currentSong.getImage()).into(item_image);

            TextView item_title = bottomSheetDialog.findViewById(R.id.item_title);
            item_title.setText(songName.getText());

            TextView item_artist = bottomSheetDialog.findViewById(R.id.item_artist);
            item_artist.setText(singer.getText());

            TextView item_belong = bottomSheetDialog.findViewById(R.id.item_belong);
            item_belong.setText(belong);
        }else if (id == R.id.track_minimize) {
            MusicService.setPos(MusicService.getPos());
            replaceLastFragment();
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.binding.frameLayout.setVisibility(View.VISIBLE);
            mainActivity.binding.bottomNavigation.setVisibility(View.VISIBLE);
        }else if(id == R.id.imageButtonRepeat){
            if(repeat_dot.getVisibility() == View.INVISIBLE){
                repeat.setImageResource(R.drawable.ic_repeat_clicked_one_green_24);
                repeat_dot.setVisibility(View.VISIBLE);
                chosenSequence = true;
                binder.setSequencePlay();
            }else if(repeat_dot.getVisibility() == View.VISIBLE){
                if(!chosenRepeatOneSong){
                    repeat.setImageResource(R.drawable.ic_repeat_clicked_two_green_24);
                    chosenRepeatOneSong = true;
                    binder.setSinglePlay();
                }else{
                    repeat.setImageResource(R.drawable.ic_repeat_white_24);
                    repeat_dot.setVisibility(View.INVISIBLE);
                    if(chosenShuffle){
                        binder.setRandomPlay();
                    }else{
                        binder.setSequencePlay();
                    }
                }
            }
        }else if(id == R.id.imageButtonShuffle){
            if(shuffle_dot.getVisibility() == View.INVISIBLE){
                shuffle.setImageResource(R.drawable.ic_shuffle_clicked_green_24);
                shuffle_dot.setVisibility(View.VISIBLE);
                chosenShuffle = true;
                if(!chosenSequence && !chosenRepeatOneSong){
                    binder.setRandomPlay();
                }
            }else{
                shuffle.setImageResource(R.drawable.ic_shuffle_white_24);
                shuffle_dot.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void replaceLastFragment(){
        FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, MainActivity.lastFrag);
        fragmentTransaction.commit();
    }

    public static void initMediaPlayer(){
        Glide.with(rootView).load(currentSong.getImage()).into(imageView);
        songName.setText(currentSong.getTitle());
        singer.setText(currentSong.getArtist());
        int totalTime = currentSong.getDuration() * 1000; //miliseconds

        String time = MusicUtils.formatTime(totalTime);
        duration.setText(time);

        seekBar.setMax(totalTime);
    }
}