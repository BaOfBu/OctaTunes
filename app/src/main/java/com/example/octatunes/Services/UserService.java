package com.example.octatunes.Services;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.example.octatunes.Model.UsersModel;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class UserService {

    private DatabaseReference usersRef;

    public UserService() {
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
    public void getCurrentUserModel(UserModelCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UsersModel userModel = dataSnapshot.getValue(UsersModel.class);
                        callback.onUserModelRetrieved(userModel);
                    } else {
                        callback.onUserModelRetrieved(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onUserModelRetrieved(null);
                }
            });
        } else {
            callback.onUserModelRetrieved(null);
        }
    }

    public interface UserModelCallback {
        void onUserModelRetrieved(UsersModel userModel);
    }

    public void getCurrentUserId(UserIdCallback callback) {
        getCurrentUserModel(new UserModelCallback() {
            @Override
            public void onUserModelRetrieved(UsersModel userModel) {
                if (userModel != null) {
                    int userId = userModel.getUserID();
                    callback.onUserIdRetrieved(userId);
                } else {
                    callback.onUserIdRetrieved(-1);
                }
            }
        });
    }

    public interface UserIdCallback {
        void onUserIdRetrieved(int userId);
    }
}
