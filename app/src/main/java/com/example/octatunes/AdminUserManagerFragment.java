package com.example.octatunes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class AdminUserManagerFragment extends AppCompatActivity {

    private EditText editTextSearch;
    private Button btnSearch, btnAddUser;
    private ImageView btnRemove;
    private RecyclerView recyclerViewUserList;
    // private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_usermanager);

        editTextSearch = findViewById(R.id.edit_text_search);
        btnSearch = findViewById(R.id.btn_search);
        recyclerViewUserList = findViewById(R.id.recycler_user_list);
        btnAddUser = findViewById(R.id.btn_add_user);
        btnRemove = findViewById(R.id.btn_remove);

        // Xử lý sự kiện khi người dùng nhấn vào nút tìm kiếm
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editTextSearch.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    // Xử lý tìm kiếm ở đây

                } else {
                    Toast.makeText(AdminUserManagerFragment.this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút xóa
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText("");
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào nút thêm người dùng mới
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý thêm người dùng mới ở đây

            }
        });
    }
}
