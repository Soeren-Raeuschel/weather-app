package com.example.weatherapp.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.FavoriteDao
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.local.FavoriteEntity


@Database(
    entities = [WeatherEntity::class, FavoriteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteDao(): FavoriteDao
}