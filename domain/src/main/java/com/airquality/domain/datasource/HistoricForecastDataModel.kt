package com.airquality.domain.datasource

data class HistoricForecastDataModel(
    val city: String = "",
    val parameters: List<AirParameterHistoricForecast>? = null,
    val averageAQI: Double = 0.0,
)

data class AirParameterHistoricForecast(
    val time: String,
    val values: Map<String, Double>
)