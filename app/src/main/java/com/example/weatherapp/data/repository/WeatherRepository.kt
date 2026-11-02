package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.api.WeatherResponse
import dagger.Lazy
import javax.inject.Inject

class WeatherRepository@Inject constructor (
    private val api: Lazy<WeatherApi>,
    private val weatherDao: WeatherDao)
{
    private val cachedExpirationTime = 15*60*1000
    suspend fun getWeather(city: String): WeatherEntity{
        val cachedWeather = weatherDao.getWeather()
        val currentTime = System.currentTimeMillis()

        if(cachedWeather != null && (currentTime - cachedWeather.timestamp) < cachedExpirationTime){
            return cachedWeather
        }


        val weather = api.get().getWeatherByCity(city)
        val newWeatherEntity = WeatherEntity(
            name = weather.name,
            temp = weather.main.temp,
            humidity = weather.main.humidity,
            speed = weather.wind.speed,
            description = weather.weather.firstOrNull()?.description ?: "",
            icon = weather.weather.firstOrNull()?.icon ?: "",
            timestamp = System.currentTimeMillis()
        )

        weatherDao.insertWeather(newWeatherEntity)

        return newWeatherEntity
    }
}