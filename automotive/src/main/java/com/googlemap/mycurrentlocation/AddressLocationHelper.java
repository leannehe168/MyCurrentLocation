package com.googlemap.mycurrentlocation;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressLocationHelper {
    private static String TAG= "AddressLocationHelper";
    //To get the address from coordinates
    public static void getAddressFromCoordinates(Context context, double latitude, double longitude) {
        // Check if Geocoder is available
        if (Geocoder.isPresent()) {
            new GetAddressTask(context, latitude, longitude).execute();
        } else {
            Log.e("Geocoder", "Geocoder is not available on this device.");
        }
    }

    private static class GetAddressTask extends AsyncTask<Void, Void, String> {
        private double latitude;
        private double longitude;
        private Context context;

        GetAddressTask(Context context, double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            try {
                // Try to get the addresses for the latitude and longitude
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Get 1 address
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: Unable to get address";
            }

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressString = new StringBuilder();

                // Get address components
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressString.append(address.getAddressLine(i)).append("\n");
                }

                // You can also fetch individual details
                String city = address.getLocality();
                String country = address.getCountryName();
                return "Address: " + addressString.toString() + "\nCity: " + city + "\nCountry: " + country;
            } else {
                return "Address not found.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Show the result as a Toast or use it in the UI
            //Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            Log.d(TAG, "My Results: "+ result);

            //// Inside MainActivity or wherever you want to call the method
            //LocationHelper.getAddressFromCoordinates(MainActivity.this, latitude, longitude);
        }
    }

}

