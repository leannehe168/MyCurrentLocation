package com.googlemap.mycurrentlocation;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MyBackgroundService extends Service {
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback; // for running at background
    private static final String CHANNEL_ID = "MyServiceChannel";
    private String TAG = "MyBackgroundService TAG";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Created");

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a notification channel if running on Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Background Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Create a persistent notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service is Running")
                .setContentText("Your background task is active.")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your own icon
                .build();

        startForeground(1, notification); // 1 is the notification ID
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Location Service Started");

        // Do background location read here:
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

                } else {
                    Log.d(TAG, "location is not available at this moment");
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //return 0;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

        // If the service gets killed, Android will try to recreate it
        return START_STICKY; // or START_NOT_STICKY, START_REDELIVER_INTENT
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService", "Service Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // This service doesn't support binding
    }
}

