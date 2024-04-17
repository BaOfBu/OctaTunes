package  com.example.octatunes.Adapter;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private List<PlaylistsModel> playlistItems;

    public PlaylistAdapter(List<PlaylistsModel> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleTextView;
        public TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.playlist_cover_image_item);
            titleTextView = itemView.findViewById(R.id.playlist_item_title);
            descriptionTextView = itemView.findViewById(R.id.playlist_item_description);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaylistsModel item = playlistItems.get(position);
        holder.titleTextView.setText(item.getName());
        holder.descriptionTextView.setText(item.getDescription());
        Picasso.get().load(item.getImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }
}
