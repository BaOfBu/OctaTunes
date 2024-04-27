package com.example.octatunes.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.octatunes.Adapter.PlaylistSmallAdapter;
import com.example.octatunes.Adapter.PopularReleaseAlbumArtistAdapter;
import com.example.octatunes.Adapter.SongAdapter;
import com.example.octatunes.FragmentListener;
import com.example.octatunes.MainActivity;
import com.example.octatunes.Model.AlbumsModel;
import com.example.octatunes.Model.ArtistsModel;
import com.example.octatunes.Model.PlaylistsModel;
import com.example.octatunes.Model.SongModel;
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.FollowerService;
import com.example.octatunes.Services.PlaylistLibraryUserService;
import com.example.octatunes.Services.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistDetailFragment extends Fragment {

    private ArtistsModel artistModel;

    private UserService userService = new UserService();

    private FollowerService followerService = new FollowerService();
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
    private void sendSignalToMainActivity(List<TracksModel> tracksModels, int trackID, int albumID, String from, String belong, String mode) {
        if (listener != null) {
            listener.onSignalReceived(tracksModels, trackID, -1, from, belong, mode);
        }
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_artist_detail, container, false);

        MainActivity.lastFrag=this;

        // Retrieve the arguments passed to the fragment
        Bundle args = getArguments();
        if (args != null) {
            // Get the ArtistsModel object from the arguments
            artistModel = (ArtistsModel) args.getSerializable("artist");

            // Artist Name
            if (artistModel != null) {
                TextView textView = rootView.findViewById(R.id.artist_detail_name);
                textView.setText(artistModel.getName());
            }

            // Artist Image
            if (artistModel != null) {
                ImageView imageView = rootView.findViewById(R.id.artist_detail_image);
                Picasso.get().load(artistModel.getImage()).into(imageView);
            }

            /* Check following */
            TextView following = rootView.findViewById(R.id.artist_follow_btn);
            userService.getCurrentUserId(new UserService.UserIdCallback() {
                @Override
                public void onUserIdRetrieved(int userId) {
                    followerService.checkIfUserFollowsArtist(userId, artistModel.getArtistID(), new FollowerService.OnFollowCheckListener() {
                        @Override
                        public void onFollowCheck(boolean isFollowing) {
                            if (isFollowing) {
                                following.setText("Following");

                            } else {
                                following.setText("Follow");

                            }
                        }
                    });
                }
            });

            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userService.getCurrentUserId(new UserService.UserIdCallback() {
                        @Override
                        public void onUserIdRetrieved(int userId) {
                            followerService.checkIfUserFollowsArtist(userId, artistModel.getArtistID(), new FollowerService.OnFollowCheckListener() {
                                @Override
                                public void onFollowCheck(boolean isFollowing) {
                                    if (isFollowing) {
                                        followerService.unfollowArtist(userId, artistModel.getArtistID(), new FollowerService.OnUnfollowListener() {
                                            @Override
                                            public void onUnfollowSuccess() {
                                                Toast.makeText(getContext(), "You are not following this artist", Toast.LENGTH_SHORT).show();
                                                following.setText("Follow");
                                            }

                                            @Override
                                            public void onUnfollowFailure(String errorMessage) {
                                            }
                                        });
                                    } else {
                                        followerService.addFollowedArtist(userId,artistModel.getArtistID());
                                        Toast.makeText(getContext(), "You are following this artist", Toast.LENGTH_SHORT).show();
                                        following.setText("Following");
                                    }
                                }
                            });
                        }
                    });
                }
            });


            TextView featuring_titlte=rootView.findViewById(R.id.featuring_titlte);
            featuring_titlte.setText("Featuring "+artistModel.getName());

            ImageView backIcon = rootView.findViewById(R.id.back_icon);
            backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

            // Set up RecyclerView for popular songs
            setupRecyclerViewPopularSong(rootView, artistModel.getArtistID());

            //setupRecyclerViewPopularRelease(rootView, artistModel.getArtistID());
            //
            //setupRecyclerViewFeaturing(rootView, artistModel.getArtistID());
        }

        return rootView;
    }

    private void setupRecyclerViewPopularSong(View rootView, int artistId) {
        ArtistService artistService = new ArtistService();
        artistService.getRandomTracksByArtistId(artistId, new OnSuccessListener<List<TracksModel>>() {
            @Override
            public void onSuccess(List<TracksModel> tracks) {
                RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSongPopular);
                if (!tracks.isEmpty()) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    SongAdapter adapter = new SongAdapter(getActivity(), tracks,listener);
                    recyclerView.setAdapter(adapter);
                    setupRecyclerViewPopularRelease(rootView, artistModel.getArtistID());
                    setupRecyclerViewFeaturing(rootView, artistModel.getArtistID());
                } else {
                    TextView popular_song_title=rootView.findViewById(R.id.popular_song_title);
                    popular_song_title.setVisibility(View.GONE);
                    TextView featuring_title=rootView.findViewById(R.id.featuring_titlte);
                    featuring_title.setVisibility(View.GONE);
                    TextView popular_release_title=rootView.findViewById(R.id.popular_release_title);
                    popular_release_title.setVisibility(View.GONE);
                    TextView noMusicTextView = rootView.findViewById(R.id.no_music_text);
                    TextView fan_also_like=rootView.findViewById(R.id.fan_also_like);
                    fan_also_like.setVisibility(View.GONE);
                    noMusicTextView.setVisibility(View.VISIBLE);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ArtistDetailFragment", "Error fetching tracks: " + e.getMessage());
            }
        });
    }


    private void setupRecyclerViewPopularRelease(View rootView, int artistId) {
        ArtistService artistService = new ArtistService();
        artistService.getRandomAlbumByArtistId(artistId, new OnSuccessListener<List<AlbumsModel>>() {
            @Override
            public void onSuccess(List<AlbumsModel> albumList) {
                RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSongPopularRelease);
                if (!albumList.isEmpty()&&albumList!=null) {
                    PopularReleaseAlbumArtistAdapter adapter = new PopularReleaseAlbumArtistAdapter(getContext(), albumList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(adapter);
                } else {
                    TextView popular_song_title=rootView.findViewById(R.id.popular_song_title);
                    popular_song_title.setVisibility(View.GONE);
                    TextView featuring_title=rootView.findViewById(R.id.featuring_titlte);
                    featuring_title.setVisibility(View.GONE);
                    TextView popular_release_title=rootView.findViewById(R.id.popular_release_title);
                    popular_release_title.setVisibility(View.GONE);
                    TextView noMusicTextView = rootView.findViewById(R.id.no_music_text);
                    TextView fan_also_like=rootView.findViewById(R.id.fan_also_like);
                    fan_also_like.setVisibility(View.GONE);
                    noMusicTextView.setVisibility(View.VISIBLE);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure, such as displaying an error message
                Log.e("ArtistDetailFragment", "Error fetching albums: " + e.getMessage());
            }
        });
    }
    private void setupRecyclerViewFeaturing(View rootView, int artistId) {
        ArtistService artistService = new ArtistService();
        artistService.getPlaylistsByArtistId(artistId, new OnSuccessListener<List<PlaylistsModel>>() {
            @Override
            public void onSuccess(List<PlaylistsModel> playlists) {
                if (!playlists.isEmpty()&&playlists!=null) {
                    RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSongFeaturing);
                    PlaylistSmallAdapter adapter = new PlaylistSmallAdapter(getContext(), playlists);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    recyclerView.setAdapter(adapter);
                }
                else{
                    TextView popular_song_title=rootView.findViewById(R.id.popular_song_title);
                    popular_song_title.setVisibility(View.GONE);
                    TextView featuring_title=rootView.findViewById(R.id.featuring_titlte);
                    featuring_title.setVisibility(View.GONE);
                    TextView popular_release_title=rootView.findViewById(R.id.popular_release_title);
                    popular_release_title.setVisibility(View.GONE);
                    TextView noMusicTextView = rootView.findViewById(R.id.no_music_text);
                    TextView fan_also_like=rootView.findViewById(R.id.fan_also_like);
                    fan_also_like.setVisibility(View.GONE);
                    noMusicTextView.setVisibility(View.VISIBLE);
                }

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure, such as displaying an error message
            }
        });
    }

    private void showNoMusicMessage(View rootView) {
        TextView popular_song_title=rootView.findViewById(R.id.popular_song_title);
        popular_song_title.setVisibility(View.GONE);
        TextView featuring_title=rootView.findViewById(R.id.featuring_titlte);
        featuring_title.setVisibility(View.GONE);
        TextView popular_release_title=rootView.findViewById(R.id.popular_release_title);
        popular_release_title.setVisibility(View.GONE);
        TextView noMusicTextView = rootView.findViewById(R.id.no_music_text);
        noMusicTextView.setVisibility(View.VISIBLE);
    }

    private void hideNoMusicMessage(View rootView) {
        TextView noMusicTextView = rootView.findViewById(R.id.no_music_text);
        noMusicTextView.setVisibility(View.GONE);
    }


}
