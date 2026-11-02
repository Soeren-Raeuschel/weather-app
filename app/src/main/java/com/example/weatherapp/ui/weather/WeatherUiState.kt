package com.example.weatherapp.ui.weather

import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.remote.api.WeatherResponse

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: WeatherEntity? = null,
    val error: String? = null
)