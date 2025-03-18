package com.googlemap.mycurrentlocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstallPackageReceiver extends BroadcastReceiver {
    private String TAG = "InstallPackageReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "InstallPackageReceiver Action: " + action);

        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.d(TAG, "Package name: " + packageName);

            if (packageName.equals(context.getPackageName())) {
                Log.d(TAG, "My app is just installed!");
            }
        } else {
            Log.d(TAG, "Received unexpected action: " + action);
        }
    }
}
