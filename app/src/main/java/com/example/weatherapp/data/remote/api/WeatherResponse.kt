package com.example.weatherapp.data.remote.api

class WeatherResponse(
    val name: String,
    val main: Main,
    val wind: Wind,
    val weather: List<WeatherDescription>
)

data class Wind(
    val speed: Double
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class WeatherDescription(
    val icon:String,
    val description: String
)