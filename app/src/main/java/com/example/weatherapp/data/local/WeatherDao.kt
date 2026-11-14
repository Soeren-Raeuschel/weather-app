package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    // Liest die (einzige) Cache-Zeile. Null, wenn noch nichts gespeichert wurde
    @Query("SELECT * FROM weather_cache WHERE id = :id")
    suspend fun getWeather(id: Int = 1): WeatherEntity?

    // REPLACE: Gibt es schon eine Zeile mit id = 1, wird sie überschrieben
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    // Leert den Cache komplett
    @Query("DELETE FROM weather_cache")
    suspend fun clearCache()
}