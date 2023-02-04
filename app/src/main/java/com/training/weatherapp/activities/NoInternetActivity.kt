package com.training.weatherapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.training.weatherapp.MainActivity
import com.training.weatherapp.R
import com.training.weatherapp.R.id.refreshBtn
import com.training.weatherapp.utils.PrerequisitesChecker

class NoInternetActivity : AppCompatActivity() {
    private lateinit var mPrerequisitesChecker: PrerequisitesChecker
    private lateinit var mButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPrerequisitesChecker = PrerequisitesChecker(this)
        setContentView(R.layout.no_internet)
        mButton = findViewById(refreshBtn)


        mButton.setOnClickListener {
           if(mPrerequisitesChecker.checkInternetConnection()){
               val intent = Intent(this, MainActivity::class.java)
               this.startActivity(intent)
           }
        }
    }
}
