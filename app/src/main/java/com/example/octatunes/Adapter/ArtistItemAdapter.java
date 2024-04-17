
package com.example.octatunes.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.R;
import java.util.List;


public class ArtistItemAdapter extends RecyclerView.Adapter<ArtistItemAdapter.ArtistViewHolder> {

    private List<ArtistsModel> artistList;

    public ArtistItemAdapter(List<ArtistsModel> artistList) {
        this.artistList = artistList;
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView artistImage;
        TextView artistName;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.artist_image);
            artistName = itemView.findViewById(R.id.artist_item_title);
        }
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_artist_item, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        //ArtistModel artistItemModel = artistList.get(position);
        //
        //holder.artistImage.setImageResource(artistItemModel.getImageResource());
        //holder.artistName.setText(artistItemModel.getArtistName());
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }


}
