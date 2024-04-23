package com.example.octatunes.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.SongAdapter;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.ArtistService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistDetailFragment extends Fragment {

    private ArtistsModel artistModel;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_artist_detail, container, false);

        // Retrieve the arguments passed to the fragment
        Bundle args = getArguments();
        if (args != null) {
            // Get the ArtistsModel object from the arguments
            artistModel = (ArtistsModel) args.getSerializable("artist");

            // Artist Name
            if (artistModel != null) {
                TextView textView = rootView.findViewById(R.id.artist_detail_name);
                textView.setText(artistModel.getName());
            }

            // Artist Image
            if (artistModel != null) {
                ImageView imageView = rootView.findViewById(R.id.artist_detail_image);
                Picasso.get().load(artistModel.getImage()).into(imageView);
            }

            // Set up RecyclerView for popular songs
            setupRecyclerViewPopularSong(rootView, artistModel.getArtistID());
        }

        return rootView;
    }

    private void setupRecyclerViewPopularSong(View rootView, int artistId) {
        ArtistService artistService = new ArtistService();
        artistService.getRandomTracksByArtistId(artistId, new OnSuccessListener<List<TracksModel>>() {
            @Override
            public void onSuccess(List<TracksModel> tracks) {
                RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSongPopular);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                SongAdapter adapter = new SongAdapter(getActivity(), tracks);
                recyclerView.setAdapter(adapter);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

}
