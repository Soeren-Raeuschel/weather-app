package com.example.weatherapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favorites", indices = [Index(value = ["name"], unique = true)])
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val name: String,
    val temp: Double,
    val humidity: Int,
    val speed: Double,
    val description: String,
    val icon: String,
    val timestamp: Long
)

// Damit die bestehende WeatherContent wiederverwendet werden kann
fun FavoriteEntity.toWeatherEntity() = WeatherEntity(
    name = name, temp = temp, humidity = humidity, speed = speed,
    description = description, icon = icon, timestamp = timestamp
)