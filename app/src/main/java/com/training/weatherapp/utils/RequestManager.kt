package com.training.weatherapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.training.weatherapp.constatns.ApiConfig
import com.training.weatherapp.constatns.ApiConfig.METRIC_UNIT
import com.training.weatherapp.constatns.Constants
import com.training.weatherapp.constatns.Constants.BAD_CONNECTION
import com.training.weatherapp.constatns.Constants.DELAY_PROCESS
import com.training.weatherapp.constatns.Constants.ERROR
import com.training.weatherapp.constatns.Constants.ERROR_400
import com.training.weatherapp.constatns.Constants.ERROR_404
import com.training.weatherapp.constatns.Constants.GENERIC_ERROR
import com.training.weatherapp.constatns.Constants.RESOURCE_NOT_FOUND
import com.training.weatherapp.constatns.Constants.RESULT_RESPONSE
import com.training.weatherapp.constatns.Constants.TAG_CURRENT_LAT
import com.training.weatherapp.constatns.Constants.TAG_CURRENT_LON
import com.training.weatherapp.models.WeatherResponse
import com.training.weatherapp.network.WeatherService
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RequestManager(context: Context) {
    private var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var mWeatherList: WeatherResponse? = null


    suspend fun getWeatherList(): WeatherResponse? {
        while (mWeatherList == null) {
            delay(DELAY_PROCESS)
        }

        return mWeatherList
    }

    fun getWeatherDetails(longitude: Double, latitude: Double) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: WeatherService = retrofit
            .create(WeatherService::class.java)

        val listCall: Call<WeatherResponse> = service
            .getWeather(latitude, longitude, ApiConfig.APP_ID, METRIC_UNIT)



        listCall.enqueue(object : Callback<WeatherResponse> {

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    mWeatherList = response.body()

                    Log.i(RESULT_RESPONSE, "$mWeatherList")
                } else {
                    when (response.code()) {
                        400 -> {
                            Log.e(ERROR_400, BAD_CONNECTION)
                        }
                        404 -> {
                            Log.e(ERROR_404, RESOURCE_NOT_FOUND)
                        }
                        else -> {
                            Log.e(ERROR, GENERIC_ERROR)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e(ERROR, t.message.toString())
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun requestLocationData() {
        val locationRequest = LocationRequest
            .Builder(Constants.INTERVAL_MILLIS)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateDistanceMeters(Constants.MIN_UPDATE_DISTANCE)
            .build()

        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation: Location? = p0.lastLocation
            val latitude = lastLocation?.latitude
            Log.i(TAG_CURRENT_LAT, "$latitude")
            val longitude = lastLocation?.longitude
            Log.i(TAG_CURRENT_LON, "$longitude")
            latitude?.let { lat ->
                longitude?.let { lng ->
                    getWeatherDetails(lng, lat)
                }
            }
        }
    }
}