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

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val TEMP_UNIT = stringPreferencesKey("temperature_unit")
        val UNIT_SYSTEM = stringPreferencesKey("unit_system")
    }

    // Liest die gespeicherten Werte und baut daraus ein UserSettings-Objekt

    val settings: Flow<UserSettings> = context.dataStore.data
        .catch { e ->
            //Wenn ein Ein-/Ausgabefehler auftritt, leere Preferences ausgeben
            // anderen Fehler = erneut Fehler werfen
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        // Wandelt die gespeicherten Preferences in ein UserSettings-Objekt um.
        // Die gespeicherten Strings werden mithilfe von valueOf() in Enum-Werte umgewandelt.
        // Falls noch keine Einstellung gespeichert wurde (null), werden die Standardwerte verwendet:
        // Celsius für die Temperatureinheit und METRIC für das Einheitensystem.
        .map { prefs ->
            UserSettings(
                temperatureUnit = prefs[Keys.TEMP_UNIT]
                    ?.let { TemperatureUnit.valueOf(it) } ?: TemperatureUnit.CELSIUS,
                unitSystem = prefs[Keys.UNIT_SYSTEM]
                    ?.let { UnitSystem.valueOf(it) } ?: UnitSystem.METRIC
            )
        }

    // Speichert die ausgewählte Temperatureinheit dauerhaft im DataStore.
    // Der Enum-Wert wird über .name als String gespeichert.
    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { it[Keys.TEMP_UNIT] = unit.name }
    }

    // Speichert das ausgewählte Einheitensystem dauerhaft im DataStore.
    // Auch hier wird der Enum-Wert über .name als String gespeichert.
    suspend fun setUnitSystem(system: UnitSystem) {
        context.dataStore.edit { it[Keys.UNIT_SYSTEM] = system.name }
    }
}