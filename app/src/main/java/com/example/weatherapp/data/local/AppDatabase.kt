package com.example.weatherapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [WeatherEntity::class, FavoriteEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteDao(): FavoriteDao
}