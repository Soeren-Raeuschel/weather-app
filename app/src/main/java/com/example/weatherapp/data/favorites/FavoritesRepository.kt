package com.example.weatherapp.data.favorites

import com.example.weatherapp.data.local.FavoriteDao
import com.example.weatherapp.data.local.FavoriteEntity
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.api.WeatherResponse
import dagger.Lazy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val api: Lazy<WeatherApi>,
    private val dao: FavoriteDao
) {
    private val maxAgeMs = 15 * 60 * 1000L

    val favorites: Flow<List<FavoriteEntity>> = dao.observeAll()

    // Stadt wird gegen die API geprüft und mit Wetter gespeichert
    suspend fun addCity(input: String): Result<Unit> = runCatching {
        val response = api.get().getWeatherByCity(input.trim())
        val id = dao.insert(response.toFavorite())
        if (id == -1L) error("Stadt ist schon in den Favoriten")
    }

    // Nur veraltete Einträge neu laden (wie bei Standort-Cache)
    suspend fun refresh(force: Boolean = false) {
        val now = System.currentTimeMillis()
        dao.getAll()
            .filter { force || now - it.timestamp > maxAgeMs }
            .forEach { fav ->
                runCatching { api.get().getWeatherByCity(fav.name) }
                    .onSuccess { dao.update(it.toFavorite(fav.id)) }
            }
    }

    suspend fun remove(favorite: FavoriteEntity) = dao.delete(favorite)
}

private fun WeatherResponse.toFavorite(id: Long = 0) = FavoriteEntity(
    id = id,
    name = name,
    temp = main.temp,
    humidity = main.humidity,
    speed = wind.speed,
    description = weather.firstOrNull()?.description ?: "",
    icon = weather.firstOrNull()?.icon ?: "",
    timestamp = System.currentTimeMillis()
)