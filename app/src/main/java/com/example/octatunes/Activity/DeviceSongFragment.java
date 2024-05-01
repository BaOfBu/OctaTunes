package com.example.octatunes.Activity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.octatunes.FragmentListener;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        //get Array
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

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] selectionArgs = null; // No selection arguments needed
        String sortOrder = null; // You can specify sorting if needed

        // Querying the media content provider
        Cursor cursor = requireActivity().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // URI for audio files
                projection,
                selection,
                null,
                null
        );
        if(cursor != null){
            while (cursor.moveToNext()) {
                // Retrieving the ID and path of each media item
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                // Check if the file is in the music folder
                list.add(new SongModel(0, title, artist, null, null, null, filePath, duration));
            }
            cursor.close(); // Close the cursor when done
        }
        return list;
    }
}
