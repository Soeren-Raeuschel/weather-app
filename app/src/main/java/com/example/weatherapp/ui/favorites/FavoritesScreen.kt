package com.example.weatherapp.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.data.local.toWeatherEntity
import com.example.weatherapp.ui.weather.WeatherContent

@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        if (state.items.isEmpty()) {
            Text("Noch keine Favoriten – tippe auf +", Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(state.items, key = { it.id }) { fav ->
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                        // Breite = Breite der Card in WeatherContent, damit der Button an der Ecke sitzt
                        Box(Modifier.width(270.dp)) {
                            WeatherContent(
                                weather = fav.toWeatherEntity(),
                                settings = state.settings
                            )
                            IconButton(
                                onClick = { viewModel.remove(fav) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) { Icon(Icons.Default.Delete, contentDescription = "Entfernen") }
                        }
                    }
                }
            }
        }

        SmallFloatingActionButton(
            onClick = { viewModel.refreshAll() },
            modifier = Modifier.align(Alignment.BottomStart)
        ) { Icon(Icons.Default.Refresh, contentDescription = "Aktualisieren") }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) { Icon(Icons.Default.Add, contentDescription = "Stadt hinzufügen") }
    }

    if (showDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false; viewModel.clearError() },
            title = { Text("Stadt hinzufügen") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; viewModel.clearError() },
                        label = { Text("Stadtname") },
                        singleLine = true,
                        isError = state.error != null,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.add(name) { showDialog = false } }
                        )
                    )
                    state.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !state.isAdding,
                    onClick = { viewModel.add(name) { showDialog = false } }
                ) { Text(if (state.isAdding) "Prüfe…" else "Hinzufügen") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; viewModel.clearError() }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}