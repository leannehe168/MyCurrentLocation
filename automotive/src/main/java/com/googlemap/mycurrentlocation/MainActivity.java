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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    TextView lattitude, longitude, address, city, country, updating_long_lat;
    Button getLocation, startBackgroundServieBtn, stopBtn, startBootServiceBtn;
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE);
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
        startBootServiceBtn = findViewById(R.id.startBootService);
        updating_long_lat = findViewById(R.id.updating_long_lat);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation.setOnClickListener(v -> getLastLocation());



        int time_ms =1000; //1 second
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time_ms, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //Log.d(TAG, "every " + time_ms + " ms, onLocationChanged will be called, and location will be updated");
                updating_long_lat.setText("continue updating Lattitude: "+ location.getLatitude() + " Longitude: " + location.getLongitude());
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
            }
        });

        startBackgroundServieBtn.setOnClickListener(v -> {
            startLocationService();
        });

        stopBtn.setOnClickListener(v -> stopLocationService());
        startBootServiceBtn.setOnClickListener(v -> {
            startBootService();
            // Minimize the app and send it to the background
            //moveTaskToBack(true);
            //or
            //finish();
        });

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
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_CODE);
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
    }

    private void startBootService(){
        Log.d(TAG, "startBootService()!!");
        Intent serviceIntent = new Intent(MainActivity.this, BootService.class);
        startService(serviceIntent);
        //startForegroundService(serviceIntent);
        //moveTaskToBack(true);
        finish();
    }

    private void stopLocationService() {
        Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        stopService(serviceIntent);

        Intent bootServiceIntent = new Intent(this, BootService.class);
        stopService(bootServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Update the intent so it's accessible in onResume or other methods
    }

}
