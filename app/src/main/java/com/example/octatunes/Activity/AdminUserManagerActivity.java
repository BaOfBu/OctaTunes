package com.example.octatunes.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.MainActivity;
import com.example.octatunes.R;

public class AdminUserManagerActivity extends AppCompatActivity {

    private TextView _textTitleAdmin;
    private EditText _editTextSearch;
    private Button _btnSearch, _btnAddUser;
    private ImageView _btnRemove;
    private RecyclerView _recyclerViewUserList;
    private ImageButton _btn_more;
    private LayoutInflater _inflater;
    private PopupWindow _popupWindow;
    private View _toolbar;
    boolean isPopupMenuShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_usermanager);
        linktoLayout();

        _textTitleAdmin.setText("Quản lý người dùng");

        // Xử lý sự kiện khi người dùng nhấn vào nút tìm kiếm
        _btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = _editTextSearch.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    // Xử lý tìm kiếm ở đây

                } else {
                    Toast.makeText(AdminUserManagerActivity.this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút xóa
        _btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editTextSearch.setText("");
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút thêm người dùng mới
        _btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý thêm người dùng mới ở đây

            }
        });

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
                Intent intent = new Intent(AdminUserManagerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        userManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminUserManagerActivity.this, AdminUserManagerActivity.class);
                startActivity(intent);
            }
        });

        musicManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminUserManagerActivity.this, AdminSongManagerActivity.class);
                startActivity(intent);
            }
        });
    }

    public void linktoLayout() {
        _textTitleAdmin = findViewById(R.id.text_title_admin);
        _editTextSearch = findViewById(R.id.edit_text_search);
        _btnSearch = findViewById(R.id.btn_search);
        _recyclerViewUserList = findViewById(R.id.recycler_user_list);
        _btnAddUser = findViewById(R.id.btn_add_user);
        _btnRemove = findViewById(R.id.btn_remove);
        _btn_more = findViewById(R.id.menu_admin);
    }
}
