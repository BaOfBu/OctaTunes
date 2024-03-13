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

public class AlbumPreviewAdapter extends RecyclerView.Adapter<AlbumPreviewAdapter.ViewHolder> {
    private ArrayList<TrackPreviewModel> albumPreviewModels;
    private Context context;

    public AlbumPreviewAdapter(ArrayList<TrackPreviewModel> albumPreviewModels, Context context) {
        this.albumPreviewModels = albumPreviewModels;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView albumImage;
        private TextView albumName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);
        }
    }
    @NonNull
    @Override
    public AlbumPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_album_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumPreviewAdapter.ViewHolder holder, int position) {
        TrackPreviewModel trackPreviewModel = albumPreviewModels.get(position);
        Picasso.get().load(trackPreviewModel.getTrackImageId()).into(holder.albumImage);
        holder.albumName.setText(trackPreviewModel.getTrackName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumPreviewModels.size();
    }

}
