package com.example.octatunes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.octatunes.Activity.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new HomeActivity())
//                .commit();
        Bundle bundle = new Bundle();
        bundle.putString("searchQuery", "Sơn Tùng M-TP");
        Fragment searchActivity = new SearchActivity();
        searchActivity.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, searchActivity)
                .commit();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeActivity())
                        .commit();
                return true;
            }
            if (item.getItemId() == R.id.search) {
                Bundle bundleNavigation = new Bundle();
                bundleNavigation.putString("searchQuery", "");
                Fragment searchActivityNavigation = new SearchActivity();
                searchActivityNavigation.setArguments(bundleNavigation);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, searchActivityNavigation)
                        .commit();
            }
            return false;
        });

    }
}
