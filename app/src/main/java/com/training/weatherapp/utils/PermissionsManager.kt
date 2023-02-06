package com.training.weatherapp.utils

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.training.weatherapp.constatns.Constants.LOCATION_CODE

class PermissionsManager(context: Context) {
    private val mContext = context
     fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(mContext,
            ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
            ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

     fun requestLocationPermission() {
         if (!isLocationPermissionGranted()) {
             ActivityCompat.requestPermissions(
                 mContext as Activity, arrayOf(
                     ACCESS_COARSE_LOCATION,
                     ACCESS_FINE_LOCATION
                 ), LOCATION_CODE
             )
         }
     }

}