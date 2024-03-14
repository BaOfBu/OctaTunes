package com.example.octatunes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ResultSearchOnlyAlbumAdapter extends RecyclerView.Adapter<ResultSearchOnlyAlbumAdapter.ViewHolder>{
    private ArrayList<TrackPreviewModel> albumPreviewModelsLeft;
    private ArrayList<TrackPreviewModel> albumPreviewModelsRight;
    private Context context;

    public ResultSearchOnlyAlbumAdapter(ArrayList<TrackPreviewModel> albumPreviewModelsLeft, ArrayList<TrackPreviewModel>  albumPreviewModelsRight, Context context) {
        this.albumPreviewModelsLeft = albumPreviewModelsLeft;
        this.albumPreviewModelsRight = albumPreviewModelsRight;
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
            TrackPreviewModel trackPreviewModelLeft = albumPreviewModelsLeft.get(position);
            Picasso.get().load(trackPreviewModelLeft.getTrackImageId()).into(holder.albumImageLeft);
            holder.albumNameLeft.setText(trackPreviewModelLeft.getTrackName());
            holder.albumArtistLeft.setText(trackPreviewModelLeft.getTrackArtist());
            holder.publishedYearLeft.setText(Integer.toString(trackPreviewModelLeft.getPublishedYear()));
        }

        if(albumPreviewModelsRight.get(position) != null){
            TrackPreviewModel trackPreviewModelRight = albumPreviewModelsRight.get(position);
            Picasso.get().load(trackPreviewModelRight.getTrackImageId()).into(holder.albumImageRight);
            holder.albumNameRight.setText(trackPreviewModelRight.getTrackName());
            holder.albumArtistRight.setText(trackPreviewModelRight.getTrackArtist());
            holder.publishedYearRight.setText(Integer.toString(trackPreviewModelRight.getPublishedYear()));
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
}
