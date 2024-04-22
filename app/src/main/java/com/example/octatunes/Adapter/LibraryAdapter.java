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
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.AlbumService;
import com.example.octatunes.Services.PlaylistService;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PLAYLIST_ALBUM = 1;
    private static final int VIEW_TYPE_ARTIST = 2;

    private Context context;
    private List<Object> items; // Replace Object with your actual item type

    public LibraryAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_PLAYLIST_ALBUM:
                view = LayoutInflater.from(context).inflate(R.layout.layout_library_item_playlist_album, parent, false);
                return new PlaylistAlbumViewHolder(view);
            case VIEW_TYPE_ARTIST:
                view = LayoutInflater.from(context).inflate(R.layout.layout_library_item_artists, parent, false);
                return new ArtistViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_PLAYLIST_ALBUM:
                PlaylistAlbumViewHolder playlistAlbumViewHolder = (PlaylistAlbumViewHolder) holder;
                String imageUrl = (item instanceof PlaylistsModel) ?
                        ((PlaylistsModel) item).getImage() : ((AlbumsModel) item).getImage();
                String name = (item instanceof PlaylistsModel) ?
                        ((PlaylistsModel) item).getName() : ((AlbumsModel) item).getName();
                ((PlaylistAlbumViewHolder) playlistAlbumViewHolder).name_library_title.setText(name);
                if (item instanceof PlaylistsModel) {
                    PlaylistsModel playlist = (PlaylistsModel) item;
                    PlaylistService playlistService = new PlaylistService();
                    playlistService.getUsernameOfPlaylistCreator(playlist).thenAccept(username -> {
                        String description = "Playlist - " + username;
                        ((PlaylistAlbumViewHolder) playlistAlbumViewHolder).artist_library_description.setText(description);
                    }).exceptionally(ex -> {
                        return null;
                    });
                } else if (item instanceof AlbumsModel) {
                    AlbumsModel album = (AlbumsModel) item;
                    AlbumService albumService = new AlbumService();
                    albumService.getArtistNameForAlbum(album.getArtistID(), new AlbumService.ArtistNameCallback() {
                        @Override
                        public void onArtistNameRetrieved(String artistName) {
                            if (artistName != null) {
                                ((PlaylistAlbumViewHolder) playlistAlbumViewHolder).artist_library_description.setText("Album - " + artistName);
                            } else {
                                ((PlaylistAlbumViewHolder) playlistAlbumViewHolder).artist_library_description.setText("Album - Unknown Artist");
                            }
                        }
                    });
                }
                Picasso.get()
                        .load(imageUrl)
                        .into(((PlaylistAlbumViewHolder) playlistAlbumViewHolder).image_library_item);
                break;
            case VIEW_TYPE_ARTIST:
                ArtistViewHolder artistViewHolder = (ArtistViewHolder) holder;
                String artistImageUrl = ((ArtistsModel)item).getImage();
                String artistName = ((ArtistsModel)item).getName();
                ((ArtistViewHolder) artistViewHolder).name_library_title.setText(artistName);
                ((ArtistViewHolder) artistViewHolder).artist_library_description.setText("Artist");
                Picasso.get()
                        .load(artistImageUrl)
                        .into(((ArtistViewHolder) artistViewHolder).image_library_item);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof PlaylistsModel || item instanceof AlbumsModel) {
            return VIEW_TYPE_PLAYLIST_ALBUM;
        } else if (item instanceof ArtistsModel) {
            return VIEW_TYPE_ARTIST;
        } else {
            throw new IllegalArgumentException("Invalid item type");
        }
    }

    // ViewHolder for playlist and album items
    private static class PlaylistAlbumViewHolder extends RecyclerView.ViewHolder {
        TextView name_library_title;

        TextView artist_library_description;

        ImageView image_library_item;


        PlaylistAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            name_library_title = itemView.findViewById(R.id.name_library_title);
            artist_library_description = itemView.findViewById(R.id.artist_library_description);
            image_library_item = itemView.findViewById(R.id.image_library_item);
        }
    }

    // ViewHolder for artist items
    private static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView name_library_title;

        TextView artist_library_description;

        ImageView image_library_item;

        ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            name_library_title = itemView.findViewById(R.id.name_library_title);
            artist_library_description = itemView.findViewById(R.id.artist_library_description);
            image_library_item = itemView.findViewById(R.id.image_library_item);
        }
    }
}
