package com.example.weatherapp.data.repository

import android.location.Location
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.remote.WeatherApi
import dagger.Lazy
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val api: Lazy<WeatherApi>,
    private val weatherDao: WeatherDao
) {
    // Cache ist 15 Minuten gültig
    private val cachedExpirationTime = 15 * 60 * 1000

    // Wenn mehr als 5 km bewegt, ist der Cache unbrauchbar
    private val maxDistanceMeters = 5_000f

    suspend fun getWeather(lat: Double, lon: Double): WeatherEntity {
        val cached = weatherDao.getWeather()
        val now = System.currentTimeMillis()

        // Cache nur verwenden, wenn es einen Eintrag gibt, er noch nicht abgelaufen ist
        // und der Standort ungefähr derselbe ist. Sonst gehe weiter zur API
        if (cached != null &&
            now - cached.timestamp < cachedExpirationTime &&
            distanceMeters(cached.lat, cached.lon, lat, lon) < maxDistanceMeters
        ) {
            return cached
        }

        // Cache war nicht brauchbar -> frische Daten von der API holen
        val response = api.get().getWeatherByCoordinates(lat, lon)

        // API-Antwort in lokales Datenmodell umwandeln.
        val entity = WeatherEntity(
            name = response.name,
            temp = response.main.temp,
            humidity = response.main.humidity,
            speed = response.wind.speed,
            description = response.weather.firstOrNull()?.description ?: "",
            icon = response.weather.firstOrNull()?.icon ?: "",
            timestamp = System.currentTimeMillis(),
            lat = lat,
            lon = lon,
            feelsLike = response.main.feelsLike,
            windDeg = response.wind.deg
        )
        // Im Cache speichern (überschreibt den alten Eintrag) und zurückgeben
        weatherDao.insertWeather(entity)
        return entity
    }

    // Entfernung zwischen zwei Koordinaten in Metern.
    // Location.distanceBetween schreibt das Ergebnis in das Array an Index 0
    private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val result = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, result)
        return result[0]
    }
}