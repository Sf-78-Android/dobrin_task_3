package com.training.weatherapp.utils

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.NetworkOnMainThreadException
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import javax.net.SocketFactory
import kotlin.system.exitProcess

class PrerequisitesChecker(context : Context) {
     private val mContext : Context = context
     private var mLocationManager : LocationManager
     private var mConnectionLiveData : ConnectionLiveData

     init {
         mLocationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
         mConnectionLiveData = ConnectionLiveData(context)
     }

    fun isInternetAvailable() {

        mConnectionLiveData.observe(mContext as LifecycleOwner) { isNetworkAvailable ->
            run {
                if (!isNetworkAvailable){
                    Log.i("Disconnected", "No internet available")
                    AlertDialog.Builder(mContext)
                        .setMessage("Internet not enabled")
                        .setPositiveButton("Open internet settings"
                        ) { _, _ ->
                            mContext.startActivity(
                                Intent(Settings.ACTION_WIFI_SETTINGS)
                            )
                        }
                        .setNegativeButton("Cancel") { _, _ ->
                            exitProcess(1)
                        }
                        .show()
                }
                isNetworkAvailable?.let {
                    Log.i("Internet", "Connected successfully")
                }
            }
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
                .setPositiveButton("Open location settings"
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

}