package com.example.octatunes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class AdminUserManagerActivity extends AppCompatActivity {

    private TextView _textTitleAdmin;
    private EditText _editTextSearch;
    private Button _btnSearch, _btnAddUser;
    private ImageView _btnRemove;
    private RecyclerView _recyclerViewUserList;
    // private UserAdapter _userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_usermanager);
        _textTitleAdmin = findViewById(R.id.text_title_admin);
        _editTextSearch = findViewById(R.id.edit_text_search);
        _btnSearch = findViewById(R.id.btn_search);
        _recyclerViewUserList = findViewById(R.id.recycler_user_list);
        _btnAddUser = findViewById(R.id.btn_add_user);
        _btnRemove = findViewById(R.id.btn_remove);

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
    }
}
