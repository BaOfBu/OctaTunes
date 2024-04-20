package com.example.octatunes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.octatunes.Activity.HomeActivity;
import com.example.octatunes.Activity.NowPlayingBarFragment;
import com.example.octatunes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeActivity());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemID = item.getItemId();
            if(itemID == R.id.home){
                replaceFragment(new HomeActivity());
            }else if(itemID == R.id.search){
                Bundle bundleNavigation = new Bundle();
                bundleNavigation.putString("searchQuery", "Ngang");
                Fragment searchActivityNavigation = new SearchActivity();
                searchActivityNavigation.setArguments(bundleNavigation);
                replaceFragment(searchActivityNavigation);
            }else if(itemID == R.id.library){

            }else if(itemID == R.id.Premium){

            }
            return true;
        });
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
