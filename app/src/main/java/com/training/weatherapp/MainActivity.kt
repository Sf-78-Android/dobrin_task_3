package com.training.weatherapp


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.training.weatherapp.utils.PrerequisitesChecker


class MainActivity : AppCompatActivity() {
    private lateinit var mPrerequisitesChecker: PrerequisitesChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //check if device location is activated and if not prompt to activate it
        mPrerequisitesChecker = PrerequisitesChecker(this)
        mPrerequisitesChecker.checkIfLocationsIsActivated()
        // check if device is connected
        mPrerequisitesChecker.isInternetAvailable()

        setContentView(R.layout.activity_main)







    }
}