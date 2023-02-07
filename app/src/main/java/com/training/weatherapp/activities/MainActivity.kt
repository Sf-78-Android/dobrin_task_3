package com.training.weatherapp.activities


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.training.weatherapp.R
import com.training.weatherapp.databinding.ActivityMainBinding
import com.training.weatherapp.utils.PrerequisitesChecker
import com.training.weatherapp.utils.RequestManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var mPrerequisitesChecker: PrerequisitesChecker
    private lateinit var mRequestManager: RequestManager
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPrerequisitesChecker = PrerequisitesChecker(this)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)
        // check if device is connected and start monitoring
        if (!mPrerequisitesChecker.checkInternetConnection()) {
            val intent = Intent(this, NoInternetActivity::class.java)
            this.startActivity(intent)
        } else {
            mRequestManager = RequestManager(this, mBinding)
            mPrerequisitesChecker.checkIfLocationsIsActivated()
            //check if device location is activated and if not prompt to activate it
            mPrerequisitesChecker.requestPermissionsIfNotGranted()

            executeRequest()

            val dataRefreshBtn : Button = findViewById(R.id.dataRefresh)
            dataRefreshBtn.setOnClickListener {
                executeRequest()
            }
        }

    }

    private fun executeRequest() {

        GlobalScope.launch(Dispatchers.Main) {
           val location = mPrerequisitesChecker.getCurrentLocation()

            val longitude = location.longitude

            val latitude = location.latitude
            mRequestManager.getWeatherDetails(longitude, latitude)
        }
    }
}