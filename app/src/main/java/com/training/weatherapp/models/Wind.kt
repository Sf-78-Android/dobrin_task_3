package com.training.weatherapp.models

import java.io.*

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
) : Serializable
