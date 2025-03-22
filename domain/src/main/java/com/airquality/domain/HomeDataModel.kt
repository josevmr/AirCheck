package com.airquality.domain

data class HomeDataModel(
    val city: String = "",
    val parameters: List<AirParameter>? = null,
    val coordinates: AirCoordinates? = null,
    val lastUpdated: String = ""
)

data class AirParameter(
    val parameter: String,
    val lastValue: Double,
    val units: String
)

data class AirCoordinates(
    val latitude: Double,
    val longitude: Double
)