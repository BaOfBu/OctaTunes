package com.example.octatunes;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.octatunes.Activity.LoginHomeFragment;

public class LoginActivity extends AppCompatActivity {
    Button btn_signup_free, btn_login_phone;
    Button btn_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_container);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_container, new LoginHomeFragment())
                .commit();
    }

    public void NavigateFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
