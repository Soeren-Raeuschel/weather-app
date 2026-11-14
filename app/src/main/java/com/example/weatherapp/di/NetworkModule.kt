package com.example.weatherapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.weatherapp.data.local.AppDatabase
import com.example.weatherapp.data.local.FavoriteDao
import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Retrofit-Instanz mit der Basis-URL der OpenWeatherMap-API
    // Gson wandelt das JSON der API automatisch in meine Kotlin-Datenklassen um

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit generiert aus meinem WeatherApi-Interface die echte Implementierung

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Baut die Room-Datenbank. Der Context wird von Hilt geliefert (@ApplicationContext)
    // "weather_cache" ist der Dateiname der SQLite-Datenbank auf dem Gerät
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "weather_cache")
            .build()
    }

    // Die DAOs hole ich aus der Datenbank, damit die Repositories sie per Injection bekommen
    @Provides
    @Singleton
    fun provideWeatherDao(database: AppDatabase): WeatherDao {
        return database.weatherDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()
}