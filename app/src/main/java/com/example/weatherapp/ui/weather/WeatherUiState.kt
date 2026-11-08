package com.example.weatherapp.ui.weather

import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.remote.api.WeatherResponse
import com.example.weatherapp.data.settings.UserSettings

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: WeatherEntity? = null,
    val error: String? = null,
    val settings: UserSettings = UserSettings()
)