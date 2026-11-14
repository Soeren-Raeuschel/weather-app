package com.example.weatherapp.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Fused Location Provider: Google wählt selbst die beste Quelle (GPS, WLAN, Mobilfunk)
    private val client = LocationServices.getFusedLocationProviderClient(context)


    // Prüft, ob der Nutzer mindestens eine der beiden Standort-Berechtigungen erteilt hat
    // (COARSE = ungefährer Standort, FINE = genauer Standort)
    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED


    // Holt die aktuelle Position. Gibt null zurück, wenn keine Berechtigung da ist
    // oder kein Standort ermittelt werden konnte

    // @SuppressLint: Die Berechtigung prüfen mit hasPermission(),
    // das Lint-Tool erkennt das aber nicht
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasPermission()) return null
        return try {
            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(0)      // keine alte Position akzeptieren
                .setDurationMillis(10_000)    // maximal 10 Sekunden auf eine Position warten
                .build()
            // await() wandelt den Google-Task in eine suspend-Funktion um.
            // Falls keine aktuelle Position kommt, wird als Fallback die zuletzt bekannte genommen
            client.getCurrentLocation(request, CancellationTokenSource().token).await()
                ?: client.lastLocation.await()
        } catch (e: SecurityException) {
            // Berechtigung wurde z. B. während des Aufrufs entzogen
            null
        }
    }
}