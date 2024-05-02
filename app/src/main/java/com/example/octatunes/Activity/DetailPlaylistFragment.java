package com.example.octatunes.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.octatunes.Adapter.SongInQueueAdapter;
import com.example.octatunes.FragmentListener;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.MusicService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class DetailPlaylistFragment extends Fragment {
    private String from;
    private String belong;
    private String mode;
    SongModel currentSong;
    private FragmentListener listener;
    public DetailPlaylistFragment(String from, String belong, String mode, SongModel currentSong) {
        this.from = from;
        this.belong = belong;
        this.mode = mode;
        this.currentSong = currentSong;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_playlist, container, false);

        TextView track_from = rootView.findViewById(R.id.track_from);
        track_from.setText(from);

        TextView track_belong = rootView.findViewById(R.id.track_belong);
        track_belong.setText(belong);

        ImageView imageCurrent = rootView.findViewById(R.id.item_image);
        Glide.with(rootView.getContext()).load(currentSong.getImage()).into(imageCurrent);

        TextView trackNameCurrent = rootView.findViewById(R.id.item_title);
        trackNameCurrent.setText("... " + currentSong.getTitle());

        TextView artistNameCurrent = rootView.findViewById(R.id.item_artist);
        artistNameCurrent.setText(currentSong.getArtist());

        ImageButton options = rootView.findViewById(R.id.show_options);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_track_view);
                bottomSheetDialog.show();
            }
        });

        ImageButton playlist_minimize = rootView.findViewById(R.id.track_minimize);
        playlist_minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DETAIL QUEUE", "ON CLICK MINIMIZE");
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, MainActivity.trackFrag).commit();
            }
        });

        if(getContext() != null){
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview_track_in_playlist);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            SongInQueueAdapter adapter = new SongInQueueAdapter(getContext(), listener, MusicService.loadSongQueue(MusicService.getPos()));
            for(int i = 0; i < MusicService.getSongList().size(); i++){
                Log.i("SONG LIST " + i, MusicService.getSongList().get(i).toString());
            }
            Log.i("SONG LIST POS: ", String.valueOf(MusicService.getPos()));

            recyclerView.setAdapter(adapter);
        }

        return rootView;
    }
}