package com.example.octatunes;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    Button btn_signup_free, btn_login_phone;
    Button btn_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_home);

        btn_signup_free = findViewById(R.id.btn_signup_free);
        btn_login_phone = findViewById(R.id.btn_login_phone);
        btn_login = findViewById(R.id.btn_login);
    }
}
