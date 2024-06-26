package com.example.octatunes.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.ArtistDetailFragment;
import com.example.octatunes.Activity.DeviceSongFragment;
import com.example.octatunes.Activity.LikedSongFragment;
import com.example.octatunes.Activity.NowPlayingBarFragment;
import com.example.octatunes.Activity.PlaylistSpotifyActivity;
import com.example.octatunes.FragmentListener;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.Playlist_TracksModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Model.UserSongModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.LoveService;
import com.example.octatunes.Services.MusicService;
import com.example.octatunes.Services.PlaylistTrackService;
import com.example.octatunes.Services.SongService;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.Services.UserService;
import com.example.octatunes.Services.UserSongService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.LogRecord;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private int userId;
    private int playlistLoveId;
    private Context context;
    private List<TracksModel> songList;
    private FragmentListener listener;
    private PlaylistsModel playList;
    Handler checkDownload;

    private OnSongRemoveListener onSongRemoveListener;
    private List<SongModel> songModelList;
    private boolean isDeviceSong = false;

    public SongAdapter(Context context, List<TracksModel> songList, FragmentListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener=listener;
    }
    public SongAdapter(Context context, List<TracksModel> songList, FragmentListener listener,int userId,int playlistLoveId) {
        this.context = context;
        this.songList = songList;
        this.listener=listener;
        this.userId=userId;
        this.playlistLoveId = playlistLoveId;
    }
    public SongAdapter(Context context, List<TracksModel> songList, FragmentListener listener,int userId,int playlistLoveId,OnSongRemoveListener onSongRemoveListener) {
        this.onSongRemoveListener =onSongRemoveListener;
        this.context = context;
        this.songList = songList;
        this.listener=listener;
        this.userId=userId;
        this.playlistLoveId = playlistLoveId;
    }
    public SongAdapter(Context context, List<TracksModel> songList, FragmentListener listener,PlaylistsModel playList,int userId,int playlistLoveId) {
        this.context = context;
        this.songList = songList;
        this.listener=listener;
        this.playList=playList;
        this.userId = userId;
        this.playlistLoveId = playlistLoveId;
    }

    public SongAdapter(Context context, List<TracksModel> songList, FragmentListener listener,PlaylistsModel playList,int userId,int playlistLoveId,OnSongRemoveListener onSongRemoveListener) {
        this.context = context;
        this.songList = songList;
        this.listener=listener;
        this.playList=playList;
        this.userId = userId;
        this.playlistLoveId = playlistLoveId;
        this.onSongRemoveListener = onSongRemoveListener;
    }
    public SongAdapter(Context context, List<SongModel> songModelList, FragmentListener listener, boolean isDeviceSong){
        this.context = context;
        this.songModelList = songModelList;
        this.listener = listener;
        this.isDeviceSong = isDeviceSong;
    }


    private void sendSignalToMainActivity(List<TracksModel> tracksModels, int trackID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived2(tracksModels, trackID, from, belong, mode);
        }
    }
    private void sendSignalToMainActivity(int trackID, int playlistID, int albumID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived(trackID, playlistID, albumID, from, belong, mode);
        }
    }
//    private void sendSignalToMainActivity(List<SongModel> songModels, int songID, String from, String belong, String mode, int nothing) {
//        if (listener != null) {
//            listener.onSignalReceived3(songModels, songID, from, belong, mode);
//        }
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_playlist_spotify_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!isDeviceSong) {
            TracksModel track = songList.get(holder.getAdapterPosition());
            holder.itemNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));
            holder.itemTitle.setText(track.getName());

            // Load image for the track
            loadImageForTrack(track, holder.itemImage);

            // Load artist name for the track
            if (context instanceof FragmentActivity) {
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                if (fragment != null) {
                    if (!(fragment instanceof ArtistDetailFragment)) {
                        loadArtistName(track.getAlubumID(), holder.itemArtist);
                    } else {
                        loadCountListenOfTrack(track.getName(), holder.itemArtist);
                    }
                }
            }


            if (listener == null) {
                listener = (FragmentListener) context;
            }

            if (context instanceof FragmentActivity) {
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                if (fragment != null) {
                    if (fragment instanceof LikedSongFragment) {
                        holder.itemNumber.setVisibility(View.GONE);
                    }
                }
            }

            holder.itemView.setOnClickListener(v -> {
                if (context instanceof FragmentActivity) {
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                    if (fragment != null) {
                        if (fragment instanceof PlaylistSpotifyActivity) {
                            String mode = "sequencePlay";
                            int trackFirstId = track.getTrackID();
                            String from = "PLAYING FROM PLAYLIST";
                            String belong = playList.getName();
                            sendSignalToMainActivity(songList, trackFirstId, from, belong, mode);
                        } else if (fragment instanceof ArtistDetailFragment) {
                            extracted(track);
                        } else if (fragment instanceof LikedSongFragment) {
                            String mode = "sequencePlay";
                            int trackFirstId = track.getTrackID();
                            int albumId = -1;
                            String from = "PLAYING FROM PLAYLIST";
                            String belong = "Liked Songs";
                            int playlistId = playlistLoveId;
                            sendSignalToMainActivity(trackFirstId, playlistId, albumId, from, belong, mode);
                        }
                    }
                }
            });

            holder.songMoreInfo.setOnClickListener(v -> {

                // Checking if a song is already loved
                LoveService loveService = new LoveService();

                if (context instanceof FragmentActivity) {
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                    if (fragment != null) {
                        if (!(fragment instanceof LikedSongFragment)) {
                            //// Creating the BottomSheetDialog
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                            bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet_song);

                            ImageView tbin_playlist_bottom_sheet_image = bottomSheetDialog.findViewById(R.id.song_item_image_bottom_sheet);
                            TextView titleBottomSheet = bottomSheetDialog.findViewById(R.id.song_bottom_sheet_item_title);
                            TextView add_to_liked_song = bottomSheetDialog.findViewById(R.id.add_to_liked_song);
                            TextView item_artist = bottomSheetDialog.findViewById(R.id.song_bottom_sheet_item_artist);
                            tbin_playlist_bottom_sheet_image.setImageDrawable(holder.itemImage.getDrawable());
                            titleBottomSheet.setText(track.getName());
                            item_artist.setText(holder.itemArtist.getText());
                            loveService.isLoved(userId, track.getTrackID(), new LoveService.LoveCheckCallback() {
                                @Override
                                public void onLoveChecked(boolean isLoved) {
                                    if (isLoved) {
                                        holder.check_icon_liked_song.setVisibility(View.VISIBLE);
                                        add_to_liked_song.setVisibility(View.GONE);
                                    } else {
                                        holder.check_icon_liked_song.setVisibility(View.GONE);
                                        add_to_liked_song.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            // Set click listener for the TextView add_to_liked_song
                            add_to_liked_song.setOnClickListener(view -> {
                                loveService.addLove(userId, track.getTrackID());
                                PlaylistTrackService playlistTrackService = new PlaylistTrackService();
                                playlistTrackService.addLovePlaylistTrack(track.getTrackID());
                                //playlistTrackService.addPlaylistTrack(new Playlist_TracksModel(playlistLoveId, track.getTrackID(),0));
                                holder.check_icon_liked_song.setVisibility(View.VISIBLE);
                                add_to_liked_song.setVisibility(View.GONE);
                                bottomSheetDialog.dismiss();
                            });
                            // Set click listener for the TextView share
                            TextView shareSong = bottomSheetDialog.findViewById(R.id.share);
                            shareSong.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Download the song
                                    try {
                                        downloadFile(track.getFile(), track.getName(), context);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            bottomSheetDialog.show();
                        } else {
                            BottomSheetDialog bottomSheetDialogLiked = new BottomSheetDialog(context);
                            bottomSheetDialogLiked.setContentView(R.layout.bottom_sheet_song_liked);

                            ImageView song_liked_item_image_bottom_sheet = bottomSheetDialogLiked.findViewById(R.id.song_liked_item_image_bottom_sheet);
                            TextView song_liked_bottom_sheet_item_title = bottomSheetDialogLiked.findViewById(R.id.song_liked_bottom_sheet_item_title);
                            TextView song_liked_bottom_sheet_item_artist = bottomSheetDialogLiked.findViewById(R.id.song_liked_bottom_sheet_item_artist);
                            TextView song_liked_remove_liked = bottomSheetDialogLiked.findViewById(R.id.song_liked_remove_liked);

                            song_liked_item_image_bottom_sheet.setImageDrawable(holder.itemImage.getDrawable());
                            song_liked_bottom_sheet_item_title.setText(track.getName());
                            song_liked_bottom_sheet_item_artist.setText(holder.itemArtist.getText());

                            song_liked_remove_liked.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PlaylistTrackService playlistTrackService = new PlaylistTrackService();
                                    playlistTrackService.removePlaylistTrack(playlistLoveId, track.getTrackID());
                                    loveService.removeLove(userId, track.getTrackID());
                                    songList.remove(holder.getAdapterPosition());
                                    notifyDataSetChanged();
                                    if (onSongRemoveListener != null) {
                                        onSongRemoveListener.onSongRemoved();
                                    }
                                    bottomSheetDialogLiked.dismiss();
                                }
                            });

                            // Set click listener for the TextView share
                            TextView shareSong = bottomSheetDialogLiked.findViewById(R.id.share);
                            shareSong.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Download the song
                                    try {
                                        downloadFile(track.getFile(), track.getName(), context);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            bottomSheetDialogLiked.show();
                        }
                    }
                }


            });
        } else {
            SongModel song = songModelList.get(holder.getAdapterPosition());
            holder.itemNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));
            holder.itemTitle.setText(song.getTitle());
            holder.itemImage.setImageResource(R.drawable.song_chacaidoseve);

            if (context instanceof FragmentActivity) {
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                if (fragment != null) {
                    holder.itemArtist.setText(song.getArtist());
                    holder.itemNumber.setVisibility(View.GONE);
                }
            }
            if (listener == null) {
                listener = (FragmentListener) context;
            }
            holder.itemView.setOnClickListener(v -> {
                if (context instanceof FragmentActivity) {
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
                    if (fragment != null) {
                        Log.e("SONG ADAPTER", "LOAD SUCCESS");
                        String mode = "sequencePlay";
                        int clickedPosition = holder.getAdapterPosition();
                        String from = "PLAYING FROM PLAYLIST";
                        String belong = "Device Songs";

                        sendSignalToMainActivity2(songModelList, clickedPosition, from, belong, mode);
                    }
                }
            });

        }
    }
    public void downloadFile(String url,String filename, Context context) throws InterruptedException {
        String DownloadUrl = url;
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request1.setDescription("Download Music for Share");   //appears the same in Notification bar while downloading
        request1.setTitle(filename+".mp3");
        request1.setVisibleInDownloadsUi(true);
        request1.allowScanningByMediaScanner();
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request1.setDestinationInExternalFilesDir(context, "/MusicFolder" ,filename + ".mp3");
        DownloadManager manager1 = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = Objects.requireNonNull(manager1).enqueue(request1);
        Cursor cursor = manager1.query(new DownloadManager.Query().setFilterById(downloadId));
        checkDownload = new Handler();
        checkDownload.postDelayed(new Runnable() {
            @Override
            public void run() {
                Boolean flag = checkDownloadStatus(cursor, filename);
                if (flag) {
                    checkDownload.removeCallbacksAndMessages(null);
                }
                else{
                    checkDownload.postDelayed(this, 500);
                }
            }
        }, 500);
    }
    private Boolean checkDownloadStatus(Cursor cursor, String filename) {
        if (cursor.moveToFirst()) {
            File[] externalFilesDirs = context.getExternalFilesDirs(null);
            if (externalFilesDirs != null && externalFilesDirs.length > 0) {
                // Get the first external files directory (primary storage)
                File primaryExternalDir = externalFilesDirs[0];
                String fileName = filename + ".mp3";
                // Construct the full path to the file within the "LyricFolder" subfolder
                File subFolder = new File(primaryExternalDir, "MusicFolder");
                File file = new File(subFolder, fileName);
                Log.d("Download", "Music downloaded to: " + file.getAbsolutePath());
                Log.d("Download", "Music downloaded existed ?: " + file.exists());
                if (file.exists()) {
                    copyFileToExternalStorageAndShare(file);
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        return false;
    }

    public void copyFileToExternalStorageAndShare(File mp3File) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/mp3");
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", mp3File);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
        share.setPackage("com.google.android.gm");
        context.startActivity(Intent.createChooser(share, "Share Sound File"));
    }
    private void sendSignalToMainActivity2(List<SongModel> songModels, int pos, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived3(songModels, pos, from, belong, mode);
        }
    }
    private void loadCountListenOfTrack(String trackName, final TextView itemArtist) {
        final SongService songService = new SongService();
        songService.countSongsWithTitle(trackName, new SongService.OnSongCountListener() {
            @Override
            public void onSongCountRetrieved(int count) {
                // Convert the count to String before setting it as text to the TextView
                itemArtist.setText("Lượt nghe" + " " + String.valueOf(count));
            }

            @Override
            public void onSongCountFailed(String errorMessage) {
                Log.e("loadCountListenOfTrack", "Failed to retrieve count: " + errorMessage);
            }
        });
    }


    public void extracted(TracksModel track) {
        if(listener == null) {
            listener = (FragmentListener) context;
        }
        String mode = "sequencePlay";
        sendSignalToMainActivity(track.getTrackID(), -1, track.getAlubumID(), "PLAYING FROM SEARCH", "Track", mode);
    }

    private void loadImageForTrack(TracksModel track, ImageView imageView) {
        TrackService trackService = new TrackService();
        trackService.getImageForTrack(track, new TrackService.OnImageLoadedListener() {
            @Override
            public void onImageLoaded(String imageUrl) {
                if (imageUrl != null && imageUrl!="") {
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
        if(!isDeviceSong){
            return songList.size();
        }else{
            return songModelList.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNumber, itemTitle, itemArtist;
        ImageView itemImage, songMoreInfo, check_icon_liked_song;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNumber = itemView.findViewById(R.id.item_number);
            itemTitle = itemView.findViewById(R.id.item_title_tbin);
            itemArtist = itemView.findViewById(R.id.item_artist_tbin);
            itemImage = itemView.findViewById(R.id.item_image_tbin);
            songMoreInfo = itemView.findViewById(R.id.song_more_info);
            check_icon_liked_song=itemView.findViewById(R.id.check_icon_liked_song);
        }
    }
    public interface OnSongRemoveListener {
        void onSongRemoved();
    }
}