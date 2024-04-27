package com.example.octatunes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.R; // Replace this with your actual package name
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PopularReleaseAlbumArtistAdapter extends RecyclerView.Adapter<PopularReleaseAlbumArtistAdapter.ViewHolder> {

    private Context context;
    private List<AlbumsModel> albumList;

    public PopularReleaseAlbumArtistAdapter(Context context, List<AlbumsModel> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_popular_release_album_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlbumsModel album = albumList.get(position);

        // Load album image using Picasso
        if (album.getImage()!="" && album.getImage()!=null){
            Picasso.get().load(album.getImage()).into(holder.albumImageView);
        }


        // Set album name
        holder.albumNameTextView.setText(album.getName());

        if (position == 0) {
            holder.releaseYearTextView.setText("Latest Release - Single");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
            String releaseYear = dateFormat.format(album.getReleaseDate());
            holder.releaseYearTextView.setText(releaseYear + " - Single");
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView albumImageView;
        TextView albumNameTextView;
        TextView releaseYearTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImageView = itemView.findViewById(R.id.album_image_view_popular_release_artist_detail);
            albumNameTextView = itemView.findViewById(R.id.album_name_text_popular_release_artist_detail);
            releaseYearTextView = itemView.findViewById(R.id.album_release_date_popular_release_artist_detail);
        }
    }
}
