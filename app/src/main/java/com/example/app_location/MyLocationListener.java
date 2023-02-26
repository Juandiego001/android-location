package com.example.app_location;

import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

import androidx.annotation.NonNull;

public class MyLocationListener implements LocationListener {
    private MainActivity mainActivity;
    private double latitude = 0;
    private double longitude = 0;

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Este método se ejecuta cada vez que se cambia de localización.
        /*
        double theLatitude = location.getLatitude();
        double theLongitude = location.getLongitude();

        Log.d("MyLocationListener, latitude", Double.toString(theLatitude));
        Log.d("MyLocationListener, longitude", Double.toString(theLongitude));

        setLatitude(theLatitude);
        setLongitude(theLongitude);
        */
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
