package com.example.octatunes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ViewHolder>{
    private ArrayList<UserProfileModel> userProfileModels;
    private Context context;
    public UserProfileAdapter(ArrayList<UserProfileModel> userProfileModels, Context context) {
        this.userProfileModels = userProfileModels;
        this.context = context;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImage;
        private TextView fullname;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            fullname = itemView.findViewById(R.id.full_name);
        }
    }
    @NonNull
    @Override
    public UserProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_profile_preview, parent, false);
        return new UserProfileAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull UserProfileAdapter.ViewHolder holder, int position) {
        UserProfileModel userProfileModel = userProfileModels.get(position);
        Picasso.get().load(userProfileModel.getUserImageId()).into(holder.userImage);
        holder.fullname.setText(userProfileModel.getFullName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    @Override
    public int getItemCount() {
        return userProfileModels.size();
    }
}
