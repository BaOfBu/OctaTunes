package com.example.octatunes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.PlaylistSpotifyActivity;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListPlaylistAdapter extends RecyclerView.Adapter<ListPlaylistAdapter.ViewHolder> {

    private List<PlaylistsModel> playlistItems;
    private Context context;
    public ListPlaylistAdapter(Context context) {
        this.playlistItems = new ArrayList<>();
        this.context = context;
    }
    public ListPlaylistAdapter() {
        this.playlistItems = new ArrayList<>();
    }

    public void setPlaylistItems(List<PlaylistsModel> playlistItems) {
        this.playlistItems = playlistItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleTextView;
        public TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.playlist_cover_image_item);
            titleTextView = itemView.findViewById(R.id.playlist_item_title);
            descriptionTextView = itemView.findViewById(R.id.playlist_item_description);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaylistsModel item = playlistItems.get(position);
        holder.titleTextView.setText(item.getName());
        holder.descriptionTextView.setText(item.getDescription());
        if (!Objects.equals(item.getImage(), "")){
            Picasso.get().load(item.getImage()).into(holder.imageView);
        }
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            PlaylistSpotifyActivity fragment = new PlaylistSpotifyActivity();
            Bundle bundle = new Bundle();
            bundle.putString("playlistItem", new Gson().toJson(item));
            fragment.setArguments(bundle);

            FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }
}


