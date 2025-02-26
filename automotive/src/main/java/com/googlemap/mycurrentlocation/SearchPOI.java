package com.googlemap.mycurrentlocation;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
public class SearchPOI extends AppCompatActivity {
    //private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private EditText keywordEditText;
    private String TAG ="SearchPOI";
    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;
    private List<PlaceDetails> placesList; // List to hold Place objects
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_poi);

        // Initialize the RecyclerView and set the LayoutManager
        recyclerView = findViewById(R.id.resultsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        placesList = new ArrayList<>();
        placeAdapter = new PlaceAdapter(placesList);
        recyclerView.setAdapter(placeAdapter);

        // Get latitude and longitude from Intent
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        // Set up the keyword input field
        keywordEditText = findViewById(R.id.keywordEditText);

        // Search for POIs when the user clicks the button (e.g., "Search" button)
        findViewById(R.id.searchButton).setOnClickListener(v -> {
            String keyword = keywordEditText.getText().toString();
            if (!keyword.isEmpty()) {
                Log.d(TAG, "clicked btn to search : " + keyword);
                searchByPlaceAPI(keyword, latitude, longitude);
            } else {
                Toast.makeText(SearchPOI.this, "Please enter a keyword", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchByPlaceAPI(String keyword, double latitude, double longitude) {
        String apiKey = "AIzaSyDcGVkknsvFJlJieiftAlEVKQnwjj-vaDM";
        int radius = 5000;  // 500 m radius

        String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latitude + "," + longitude +
                "&radius=" + radius +
                "&keyword=" + keyword +
                "&key=" + apiKey;

        // Create an instance of DownloadUrl and start the AsyncTask
        DownloadUrl downloadUrl = new DownloadUrl(this);
        downloadUrl.fetchNearbyPlaces(urlString);
    }



    // Method to be called once data is fetched
    public void updateRecyclerView(List<PlaceDetails> places) {
        placeAdapter.updatePlacesList(places); // Update the adapter with the new list of places
    }

}
