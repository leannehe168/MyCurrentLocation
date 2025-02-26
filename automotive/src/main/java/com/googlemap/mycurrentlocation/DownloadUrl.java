package com.googlemap.mycurrentlocation;

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
import java.util.ArrayList;
import java.util.List;

public class DownloadUrl {
    private static final String TAG = "DownloadUrl";
    private SearchPOI placesActivity;

    // Constructor to pass the reference of PlacesActivity
    public DownloadUrl(SearchPOI placesActivity) {
        this.placesActivity = placesActivity;
    }


    // AsyncTask to perform network operation in the background
    public static class FetchPlacesTask extends AsyncTask<String, Void, String> {
        private SearchPOI placesActivity;
        public FetchPlacesTask(SearchPOI placesActivity) {
            this.placesActivity = placesActivity;
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // The URL to fetch
            String jsonData = "";
            try {
                // Perform the network request
                jsonData = retrieveUrl(urlString);
            } catch (IOException e) {
                Log.e(TAG, "Error retrieving data: " + e.toString());
            }
            return jsonData; // Return the JSON data
        }

        @Override
        protected void onPostExecute(String jsonData) {
            super.onPostExecute(jsonData);

            // If the data is not null, parse the JSON response
            if (jsonData != null && !jsonData.isEmpty()) {
                //parseNearbyPlaces(jsonData);

                List<PlaceDetails> places = parseNearbyPlaces(jsonData);
                placesActivity.updateRecyclerView(places); // Update RecyclerView with the new places list
            }
        }

        // Method to retrieve the data from the Google Places API
        private String retrieveUrl(String urlString) throws IOException {
            String urlData = "";
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(urlString);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer sb = new StringBuffer();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                urlData = sb.toString();
                bufferedReader.close();

            } catch (Exception e) {
                Log.d(TAG, "Error retrieving data: " + e.toString());
            } finally {
                if (inputStream != null) inputStream.close();
                if (httpURLConnection != null) httpURLConnection.disconnect();
            }

            System.out.println("my returned urlData: "+ urlData);

            return urlData;
        }

        // Method to extract store names and addresses from the Places API response
        private List<PlaceDetails> parseNearbyPlaces(String jsonData) {
            List<PlaceDetails> places = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray results = jsonObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject place = results.getJSONObject(i);
                    String name = place.getString("name");
                    String address = place.optString("vicinity", "Address not available");


                    Log.d(TAG, "Place Name: " + name);
                    Log.d(TAG, "Address: " + address);
                    //Log.d(TAG, "Open Status: " + status);

                    places.add(new PlaceDetails(name, address)); // Add place to the list
                }
            } catch (JSONException e) {
                Log.d(TAG, "Error parsing JSON: " + e.toString());
            }

            return places;
        }
    }

    // Method to start the AsyncTask for nearby places
    public void fetchNearbyPlaces(String urlString) {
        new FetchPlacesTask(placesActivity).execute(urlString);
    }
}
