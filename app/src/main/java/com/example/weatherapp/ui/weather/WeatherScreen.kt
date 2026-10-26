package com.example.weatherapp.ui.weather

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.data.remote.api.WeatherResponse
import java.util.Locale
import coil.compose.AsyncImage

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
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
fun WeatherContent(weather: WeatherResponse){
    val iconCode = weather.weather.firstOrNull()?.icon
    Column() {

        Row() {
            Text("${weather.weather.firstOrNull()?.description}")
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
            val weatherGer = String.format(Locale.GERMANY, "%.1f", weather.main.temp)
            Text("Temp: ${weatherGer} °C")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row() {
            Text("Luftfeuchtigkeit: ${weather.main.humidity}%")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row() {
            Text("Windgeschwindigkeit: ${weather.wind.speed} m/s")
        }
    }
}

