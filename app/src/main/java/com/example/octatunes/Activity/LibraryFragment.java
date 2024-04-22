package com.example.octatunes.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.LibraryAdapter;
import com.example.octatunes.Model.AlbumLibrary_User;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistLibrary_User;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.AlbumLibraryUserService;
import com.example.octatunes.Services.PlaylistLibraryUserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LibraryFragment extends Fragment {
    private RecyclerView recyclerView;

    private List<Object> items;

    private LibraryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_library, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create an empty list to hold all items
        List<Object> items = new ArrayList<>();

        // Create an instance of LibraryAdapter
        adapter = new LibraryAdapter(getActivity(), items);

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);



        // Get all playlist library users
        PlaylistLibraryUserService playlistLibraryUserService = new PlaylistLibraryUserService();
        playlistLibraryUserService.getAllPlaylistLibraryUsers(new PlaylistLibraryUserService.AllPlaylistLibraryUsersCallback() {
            @Override
            public void onAllPlaylistLibraryUsersRetrieved(List<PlaylistsModel> playlistLibraryUsers) {
                // Add playlist library users to the list
                items.addAll(playlistLibraryUsers);
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
            }
        });

        AlbumLibraryUserService albumLibraryUserService = new AlbumLibraryUserService();
        albumLibraryUserService.getAllAlbumLibraryUsers(new AlbumLibraryUserService.AllAlbumLibraryUsersCallback() {
            @Override
            public void onAllAlbumLibraryUsersRetrieved(List<AlbumsModel> albumLibraryUsers) {
                items.addAll(albumLibraryUsers);
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

}
