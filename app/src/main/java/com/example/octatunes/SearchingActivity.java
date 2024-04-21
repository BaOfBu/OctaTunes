package com.example.octatunes;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Services.AlbumService;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.TrackService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SearchingActivity extends Fragment {
    String queryString;
    TextView searchBox;
    private Handler handler = new Handler();
    private Runnable searchRunnable;
    private final long DELAY = 1000; // Delay in milliseconds
    private AlbumService albumService = new AlbumService();
    private ArtistService artistService = new ArtistService();
    private TrackService trackService = new TrackService();

    RecyclerView searchResultsRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.layout_search, container, false);
        searchBox = rootView.findViewById(R.id.search_bar_edit_text);
        searchResultsRecyclerView = rootView.findViewById(R.id.recyclerview_recent_search);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable); // Remove pending searchRunnable if any
                // Create a new searchRunnable
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // Call the search method after the delay
                        String queryString = searchBox.getText().toString(); // Get the current text from the search box
                try {
                    search(queryString); // Call the search method with the current text
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                    }
                };
                // Post the searchRunnable with a delay
                handler.postDelayed(searchRunnable, DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager)
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("searchQuery", searchBox.getText().toString());
                    Fragment searchActivity = new SearchActivity();
                    searchActivity.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, searchActivity).commit();
                    return true;
                }
                return false;
            }
        });
        return rootView;
    }



    public void search(String query) throws InterruptedException {
        trackService.findTrackByName(query, new TrackService.OnTracksLoadedListener() {
            @Override
            public void onTracksLoaded(List<TracksModel> tracks) {
                Log.e("SearchActivity", "Tracks: " + tracks.size());
                TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(tracks, getContext());
                searchResultsRecyclerView.setAdapter(trackPreviewAdapter);
                trackPreviewAdapter.notifyDataSetChanged();
            }
        });

    }
}
