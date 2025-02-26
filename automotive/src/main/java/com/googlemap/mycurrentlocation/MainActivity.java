package com.googlemap.mycurrentlocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback; // for running at background
    TextView lattitude, longitude, address, city, country, updating_long_lat;
    Button getLocation, startBackgroundServieBtn, stopBtn;
    private final static int REQUEST_CODE = 100;
    String TAG = "GPS MainActivity";
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            askPermission();
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        setContentView(R.layout.activity_main);
        lattitude = findViewById(R.id.lattitude);
        longitude = findViewById(R.id.longitude);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        getLocation = findViewById(R.id.getLocation);
        startBackgroundServieBtn = findViewById(R.id.startBackgroundService);
        stopBtn = findViewById(R.id.stopService);
        updating_long_lat = findViewById(R.id.updating_long_lat);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation.setOnClickListener(v -> getLastLocation());



        int time_ms =1000; //1 second
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time_ms, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(TAG, "every " + time_ms + " ms, onLocationChanged will be called, and location will be updated");
                updating_long_lat.setText("continue updating Lattitude: "+ location.getLatitude() + " Longitude: " + location.getLongitude());

            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
            }
        });

        startBackgroundServieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationService();
                finish();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationService();
            }
        });

        /*
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // 1 seconds
        locationRequest.setFastestInterval(1000); // 1 s
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Log.d(TAG, "onLocationResult");
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {

                    locationResult.getLocations().forEach(location -> {
                        // Handle the updated location
                        double cur_latitude = location.getLatitude();
                        double cur_longitude = location.getLongitude();
                        // You can log the location or update your UI
                        System.out.println("read my current Location (background): " + cur_latitude + ", " + cur_longitude);
                    });

                }
                else{
                    Log.d(TAG, "location is not available at this moment");
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
         */

    }

    private void getLastLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null){
                            try {
                                location.getAccuracy(); //5.0 default
                                Log.d(TAG, "my lat: " + location.getLatitude() + "  accuracy  : " + location.getAccuracy());
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                lattitude.setText("Last known Lattitude: " + addresses.get(0).getLatitude());
                                longitude.setText("Last known Longitude: " + addresses.get(0).getLongitude());

                                address.setText("Address: " + addresses.get(0).getAddressLine(0));
                                city.setText("City: " + addresses.get(0).getLocality());
                                country.setText("Country: " +addresses.get(0).getCountryName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_BACKGROUND_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();

            }else {
                Toast.makeText(MainActivity.this,"Please provide the required permission",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        startService(serviceIntent);
        finish();
    }


    private void stopLocationService() {
        Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Release (e.g., unregister sensors or listeners)
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
