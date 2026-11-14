package com.example.weatherapp.ui.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.location.LocationProvider
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationProvider: LocationProvider,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())

    val uiState: StateFlow<WeatherUiState> = combine(
        _state,
        settingsRepository.settings
    ) { state, settings -> state.copy(settings = settings) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeatherUiState())

    // Merkt sich den laufenden Ladevorgang, damit nicht mehrere parallel starten

    private var loadJob: Job? = null

    fun loadWeather() {
        // Läuft schon ein Ladevorgang, wird nichts gemacht

        if (loadJob?.isActive == true) return
        // 1. Ohne Berechtigung kann kein Standort geholt werden -> UI soll danach fragen
        loadJob = viewModelScope.launch {
            if (!locationProvider.hasPermission()) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        permissionRequired = true,
                        error = null
                    )
                }
                return@launch
            }
            // Spinner nur, wenn noch nichts angezeigt wird
            // (sonst bleiben die alten Daten sichtbar, während im Hintergrund aktualisiert wird)
            _state.update {
                it.copy(isLoading = it.weather == null, permissionRequired = false, error = null)
            }
            try {
                // 2. Standort holen; null heißt: GPS aus oder kein Empfang -> eigener Fehler
                val location = locationProvider.getCurrentLocation()
                    ?: error("Standort konnte nicht ermittelt werden. Ist der Standort am Gerät eingeschaltet?")
                Log.d("Weather", "Standort: ${location.latitude}, ${location.longitude}")
                // 3. Wetter aus Cache oder API holen und in den State schreiben
                val weather = repository.getWeather(location.latitude, location.longitude)
                _state.update { it.copy(isLoading = false, weather = weather) }
            } catch (e: Exception) {
                // z. B. kein Internet oder API-Fehler
                _state.update {
                    it.copy(
                        isLoading = false,
                        // vorhandenes Wetter bleibt sichtbar, Fehler nur ohne Daten
                        error = if (it.weather == null) e.message ?: "Unbekannter Fehler" else null
                    )
                }
            }
        }
    }
}