package com.example.weatherapp.data.repository

import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.api.WeatherResponse
import jakarta.inject.Inject

class WeatherRepository@Inject constructor (private val api: WeatherApi){
    suspend fun getWeather(city: String): WeatherResponse{
        val weather = api.getWeatherByCity(city)
        return weather
    }
}