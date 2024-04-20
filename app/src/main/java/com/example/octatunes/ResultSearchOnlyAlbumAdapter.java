package com.example.octatunes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Services.TrackService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ResultSearchOnlyAlbumAdapter extends RecyclerView.Adapter<ResultSearchOnlyAlbumAdapter.ViewHolder>{
    private ArrayList<AlbumsModel> albumPreviewModelsLeft;
    private ArrayList<AlbumsModel> albumPreviewModelsRight;
    private Context context;

    private TrackService trackService = new TrackService();

    public ResultSearchOnlyAlbumAdapter(List<AlbumsModel> array, Context context) {
        albumPreviewModelsLeft = new ArrayList<>();
        albumPreviewModelsRight = new ArrayList<>();
        for(int i = 0; i < array.size(); i++) {
            if (i % 2 == 0) {
                albumPreviewModelsLeft.add(array.get(i));
            } else {
                albumPreviewModelsRight.add(array.get(i));
            }
        }

        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumImageLeft, albumImageRight;
        private TextView albumNameLeft, albumArtistLeft, publishedYearLeft;
        private TextView albumNameRight, albumArtistRight, publishedYearRight;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImageLeft = itemView.findViewById(R.id.album_image_left);
            albumNameLeft = itemView.findViewById(R.id.album_name_left);
            albumArtistLeft = itemView.findViewById(R.id.album_artist_left);
            publishedYearLeft = itemView.findViewById(R.id.published_year_left);

            albumImageRight = itemView.findViewById(R.id.album_image_right);
            albumNameRight = itemView.findViewById(R.id.album_name_right);
            albumArtistRight = itemView.findViewById(R.id.album_artist_right);
            publishedYearRight = itemView.findViewById(R.id.published_year_right);
        }
    }
    @NonNull
    @Override
    public ResultSearchOnlyAlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_album_preview_2, parent, false);
        return new ResultSearchOnlyAlbumAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ResultSearchOnlyAlbumAdapter.ViewHolder holder, int position) {
        if(albumPreviewModelsLeft.get(position) != null){
            AlbumsModel trackPreviewModelLeft = albumPreviewModelsLeft.get(position);
            Picasso.get().load(trackPreviewModelLeft.getImage()).into(holder.albumImageLeft);
            holder.albumNameLeft.setText(trackPreviewModelLeft.getName());
            trackService.getArtistNameByAlbumId(trackPreviewModelLeft.getAlbumID(), (TrackService.OnArtistNameLoadedListener) artistName -> holder.albumArtistLeft.setText(artistName));
            holder.publishedYearLeft.setText(Integer.toString(trackPreviewModelLeft.getReleaseDate().getYear()));
        }

        if(albumPreviewModelsRight.size() <= position){
            setupNullAlbumRight(holder);
            return;
        }

        if(albumPreviewModelsRight.get(position) != null){
            AlbumsModel trackPreviewModelRight = albumPreviewModelsRight.get(position);
            Picasso.get().load(trackPreviewModelRight.getImage()).into(holder.albumImageRight);
            holder.albumNameRight.setText(trackPreviewModelRight.getName());
            trackService.getArtistNameByAlbumId(trackPreviewModelRight.getAlbumID(), (TrackService.OnArtistNameLoadedListener) artistName -> holder.albumArtistRight.setText(artistName));
            holder.publishedYearRight.setText(Integer.toString(trackPreviewModelRight.getReleaseDate().getYear()));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    @Override
    public int getItemCount() {
        return Math.max(albumPreviewModelsLeft.size(), albumPreviewModelsRight.size());
    }

    private void setupNullAlbumLeft(ViewHolder holder) {
        holder.albumImageLeft.setVisibility(View.INVISIBLE);
        holder.albumNameLeft.setVisibility(View.INVISIBLE);
        holder.albumArtistLeft.setVisibility(View.INVISIBLE);
        holder.publishedYearLeft.setVisibility(View.INVISIBLE);
    }
    private void setupNullAlbumRight(ViewHolder holder) {
        holder.albumImageRight.setVisibility(View.INVISIBLE);
        holder.albumNameRight.setVisibility(View.INVISIBLE);
        holder.albumArtistRight.setVisibility(View.INVISIBLE);
        holder.publishedYearRight.setVisibility(View.INVISIBLE);
    }
}