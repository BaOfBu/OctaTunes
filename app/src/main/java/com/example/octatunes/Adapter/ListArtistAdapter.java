package com.example.octatunes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.ArtistDetailFragment;
import com.example.octatunes.Activity.PlaylistSpotifyActivity;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListArtistAdapter extends RecyclerView.Adapter<ListArtistAdapter.ViewHolder>{
    private List<ArtistsModel> artistsList;
    private Context context;

    public ListArtistAdapter(Context context) {
        this.artistsList = new ArrayList<>();
        this.context = context;
    }
    public ListArtistAdapter() {
        this.artistsList = new ArrayList<>();
    }
    public void setArtistItems(List<ArtistsModel> artistsList) {
        this.artistsList = artistsList;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView artist_item_title;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.artist_image);
            artist_item_title = itemView.findViewById(R.id.artist_item_title);
        }
    }
    @NonNull
    @Override
    public ListArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_artist_item, parent, false);
        return new ListArtistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListArtistAdapter.ViewHolder holder, int position) {
        ArtistsModel item = artistsList.get(position);
        holder.artist_item_title.setText(item.getName());
        if (!Objects.equals(item.getImage(), "")){
            Picasso.get().load(item.getImage()).into(holder.imageView);
        }

        // Set click listener for each item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistDetailFragment fragment = new ArtistDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("artist", item);
                fragment.setArguments(bundle);

                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistsList.size();
    }
}
