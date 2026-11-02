package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val temp: Double,
    val humidity: Int,
    val speed: Double,
    val description: String,
    val icon: String,
    val timestamp: Long
)