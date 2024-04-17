package com.example.octatunes.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.SongAdapter;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSpotifyActivity extends AppCompatActivity {
    private List<SongModel> songList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_playlist_spotify);

        songList = new ArrayList<>();
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));
        songList.add(new SongModel("Chúng ta của tương lai", "Sơn Tùng MTP",
                "Chúng ta của tương lai", 310,
                R.drawable.music_01, ""));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SongAdapter adapter = new SongAdapter(songList);
        recyclerView.setAdapter(adapter);
    }
}
