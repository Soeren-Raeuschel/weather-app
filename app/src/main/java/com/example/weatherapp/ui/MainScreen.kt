package com.example.weatherapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.navbar.AppBottomBar
import com.example.weatherapp.ui.navbar.Screen
import com.example.weatherapp.ui.weather.WeatherScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.weather.WeatherContent
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.ui.favorites.FavoritesScreen
import com.example.weatherapp.ui.settings.SettingsScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    MainScreenContent(navController = navController) {
        NavHost(
            navController = navController,
            startDestination = Screen.Weather.rout
        ) {
            composable(Screen.Weather.rout) { WeatherScreen() }
            composable(Screen.Favorites.rout) { FavoritesScreen() }
            composable(Screen.Settings.rout) { SettingsScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = { AppBottomBar(navController) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = getTitleForRoute(currentRoute)) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            content()
        }
    }
}

fun getTitleForRoute(route: String?): String {
    return when (route) {
        "weather" -> "Wetter"
        "favorites" -> "Favoriten"
        "settings" -> "Einstellungen"
        else -> "Meine App"
    }
}

@Preview(showBackground = true, name = "MainScreen")
@Composable
fun MainScreenPreview() {
    WeatherAppTheme {
        MainScreenContent(navController = rememberNavController()) {
            WeatherContent(
                weather = WeatherEntity(
                    // Felder an deine Entity anpassen
                    name = "Kassel",
                    description = "Leicht bewölkt",
                    temp = 18.5,
                    humidity = 72,
                    speed = 3.4,
                    icon = "02d",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}