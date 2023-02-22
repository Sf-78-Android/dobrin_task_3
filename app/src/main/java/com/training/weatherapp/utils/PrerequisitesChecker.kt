package com.training.weatherapp.utils

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity

class PrerequisitesChecker(context: Context) {

    private var mLocationManager: LocationManager
    private val mConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        mLocationManager =
            context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
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

    fun hasLocation(): Boolean {
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




