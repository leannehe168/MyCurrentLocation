package com.googlemap.mycurrentlocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private String TAG ="BootBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())) {
            // Start your service when boot is completed
            Log.d(TAG, "Boot completed. Starting service.");
            Intent serviceIntent = new Intent(context, BootService.class);
            context.startService(serviceIntent);
        }
    }
}
