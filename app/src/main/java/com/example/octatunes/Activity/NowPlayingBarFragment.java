package com.example.octatunes.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class NowPlayingBarFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private TracksModel trackModel;

    private String trackImg;

    public NowPlayingBarFragment() {
    }
    public static NowPlayingBarFragment newInstance(String param1, String param2) {
        NowPlayingBarFragment fragment = new NowPlayingBarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trackModel = (TracksModel) getArguments().getSerializable("trackModel");
            trackImg = (String)getArguments().getSerializable("trackImg");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing_bar, container, false);
        TextView trackNameTextView = rootView.findViewById(R.id.nowplaying_track_name);
        ImageView trackImageView = rootView.findViewById(R.id.nowplaying_track_img);

        if (trackModel != null) {
            trackNameTextView.setText(trackModel.getName());
            Log.w("ferf",trackImg + "ferf");
            if (trackImg != null && trackImg!="") {
                Picasso.get().load(trackImg).into(trackImageView);
            } else {

            }
        }

        return rootView;
    }


    public static NowPlayingBarFragment newInstance(String param1, String param2, TracksModel trackModel,String trackImg) {
        NowPlayingBarFragment fragment = new NowPlayingBarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putSerializable("trackModel", trackModel);
        args.putSerializable("trackImg", trackImg);
        fragment.setArguments(args);
        return fragment;
    }
}