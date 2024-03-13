package com.example.octatunes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListCategoriesButtonAdapter extends RecyclerView.Adapter<ListCategoriesButtonAdapter.ViewHolder> {
    private ArrayList<String> contentButton;
    private Context context;
    private OnCategorySelectedListener listener;

    private int selectedPosition = -1;
    public ListCategoriesButtonAdapter(ArrayList<String> contentButton, Context context, OnCategorySelectedListener listener) {
        this.contentButton = contentButton;
        this.context = context;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ToggleButton categoryButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryButton = itemView.findViewById(R.id.layout_custom_button);
        }
    }
    @NonNull
    @Override
    public ListCategoriesButtonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_custom_button, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ListCategoriesButtonAdapter.ViewHolder holder, int position) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        Color.parseColor("#1ed760"),
                        Color.parseColor("#05CCCCCC")
                }
        );

        ColorStateList colorStateFontList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        Color.BLACK,
                        Color.WHITE
                }
        );

        String textOnButton = contentButton.get(position);
        holder.categoryButton.setTextOn(textOnButton);
        holder.categoryButton.setTextOff(textOnButton);
        holder.categoryButton.setText(textOnButton);

        holder.categoryButton.setBackgroundTintList(colorStateList);
        holder.categoryButton.setTextColor(colorStateFontList);

        if(selectedPosition != -1 ) {
            holder.categoryButton.setChecked(selectedPosition == position);
        }else{
            holder.categoryButton.setChecked(position == 0);
            selectedPosition = 0;
        }

        holder.categoryButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                notifyItemChanged(selectedPosition);
                selectedPosition = holder.getAdapterPosition();
                String selectedCategory = contentButton.get(selectedPosition);
                if (listener != null) {
                    listener.onCategorySelected(selectedCategory);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentButton.size();
    }
    public interface OnCategorySelectedListener {
        void onCategorySelected(String category);
    }
}