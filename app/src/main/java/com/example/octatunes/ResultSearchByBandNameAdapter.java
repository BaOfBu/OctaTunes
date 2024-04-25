package com.example.octatunes;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.ArtistDetailFragment;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.TracksModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ResultSearchByBandNameAdapter extends RecyclerView.Adapter<ResultSearchByBandNameAdapter.ViewHolder>{
    private List<ArtistsModel> artistProfileModel;
    private List<AlbumsModel> albumPreviewModels;
    private List<TracksModel> trackPreviewModels;
    private Context context;

    public ResultSearchByBandNameAdapter(List<ArtistsModel> artistProfileModel, List<AlbumsModel> albumModels, List<TracksModel> trackModels, Context context) {
        this.artistProfileModel = artistProfileModel;
        this.albumPreviewModels = albumModels;
        this.trackPreviewModels = trackModels;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView artistImage;
        private TextView artistName;

        private TextView cosuxuathien;
        private RecyclerView albumRecyclerView;
        private RecyclerView trackRecyclerView;
        private RelativeLayout artistLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.artist_image);
            artistName = itemView.findViewById(R.id.artist_name);

            artistLayout = itemView.findViewById(R.id.artist);
            cosuxuathien = itemView.findViewById(R.id.textview_album);

            albumRecyclerView = itemView.findViewById(R.id.recyclerview_album_preview); // Initialize the new RecyclerView
            trackRecyclerView = itemView.findViewById(R.id.recyclerview_track_preview); // Initialize the new RecyclerView

        }
    }
    @NonNull
    @Override
    public ResultSearchByBandNameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result_search_band, parent, false);
        return new ResultSearchByBandNameAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultSearchByBandNameAdapter.ViewHolder holder, int position) {
//        Log.e("ByBand","artist model:"+ artistProfileModel.size());
//        Log.e("ByBand","album model:"+ albumPreviewModels.size());
//        Log.e("ByBand","track model:"+ trackPreviewModels.size());

        if(artistProfileModel.size() == 0){
            holder.artistLayout.setVisibility(View.GONE);
        }
        else{
            holder.artistName.setVisibility(View.VISIBLE);
            holder.artistImage.setVisibility(View.VISIBLE);
            Picasso.get().load(artistProfileModel.get(0).getImage()).into(holder.artistImage);
            holder.artistName.setText(artistProfileModel.get(0).getName());
            holder.artistLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArtistDetailFragment fragment = new ArtistDetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("artist", artistProfileModel.get(0));
                    fragment.setArguments(bundle);

                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        if(albumPreviewModels.size() == 0){
            holder.albumRecyclerView.setVisibility(View.GONE);
            holder.cosuxuathien.setVisibility(View.GONE);
        }
        else{
            holder.albumRecyclerView.setVisibility(View.VISIBLE);
            holder.cosuxuathien.setVisibility(View.VISIBLE);
            AlbumPreviewAdapter albumPreviewAdapter = new AlbumPreviewAdapter(albumPreviewModels, context);
            holder.albumRecyclerView.setAdapter(albumPreviewAdapter);
            albumPreviewAdapter.notifyDataSetChanged();
        }

        if(trackPreviewModels == null){
            holder.trackRecyclerView.setVisibility(View.GONE);
        }
        else {
            holder.trackRecyclerView.setVisibility(View.VISIBLE);
            // Create a temporary list to pass to the adapter (to avoid modifying the original list
            TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(trackPreviewModels, context,null, null);
            holder.trackRecyclerView.setAdapter(trackPreviewAdapter);
            trackPreviewAdapter.notifyDataSetChanged();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    @Override
    public int getItemCount() {
        return 1;
    }
}
