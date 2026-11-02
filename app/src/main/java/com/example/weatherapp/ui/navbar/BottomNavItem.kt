package com.example.weatherapp.ui.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navigationItems = listOf(
    BottomNavItem("Wetter", Icons.Default.Cloud, Screen.Weather.rout),
    BottomNavItem("Favoriten", Icons.Default.Star, Screen.Favorites.rout),
    BottomNavItem("Einstellungen", Icons.Default.Settings, Screen.Settings.rout)
)