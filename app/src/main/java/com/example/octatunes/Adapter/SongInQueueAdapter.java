package com.example.octatunes.Adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Activity.DetailPlaylistFragment;
import com.example.octatunes.Activity.ListenToMusicActivity;
import com.example.octatunes.FragmentListener;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.MusicService;

import java.util.List;
public class SongInQueueAdapter extends RecyclerView.Adapter<SongInQueueAdapter.ViewHolder> {
    private FragmentListener listener;
    private Context context;
    private List<SongModel> songList;
    public SongInQueueAdapter(Context context, FragmentListener listener, List<SongModel> songList) {
        this.context = context;
        this.listener = listener;
        this.songList = songList;
    }
    private void sendSignalToMainActivity() {
        if (listener != null) {
            listener.onSignalReceived4();
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_track_in_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongModel songCurrent = songList.get(position);
        Log.i("SONG IN QUEUE ADAPTER", songCurrent.toString());
        holder.itemTitle.setText(songCurrent.getTitle());
        holder.itemArtist.setText(songCurrent.getArtist());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();

                List<SongModel> newList = MusicService.loadSongQueue(clickedPosition);

                MusicService.setSongList(newList);
                MusicService.setPos(newList.size() - 1);

                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                Fragment fragment = new DetailPlaylistFragment(ListenToMusicActivity.from, ListenToMusicActivity.belong, ListenToMusicActivity.mode, songCurrent);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

                for(int i = 0; i < MusicService.getSongList().size(); i++){
                    Log.i("QUEUE ADAPTER - SONG LIST " + i, MusicService.getSongList().get(i).toString());
                }
                Log.i("QUEUE ADAPTER - SONG LIST POS: ", String.valueOf(MusicService.getPos()));

                sendSignalToMainActivity();
            }
        });
    }
    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle, itemArtist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.track_name);
            itemArtist = itemView.findViewById(R.id.track_artist);
        }
    }
}
