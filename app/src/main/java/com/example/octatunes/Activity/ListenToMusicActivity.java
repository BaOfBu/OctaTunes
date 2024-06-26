package com.example.octatunes.Activity;

import static android.content.Context.BIND_AUTO_CREATE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.octatunes.Services.DownloadMusicService;
import com.example.octatunes.Services.LyricService;
import com.example.octatunes.Services.MusicService;
import com.example.octatunes.Utils.MusicUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import me.zhengken.lyricview.LyricView;

public class ListenToMusicActivity extends Fragment implements View.OnClickListener {
    public static String from;
    public static String belong;
    public static String mode;
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
    private ImageButton imageButtonDownload;
    public static LyricView mLyricView;
    @SuppressLint("StaticFieldLeak")
    private static View rootView;
    private FragmentListener listener;
    public static Handler handlerLyric;
    private Handler checkDownload;
    @SuppressLint("StaticFieldLeak")
    public static ImageButton repeat;
    private View repeat_dot;
    private ImageButton shuffle;
    private View shuffle_dot;
    private ImageButton alarm;
    private ImageButton queue;
    private boolean isOnAlarm = false;
    private int minutes_schedule_alarm = 0;
    static LyricService lyricService = new LyricService();
    private boolean chosenSequence = false;
    public static boolean chosenRepeatOneSong = false;
    public static boolean chosenShuffle = false;
    private StorageReference storageRef;
    Handler handlerAlarm = new Handler();
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    public ListenToMusicActivity(String From, String Belong, SongModel Song){
        from = From;
        belong = Belong;
        currentSong = Song;
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
        alarm = rootView.findViewById(R.id.imageButtonAlarm);
        imageButtonDownload = rootView.findViewById(R.id.imageButtonDownload);
        queue = rootView.findViewById(R.id.imageButtonPlaylist);

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
//        mLyricView = (LyricView)rootView.findViewById(R.id.custom_lyric_view);
//        mLyricView.reset();
//        File fileLyric = null;
//        try {
//            fileLyric = FileUtils.createFileFromRaw(getContext(), R.raw.thudocypher, "thudocypher.lrc");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        mLyricView.setLyricFile(fileLyric);
//        mLyricView.setCurrentTimeMillis(0);
//        mLyricView.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
//            @Override
//            public void onPlayerClicked(long progress, String content) {
//                MusicService.mediaPlayer.seekTo((int) progress);
//            }
//        });
        handlerLyric = new Handler(Looper.getMainLooper());
        updateUILyric();

        track_minimize.setOnClickListener(this);
        show_options.setOnClickListener(this);
        imageButtonDownload.setOnClickListener(this);
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);
        alarm.setOnClickListener(this);
        queue.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    MusicService.player.seekTo(progress);
                    if(mLyricView != null){
                        mLyricView.setCurrentTimeMillis(progress);
//                        handlerLyric.postDelayed(updateProgress, 500);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MusicService.player.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.player.setPlayWhenReady(true);
            }
        });

        myThread=new Thread(new MyThread());
        myThread.start();

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, connection, BIND_AUTO_CREATE);

        if(MusicService.player.isPlaying()){
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
    private void DownloadFileTask(String fileUrl) throws IOException {
        // Create URL object
        URL url = new URL(fileUrl);

        // Open connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        // Get input stream
        InputStream inputStream = urlConnection.getInputStream();

        // Create a directory for the downloaded file
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Lyrics");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Create file to write the downloaded data
        File file= new File(dir, "currentLyrics.lrc");
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        // Read from input stream and write to file
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        // Close streams
        fileOutputStream.close();
        inputStream.close();
        urlConnection.disconnect();

        Log.d("Download", "File downloaded to: " + file.getAbsolutePath());
    }
    public void updateUILyric(){
        lyricService.getLyricFile(currentSong.getTitle()).thenAccept(lyricModel -> {
            Log.e("UPDATE LYRIC", "Lyric: " );
            mLyricView = (LyricView)rootView.findViewById(R.id.custom_lyric_view);
            mLyricView.reset();
            if(lyricModel == null){
                Log.d("Lyric", "Lyric not found");
                return;
            }
            //Log.d("Lyric", "Lyric: " + lyricModel.getLyric());
            File[] externalFilesDirs = this.getContext().getExternalFilesDirs(null);
            if (externalFilesDirs != null && externalFilesDirs.length > 0) {
                // Get the first external files directory (primary storage)
                File primaryExternalDir = externalFilesDirs[0];

                String fileName = lyricModel.getTitle() + ".lrc";

                // Construct the full path to the file within the "LyricFolder" subfolder
                File subFolder = new File(primaryExternalDir, "LyricFolder");
                File file = new File(subFolder, fileName);

                // Check if the file exists
                if (file.exists()) {
                    Log.d("Lyric", "File existed: " + file.getAbsolutePath());
                    Log.d("Download", "File downloaded to: " + file.getAbsolutePath());
                    mLyricView.setLyricFile(file);
                    mLyricView.setCurrentTimeMillis(0);
                    // Update progress bar and lyrics every second
                    handlerLyric.postDelayed(updateLyricProgress, 500);
                } else {
                    try {
                        downloadFile(lyricModel.getLyric(),lyricModel.getTitle(), getContext());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }


            mLyricView.setOnPlayerClickListener(new LyricView.OnPlayerClickListener() {
                @Override
                public void onPlayerClicked(long progress, String content) {
                    MusicService.player.seekTo((int) progress);
                }
            });
        });
    }

    public void downloadFile(String url,String filename, Context context) throws InterruptedException {
        String DownloadUrl = url;
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request1.setDescription("Download lyric");   //appears the same in Notification bar while downloading
        request1.setTitle(filename+".lrc");
        request1.setVisibleInDownloadsUi(true);

        request1.allowScanningByMediaScanner();
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request1.setDestinationInExternalFilesDir(context, "/LyricFolder", filename + ".lrc");

        DownloadManager manager1 = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = Objects.requireNonNull(manager1).enqueue(request1);
        Cursor cursor = manager1.query(new DownloadManager.Query().setFilterById(downloadId));
        checkDownload = new Handler();
        checkDownload.postDelayed(new Runnable() {
            @Override
            public void run() {
                Boolean flag = checkDownloadStatus(cursor, filename);
                if (flag) {
                    checkDownload.removeCallbacksAndMessages(null);
                }
                else{
                    checkDownload.postDelayed(this, 500);
                }
            }
        }, 500);
    }
    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (MusicService.player != null && MusicService.player.isPlaying()) {
                int currentPosition = (int) MusicService.player.getCurrentPosition();
                Log.d("MLyricView", "mLyricView: " + mLyricView);
                // Call method to update lyrics progress
                mLyricView.setCurrentTimeMillis(currentPosition);

                // Call this runnable again after 1 second
                handler.postDelayed(this, 500);
            }
        }
    };

    private Boolean checkDownloadStatus(Cursor cursor, String filename) {
        if (cursor.moveToFirst()) {
            File[] externalFilesDirs = requireContext().getExternalFilesDirs(null);
            if (externalFilesDirs != null && externalFilesDirs.length > 0) {
                // Get the first external files directory (primary storage)
                File primaryExternalDir = externalFilesDirs[0];

                String fileName = filename + ".lrc";

                // Construct the full path to the file within the "LyricFolder" subfolder
                File subFolder = new File(primaryExternalDir, "LyricFolder");
                File file = new File(subFolder, fileName);
                Log.d("Download", "File downloaded to: " + file.getAbsolutePath());
                Log.d("Download", "File downloaded existed ?: " + file.exists());
                if (file.exists()) {
                    mLyricView.setLyricFile(file);
                    mLyricView.setCurrentTimeMillis(0);
                    // Update progress bar and lyrics every second
                    handlerLyric.postDelayed(updateLyricProgress, 500);
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        return false;
    }

    private class MyThread implements Runnable{
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                if(MusicService.player!= null ) {
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
                    if(MusicService.player.isPlaying()){
                        play.setImageResource(R.drawable.ic_circle_pause_white_70);
                    }else{
                        play.setImageResource(R.drawable.ic_circle_play_white_70);
                    }
                    //handlerLyric.postDelayed(updateProgress, 500);
                    int currentPos = (int) MusicService.player.getCurrentPosition();
                    playTime.setText(MusicUtils.formatTime(currentPos));
                    seekBar.setProgress(currentPos);

                    long buffer = MusicService.player.getBufferedPosition();

                    seekBar.setSecondaryProgress((int) buffer);

//                    mLyricView.setCurrentTimeMillis(MusicService.player.getCurrentPosition());
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
    private Runnable updateLyricProgress = new Runnable() {
        @Override
        public void run() {
            if (MusicService.player != null && MusicService.player.isPlaying()) {
                int currentPosition = (int) MusicService.player.getCurrentPosition();
                if (mLyricView != null) {
                    mLyricView.setCurrentTimeMillis(currentPosition);
                }
                handlerLyric.postDelayed(this, 500); // Cập nhật mỗi 500ms
            }
        }
    };
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.imageButtonPlayPause){
            binder.playMusic();
            if(MusicService.player.isPlaying()){
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
            updateUILyric();
        }else if(id == R.id.imageButtonNext){
            binder.nextMusic();
            play.setImageResource(R.drawable.ic_circle_pause_white_70);
            pos = MusicService.getPos();
            currentSong = MusicService.getSongList().get(MusicService.getPos());
            initMediaPlayer();
            updateUILyric();
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

//            androidx.appcompat.widget.AppCompatButton download = bottomSheetDialog.findViewById(R.id.download);
//            assert download != null;
//            download.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent downloadService = new Intent(getActivity(), DownloadMusicService.class);
//                    requireActivity().startService(downloadService);
//                }
//            });
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
                if(!chosenRepeatOneSong){
                    binder.setRandomPlay();
                }
            }else{
                shuffle.setImageResource(R.drawable.ic_shuffle_white_24);
                shuffle_dot.setVisibility(View.INVISIBLE);
                if (!binder.isSinglePlay()) binder.setSequencePlay();
            }
        }else if(id == R.id.imageButtonDownload){
            String songTitle = currentSong.getTitle();
            String songFile = currentSong.getFile();

            Toast.makeText(getActivity(), "Bắt đầu tải " + songTitle, Toast.LENGTH_SHORT).show();

            Intent downloadService = new Intent(getActivity(), DownloadMusicService.class);

            downloadService.putExtra("SONG_TITLE", songTitle);
            downloadService.putExtra("SONG_FILE", songFile);

            requireActivity().startService(downloadService);

        }else if(id == R.id.imageButtonAlarm){
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_schedule_alarm);
            bottomSheetDialog.show();

            TextView title = bottomSheetDialog.findViewById(R.id.title);

            TextView close = bottomSheetDialog.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.cancel();
                }
            });

            TextView five_minutesTV = bottomSheetDialog.findViewById(R.id.five_minutes);
            TextView ten_minutesTV = bottomSheetDialog.findViewById(R.id.ten_minutes);
            TextView fifteen_minutesTV = bottomSheetDialog.findViewById(R.id.fifteen_minutes);
            TextView thirty_minutesTV = bottomSheetDialog.findViewById(R.id.thirty_minutes);
            TextView fortyfive_minutesTV = bottomSheetDialog.findViewById(R.id.fortyfive_minutes);
            TextView one_hourTV = bottomSheetDialog.findViewById(R.id.one_hour);

            setupClickListeners(bottomSheetDialog, five_minutesTV, 5);
            setupClickListeners(bottomSheetDialog, ten_minutesTV, 10);
            setupClickListeners(bottomSheetDialog, fifteen_minutesTV, 15);
            setupClickListeners(bottomSheetDialog, thirty_minutesTV, 30);
            setupClickListeners(bottomSheetDialog, fortyfive_minutesTV, 45);
            setupClickListeners(bottomSheetDialog, one_hourTV, 60);

            TextView end_track = bottomSheetDialog.findViewById(R.id.end_track);
            end_track.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.cancel();
                    int delay = (int) ((currentSong.getDuration() + 2) * 1000 + 1 - MusicService.player.getCurrentPosition());
                    handleScheduleAlarm(delay);
                    if(isOnAlarm){
                        title.setText("Hẹn giờ đi ngủ - Cuối bản nhạc");
                    }
                }
            });

            TextView off_mode = bottomSheetDialog.findViewById(R.id.off_mode);
            off_mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.cancel();
                    isOnAlarm = false;
                    minutes_schedule_alarm = 0;
                    alarm.setImageResource(R.drawable.ic_alarm_white_24);
                    handlerAlarm.removeCallbacksAndMessages(null);
                    if(isOnAlarm) title.setText("Hẹn giờ đi ngủ");
                    Toast.makeText(requireContext(), "Đã tắt chế độ hẹn giờ thành công!", Toast.LENGTH_SHORT).show();
                }
            });

            if(minutes_schedule_alarm != 0){
                if (isOnAlarm) {
                    String titleText = "Hẹn giờ đi ngủ - Còn lại " + minutes_schedule_alarm + " phút";
                    title.setText(titleText);
                }
            }
        }else if(id == R.id.imageButtonPlaylist){
            Fragment fragment = new DetailPlaylistFragment(from, belong,  "sequencePlay", currentSong);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }
    private void setupClickListeners(final BottomSheetDialog bottomSheetDialog, TextView textView, final int minutes) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
                minutes_schedule_alarm = minutes;
                int delay = minutes * 60 * 1000; //milliseconds
                handleScheduleAlarm(delay);
            }
        });
    }
    private void handleScheduleAlarm(int time){
        scheduleAlarm();
        isOnAlarm = true;
        handlerAlarm.postDelayed(new Runnable() {
            @Override
            public void run() {
                binder.pauseMusic();
                isOnAlarm = false;
                alarm.setImageResource(R.drawable.ic_alarm_white_24);
            }
        }, time);
        alarm.setImageResource(R.drawable.ic_alarm_on_green_24);
    }
    private void scheduleAlarm() {
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.i("SCHEDULE ALARM", "SUCCESS");
        Toast.makeText(requireContext(), "Đã bật chế độ hẹn giờ thành công!", Toast.LENGTH_SHORT).show();
    }
    public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Đã bật chế độ hẹn giờ", Toast.LENGTH_SHORT).show();
        }
    }
    private void replaceLastFragment(){
        FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, MainActivity.lastFrag);
        fragmentTransaction.commit();
    }

    public static void initMediaPlayer(){
        if(rootView != null) {
            Glide.with(rootView).load(currentSong.getImage()).into(imageView);
            songName.setText(currentSong.getTitle());
            singer.setText(currentSong.getArtist());

            int totalTime = currentSong.getDuration() * 1000; //miliseconds

            String time = MusicUtils.formatTime(totalTime);
            duration.setText(time);

            seekBar.setMax(totalTime);

        }
    }
}