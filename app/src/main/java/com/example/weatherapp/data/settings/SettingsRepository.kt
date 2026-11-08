package com.example.weatherapp.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


// Top-Level-Property, nur EINE Instanz pro Datei/App!
private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val TEMP_UNIT = stringPreferencesKey("temperature_unit")
        val UNIT_SYSTEM = stringPreferencesKey("unit_system")
    }

    val settings: Flow<UserSettings> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            UserSettings(
                temperatureUnit = prefs[Keys.TEMP_UNIT]
                    ?.let { TemperatureUnit.valueOf(it) } ?: TemperatureUnit.CELSIUS,
                unitSystem = prefs[Keys.UNIT_SYSTEM]
                    ?.let { UnitSystem.valueOf(it) } ?: UnitSystem.METRIC
            )
        }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { it[Keys.TEMP_UNIT] = unit.name }
    }

    suspend fun setUnitSystem(system: UnitSystem) {
        context.dataStore.edit { it[Keys.UNIT_SYSTEM] = system.name }
    }
}