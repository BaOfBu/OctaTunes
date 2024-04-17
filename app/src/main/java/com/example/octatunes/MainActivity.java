package com.example.octatunes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.layout_home);
        //loginActivity();

        //listenToMusicActivity();
        searchActivity("Chúng ta của tương lai");
        //playlistActivity();
    }

    private void searchActivity(String searchQuery) {
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra("searchQuery", searchQuery);
        startActivity(i);
    }
//    private void loginActivity() {
//        Intent i = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(i);
//    }
    private void playlistActivity() {
        Intent i = new Intent(MainActivity.this, PlaylistActivity.class);
        startActivity(i);
    }
}