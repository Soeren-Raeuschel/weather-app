package com.example.weatherapp.data.favorites

import com.example.weatherapp.data.local.FavoriteDao
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.WeatherResponse
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val api: Lazy<WeatherApi>,
    private val dao: FavoriteDao
) {
    // Favoriten gelten nach 15 Minuten als veraltet
    private val cachedExpirationTime = 15 * 60 * 1000L

    // Flow direkt aus der Datenbank: die UI bekommt automatisch jede Änderung mit

    val favorites: Flow<List<FavoriteEntity>> = dao.observeAll()

    // Stadt wird gegen die API geprüft und mit Wetter gespeichert
    // runCatching fängt alle Exceptions (z. B. 404 bei unbekannter Stadt, kein Internet)
    // und verpackt sie in ein Result, damit das ViewModel sie sauber auswerten kann

    suspend fun addCity(input: String): Result<Unit> = runCatching {
        val response = api.get().getWeatherByCity(input.trim())
        val id = dao.insert(response.toFavorite())
        if (id == -1L) error("Stadt ist schon in den Favoriten")
    }

    // Nur veraltete Einträge neu laden
    // Mit force = true werden alle aktualisiert
    suspend fun refresh(force: Boolean = false) {
        val now = System.currentTimeMillis()
        dao.getAll()
            .filter { force || now - it.timestamp > cachedExpirationTime }
            .forEach { fav ->
                // Schlägt ein einzelner Abruf fehl, bleibt der alte Wert stehen
                // und die anderen Städte werden trotzdem aktualisiert
                runCatching { api.get().getWeatherByCity(fav.name) }
                    .onSuccess { dao.update(it.toFavorite(fav.id)) }
            }
    }

    suspend fun remove(favorite: FavoriteEntity) = dao.delete(favorite)
}

// Mapper: API-Antwort -> Datenbank-Entity
// Die id wird beim Update mitgegeben, damit die richtige Zeile überschrieben wird
// Bei einem neuen Favoriten bleibt sie 0, dann vergibt Room automatisch eine
private fun WeatherResponse.toFavorite(id: Long = 0) = FavoriteEntity(
    id = id,
    name = name,
    temp = main.temp,
    humidity = main.humidity,
    speed = wind.speed,
    description = weather.firstOrNull()?.description ?: "",
    icon = weather.firstOrNull()?.icon ?: "",
    timestamp = System.currentTimeMillis(),
    feelsLike = main.feelsLike,
    windDeg = wind.deg
)