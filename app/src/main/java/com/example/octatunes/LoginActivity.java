package com.example.octatunes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.octatunes.Activity.AdminDashboardActivity;
import com.example.octatunes.Activity.LoginHomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    Button btn_signup_free, btn_login_phone;
    Button btn_login;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_container);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_container, new LoginHomeFragment())
                .commit();

        createProgressDialog();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //Uncomment saveAutoLoginAccount to disable auto login
        //saveAutoLoginAccount("", "");
        //auto login after user logged in
        autoLogin();
    }

    public void NavigateFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    public boolean validateEmail(String email){
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public void saveAutoLoginAccount(String account, String password) {
        SharedPreferences preferences = getSharedPreferences("auto_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("logged_account", account);
        editor.putString("logged_password", password);
        editor.apply();
    }

    public String getAccount() {
        SharedPreferences sharedPref = getSharedPreferences("auto_login", Context.MODE_PRIVATE);
        return sharedPref.getString("logged_account", "");
    }

    public String getPassword() {
        SharedPreferences sharedPref = getSharedPreferences("auto_login", Context.MODE_PRIVATE);
        return sharedPref.getString("logged_password", "");
    }
    public int getLoginTime() {
        SharedPreferences sharedPref = getSharedPreferences("auto_login", Context.MODE_PRIVATE);
        return sharedPref.getInt("logged_time", 0);
    }

    public void setLoginTime(){
        int maxLoginTime = 3;

        SharedPreferences sharedPref = getSharedPreferences("auto_login", Context.MODE_PRIVATE);
        int result = sharedPref.getInt("logged_time", 0);
        result = (result >= maxLoginTime) ? 0 : result + 1;

        SharedPreferences preferences = getSharedPreferences("auto_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("logged_time", result);
        editor.apply();
    }

    public void autoLogin() {
        startProgressDialog();

        String UE = getAccount();
        String pass = getPassword();
        int loginTime = getLoginTime();

        if (loginTime == 0) {
            saveAutoLoginAccount("", "");
            stopProgressDialog();
            return;
        }

        if (UE.equals("") && pass.equals("")) {
            stopProgressDialog();
            return;
        }

        if (validateEmail(UE)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(UE, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                setLoginTime();

                                stopProgressDialog();
                                moveToLoggedActivity(UE, pass);
                            } else {
                                stopProgressDialog();
                            }
                        }
                    });
        } else {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.orderByChild("name").equalTo(UE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String email = userSnapshot.child("email").getValue(String.class);
                            // Authenticate the user with Firebase Authentication using email and password
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // User signed in successfully
                                                setLoginTime();

                                                stopProgressDialog();
                                                moveToLoggedActivity(UE, pass);
                                            } else {
                                                stopProgressDialog();
                                            }
                                        }
                                    });
                            return;
                        }
                    } else {
                        stopProgressDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    stopProgressDialog();
                }
            });
        }
    }
    public void createProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải...");
        progressDialog.setCancelable(false);
    }

    public void startProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void stopProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    public void moveToLoggedActivity(String UE, String pass){
        Intent intent;
        if((UE.equals("sonsung2003@gmail.com") || UE.equals("Dolphin")) && pass.equals("1234567890")){
            intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
        }else{
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
    }
}
