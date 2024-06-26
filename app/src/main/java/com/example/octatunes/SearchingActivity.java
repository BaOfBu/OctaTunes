package com.example.octatunes;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.HistoryModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.Model.UserSongModel;
import com.example.octatunes.Model.UsersModel;
import com.example.octatunes.Services.AlbumService;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.HistoryService;
import com.example.octatunes.Services.TrackService;
import com.example.octatunes.Services.UserService;
import com.example.octatunes.Services.UserSongService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private HistoryService historyService = new HistoryService();
    private UserService userService = new UserService();
    //public static Integer currentUserID;
    LinearLayoutManager layoutManager;

    RecyclerView searchResultsRecyclerView;
    TextView text;

    private FragmentListener listener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }
    private void sendSignalToMainActivity(List<TracksModel> tracksModels, int trackID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived2(tracksModels, trackID, from, belong, mode);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.lastFrag=this;
        View rootView = inflater.inflate(R.layout.layout_search, container, false);
        searchBox = rootView.findViewById(R.id.search_bar_edit_text);
        text = rootView.findViewById(R.id.BrowseAllText);
        if (searchResultsRecyclerView == null) {
            layoutManager = new LinearLayoutManager(getContext());
            searchResultsRecyclerView = rootView.findViewById(R.id.recyclerview_recent_search);
            searchResultsRecyclerView.setLayoutManager(layoutManager);
        }
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable); // Remove pending searchRunnable if any
                // check if the search box is empty
                if (searchBox.getText().toString().isEmpty()) {
                    getCurrentUserID();
                    return;
                }
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
        getCurrentUserID();
        return rootView;
    }



    public void search(String query) throws InterruptedException {
        trackService.findTrackByName(query, new TrackService.OnTracksLoadedListener() {
            @Override
            public void onTracksLoaded(List<TracksModel> tracks) {
                //Log.e("SearchActivity", "Tracks: " + tracks.size());
                text.setVisibility(View.GONE);
                TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(tracks,getContext(),listener, FormatSpace(searchBox.getText().toString())+" From Search");
                searchResultsRecyclerView.setAdapter(trackPreviewAdapter);
                searchResultsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                        super.onScrollStateChanged(recyclerView, newState);
//                        // Check if the user has stopped scrolling
//                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                            int totalItemCount = layoutManager.getItemCount();
//                            int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//                            if (lastVisibleItem + 1 == totalItemCount) {
//                                // Load more data
//                                trackPreviewAdapter.loadMore();
//                            }
//                        }
//                    }
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int totalItemCount = layoutManager.getItemCount();
                        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                        // Load more data when the user reaches near the end of the list
                        int threshold = 5; // Load more when 5 items are left
                        if (lastVisibleItem >= totalItemCount - 1 - threshold) {
                            // Load more data
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Load more data
                                    trackPreviewAdapter.loadMore();
                                }
                            });
                        }
                    }
                });
                trackPreviewAdapter.notifyDataSetChanged();
            }
        });
    }

    public void getHistory(Integer userID) {
        // Get the search history from the database
        // Display the search history in the search box
        text.setVisibility(View.VISIBLE);
        //Log.e("SearchingActivity", "current user: " + currentUser.getDisplayName());
        List<HistoryModel> historyModels = new ArrayList<>();
        historyService.getHistory(userID).thenAccept(userSongs1 -> {
            Log.e("SearchingActivity", "History size: " + userSongs1.size());
            historyModels.addAll(userSongs1);
        });

        trackService.findTrackByID(historyModels, new TrackService.OnTracksLoadedListener() {
            @Override
            public void onTracksLoaded(List<TracksModel> tracks) {
                Log.e("SearchActivity", "Tracks: " + tracks.size());
                TrackPreviewAdapter trackPreviewAdapter = new TrackPreviewAdapter(tracks,getContext(),listener, "Recent Search");
                searchResultsRecyclerView.setAdapter(trackPreviewAdapter);
                trackPreviewAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getCurrentUserID(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UsersModel userModel = snapshot.getValue(UsersModel.class);
                        if (userModel != null) {
                            Integer currentUserID = userModel.getUserID();
                            getHistory(currentUserID);
                        }
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });
        } else {

        }
    }

    private String FormatSpace(String str) {
        return str.replace(" ", "+");
    }
}
