package com.example.weatherapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.settings.SettingsRepository
import com.example.weatherapp.data.settings.TemperatureUnit
import com.example.weatherapp.data.settings.UnitSystem
import com.example.weatherapp.data.settings.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    // Der Flow aus dem DataStore wird zum StateFlow, damit Compose ihn beobachten kann
    // initialValue = Standardwerte, bis der DataStore das erste Mal geantwortet hat

    val settings: StateFlow<UserSettings> = repository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserSettings()
        )

    // Schreiben ist suspend, deshalb in einer Coroutine im viewModelScope
    fun onTemperatureUnitChange(unit: TemperatureUnit) {
        viewModelScope.launch { repository.setTemperatureUnit(unit) }
    }

    fun onUnitSystemChange(system: UnitSystem) {
        viewModelScope.launch { repository.setUnitSystem(system) }
    }
}