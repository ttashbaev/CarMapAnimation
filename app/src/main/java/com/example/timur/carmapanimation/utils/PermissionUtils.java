package com.example.timur.carmapanimation.utils;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.example.timur.carmapanimation.config.AppConstants;

/**
 * Created by Timur on 05.05.2018.
 */

public final class PermissionUtils {


    public static boolean isLocationServicesEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean isLocationPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(
                context,
                permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkLocationPermission(Activity activity) {
        String[] permissions = new String[]{
                permission.ACCESS_FINE_LOCATION,
                permission.ACCESS_COARSE_LOCATION
        };

        if (isLocationPermissionGranted(activity)) return true;

        ActivityCompat.requestPermissions(activity, permissions, AppConstants.REQUEST_CODE_LOCATION_PERMISSION);
        return false;
    }
}
