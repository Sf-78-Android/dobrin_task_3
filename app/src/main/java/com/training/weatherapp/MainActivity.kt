package com.training.weatherapp


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.training.weatherapp.activities.NoInternetActivity
import com.training.weatherapp.utils.PrerequisitesChecker


class MainActivity : AppCompatActivity() {
    private lateinit var mPrerequisitesChecker: PrerequisitesChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //check if device location is activated and if not prompt to activate it
        mPrerequisitesChecker = PrerequisitesChecker(this)
        mPrerequisitesChecker.checkIfLocationsIsActivated()
        mPrerequisitesChecker.requestPermissionsIfNotGranted()
        // check if device is connected and start monitoring
        if(!mPrerequisitesChecker.checkInternetConnection()){
            val intent = Intent(this, NoInternetActivity::class.java)
            this.startActivity(intent)
        }

    }
}