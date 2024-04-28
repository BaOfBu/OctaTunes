package com.example.octatunes.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context context;
    private List<TracksModel> songList;
    private FragmentListener listener;
    private PlaylistsModel playList;
    Handler checkDownload;

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

    private void sendSignalToMainActivity(int trackID, int playlistID, int albumID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived(trackID, playlistID, albumID, from, belong, mode);
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
                        sendSignalToMainActivity(trackFirstId, playlistId, albumId, from, belong, mode);
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

            TextView share = bottomSheetDialog.findViewById(R.id.share);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        downloadFile(track.getFile(), track.getName(), context);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            bottomSheetDialog.show();
        });
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
        File externalStorageDir = Environment.getExternalStorageDirectory(); // Get external storage directory
        File newFile = new File(externalStorageDir, mp3File.getName()); // Create a new file in external storage directory
        Log.d("Download", "New file path: " + newFile.getAbsolutePath());
        try {
            Log.d("Download", "In Try block...");
            FileInputStream inStream = new FileInputStream(mp3File);
            FileOutputStream outStream = new FileOutputStream(newFile);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inStream.read(buffer)) > 0) {
                Log.d("Download", "Copying file...");
                outStream.write(buffer, 0, length);
            }
            inStream.close();
            outStream.close();
            // Share the copied file
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/mp3");
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(newFile));
            share.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
            share.setPackage("com.google.android.gm");
            context.startActivity(Intent.createChooser(share, "Share Sound File"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
