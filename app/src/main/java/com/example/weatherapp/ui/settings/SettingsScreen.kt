package com.example.weatherapp.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.data.settings.TemperatureUnit
import com.example.weatherapp.data.settings.UnitSystem

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(Modifier.padding(16.dp)) {
        Text("Temperatur", style = MaterialTheme.typography.titleMedium)
        TemperatureUnit.entries.forEach { unit ->
            SettingsRadioRow(
                label = when (unit) {
                    TemperatureUnit.CELSIUS -> "Celsius (°C)"
                    TemperatureUnit.FAHRENHEIT -> "Fahrenheit (°F)"
                    TemperatureUnit.KELVIN -> "Kelvin (K)"
                },
                selected = settings.temperatureUnit == unit,
                onClick = { viewModel.onTemperatureUnitChange(unit) }
            )
        }

        Spacer(Modifier.height(24.dp))

        Text("Einheitensystem", style = MaterialTheme.typography.titleMedium)
        UnitSystem.entries.forEach { system ->
            SettingsRadioRow(
                label = when (system) {
                    UnitSystem.METRIC -> "Metrisch (m/s, km)"
                    UnitSystem.IMPERIAL -> "Imperial (mph, mi)"
                },
                selected = settings.unitSystem == system,
                onClick = { viewModel.onUnitSystemChange(system) }
            )
        }
    }
}

@Composable
private fun SettingsRadioRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}