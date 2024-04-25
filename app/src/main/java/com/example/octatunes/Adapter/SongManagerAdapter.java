package com.example.octatunes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.SongManagerModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.AlbumService;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongManagerAdapter {
    private List<SongManagerModel> songs;
    private Context context;
    private AlbumService albumService;

    public SongManagerAdapter(Context context, List<SongManagerModel> songs) {
        this.context = context;
        this.songs = songs;
        this.albumService = new AlbumService();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_song_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongManagerModel songManagerModel = songs.get(position);
        holder.textViewName.setText(songManagerModel.getName());
        holder.textViewArtist.setText(songManagerModel.getArtist());

        albumService.getAlbumImageUrl(songManagerModel.getAlbumID(), new AlbumService.OnImageUrlRetrievedListener() {
            @Override
            public void onImageUrlRetrieved(String imageUrl) {
                if (imageUrl != null) {
                    // Sử dụng Picasso để tải và hiển thị hình ảnh từ URL vào imageViewSong
                    Picasso.get()
                            .load(imageUrl)
                            .into(holder.imageViewSong);
                } else {
                    // Xử lý khi không tìm thấy URL hình ảnh
                    // Có thể hiển thị một hình ảnh mặc định hoặc không làm gì cả
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewSong;
        TextView textViewName;
        TextView textViewArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSong = itemView.findViewById(R.id.imageViewSong);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewArtist = itemView.findViewById(R.id.textViewArtist);
        }
    }
}
