package com.example.weatherapp.data.remote

import com.example.weatherapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    // Wetter anhand eines Stadtnamens (wird bei den Favoriten benutzt)
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "de",
        @Query("appid") apiKey: String = BuildConfig.OPENWEATHER_API_KEY
    ): WeatherResponse

    // Wetter anhand der GPS-Koordinaten (wird für den aktuellen Standort benutzt)
    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "de",
        @Query("appid") apiKey: String = BuildConfig.OPENWEATHER_API_KEY
    ): WeatherResponse
}