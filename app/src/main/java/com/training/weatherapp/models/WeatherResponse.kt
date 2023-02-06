package com.training.weatherapp.models

import java.io.*

data class WeatherResponse(
    val coord: LocationModel,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Int,
    val sys: Sys,
    val name: String
) : Serializable