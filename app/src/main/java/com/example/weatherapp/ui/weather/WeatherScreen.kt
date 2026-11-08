package com.example.weatherapp.ui.weather

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import java.util.Locale
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.settings.TemperatureUnit
import com.example.weatherapp.data.settings.UnitSystem
import com.example.weatherapp.data.settings.UserSettings
import com.example.weatherapp.ui.theme.WeatherAppTheme

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator()
            state.error != null -> Text(text = state.error!!)
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
@Composable
fun WeatherContent(
    weather: WeatherEntity,
    settings: UserSettings = UserSettings()
){
    val iconCode = weather.icon

    val temp = when (settings.temperatureUnit) {
        TemperatureUnit.CELSIUS -> weather.temp
        TemperatureUnit.FAHRENHEIT -> weather.temp * 9 / 5 + 32
        TemperatureUnit.KELVIN -> weather.temp + 273.15
    }
    val tempUnit = when (settings.temperatureUnit) {
        TemperatureUnit.CELSIUS -> "°C"
        TemperatureUnit.FAHRENHEIT -> "°F"
        TemperatureUnit.KELVIN -> "K"
    }

    val isImperial = settings.unitSystem == UnitSystem.IMPERIAL
    val wind = if (isImperial) weather.speed * 2.23694 else weather.speed
    val windUnit = if (isImperial) "mph" else "m/s"

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .size(width = 270.dp, height = 300.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface, // Hintergrundfarbe der ElevatedCard
                contentColor = MaterialTheme.colorScheme.onSurface,   // Text-/Icon-Farbe
            ),
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row() {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row() {
                            Text("${weather.description}")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row() {
                            Text("Ort: ${weather.name}")
                        }

                        Row() {
                            val tempGer = String.format(Locale.GERMANY, "%.1f", temp)
                            Text("Temp: $tempGer $tempUnit")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row() {
                            Text("Luftfeuchtigkeit: ${weather.humidity}%")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row() {
                            val windGer = String.format(Locale.GERMANY, "%.1f", wind)
                            Text("Windgeschwindigkeit: $windGer $windUnit")
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (iconCode != null) {
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${iconCode}@2x.png",
                        contentDescription = "Wettersymbol",
                        modifier = Modifier.size(100.dp),
                        onError = { Log.e("Icon", "Fehler: ${it.result.throwable}") }
                    )
                }
            }
        }
    }

}

private val previewWeather = WeatherEntity(
    // Felder an deine Entity anpassen, weitere Pflichtfelder (z. B. id) ergänzen
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


