package com.example.octatunes.Activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.SongAdapter;
import com.example.octatunes.FragmentListener;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.PlaylistService;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.Services.UserService;

import java.util.List;

public class LikedSongFragment extends Fragment {

    private FragmentListener listener;
    private Handler handler = new Handler();
    @Override
    public void onAttach(@android.support.annotation.NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }
    private void sendSignalToMainActivity(int trackID, int playlistID, int albumID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived(trackID, playlistID, albumID, from, belong, mode);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.liked_song_layout, container, false);

        MainActivity.lastFrag=this;

        // Background
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#1d2859"), Color.parseColor("#161a2b")});

        rootView.findViewById(R.id.background_liked_song).setBackground(gradientDrawable);

        // Back Icon
        ImageView backIcon = rootView.findViewById(R.id.back_icon_liked_song);
        backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

        // Adapter
        UserService userService = new UserService();
        TextView number = rootView.findViewById(R.id.number_liked_songs);
        userService.getCurrentUserId(new UserService.UserIdCallback() {
            @Override
            public void onUserIdRetrieved(int userId) {
                PlaylistService playlistService = new PlaylistService();
                playlistService.getPlaylistModelLiked(userId, new PlaylistService.PlaylistCallback() {
                    @Override
                    public void onPlaylistRetrieved(PlaylistsModel playlistModel) {
                        TrackService trackService = new TrackService();
                        trackService.getTracksByPlaylistId(playlistModel.getPlaylistID()).thenAccept(tracks -> {
                            number.setText(tracks.size() + " songs");
                            if (tracks.size()>0){
                                setupRecyclerViewPopularSong(rootView,tracks,userId,playlistModel.getPlaylistID());
                                setupPlayButton(rootView,tracks.get(0),playlistModel.getPlaylistID());

                            }

                        });


                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Handle error if any
                    }
                });
            }
        });



        return rootView;
    }
    private void setupPlayButton(View rootView, TracksModel tracksModel, int playlistID){
        ImageView play_button_liked_display = rootView.findViewById(R.id.play_button_liked_display);
        play_button_liked_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mode = "sequencePlay";
                int trackFirstId = tracksModel.getTrackID();
                int albumId = -1;
                String from =  "PLAYING FROM PLAYLIST";
                String belong = "Liked Songs";
                int playlistId = playlistID;
                sendSignalToMainActivity(trackFirstId, playlistId, albumId, from, belong, mode);
            }
        });
    }
    private void setupRecyclerViewPopularSong(View rootView, List<TracksModel> tracks, int userID,int playlistId) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewLove);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        SongAdapter adapter = new SongAdapter(getActivity(), tracks, listener,userID,playlistId);
        recyclerView.setAdapter(adapter);
    }
}
