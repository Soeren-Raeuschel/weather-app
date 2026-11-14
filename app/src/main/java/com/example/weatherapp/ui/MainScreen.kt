package com.example.weatherapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.ui.favorites.FavoritesScreen
import com.example.weatherapp.ui.navbar.AppBottomBar
import com.example.weatherapp.ui.navbar.Screen
import com.example.weatherapp.ui.settings.SettingsScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.theme.WeatherBackground
import com.example.weatherapp.ui.weather.WeatherContent
import com.example.weatherapp.ui.weather.WeatherScreen

@Composable
fun MainScreen() {
    // rememberNavController merkt sich den Controller über Neuzeichnen hinweg
    val navController = rememberNavController()

    MainScreenContent(navController = navController) {
        // NavHost zeigt je nach aktueller Route den passenden Screen an
        NavHost(
            navController = navController,
            startDestination = Screen.Weather.route // App startet auf dem Wetter-Tab
        ) {
            composable(Screen.Weather.route) { WeatherScreen() }
            composable(Screen.Favorites.route) { FavoritesScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    // Aktuelle Route, damit der Titel oben zum Screen passt
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        //Hintergrund; dunkle Schrift für Texte
        containerColor = WeatherBackground,
        contentColor = Color(0xFF14152B),
        bottomBar = { AppBottomBar(navController) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = getTitleForRoute(currentRoute)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        // innerPadding verhindert, dass der Inhalt unter den Bars verschwindet
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

// Übersetzt die Route in den deutschen Titel für die Top-Bar
fun getTitleForRoute(route: String?): String {
    return when (route) {
        "weather" -> "Wetter"
        "favorites" -> "Favoriten"
        "settings" -> "Einstellungen"
        else -> "Meine App"
    }
}


// Vorschau mit festen Beispieldaten
@Preview(showBackground = true, name = "MainScreen")
@Composable
fun MainScreenPreview() {
    WeatherAppTheme {
        MainScreenContent(navController = rememberNavController()) {
            WeatherContent(
                weather = WeatherEntity(
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