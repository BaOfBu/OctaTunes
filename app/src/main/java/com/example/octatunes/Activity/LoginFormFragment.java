package com.example.octatunes.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.octatunes.LoginActivity;
import com.example.octatunes.MainActivity;
import com.example.octatunes.R;
import com.example.octatunes.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFormFragment extends Fragment {
    LoginActivity main; Context context;
    LinearLayout layout;
    Button btnLogin, btnNoPass; EditText edtUE, edtPass;
    ImageButton btnPrev;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            context = getActivity();
            main = (LoginActivity) (getActivity());
        }
        catch (IllegalStateException e){
            throw new IllegalStateException("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState ){
        layout = (LinearLayout) inflater.inflate(R.layout.layout_login_form,null);
        edtUE = layout.findViewById(R.id.edtUE);
        edtPass = layout.findViewById(R.id.edtPass);
        btnLogin = layout.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UE = edtUE.getText().toString();
                String pass = edtPass.getText().toString();
                if(UE.equals("")){
                    Toast.makeText(context.getApplicationContext(), "Enter email or username", Toast.LENGTH_SHORT).show();
                }
                if(pass.equals("")){
                    Toast.makeText(context.getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                }
                if(validateEmail(UE)){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(UE, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //User signed in successfully
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Authentication failed
                                        Toast.makeText(context.getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    loginByUsername(UE, pass);
                }
            }
        });
        btnNoPass = layout.findViewById(R.id.btnNoPass);
        btnNoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnPrev = layout.findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.onBackPressed();
            }
        });
        return layout;
    }
    public boolean validateEmail(String email){
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void loginByUsername(String username, String password) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String email = userSnapshot.child("email").getValue(String.class);
                        // Authenticate the user with Firebase Authentication using email and password
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // User signed in successfully
//                                            Intent intent = new Intent(main.getApplicationContext(), HomeActivity.class);
//                                            startActivity(intent);
//                                            main.finish();
                                            Toast.makeText(context.getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Authentication failed
                                            Toast.makeText(context.getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        return;
                    }
                }
                // Username not found
                Toast.makeText(context.getApplicationContext(), "Username not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(context.getApplicationContext(), "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
