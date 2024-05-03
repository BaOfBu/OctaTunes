package com.example.octatunes.Services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SpotifyPlaylistFetcher {

    private static final String TAG = SpotifyPlaylistFetcher.class.getSimpleName();

    private static final String SPOTIFY_API_URL = "https://api.spotify.com/v1/recommendations/playlists";
    private static final String CLIENT_ID = "51d4957376b143faacc1a324273d00a5";
    private static final String CLIENT_SECRET = "9ff1b61a5e574dd6a0164dc35d870b66";

    // Method to fetch recommended playlists
    public void fetchSpotifyRecommendedPlaylists(List<String> sectionTitles) {
        new FetchRecommendedPlaylistsTask().execute(sectionTitles);
    }

    private class FetchRecommendedPlaylistsTask extends AsyncTask<List<String>, Void, String> {

        @Override
        protected String doInBackground(List<String>... lists) {
            List<String> sectionTitles = lists[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String recommendationsJsonStr = null;

            try {
                // Obtain access token using Client Credentials Flow
                String accessToken = getAccessToken();
                if (accessToken == null) {
                    // Failed to obtain access token, return null
                    return null;
                }

                // Construct the URL for the Spotify API request
                StringBuilder urlBuilder = new StringBuilder(SPOTIFY_API_URL);
                urlBuilder.append("?limit=10");

                // Append section titles as seed genres
                for (String sectionTitle : sectionTitles) {
                    urlBuilder.append("&seed_genres=").append(sectionTitle);
                }

                URL url = new URL(urlBuilder.toString());

                // Open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty
                    return null;
                }
                recommendationsJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the data, there's no point in attempting to parse it
                return null;
            } finally {
                // Close the HttpURLConnection and BufferedReader
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return recommendationsJsonStr;
        }

        @Override
        protected void onPostExecute(String recommendationsJsonStr) {
            if (recommendationsJsonStr != null) {
                try {
                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(recommendationsJsonStr);
                    JSONArray playlistsArray = jsonObject.getJSONArray("playlists");

                    // Process recommended playlists
                    for (int i = 0; i < playlistsArray.length(); i++) {
                        JSONObject playlistObject = playlistsArray.getJSONObject(i);
                        String playlistName = playlistObject.getString("name");
                        Log.d(TAG, "Recommended Playlist: " + playlistName);
                        // Handle other playlist details as needed
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON", e);
                }
            } else {
                Log.e(TAG, "Empty or null JSON response");
            }
        }
    }

    // Method to obtain access token using Client Credentials Flow
    private String getAccessToken() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            // Construct request body
            String requestBody = "grant_type=client_credentials";

            // Encode client ID and client secret using Base64
            String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
            String encodedCredentials = android.util.Base64.encodeToString(credentials.getBytes(), android.util.Base64.NO_WRAP);

            // Send POST request to Spotify Accounts service
            URL url = new URL("https://accounts.spotify.com/api/token");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setDoOutput(true);

            // Write request body
            urlConnection.getOutputStream().write(requestBody.getBytes());

            // Read response
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            // Extract access token from JSON response
            JSONObject responseJson = new JSONObject(buffer.toString());
            return responseJson.getString("access_token");
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error obtaining access token", e);
            return null;
        } finally {
            // Close the HttpURLConnection and BufferedReader
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
    }
}
