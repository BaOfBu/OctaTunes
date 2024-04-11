package com.example.octatunes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.octatunes.Activity.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
    }

    private void playlistActivity() {
        Intent i = new Intent(MainActivity.this, PlaylistActivity.class);
        startActivity(i);
    }
}