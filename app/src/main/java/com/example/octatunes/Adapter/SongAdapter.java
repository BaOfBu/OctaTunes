package com.example.octatunes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<TracksModel> songs;

    private Context context;

    public SongAdapter(Context context,List<TracksModel> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_playlist_spotify_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        //TracksModel song = songs.get(position);
        //holder.title.setText(song.getTitle());
        //holder.artist.setText(song.getArtist());
        //holder.image.setImageResource(song.getImageUrl());
        //holder.itemNumber.setText(position + 1 + "");
        //holder.songMoreInfor.setOnClickListener(new View.OnClickListener(){
        //    @Override
        //    public void onClick(View view) {
        //        showBottomSheetDialog(song);
        //    }
        //});
        // Additional setup for other views and click listeners
    }
    public void showBottomSheetDialog(SongModel song) {
        // Use the Context to create the BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        // Use the Context to get the LayoutInflater
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_song, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        ImageView itemImage = bottomSheetView.findViewById(R.id.item_image);
        TextView itemTitle = bottomSheetView.findViewById(R.id.item_title);
        TextView itemArtist = bottomSheetView.findViewById(R.id.item_artist);

        // Set the data for the BottomSheet views
        //itemImage.setImageResource(song.getImageUrl());
        itemTitle.setText(song.getTitle());
        itemArtist.setText(song.getArtist());

        // Setup click listeners for the bottom sheet options if necessary

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        ImageView image;

        TextView itemNumber;

        ImageView songMoreInfor;

        public SongViewHolder(View itemView) {
            super(itemView);
            itemNumber = itemView.findViewById(R.id.item_number);
            title = itemView.findViewById(R.id.item_title);
            artist = itemView.findViewById(R.id.item_artist);
            image = itemView.findViewById(R.id.item_image);
            songMoreInfor = itemView.findViewById(R.id.song_more_info);
        }
    }
}
