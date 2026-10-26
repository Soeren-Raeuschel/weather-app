package com.example.weatherapp.data.remote

import com.example.weatherapp.Secrets
import com.example.weatherapp.data.remote.api.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
        @GET("weather")
        suspend fun getWeatherByCity(
            @Query("q") city: String,
            @Query("units") units: String = "metric",
            @Query("lang") lang: String = "de",
            @Query("appid") apiKey: String = Secrets.API_KEY
        ): WeatherResponse
}

object RetrofitInstance {
    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}