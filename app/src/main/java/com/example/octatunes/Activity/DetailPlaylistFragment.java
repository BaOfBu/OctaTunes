package com.example.octatunes.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DetailPlaylistFragment extends Fragment {
    private String from;
    private String belong;
    SongModel currentSong;
    public DetailPlaylistFragment(String from, String belong, SongModel currentSong) {
        this.from = from;
        this.belong = belong;
        this.currentSong = currentSong;
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
                MainActivity mainActivity = (MainActivity) getActivity();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, mainActivity.lastFrag).commit();
                if(mainActivity.binding.frameLayout.getVisibility() == View.GONE){
                    mainActivity.binding.frameLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }
}