package com.example.octatunes;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

public class NonScrollLinearLayoutManager extends LinearLayoutManager {

    public NonScrollLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        // Disable vertical scrolling
        return false;
    }
}

