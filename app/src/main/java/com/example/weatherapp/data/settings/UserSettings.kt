package com.example.weatherapp.data.settings

enum class TemperatureUnit { CELSIUS, FAHRENHEIT, KELVIN }
enum class UnitSystem { METRIC, IMPERIAL }

data class UserSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val unitSystem: UnitSystem = UnitSystem.METRIC
)