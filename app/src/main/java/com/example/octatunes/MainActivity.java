package com.example.octatunes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.octatunes.Activity.HomeActivity;
import com.example.octatunes.Activity.ListenToMusicActivity;
import com.example.octatunes.Activity.LibraryFragment;
import com.example.octatunes.Activity.NowPlayingBarFragment;
import com.example.octatunes.Model.Playlist_TracksModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Services.PlaylistService;
import com.example.octatunes.Services.PlaylistTrackService;
import com.example.octatunes.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public static List<SongModel> songList;
    public static List<SongModel> getSongList(){
        return songList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        songList = new ArrayList<>();

        replaceFragment(new HomeActivity());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemID = item.getItemId();
            if(itemID == R.id.home){
                replaceFragment(new HomeActivity());
            }else if(itemID == R.id.search){
                replaceFragment(new SearchingActivity());
            }else if(itemID == R.id.library){
                replaceFragment(new LibraryFragment());
            }else if(itemID == R.id.Premium){

            }
            return true;
        });

        Intent intent = new Intent(MainActivity.this, ListenToMusicActivity.class);
        intent.putExtra("trackID", 24);
        intent.putExtra("playlistID", 11);
        intent.putExtra("albumID", 4);
        intent.putExtra("from", "PLAYING FROM SEARCH");
        intent.putExtra("belong", "\"Như+ngày+hôm+qua\" in Search");
        startActivity(intent);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void showNowPlayingBar(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new NowPlayingBarFragment());
        fragmentTransaction.commit();
        FrameLayout frameLayout = findViewById(R.id.frame_layout);
        frameLayout.setVisibility(View.VISIBLE);
    }
}
