package com.example.weatherapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.favorites.FavoritesRepository
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.data.settings.SettingsRepository
import com.example.weatherapp.data.settings.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class FavoritesUiState(
    val items: List<FavoriteEntity> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val isAdding: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoritesRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val _isAdding = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // Vier Quellen werden zu einem State kombiniert, sobald sich eine ändert
    // (z. B. neuer Favorit in der DB), wird der State neu gebaut und die UI neu gezeichnet

    val uiState: StateFlow<FavoritesUiState> = combine(
        repository.favorites, settingsRepository.settings, _isAdding, _error
    ) { items, settings, adding, error ->
        FavoritesUiState(items, settings, adding, error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesUiState())

    init {
        // Beim Öffnen des Screens werden nur veraltete Favoriten aktualisiert
        viewModelScope.launch { repository.refresh() }
    }

    // Neue Stadt hinzufügen. onSuccess ist ein Callback, damit die UI den Dialog
    // erst schließt, wenn es wirklich geklappt hat

    fun add(name: String, onSuccess: () -> Unit) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _isAdding.value = true
            repository.addCity(name)
                .onSuccess { _error.value = null; onSuccess() }
                .onFailure {
                    // 404 von der API = Stadt gibt es nicht; sonst die Meldung der Exception
                    _error.value = when {
                        it is HttpException && it.code() == 404 -> "Stadt nicht gefunden"
                        else -> it.message ?: "Fehler beim Hinzufügen"
                    }
                }
            _isAdding.value = false
        }
    }

    fun remove(favorite: FavoriteEntity) = viewModelScope.launch { repository.remove(favorite) }

    // Refresh-Button: alle Favoriten neu laden, egal wie alt sie sind
    fun refreshAll() = viewModelScope.launch { repository.refresh(force = true) }
    fun clearError() {
        _error.value = null
    }
}