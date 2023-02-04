package com.training.weatherapp.utils

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.training.weatherapp.MainActivity
import com.training.weatherapp.R
import com.training.weatherapp.activities.NoInternetActivity
import com.training.weatherapp.model.LocationModel
import kotlin.system.exitProcess

class PrerequisitesChecker(context: Context) {
    private val mContext: Context = context
    private var mLocationManager: LocationManager
    private var mConnectionLiveData: ConnectionLiveData
    private var mPermissionsManager: PermissionsManager
    private var mLocationLiveData: LocationLiveData
    private lateinit var mLocationModel: LocationModel


    init {
        mLocationManager =
            context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        mConnectionLiveData = ConnectionLiveData(mContext)
        mPermissionsManager = PermissionsManager(mContext)
        mLocationLiveData = LocationLiveData(mContext)

    }

    fun requestPermissionsIfNotGranted() {
        mPermissionsManager.requestLocationPermission()
    }

    private fun monitorInternetConnection() {
        mConnectionLiveData.observe(mContext as LifecycleOwner) { isNetworkAvailable ->
            run {
                if (!isNetworkAvailable) {
                    Log.i("Disconnected", "No internet available")
                    val intent = Intent(mContext, NoInternetActivity::class.java)
                    mContext.startActivity(intent)
                }
                isNetworkAvailable?.let {
                    Log.i("Internet", "Connected successfully")
                }
            }
        }

    }

    fun checkInternetConnection() : Boolean  {
        return if (hasInternet()) {
            monitorInternetConnection()
            true
        } else {
            Toast.makeText(mContext, "No internet available. Please, connect!", Toast.LENGTH_LONG)
                .show()
            false
        }
    }

    private fun hasInternet(): Boolean {
        val network = mConnectionLiveData.getManager().activeNetwork ?: return false
        val activeNetwork =
            mConnectionLiveData.getManager().getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


    fun checkIfLocationsIsActivated() {
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

        if (!gpsEnabled && !networkEnabled) {
            // notify user
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
        } else {
            mLocationLiveData.observe(mContext as LifecycleOwner) { LocationModel ->
                kotlin.run {
                    mLocationModel = LocationModel
                    Log.i(
                        "Current location",
                        "longitude: ${LocationModel.longitude} latitude: ${LocationModel.latitude}"
                    )
                }

            }
        }
    }

    fun getCurrentLocation(): LocationModel {
        return mLocationModel
    }
}