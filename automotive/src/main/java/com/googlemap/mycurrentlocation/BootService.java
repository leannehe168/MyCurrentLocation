package com.googlemap.mycurrentlocation;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class BootService extends Service {
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback; // for running at background
    private static final String CHANNEL_ID = "MyServiceChannel";
    Handler handler;
    private String TAG = "BootService TAG";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Boot Service Created");
        createNotificationChannel();
        startForeground(1, createNotification());
        handler = new Handler();
        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void createNotificationChannel(){
        // Create a notification channel if running on Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Boot Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }

    private Notification createNotification() {
        Log.d(TAG, "createNotification() called");
        return new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Notification")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Boot Service is running in the background.");
        Log.d(TAG, "Location Service Started");
        keepReadingMyLocation();
        // If the service gets killed, Android will try to recreate it
        return START_STICKY; // or START_NOT_STICKY, START_REDELIVER_INTENT
    }

    public void keepReadingMyLocation(){
        Log.d(TAG, "Start read my location at background!");
        LocationRequest locationRequest = new LocationRequest.Builder(2000) // Set the interval to 1000ms (1 second)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdates(50) // Optional, limits the number of location updates
                .build();

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
                        System.out.println("read my current Location at background: " + cur_latitude + ", " + cur_longitude);
                    });

                } else {
                    Log.d(TAG, "location is not available at this moment");
                }
            }
        };

        // Ensure permission is granted before requesting location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ; // Don't continue if permission is missing
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService", "Service Destroyed");
        // Stop location updates when the service is destroyed
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // This service doesn't support binding
    }
}

