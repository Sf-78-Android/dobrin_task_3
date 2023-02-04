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
import com.training.weatherapp.constatns.LocationPermission
import com.training.weatherapp.model.LocationModel


class LocationLiveData(context : Context) : LiveData<LocationModel>() {
    private var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val mContext = context

    override fun onActive() {
        super.onActive()
            if (LocationPermission.isLocationPermissionGranted(mContext)) {
                try {
                    mFusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            location?.also {
                                setLocationData(it)
                            }
                        }
                    startLocationUpdates()
                }catch (_:SecurityException){

                }
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
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

}