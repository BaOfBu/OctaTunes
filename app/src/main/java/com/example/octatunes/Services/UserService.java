package com.example.octatunes.Services;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.octatunes.Model.UsersModel;

import java.util.Date;

public class UserService {

    private DatabaseReference usersRef;

    public UserService() {
        // Initialize the Realtime Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
    }

    public void addUserToRealtimeDatabase(UsersModel user) {
        // Push a new user object to the "users" node in the Realtime Database
        usersRef.push().setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // User added successfully
                    Log.d("UserManager", "User added with auto-generated ID");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("UserManager", "Error adding user", e);
                });
    }

}
