package com.example.octatunes.Activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.SongAdapter;
import com.example.octatunes.FragmentListener;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.MusicService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DeviceSongFragment extends Fragment {
    private FragmentListener listener;
    View rootViews;
    private Handler handler = new Handler();
    @Override
    public void onAttach(@android.support.annotation.NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_device_playlist, container, false);
        rootViews = rootView;

        MainActivity.lastFrag = this;

        // Background
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#1d2859"), Color.parseColor("#161a2b")});

        rootView.findViewById(R.id.background_device_song).setBackground(gradientDrawable);

        // Back Icon
        ImageView backIcon = rootView.findViewById(R.id.back_icon_device_song);
        backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

        // Adapter
        TextView number = rootView.findViewById(R.id.number_device_songs);

        List<SongModel> deviceSongs = getDeviceSongs();
        if (deviceSongs != null && !deviceSongs.isEmpty()) {
            number.setText(deviceSongs.size() + " songs");
            setupPlayButton(rootView, deviceSongs);
            setupRecyclerViewSong(rootView, deviceSongs);
        } else {
            number.setText("0 song");
        }

        return rootView;
    }

    public List<SongModel> getDeviceSongs(){
        ArrayList<SongModel> list = new ArrayList<SongModel>();
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA, // Path to the media file
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0" +
                " AND " + MediaStore.Audio.Media.DATA + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + Environment.DIRECTORY_MUSIC + "%"}; // Filter for files in the music directory
        String sortOrder = null; // You can specify sorting if needed

        // Querying the media content provider
        Cursor cursor = requireActivity().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // URI for audio files
                projection,
                selection,
                selectionArgs,
                null
        );
        if(cursor != null){
            int i = 1;
            while (cursor.moveToNext()) {
                // Retrieving the ID and path of each media item
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) / 1000;
                String image = "https://firebasestorage.googleapis.com/v0/b/octatunes-495d2.appspot.com/o/images%2Falbums%2F004.jpg?alt=media&token=5761d139-98a0-409c-92a2-5ea55e843155";

                // Handle unknown artist
                if (artist == null || artist.isEmpty()) {
                    artist = "Unknown Artist";
                }

                // Check if the file is in the music folder
                list.add(new SongModel(i, title, artist, null, null, image, filePath, duration));
                i++;
            }
            cursor.close(); // Close the cursor when done
        }
        return list;
    }

    private void setupRecyclerViewSong(View rootView, List<SongModel> songs) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewLove);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        SongAdapter adapter = new SongAdapter(getActivity(), songs, listener, true);
        recyclerView.setAdapter(adapter);
    }

    private void setupPlayButton(View rootView, List<SongModel> deviceSongs){
        ImageView play_button_device_display = rootView.findViewById(R.id.play_button_device_display);
        play_button_device_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mode = "sequencePlay";
                String from = "PLAYING FROM PLAYLIST";
                String belong = "Device Songs";

                sendSignalToMainActivity(deviceSongs, 0, from, belong, mode);
            }
        });
    }

    private void sendSignalToMainActivity(List<SongModel> songModels, int pos, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived3(songModels, pos, from, belong, mode);
        }
    }
}
