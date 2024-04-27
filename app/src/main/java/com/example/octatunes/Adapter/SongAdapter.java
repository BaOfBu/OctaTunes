package com.example.octatunes.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.ArtistDetailFragment;
import com.example.octatunes.Activity.NowPlayingBarFragment;
import com.example.octatunes.Activity.PlaylistSpotifyActivity;
import com.example.octatunes.FragmentListener;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.Playlist_TracksModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.TrackService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context context;
    private List<TracksModel> songList;
    private FragmentListener listener;
    private PlaylistsModel playList;

    public SongAdapter(Context context, List<TracksModel> songList, FragmentListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
    }

    public SongAdapter(Context context, List<TracksModel> songList, FragmentListener listener, PlaylistsModel playList) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
        this.playList = playList;
    }

    private void sendSignalToMainActivity(List<TracksModel> tracksModels, int trackID, int albumID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived(tracksModels, trackID, albumID, from, belong, mode);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_playlist_spotify_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TracksModel track = songList.get(position);
        holder.itemNumber.setText(String.valueOf(position + 1));
        holder.itemTitle.setText(track.getName());

        // Load image for the track
        loadImageForTrack(track, holder.itemImage);

        // Load artist name for the track
        loadArtistName(track.getAlubumID(), holder.itemArtist);


        holder.itemView.setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                if (fragment != null) {
                    if (fragment instanceof PlaylistSpotifyActivity) {
                        String mode = "sequencePlay";
                        int trackFirstId = track.getTrackID();
                        int albumId = -1;
                        String from = "PLAYING FROM PLAYLIST";
                        String belong = playList.getName();
                        int playlistId = playList.getPlaylistID();
                        sendSignalToMainActivity(songList, trackFirstId, albumId, from, belong, mode);
                    } else if (fragment instanceof ArtistDetailFragment) {

                    }
                }
            }
        });

        holder.songMoreInfo.setOnClickListener(v -> {
            //// Creating the BottomSheetDialog
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_song);
            //
            ImageView tbin_playlist_bottom_sheet_image = bottomSheetDialog.findViewById(R.id.song_item_image_bottom_sheet);
            TextView titleBottomSheet = bottomSheetDialog.findViewById(R.id.song_bottom_sheet_item_title);
            TextView item_artist = bottomSheetDialog.findViewById(R.id.song_bottom_sheet_item_artist);
            tbin_playlist_bottom_sheet_image.setImageDrawable(holder.itemImage.getDrawable());
            titleBottomSheet.setText(track.getName());
            item_artist.setText(holder.itemArtist.getText());

            bottomSheetDialog.show();
        });
    }

    private void loadImageForTrack(TracksModel track, ImageView imageView) {
        TrackService trackService = new TrackService();
        trackService.getImageForTrack(track, new TrackService.OnImageLoadedListener() {
            @Override
            public void onImageLoaded(String imageUrl) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(imageView);
                } else {
                }
            }
        });
    }

    private void loadArtistName(int albumId, TextView textView) {
        TrackService trackService = new TrackService();
        trackService.getArtistNameByAlbumId(albumId, new TrackService.OnArtistNameLoadedListener() {
            @Override
            public void onArtistNameLoaded(String artistName) {
                if (artistName != null) {
                    textView.setText(artistName);
                } else {
                    textView.setText("Unknown Artist");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNumber, itemTitle, itemArtist;
        ImageView itemImage, songMoreInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNumber = itemView.findViewById(R.id.item_number);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemArtist = itemView.findViewById(R.id.item_artist);
            itemImage = itemView.findViewById(R.id.item_image);
            songMoreInfo = itemView.findViewById(R.id.song_more_info);
        }
    }
}
