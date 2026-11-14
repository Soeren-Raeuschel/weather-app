package com.example.weatherapp.ui.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// Liste aller Einträge. Die AppBottomBar läuft darüber und zeichnet pro Eintrag einen Button,
// neue Tabs kann man hier ergänzen
val navigationItems = listOf(
    BottomNavItem("Wetter", Icons.Default.Cloud, Screen.Weather.route),
    BottomNavItem("Favoriten", Icons.Default.Star, Screen.Favorites.route),
    BottomNavItem("Einstellungen", Icons.Default.Settings, Screen.Settings.route)
)