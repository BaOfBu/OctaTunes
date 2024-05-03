package com.example.octatunes.Activity;

import static com.example.octatunes.MainActivity.isServiceBound;
import static com.example.octatunes.MainActivity.songList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.FragmentListener;
import com.example.octatunes.LoginActivity;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.HistoryModel;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Model.UserSongModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.Services.UserService;
import com.example.octatunes.Services.UserSongService;
import com.example.octatunes.TrackPreviewAdapter;
import com.example.octatunes.TrackPreviewAdapterForUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserActivity extends Fragment {
    Button btnLogout;
    Button editProfile;
    TextView usernameText;
    TextView emailText;
    RecyclerView recentActivityRecyclerView;
    UserService userService = new UserService();
    UserSongService userSongService = new UserSongService();
    TrackService trackService = new TrackService();

    private FragmentListener listener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }
    private void sendSignalToMainActivity(List<TracksModel> tracksModels, int trackID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived2(tracksModels, trackID, from, belong, mode);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.lastFrag=this;
        View rootView = inflater.inflate(R.layout.layout_user_profile, container, false);
        usernameText = (TextView) rootView.findViewById(R.id.username);
        emailText = (TextView) rootView.findViewById(R.id.email);
        btnLogout = (Button) rootView.findViewById(R.id.logout);
        editProfile = (Button) rootView.findViewById(R.id.edit_profile);
        recentActivityRecyclerView = rootView.findViewById(R.id.recyclerview_recent_activity);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
        // Load profile
        LoadProfile();
        // return the view
        return rootView;
    }

    private void logout() {
        //logout firebase
        FirebaseAuth.getInstance().signOut();

        //clear auto login account
        SharedPreferences preferences = requireActivity().getSharedPreferences("auto_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("logged_account", "");
        editor.putString("logged_password", "");
        editor.putInt("logged_time", 0);
        editor.apply();

        //interrupt music thread
        MainActivity main = (MainActivity) requireActivity();
        songList.clear();
        if(!main.myThread.isInterrupted()) main.myThread.interrupt();

        // Move to login home
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void editProfile() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(usernameText.getText());
        String usernameStringText = spannableStringBuilder.toString();
        userService.EditUserName(usernameStringText);
        // close keyboard
        InputMethodManager imm = (InputMethodManager)
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if(getActivity().getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
    }

    private void LoadProfile() {
        userService.getCurrentUserModel(userModel -> {
            if (userModel != null) {
                // Load user profile
                usernameText.setText(userModel.getName());
                emailText.setText(userModel.getEmail());
                LoadRecentSongs(userModel.getUserID());
            } else {
                // User not found
            }
        });
    }

    private void LoadRecentSongs(int userID) {
        // Load recent songs
        List<UserSongModel> userSongModels = new ArrayList<>();
        userSongService.getHistory(userID).thenAccept(userSongs1 -> {
            Log.e("UserActivity", "UserSong size: " + userSongs1.size());
            userSongModels.addAll(userSongs1);
        });

        trackService.findTrackByIDUserSong(userSongModels, new TrackService.OnSongLoadedListener() {
            @Override
            public void onSongLoaded(List<SongModel> songs) {
                Log.e("SearchActivity", "Tracks: " + songs);
                TrackPreviewAdapterForUser trackPreviewAdapter = new TrackPreviewAdapterForUser(songs,getContext(),listener, "Recent Activity");
                recentActivityRecyclerView.setAdapter(trackPreviewAdapter);
                trackPreviewAdapter.notifyDataSetChanged();
            }
        });
    }
}
