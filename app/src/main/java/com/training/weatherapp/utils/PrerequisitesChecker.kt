package com.training.weatherapp.utils

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.lifecycle.LifecycleOwner
import com.training.weatherapp.activities.NoInternetActivity
import com.training.weatherapp.constatns.Constants.DELAY_PROCESS
import com.training.weatherapp.constatns.Constants.TIMEOUT_PROCESS
import com.training.weatherapp.models.LocationModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.Dispatcher
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

    private fun monitorLocation() {
        mLocationLiveData.observe(mContext as LifecycleOwner) { LocationModel ->
            run {
                mLocationModel = LocationModel
                Log.i(
                    "Current location",
                    "longitude: ${LocationModel.longitude} latitude: ${LocationModel.latitude}"
                )
            }
        }
    }

    fun checkInternetConnection(): Boolean {
        return if (hasInternet()) {
            monitorInternetConnection()
            true
        } else {
            false
        }
    }

    fun checkIfLocationsIsActivated() {
        if (hasLocation()) {
            monitorLocation()
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


    suspend fun getCurrentLocation(): LocationModel {

        var counter = 0
        val timeOut = TIMEOUT_PROCESS // 5 sec

        while (mLocationLiveData.value == null) {
            if (counter > timeOut) break
            counter++
            delay(DELAY_PROCESS)
        }

        return withContext(Dispatchers.IO) {
            mLocationModel
        }
    }
}