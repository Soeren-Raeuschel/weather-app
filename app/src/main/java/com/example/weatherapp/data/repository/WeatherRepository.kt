package com.example.weatherapp.data.repository

import com.example.weatherapp.data.remote.RetrofitInstance
import com.example.weatherapp.data.remote.api.WeatherResponse

class WeatherRepository() {
    suspend fun getWeather(city: String): WeatherResponse{
        val weather = RetrofitInstance.api.getWeatherByCity(city)
        return weather
    }
}