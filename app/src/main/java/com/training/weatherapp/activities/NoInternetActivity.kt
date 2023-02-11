package com.training.weatherapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.training.weatherapp.R
import com.training.weatherapp.R.id.refreshBtn
import com.training.weatherapp.constatns.Constants
import com.training.weatherapp.utils.PrerequisitesChecker

class NoInternetActivity : AppCompatActivity() {
    private lateinit var mPrerequisitesChecker: PrerequisitesChecker
    private lateinit var mButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPrerequisitesChecker = PrerequisitesChecker(this)
        setContentView(R.layout.activity_no_internet)
        mButton = findViewById(refreshBtn)
        val layout: View = findViewById(R.id.no_internet_layout)
        val snackbar = Snackbar
            .make(layout, Constants.noConnections, Snackbar.LENGTH_LONG)
            .setAction(Constants.retry, View.OnClickListener {
                if (mPrerequisitesChecker.checkInternetConnection()) {
                    val intent = Intent(this, MainActivity::class.java)
                    this.startActivity(intent)
                }
            })
        snackbar.show()

        mButton.setOnClickListener {
            if (mPrerequisitesChecker.checkInternetConnection()) {
                val intent = Intent(this, MainActivity::class.java)
                this.startActivity(intent)
            } else {
                snackbar.show()
            }
        }
    }
}
