package com.example.octatunes.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
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
import com.example.octatunes.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        String argsName = args.getString("name");
        edtName.setText(argsName);

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
                signUp(email, pass, birth, name);
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
    private void getHighestUserID(HighestUserIDCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maxID = Integer.MIN_VALUE;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve the userID for each user
                    Integer userID = userSnapshot.child("userID").getValue(Integer.class);
                    if (userID != null && userID > maxID) {
                        maxID = userID;
                    }
                }
                callback.onHighestUserID(maxID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                callback.onHighestUserID(-1);
            }
        });
    }
    public interface HighestUserIDCallback {
        void onHighestUserID(int highestUserID);
    }
    public void signUp(String email, String pass, Date birth, String name){
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
                            //Set new userID
                            getHighestUserID(new HighestUserIDCallback() {
                                @Override
                                public void onHighestUserID(int highestUserID) {
                                    if (highestUserID != -1) {
                                        // Do something with the highestUserID
                                        int newID = highestUserID + 1;
                                        userData.put("userID", newID);
                                        //Set other value
                                        userData.put("email", email); // Store email
                                        userData.put("password", pass); // Store hashed password but not hash
                                        userData.put("name", name);
                                        userData.put("dayOfBirth", birth);

                                        // Add more fields as needed
                                        FirebaseDatabase.getInstance().getReference("users").child(userId)
                                                .setValue(userData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> databaseTask) {
                                                        if (databaseTask.isSuccessful()) {
                                                            //save logged user for auto login
                                                            main.saveAutoLoginAccount(email, pass);
                                                            //Move to home
                                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(context.getApplicationContext(), "Failed to save user data"
                                                                    , Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Handle error
                                        Toast.makeText(context.getApplicationContext(), "Failed to retrieve highest User ID"
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
}
