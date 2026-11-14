package com.example.weatherapp.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val name: String,
    val main: Main,
    val wind: Wind,
    val weather: List<WeatherDescription>,
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    val humidity: Int
)

data class WeatherDescription(
    val icon: String,
    val description: String
)