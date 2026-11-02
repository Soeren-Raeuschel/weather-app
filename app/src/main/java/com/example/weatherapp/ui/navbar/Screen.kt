package com.example.weatherapp.ui.navbar

sealed class Screen(val rout: String) {
    object Weather: Screen("weather")
    object Favorites: Screen("favorites")
    object Settings: Screen("settings")
}