package com.training.weatherapp.utils

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class PrerequisitesChecker(context: Context) {
    private val mContext: Context = context
    private var mLocationManager: LocationManager
    private var mPermissionsManager: PermissionsManager
    private val mConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        mLocationManager =
            context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        mPermissionsManager = PermissionsManager(mContext)

    }

    private fun requestPermissionsIfNotGranted() {
        mPermissionsManager.requestLocationPermission()

    }


    fun checkInternetConnection(): Boolean {
        val network = mConnectivityManager.activeNetwork ?: return false
        val activeNetwork =
            mConnectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun checkIfLocationsIsActivated() {
        if (hasLocation()) {
            requestPermissionsIfNotGranted()
        } else {
            AlertDialog.Builder(mContext)
                .setMessage("Location not enabled")
                .setPositiveButton(
                    "Open settings"
                ) { _, _ ->
                    mContext.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }
                .setNegativeButton("Cancel") { _, _ ->
                    exitProcess(1)
                }
                .show()
        }
    }


    private fun hasLocation(): Boolean {
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (_: Exception) {
        }

        try {
            networkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
        }
        return (gpsEnabled && networkEnabled)
    }



}




