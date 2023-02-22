package com.training.weatherapp.models

import java.io.Serializable

data class LocationModel(
    val longitude: Double,
    val latitude: Double
) : Serializable