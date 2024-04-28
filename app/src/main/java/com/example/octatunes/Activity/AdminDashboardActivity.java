package com.example.octatunes.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.octatunes.R;

public class AdminDashboardActivity extends AppCompatActivity {
    ImageButton _btn_more;
    TextView _textTitleAdmin;
    LayoutInflater _inflater;
    PopupWindow _popupWindow;
    View _toolbar;
    boolean isPopupMenuShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_dashboard);

        _textTitleAdmin = findViewById(R.id.text_title_admin);
        _btn_more = findViewById(R.id.menu_admin);

        _textTitleAdmin.setText("Dashboard");

        _btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePopupMenu();
            }
        });
    }

    private void togglePopupMenu() {
        if (isPopupMenuShow) _popupWindow.dismiss();
        else showPopupMenu(_btn_more);
    }

    private void showPopupMenu(View view) {
        _inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = _inflater.inflate(R.layout.layout_admin_menu, null);

        int width = getResources().getDisplayMetrics().widthPixels / 2;
        int height = getResources().getDisplayMetrics().heightPixels;

        boolean focus = true;

        _toolbar = findViewById(R.id.toolbar);
        int _toolbarHeight = _toolbar.getHeight();

        _popupWindow = new PopupWindow(popupView, width, height - _toolbarHeight, focus);
        _popupWindow.showAtLocation(view, Gravity.START | Gravity.BOTTOM, 0, 0);

        TextView dashboard = popupView.findViewById(R.id.dashboard);
        TextView userManager = popupView.findViewById(R.id.user_manager);
        TextView musicManager = popupView.findViewById(R.id.music_manager);

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
        });

        userManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminUserManagerActivity.class);
                startActivity(intent);
            }
        });

        musicManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AdminSongManagerActivity.class);
                startActivity(intent);
            }
        });
    }
}