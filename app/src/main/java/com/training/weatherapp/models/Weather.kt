package com.training.weatherapp.models

import android.media.Image
import java.io.*

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
) : Serializable
