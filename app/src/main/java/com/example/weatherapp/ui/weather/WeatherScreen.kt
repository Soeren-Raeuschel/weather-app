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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.remote.api.WeatherResponse
import java.util.Locale
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.data.local.WeatherEntity

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator() //Ladebalken
            state.error != null -> Text(text = state.error!!)
            state.weather != null -> WeatherContent(weather = state.weather!!)
        }
    }
}
@Composable
fun WeatherContent(weather: WeatherEntity){
    val iconCode = weather.icon

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wetter",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

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
                    Text("${weather.description}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row() {
                    Text("Ort: ${weather.name}")
                }

                if (iconCode != null) {
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${iconCode}@2x.png",
                        contentDescription = "Wettersymbol",
                        modifier = Modifier.size(100.dp),
                        onError = { Log.e("Icon", "Fehler: ${it.result.throwable}") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row() {
                    val weatherGer = String.format(Locale.GERMANY, "%.1f", weather.temp)
                    Text("Temp: ${weatherGer} °C")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row() {
                    Text("Luftfeuchtigkeit: ${weather.humidity}%")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row() {
                    Text("Windgeschwindigkeit: ${weather.speed} m/s")
                }
            }
        }
    }

}

