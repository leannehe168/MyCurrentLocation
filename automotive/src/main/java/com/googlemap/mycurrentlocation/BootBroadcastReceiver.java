package com.googlemap.mycurrentlocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "BootBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Boot completed. Starting service.");

            // Ensure the service is running in the foreground to avoid restrictions on background services
            Intent serviceIntent = new Intent(context, BootService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent); // Use startForegroundService on Android O and above
            } else {
                context.startService(serviceIntent); // For older versions, use startService
            }
        }
    }
}
