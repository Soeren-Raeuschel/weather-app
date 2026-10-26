package com.example.weatherapp.ui.weather

import com.example.weatherapp.data.remote.api.WeatherResponse

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: WeatherResponse? = null,
    val error: String? = null
)