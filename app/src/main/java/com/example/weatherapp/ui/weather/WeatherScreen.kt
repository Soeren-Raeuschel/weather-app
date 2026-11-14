package com.example.weatherapp.ui.weather

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.compose.AsyncImage
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.settings.TemperatureUnit
import com.example.weatherapp.data.settings.UnitSystem
import com.example.weatherapp.data.settings.UserSettings
import com.example.weatherapp.ui.theme.CardDark
import com.example.weatherapp.ui.theme.CardDivider
import com.example.weatherapp.ui.theme.ChipBg
import com.example.weatherapp.ui.theme.ChipText
import com.example.weatherapp.ui.theme.IconBoxBg
import com.example.weatherapp.ui.theme.IconLavender
import com.example.weatherapp.ui.theme.MutedText
import com.example.weatherapp.ui.theme.RemoveBg
import com.example.weatherapp.ui.theme.RemoveIcon
import com.example.weatherapp.ui.theme.TempLavender
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    // Compose zeichnet den Screen automatisch neu, sobald sich der State im ViewModel ändert
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Beide Standort-Berechtigungen
    val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    // Launcher für den System-Dialog zur Berechtigung. Das Ergebnis muss nicht ausgewertet werden,
    // weil ON_RESUME danach sowieso neu lädt
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* ON_RESUME lädt danach automatisch neu */ }

    // Lifecycle-Effekt: loadWeather() wird jedes Mal aufgerufen, wenn der Screen wieder in den Vordergrund kommt
    // Lädt beim Öffnen, nach dem Berechtigungsdialog und nach Rückkehr aus den Einstellungen
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.loadWeather() }

    // Dialog einmal automatisch zeigen
    LaunchedEffect(state.permissionRequired) {
        if (state.permissionRequired) launcher.launch(permissions)
    }

    // Je nach State wird genau ein Fall angezeigt. Beim when gewinnt die erste passende Bedingung, die Reihenfolge ist also wichtig
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            // Fall 1: Berechtigung fehlt -> Erklärung + Button + Link in die App-Einstellungen
            state.permissionRequired -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Für das Wetter an deinem Standort brauche ich die Standortfreigabe.",
                    textAlign = TextAlign.Center
                )
                Button(onClick = { launcher.launch(permissions) }) { Text("Standort erlauben") }
                TextButton(onClick = {
                    context.startActivity(
                        Intent(
                            "android.settings.APPLICATION_DETAILS_SETTINGS",
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                }) { Text("App-Einstellungen öffnen") }
            }
            // Fall 2: Daten werden geladen -> Ladekreis
            state.isLoading -> CircularProgressIndicator()

            // Fall 3: Fehler und noch keine Daten -> Meldung mit Button zum erneuten Laden
            state.error != null -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(state.error!!, textAlign = TextAlign.Center)
                Button(onClick = { viewModel.loadWeather() }) { Text("Erneut versuchen") }
            }

            // Fall 4: Daten sind da -> Wetterkarte anzeigen
            state.weather != null -> WeatherContent(
                weather = state.weather!!,
                settings = state.settings
            )
        }
    }
}

@Composable
fun WeatherScreenContent(
    isLoading: Boolean,
    error: String?,
    weather: WeatherEntity?,
    settings: UserSettings = UserSettings()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text(text = error)
            weather != null -> WeatherContent(weather = weather, settings = settings)
        }
    }
}

// Wetterkarte, wird für den aktuellen Standort und für die Favoriten benutzt.
// onRemove != null -> oben rechts erscheint der rote X-Button
@Composable
fun WeatherContent(
    weather: WeatherEntity,
    settings: UserSettings = UserSettings(),
    onRemove: (() -> Unit)? = null
) {
    // Temperatur umrechnen: Celsius -> Fahrenheit bzw. Kelvin
    fun convert(celsius: Double) = when (settings.temperatureUnit) {
        TemperatureUnit.CELSIUS -> celsius
        TemperatureUnit.FAHRENHEIT -> celsius * 9 / 5 + 32
        TemperatureUnit.KELVIN -> celsius + 273.15
    }

    val tempUnit = when (settings.temperatureUnit) {
        TemperatureUnit.CELSIUS -> "°C"
        TemperatureUnit.FAHRENHEIT -> "°F"
        TemperatureUnit.KELVIN -> "K"
    }
    val temp = convert(weather.temp).roundToInt()
    val feelsLike = convert(weather.feelsLike).roundToInt()

    // Wind: bei Imperial von m/s in mph umrechnen
    val isImperial = settings.unitSystem == UnitSystem.IMPERIAL
    val wind = if (isImperial) weather.speed * 2.23694 else weather.speed
    val windUnit = if (isImperial) "mph" else "m/s"
    val windText = String.format(Locale.GERMANY, "%.1f", wind) + " $windUnit"

    // Windrichtung als Himmelsrichtung + Grad, z. B. "O (110°)"
    val directions = listOf("N", "NO", "O", "SO", "S", "SW", "W", "NW")
    val compass = directions[((weather.windDeg % 360) / 45.0).roundToInt() % 8]
    val directionText = "$compass (${weather.windDeg}°)"

    // Uhrzeit der letzten Aktualisierung
    val updated = remember(weather.timestamp) {
        SimpleDateFormat("HH:mm", Locale.GERMANY).format(Date(weather.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = White)
    ) {
        Column(Modifier.padding(20.dp)) {

            // Kopfzeile: Stadtname + optional X-Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weather.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (onRemove != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RemoveBg)
                            .clickable(onClick = onRemove),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Entfernen",
                            tint = RemoveIcon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Temperatur (groß) + Wettersymbol rechts
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "$temp",
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            color = TempLavender,
                            lineHeight = 72.sp
                        )
                        Text(
                            text = tempUnit,
                            fontSize = 28.sp,
                            color = IconLavender,
                            modifier = Modifier.padding(top = 8.dp, start = 2.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    // Chip "Gefühlt"
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ChipBg)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = ChipText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Gefühlt $feelsLike$tempUnit", color = ChipText, fontSize = 14.sp)
                    }
                }

                // Wettersymbol: wird von OpenWeatherMap über die URL geladen.
                // Coil (AsyncImage) lädt das asynchron und cached es.
                if (weather.icon.isNotEmpty()) {
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${weather.icon}@2x.png",
                        contentDescription = weather.description,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(96.dp),
                        onError = { Log.e("Icon", "Fehler: ${it.result.throwable}") }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CardDivider)
            )
            Spacer(Modifier.height(20.dp))

            // Wind und Richtung
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(icon = Icons.Default.Air, label = "Wind", value = windText)
                InfoItem(
                    icon = Icons.Default.Navigation,
                    label = "Richtung",
                    value = directionText,
                    iconRotation = weather.windDeg.toFloat()
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Zuletzt aktualisiert: $updated",
                color = MutedText,
                fontSize = 12.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Ein Eintrag unten in der Karte: Icon in einem Kästchen, daneben Label + Wert
@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    iconRotation: Float = 0f
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(IconBoxBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = IconLavender,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(iconRotation)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, color = MutedText, fontSize = 13.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// Beispieldaten für die Vorschau
private val previewWeather = WeatherEntity(
    name = "Kassel",
    description = "Leicht bewölkt",
    temp = 18.5,
    humidity = 72,
    speed = 3.4,
    icon = "02d",
    timestamp = System.currentTimeMillis()
)

@Preview(showBackground = true, name = "Wetter")
@Composable
fun WeatherContentPreview() {
    WeatherAppTheme {
        WeatherContent(weather = previewWeather)
    }
}

@Preview(showBackground = true, name = "Screen: Daten")
@Composable
fun WeatherScreenDataPreview() {
    WeatherAppTheme {
        WeatherScreenContent(isLoading = false, error = null, weather = previewWeather)
    }
}


