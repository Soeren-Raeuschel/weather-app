package com.example.weatherapp.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.WeatherEntity


@Database(entities = [WeatherEntity::class],version = 1,exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}