package com.example.weatherapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.navbar.AppBottomBar
import com.example.weatherapp.ui.navbar.Screen
import com.example.weatherapp.ui.weather.WeatherScreen // Pfad ggf. anpassen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Weather.rout,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Weather.rout) { WeatherScreen() }
            //composable(Screen.Favorites.rout) { PlaceholderScreen("Favoriten") }
            //composable(Screen.Settings.rout) { PlaceholderScreen("Einstellungen") }
        }
    }
}