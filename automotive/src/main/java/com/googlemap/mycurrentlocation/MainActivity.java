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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView lattitude, longitude, address, city, country, updating_long_lat;
    Button getLocation;
    private final static int REQUEST_CODE = 100;
    String TAG = "GPS MainActivity";
    LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        lattitude = findViewById(R.id.lattitude);
        longitude = findViewById(R.id.longitude);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        getLocation = findViewById(R.id.getLocation);
        updating_long_lat = findViewById(R.id.updating_long_lat);

        Button secondActBtn = findViewById(R.id.secondActBtn);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation.setOnClickListener(v -> getLastLocation());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            askPermission();
        }

        int time_ms = 100; //0.5 second
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time_ms, 0, new LocationListener() { //GPS_PROVIDER
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //Log.d(TAG, "every " + time_ms + " ms, onLocationChanged will be called, and location will be updated");
                updating_long_lat.setText("continue updating Lattitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
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


        secondActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGoogleMapPOI();
            }
        });


    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            try {
                                location.getAccuracy(); //5.0 default
                                Log.d(TAG, "my lat: " + location.getLatitude() + "  accuracy  : " + location.getAccuracy()); //accuracy  : 3.315
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                lattitude.setText("Last known Lattitude: " + addresses.get(0).getLatitude());
                                longitude.setText("Last known Longitude: " + addresses.get(0).getLongitude());

                                address.setText("Address: " + addresses.get(0).getAddressLine(0));
                                city.setText("City: " + addresses.get(0).getLocality());
                                country.setText("Country: " + addresses.get(0).getCountryName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(MainActivity.this, "Please provide the required permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void launchGoogleMapPOI() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Get latitude and longitude
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // Create an Intent to send the data to SecondActivity
                    Intent intent = new Intent(MainActivity.this, SearchPOI.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    // Start SecondActivity
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
