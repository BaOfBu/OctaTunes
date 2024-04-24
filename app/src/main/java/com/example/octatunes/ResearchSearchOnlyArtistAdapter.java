package com.example.octatunes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.ArtistSpotifyActivity;
import com.example.octatunes.Activity.PlaylistSpotifyActivity;
import com.example.octatunes.Model.ArtistsModel;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ResearchSearchOnlyArtistAdapter extends RecyclerView.Adapter<ResearchSearchOnlyArtistAdapter.ViewHolder>{
    private List<ArtistsModel> artistProfileModels;
    private Context context;

    public ResearchSearchOnlyArtistAdapter(List<ArtistsModel> artistProfileModels, Context context) {
        this.artistProfileModels = artistProfileModels;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImage;
        private TextView fullname;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.artist_image);
            fullname = itemView.findViewById(R.id.artist_name);
        }
    }
    @NonNull
    @Override
    public ResearchSearchOnlyArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_artist_preview, parent, false);
        return new ResearchSearchOnlyArtistAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ResearchSearchOnlyArtistAdapter.ViewHolder holder, int position) {
        ArtistsModel artistsModel = null;
        artistsModel = artistProfileModels.get(position);

        Picasso.get().load(artistsModel.getImage()).into(holder.userImage);
        holder.fullname.setText(artistsModel.getName());
        ArtistsModel finalArtistsModel = artistsModel;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArtistSpotifyActivity fragment = new ArtistSpotifyActivity();
//                Bundle bundle = new Bundle();
//                bundle.putString("artistModel", new Gson().toJson(finalArtistsModel));
//                fragment.setArguments(bundle);
//
//                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, fragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
            }
        });
    }
    @Override
    public int getItemCount() {
        return artistProfileModels.size();

    }
}
