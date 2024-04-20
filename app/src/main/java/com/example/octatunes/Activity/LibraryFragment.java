package com.example.octatunes.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.LibraryAdapter;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LibraryFragment extends Fragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_library, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create a list of items to display in the RecyclerView
        List<Object> items = new ArrayList<>();
        items.add(new PlaylistsModel(
            1,1,"Top 50 - Vietnam","",""
        ));
        items.add(new AlbumsModel(
            1,1,"Những lời hứa bỏ quên",new Date(),""
        ));
        items.add(new ArtistsModel(
            1,"RPT MCK","",""
        ));

        // Create an instance of LibraryAdapter
        LibraryAdapter adapter = new LibraryAdapter(getActivity(), items);

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
