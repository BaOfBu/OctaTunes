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
import com.example.octatunes.Model.TracksModel;
import com.example.octatunes.R;
import com.example.octatunes.Services.ArtistService;
import com.example.octatunes.Services.FollowerService;
import com.example.octatunes.Services.PlaylistService;
import com.example.octatunes.Services.SongService;
import com.example.octatunes.Services.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArtistDetailFragment extends Fragment {

    private ArtistsModel artistModel;

    private UserService userService = new UserService();

    private FollowerService followerService = new FollowerService();
    private FragmentListener listener;
    SongAdapter songAdapter;

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
                if (artistModel.getImage()!=null && artistModel.getImage()!=""){
                    Picasso.get().load(artistModel.getImage()).into(imageView);
                }

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
                            // Set up RecyclerView for popular songs
                            setupRecyclerViewPopularSong(rootView, artistModel.getArtistID(),userId);
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



            // Set up click event for shuffle icon
            setupShuffleIcon(rootView);

            // Set up number count listen of artist
            setupNumberCountArtist(rootView,artistModel.getName());

        }

        return rootView;
    }

    private void setupNumberCountArtist(View rootView, String name) {
        final TextView count_number_artist_listen = rootView.findViewById(R.id.count_number_artist_listen);
        final SongService songService = new SongService();
        songService.countSongWithArtistName(name, new SongService.OnSongCountListener() {
            @Override
            public void onSongCountRetrieved(int count) {
                // Update the TextView with the count of listeners
                count_number_artist_listen.setText(("Lượt nghe" + " " + String.valueOf(count)));
            }

            @Override
            public void onSongCountFailed(String errorMessage) {
            }
        });
    }

    private void setupPlayIcon(View rootView,TracksModel tracksModels,SongAdapter songAdapter) {
        ImageView playIcon = rootView.findViewById(R.id.play_button_artist_display);
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songAdapter.extracted(tracksModels);
            }
        });
    }

    private void setupShuffleIcon(View rootView) {
        ImageView shuffleIcon = rootView.findViewById(R.id.artist_shuffle_icon);
        shuffleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupRecyclerViewPopularSong(View rootView, int artistId,int userId) {
        ArtistService artistService = new ArtistService();
        artistService.getRandomTracksByArtistId(artistId)
                .thenAccept(tracks -> {
                    RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSongPopular);
                    if (!tracks.isEmpty()) {
                        PlaylistService playlistService = new PlaylistService();
                        playlistService.getPlaylistModelLiked(userId, new PlaylistService.PlaylistCallback() {
                            @Override
                            public void onPlaylistRetrieved(PlaylistsModel playlistModel) {

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(layoutManager);
                                if (playlistModel!=null){
                                    SongAdapter adapter = new SongAdapter(getActivity(), tracks, listener,userId,playlistModel.getPlaylistID());
                                    setupPlayIcon(rootView,tracks.get(0),adapter);
                                    recyclerView.setAdapter(adapter);
                                }
                                else{
                                    SongAdapter adapter = new SongAdapter(getActivity(), tracks, listener,userId,-1);
                                    setupPlayIcon(rootView,tracks.get(0),adapter);
                                    recyclerView.setAdapter(adapter);
                                }


                                setupRecyclerViewPopularRelease(rootView, artistId);
                                setupRecyclerViewFeaturing(rootView, artistId);

                            }
                            @Override
                            public void onError(String errorMessage) {
                                // Handle error if any
                            }
                        });

                    } else {
                        TextView popularSongTitle = rootView.findViewById(R.id.popular_song_title);
                        popularSongTitle.setVisibility(View.GONE);
                        TextView featuringTitle = rootView.findViewById(R.id.featuring_titlte);
                        featuringTitle.setVisibility(View.GONE);
                        TextView popularReleaseTitle = rootView.findViewById(R.id.popular_release_title);
                        popularReleaseTitle.setVisibility(View.GONE);
                        TextView noMusicTextView = rootView.findViewById(R.id.no_music_text);
                        noMusicTextView.setVisibility(View.VISIBLE);
                    }
                })
                .exceptionally(e -> {
                    Log.e("ArtistDetailFragment", "Error fetching tracks: " + e.getMessage());
                    return null;
                });
    }



    private void setupRecyclerViewPopularRelease(View rootView, int artistId) {
        ArtistService artistService = new ArtistService();
        artistService.getRandomAlbumByArtistId(artistId, new OnSuccessListener<List<AlbumsModel>>() {
            @Override
            public void onSuccess(List<AlbumsModel> albumList) {
                RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewSongPopularRelease);
                if (!albumList.isEmpty()&&albumList!=null) {
                    PopularReleaseAlbumArtistAdapter adapter = new PopularReleaseAlbumArtistAdapter(getContext(), albumList,listener);
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
