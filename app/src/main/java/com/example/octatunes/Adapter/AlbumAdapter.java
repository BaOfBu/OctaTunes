package com.example.octatunes.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.AlbumsModel;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>{
    private List<AlbumsModel> albumsList = new ArrayList<>();

    public AlbumAdapter() {

    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
