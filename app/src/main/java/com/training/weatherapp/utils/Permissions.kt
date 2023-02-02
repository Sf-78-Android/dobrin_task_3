package com.training.weatherapp.utils

import android.Manifest
import android.Manifest.*
import android.Manifest.permission.*
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permissions {

    fun requestLocation(activity: Activity, code: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION), code
        );
    }


    fun checkFineLocation(activity: Activity): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION);
        val coarseLocation = ContextCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION);
        return fineLocation == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED;
    }

}