package com.example.octatunes.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;

import java.util.List;

public class ListPlaylistAdapter extends RecyclerView.Adapter<ListPlaylistAdapter.ViewHolder> {

    public ListPlaylistAdapter() {

    }

    public String getListPlaylistTitle() {
        return ListPlaylistTitle;
    }

    public void setListPlaylistTitle(String listPlaylistTitle) {
        ListPlaylistTitle = listPlaylistTitle;
    }

    private String ListPlaylistTitle;

    public List<PlaylistsModel> getPlaylistItems() {
        return playlistItems;
    }

    public void setPlaylistItems(List<PlaylistsModel> playlistItems) {
        this.playlistItems = playlistItems;
    }

    private List<PlaylistsModel> playlistItems;

    public ListPlaylistAdapter(String ListPlaylistTitle,List<PlaylistsModel> playlistItems) {
        this.ListPlaylistTitle =ListPlaylistTitle;
        this.playlistItems = playlistItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView playlistHeader;

        public ViewHolder(View itemView) {
            super(itemView);
            playlistHeader = itemView.findViewById(R.id.playlistHeader);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_playlist_section, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //PlaylistsModel item = playlistItems.get(position);
        //holder.titleTextView.setText(item.getTitle());
        //holder.descriptionTextView.setText(item.getDescription());
        holder.playlistHeader.setText(this.ListPlaylistTitle);

    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }
}