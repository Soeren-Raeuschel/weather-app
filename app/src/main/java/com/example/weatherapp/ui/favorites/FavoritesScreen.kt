package com.example.weatherapp.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.data.local.toWeatherEntity
import com.example.weatherapp.ui.theme.CardDark
import com.example.weatherapp.ui.theme.IconLavender
import com.example.weatherapp.ui.theme.White
import com.example.weatherapp.ui.weather.WeatherContent

@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 72.dp)
        ) {
            if (state.items.isEmpty()) {
                item {
                    Text(
                        "Noch keine Favoriten",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(state.items, key = { it.id }) { fav ->
                WeatherContent(
                    weather = fav.toWeatherEntity(),
                    settings = state.settings,
                    onRemove = { viewModel.remove(fav) }
                )
            }

            item {
                Surface(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = CardDark,
                    contentColor = White
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Standort hinzufügen", fontSize = 16.sp)
                    }
                }
            }
        }

        SmallFloatingActionButton(
            onClick = { viewModel.refreshAll() },
            modifier = Modifier.align(Alignment.BottomEnd),
            containerColor = CardDark,
            contentColor = IconLavender
        ) { Icon(Icons.Default.Refresh, contentDescription = "Aktualisieren") }
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