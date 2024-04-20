package com.example.octatunes.Activity;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.octatunes.LoginActivity;
import com.example.octatunes.MainActivity;
import com.example.octatunes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SignUpNameFragment extends Fragment {
    LoginActivity main; Context context; Bundle args;
    RelativeLayout layout;
    Button btnSignUp; EditText edtName; ImageButton btnPrev;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            context = getActivity();
            main = (LoginActivity) (getActivity());
            args = getArguments();
        }
        catch (IllegalStateException e){
            throw new IllegalStateException("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState ){
        layout = (RelativeLayout) inflater.inflate(R.layout.layout_signup_name,null);
        edtName = layout.findViewById(R.id.edtName);
        btnSignUp = layout.findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "Button Press",
                        Toast.LENGTH_SHORT).show();
                String name = edtName.getText().toString();
                String email = args.getString("email");
                String pass = args.getString("password");
                Date birth = new Date(args.getLong("birth"));
                if(name.equals("")){
                    Toast.makeText(context.getApplicationContext(), "Enter Username",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User signed up successfully
                                    String userId = Objects.requireNonNull(FirebaseAuth.getInstance()
                                            .getCurrentUser()).getUid();
                                    // Save additional user information to Firebase Realtime Database
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", email); // Store email
                                    userData.put("password", pass); // Store hashed password
                                    userData.put("name", name);
                                    userData.put("dayOfBirth", birth);
//                                    // Add more fields as needed
                                    FirebaseDatabase.getInstance().getReference("users").child(userId)
                                            .setValue(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> databaseTask) {
                                                    if (databaseTask.isSuccessful()) {
//                                                        Intent intent = new Intent(context.getApplicationContext(), HomeActivity.class);
//                                                        startActivity(intent);
//                                                        main.finish();
                                                        Toast.makeText(context.getApplicationContext(), "Sign up success", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(context.getApplicationContext(), "Failed to save user data"
                                                                , Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "Sign up failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        btnPrev = layout.findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                args.putString("name", name);
                main.onBackPressed();
            }
        });
        return layout;
    }
    public boolean setChild(String userId, Map<String, Object> userData) {
        final boolean[] result = {false};
        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .setValue(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> databaseTask) {
                        if (databaseTask.isSuccessful()) {
                            result[0] = true;
                        } else {
                            result[0] = false;
                        }
                    }
                });
        return result[0];
    }
}
