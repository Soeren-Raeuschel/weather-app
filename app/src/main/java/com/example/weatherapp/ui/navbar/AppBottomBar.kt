package com.example.weatherapp.ui.navbar

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(
    navController: NavHostController,
    containerColor: Color = MaterialTheme.colorScheme.surface,

    ) {
    // "by" + State: Compose zeichnet die Leiste automatisch neu, wenn sich die Route ändert
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(containerColor = containerColor) {
        // Für jeden Eintrag aus navigationItems einen Tab erzeugen
        navigationItems.forEach { item ->
            NavigationBarItem(
                // Tab ist markiert, wenn seine Route die aktuelle ist
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Zurück-Stack bis zum Startscreen aufräumen, damit sich beim Tab-Wechsel nicht viele Screens stapeln
                        // saveState/restoreState merkt sich den Zustand der Tabs
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // verhindert, dass derselbe Screen mehrfach oben liegt
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                // Alles weiß, weil der Hintergrund in Theme dunkelblau ist
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White,

                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White
                )
            )
        }
    }
}