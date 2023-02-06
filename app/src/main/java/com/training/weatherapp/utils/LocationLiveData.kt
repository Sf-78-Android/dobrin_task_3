package com.training.weatherapp.utils

import android.Manifest.*
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.*
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.training.weatherapp.constatns.Constants.INTERVAL_MILLIS
import com.training.weatherapp.constatns.Constants.MIN_UPDATE_DISTANCE
import com.training.weatherapp.models.LocationModel


class LocationLiveData(context: Context) : LiveData<LocationModel>() {
    private var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val mContext = context
    private val mPermissionsManager: PermissionsManager = PermissionsManager(mContext)


    override fun onActive() {
        super.onActive()
        while (!mPermissionsManager.isLocationPermissionGranted()) {
            mPermissionsManager.requestLocationPermission()
        }
        try {
            mFusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.also {
                        setLocationData(it)
                    }
                }

            startLocationUpdates()
        } catch (_: SecurityException) {

        }
    }

    override fun onInactive() {
        super.onInactive()
        mFusedLocationClient.removeLocationUpdates(locationCallback)

    }

    private fun setLocationData(location: Location) {
        value = LocationModel(
            longitude = location.longitude,
            latitude = location.latitude
        )

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )

    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest
            .Builder(INTERVAL_MILLIS)
            .setPriority(PRIORITY_HIGH_ACCURACY)
            .setMinUpdateDistanceMeters(MIN_UPDATE_DISTANCE)
            .build()

    }

}