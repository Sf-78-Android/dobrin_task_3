package com.training.weatherapp.activities


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.training.weatherapp.R
import com.training.weatherapp.constatns.Constants
import com.training.weatherapp.constatns.Constants.DATE_FORMAT
import com.training.weatherapp.constatns.Constants.TAG_WEATHER_NAME
import com.training.weatherapp.models.WeatherResponse
import com.training.weatherapp.utils.LocationTranslator
import com.training.weatherapp.utils.PermissionsManager
import com.training.weatherapp.utils.PrerequisitesChecker
import com.training.weatherapp.utils.RequestManager
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private lateinit var mPrerequisitesChecker: PrerequisitesChecker
    private lateinit var mRequestManager: RequestManager
    private lateinit var mPermissionsManager: PermissionsManager
    private var mProgressDialog: Dialog? = null
    private lateinit var mTvMain: TextView
    private lateinit var mTvMainDescription: TextView
    private lateinit var mTvTemp: TextView
    private lateinit var mTvHumidity: TextView
    private lateinit var mIvMain: ImageView
    private lateinit var mTvMin: TextView
    private lateinit var mTvMax: TextView
    private lateinit var mTvSpeed: TextView
    private lateinit var mTvCountry: TextView
    private lateinit var mTvName: TextView
    private lateinit var mTvSunsetTime: TextView
    private lateinit var mTvSunriseTime: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTvMain = findViewById(R.id.tv_main)
        mTvMainDescription = findViewById(R.id.tv_main_description)
        mTvTemp = findViewById(R.id.tv_temp)
        mTvHumidity = findViewById(R.id.tv_humidity)
        mIvMain = findViewById(R.id.iv_main)
        mTvMin = findViewById(R.id.tv_min)
        mTvMax = findViewById(R.id.tv_max)
        mTvSpeed = findViewById(R.id.tv_speed)
        mTvCountry = findViewById(R.id.tv_country)
        mTvName = findViewById(R.id.tv_name)
        mTvSunsetTime = findViewById(R.id.tv_sunset_time)
        mTvSunriseTime = findViewById(R.id.tv_sunrise_time)
        mPrerequisitesChecker = PrerequisitesChecker(this)
        mRequestManager = RequestManager(this)
        mPermissionsManager = PermissionsManager(this)

        startProcess()

        val dataRefreshBtn: Button = findViewById(R.id.dataRefresh)
        dataRefreshBtn.setOnClickListener {
            startProcess()
        }
    }

    fun startProcess() {
        if (!mPrerequisitesChecker.checkInternetConnection()) {
            val intent = Intent(this, NoInternetActivity::class.java)
            this.startActivity(intent)
        } else if (!mPrerequisitesChecker.hasLocation()) {
            showDialogForLocationActivation()
        } else {
            mPermissionsManager.requestLocationPermission()
            if (mPermissionsManager.isLocationPermissionGranted()) {
                mRequestManager.requestLocationData()
                executeRequest()
            } else {
                showDialogForPermissions()
            }
        }
    }

    private fun showDialogForLocationActivation() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.location_not_enabled))
            .setPositiveButton(
                Constants.OPEN_SETTINGS
            ) { _, _ ->
                this.startActivity(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                )
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                exitProcess(1)
            }
            .show()
    }

    private fun showDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.go_to_app_settings))
            .setPositiveButton(R.string.no_permission) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                exitProcess(1)
            }.show()
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun executeRequest() {
        showCustomProgressDialog()
        GlobalScope.launch {
            mRequestManager.getWeatherList()?.let { _weatherList ->
                run {

                    setupUI(_weatherList)
                    hideCustomProgressDialog()
                }
            }
        }

    }


    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)

        mProgressDialog?.setContentView(R.layout.dialog_custom_progress)

        mProgressDialog?.show()
    }

    private fun hideCustomProgressDialog() {
        mProgressDialog?.dismiss()
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(weatherList: WeatherResponse) {
        this.runOnUiThread {
            for (i in weatherList.weather.indices) {
                Log.i(TAG_WEATHER_NAME, weatherList.weather.toString())

                mTvMain.text = weatherList.weather[i].main
                mTvMainDescription.text = weatherList.weather[i].description
                mTvTemp.text = weatherList.main.temp.toString() + getUnit(weatherList.sys.country)


                mTvHumidity.text =
                    weatherList.main.humidity.toString() + getString(R.string.percent_sign)
                mTvMin.text = weatherList.main.temp_min.toString() + getString(R.string.min)
                mTvMax.text = weatherList.main.temp_max.toString() + getString(R.string.max)
                mTvSpeed.text = weatherList.wind.speed.toString()
                mTvCountry.text = LocationTranslator.translateLocation(weatherList.sys.country)
                mTvName.text = weatherList.name


                mTvSunriseTime.text = unixTime(weatherList.sys.sunrise.toLong())
                mTvSunsetTime.text = unixTime(weatherList.sys.sunset.toLong())
                when (weatherList.weather[i].icon) {
                    "01d" -> mIvMain.setImageResource(R.drawable.sunny)
                    "02d" -> mIvMain.setImageResource(R.drawable.cloud)
                    "03d" -> mIvMain.setImageResource(R.drawable.cloud)
                    "04d" -> mIvMain.setImageResource(R.drawable.cloud)
                    "04n" -> mIvMain.setImageResource(R.drawable.cloud)
                    "10d" -> mIvMain.setImageResource(R.drawable.rain)
                    "11d" -> mIvMain.setImageResource(R.drawable.storm)
                    "13d" -> mIvMain.setImageResource(R.drawable.snowflake)
                    "01n" -> mIvMain.setImageResource(R.drawable.cloud)
                    "02n" -> mIvMain.setImageResource(R.drawable.cloud)
                    "03n" -> mIvMain.setImageResource(R.drawable.cloud)
                    "10n" -> mIvMain.setImageResource(R.drawable.cloud)
                    "11n" -> mIvMain.setImageResource(R.drawable.rain)
                    "13n" -> mIvMain.setImageResource(R.drawable.snowflake)
                }
            }
        }
    }

    private fun getUnit(value: String): String {
        if (Constants.US_SYMBOL == value || Constants.LR_SYMBOL == value || Constants.MM_SYMBOL == value) {
            return Constants.FAHRENHEIT_SYMBOL
        }
        return Constants.CELSIUS_SYMBOL

    }

    private fun unixTime(timex: Long): String? {
        val date = Date(timex * Constants.DATE_ALLOCATION)

        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat(DATE_FORMAT)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }


}

